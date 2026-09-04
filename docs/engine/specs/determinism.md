<!--
  File: docs/engine/specs/determinism.md
  Purpose: Determinism and convergence guarantees
  Audience: Agents implementing the engine
  Update when: Guarantees or conditions change
-->

# Determinism and convergence

> **Code status (through F-005):** Within-System order, across-System independence, and across-Step no-refill hold under tests. Same config + setup + N Steps → equal settled Pool; System registration order does not change independent field outcomes. Delete Request remains the open gap in the full guarantee.

## Within a System

Order can matter because Sub-Systems depend on each other. The framework computes one **global deterministic order** across the whole conflict set ([systems.md](systems.md)).

## Across Systems

Order cannot matter: no System reads another System's output in the same Step. The only leak would be merge order; each field type's resolver is a pure function of conflicting values and provenance ([merge-types.md](merge-types.md)).

## Across Steps

Nothing loops within a Step. Implied events become eligible only at the **next** Step. Consequence chains are sequences of fully settled Pool states — no same-Step recursion or fixed-point termination.

## Condition

Guarantee holds when every order-sensitive field has a pure resolver. **Delete Request** policy is not yet fixed ([open-questions.md](open-questions.md)) — the one open gap in the guarantee.
