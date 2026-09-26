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

The page an agent reads instead of the source file. It says what one mechanism reads, what it writes, what it computes as mathematics, how it computes it step by step, what is true afterwards, and where the code is. It states the mechanism twice: in plain words, and as a model. It backs both with the code: every step names the member that performs it, and the lines that compute a formula are quoted. Where the page and the source disagree, the page is wrong.

## Skeleton

Form A, one mechanism per page (the default):

````markdown
<!--
  File: docs/architecture/<area>/<page>.md
  Purpose: <Type> — <what it does>
  Audience: Agents and humans
  Update when: <Type.method> changes
-->

# <Mechanism>

<One or two plain sentences: what this mechanism is for, and what it is not. No math, no code span.>

## What it reads

<Inputs: fields, types, and values, named exactly as in the source.>

## What it writes

<Outputs, and what throws or is refused.>

## Model

<The mechanism as mathematics, in LaTeX: the sets and functions it works on, what it computes, and the invariants it keeps. Every symbol is defined here or in the Symbols table of the paper glossary. Example: $d_{k+1} = \min(b \cdot 2^{k}, c)$.>

<Excerpt, after the formula it implements:>

`<Type.member>` in [`<file name>`](<relative link to the source file>):

```<language of the source file>
<at most 15 lines, copied from the source>
```

## Procedure

1. <One step in plain words, with its quantities and rules.> [`<Type.member>`](<relative link to the source file>).
…

## What is true afterwards

<What a later stage may rely on.>

## Cost

<Optional: the time and memory of one run, in the Model's symbols, e.g. $O(n)$ time and one buffer of $n$ entries.>

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| <role> | `<Type>` | `<member>`, `<member>` | [`<source path>`](<relative link>) |
…

Parent: [<parent page>](README.md). <Optional: further pointers, e.g. Why: [ADR-0xx](../../paperwork/decisions/ADR-0xx-<slug>.md).>
````

Form B, several mechanisms that a reader needs together: an opening of one or two plain sentences, then one `## <Type>` per mechanism, each holding the sections of Form A as `###` headings (What it reads, What it writes, Model, Procedure, What is true afterwards, optional Cost, Where it lives), and one `Parent:` line at the end of the page.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | `Purpose` names the type(s). `Update when` names the method(s) whose change changes the page |
| Title | yes | `# <Mechanism>` in the paper's words |
| Opening | yes | One or two plain sentences. No math, no code span, no Step id |
| What it reads | yes | Every input, named as in the source |
| What it writes | yes | Every output, and every refusal or exception |
| Model | yes | The mechanism in LaTeX: `$…$` inline, `$$…$$` for a display formula. Every symbol is defined in the section, or has a row in the Symbols table of the paper glossary. Invariants the code keeps are stated as formulas |
| Excerpt | when the code computes a formula the Model or the Procedure states | Directly after the formula or the step it implements. One line naming the member in backticks and linking its source file, then a fence in the source file's language holding at most 15 consecutive lines copied from that file. Leading indentation may be removed. Nothing else may change: no elision, no added comment |
| Procedure | yes | Numbered steps in the order the source runs them, with every quantity and threshold. Each step ends with the member that performs it, in backticks, linked to its source file. A parent's procedure lists children. It does not repeat them |
| What is true afterwards | yes | Guarantees a later stage relies on |
| Cost | no | Time and memory of one run, in the Model's symbols |
| Where it lives | yes | A table `\| Piece \| Type \| Members \| Path \|`. **Members** names every member whose behaviour this page describes, and no member that another page's table names. **Path** is the source path in backticks, linked to the file |
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
2. Copy Form A and fill every section from the source as it now is: the Model from what the code computes, each Procedure step with the member that performs it, and an excerpt after each formula the code computes.  
3. The level page gains a stage or a row, by **Add stage** or **Add child row** of [level-page.md](level-page.md).

### Rewrite section

**Before.** You have read the source of the method named in `Update when`.

**Edit.**

1. Replace the text of each section that the change made false (What it reads, What it writes, Model, Procedure, What is true afterwards, Cost, Where it lives) with a description of the source as it now is.  
2. Copy again every excerpt whose source lines changed, and move its member line if the member moved.  
3. Update the header's `Update when` if the method moved.  
4. Do not add a banner, a Step id, or a "previously" clause.

### Add mechanism

**Edit.**

1. Before the `Parent:` line, add `## <Type>` with the `###` sections of Form B filled from source.

### Add pointer

**Edit.**

1. At the end of the pointer line, add `. Why: [ADR-0xx](<relative path to the ADR>).`

### Relink

**Edit.**

1. Replace the moved path in the link target, in the backticked source path, and in every source link of the Procedure, the excerpts, and Where it lives.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has the sections of Form A in order, or those sections as `###` under each `## <Type>` of Form B |
| 2 | Every backticked path exists, and every backticked type is declared in source |
| 3 | The last line begins `Parent: ` and its link resolves |
| 4 | No Step id, status banner, or requirement table appears |
| 5 | The opening contains no `$` and no backtick |
| 6 | Every Model section contains LaTeX, and every symbol in it is defined in the section or in the Symbols table of the paper glossary |
| 7 | Every numbered Procedure step ends with a member in backticks, linked to a source file that declares it |
| 8 | Every excerpt has at most 15 lines, and its lines occur consecutively in the linked source file, ignoring leading indentation |
| 9 | No member in the Members column also appears in the Members column of another page |

## Keep out

- Step history: the changelog.  
- Requirement tables: Step records.  
- A child's procedure copied onto a parent.  
- Test code: the paper describes the program, not its tests.
