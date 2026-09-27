<!--
  File: docs/protocol/blueprints/architecture/mechanism-page.md
  Purpose: Shape of a mechanism page of the implementation paper, and the operations that create and edit one
  Audience: Agents
  Update when: The mechanism-page sections or an operation change
-->

# Mechanism page

**Shapes:** every `.md` under `docs/architecture/` except: `README.md` files ([abstract.md](abstract.md), [level-page.md](level-page.md)), `program.md` ([program.md](program.md)), the paper's glossary ([glossary.md](glossary.md)), the paper's open-questions page ([open-questions.md](open-questions.md)), and the code conventions page `conventions.md` ([conventions.md](conventions.md))  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a wiki page ([../product/wiki-page.md](../product/wiki-page.md)) — the rule for a person. A mechanism page — the concept and the mathematics of how the program computes it. A code door ([../doors/code-door.md](../doors/code-door.md)) — the members and files that perform each step. A decision record ([../paperwork/decision-record.md](../paperwork/decision-record.md)) — why.

The concept, the engineering, and the mathematics of one mechanism. It says what the mechanism reads, what it writes, what it computes as a model, the steps it takes in the order the code takes them, and what is true afterwards. It states the mechanism twice: in plain words, and as a model. It may name a type or a field in passing, but it quotes no source and names no member: the code door of each folder that performs the mechanism, linked from the page's `Code:` line, maps every step to the member that performs it. Where the page and the source disagree, the page is wrong.

## Skeleton

Form A, one mechanism per page (the default):

````markdown
<!--
  File: docs/architecture/<area>/<page>.md
  Purpose: <Mechanism> — <what it does>
  Audience: Agents and humans
  Update when: What <mechanism> computes, or the order of its steps, changes
-->

# <Mechanism>

<One or two plain sentences: what this mechanism is for, and what it is not. No math, no code span.>

## What it reads

<Inputs: fields and values, in the paper's words. A field or a type may be named in backticks, as in the source, in passing.>

## What it writes

<Outputs, and what is refused.>

## Model

<The mechanism as mathematics, in LaTeX: the sets and functions it works on, what it computes, and the invariants it keeps. Every symbol is defined here or in the Symbols table of the paper glossary. Example: $d_{k+1} = \min(b \cdot 2^{k}, c)$.>

## Procedure

1. <One step in plain words, with its quantities and rules.>
…

## What is true afterwards

<What a later stage may rely on.>

## Cost

<Optional: the time and memory of one run, in the Model's symbols, e.g. $O(n)$ time and one buffer of $n$ entries.>

Code: [<folder>/](<relative link to the README.md of the code folder that performs this mechanism>) <· further folders, when several perform it>
Parent: [<parent page>](README.md). <Optional: further pointers, e.g. Why: [ADR-0xx](../../paperwork/decisions/ADR-0xx-<slug>.md).>
````

Form B, several mechanisms that a reader needs together: an opening of one or two plain sentences, then one `## <Mechanism>` per mechanism, each holding the sections of Form A as `###` headings (What it reads, What it writes, Model, Procedure, What is true afterwards, optional Cost), and one `Code:` line and one `Parent:` line at the end of the page.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | `Purpose` names the mechanism. `Update when` names the behaviour whose change changes the page |
| Title | yes | `# <Mechanism>` in the paper's words |
| Opening | yes | One or two plain sentences. No math, no code span, no Step id |
| What it reads | yes | Every input, in the paper's words. A field, type, or value may be named in backticks as in the source, in passing |
| What it writes | yes | Every output, and every refusal |
| Model | yes | The mechanism in LaTeX: `$…$` inline, `$$…$$` for a display formula. Every symbol is defined in the section, or has a row in the Symbols table of the paper glossary. Invariants the code keeps are stated as formulas |
| Procedure | yes | Numbered steps in the order the code runs them, in plain words, with every quantity and threshold. No step names a member or links a source file: the code door maps each step to its member. A parent's procedure lists children. It does not repeat them |
| What is true afterwards | yes | Guarantees a later stage relies on |
| Cost | no | Time and memory of one run, in the Model's symbols |
| Code line | yes | The line before the pointer line. Begins `Code: `, then one link per code folder that performs this mechanism, to that folder's `README.md` ([../doors/code-door.md](../doors/code-door.md)), separated by ` · ` |
| Pointer line | yes | Last line. Begins `Parent: ` with a link to the level page or door. Further pointers follow, separated by `. ` |
| Code detail | no | Never on the page: a fenced excerpt of source, a link from a Procedure step to a source file, or a table of types and members. They belong in the code door's Where each step happens and Contents |


## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create page** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a mechanism with no page |
| **Rewrite section** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes the mechanism's behaviour; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 9 (fix) |
| **Rewrite page** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a page written to an earlier form of this blueprint is brought to the current form |
| **Add mechanism** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a new mechanism belongs on an existing Form B page |
| **Set code line** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when the code folders that perform the mechanism change |
| **Add pointer** | [../../flows/step.md](../../flows/step.md) **Decide** step 7 |
| **Relink** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |

### Create page

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md).  
2. Copy Form A and fill every section from the source as it now is: the Model from what the code computes, and the Procedure as the steps the code takes, in its order, in plain words.  
3. Write the `Code:` line, linking the door of each code folder that performs the mechanism.

**Result.** The page exists. The level page still needs **Add stage** or **Add child row** of [level-page.md](level-page.md), and each door on the `Code:` line still needs **Set step map** of [../doors/code-door.md](../doors/code-door.md).

### Rewrite section

**Before.** You have read the source of the mechanism, reached through the doors on the `Code:` line.

**Edit.**

1. Replace the text of each section that the change made false (What it reads, What it writes, Model, Procedure, What is true afterwards, Cost) with a description of the source as it now is.  
2. Update the header's `Update when` if the behaviour it names changed.  
3. Do not add a banner, a Step id, or a "previously" clause.

**Result.** The page is true. If the Procedure's steps changed, each door on the `Code:` line still needs **Set step map** of [../doors/code-door.md](../doors/code-door.md).

### Rewrite page

**Before.** The page has a part this blueprint no longer allows (a source excerpt, a member link in the Procedure, or a table of types and members), or lacks the `Code:` line. The doors of the code folders that perform the mechanism exist.

**Edit.**

1. Keep the header, title, opening, What it reads, What it writes, Model, What is true afterwards, and Cost where they are true; remove every backticked member name from them, keeping a type or field named in passing.  
2. Delete every fenced source excerpt and the line that introduces it.  
3. Rewrite each Procedure step in plain words, deleting its member and its source link.  
4. Delete the table of types and members, with its heading.  
5. Write the `Code:` line before the pointer line.

**Result.** The page is in the current form. The member behind each step is on the code doors, by **Set step map** of [../doors/code-door.md](../doors/code-door.md).

### Add mechanism

**Edit.**

1. Before the `Code:` line, add `## <Mechanism>` with the `###` sections of Form B filled from source.

### Set code line

**Before.** A code folder began or stopped performing this mechanism, or its door moved.

**Edit.**

1. Replace the `Code:` line with `Code: ` followed by one link per code folder that now performs the mechanism, to that folder's `README.md`, separated by ` · `.

**Result.** The `Code:` line names every door that maps this page's steps.

### Add pointer

**Edit.**

1. At the end of the pointer line, add `. Why: [ADR-0xx](<relative path to the ADR>).`

### Relink

**Edit.**

1. Replace the moved path in every link target on the page, including the `Code:` line and the pointer line, and in every backticked path.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has the sections of Form A in order, or those sections as `###` under each `## <Mechanism>` of Form B |
| 2 | Every backticked path exists, and every backticked type is declared in source |
| 3 | The last line begins `Parent: ` and its link resolves |
| 4 | No Step id, status banner, or requirement table appears |
| 5 | The opening contains no `$` and no backtick |
| 6 | Every Model section contains LaTeX, and every symbol in it is defined in the section or in the Symbols table of the paper glossary |
| 7 | No numbered Procedure step names a member or links a source file |
| 8 | The page holds no fenced excerpt of source and no table of types and members |
| 9 | The line before the last begins `Code: `, and each of its links resolves to a code door whose Where each step happens has a `###` for this page |

## Keep out

- Member names, source excerpts, and the table of types and members: the code door ([../doors/code-door.md](../doors/code-door.md)).  
- Step history: the changelog.  
- Requirement tables: Step records.  
- A child's procedure copied onto a parent.  
- Test code: the paper describes the program, not its tests.
