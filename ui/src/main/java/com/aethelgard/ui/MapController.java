/*
 * File: ui/src/main/java/com/aethelgard/ui/MapController.java
 * Purpose: Headless map tool logic — session, layers, play, inspect (no Swing)
 * Audience: Tests / MapHost / Next front
 * Update when: Map window behavior changes
 */

package com.aethelgard.ui;

import com.aethelgard.cli.CliResult;
import com.aethelgard.cli.CommandDispatch;
import com.aethelgard.product.DiagnosticIds;
import com.aethelgard.product.Grid;
import com.aethelgard.product.PlateVelocities;
import com.aethelgard.product.ProductSession;
import com.aethelgard.product.WorldSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Headless controller for the product map. No Swing types — safe for CI tests.
 *
 * <p>Owns a {@link ProductSession}. {@link #advance()} runs one generation Step on the caller.
 * {@link #advanceAsync()} runs it on the injected {@link Executor} and reports {@link #busy()} /
 * {@link #WORKING_STATUS} while in flight. Play uses an injected {@link PlayScheduler}. Terminal
 * lines go through {@link CommandDispatch} on the same session.
 */
public final class MapController {

  public static final String WORKING_STATUS = "Working...";

  private final Executor executor;
  private final PlayScheduler playScheduler;
  private final AtomicBoolean busy = new AtomicBoolean(false);
  private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
  /** Guards paint buffers, layer, and raster vs concurrent advance / setLayer (F-054). */
  private final Object paintLock = new Object();

  private WorldSpec spec;
  private ProductSession session;
  private volatile int cachedStep;
  private Grid elevation;
  private Grid plates;
  private PlateVelocities velocities;
  private ElevationRaster raster;
  private MapLayer layer = MapLayer.ELEVATION;
  private MapSpeed speed = MapSpeed.X1;
  private boolean playing;
  private CellInspect inspected;
  private long lastPaintNanos;
  /** Reused paint backing stores when width×height unchanged (F-047). Double-buffered so prior
   * snapshots keep correct pixels. */
  private int[] paintBufferA;

  private int[] paintBufferB;
  private boolean paintIntoA = true;
  /** Bumps on every paint so MapHost can invalidate packed cache (F-047). */
  private int paintGeneration;

  public MapController(WorldSpec spec) {
    this(spec, Runnable::run, PlayScheduler.idle());
  }

  public MapController(WorldSpec spec, Executor executor) {
    this(spec, executor, PlayScheduler.idle());
  }

  public MapController(WorldSpec spec, Executor executor, PlayScheduler playScheduler) {
    this.spec = Objects.requireNonNull(spec, "spec");
    this.executor = Objects.requireNonNull(executor, "executor");
    this.playScheduler = Objects.requireNonNull(playScheduler, "playScheduler");
    this.session = new ProductSession(spec);
    capture();
  }

  /** Launch controller: {@link WorldSpec#VIEW}. */
  public static MapController view(Executor executor) {
    return view(executor, PlayScheduler.idle());
  }

  public static MapController view(Executor executor, PlayScheduler playScheduler) {
    return new MapController(WorldSpec.VIEW, executor, playScheduler);
  }

  public WorldSpec spec() {
    return spec;
  }

  public ProductSession session() {
    return session;
  }

  /** Wall time of the last {@link ElevationRaster#paint} in {@link #capture()}. */
  public long lastPaintNanos() {
    return lastPaintNanos;
  }

  /**
   * Last captured step index. Does not take the session physics lock — safe during {@link
   * #busy()} Advance.
   */
  public int stepIndex() {
    return cachedStep;
  }

  public ElevationRaster raster() {
    synchronized (paintLock) {
      return raster;
    }
  }

  /** Monotonic paint counter; changes whenever the visible raster is rebuilt. */
  public int paintGeneration() {
    synchronized (paintLock) {
      return paintGeneration;
    }
  }

  public MapLayer layer() {
    synchronized (paintLock) {
      return layer;
    }
  }

  public MapSpeed speed() {
    return speed;
  }

  public boolean playing() {
    return playing;
  }

  public CellInspect inspected() {
    return inspected;
  }

  public boolean busy() {
    return busy.get();
  }

  /** {@link #WORKING_STATUS} while busy; otherwise {@code Step n} from the cached step. */
  public String statusText() {
    if (busy.get()) {
      return WORKING_STATUS;
    }
    return "Step " + cachedStep;
  }

  public void onChanged(Runnable listener) {
    Objects.requireNonNull(listener, "listener");
    listeners.add(listener);
    listener.run();
  }

  /**
   * Switch paint layer. Does not advance the world. Uses the last captured grids (safe while
   * busy). Serialized with {@link #capture()} so paint buffers are not torn under Play.
   */
  public void setLayer(MapLayer layer) {
    Objects.requireNonNull(layer, "layer");
    long paintNanos;
    synchronized (paintLock) {
      this.layer = layer;
      long t0 = System.nanoTime();
      raster = paintReuse(elevation, plates, this.layer);
      paintNanos = System.nanoTime() - t0;
      lastPaintNanos = paintNanos;
    }
    session.diagnostics().record(DiagnosticIds.PAINT_WALL, paintNanos);
    fire();
  }

  public void setSpeed(MapSpeed speed) {
    this.speed = Objects.requireNonNull(speed, "speed");
    if (playing) {
      playScheduler.stop();
      playScheduler.start(this.speed.periodMillis(), this::playTick);
    }
    fire();
  }

  public void play() {
    if (playing) {
      return;
    }
    playing = true;
    playScheduler.start(speed.periodMillis(), this::playTick);
    fire();
  }

  public void pause() {
    if (!playing) {
      return;
    }
    playing = false;
    playScheduler.stop();
    fire();
  }

  /** One play tick. No-op when paused. Delegates to {@link #advanceAsync()}. */
  public void playTick() {
    if (!playing) {
      return;
    }
    advanceAsync();
  }

  /**
   * Replace the session with the same geometry and a new seed. Ignored while {@link #busy()}.
   * Pauses play. Step index returns to 0.
   */
  public void newWorld(long seed) {
    if (busy.get()) {
      return;
    }
    pause();
    spec = new WorldSpec(spec.width(), spec.height(), seed);
    replaceSession();
  }

  /**
   * Recreate the session with the current geometry and seed. Works while {@link #busy()}. Pauses
   * play. In-flight Advance on the old session must not capture over the new world.
   */
  public void restartEngine() {
    pause();
    replaceSession();
  }

  private void replaceSession() {
    session = new ProductSession(spec);
    busy.set(false);
    inspected = null;
    capture();
    fire();
  }

  /** Snapshot of cell {@code (x, y)} from the last captured grids. */
  public CellInspect inspect(int x, int y) {
    CellInspect snap;
    synchronized (paintLock) {
      int id = plates.get(x, y);
      snap =
          new CellInspect(x, y, elevation.get(x, y), id, velocities.vx(id), velocities.vy(id));
      inspected = snap;
    }
    fire();
    return snap;
  }

  /** Legend rows for the current layer. */
  public List<LegendEntry> legend() {
    MapLayer current;
    synchronized (paintLock) {
      current = layer;
    }
    return switch (current) {
      case ELEVATION -> elevationLegend();
      case PLATES -> plateLegend();
      case OVERLAY -> {
        List<LegendEntry> rows = new ArrayList<>(elevationLegend());
        rows.add(new LegendEntry(ElevationRaster.darken(ElevationRaster.landRamp(16)), "Suture"));
        yield rows;
      }
    };
  }

  /** One generation Step on the caller thread. */
  public void advance() {
    session.advance();
    capture();
    if (inspected != null) {
      inspect(inspected.x(), inspected.y());
      return;
    }
    fire();
  }

  /**
   * One generation Step on {@code executor}. Ignored while {@link #busy()}. Sets busy and status
   * before submit.
   */
  public void advanceAsync() {
    if (!busy.compareAndSet(false, true)) {
      return;
    }
    fire();
    final ProductSession target = session;
    executor.execute(
        () -> {
          try {
            if (session != target) {
              return;
            }
            target.advance();
            if (session != target) {
              return;
            }
            capture();
            if (inspected != null) {
              inspect(inspected.x(), inspected.y());
              return;
            }
          } finally {
            if (session == target) {
              busy.set(false);
              fire();
            }
          }
        });
  }

  /**
   * Runs one shared-dispatcher line on this session. Ignored while {@link #busy()} ({@code error:
   * busy}). Refreshes the raster after the dispatcher returns so map Advance and terminal share one
   * view.
   */
  public CliResult runCommand(String line) {
    if (busy.get()) {
      return new CliResult(2, "error: busy");
    }
    CliResult result = CommandDispatch.execute(session, line);
    capture();
    if (inspected != null) {
      inspect(inspected.x(), inspected.y());
      return result;
    }
    fire();
    return result;
  }

  private List<LegendEntry> elevationLegend() {
    return List.of(
        new LegendEntry(ElevationRaster.oceanRamp(ElevationRaster.OCEAN_FLOOR), "Deep (≤ −64)"),
        new LegendEntry(ElevationRaster.OCEAN_RGB, "Shallow (−1)"),
        new LegendEntry(ElevationRaster.landRamp(0), "Low (0)"),
        new LegendEntry(ElevationRaster.landRamp(ElevationRaster.CLAMP), "High (64)"));
  }

  private List<LegendEntry> plateLegend() {
    return List.of(
        new LegendEntry(ElevationRaster.PLATE_INTERIOR_RGB, "Interior"),
        new LegendEntry(ElevationRaster.PLATE_BOUNDARY_RGB, "Boundary"));
  }

  private void capture() {
    Grid nextElev = session.elevation();
    Grid nextPlates = session.plates();
    PlateVelocities nextVel = session.plateVelocities();
    int nextStep = session.stepIndex();
    long paintNanos;
    synchronized (paintLock) {
      elevation = nextElev;
      plates = nextPlates;
      velocities = nextVel;
      cachedStep = nextStep;
      long t0 = System.nanoTime();
      raster = paintReuse(elevation, plates, layer);
      paintNanos = System.nanoTime() - t0;
      lastPaintNanos = paintNanos;
    }
    session.diagnostics().record(DiagnosticIds.PAINT_WALL, paintNanos);
  }

  /** Caller must hold {@link #paintLock}. */
  private ElevationRaster paintReuse(Grid elev, Grid plateGrid, MapLayer mapLayer) {
    int need = elev.width() * elev.height();
    if (paintBufferA == null || paintBufferA.length != need) {
      paintBufferA = new int[need];
      paintBufferB = new int[need];
      paintIntoA = true;
    }
    int[] target = paintIntoA ? paintBufferA : paintBufferB;
    paintIntoA = !paintIntoA;
    paintGeneration++;
    return ElevationRaster.paint(elev, plateGrid, mapLayer, target);
  }

  /** Test hook: either slot of the double paint buffer. */
  int[] paintBufferForTest() {
    synchronized (paintLock) {
      return paintBufferA;
    }
  }

  private void fire() {
    for (Runnable listener : listeners) {
      listener.run();
    }
  }
}
