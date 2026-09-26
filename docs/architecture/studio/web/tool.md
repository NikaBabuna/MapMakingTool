<!--
  File: docs/architecture/studio/web/tool.md
  Purpose: MapTool, app/layout.tsx, app/page.tsx — the root of the page: its state, its poll, play, and action loops, and the guard against stale answers
  Audience: Agents and humans
  Update when: MapTool's loops, handlers, keyboard map, state, or persisted flags change, or the app shell changes
-->

# Tool

The page's root keeps a copy of what the host last said and redraws from it. Three things keep that copy current: a poll that asks the host every fifth of a second, a play timer that asks for a step at the chosen speed, and the person's own actions. Because answers can arrive out of order, every action that changes the picture stamps a generation number, and an answer that belongs to an older generation is thrown away.

## What it reads

The host's status and raster ([client](client.md)); the stage size reported by the [canvas](canvas.md); the person's clicks, keys, menu choices, and terminal lines; and saved flags and sizes in the browser's local storage ([chrome](chrome.md)).

## What it writes

Requests to the host; the page's React state (status, raster, viewport, playing, speed, rails, panels, layout, dialogs, error banner); and saved flags and sizes in local storage. A failed request shows its message in an error banner, whose Retry button reconnects.

## Model

**Stale guard.** Let $\mathit{applyGen}$ be the apply generation, a counter. An action that fetches a new picture sets $\mathit{applyGen} := \mathit{applyGen} + 1$ and remembers it; a poll remembers the current value. A response to a request made under $\mathit{applyGen}_r$ is applied only when

$$\mathit{applyGen}_r = \mathit{applyGen} \quad \text{at the time the response arrives,}$$

so a slow poll that returns after a layer change cannot put the old layer back.

`MapTool` (the poll body) in [`MapTool.tsx`](../../../../ui/web/src/components/MapTool.tsx):

```tsx
const gen = applyGenRef.current;
const next = await fetchStatus();
if (gen !== applyGenRef.current) {
  return;
}
setStatus(next);
busyRef.current = next.busy;
if (!next.busy) {
  const bytes = await fetchRaster();
  if (gen !== applyGenRef.current) {
    return;
  }
  setRaster(bytes);
}
```

**Loops.** With $\Delta t_{\mathrm{poll}} = 200$ ms and $\Delta t_{\mathrm{play}} \in \{250, 125, 62, 1\}$ ms the period of the chosen speed:

$$\text{poll}: \text{every } \Delta t_{\mathrm{poll}}:\;\; \mathrm{health};\; \mathrm{status};\; \mathrm{raster} \text{ unless busy}; \qquad \text{play}: \text{every } \Delta t_{\mathrm{play}} \text{ while playing}:\;\; \mathrm{advance} \text{ unless busy}.$$

Play runs in the page, not in the host: the page posts `/api/advance` on its own timer and never calls `/api/play` or `/api/pause`. A tick that finds the host busy is skipped. The speed is also sent to the host, where it sets the rate of a scheduler the page does not start.

**Performance rail.** Each row shows the mean of one diagnostics collector: nanoseconds as µs below 1 ms, as ms with two decimals below 10 ms and one decimal above; bytes as MiB with one decimal. The step rate is

$$r = \frac{10^9}{\overline{\mathrm{advance.wall}}} \;\; \text{steps per second.}$$

The rail shows `paint.wall`, `advance.wall`, five phase timings (every phase but isostasy), `heap.used`, and `heap.max`.

## Procedure

1. The app shell sets the page title and description, loads the IBM Plex Sans and Mono fonts into CSS variables, imports the stylesheet, and renders the root component on the only route. [`RootLayout`](../../../../ui/web/src/app/layout.tsx), [`metadata`](../../../../ui/web/src/app/layout.tsx), [`HomePage`](../../../../ui/web/src/app/page.tsx).
2. On mount, the root restores the rail and panel flags and the layout sizes from local storage with `readFlag` and `readLayout`, and writes each flag back when it changes with `writeFlag`. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
3. `connect` checks the host's health, shows `Map host offline at <host>. Start MapHostApp on port 7420.` when it fails, and otherwise refreshes status and raster. Then the 200 ms poll starts. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
4. While playing, the play timer posts one advance per period unless the last status said busy, and stops playing when a request fails. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
5. The canvas reports the stage size, and on the first report with a real size the root fits the whole map into the stage. `fitView` does the same on demand. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
6. The action handlers post to the host and apply the answer: advance one step; advance $N$ steps through the terminal line `session advance N`; change layer (with a new generation); change speed; new world (confirmed first when the step is above 0, then the view is fitted); restart the engine; inspect a clicked cell; run a terminal line (with a new generation). [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
7. The menu actions map to the same handlers, to toggling rails, focusing the terminal, resetting the view or the layout, reloading the page (`Restart UI`), and opening the shortcut list. The seed can be randomised below $2^{31} - 1$ and copied to the clipboard. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
8. A key handler on the window, ignored while typing in a field, implements the shortcut list: Space plays or pauses; A or . advances; 1, 2, 3 choose a layer; [ and ] change speed; N asks for a new world; ` or C focuses the terminal; D and P toggle the rails; R fits the view; ? opens the list; Esc closes a dialog. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).
9. The root renders the menu bar, the top bar (identity and online dot, transport, view toggles), the error banner, the left rail with the performance panel, the map canvas with its layer switch, the right rail with the world, inspect, and legend panels, the drag splitters, the terminal, and the two dialogs. `formatNs`, `formatBytes`, `formatDiag`, and `formatStepRate` format the rail, and `isTypingTarget` guards the keys. [`MapTool`](../../../../ui/web/src/components/MapTool.tsx).

## What is true afterwards

The page shows the host's last status within about 200 ms, and the raster of the last settled step whenever the host is not busy. No answer older than the latest picture-changing action is ever applied. While the host is busy, Play keeps asking and Advance is ignored, so a slow world plays at the rate it can step.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Root component | `MapTool` | `MapTool`, `readFlag`, `writeFlag`, `isTypingTarget`, `formatNs`, `formatBytes`, `formatDiag`, `formatStepRate`, `LAYERS`, `SPEEDS`, `PERF_ROWS` | [`ui/web/src/components/MapTool.tsx`](../../../../ui/web/src/components/MapTool.tsx) |
| App shell | `RootLayout` | `RootLayout`, `metadata` | [`ui/web/src/app/layout.tsx`](../../../../ui/web/src/app/layout.tsx) |
| Route | `HomePage` | `HomePage` | [`ui/web/src/app/page.tsx`](../../../../ui/web/src/app/page.tsx) |

Parent: [web front](README.md). The requests it makes: [client](client.md).
