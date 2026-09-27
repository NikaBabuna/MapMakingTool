<!--
  File: engine/src/main/java/com/aethelgard/engine/merge/README.md
  Purpose: Door to the merge: the field schema, the writes systems make in a step, and the rule per field that settles them
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Merge

Settles the writes of a step: the schema that gives every field its merge rule, the buffer of writes each carrying the id of the system that made it, and the merge that applies each field's rule to the standing value and the writes.

**Paper:** [Merge](../../../../../../../../docs/architecture/engine/merge.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Several systems may write the same field in one step, and the result must not depend on who ran first. Keeping the rules and the merge in one package that depends on nothing else lets a product add its own rule without touching the engine. How the writes are produced is `systems/`; where the merged fields are stored is the Pool in `pool/`.

## How it works

1. A `FieldSchema` maps each field name to a `FieldMergeType`; the built-in rules are the `FieldType` constants, and a product may supply its own rule.
2. While systems run, the engine appends a `ProvenancedWrite` (the system's id and the value) for each written field to the step's `StepOutputBuffer`.
3. `TypedMerge.merge` copies the standing fields and, for every written field, asks that field's rule to settle the standing value and its writes; the result is an immutable map the engine stores in the Pool.

**Start reading at:** `TypedMerge.merge` in [TypedMerge.java](TypedMerge.java).

## Depends on

- nothing in this repository

## Used by

- [pool/](../pool/README.md) — the engine holds the schema and the output buffer, and merges after the barrier
- [product world/](../../../../../../../../product/src/main/java/com/aethelgard/product/world/README.md) — the product's schema
- [product session/](../../../../../../../../product/src/main/java/com/aethelgard/product/session/README.md) — the session describes the schema's rules
- [the merge tests](../../../../../../test/java/com/aethelgard/engine/merge/README.md) — the four built-in rules, provenance, and a custom rule

## Where each step happens

### [Merge](../../../../../../../../docs/architecture/engine/merge.md)

| Step | Member | File |
|------|--------|------|
| 1. The schema maps each field to its rule | `FieldSchema` | [FieldSchema.java](FieldSchema.java) |
| 2. Every write is recorded with the id of its system | `StepOutputBuffer.add`, `ProvenancedWrite` | [StepOutputBuffer.java](StepOutputBuffer.java), [ProvenancedWrite.java](ProvenancedWrite.java) |
| 3. Each written field's rule settles the standing value and the writes | `TypedMerge.merge` | [TypedMerge.java](TypedMerge.java) |
| 4. The rule decides | `FieldType.merge`, `FieldMergeType.merge` | [FieldType.java](FieldType.java), [FieldMergeType.java](FieldMergeType.java) |
| 5. The full map is returned for the Pool to store | `TypedMerge.merge` | [TypedMerge.java](TypedMerge.java) |

### [Determinism](../../../../../../../../docs/architecture/engine/determinism.md)

| Step | Member | File |
|------|--------|------|
| 6. Each rule reads only the standing value and the writes | `TypedMerge.merge` | [TypedMerge.java](TypedMerge.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [FieldSchema.java](FieldSchema.java) | The declared fields and each one's merge rule | `FieldSchema`, `of`, `typeOf` |
| [FieldMergeType.java](FieldMergeType.java) | The port of a merge rule | `FieldMergeType`, `merge` |
| [FieldType.java](FieldType.java) | The built-in merge rules | `FieldType`, `merge` |
| [ProvenancedWrite.java](ProvenancedWrite.java) | One write, with the id of the system that made it | `ProvenancedWrite` |
| [StepOutputBuffer.java](StepOutputBuffer.java) | The writes of one step, per field | `StepOutputBuffer`, `add` |
| [TypedMerge.java](TypedMerge.java) | Settles every written field by its rule | `TypedMerge`, `merge` |
