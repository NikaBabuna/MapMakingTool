<!--
  File: docs/protocol/blueprints/doors/goal-pointer.md
  Purpose: Shape of the Goal pointer carried by the five doors, and the operations that set and clear it
  Audience: Agents
  Update when: The pointer format, or the set of doors that carry it, changes
-->

# Goal pointer

**Shapes:** the Goal pointer text on exactly five doors, at the places listed under **Where it appears**.  
**Register:** legal · **Human-facing:** no  
**Header:** none — this is a fragment of other files  
**Neighbours:** the Active Goal line ([../paperwork/goal-index.md](../paperwork/goal-index.md)) — the single authoritative sentence. The pointer only names ids beside a link to it.

A door may name the Active Goal's id so a reader knows which Goal is current before opening the index ([../../environment/core-definition.md](../../environment/core-definition.md) 1.3). It must never carry the `**Active Goal:**` sentence itself.

## Skeleton

The pointer is one of these three texts:

| Situation | Pointer text |
|-----------|--------------|
| A Goal is active, and at least one Goal is done | `G-0xx <name> · last completed G-0yy` |
| No Goal is active | `none · last completed G-0yy` |
| A Goal is active, and no Goal is done yet | `G-0xx <name> · last completed none` |

`G-0xx <name>` is exactly the id and name on the Active Goal line. `G-0yy` is exactly the id in its "Last completed" link.

## Where it appears

| Door | Line that carries it |
|------|----------------------|
| `AGENTS.md` | `**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <pointer>` |
| `.cursor/rules/protocol.mdc` | `**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <pointer>. Do not copy the Active Goal line; it lives only on that index.` |
| `README.md` (repository root) | The line `**Goal index:** [docs/paperwork/goals.md](docs/paperwork/goals.md) — <pointer>`, and the Documentation table row `\| [Goals index](docs/paperwork/goals.md) \| <pointer> \|` |
| `docs/README.md` | `**Goal index:** [paperwork/goals.md](paperwork/goals.md) — <pointer>` |
| `docs/navigation.md` | `**Goal index:** [paperwork/goals.md](paperwork/goals.md) — <pointer>` |

## Parts

| Part | Required | Rule |
|------|----------|------|
| Link to the Goal index | yes | Exactly as in **Where it appears**, relative to the door |
| ` — ` | yes | A space, an em dash, a space |
| Pointer text | yes | One of the three texts in the Skeleton. Nothing else |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Set pointer** | [../../flows/goal.md](../../flows/goal.md) Open goal (W7), Amend goal (W7) |
| **Clear pointer** | [../../flows/goal.md](../../flows/goal.md) Close goal (W7), Abandon goal (W7) |
| **Correct pointer** | [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 7.4 |

### Set pointer

**Before.** The Active Goal line in `docs/paperwork/goals.md` names the Goal to point at.

**Edit.** On each door, at each place in **Where it appears**:

1. Replace everything after ` — ` up to the end of the pointer text (for the editor rule, up to `. Do not copy`) with `G-0xx <name> · last completed <G-0yy | none>`.

**Result.** All six places (five doors, two places on the root README) carry the same pointer text.

### Clear pointer

**Before.** The Active Goal line says `none`.

**Edit.** At each place in **Where it appears**:

1. Replace the pointer text with `none · last completed G-0yy`, where `G-0yy` is the Goal in the "Last completed" link of the Active Goal line.

**Result.** All six places say `none`.

### Correct pointer

**Before.** A place carries pointer text that differs from the Active Goal line.

**Edit.**

1. Apply **Set pointer** or **Clear pointer** to that place only, whichever matches the Active Goal line.

**Result.** The place agrees with the index.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every place in **Where it appears** carries pointer text in one of the three forms |
| 2 | Every place names the same ids as the Active Goal line |
| 3 | None of the five doors contains the string `**Active Goal:**` |

## Keep out

- The `**Active Goal:**` sentence: only `docs/paperwork/goals.md`.  
- Goal status words, progress, or Step ids: the Goal file.
