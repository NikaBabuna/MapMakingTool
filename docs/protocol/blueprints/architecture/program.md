<!--
  File: docs/protocol/blueprints/architecture/program.md
  Purpose: Shape of the program page (docs/architecture/program.md) — modules, dependencies, and the project facts the protocol relies on — and the operations that edit it
  Audience: Agents
  Update when: The program-page shape, a project-fact line, or an operation changes
-->

# Program page

**Shapes:** `docs/architecture/program.md`  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** scope ([../project/scope.md](../project/scope.md)) — the stack as an approved choice. The program page — the stack as built.

The coarsest level of the implementation paper: how the project is built, its modules, which way they depend, and four project facts that the protocol names but never states itself. Every protocol page that runs the suite, finds a declaration, finds a test, or reads test output takes that fact from the lines below, and from nowhere else.

## Project-fact lines

| Line | What the project states there | Used by |
|------|-------------------------------|---------|
| `**Witness command:**` | The one command that runs the whole suite, from the repository root, and any alternate form for another shell | Every flow that runs the witness |
| `**Tests:**` | Where test files live, and how a test file is named after the unit it tests | [../../navigation/code.md](../../navigation/code.md), [../../flows/amendment.md](../../flows/amendment.md), [../../flows/step.md](../../flows/step.md) |
| `**Declarations:**` | The search pattern, per language of the project, that finds where a named unit (type, function, module) is declared, and the folders to search | [../../navigation/reading.md](../../navigation/reading.md), [../../navigation/code.md](../../navigation/code.md), [../../flows/global-docsync.md](../../flows/global-docsync.md) |
| `**Output summary:**` | The text patterns in the witness command's output that mark the summary and each failure | [../../navigation/reading.md](../../navigation/reading.md), [../../navigation/walks.md](../../navigation/walks.md) |

## Skeleton

````markdown
<!--
  File: docs/architecture/program.md
  Purpose: Level 1 — modules and the dependency direction
  Audience: Agents and humans
  Update when: A module is added, the dependency direction changes, or a project-fact line changes
-->

# Program

<One paragraph: what builds the project, the language level, and where the build's entry script lives.>

**Witness command:** `<command>` from the repository root<; `<alternate form>` in <other shell>>. <What runs it in CI, if anything.>  
**Tests:** <where test files live>, named <naming rule relative to the unit tested>.  
**Declarations:** <per language: `<search pattern>` in `<folders>`>.  
**Output summary:** lines matching <`<pattern>`, …> in the witness output.

<One paragraph: how many modules, and the job of each in one clause.>

| Module | Build id | Code root | Role |
|--------|----------|-----------|------|
| `<folder>/` | `<the build's name for it>` | `<root of its code namespace>` | <role> |
…

## Which way dependencies go

<The rule in one sentence.>

```
<module>  →  <module>  →  <module>
…
```

<Sentences naming every allowed exception and every forbidden dependency.>

## Where the rest of the paper is

| Question | Page |
|----------|------|
| <question> | [<area>/](<area>/README.md) |
…
````

The Module table's second and third column headings may use the build's own words (for example the build's name for a module identifier, and the language's name for a code namespace).

## Parts

| Part | Required | Rule |
|------|----------|------|
| Build paragraph | yes | What builds the project, and the language level |
| Project-fact lines | yes | The four lines of **Project-fact lines**, each exactly once, in that order, each beginning with its bold label |
| Module table | yes | One row per module the build declares, in the build's order |
| Dependency section | yes | A diagram in a plain fence, and every exception stated |
| Pointer table | yes | One row per area of the paper |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add module** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a module |
| **Change dependency** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes which module depends on which |
| **Set witness command** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes how the suite is run |
| **Set project fact** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes where tests live, how they are named, which languages the project uses, or the witness output format |

### Add module

**Edit.**

1. Add `| \`<folder>/\` | \`<build id>\` | \`<code root>\` | <role> |` in the build's order.  
2. Update the module-count sentence and the dependency diagram.  
3. If the module adds a language, apply **Set project fact** to **Declarations**.

### Change dependency

**Edit.**

1. Redraw the diagram lines that changed, and rewrite the exception sentences.

### Set witness command

**Edit.**

1. Replace the backticked command, and the alternate form, on the **Witness command** line.

### Set project fact

**Edit.**

1. Replace the text after the label on the **Tests**, **Declarations**, or **Output summary** line with the fact as it now is.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Each of the four project-fact lines exists exactly once |
| 2 | The witness command runs the whole suite. The **Declarations** pattern finds a known declaration in each listed folder. The **Tests** rule names an existing test file correctly |
| 3 | The module table matches the modules the build declares |
| 4 | The diagram matches the declared module dependencies |

## Keep out

- The stack as a decision: scope and decision records.  
- A module's internals: its level and mechanism pages.
