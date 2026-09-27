<!--
  File: docs/architecture/session/dump.md
  Purpose: Dump — the canonical text of a settled world
  Audience: Agents and humans
  Update when: The text of a dump, or how it is built, changes
-->

# Dump

A settled world can be printed as plain text: a header line, then every grid row by row, then every table entry by entry, always in the same order and the same layout. Two equal worlds print the same text character for character, so a stored text can prove that a world has not changed.

## What it reads

From an engine: its nine settled fields and its step index, and the spec that sized it. Shorter forms take fewer fields and compute the rest (see Procedure).

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

## Procedure

1. The nine settled fields and the step index are read from the engine, and the full form is written.
2. The full form checks every part and each grid's size, then writes the header, the three grids row by row, and the tables in the order of the Model.
3. The shorter forms fill in what they are not given. Velocities come from the seed. The registry and contacts are counted and traced from the plates. The budgets and intents use the area-only loser. The occupancy and lockers are the step-0 keys and an all-oceanic table.
4. A grid of another size than the spec is refused.

## What is true afterwards

The same settled fields always give the same text, and a change in any cell or table entry changes the text. The canonical step count, 3, is the number of steps after which the $8 \times 8$, seed-0 world is kept as the stored canonical dump. Printing is read-only: it changes no field and no diagnostic.

## Cost

$O(3WH + L + |K| + N)$ characters.

Code: [session/](../../../product/src/main/java/com/aethelgard/product/session/README.md)  
Parent: [session](README.md). The fields it prints: [fields](../world/fields.md).
