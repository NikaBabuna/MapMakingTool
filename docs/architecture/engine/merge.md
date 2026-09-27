<!--
  File: docs/architecture/engine/merge.md
  Purpose: Merge — how the proposals of all systems become one value per field
  Audience: Agents and humans
  Update when: How the merge settles a field, a built-in rule, or the field schema changes
-->

# Merge

When several systems propose values for the same field in one step, something must decide what the field becomes. Each field carries its own rule for that decision, chosen once in the field schema; the engine applies the rules and knows nothing of what the fields mean.

## What it reads

The standing field map $F'$ (the Pool after compute), the `FieldSchema`, and the `StepOutputBuffer`, which maps each written field to the list of `ProvenancedWrite`s that named it, in system order. Provenance is the writing system's id.

## What it writes

A new, complete field map, which the Pool stores. Fields nobody wrote keep their standing values. Refusals: asking the schema for the rule of a field it does not declare throws `IllegalArgumentException`, so a system that writes an undeclared field fails the step at merge. Picking one write from an empty list throws `IllegalStateException`. `INCREMENT` throws `IllegalArgumentException` when the standing value or a write is not a `Long` or an `Integer`. `CONSTANT` throws `IllegalStateException` when the standing value is null. A null merge result throws `NullPointerException`.

## Model

For each field $f$ let $\mathrm{Wr}_f = \bigl((\mathrm{id}_1, x_1), \dots, (\mathrm{id}_n, x_n)\bigr)$ be its writes this step, in system order. The merged map is

$$F_k(f) = \begin{cases} \tau_f\bigl(F'(f),\; \mathrm{Wr}_f\bigr) & \text{if } \mathrm{Wr}_f \text{ is non-empty}, \\ F'(f) & \text{otherwise}, \end{cases}$$

and the four built-in rules $\tau \in$ `FieldType` are

$$\begin{aligned}
\textsf{STATIC},\ \textsf{DESTRUCTIVE}:&\quad \tau(x, W) = x_{i^*}, \quad i^* = \min\bigl\{\, i : \mathrm{id}_i = \min\nolimits_{\mathrm{lex}} \{\mathrm{id}_1, \dots, \mathrm{id}_n\} \,\bigr\}, \\
\textsf{INCREMENT}:&\quad \tau(x, W) = [x]_0 + \textstyle\sum_{i=1}^{n} x_i \pmod{2^{64}}, \qquad [x]_0 = \begin{cases} 0 & x \text{ absent} \\ x & \text{otherwise} \end{cases} \\
\textsf{CONSTANT}:&\quad \tau(x, W) = x .
\end{aligned}$$

$\min_{\mathrm{lex}}$ compares ids as strings, code unit by code unit. Among equal ids the earliest write wins. The sum wraps like 64-bit two's-complement arithmetic. A product adds a rule by implementing `FieldMergeType` and naming it in the schema, not by adding an enum constant.

## Procedure

1. The schema maps each field name to its rule. It is built from a map, which it copies, or declares nothing; it can be asked whether it declares a field, and it gives a field's rule or throws.
2. During the run of systems, the engine appends a `ProvenancedWrite` of the system's id and the value for every entry of every `OUT_SYS` under its field, in system order. A write rejects a null id or value.
3. The merge copies the standing map, and for every written field looks up the field's rule and replaces the value with the rule's result.
4. The rule decides. `STATIC` and `DESTRUCTIVE` take the write of the lexicographically smallest system id, ignoring the standing value. `INCREMENT` adds every write to the standing value (0 when absent). `CONSTANT` keeps the standing value and ignores every write. A product rule implements the `FieldMergeType` port.
5. The merge returns the full map as an immutable copy, and the engine stores it in the Pool.

## What is true afterwards

Every declared field has exactly one non-null value. A `STATIC` or `DESTRUCTIVE` result is a function of the system ids and their values, not of the order in which systems were registered, unless two writers share an id. An `INCREMENT` result is a function of the standing value and the multiset of writes. A `CONSTANT` field is never changed by merge; only a compute that sets it directly can change it. The next step's systems see these values only through the next snapshot.

Two layers of conflict stay separate: inside one system, the resolver orders overlapping sub-systems before they stage ([systems](systems.md)); across systems, writers never see each other, and only the field's rule sees the set.

## Cost

$O(|\Phi| + \sum_f |\mathrm{Wr}_f|)$ per step, plus the cost of any product rule.

Code: [engine/merge/](../../../engine/src/main/java/com/aethelgard/engine/merge/README.md)  
Parent: [one engine step](README.md). Why the order of systems still settles the same: [determinism](determinism.md). A delete rule is not decided: [open question 4](../open-questions.md).
