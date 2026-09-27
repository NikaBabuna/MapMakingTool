<!--
  File: docs/architecture/world/motion/sink.md
  Purpose: Sink and claim — the entry of the geometry pass, and the stages that sink cells at collisions and refill them at rifts
  Audience: Agents and humans
  Update when: What the sink and claim stages compute, the ragged hashes, or the order of the stages changes
-->

# Sink and claim

Where plates collide, the losing plate's crust goes down: its cell at the contact empties. Where plates pull apart, each side reaches for an emptied cell beside it and takes it. A seeded hash skips about a quarter of all contacts and adds an extra bite at about an eighth, so fronts come out ragged rather than ruled.

## What it reads

As a phase: the settled `plates` and `occupancy`; the staged `plate_velocity`, `plate_registry`, `boundaries`, `area_flux`, and `lockers` (settled when not staged); and the heartbeat, as the generation index $g = h - 1$. As a pass: the plates, the contacts, the budgets, the registry, the velocities (whose seed $s$ drives the hashes), and, in the full form, the occupancy, the lockers, and a skip mask to fill.

## What it writes

The phase stages `plates`, `plate_registry`, `plate_velocity`, and `occupancy` after the whole pass ([advect](advect.md)). The pass returns the plates, registry, and velocities after [remap](fission.md), before advection. The stages on this page mark cells in the skip mask. A null argument throws `NullPointerException`, a field of the wrong type throws `IllegalStateException`, and a heartbeat beyond the range of `int` throws `ArithmeticException`.

## Model

The pass works on $C := P$, a mutable copy, with $\bot$ = −1 for an unowned cell, and on the skip mask $\mathrm{Skip} := \emptyset$. Two hashes of a cell, with $s$ the seed and the 64-bit constants $\phi_1 = \texttt{0x9E3779B97F4A7C15}$, $\phi_2 = \texttt{0xBF58476D1CE4E5B9}$, $\phi_3 = \texttt{0x94D049BB133111EB}$, $\phi_4 = \texttt{0x2545F4914F6CDD1D}$:

$$z = s \oplus x\phi_1 \oplus y\phi_2,\quad \mathrm{skip}(x, y) \iff \bigl(z \oplus (z \gg 30)\bigr) \bmod 4 = 0; \qquad z' = s \oplus x\phi_3 \oplus y\phi_4,\quad \mathrm{extra}(x, y) \iff \bigl(z' \oplus (z' \gg 27)\bigr) \bmod 8 = 0 .$$

**Collide.** For each contact $\xi = (c, n, a, b, \textsf{COLLIDE})$ of $K$, in order, with $\mathrm{skip}(c)$ false and a loser $\lambda = \lambda(\xi) \neq \textsf{NONE}$ ([precedence](../crust/precedence.md)): the loser's contact cell is $\ell = c$ if $\lambda = a$, else $\ell = \nu(c, n)$, and

$$C(\ell) = \lambda \;\Longrightarrow\; C(\ell) := \bot,\;\; \mathrm{Skip} := \mathrm{Skip} \cup \{\ell\},\;\; \text{and if } \mathrm{extra}(\ell):\;\; C(\ell') := \bot,\;\; \mathrm{Skip} := \mathrm{Skip} \cup \{\ell'\}$$

for the first $\ell' = \nu(\ell, d)$, $d \in D_4$, with $C(\ell') = \lambda$. A cell the loser no longer holds is left alone.

**Separate.** For each contact $\xi = (c, n, a, b, \textsf{SEPARATE})$ with $\mathrm{skip}(c)$ false, plate $a$ claims at $c$ and plate $b$ claims at $\nu(c, n)$, where

$$\mathrm{claim}(o, p):\;\; \text{the first of } o, \nu(o, D_4) \text{ with } C = \bot \text{ becomes } p \text{ and joins } \mathrm{Skip};$$

and if $\mathrm{extra}(c)$, each side also nibbles:

$$\mathrm{nibble}(o, p):\;\; \text{the first of } \nu(o, D_4) \text{ with } C \ge 0,\; C \ne p \text{ becomes } p \text{ and joins } \mathrm{Skip} .$$

A rift opens no hole of its own: it fills holes that collisions opened next to it, and its nibble takes a cell from a neighbouring plate.

## Procedure

1. The phase reads its inputs as listed, computes $g$ from the heartbeat, creates the skip mask, and runs the full pass.
2. The pass copies the plates, takes the seed from the velocities, and runs the stages in order: collide, separate, [flood](flood.md), [fission and crumbs](fission.md), [remap](fission.md). A shorter form passes no crust and no mask, so it uses the area-only loser.
3. The collide stage walks the collide contacts, skips those the skip hash selects and those without a loser, sinks the loser's contact cell if the loser still holds it, and nibbles one more loser cell when the extra hash selects the sunken cell.
4. The separate stage walks the rift contacts, skips those the skip hash selects, lets each side claim a sunken cell at or beside its contact cell, and, when the extra hash selects the contact cell, lets each side take one neighbouring cell of another plate.
5. Every cell a stage changes is marked in the skip mask, when a mask was given.

## What is true afterwards

Sunken cells remain only where a collision sank a cell and no rift claimed it; the flood fills them next. Every cell whose owner changed on this page is in $\mathrm{Skip}$, so its old crust key will not be carried to its destination. Whether a contact is skipped or bites twice depends only on the seed and the cell, so the same world always yields the same fronts. The budgets `area_flux` are read and checked, and play no part.

## Cost

$O(|K|)$ for both stages.

Code: [world/motion/](../../../../product/src/main/java/com/aethelgard/product/world/motion/README.md)  
Parent: [motion](README.md).
