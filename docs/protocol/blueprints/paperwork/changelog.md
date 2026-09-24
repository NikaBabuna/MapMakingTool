<!--
  File: docs/protocol/blueprints/paperwork/changelog.md
  Purpose: Shape of the changelog (docs/paperwork/changelog.md), every line kind it may hold, and the operations that add lines
  Audience: Agents
  Update when: A line kind, a section, or an operation changes
-->

# Changelog

**Shapes:** `docs/paperwork/changelog.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** git history — every commit. The changelog holds only structure, phase, scope, and Goal events. A decision record ([decision-record.md](decision-record.md)) — why a choice was made.

The index of events a reader of the tree cares about: what moved, what the scope became, which Goal opened or closed, and when.

## Skeleton

```markdown
<!--
  File: docs/paperwork/changelog.md
  Purpose: High-signal structure, phase, and scope history
  Audience: Humans and agents
  Update when: Structure, phase, or scope shifts
-->

# Changelog

Not every commit — only structure, phase, and scope shifts.

---

## Structure

- <line, newest first>
…
```

A `## Scope` or `## Phase` section may follow `## Structure`, separated by `---`, when such lines exist. No other section is created.

## Line kinds

Every line is exactly one of these. Newest lines are at the top of their section.

| Kind | Section | Line |
|------|---------|------|
| Step structure change | Structure | `- **YYYY-MM-DD** — **F-0xx:** <what moved, from where to where>. <why a reader should care>.` Add ` ADR-0xx.` at the end when a decision record explains it |
| Scope change | Scope, or Structure if there is no Scope section | `- **YYYY-MM-DD** — **F-0xx:** scope — <in \| out>: <the approved sentence>.` |
| Phase change | Phase, or Structure if there is no Phase section | `- **YYYY-MM-DD** — **F-0xx:** phase changed from <old> to <new>.` |
| Goal opened | Structure | `- **YYYY-MM-DD** — **G-0xx open:** <name> approved (<one clause of direction>). No behaviour change.` |
| Goal closed | Structure | `- **YYYY-MM-DD** — **F-0xx / G-0xx close:** <what the claims now witness, in one sentence>. <Active Goal none \| next Goal G-0yy>; last completed G-0xx.` |
| Goal abandoned | Structure | `- **YYYY-MM-DD** — **G-0xx abandoned:** <reason>. Steps done: <n> of <m>.` |

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening sentence | yes | `Not every commit — only structure, phase, and scope shifts.` |
| `## Structure` | yes | Lines of the kinds above, newest first |
| `## Scope`, `## Phase` | no | Only when they hold lines |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add line** | [../../flows/step.md](../../flows/step.md) SYNC (Changelog); [../../flows/amendment.md](../../flows/amendment.md) Steps 6.5 and 7.3; [../../flows/goal.md](../../flows/goal.md) Open goal, Close goal, Abandon goal (W6) |
| **Insert missing line** | [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 14 |

### Add line

**Edit.**

1. Choose the line kind from **Line kinds**, and its section.  
2. Insert the line directly below that section's heading and its blank line, above every older line.

### Insert missing line

**Before.** A Step record ticks the Changelog box, or a Goal opened or closed, and no line names it.

**Edit.**

1. Write the line in its kind's form, dated with the date of the event (the Step's Accept commit date, or the Goal's `**Approved:**` date).  
2. Insert it in its section at the position its date requires, keeping newest first.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every line matches one line kind |
| 2 | Within each section, dates never increase downwards |
| 3 | Every Step whose record ticks the Changelog box, and every opened or closed Goal, has its line |

## Keep out

- A description of an algorithm: the flow or the architecture page.  
- The Active Goal line.  
- A line whose only content is "updated docs".
