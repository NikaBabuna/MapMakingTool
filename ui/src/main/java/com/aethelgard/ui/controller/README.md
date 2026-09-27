<!--
  File: ui/src/main/java/com/aethelgard/ui/controller/README.md
  Purpose: Door to the studio's controller: one session held, stepped in the background, played, inspected, and painted
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Controller

Holds the studio's one session and keeps a painted picture of it current while a person steps, plays, inspects, and types commands.

**Paper:** [controller](../../../../../../../../docs/architecture/studio/controller.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The controller is the studio's state, apart from any way of showing it: it has no HTTP, no window, and no web code, so a test can drive it directly and the HTTP host is only one caller. It owns the timing too, the background step and the play timer, because those decide when the session may change. Colours belong to the [raster](../raster/README.md) package, and the routes and JSON belong to the [http](../http/README.md) package.

## How it works

1. A `MapController` is built for a `WorldSpec` with an `Executor` for background steps and a `PlayScheduler` for play. `MapController.view` builds one for the map window. The constructor creates the `ProductSession` and calls `capture`.
2. `capture` reads the settled grids, velocities, and step index, then repaints through `paintIntoNextBuffer`, which alternates two `int` buffers and calls `ElevationRaster.paint` from the raster package.
3. `advanceAsync` sets `busy` and hands the step to the executor. The task re-checks that its session is still current before and after `ProductSession.advance`, captures, and releases `busy` in a `finally` block. `advance` does the same on the caller.
4. `play` starts the `PlayScheduler` with `MapSpeed.periodMillis`, and each tick calls `playTick`, which calls `advanceAsync`. `ExecutorPlayScheduler` is the scheduler the host uses, and `PlayScheduler.idle` is the one tests use.
5. `setLayer` repaints from the captured grids. `newWorld` and `restartEngine` go through `replaceSession`. `inspect` fills a `CellInspect`. `legend` returns `LegendEntry` rows. `runCommand` runs a line through `CommandDispatch` and captures.
6. Every change calls `fire`, which runs each listener registered with `addChangeListener`. Readers such as `statusText`, `isBusy`, and `isPlaying` never take the session's lock.

**Start reading at:** `MapController.advanceAsync` in [MapController.java](MapController.java).

## Depends on

- [raster](../raster/README.md) — `ElevationRaster.paint` for every capture, and `MapLayer` for the layer in force
- [product session](../../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md) — `ProductSession`, the world it steps and reads
- [product session diagnostics](../../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md) — `DiagnosticIds` for the `paint.wall` sample
- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `WorldSpec`, `Grid`, and `PlateVelocities`
- [cli](../../../../../../../../cli/src/main/java/com/aethelgard/cli/README.md) — `CommandDispatch` and `CliResult` for terminal lines

## Used by

- [http](../http/README.md) — `MapHost` wraps one `MapController`, and builds it with an `ExecutorPlayScheduler`
- [controller tests](../../../../../../test/java/com/aethelgard/ui/controller/README.md) — `MapControllerTest` drives it directly

## Where each step happens

### [Controller](../../../../../../../../docs/architecture/studio/controller.md)

| Step | Member | File |
|------|--------|------|
| 1. Construction keeps the spec, executor, and scheduler, creates the session, and captures; the launch controller is for the map window | `MapController.MapController`, `MapController.view` | [MapController.java](MapController.java) |
| 2. Capture reads the session, repaints into the next buffer, and records the paint time | `MapController.capture`, `MapController.paintIntoNextBuffer` | [MapController.java](MapController.java) |
| 3. A background step sets busy, submits the step with its target checks, and releases busy; a step on the caller advances and captures | `MapController.advanceAsync`, `MapController.advance` | [MapController.java](MapController.java) |
| 4. Play, pause, the play tick, and a change of speed drive the scheduler port | `MapController.play`, `MapController.pause`, `MapController.playTick`, `MapController.setSpeed`, `PlayScheduler.start`, `PlayScheduler.stop`, `PlayScheduler.idle` | [MapController.java](MapController.java), [PlayScheduler.java](PlayScheduler.java) |
| 5. The timer scheduler runs ticks on one daemon thread at a fixed rate | `ExecutorPlayScheduler.start`, `ExecutorPlayScheduler.stop`, `ExecutorPlayScheduler.close` | [ExecutorPlayScheduler.java](ExecutorPlayScheduler.java) |
| 6. A change of layer repaints from the captured grids | `MapController.setLayer` | [MapController.java](MapController.java) |
| 7. A new world and a restart replace the session | `MapController.newWorld`, `MapController.restartEngine`, `MapController.replaceSession` | [MapController.java](MapController.java) |
| 8. Inspecting a cell fills and remembers a `CellInspect` | `MapController.inspect`, `CellInspect` | [MapController.java](MapController.java), [CellInspect.java](CellInspect.java) |
| 9. The legend lists colour and label rows for the layer | `MapController.legend`, `MapController.elevationLegend`, `MapController.plateLegend`, `LegendEntry` | [MapController.java](MapController.java), [LegendEntry.java](LegendEntry.java) |
| 10. A command line is refused while busy, else dispatched on the session, then captured | `MapController.runCommand` | [MapController.java](MapController.java) |
| 11. The read side, the status text, and the change listeners | `MapController.statusText`, `MapController.WORKING_STATUS`, `MapController.stepIndex`, `MapController.isBusy`, `MapController.isPlaying`, `MapController.addChangeListener`, `MapController.fire` | [MapController.java](MapController.java) |
| 12. The speeds 1x, 2x, 4x, and Fastest | `MapSpeed.X1`, `MapSpeed.X2`, `MapSpeed.X4`, `MapSpeed.FASTEST`, `MapSpeed.periodMillis` | [MapSpeed.java](MapSpeed.java) |

The layers of step 12 are in the [raster](../raster/README.md) package.

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MapController.java](MapController.java) | Holds one session, steps it in the background, plays it, inspects it, runs commands on it, and keeps its painted raster current | `MapController`, `advanceAsync`, `capture`, `runCommand` |
| [PlayScheduler.java](PlayScheduler.java) | The port through which the controller asks for repeating play ticks | `PlayScheduler`, `idle` |
| [ExecutorPlayScheduler.java](ExecutorPlayScheduler.java) | Runs play ticks at a fixed rate on one daemon thread | `ExecutorPlayScheduler` |
| [MapSpeed.java](MapSpeed.java) | The play speeds, each with its label and its period in milliseconds | `MapSpeed` |
| [CellInspect.java](CellInspect.java) | The values of one inspected cell: position, elevation, plate, and velocity | `CellInspect` |
| [LegendEntry.java](LegendEntry.java) | One legend row: a packed colour and its label | `LegendEntry` |
