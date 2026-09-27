/*
 * File: ui/src/main/java/com/aethelgard/ui/host/MapHost.java
 * Purpose: Localhost HTTP facade over MapController (G-006 / F-024)
 * Audience: Next front / Tauri / Maven tests
 * Update when: Host API routes or status shape change
 */

package com.aethelgard.ui.host;

import com.aethelgard.cli.CliResult;
import com.aethelgard.product.session.diagnostics.DiagnosticCollector;
import com.aethelgard.product.session.diagnostics.DiagnosticsHub;
import com.aethelgard.product.world.fields.WorldSpec;
import com.aethelgard.ui.CellInspect;
import com.aethelgard.ui.ElevationRaster;
import com.aethelgard.ui.ExecutorPlayScheduler;
import com.aethelgard.ui.LegendEntry;
import com.aethelgard.ui.MapController;
import com.aethelgard.ui.MapLayer;
import com.aethelgard.ui.MapSpeed;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Loopback HTTP adapter for one {@link MapController}. No Swing. Bind address is always {@code
 * 127.0.0.1}.
 *
 * <p>Routes (CORS enabled for local Next):
 *
 * <ul>
 *   <li>{@code GET /health} — plain {@code ok}
 *   <li>{@code GET /api/status} — JSON status + optional inspect + legend
 *   <li>{@code GET /api/raster} — packed RGB ({@code width}/{@code height} headers + BE int pixels)
 *   <li>{@code POST /api/advance} — {@link MapController#advanceAsync()}
 *   <li>{@code POST /api/play} / {@code /api/pause}
 *   <li>{@code POST /api/layer} — body or {@code ?layer=}
 *   <li>{@code POST /api/speed} — body or {@code ?speed=}
 *   <li>{@code POST /api/new-world} — {@code ?seed=}
 *   <li>{@code POST /api/restart-engine} — same seed, Step 0; works while busy
 *   <li>{@code POST /api/inspect} — {@code ?x=} {@code ?y=}
 *   <li>{@code POST /api/command} — plain-text console line
 * </ul>
 */
public final class MapHost implements AutoCloseable {

  private final MapController controller;
  private final ExecutorPlayScheduler playScheduler;
  private final java.util.concurrent.ExecutorService advanceExecutor;
  private final java.util.concurrent.ExecutorService httpExecutor;
  private final HttpServer server;
  private final boolean ownsLifecycle;

  /** Reused packed raster body (F-047); invalidated when step or layer changes. */
  private byte[] cachedPacked;

  private int cachedPackedStep = Integer.MIN_VALUE;
  private int cachedPaintGeneration = Integer.MIN_VALUE;
  private MapLayer cachedPackedLayer;
  private int packedBodyAllocations;

  private MapHost(
      MapController controller,
      ExecutorPlayScheduler playScheduler,
      java.util.concurrent.ExecutorService advanceExecutor,
      java.util.concurrent.ExecutorService httpExecutor,
      boolean ownsLifecycle,
      HttpServer server) {
    this.controller = controller;
    this.playScheduler = playScheduler;
    this.advanceExecutor = advanceExecutor;
    this.httpExecutor = httpExecutor;
    this.ownsLifecycle = ownsLifecycle;
    this.server = server;
  }

  /** Host on an ephemeral loopback port with {@link WorldSpec#VIEW}. */
  public static MapHost startView() throws IOException {
    return start(WorldSpec.VIEW, 0);
  }

  /** Host on {@code port} (0 = ephemeral) for {@code spec}. Owns play + advance threads. */
  public static MapHost start(WorldSpec spec, int port) throws IOException {
    Objects.requireNonNull(spec, "spec");
    ExecutorPlayScheduler play = new ExecutorPlayScheduler();
    java.util.concurrent.ExecutorService advances =
        Executors.newSingleThreadExecutor(daemonFactory("map-host-advance"));
    MapController controller = new MapController(spec, advances, play);
    return bind(controller, play, advances, true, port);
  }

  /**
   * Wrap an existing controller (tests inject executor / idle play). Does not shut down the
   * controller's executors.
   */
  public static MapHost start(MapController controller, int port) throws IOException {
    return bind(controller, null, null, false, port);
  }

  private static MapHost bind(
      MapController controller,
      ExecutorPlayScheduler play,
      java.util.concurrent.ExecutorService advances,
      boolean ownsLifecycle,
      int port)
      throws IOException {
    Objects.requireNonNull(controller, "controller");
    HttpServer http = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
    java.util.concurrent.ExecutorService httpThreads =
        Executors.newCachedThreadPool(daemonFactory("map-host-http"));
    MapHost host = new MapHost(controller, play, advances, httpThreads, ownsLifecycle, http);
    http.createContext("/health", host::health);
    http.createContext("/api/status", host::status);
    http.createContext("/api/raster", host::raster);
    http.createContext("/api/advance", host::advance);
    http.createContext("/api/play", host::play);
    http.createContext("/api/pause", host::pause);
    http.createContext("/api/layer", host::layer);
    http.createContext("/api/speed", host::speed);
    http.createContext("/api/new-world", host::newWorld);
    http.createContext("/api/restart-engine", host::restartEngine);
    http.createContext("/api/inspect", host::inspect);
    http.createContext("/api/command", host::command);
    http.setExecutor(httpThreads);
    http.start();
    return host;
  }

  public int port() {
    return server.getAddress().getPort();
  }

  public String baseUrl() {
    return "http://127.0.0.1:" + port();
  }

  public MapController controller() {
    return controller;
  }

  /** Test hook: how many distinct packed {@code byte[]} bodies this host allocated. */
  public int packedBodyAllocations() {
    return packedBodyAllocations;
  }

  /** Test hook: identity of the current cached packed body, or {@code null}. */
  public byte[] cachedPackedBody() {
    return cachedPacked;
  }

  @Override
  public void close() {
    server.stop(0);
    httpExecutor.shutdownNow();
    if (ownsLifecycle) {
      if (playScheduler != null) {
        playScheduler.close();
      }
      if (advanceExecutor != null) {
        advanceExecutor.shutdownNow();
      }
    }
  }

  private void health(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    send(exchange, 200, "text/plain; charset=utf-8", "ok");
  }

  private void status(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void raster(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    ElevationRaster image = controller.raster();
    byte[] body = packedRasterCached();
    Headers headers = exchange.getResponseHeaders();
    cors(headers);
    headers.set("Content-Type", "application/octet-stream");
    headers.set("X-Width", Integer.toString(image.width()));
    headers.set("X-Height", Integer.toString(image.height()));
    exchange.sendResponseHeaders(200, body.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(body);
    }
  }

  /**
   * Returns the packed raster for the current controller step+layer, reusing one {@code byte[]}
   * when size matches (F-047). Refills when step or layer changes.
   */
  byte[] packedRasterCached() {
    ElevationRaster image = controller.raster();
    int step = controller.stepIndex();
    int gen = controller.paintGeneration();
    MapLayer layer = controller.layer();
    int need = 8 + (image.width() * image.height() * 4);
    boolean sameKey =
        cachedPacked != null
            && cachedPackedStep == step
            && cachedPaintGeneration == gen
            && cachedPackedLayer == layer;
    if (sameKey && cachedPacked.length == need) {
      return cachedPacked;
    }
    if (cachedPacked == null || cachedPacked.length != need) {
      cachedPacked = new byte[need];
      packedBodyAllocations++;
    }
    packRasterInto(image, cachedPacked);
    cachedPackedStep = step;
    cachedPaintGeneration = gen;
    cachedPackedLayer = layer;
    return cachedPacked;
  }

  private void advance(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    drain(exchange);
    controller.advanceAsync();
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void play(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    drain(exchange);
    controller.play();
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void pause(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    drain(exchange);
    controller.pause();
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void layer(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    String raw = firstParam(exchange, "layer");
    if (raw == null || raw.isBlank()) {
      raw = readBody(exchange).trim();
    } else {
      drain(exchange);
    }
    MapLayer chosen = parseLayer(raw);
    if (chosen == null) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: unknown layer");
      return;
    }
    controller.setLayer(chosen);
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void speed(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    String raw = firstParam(exchange, "speed");
    if (raw == null || raw.isBlank()) {
      raw = readBody(exchange).trim();
    } else {
      drain(exchange);
    }
    MapSpeed chosen = parseSpeed(raw);
    if (chosen == null) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: unknown speed");
      return;
    }
    controller.setSpeed(chosen);
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void newWorld(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    String seedText = firstParam(exchange, "seed");
    drain(exchange);
    if (seedText == null || seedText.isBlank()) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: seed required");
      return;
    }
    long seed;
    try {
      seed = Long.parseLong(seedText.trim());
    } catch (NumberFormatException ex) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: bad seed");
      return;
    }
    controller.newWorld(seed);
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void restartEngine(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    drain(exchange);
    controller.restartEngine();
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void inspect(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    String xs = firstParam(exchange, "x");
    String ys = firstParam(exchange, "y");
    drain(exchange);
    if (xs == null || ys == null) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: x and y required");
      return;
    }
    int x;
    int y;
    try {
      x = Integer.parseInt(xs.trim());
      y = Integer.parseInt(ys.trim());
    } catch (NumberFormatException ex) {
      send(exchange, 400, "text/plain; charset=utf-8", "error: bad coordinates");
      return;
    }
    controller.inspect(x, y);
    send(exchange, 200, "application/json; charset=utf-8", statusJson());
  }

  private void command(HttpExchange exchange) throws IOException {
    if (preflight(exchange)) {
      return;
    }
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      send(exchange, 405, "text/plain; charset=utf-8", "method not allowed");
      return;
    }
    String line = readBody(exchange);
    CliResult result = controller.runCommand(line);
    String json =
        "{\"exitCode\":"
            + result.exitCode()
            + ",\"ok\":"
            + result.isOk()
            + ",\"output\":"
            + jsonString(result.output())
            + ",\"status\":"
            + statusJson()
            + "}";
    send(exchange, result.isOk() ? 200 : 400, "application/json; charset=utf-8", json);
  }

  private String statusJson() {
    WorldSpec spec = controller.spec();
    StringBuilder sb = new StringBuilder(256);
    sb.append('{');
    field(sb, "step", controller.stepIndex(), true);
    field(sb, "seed", spec.seed(), false);
    field(sb, "width", spec.width(), false);
    field(sb, "height", spec.height(), false);
    field(sb, "layer", controller.layer().label(), false);
    field(sb, "speed", controller.speed().label(), false);
    field(sb, "playing", controller.playing(), false);
    field(sb, "busy", controller.busy(), false);
    field(sb, "statusText", controller.statusText(), false);
    CellInspect inspected = controller.inspected();
    sb.append(",\"inspect\":");
    if (inspected == null) {
      sb.append("null");
    } else {
      sb.append('{');
      field(sb, "x", inspected.x(), true);
      field(sb, "y", inspected.y(), false);
      field(sb, "elevation", inspected.elevation(), false);
      field(sb, "plateId", inspected.plateId(), false);
      field(sb, "vx", inspected.vx(), false);
      field(sb, "vy", inspected.vy(), false);
      sb.append('}');
    }
    sb.append(",\"legend\":[");
    List<LegendEntry> legend = controller.legend();
    for (int i = 0; i < legend.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      LegendEntry row = legend.get(i);
      sb.append("{\"rgb\":")
          .append(row.rgb())
          .append(",\"label\":")
          .append(jsonString(row.label()))
          .append('}');
    }
    sb.append("],\"diag\":");
    appendDiag(sb, controller.session().diagnostics());
    sb.append('}');
    return sb.toString();
  }

  /** Compact diagnostics snapshot: id → { last, mean, n }. */
  static void appendDiag(StringBuilder sb, DiagnosticsHub hub) {
    sb.append('{');
    List<DiagnosticCollector> collectors = hub.collectors();
    for (int i = 0; i < collectors.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      DiagnosticCollector c = collectors.get(i);
      Long last = c.latest();
      Long mean = c.mean();
      sb.append('"').append(c.id()).append("\":{");
      sb.append("\"last\":").append(last == null ? "null" : last);
      sb.append(",\"mean\":").append(mean == null ? "null" : mean);
      sb.append(",\"n\":").append(c.size());
      sb.append('}');
    }
    sb.append('}');
  }

  private static void field(StringBuilder sb, String name, long value, boolean first) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(name).append("\":").append(value);
  }

  private static void field(StringBuilder sb, String name, boolean value, boolean first) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(name).append("\":").append(value);
  }

  private static void field(StringBuilder sb, String name, String value, boolean first) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(name).append("\":").append(jsonString(value));
  }

  private static String jsonString(String value) {
    StringBuilder sb = new StringBuilder(value.length() + 8);
    sb.append('"');
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '\\', '"' -> sb.append('\\').append(c);
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> {
          if (c < 0x20) {
            sb.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
          } else {
            sb.append(c);
          }
        }
      }
    }
    sb.append('"');
    return sb.toString();
  }

  /** Big-endian width, height, then row-major packed {@code 0xRRGGBB} ints. */
  public static byte[] packRaster(ElevationRaster image) {
    int w = image.width();
    int h = image.height();
    byte[] body = new byte[8 + (w * h * 4)];
    packRasterInto(image, body);
    return body;
  }

  /** Packs into {@code body}; length must be {@code 8 + width * height * 4}. */
  public static void packRasterInto(ElevationRaster image, byte[] body) {
    int w = image.width();
    int h = image.height();
    int need = 8 + (w * h * 4);
    if (body.length != need) {
      throw new IllegalArgumentException("body length " + body.length + " != " + need);
    }
    putInt(body, 0, w);
    putInt(body, 4, h);
    int o = 8;
    int[] pixels = image.pixels();
    for (int i = 0; i < pixels.length; i++) {
      putInt(body, o, pixels[i]);
      o += 4;
    }
  }

  public static int readInt(byte[] body, int offset) {
    return ((body[offset] & 0xFF) << 24)
        | ((body[offset + 1] & 0xFF) << 16)
        | ((body[offset + 2] & 0xFF) << 8)
        | (body[offset + 3] & 0xFF);
  }

  private static void putInt(byte[] body, int offset, int value) {
    body[offset] = (byte) (value >>> 24);
    body[offset + 1] = (byte) (value >>> 16);
    body[offset + 2] = (byte) (value >>> 8);
    body[offset + 3] = (byte) value;
  }

  private static MapLayer parseLayer(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String key = raw.trim();
    for (MapLayer layer : MapLayer.values()) {
      if (layer.name().equalsIgnoreCase(key) || layer.label().equalsIgnoreCase(key)) {
        return layer;
      }
    }
    return null;
  }

  private static MapSpeed parseSpeed(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String key = raw.trim();
    for (MapSpeed speed : MapSpeed.values()) {
      if (speed.name().equalsIgnoreCase(key) || speed.label().equalsIgnoreCase(key)) {
        return speed;
      }
    }
    return null;
  }

  private static String firstParam(HttpExchange exchange, String name) {
    String query = exchange.getRequestURI().getRawQuery();
    if (query == null || query.isEmpty()) {
      return null;
    }
    for (String part : query.split("&")) {
      int eq = part.indexOf('=');
      if (eq <= 0) {
        continue;
      }
      String key = decode(part.substring(0, eq));
      if (name.equals(key)) {
        return decode(part.substring(eq + 1));
      }
    }
    return null;
  }

  private static String decode(String raw) {
    return java.net.URLDecoder.decode(raw, StandardCharsets.UTF_8);
  }

  private static boolean preflight(HttpExchange exchange) throws IOException {
    cors(exchange.getResponseHeaders());
    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
      exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      exchange
          .getResponseHeaders()
          .set("Access-Control-Allow-Headers", "Content-Type");
      exchange.sendResponseHeaders(204, -1);
      exchange.close();
      return true;
    }
    return false;
  }

  private static void cors(Headers headers) {
    headers.set("Access-Control-Allow-Origin", "*");
  }

  private static void send(HttpExchange exchange, int code, String type, String body)
      throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    Headers headers = exchange.getResponseHeaders();
    cors(headers);
    headers.set("Content-Type", type);
    exchange.sendResponseHeaders(code, bytes.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(bytes);
    }
  }

  private static void drain(HttpExchange exchange) throws IOException {
    try (InputStream in = exchange.getRequestBody()) {
      in.readAllBytes();
    }
  }

  private static String readBody(HttpExchange exchange) throws IOException {
    try (InputStream in = exchange.getRequestBody()) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static ThreadFactory daemonFactory(String name) {
    return r -> {
      Thread t = new Thread(r, name);
      t.setDaemon(true);
      return t;
    };
  }
}
