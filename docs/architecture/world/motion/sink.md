<!--
  File: docs/architecture/world/motion/sink.md
  Purpose: ApplyGeometry.execute, ApplyGeometry.apply, applyCollide, applySeparate — the entry of the geometry pass, and the stages that sink cells at collisions and refill them at rifts
  Audience: Agents and humans
  Update when: ApplyGeometry.execute, ApplyGeometry.apply, applyCollide, applySeparate, claimSinkNear, nibbleSink, nibbleClaim, raggedSkip, or raggedExtra changes
-->

# Sink and claim

Where plates collide, the losing plate's crust goes down: its cell at the contact empties. Where plates pull apart, each side reaches for an emptied cell beside it and takes it. A seeded hash skips about a quarter of all contacts and adds an extra bite at about an eighth, so fronts come out ragged rather than ruled.

## What it reads

In `execute`: the settled `plates` and `occupancy`; the staged `plate_velocity`, `plate_registry`, `boundaries`, `area_flux`, and `lockers` (settled when not staged); and the heartbeat, as the generation index $g = h - 1$. In `apply`: the plates, the contacts, the budgets, the registry, the velocities (whose seed $s$ drives the hashes), and, in the full form, the occupancy, the lockers, and a skip mask to fill.

## What it writes

`execute` stages `plates`, `plate_registry`, `plate_velocity`, and `occupancy` after the whole pass ([advect](advect.md)). `apply` returns a `Result`: the plates, registry, and velocities after [remap](fission.md), before advection. The stages on this page mark cells in the skip mask. A null argument throws `NullPointerException`, a field of the wrong type throws `IllegalStateException`, and a heartbeat beyond the range of `int` throws `ArithmeticException`.

## Model

The pass works on $C := P$, a mutable copy, with $\bot$ = `SINK` = −1 for an unowned cell, and on the skip mask $\mathrm{Skip} := \emptyset$. Two hashes of a cell, with $s$ the seed and the 64-bit constants $\phi_1 = \texttt{0x9E3779B97F4A7C15}$, $\phi_2 = \texttt{0xBF58476D1CE4E5B9}$, $\phi_3 = \texttt{0x94D049BB133111EB}$, $\phi_4 = \texttt{0x2545F4914F6CDD1D}$:

$$z = s \oplus x\phi_1 \oplus y\phi_2,\quad \mathrm{skip}(x, y) \iff \bigl(z \oplus (z \gg 30)\bigr) \bmod 4 = 0; \qquad z' = s \oplus x\phi_3 \oplus y\phi_4,\quad \mathrm{extra}(x, y) \iff \bigl(z' \oplus (z' \gg 27)\bigr) \bmod 8 = 0 .$$

`ApplyGeometry.raggedSkip` in [`ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java):

```java
long z = seed ^ (((long) x) * 0x9E3779B97F4A7C15L) ^ (((long) y) * 0xBF58476D1CE4E5B9L);
z ^= z >>> 30;
return (z & 3L) == 0L; // ~25% of contact cells skipped
```

**Collide.** For each contact $\xi = (c, n, a, b, \textsf{COLLIDE})$ of $K$, in order, with $\mathrm{skip}(c)$ false and a loser $\lambda = \lambda(\xi) \neq \textsf{NONE}$ ([precedence](../crust/precedence.md)): the loser's contact cell is $\ell = c$ if $\lambda = a$, else $\ell = \nu(c, n)$, and

$$C(\ell) = \lambda \;\Longrightarrow\; C(\ell) := \bot,\;\; \mathrm{Skip} := \mathrm{Skip} \cup \{\ell\},\;\; \text{and if } \mathrm{extra}(\ell):\;\; C(\ell') := \bot,\;\; \mathrm{Skip} := \mathrm{Skip} \cup \{\ell'\}$$

for the first $\ell' = \nu(\ell, d)$, $d \in D_4$, with $C(\ell') = \lambda$. A cell the loser no longer holds is left alone.

`ApplyGeometry.applyCollide` (the sink) in [`ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java):

```java
if (cells[y][x] == lose) {
  cells[y][x] = SINK;
  markSkip(skipOccupancyExport, x, y);
  if (raggedExtra(seed, x, y)) {
    nibbleSink(cells, width, height, x, y, lose, skipOccupancyExport);
  }
}
```

**Separate.** For each contact $\xi = (c, n, a, b, \textsf{SEPARATE})$ with $\mathrm{skip}(c)$ false, plate $a$ claims at $c$ and plate $b$ claims at $\nu(c, n)$, where

$$\mathrm{claim}(o, p):\;\; \text{the first of } o, \nu(o, D_4) \text{ with } C = \bot \text{ becomes } p \text{ and joins } \mathrm{Skip};$$

and if $\mathrm{extra}(c)$, each side also nibbles:

$$\mathrm{nibble}(o, p):\;\; \text{the first of } \nu(o, D_4) \text{ with } C \ge 0,\; C \ne p \text{ becomes } p \text{ and joins } \mathrm{Skip} .$$

A rift opens no hole of its own: it fills holes that collisions opened next to it, and its nibble takes a cell from a neighbouring plate.

## Procedure

1. `execute` reads its inputs as listed, computes $g$ from the heartbeat, creates the skip mask, and calls the full `apply`. [`ApplyGeometry.execute`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
2. `apply` copies the plates, takes the seed from the velocities, and runs the stages in order: collide, separate, [flood](flood.md), [fission and crumbs](fission.md), [remap](fission.md). The five-argument form passes no crust and no mask, so it uses the area-only loser. [`ApplyGeometry.apply`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
3. The collide stage walks the collide contacts, skips those that `raggedSkip` selects and those without a loser, sinks the loser's contact cell if the loser still holds it, and nibbles one more loser cell when `raggedExtra` selects the sunken cell. [`ApplyGeometry.applyCollide`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java), [`ApplyGeometry.nibbleSink`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
4. The separate stage walks the rift contacts, skips those that `raggedSkip` selects, lets each side claim a sunken cell at or beside its contact cell, and, when `raggedExtra` selects the contact cell, lets each side take one neighbouring cell of another plate. [`ApplyGeometry.applySeparate`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java), [`ApplyGeometry.claimSinkNear`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java), [`ApplyGeometry.nibbleClaim`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).
5. Every cell a stage changes is marked in the skip mask by `markSkip`, when a mask was given. [`ApplyGeometry.markSkip`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java).

## What is true afterwards

Sunken cells remain only where a collision sank a cell and no rift claimed it; the flood fills them next. Every cell whose owner changed on this page is in $\mathrm{Skip}$, so its old crust key will not be carried to its destination. Whether a contact is skipped or bites twice depends only on the seed and the cell, so the same world always yields the same fronts. The budgets `area_flux` are read and checked, and play no part.

## Cost

$O(|K|)$ for both stages.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Geometry pass, sink and claim | `ApplyGeometry` | `ApplyGeometry.id`, `ApplyGeometry.writeRanges`, `ApplyGeometry.execute`, `apply`, `Result`, `SINK`, `raggedSkip`, `raggedExtra`, `applyCollide`, `applySeparate`, `claimSinkNear`, `nibbleSink`, `nibbleClaim`, `markSkip`, `copyCells`, `readField` | [`product/src/main/java/com/aethelgard/product/ApplyGeometry.java`](../../../../product/src/main/java/com/aethelgard/product/ApplyGeometry.java) |

Parent: [motion](README.md).
