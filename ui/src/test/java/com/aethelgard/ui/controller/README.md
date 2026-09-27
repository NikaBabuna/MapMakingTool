<!--
  File: ui/src/test/java/com/aethelgard/ui/controller/README.md
  Purpose: Door to the tests of the studio's controller: layers, background steps, play, new world and restart, inspect, the console, and safe painting
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Controller tests

Proves what the studio's controller does with its session, without a server or a window.

**Paper:** [controller](../../../../../../../../docs/architecture/studio/controller.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The tests sit in the controller's own package, so they can reach its package-private test hook, `MapController.paintBufferForTest`, as well as its public side. They drive the controller directly, which keeps HTTP out of them: the routes are proved by the [http tests](../http/README.md) and the colours by the [raster tests](../raster/README.md).

## How it works

Each test builds a `MapController` on a small `WorldSpec` of 16 by 8 cells, seed 3, and chooses how background steps run. An `ArrayDeque` of tasks lets a test hold a step in flight and release it when it likes, which is how `advanceRunsInTheBackground` and `statusNeverWaitsForAStep` see the busy state. `Runnable::run` steps on the caller, and a fixed thread pool with a `CyclicBarrier` races paints in `paintNeverTears`. Play is driven by `RecordingScheduler`, a `PlayScheduler` that keeps the tick and its period, or by `PlayScheduler.idle`, with the test calling `MapController.playTick` itself. The helpers `labels`, `row`, and `contains` read legends and rasters.

**Start reading at:** `MapControllerTest.advanceRunsInTheBackground` in [MapControllerTest.java](MapControllerTest.java).

## Depends on

- [controller](../../../../../../main/java/com/aethelgard/ui/controller/README.md) — `MapController`, `MapSpeed`, `PlayScheduler`, `CellInspect`, and `LegendEntry`, the code under test
- [raster](../../../../../../main/java/com/aethelgard/ui/raster/README.md) — `ElevationRaster` and `MapLayer`, to read what the controller painted
- [product session](../../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md) — `ProductSession`, to compare with the world the controller holds
- [product session diagnostics](../../../../../../../../product/src/main/java/com/aethelgard/product/session/diagnostics/README.md) — `DiagnosticIds` and `RingDiagnosticCollector`, to read the `paint.wall` samples
- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `WorldSpec`
- [cli](../../../../../../../../cli/src/main/java/com/aethelgard/cli/README.md) — `CliResult`, the answer of a console line

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MapControllerTest.java](MapControllerTest.java) | Proves the controller's layers and legends, background steps, play, new world and restart, inspect, console, and safe painting | `MapControllerTest` |
