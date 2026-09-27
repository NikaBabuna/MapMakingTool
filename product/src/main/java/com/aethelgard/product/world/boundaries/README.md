<!--
  File: product/src/main/java/com/aethelgard/product/world/boundaries/README.md
  Purpose: Door to the boundaries: finding every contact between two plates and classifying it by the plates' relative motion
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Boundaries

Finds every place where two plates touch and classifies each contact as separating, colliding, or passing by, from the two plates' velocities.

**Paper:** [Boundaries](../../../../../../../../../docs/architecture/world/boundaries.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

The contacts are the input of almost every later phase: budgets and intents, the geometry pass, orogeny, margins, collisions, and subduction all walk them. They are traced once per generation, in the first phase, and stored as one immutable list, so every later phase sees the same contacts. What a contact does to the plates or the crust belongs to the phase that acts on it, not here.

## How it works

1. The phase `BoundaryTracing` reads the settled plates and velocities and stages the result of `Boundaries.trace` under the boundaries field.
2. `Boundaries.trace` visits the cells row by row and, for each cell, looks one step east and one step south through `SphereTopology.neighbor`. Where the neighbour belongs to another plate, it records one contact.
3. `Boundaries.classify` gives the contact its `BoundaryKind` — `SEPARATE`, `COLLIDE`, or `PASS_BY` — from the closing speed that `Orogeny.closing` computes from the two velocities.
4. Each contact is a `BoundaryContact`: the cell, the step to the neighbour, the two plates, and the kind. The list is kept in a `Boundaries`, which answers `contacts`, `size`, and `count` of one kind.

Because the trace steps only east and south, contacts that cross a pole are not all found exactly once: those across the north pole are missed, and those across the south pole are found twice. The boundary test fails on this on purpose until the trace is fixed.

**Start reading at:** `Boundaries.trace` in [Boundaries.java](Boundaries.java).

## Depends on

- [fields/](../fields/README.md) — the plates grid, the velocities, and the field names
- [topology/](../topology/README.md) — the neighbour of a cell across the seam and the poles
- [crust/](../crust/README.md) — `Orogeny.closing`, the closing speed of two plates; the crust and boundary phases are peers ([conventions](../../../../../../../../../docs/architecture/conventions.md))
- [engine systems](../../../../../../../../../engine/src/main/java/com/aethelgard/engine/systems/README.md) — the `SubSystem` port the phase implements

## Used by

- [world/](../README.md) — the phase in the setup, and the contacts of step 0 and of the reference pipeline
- [interaction/](../interaction/README.md), [motion/](../motion/README.md), [crust/](../crust/README.md) — every later phase walks the contacts
- [session/](../../session/README.md) — the session's reads and the world dump
- [cli](../../../../../../../../../cli/README.md) — the command language lists the contacts
- [the boundary tests](../../../../../../../test/java/com/aethelgard/product/world/boundaries/README.md) — every contact found once and classified

## Where each step happens

### [Boundaries](../../../../../../../../../docs/architecture/world/boundaries.md)

| Step | Member | File |
|------|--------|------|
| 1. The phase reads the plates and velocities and stages the contacts | `BoundaryTracing.execute` | [BoundaryTracing.java](BoundaryTracing.java) |
| 2. Cells are visited row by row, each with its east and south step | `Boundaries.trace` | [Boundaries.java](Boundaries.java) |
| 3. A contact is classified from the closing speed | `Boundaries.classify` | [Boundaries.java](Boundaries.java) |
| 4. Each contact is stored with its direction | `BoundaryContact` | [BoundaryContact.java](BoundaryContact.java) |
| 5. The list answers its contacts, its size, and the count of one kind | `Boundaries.count` | [Boundaries.java](Boundaries.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [BoundaryTracing.java](BoundaryTracing.java) | The phase that retraces the contacts every generation | `BoundaryTracing`, `execute` |
| [Boundaries.java](Boundaries.java) | Traces, classifies, and holds the list of contacts | `Boundaries`, `trace`, `classify`, `contacts`, `count` |
| [BoundaryContact.java](BoundaryContact.java) | One contact: a cell, its step to the neighbour, the two plates, and the kind | `BoundaryContact`, `plateA`, `plateB`, `kind` |
| [BoundaryKind.java](BoundaryKind.java) | The three kinds of contact | `BoundaryKind`, `SEPARATE`, `COLLIDE`, `PASS_BY` |
