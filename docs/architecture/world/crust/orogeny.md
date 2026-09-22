<!--
  File: docs/architecture/world/crust/orogeny.md
  Purpose: Orogeny locker stamps from standing contacts
  Audience: Agents and humans
  Update when: Orogeny.applyToLockers changes
-->

# Orogeny

`Orogeny` is the first crust writer in the pipeline. It stamps thickness from the contacts that were traced before plates moved. The stamps ride later because they sit on locker ids, and occupancy carries those ids. This phase runs after apply in the sub-system list, and it reads standing occupancy from the Pool, so the stamps land on the pre-move keys.

## What it reads

Standing `occupancy`, standing `lockers`, staged or pool `boundaries`, standing `plate_registry`.

## What it writes

`lockers`.

## Procedure

`Orogeny.closing` is `n · (vA − vB)`, the same quantity trace uses to classify.

Each contact cell gets a rank. Higher rank replaces lower rank. A cell keeps one rank.

| Rank | Value | Thickness delta |
|------|-------|-----------------|
| Win | 3 | `+1` |
| Lose | 2 | `−1` |
| Separate | 1 | `−1` |
| None, including pass-by | 0 | `0` |

On a `COLLIDE`, the winner's cell is Win and the loser's cell is Lose, using [precedence](precedence.md). On a `SEPARATE`, both cells are Separate. The walk is one pass over the contacts.

`applyToLockers` adds that delta to the locker id stored in standing occupancy at the contact cell. There is no floor. Thickness may go negative. The cap is not applied here.

## What is true afterwards

Lockers at standing contact cells have moved by at most 1. Cells that were not a contact are unchanged. `elevation` is still the previous generation's grid. [Isostasy](isostasy.md) reads thickness later.

## Where it lives

`Orogeny` in `product/src/main/java/com/aethelgard/product/Orogeny.java`.

Parent: [crust](README.md).
