<!--
  File: docs/architecture/studio/controller.md
  Purpose: Controller — the studio's hold on one session: advance, play, layers, inspection, commands, and the painted map
  Audience: Agents and humans
  Update when: How the studio steps, plays, repaints, inspects, or runs commands on its session changes, or a layer or speed is added
-->

# Controller

The studio keeps one world and a painted picture of it. Stepping the world can take a while, so the studio steps it in the background, says "Working..." meanwhile, and refuses a second step until the first is done. Play is a timer that asks for a step at a chosen rate. The picture is repainted after every step or change of layer, into one of two alternating buffers, so a picture already handed out is never overwritten while the next one is painted.

## What it reads

A `WorldSpec` (the map window, `WorldSpec.VIEW`, at launch), an `Executor` for background steps, and a `PlayScheduler`. From its session: the settled elevation, plates, velocities, and step index.

## What it writes

Steps of its session. A painted `ElevationRaster`. Samples of `paint.wall` into the session's diagnostics. Calls to every registered change listener. Refusals: a null spec, executor, scheduler, layer, speed, or listener throws `NullPointerException`; inspecting a cell outside the map throws `IllegalArgumentException`; a command line while busy answers `(2, "error: busy")`.

## Model

The controller is the state

$$\mathit{ctl} = \bigl(\mathit{spec},\; \mathit{session},\; \mathit{busy},\; \mathit{playing},\; \mathit{layer},\; \mathit{speed},\; \mathit{inspected},\; (E, P, v, k)_{\mathrm{cap}},\; \mathit{raster},\; \mathit{gen}\bigr),$$

with the captured grids, velocities, and step index written $(E, P, v, k)_{\mathrm{cap}}$, and $\mathit{gen}$ the paint generation. Its transitions:

| Event | Guard | Effect |
|-------|-------|--------|
| Background step | $\mathit{busy} = 0$, set atomically to 1 | On the executor: if $\mathit{session}$ is still the target, advance it one step, capture, refresh the inspected cell; then, if $\mathit{session}$ is still the target, $\mathit{busy} := 0$ |
| Step on the caller | — | Advance $\mathit{session}$ on the caller, capture |
| Play, pause | $\mathit{playing} = 0$, resp. $\mathit{playing} = 1$ | Start or stop the scheduler with the period of $\mathit{speed}$ |
| Play tick | $\mathit{playing} = 1$ | A background step (a tick while busy is dropped, not queued) |
| Set speed | — | $\mathit{speed} :=$ the new speed; if playing, restart the scheduler |
| Set layer | — | $\mathit{layer} :=$ the new layer; repaint from the captured grids |
| New world with seed $s$ | $\mathit{busy} = 0$ | Pause; $\mathrm{spec} := (W, H, s)$; replace the session |
| Restart | — | Pause; replace the session with the same spec |
| Run a command line | $\mathit{busy} = 0$ | Dispatch the line on $\mathit{session}$; capture |

Replacing the session builds a new one at step 0, clears $\mathit{busy}$ and the inspected cell, and captures. An advance still running on the old session finds on both checks that the target is no longer current. It captures nothing, and it leaves $\mathit{busy}$ alone.

Painting alternates two buffers of $WH$ ints, so the $i$-th paint writes buffer $i \bmod 2$ and the raster of paint $i-1$ stays intact while paint $i$ runs:

$$\mathit{raster}_i = \mathrm{paint}\bigl(E, P, \mathit{layer};\; \mathit{buf}_{i \bmod 2}\bigr), \qquad \mathit{gen} := \mathit{gen} + 1 .$$

## Procedure

1. Construction keeps the spec, the executor, and the scheduler (by default one that runs work on the caller and one that never ticks), creates the session, and captures. The launch controller is built for `WorldSpec.VIEW`.
2. Capture reads the settled elevation, plates, velocities, and step index from the session, which takes the session's lock once for each. It then stores them, repaints into the next of the two buffers under the paint lock, and records the paint time, as the last paint time and as a `paint.wall` sample.
3. A background step sets busy with a compare-and-set, notifies the listeners, and submits the step, with the two target checks and the release of busy in a block that always runs. A step on the caller advances the session and captures.
4. Play, pause, the play tick, and a change of speed drive the scheduler. The scheduler port starts a tick at a period and stops it, and the idle scheduler never ticks.
5. The timer scheduler runs ticks on one daemon thread, `map-play`, at a fixed rate with period $\max(1, \text{ms})$ and the same initial delay. It cancels the previous schedule on every start, and closing it shuts the thread down.
6. A change of layer repaints from the captured grids without touching the session, so it works while a step is in flight.
7. A new world and a restart both replace the session.
8. Inspecting cell $(x, y)$ reads the captured grids and velocities into a `CellInspect` of $x$, $y$, elevation, plate id, $v_x$, and $v_y$, remembers it, and notifies. Every capture afterwards refreshes the remembered cell.
9. The legend is a list of `LegendEntry` rows, a colour and a label, for the layer. The elevation legend gives deep (−64), shallow (−1), low (0), and high (64). The plates legend gives interior and boundary. The overlay legend is the elevation rows plus the darkened colour of elevation 16, labelled "Boundary".
10. A command line is refused while busy. Otherwise it runs through the shared command language on the session, and the controller captures, so a command that advances the world repaints the map ([language](../cli/language.md)).
11. The read side answers the spec, the session, the captured step index (without the session lock), the raster, the paint generation, the layer, the speed, whether it plays, the inspected cell, whether it is busy, the last paint time, and the status text, which is "Working..." while busy and "Step k" otherwise. Registering a change listener calls it once, and every change calls all of them.
12. The layers are Elevation, Plates, and Overlay. The speeds are 1x (250 ms), 2x (125 ms), 4x (62 ms), and Fastest (1 ms).

## What is true afterwards

At most one background step runs at a time, and a play tick that finds a step running is lost, so the effective play rate is the lower of the chosen rate and the rate the world can step. Status reads never wait for a step. The raster always matches the captured step and layer, and the paint generation grows with every paint. Capture reads its four values under four separate acquisitions of the session's lock, so a step that finishes between them can mix two steps in one capture. The studio avoids that by stepping only through the executor and by refusing commands while busy.

Code: [controller/](../../../ui/src/main/java/com/aethelgard/ui/controller/README.md) · [raster/](../../../ui/src/main/java/com/aethelgard/ui/raster/README.md)
Parent: [studio](README.md). The paint it calls: [raster](raster.md). The host that drives it: [http](http.md).
