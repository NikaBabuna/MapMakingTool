<!--
  File: docs/architecture/session/dump.md
  Purpose: WorldDump — the canonical text of a settled world
  Audience: Agents and humans
  Update when: WorldDump.format or WorldDump.of changes
-->

# Dump

A settled world can be printed as plain text: a header line, then every grid row by row, then every table entry by entry, always in the same order and the same layout. Two equal worlds print the same text character for character, so a stored text can prove that a world has not changed.

## What it reads

`of(engine, spec)`: the nine settled fields and the step index of an engine, and the spec that sized it. The shorter `format` forms take fewer fields and compute the rest (see Procedure).

## What it writes

A string, lines ended by a newline, including the last. Refusals: a null part throws `NullPointerException`; a negative step count, or a grid whose size differs from the spec, throws `IllegalArgumentException`.

## Model

In extended Backus–Naur form, with $W, H$ from the spec, $L$ lockers, $N$ plates, and $|K|$ contacts:

```ebnf
dump     = header,
           "elevation:", nl, grid,  "plates:", nl, grid,  "occupancy:", nl, grid,
           "lockers:", nl, L * (int, nl),
           "plate_velocity:", nl, N * (int, " ", int, nl),
           "plate_registry:", nl, N * (int, " ", int, " ", int, nl),
           "boundaries:", nl, |K| * (int, 5 * (" ", int), " ", kind, nl),
           "area_flux:", nl, int, nl, N * (int, nl),
           "motion_intent:", nl, N * (int, " ", int, nl) ;
header   = "world w=", W, " h=", H, " seed=", seed, " steps=", k, nl ;
grid     = H * (int, (W - 1) * (" ", int), nl) ;
kind     = "SEPARATE" | "COLLIDE" | "PASS_BY" ;
nl       = "\n" ;
```

The lines hold, in order: the elevation, plate, and occupancy grids row by row; the locker thicknesses; each velocity $(v^x, v^y)$; each registry row $(A, v^x, v^y)$; each contact $(x, y, n_x, n_y, a, b, \kappa)$; the sink budget, then each plate budget; each intent $(\iota^x, \iota^y)$. Every cell and every table entry of the nine fields appears, so two worlds that differ in any of them print different text. The seed shown is the spec's.

`WorldDump.format` (the header) in [`WorldDump.java`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java):

```java
out.append("world w=")
    .append(spec.width())
    .append(" h=")
    .append(spec.height())
    .append(" seed=")
    .append(spec.seed())
    .append(" steps=")
    .append(steps)
    .append('\n');
```

## Procedure

1. `of` reads the nine settled fields and the step index, and calls the full `format`. [`WorldDump.of`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java).
2. The full `format` checks every part and each grid's size, then writes the header, the three grids through `appendGrid`, and the tables in the order of the Model. [`WorldDump.format`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java), [`WorldDump.appendGrid`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java).
3. The shorter `format` forms fill in what they are not given. Velocities come from the seed. The registry and contacts are counted and traced from the plates. The budgets and intents use the area-only loser. The occupancy and lockers are the step-0 keys and an all-oceanic table. [`WorldDump.format`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java).
4. `requireGeometry` rejects a grid of another size than the spec. [`WorldDump.requireGeometry`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java).

## What is true afterwards

The same settled fields always give the same text, and a change in any cell or table entry changes the text. `CANONICAL_STEPS` (3) is the number of steps after which the $8 \times 8$, seed-0 world is kept as the stored canonical dump. Printing is read-only: it changes no field and no diagnostic.

## Cost

$O(3WH + L + |K| + N)$ characters.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Dump | `WorldDump` | `CANONICAL_STEPS`, `of`, `format`, `appendGrid`, `requireGeometry` | [`product/src/main/java/com/aethelgard/product/WorldDump.java`](../../../product/src/main/java/com/aethelgard/product/WorldDump.java) |

Parent: [session](README.md). The fields it prints: [fields](../world/fields.md).
