<!--
  File: docs/protocol/blueprints/architecture.md
  Purpose: Shape of an implementation paper
  Audience: Agents and humans
  Update when: The paper shape changes
-->

# Implementation paper

Three files use this shape.

| File | What it covers |
|------|----------------|
| `docs/architecture.md` | The roll-up. Modules, the direction of dependencies, and pointers. Short on purpose |
| `docs/engine/architecture.md` | The framework: what the host guarantees, and where the code lives |
| `docs/product/architecture.md` | The product program: how a world step is assembled, and how the studio talks to it |

A paper is what an agent reads instead of every source file. It is written as an explanation: what the piece is for, what it reads, what it writes, and what is true afterwards. A stack of “F-056 did this, F-058 did that” is a changelog wearing the paper’s name. The changelog already exists.

**Write or edit it when.** **Record source** runs for that module, or a structural decision changes the layout.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| What this module is | A paragraph, before any table |
| Layout | Paths and the dependency rule, so a new file has an obvious home |
| How the pieces work together | The order of a step, or the list of ports, with a sentence each. This is the part that must stay readable as the system grows. When it gets too long, split a chapter under `docs/architecture/<area>/<chapter>/` rather than appending forever. That fourth level is allowed only on the architecture shelf |
| Pointers | To the spec, the glossary, and the ADR. One line each |

## Skeleton

```markdown
# <Module> architecture

<What this module is for.>

## Layout

| Piece | Path | Role |
|-------|------|------|
| <piece> | `<path>` | <role> |

## How a step is assembled

1. <piece> — <what it reads, what it writes, what is true after>

See ADR-0xx. Words: <glossary>. Topic detail: <spec>.
```

## Keep out

A status banner that replaces the explanation. Requirement tables. Protocol law.
