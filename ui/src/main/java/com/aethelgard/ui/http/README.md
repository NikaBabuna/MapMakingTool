<!--
  File: ui/src/main/java/com/aethelgard/ui/http/README.md
  Purpose: Door to the studio's HTTP host: one controller served on the loopback address, and the process that starts it
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# HTTP host

Serves one map controller over HTTP on the loopback address, and starts that server as its own process for the desktop shell.

**Paper:** [http](../../../../../../../../docs/architecture/studio/http.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The web page reaches the Java side only over HTTP, so everything that knows about routes, status codes, JSON, and the packed raster body is kept here, and the [controller](../controller/README.md) stays free of HTTP. The process entry lives beside the host because it does nothing but start one host and keep it running. What a route does to the world belongs to the controller, and how a colour is chosen belongs to the [raster](../raster/README.md) package.

## How it works

1. `MapHostApp.main` reads the port (default `MapHostApp.DEFAULT_PORT`), writes its process id to `MapHostApp.pidFile`, calls `MapHost.start` for `WorldSpec.VIEW`, registers a shutdown hook that closes the host and deletes the file, and joins its own thread for ever.
2. `MapHost.start(WorldSpec, int)` builds an `ExecutorPlayScheduler`, a single daemon thread `map-host-advance` from `daemonFactory`, and a `MapController` on both, then calls `bind`. `MapHost.start(MapController, int)` binds around a given controller without owning its threads, and `startView` binds a free port for `WorldSpec.VIEW`.
3. `bind` creates the JDK `HttpServer` on `127.0.0.1`, registers one handler per route (`answerHealth`, `answerStatus`, `answerRaster`, `answerAdvance`, `answerPlay`, `answerPause`, `answerLayer`, `answerSpeed`, `answerNewWorld`, `answerRestartEngine`, `answerInspect`, `answerCommand`), and runs requests on a cached pool of `map-host-http` daemon threads.
4. Every handler starts with `answerPreflight`, which sets the origin header with `allowAnyOrigin` and answers an `OPTIONS` request itself. It then checks the method, reads its input with `drain`, `readBody`, `firstParam`, `decode`, `parseLayer`, and `parseSpeed`, calls the controller, and answers through `send`.
5. Actions answer with `statusJson`, written with `appendField`, `jsonString`, and `appendDiag`. `answerRaster` answers `packedRasterCached`, which refills one kept body through `packRasterInto` and `putInt` only when the step, the paint generation, or the layer changed.
6. `close` stops the server and the request pool, and the scheduler and advance thread when this host built them.

**Start reading at:** `MapHost.bind` in [MapHost.java](MapHost.java).

## Depends on

- [controller](../controller/README.md) — `MapController`, which every route calls; `ExecutorPlayScheduler`; `MapSpeed`, `CellInspect`, and `LegendEntry` for the status
- [raster](../raster/README.md) — `ElevationRaster.pixels`, packed into the raster body, and `MapLayer` for layer names
- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `WorldSpec`, the world the process entry starts
- [product session diagnostics](../../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md) — `DiagnosticsHub` and `DiagnosticCollector`, written into the status as `diag`
- [cli](../../../../../../../../cli/src/main/java/com/aethelgard/cli/README.md) — `CliResult`, the answer of `/api/command`

## Used by

- [desktop shell source](../../../../../../../desktop/src-tauri/src/README.md) — starts `MapHostApp` through the Maven wrapper
- [the UI module's build](../../../../../../../README.md) — `ui/pom.xml` names `MapHostApp` as the main class of `exec:java`
- [http tests](../../../../../../test/java/com/aethelgard/ui/http/README.md) — `MapHostTest` sends real requests to a host on a free port
- [web front](../../../../../../../web/README.md) — calls every route over HTTP, through its host client

## Where each step happens

### [HTTP host](../../../../../../../../docs/architecture/studio/http.md)

| Step | Member | File |
|------|--------|------|
| 1. The process entry reads the port, writes its process id, starts the host, and blocks | `MapHostApp.main`, `MapHostApp.pidFile`, `MapHostApp.DEFAULT_PORT`, `MapHostApp.PID_FILE_NAME` | [MapHostApp.java](MapHostApp.java) |
| 2. Starting a host for a spec, for a given controller, or for the map window | `MapHost.start`, `MapHost.startView`, `MapHost.daemonFactory` | [MapHost.java](MapHost.java) |
| 3. Binding creates the server, registers the twelve routes, and starts | `MapHost.bind` | [MapHost.java](MapHost.java) |
| 4. Every handler answers a preflight, checks the method, reads its input, calls the controller, and answers | `MapHost.answerPreflight`, `MapHost.allowAnyOrigin`, `MapHost.drain`, `MapHost.readBody`, `MapHost.firstParam`, `MapHost.decode`, `MapHost.send` | [MapHost.java](MapHost.java) |
| 5. One handler per route; layer and speed names resolved | `MapHost.answerHealth`, `MapHost.answerStatus`, `MapHost.answerRaster`, `MapHost.answerAdvance`, `MapHost.answerPlay`, `MapHost.answerPause`, `MapHost.answerLayer`, `MapHost.answerSpeed`, `MapHost.answerNewWorld`, `MapHost.answerRestartEngine`, `MapHost.answerInspect`, `MapHost.answerCommand`, `MapHost.parseLayer`, `MapHost.parseSpeed` | [MapHost.java](MapHost.java) |
| 6. The status object, its escaped strings, and its diagnostics | `MapHost.statusJson`, `MapHost.appendField`, `MapHost.jsonString`, `MapHost.appendDiag` | [MapHost.java](MapHost.java) |
| 7. The cached raster body, refilled in place when its key changes | `MapHost.packedRasterCached`, `MapHost.packRasterInto`, `MapHost.putInt`, `MapHost.packRaster`, `MapHost.readInt` | [MapHost.java](MapHost.java) |
| 8. Closing, and the host's read side | `MapHost.close`, `MapHost.port`, `MapHost.baseUrl`, `MapHost.controller`, `MapHost.packedBodyAllocations`, `MapHost.cachedPackedBody` | [MapHost.java](MapHost.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MapHost.java](MapHost.java) | Serves one controller over HTTP on the loopback address: its status, its raster, and its actions | `MapHost`, `start`, `bind`, `statusJson`, `packRaster` |
| [MapHostApp.java](MapHostApp.java) | Starts the map host on the loopback address, as the desktop shell and Maven launch it | `MapHostApp`, `main`, `pidFile` |
