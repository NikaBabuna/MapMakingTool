<!--
  File: docs/architecture/host/merge.md
  Purpose: FieldMergeType and the four built-in resolvers
  Audience: Agents and humans
  Update when: TypedMerge or FieldType changes
-->

# Merge

After every claiming system has finished, system writes become Pool fields. Each field name has one `FieldMergeType`. The type is the resolver. The engine does not special-case product rules.

## What it reads

The standing field map, the `FieldSchema`, and the step output buffer. The buffer maps each field to the list of `ProvenancedWrite`s that named it. Provenance is the system id.

## What it writes

A new field map. Untouched standing fields are copied through. Each field that has writers is replaced by `type.merge(standing, writers)`. `Pool.applyFields` stores that map. The event buffer is cleared by the step, not by the merger.

## Procedure

`TypedMerge.merge` walks buffer fields. `schema.typeOf(field)` selects the rule. Writer values are `Object`s. A custom `FieldMergeType` decides which shapes are legal. A null merge result throws.

Built-in rules are the `FieldType` enum. A product adds a rule by implementing `FieldMergeType` and placing that instance in the schema. A product rule is not a new enum constant.

| Type | Rule in code |
|------|----------------|
| `STATIC` | `pickOne`: the write whose `systemId` is lexicographically smallest. Standing is ignored. |
| `DESTRUCTIVE` | The same `pickOne`. |
| `INCREMENT` | Standing defaults to 0 when null. Every writer value must be a `Long` or an `Integer`. The result is standing plus the sum of the writes. |
| `CONSTANT` | The standing value. Writers are ignored. A null standing throws. |

`pickOne` on an empty writer list throws. Delete Request is not a `FieldType`. How a delete would meet a concurrent write is [open question 4](open-questions.md).

Two layers of conflict stay distinct. Inside one system, overlapping sub-systems are ordered by the resolver before they write staging. Across systems, writers never see each other; only the field type sees the set.

## What is true afterwards

Every written field has one value. Static and destructive ties are a pure function of the system ids. Increment is a pure function of standing and the values. Constant is a pure function of standing. The next step's systems see these values only after a new snapshot.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Orchestration | `TypedMerge` | `engine/.../merge/TypedMerge.java` |
| Built-ins | `FieldType` | `engine/.../merge/FieldType.java` |
| Port | `FieldMergeType` | `engine/.../merge/FieldMergeType.java` |
| Schema | `FieldSchema` | `engine/.../merge/FieldSchema.java` |
| Provenance | `ProvenancedWrite` | `engine/.../merge/ProvenancedWrite.java` |
| Buffer | `StepOutputBuffer` | `engine/.../merge/StepOutputBuffer.java` |

Parent: [one engine step](README.md). Why order still settles: [determinism](determinism.md).
