<!--
  File: docs/architecture/world/crust/ridge.md
  Purpose: RidgeCreate — phase 6: a new oceanic locker for every cell left without crust
  Audience: Agents and humans
  Update when: RidgeCreate.apply or RidgeCreate.execute changes
-->

# Ridge

Where plates part, or where crust was consumed or unshared, cells are left with no crust under them. Each such cell gets a brand-new column of thin ocean floor, as a mid-ocean ridge makes new sea floor.

## What it reads

The staged `occupancy` of the motion chapter and the staged `lockers` of the orogeny (the settled values when none were staged).

## What it writes

`occupancy` with no gap left, and `lockers` extended by one oceanic locker per gap. A field of the wrong type throws `IllegalStateException`; a miscount of new ids throws `IllegalStateException`.

## Model

Let $G = \{c : O(c) = \bot\}$ be the gaps, numbered $c_0, c_1, \dots$ in row-major order, and $L$ the number of lockers. Then

$$O'(c_i) = L + i, \qquad O'(c) = O(c) \;\; (c \notin G), \qquad T'(L + i) = T_{\mathrm{ocean}} \;\; (0 \le i < |G|),$$

and every other thickness is unchanged. With no gap, the phase stages its inputs unchanged.

`RidgeCreate.apply` (the mint) in [`RidgeCreate.java`](../../../../product/src/main/java/com/aethelgard/product/RidgeCreate.java):

```java
int nextId = lockers.count();
for (int y = 0; y < height; y++) {
  for (int x = 0; x < width; x++) {
    int id = occupancy.get(x, y);
    if (id == PlateKinematics.UNRESOLVED) {
      cells[y][x] = nextId++;
    } else {
      cells[y][x] = id;
    }
  }
}
```

## Procedure

1. `execute` reads the occupancy and lockers (staged first) and stages both parts of the `Result` of `apply`. [`RidgeCreate.execute`](../../../../product/src/main/java/com/aethelgard/product/RidgeCreate.java).
2. `apply` counts the gaps, and with none returns its inputs. [`RidgeCreate.apply`](../../../../product/src/main/java/com/aethelgard/product/RidgeCreate.java).
3. Otherwise it gives every gap, in row-major order, the next locker id, checks that it minted exactly one id per gap, and extends the table with `Lockers.appendOceanic`. [`RidgeCreate.apply`](../../../../product/src/main/java/com/aethelgard/product/RidgeCreate.java).

## What is true afterwards

Every cell has a locker (invariant I4 of [fields](../fields.md)), and every minted locker belongs to exactly one cell and is $T_{\mathrm{ocean}}$ thick, so a fresh ridge stands at height 0. The table only grows: lockers no cell points at any longer stay in it, so $L$ counts every column the world has ever had.

## Cost

$O(WH)$, plus $O(L)$ to extend the table.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Phase | `RidgeCreate` | `RidgeCreate.id`, `RidgeCreate.writeRanges`, `RidgeCreate.execute`, `RidgeCreate.apply`, `RidgeCreate.Result` | [`product/src/main/java/com/aethelgard/product/RidgeCreate.java`](../../../../product/src/main/java/com/aethelgard/product/RidgeCreate.java) |

Parent: [crust](README.md). Why rifts mint ocean rather than stretch the border: [ADR-013](../../../paperwork/decisions/ADR-013-crust-topology.md).
