<!--
  File: docs/architecture/studio/controller.md
  Purpose: MapController, PlayScheduler, ExecutorPlayScheduler, MapLayer, MapSpeed, CellInspect, LegendEntry — the studio's hold on one session: advance, play, layers, inspection, commands, and the painted map
  Audience: Agents and humans
  Update when: MapController.advanceAsync, capture, paintReuse, replaceSession, runCommand, a MapLayer or MapSpeed constant, or ExecutorPlayScheduler changes
-->

# Controller

The studio keeps one world and a painted picture of it. Stepping the world can take a while, so the studio steps it in the background, says "Working..." meanwhile, and refuses a second step until the first is done. Play is a timer that asks for a step at a chosen rate. The picture is repainted after every step or change of layer, into one of two alternating buffers, so a picture already handed out is never overwritten while the next one is painted.

## What it reads

A `WorldSpec` (the map window, `WorldSpec.VIEW`, at launch), an `Executor` for background steps, and a `PlayScheduler`. From its session: the settled elevation, plates, velocities, and step index.

## What it writes

Steps of its session. A painted `ElevationRaster`. Samples of `paint.wall` into the session's diagnostics. Calls to every listener registered with `onChanged`. Refusals: a null spec, executor, scheduler, layer, speed, or listener throws `NullPointerException`; `inspect` outside the map throws `IllegalArgumentException`; `runCommand` while busy answers `(2, "error: busy")`.

## Model

The controller is the state

$$\mathit{ctl} = \bigl(\mathit{spec},\; \mathit{session},\; \mathit{busy},\; \mathit{playing},\; \mathit{layer},\; \mathit{speed},\; \mathit{inspected},\; (E, P, v, k)_{\mathrm{cap}},\; \mathit{raster},\; \mathit{gen}\bigr),$$

with the captured grids, velocities, and step index written $(E, P, v, k)_{\mathrm{cap}}$, and $\mathit{gen}$ the paint generation. Its transitions:

| Event | Guard | Effect |
|-------|-------|--------|
| `advanceAsync` | $\mathit{busy} = 0$, set atomically to 1 | On the executor: if $\mathit{session}$ is still the target, advance it one step, capture, refresh the inspected cell; then, if $\mathit{session}$ is still the target, $\mathit{busy} := 0$ |
| `advance` | — | Advance $\mathit{session}$ on the caller, capture |
| `play`, `pause` | $\mathit{playing} = 0$, resp. $\mathit{playing} = 1$ | Start or stop the scheduler with the period of $\mathit{speed}$ |
| `playTick` | $\mathit{playing} = 1$ | `advanceAsync` (a tick while busy is dropped, not queued) |
| `setSpeed` | — | $\mathit{speed} :=$ the new speed; if playing, restart the scheduler |
| `setLayer` | — | $\mathit{layer} :=$ the new layer; repaint from the captured grids |
| `newWorld(s)` | $\mathit{busy} = 0$ | Pause; $\mathrm{spec} := (W, H, s)$; replace the session |
| `restartEngine` | — | Pause; replace the session with the same spec |
| `runCommand(line)` | $\mathit{busy} = 0$ | Dispatch the line on $\mathit{session}$; capture |

Replacing the session builds a new one at step 0, clears $\mathit{busy}$ and the inspected cell, and captures. An advance still running on the old session finds on both checks that the target is no longer current. It captures nothing, and it leaves $\mathit{busy}$ alone.

`MapController.advanceAsync` (the target checks) in [`MapController.java`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java):

```java
try {
  if (session != target) {
    return;
  }
  target.advance();
  if (session != target) {
    return;
  }
```

Painting alternates two buffers of $WH$ ints, so the $i$-th paint writes buffer $i \bmod 2$ and the raster of paint $i-1$ stays intact while paint $i$ runs:

$$\mathit{raster}_i = \mathrm{paint}\bigl(E, P, \mathit{layer};\; \mathit{buf}_{i \bmod 2}\bigr), \qquad \mathit{gen} := \mathit{gen} + 1 .$$

`MapController.paintReuse` in [`MapController.java`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java):

```java
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
```

## Procedure

