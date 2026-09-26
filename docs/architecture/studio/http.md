<!--
  File: docs/architecture/studio/http.md
  Purpose: MapHost, MapHostApp — the local HTTP host: routes, status JSON, packed raster body, threads, and the process entry
  Audience: Agents and humans
  Update when: A MapHost route, the status JSON, the packed body, the raster cache, or MapHostApp changes
-->

# HTTP host

The web page cannot call Java directly, so the Java process listens on a local port and answers a handful of requests. Some read the state, one returns the painted map as raw bytes, and the rest pass a person's action to the controller. It listens only on the loopback address, so nothing outside the machine can reach it.

## What it reads

HTTP requests on `127.0.0.1`: the method, the path, query parameters, and for some routes a text body. From the controller: its status, raster, legend, inspected cell, and the session's diagnostics.

## What it writes

HTTP responses, and calls into the [controller](controller.md). Refusals: a wrong method answers 405 `method not allowed`; a missing or unparsable parameter answers 400 `error: …`; a refused command answers 400 with the command's result. A failure the handler does not catch, for example `inspect` outside the map, leaves the request without a response from the handler.

## Model

**Routes.** Every response carries `Access-Control-Allow-Origin: *`. An `OPTIONS` request to any route is a CORS preflight, answered 204 with `Access-Control-Allow-Methods: GET, POST, OPTIONS` and `Access-Control-Allow-Headers: Content-Type`.

| Method, path | Input | Effect | Answer |
|--------------|-------|--------|--------|
| `GET /health` | — | — | `ok` |
| `GET /api/status` | — | — | Status JSON |
| `GET /api/raster` | — | — | Packed raster; headers `X-Width`, `X-Height`; type `application/octet-stream` |
| `POST /api/advance` | — | `advanceAsync` | Status JSON |
| `POST /api/play`, `POST /api/pause` | — | `play`, `pause` | Status JSON |
| `POST /api/layer` | `?layer=` or body | `setLayer` | Status JSON, or 400 `error: unknown layer` |
| `POST /api/speed` | `?speed=` or body | `setSpeed` | Status JSON, or 400 `error: unknown speed` |
| `POST /api/new-world` | `?seed=` | `newWorld` (ignored while busy) | Status JSON, or 400 `error: seed required`, `error: bad seed` |
| `POST /api/restart-engine` | — | `restartEngine` (also while busy) | Status JSON |
| `POST /api/inspect` | `?x=`, `?y=` | `inspect` | Status JSON, or 400 `error: x and y required`, `error: bad coordinates` |
| `POST /api/command` | body: one line | `runCommand` | `{"exitCode":n,"ok":b,"output":s,"status":{…}}`; 200 if ok, else 400 |

A layer or speed matches its constant name or its label, ignoring case: `"overlay"` and `"Overlay"` both select `OVERLAY`.

**Status JSON.** Its shape, with placeholders:

```text
{"step":k,"seed":s,"width":W,"height":H,"layer":"Elevation","speed":"1x",
 "playing":false,"busy":false,"statusText":"Step k",
 "inspect":null | {"x":…,"y":…,"elevation":…,"plateId":…,"vx":…,"vy":…},
 "legend":[{"rgb":n,"label":"…"},…],
 "diag":{"<collector id>":{"last":n|null,"mean":n|null,"n":m},…}}
```

**Packed raster.** The body is $8 + 4WH$ bytes of big-endian 32-bit integers: $W$, $H$, then the $WH$ packed colours in row-major order,

$$\mathrm{body} = \mathrm{be32}(W)\,\|\,\mathrm{be32}(H)\,\|\,\mathop{\big\|}_{i=0}^{WH-1} \mathrm{be32}(\mathrm{rgb}_i).$$

`MapHost.packRasterInto` in [`MapHost.java`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java):

```java
putInt(body, 0, w);
putInt(body, 4, h);
int o = 8;
int[] pixels = image.pixels();
for (int i = 0; i < pixels.length; i++) {
  putInt(body, o, pixels[i]);
  o += 4;
}
```

The host keeps one body and its key $(k, \mathit{gen}, \mathit{layer})$: the step, the paint generation, and the layer. A raster request with the same key and size gets the same bytes; any other request refills the body in place, allocating only when the size changes:

