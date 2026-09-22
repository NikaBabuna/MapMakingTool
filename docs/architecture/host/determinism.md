<!--
  File: docs/architecture/host/determinism.md
  Purpose: What is guaranteed to repeat, and the one open gap
  Audience: Agents and humans
  Update when: A resolver stops being a pure function, or Delete Request is decided
-->

# Determinism

The same config, the same `EngineSetup`, and the same number of steps settle the same Pool. Registration order of independent systems does not change a field whose resolver is a pure function of the conflicting set.

## Within a system

Sub-systems can read each other's staging, so order can matter. Overlapping write-ranges get one order from the conflict resolver, for the whole conflict set, not pairwise. Disjoint ranges do not share fields. That order is [systems](systems.md).

## Across systems

A system cannot read another system's output in the same step. Every claiming system sees one snapshot. The only place order could leak is merge. `STATIC` and `DESTRUCTIVE` pick the lexicographically smallest system id. `INCREMENT` sums. `CONSTANT` keeps standing. None of those reads list order. The rules are [merge](merge.md).

## Across steps

The event buffer is filled once, during compute, and cleared after merge. A system cannot enqueue an event that another system claims in the same step. A consequence chain is a sequence of settled snapshots.

## Condition

The guarantee holds while every order-sensitive field has a pure resolver. Delete Request has no resolver. That is the open gap, [question 4](open-questions.md).

Parent: [one engine step](README.md).