1. The constructors keep the spec, executor, and scheduler (by default one that runs on the caller and one that never ticks), create the session, and capture. `view` builds a controller for `WorldSpec.VIEW`. [`MapController`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
2. `capture` reads the settled elevation, plates, velocities, and step index from the session, which takes the session's lock once for each. It then stores them, repaints through `paintReuse` under the paint lock, and records the paint time as `lastPaintNanos` and as a `paint.wall` sample. [`MapController.capture`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
3. `advanceAsync` sets busy with a compare-and-set, notifies, and submits the step, with the target checks and the release of busy in a `finally` block. `advance` steps on the caller. [`MapController.advanceAsync`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
4. `play`, `pause`, `playTick`, and `setSpeed` drive the scheduler. The `PlayScheduler` port has `start(period, tick)` and `stop()`, and `idle()` is a scheduler that never ticks. [`MapController.play`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java), [`PlayScheduler`](../../../ui/src/main/java/com/aethelgard/ui/PlayScheduler.java).
5. `ExecutorPlayScheduler` runs ticks on one daemon thread, `map-play`, at a fixed rate with period $\max(1, \text{ms})$ and the same initial delay. It cancels the previous schedule on every `start`, and `close` shuts the thread down. [`ExecutorPlayScheduler.start`](../../../ui/src/main/java/com/aethelgard/ui/ExecutorPlayScheduler.java).
6. `setLayer` repaints from the captured grids without touching the session, so it works while a step is in flight. [`MapController.setLayer`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
7. `newWorld` and `restartEngine` go through `replaceSession`. [`MapController.replaceSession`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
8. `inspect(x, y)` reads the captured grids and velocities into a `CellInspect(x, y, elevation, plateId, vx, vy)`, remembers it, and notifies. Every capture afterwards refreshes the remembered cell. [`MapController.inspect`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java), [`CellInspect`](../../../ui/src/main/java/com/aethelgard/ui/CellInspect.java).
9. `legend` returns `LegendEntry(rgb, label)` rows for the layer. `elevationLegend` gives deep (−64), shallow (−1), low (0), and high (64). `plateLegend` gives interior and boundary. The overlay legend is the elevation rows plus the darkened colour of elevation 16, labelled `"Boundary"`. [`MapController.legend`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java), [`LegendEntry`](../../../ui/src/main/java/com/aethelgard/ui/LegendEntry.java).
10. `runCommand` refuses while busy, runs the line through `CommandDispatch.execute` on the session, and captures, so a command that advances the world repaints the map ([language](../cli/language.md)). [`MapController.runCommand`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
11. The read side is `spec`, `session`, `stepIndex` (the captured index, without the session lock), `raster`, `paintGeneration`, `layer`, `speed`, `playing`, `inspected`, `busy`, `lastPaintNanos`, and `statusText`, which is `Working...` (`WORKING_STATUS`) while busy and `Step <k>` otherwise. `onChanged` registers a listener and calls it once, and `fire` calls all of them. [`MapController.statusText`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java).
12. The layers are `ELEVATION`, `PLATES`, and `OVERLAY`, labelled `"Elevation"`, `"Plates"`, and `"Overlay"`. The speeds are `X1` (250 ms), `X2` (125 ms), `X4` (62 ms), and `FASTEST` (1 ms), labelled `"1x"`, `"2x"`, `"4x"`, and `"Fastest"`. [`MapLayer`](../../../ui/src/main/java/com/aethelgard/ui/MapLayer.java), [`MapSpeed`](../../../ui/src/main/java/com/aethelgard/ui/MapSpeed.java).

## What is true afterwards

At most one background step runs at a time, and a play tick that finds a step running is lost, so the effective play rate is the lower of the chosen rate and the rate the world can step. Status reads never wait for a step. The raster always matches the captured step and layer, and the paint generation grows with every paint. Capture reads its four values under four separate acquisitions of the session's lock, so a step that finishes between them can mix two steps in one capture. The studio avoids that by stepping only through the executor and by refusing commands while busy.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Controller | `MapController` | `MapController`, `view`, `WORKING_STATUS`, `spec`, `session`, `lastPaintNanos`, `stepIndex`, `raster`, `paintGeneration`, `layer`, `speed`, `playing`, `inspected`, `busy`, `statusText`, `onChanged`, `setLayer`, `setSpeed`, `play`, `pause`, `playTick`, `newWorld`, `restartEngine`, `replaceSession`, `inspect`, `legend`, `advance`, `advanceAsync`, `runCommand`, `elevationLegend`, `plateLegend`, `capture`, `paintReuse`, `paintBufferForTest`, `fire` | [`ui/src/main/java/com/aethelgard/ui/MapController.java`](../../../ui/src/main/java/com/aethelgard/ui/MapController.java) |
| Play port | `PlayScheduler` | `PlayScheduler.start`, `PlayScheduler.stop`, `idle` | [`ui/src/main/java/com/aethelgard/ui/PlayScheduler.java`](../../../ui/src/main/java/com/aethelgard/ui/PlayScheduler.java) |
| Play timer | `ExecutorPlayScheduler` | `ExecutorPlayScheduler`, `ExecutorPlayScheduler.start`, `ExecutorPlayScheduler.stop`, `close` | [`ui/src/main/java/com/aethelgard/ui/ExecutorPlayScheduler.java`](../../../ui/src/main/java/com/aethelgard/ui/ExecutorPlayScheduler.java) |
| Layer | `MapLayer` | `ELEVATION`, `PLATES`, `OVERLAY`, `MapLayer.label` | [`ui/src/main/java/com/aethelgard/ui/MapLayer.java`](../../../ui/src/main/java/com/aethelgard/ui/MapLayer.java) |
| Speed | `MapSpeed` | `X1`, `X2`, `X4`, `FASTEST`, `MapSpeed.label`, `periodMillis` | [`ui/src/main/java/com/aethelgard/ui/MapSpeed.java`](../../../ui/src/main/java/com/aethelgard/ui/MapSpeed.java) |
| Inspected cell | `CellInspect` | `CellInspect`, `x`, `y`, `elevation`, `plateId`, `vx`, `vy` | [`ui/src/main/java/com/aethelgard/ui/CellInspect.java`](../../../ui/src/main/java/com/aethelgard/ui/CellInspect.java) |
| Legend row | `LegendEntry` | `LegendEntry`, `rgb`, `LegendEntry.label` | [`ui/src/main/java/com/aethelgard/ui/LegendEntry.java`](../../../ui/src/main/java/com/aethelgard/ui/LegendEntry.java) |

Parent: [studio](README.md). The paint it calls: [raster](raster.md). The host that drives it: [http](http.md).