$$\mathrm{body}_{t} = \begin{cases} \mathrm{body}_{t-1} & (k, \mathit{gen}, \mathit{layer})_t = (k, \mathit{gen}, \mathit{layer})_{t-1} \\ \mathrm{pack}(\mathit{raster}_t) & \text{otherwise.} \end{cases}$$

**Threads.** Requests run on a cached pool of daemon threads named `map-host-http`. Background steps run on one daemon thread, `map-host-advance`, and play ticks on `map-play` ([controller](controller.md)).

## Procedure

1. `MapHostApp.main` takes the port from its first argument (default `DEFAULT_PORT`, 7420), and writes its process id to `aethelgard-maphost.pid` in the system temporary folder (`pidFile`, `PID_FILE_NAME`). It starts the host for `WorldSpec.VIEW`, prints `Aethelgard map host http://127.0.0.1:<port>`, and registers a shutdown hook that closes the host and deletes the file. It then blocks for ever. [`MapHostApp.main`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java).
2. `start(spec, port)` builds the play scheduler, the advance thread, and a controller that uses them, then binds. `start(controller, port)` wraps a given controller and does not own its threads. `startView` is an ephemeral port on `WorldSpec.VIEW`. [`MapHost.start`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
3. `bind` creates the server on `127.0.0.1:port` (0 picks a free port), registers the twelve routes, sets the request pool, and starts. [`MapHost.bind`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
4. Every handler first answers a preflight through `preflight`, then checks the method, drains or reads the body (`drain`, `readBody`), reads parameters with `firstParam` and `decode`, calls the controller, and answers with `send`. [`MapHost.preflight`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java), [`MapHost.send`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
5. The route handlers are `health`, `status`, `raster`, `advance`, `play`, `pause`, `layer`, `speed`, `newWorld`, `restartEngine`, `inspect`, and `command`. `parseLayer` and `parseSpeed` resolve names. [`MapHost.command`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
6. `statusJson` writes the status object with the `field` helpers and `jsonString`, which escapes quotes, backslashes, and control characters, and `appendDiag` writes each collector's last value, mean, and count. [`MapHost.statusJson`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java), [`MapHost.appendDiag`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
7. `raster` answers `packedRasterCached`, which compares the key and refills through `packRasterInto` and `putInt`; `packRaster` packs into a new array, and `readInt` reads one integer back. [`MapHost.packedRasterCached`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).
8. `close` stops the server and its pool, and, when the host owns them, the play scheduler and the advance thread. `port`, `baseUrl`, `controller`, `packedBodyAllocations`, and `cachedPackedBody` read it. [`MapHost.close`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java).

## What is true afterwards

A status request never waits for a step: it reads the controller's captured values and the diagnostics hub. A raster response always matches the status's step and layer at the moment it was packed. Repeated raster requests between two paints return the same bytes without allocating. Every action answers with the status after the action was accepted, not after its step finished: an advance reports `busy: true` until a later status shows it done.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Host | `MapHost` | `startView`, `start`, `bind`, `port`, `baseUrl`, `controller`, `packedBodyAllocations`, `cachedPackedBody`, `close`, `health`, `status`, `raster`, `packedRasterCached`, `advance`, `play`, `pause`, `layer`, `speed`, `newWorld`, `restartEngine`, `inspect`, `command`, `statusJson`, `appendDiag`, `field`, `jsonString`, `packRaster`, `packRasterInto`, `readInt`, `putInt`, `parseLayer`, `parseSpeed`, `firstParam`, `decode`, `preflight`, `cors`, `send`, `drain`, `readBody`, `daemonFactory` | [`ui/src/main/java/com/aethelgard/ui/host/MapHost.java`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHost.java) |
| Process entry | `MapHostApp` | `main`, `pidFile`, `DEFAULT_PORT`, `PID_FILE_NAME` | [`ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java`](../../../ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java) |

Parent: [studio](README.md). The client that calls it: [web client](web/client.md). Why a local host and a web page: [ADR-011](../../paperwork/decisions/ADR-011-local-webview.md).
