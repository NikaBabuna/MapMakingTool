<!--
  File: docs/architecture/studio/host.md
  Purpose: MapHost routes, raster cache, play, and CommandDispatch
  Audience: Agents and humans
  Update when: MapHost routes or the command language change
-->

# Host

`MapHost` is a loopback HTTP server in front of one `MapController`. The bind address is `127.0.0.1`. There is no Swing UI. The desktop shell is Tauri, and the page is Next. Both talk to this host.

## What it reads

The `MapController`'s session: step, grids, diagnostics, busy flag. `GET /api/status` reads a cached step and `busy` so a poll does not take the session lock while physics runs.

## What it writes

Advances, layer, speed, a new seed, a restart to step 0, an inspect cell, and command results, by calling the controller. Raster bytes are a cache, not a field.

## Procedure

| Route | Effect |
|-------|--------|
| `GET /health` | Body `ok`. |
| `GET /api/status` | JSON step, busy, optional inspect and legend, diagnostic means. |
| `GET /api/raster` | Packed RGB. Headers `width` and `height`. Body is big-endian ints. |
| `POST /api/advance` | `advanceAsync` on a single-thread executor. |
| `POST /api/play`, `POST /api/pause` | Arm or stop the server play scheduler. |
| `POST /api/layer` | Selects `MapLayer`. |
| `POST /api/speed` | Selects `MapSpeed`. |
| `POST /api/new-world` | New seed. Ignored while busy. |
| `POST /api/restart-engine` | Same seed, back to step 0. Accepted while busy. |
| `POST /api/inspect` | Cell `x`, `y`. |
| `POST /api/command` | One console line. |

`MapHost` keeps one packed `byte[]`. It refills when step, layer, or paint generation changes, and reuses the array otherwise. A play soak that does not change those keys does not allocate another body.

The window opens at step 0 on `WorldSpec.VIEW`. One pixel is one cell. The Next page is `ui/web`. Panel layout is `ui/web/src/lib/panels.ts`. The console panel is `Terminal.tsx`. Play speeds are `MapSpeed`. Play in the Next client is a timer that posts `/api/advance`. While an advance is in flight the world rail reads Working, and further advances are ignored. There is no map overlay for that wait.

`POST /api/command` and the CLI both call `com.aethelgard.cli.CommandDispatch` on the same `ProductSession`. The language is a noun path plus a verb: `session get`, `list pool`, `pool.plates get`, `systems.tectonics get`, `diag list|on|off|clear`. `ui` depends on `cli` for that class and for nothing else.

## What is true afterwards

A raster response matches `ElevationRaster` for the current step and layer. A command that advances the session is the same step the HTTP advance runs. Status can be read without waiting on the physics lock.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| HTTP | `MapHost` | `ui/.../host/MapHost.java` |
| Session owner in the UI | `MapController` | `ui/.../MapController.java` |
| Commands | `CommandDispatch` | `cli/.../CommandDispatch.java` |
| Page | Next | `ui/web/` |
| Shell | Tauri | `ui/desktop/` |

Parent: [studio](README.md). The session behind the controller: [session](session.md).
