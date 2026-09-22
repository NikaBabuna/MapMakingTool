<!--
  File: docs/protocol/flows/README.md
  Purpose: Index of bookkeeping algorithms, and how to read one
  Audience: Agents and humans
  Update when: A flow file is added
-->

# Flows

A flow is the bookkeeping for one situation. It is an algorithm: when this is true, edit these files, in this order, and stop. It is not an essay about the idea, and it is not the shape of the file. The shape is a blueprint. The permission to run the algorithm is [../environment/dispatch.md](../environment/dispatch.md).

Each algorithm below has the same parts:

| Part | What it tells you |
|------|-------------------|
| When | The observation that makes this algorithm legal. If the observation is false, do not run it |
| Before | What must already be true, or the algorithm will write a lie |
| Steps | The files, in order |
| Done | What the tree looks like when the algorithm has finished |
| Not done | The tempting stop that leaves the tree half-updated |

The witness command, when an algorithm says “run the witness,” is `./mvnw test` or `mvnw.cmd test`. It runs this Step’s checks and every earlier Accepted Step’s checks. A documentation Step does not add a program that searches documents for phrases.

| File | Algorithms | The situation, in one line |
|------|------------|----------------------------|
| [goals.md](goals.md) | Open goal, Amend goal, Close goal | A Goal is being created, changed, or finished |
| [steps.md](steps.md) | Store step, Implement, Sync, Close step, Amend step | A Step is being stored, done, recorded, finished, or its requirements changed |
| [structure.md](structure.md) | Restructure, Scope, Decide | Folders move, scope changes, or a decision must be kept |
| [source.md](source.md) | Record source | Source files changed, so the papers that describe them must change |
| [judgment.md](judgment.md) | Reconcile, Rollback | A chat is starting, or a Step is torn |
