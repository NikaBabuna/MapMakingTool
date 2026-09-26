<!--
  File: docs/architecture/engine/merge.md
  Purpose: TypedMerge, FieldType, FieldMergeType, FieldSchema, ProvenancedWrite, StepOutputBuffer — how the proposals of all systems become one value per field
  Audience: Agents and humans
  Update when: TypedMerge.merge, TypedMerge.pickOne, a FieldType rule, or FieldSchema changes
-->

# Merge

When several systems propose values for the same field in one step, something must decide what the field becomes. Each field carries its own rule for that decision, chosen once in the field schema; the engine applies the rules and knows nothing of what the fields mean.

## What it reads

The standing field map $F'$ (the Pool after compute), the `FieldSchema`, and the `StepOutputBuffer`, which maps each written field to the list of `ProvenancedWrite`s that named it, in system order. Provenance is the writing system's id.

## What it writes

A new, complete field map, which `Pool.applyFields` stores. Fields nobody wrote keep their standing values. Refusals: `FieldSchema.typeOf` throws `IllegalArgumentException` for a field the schema does not declare, so a system that writes an undeclared field fails the step at merge. `pickOne` throws `IllegalStateException` for an empty list. `INCREMENT` throws `IllegalArgumentException` when the standing value or a write is not a `Long` or an `Integer`. `CONSTANT` throws `IllegalStateException` when the standing value is null. A null merge result throws `NullPointerException`.

## Model

For each field $f$ let $\mathrm{Wr}_f = \bigl((\mathrm{id}_1, x_1), \dots, (\mathrm{id}_n, x_n)\bigr)$ be its writes this step, in system order. The merged map is

$$F_k(f) = \begin{cases} \tau_f\bigl(F'(f),\; \mathrm{Wr}_f\bigr) & \text{if } \mathrm{Wr}_f \text{ is non-empty}, \\ F'(f) & \text{otherwise}, \end{cases}$$

and the four built-in rules $\tau \in$ `FieldType` are

$$\begin{aligned}
\textsf{STATIC},\ \textsf{DESTRUCTIVE}:&\quad \tau(x, W) = x_{i^*}, \quad i^* = \min\bigl\{\, i : \mathrm{id}_i = \min\nolimits_{\mathrm{lex}} \{\mathrm{id}_1, \dots, \mathrm{id}_n\} \,\bigr\}, \\
\textsf{INCREMENT}:&\quad \tau(x, W) = [x]_0 + \textstyle\sum_{i=1}^{n} x_i \pmod{2^{64}}, \qquad [x]_0 = \begin{cases} 0 & x \text{ absent} \\ x & \text{otherwise} \end{cases} \\
\textsf{CONSTANT}:&\quad \tau(x, W) = x .
\end{aligned}$$

$\min_{\mathrm{lex}}$ compares ids by `String.compareTo`, code unit by code unit. Among equal ids the earliest write wins. The sum wraps like 64-bit two's-complement arithmetic. A product adds a rule by implementing `FieldMergeType` and naming it in the schema, not by adding an enum constant.

`TypedMerge.merge` in [`TypedMerge.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java):

```java
Map<String, Object> result = new LinkedHashMap<>(standing);
for (var entry : buffer.asMap().entrySet()) {
  String field = entry.getKey();
  List<ProvenancedWrite> writers = entry.getValue();
  FieldMergeType type = schema.typeOf(field);
  Object standingValue = standing.get(field);
  Object merged = type.merge(standingValue, writers);
  result.put(field, Objects.requireNonNull(merged, "merge result for " + field));
}
return Map.copyOf(result);
```

`TypedMerge.pickOne` in [`TypedMerge.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java):

```java
return writers.stream()
    .min(Comparator.comparing(ProvenancedWrite::systemId))
    .orElseThrow(() -> new IllegalStateException("empty writer set"));
```

## Procedure

1. The schema maps each field name to its rule. `FieldSchema.of` copies the given map, `FieldSchema.empty` declares nothing, `has` asks, and `typeOf` answers or throws. [`FieldSchema`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldSchema.java).
2. During the run of systems, the engine appends `ProvenancedWrite(systemId, value)` for every entry of every `OUT_SYS` under its field, in system order. A write rejects a null id or value. [`StepOutputBuffer.add`](../../../engine/src/main/java/com/aethelgard/engine/merge/StepOutputBuffer.java), [`ProvenancedWrite`](../../../engine/src/main/java/com/aethelgard/engine/merge/ProvenancedWrite.java).
3. `merge` copies the standing map, and for every written field looks up the field's rule and replaces the value with the rule's result. [`TypedMerge.merge`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java).
4. The rule decides. `STATIC` and `DESTRUCTIVE` take the write of the lexicographically smallest system id through `pickOne`, ignoring the standing value. `INCREMENT` adds every write to the standing value (0 when absent). `CONSTANT` keeps the standing value and ignores every write. A product rule implements `FieldMergeType.merge`. [`FieldType.merge`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldType.java), [`FieldMergeType.merge`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldMergeType.java).
5. `merge` returns the full map as an immutable copy, and the engine stores it with `Pool.applyFields`. [`TypedMerge.merge`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java).

## What is true afterwards

Every declared field has exactly one non-null value. A `STATIC` or `DESTRUCTIVE` result is a function of the system ids and their values, not of the order in which systems were registered, unless two writers share an id. An `INCREMENT` result is a function of the standing value and the multiset of writes. A `CONSTANT` field is never changed by merge; only a compute that sets it directly can change it. The next step's systems see these values only through the next snapshot.

Two layers of conflict stay separate: inside one system, the resolver orders overlapping sub-systems before they stage ([systems](systems.md)); across systems, writers never see each other, and only the field's rule sees the set.

## Cost

$O(|\Phi| + \sum_f |\mathrm{Wr}_f|)$ per step, plus the cost of any product rule.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Orchestration | `TypedMerge` | `merge`, `pickOne` | [`engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java) |
| Built-in rules | `FieldType` | `STATIC`, `INCREMENT`, `CONSTANT`, `DESTRUCTIVE`, `FieldType.merge` | [`engine/src/main/java/com/aethelgard/engine/merge/FieldType.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldType.java) |
| Rule port | `FieldMergeType` | `FieldMergeType.merge` | [`engine/src/main/java/com/aethelgard/engine/merge/FieldMergeType.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldMergeType.java) |
| Schema | `FieldSchema` | `FieldSchema.empty`, `FieldSchema.of`, `typeOf`, `has`, `asMap`, `FieldSchema.isEmpty` | [`engine/src/main/java/com/aethelgard/engine/merge/FieldSchema.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/FieldSchema.java) |
| Provenance | `ProvenancedWrite` | `ProvenancedWrite`, `systemId`, `ProvenancedWrite.value` | [`engine/src/main/java/com/aethelgard/engine/merge/ProvenancedWrite.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/ProvenancedWrite.java) |
| Output buffer | `StepOutputBuffer` | `StepOutputBuffer.add`, `StepOutputBuffer.asMap`, `StepOutputBuffer.isEmpty` | [`engine/src/main/java/com/aethelgard/engine/merge/StepOutputBuffer.java`](../../../engine/src/main/java/com/aethelgard/engine/merge/StepOutputBuffer.java) |

Parent: [one engine step](README.md). Why the order of systems still settles the same: [determinism](determinism.md). A delete rule is not decided: [open question 4](../open-questions.md).
