<!--
  File: docs/protocol/blueprints/project/scope.md
  Purpose: Shape of the scope document (docs/project/project.md), and the operations that edit it
  Audience: Agents
  Update when: The scope shape or an operation changes
-->

# Scope

**Shapes:** `docs/project/project.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the concept ([../product/concept.md](../product/concept.md)) — the promise, in prose. Scope is the binding list. The program page ([../architecture/program.md](../architecture/program.md)) — the stack as built.

The hard bound on what the project includes and refuses. Work outside **In scope**, or inside **Out of scope**, is not done until this file changes first ([../../flows/conflict-resolve.md](../../flows/conflict-resolve.md) R6).

## Skeleton

```markdown
<!--
  File: docs/project/project.md
  Purpose: Hard scope lock
  Audience: Agents and humans
  Update when: Scope changes (also decisions.md if technical)
-->

# Project scope

**Product name:** <name>  
**Repository:** <repository name>  
**Phase:** <alpha | beta | prod>

---

## One line

<The product in one sentence a stranger can repeat.>

---

## In scope

| Area | Description |
|------|-------------|
| **<Area>** | <what the area includes> |
…

---

## Out of scope (for now)

- <explicit refusal>
…

---

## Stack (intent)

| Choice | Status |
|--------|--------|
| <Kind>: <choice> | Accepted — see <ADR-0xx in [../paperwork/decisions.md](../paperwork/decisions.md) \| the page that records it> |
…

---

## Expansion

Scope changes belong here first. Technical scope changes also get an entry in [../paperwork/decisions.md](../paperwork/decisions.md).
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Key block | yes | Three lines. **Phase** equals `phase.md` |
| One line | yes | One sentence. The repository README quotes it word for word |
| In scope | yes | One row per area. Work outside the table is not allowed |
| Out of scope | yes | One bullet per refusal. No placeholder bullets |
| Stack | yes | One row per accepted choice, each pointing at the ADR or page that records it |
| Expansion | yes | As in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add in-scope area** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Amend in-scope area** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Add exclusion** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Remove exclusion** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Set stack row** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.2; [../../flows/step.md](../../flows/step.md) **Decide** step 7; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 11 |
| **Change one line** | [../../flows/amendment.md](../../flows/amendment.md) Step 7.1 |
| **Set phase** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when `phase.md` changes phase |

### Add in-scope area

**Edit.**

1. Add `| **<Area>** | <the approved sentence> |` at the bottom of **In scope**.

### Amend in-scope area

**Edit.**

1. Replace the Description cell of that area with the approved sentence.

### Add exclusion

**Edit.**

1. Add `- <the approved sentence>` at the bottom of **Out of scope**.

### Remove exclusion

**Edit.**

1. Delete that bullet.

### Set stack row

**Edit.**

1. If the choice has a row, replace its Status cell with `Accepted — see ADR-0xx in [../paperwork/decisions.md](../paperwork/decisions.md)`. Otherwise add `| <Kind>: <choice> | Accepted — see ADR-0xx in [../paperwork/decisions.md](../paperwork/decisions.md) |`.

### Change one line

**Edit.**

1. Replace the sentence under **One line** with the approved sentence. The repository README then needs **Sync one line** of [../doors/repo-readme.md](../doors/repo-readme.md).

### Set phase

**Edit.**

1. Replace the value after `**Phase:** ` with the new phase word.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every Stack row's pointer resolves |
| 2 | **Phase** equals `phase.md` |
| 3 | No placeholder or editorial comment remains |

## Keep out

- How a system works: the architecture paper.  
- Step status: paperwork.  
- Protocol rules.
