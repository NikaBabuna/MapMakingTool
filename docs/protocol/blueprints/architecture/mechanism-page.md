<!--
  File: docs/protocol/blueprints/architecture/mechanism-page.md
  Purpose: Shape of a mechanism page of the implementation paper, and the operations that create and edit one
  Audience: Agents
  Update when: The mechanism-page sections or an operation change
-->

# Mechanism page

**Shapes:** every `.md` under `docs/architecture/` except: `README.md` files ([abstract.md](abstract.md), [level-page.md](level-page.md)), `program.md` ([program.md](program.md)), the paper's glossary ([glossary.md](glossary.md)), and the paper's open-questions page ([open-questions.md](open-questions.md))  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a wiki page ([../product/wiki-page.md](../product/wiki-page.md)) — the rule for a person. A mechanism page — how the program computes it. A decision record ([../paperwork/decision-record.md](../paperwork/decision-record.md)) — why.

The page an agent reads instead of the source file. It says what one mechanism reads, what it writes, how, what is true afterwards, and where the code is. Where the page and the source disagree, the page is wrong.

## Skeleton

Form A, one mechanism per page (the default):

```markdown
<!--
  File: docs/architecture/<area>/<page>.md
  Purpose: <Type> — <what it does>
  Audience: Agents and humans
  Update when: <Type.method> changes
-->

# <Mechanism>

<One or two sentences: what this mechanism is for, and what it is not.>

## What it reads

<Inputs: fields, types, and values, named exactly as in the source.>

## What it writes

<Outputs, and what throws or is refused.>

## Procedure

<Ordered steps with their quantities and rules, e.g. `delay = min(base × 2^attempt, cap)`.>

## What is true afterwards

<What a later stage may rely on.>

## Where it lives

`<Type>` in `<source path>`.

Parent: [<parent page>](README.md). <Optional: one or more further pointers, e.g. Why: [ADR-0xx](../../paperwork/decisions/ADR-0xx-<slug>.md).>
```

Form B, several mechanisms that a reader needs together: an opening paragraph, then one `## <Type>` per mechanism, each holding the five sections of Form A as `###` headings, and one `Parent:` line at the end of the page.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | `Purpose` names the type(s). `Update when` names the method(s) whose change changes the page |
| Title | yes | `# <Mechanism>` in the paper's words |
| Opening | yes | One or two sentences. No Step ids |
| What it reads | yes | Every input, named as in the source |
| What it writes | yes | Every output, and every refusal or exception |
| Procedure | yes | Ordered, with every quantity and threshold. A parent's procedure lists children. It does not repeat them |
| What is true afterwards | yes | Guarantees a later stage relies on |
| Where it lives | yes | The type in backticks and its source path in backticks. A table `\| Piece \| Type \| Path \|` when there are several |
| Pointer line | yes | Last line. Begins `Parent: ` with a link to the level page or door. Further pointers follow, separated by `. ` |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create page** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a mechanism with no page |
| **Rewrite section** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes the mechanism's behaviour; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 9 (fix) |
| **Add mechanism** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a new type belongs on an existing Form B page |
| **Add pointer** | [../../flows/step.md](../../flows/step.md) **Decide** step 7 |
| **Relink** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |

### Create page

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md).  
2. Copy Form A and fill every section from the source as it now is.  
3. The level page gains a stage or a row, by **Add stage** or **Add child row** of [level-page.md](level-page.md).

### Rewrite section

**Before.** You have read the source of the method named in `Update when`.

**Edit.**

1. Replace the text of each section that the change made false (What it reads, What it writes, Procedure, What is true afterwards, Where it lives) with a description of the source as it now is.  
2. Update the header's `Update when` if the method moved.  
3. Do not add a banner, a Step id, or a "previously" clause.

### Add mechanism

**Edit.**

1. Before the `Parent:` line, add `## <Type>` with the five `###` sections filled from source.

### Add pointer

**Edit.**

1. At the end of the pointer line, add `. Why: [ADR-0xx](<relative path to the ADR>).`

### Relink

**Edit.**

1. Replace the moved path in the link target, and in the backticked source path if the source moved.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has the five sections (Form A) or five `###` sections per mechanism (Form B) |
| 2 | Every backticked path exists, and every backticked type is declared in source |
| 3 | The last line begins `Parent: ` and its link resolves |
| 4 | No Step id, status banner, or requirement table appears |

## Keep out

- Step history: the changelog.  
- Requirement tables: Step records.  
- A child's procedure copied onto a parent.
