<!--
  File: docs/architecture/studio/web/terminal.md
  Purpose: Terminal — the always-open command line of the page: running a line, the transcript, and the history
  Audience: Agents and humans
  Update when: Terminal's run, history, transcript caps, or key handling changes
-->

# Terminal

At the bottom of the page is a command line that speaks the same language as the command-line program, on the same world the map shows. It keeps a short transcript of lines and answers, and a history of recent lines that the arrow keys walk through.

## What it reads

The typed line, Enter and the arrow keys, the Run and Clear buttons, and the answers of the root's `onRun`, which posts the line to the host ([tool](tool.md), [language](../../cli/language.md)).

## What it writes

The transcript and the history, both held only in the page; a call of `onRun` per line. Clearing affects only the transcript on screen, not the world.

## Model

The history $\mathit{Hist} = (h_0, h_1, \dots)$ lists lines newest first, with $|\mathit{Hist}| \le 32$; a run of line $\ell$ sets

$$\mathit{Hist} := \begin{cases} \mathit{Hist} & h_0 = \ell \\ (\ell, h_0, h_1, \dots)_{[0, 32)} & \text{otherwise,} \end{cases}$$

so an immediate repeat is not stored twice. The transcript $\mathit{Log}$ keeps the last 40 entries; each run appends two, the line as `aethelgard> <line>` and the answer, so the last 20 runs stay visible. A failed line's answer is marked as an error and ends with `(exit <code>)` unless it already names that code; an empty answer shows `(exit <code>)`; a request that throws shows `error: <message>`. The history cursor $i \in \{-1, 0, \dots, |\mathit{Hist}| - 1\}$ moves with the arrows:

$$\uparrow:\; i := \min(i + 1,\; |\mathit{Hist}| - 1); \qquad \downarrow:\; i := \begin{cases} -1 \text{ and the line is cleared} & i \le 0 \\ i - 1 & \text{otherwise.} \end{cases}$$

`Terminal` (the history) in [`Terminal.tsx`](../../../../ui/web/src/components/Terminal.tsx):

```tsx
setHistory((prev) => {
  if (prev[0] === trimmed) {
    return prev;
  }
  return [trimmed, ...prev].slice(0, HISTORY_CAP);
});
```

## Procedure

1. Enter or Run trims the line, ignores a blank one, awaits `onRun`, appends the two transcript entries, stores the line in the history, and clears the input; a thrown error appends the line and `error: <message>` instead. [`Terminal`](../../../../ui/web/src/components/Terminal.tsx).
2. The arrow keys walk the history as in the Model; typing resets the cursor. [`Terminal`](../../../../ui/web/src/components/Terminal.tsx).
3. The transcript scrolls to its end after every change. When it is empty it shows `EMPTY_HINT`, a one-line reminder of the language. [`Terminal`](../../../../ui/web/src/components/Terminal.tsx).
4. The component exposes a `TerminalHandle` whose `focus` puts the cursor in the input, which the root uses for the ` and C keys; entries are `TerminalEntry` values of kind command, output, or error. [`TerminalHandle`](../../../../ui/web/src/components/Terminal.tsx).

## What is true afterwards

Every line reaches the same dispatcher the command-line program uses, on the page's own world, and the map is refreshed from the answer ([tool](tool.md)). The history and transcript are lost when the page reloads.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Terminal | `Terminal` | `Terminal`, `TerminalEntry`, `TerminalHandle`, `HISTORY_CAP`, `LOG_CAP`, `EMPTY_HINT` | [`ui/web/src/components/Terminal.tsx`](../../../../ui/web/src/components/Terminal.tsx) |

Parent: [web front](README.md).
