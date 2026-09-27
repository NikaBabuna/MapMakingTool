<!--
  File: docs/architecture/studio/web/terminal.md
  Purpose: Terminal — the always-open command line of the page: running a line, the transcript, and the history
  Audience: Agents and humans
  Update when: How the terminal runs a line, its history or transcript caps, or its key handling changes
-->

# Terminal

At the bottom of the page is a command line that speaks the same language as the command-line program, on the same world the map shows. It keeps a short transcript of lines and answers, and a history of recent lines that the arrow keys walk through.

## What it reads

The typed line, Enter and the arrow keys, the Run and Clear buttons, and the answers of the root's run action, which posts the line to the host ([tool](tool.md), [language](../../cli/language.md)).

## What it writes

The transcript and the history, both held only in the page; one call of the root's run action per line. Clearing affects only the transcript on screen, not the world.

## Model

The history $\mathit{Hist} = (h_0, h_1, \dots)$ lists lines newest first, with $|\mathit{Hist}| \le 32$; a run of line $\ell$ sets

$$\mathit{Hist} := \begin{cases} \mathit{Hist} & h_0 = \ell \\ (\ell, h_0, h_1, \dots)_{[0, 32)} & \text{otherwise,} \end{cases}$$

so an immediate repeat is not stored twice. The transcript $\mathit{Log}$ keeps the last 40 entries; each run appends two, the line as `aethelgard> <line>` and the answer, so the last 20 runs stay visible. A failed line's answer is marked as an error and ends with `(exit <code>)` unless it already names that code; an empty answer shows `(exit <code>)`; a request that throws shows `error: <message>`. The history cursor $i \in \{-1, 0, \dots, |\mathit{Hist}| - 1\}$ moves with the arrows:

$$\uparrow:\; i := \min(i + 1,\; |\mathit{Hist}| - 1); \qquad \downarrow:\; i := \begin{cases} -1 \text{ and the line is cleared} & i \le 0 \\ i - 1 & \text{otherwise.} \end{cases}$$

## Procedure

1. Enter or Run trims the line, ignores a blank one, awaits the root's run action, appends the two transcript entries, stores the line in the history, and clears the input; a thrown error appends the line and `error: <message>` instead.
2. The arrow keys walk the history as in the Model; typing resets the cursor.
3. The transcript scrolls to its end after every change. When it is empty it shows a one-line reminder of the language.
4. The terminal lets the root put the cursor in its input, which the root does for the ` and C keys. Its entries are of three kinds: command, output, and error.

## What is true afterwards

Every line reaches the same dispatcher the command-line program uses, on the page's own world, and the map is refreshed from the answer ([tool](tool.md)). The history and transcript are lost when the page reloads.

Code: [components/](../../../../ui/web/src/components/README.md)
Parent: [web front](README.md).
