<!--
  File: docs/protocol/blueprints/architecture/program.md
  Purpose: Shape of the program page (docs/architecture/program.md) — modules, build files, dependencies, processes, and the project facts the protocol relies on — and the operations that edit it
  Audience: Agents
  Update when: The program-page shape, a project-fact line, or an operation changes
-->

# Program page

**Shapes:** `docs/architecture/program.md`  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a decision record ([../paperwork/decision-record.md](../paperwork/decision-record.md)) — why a stack choice was made. The program page lists the choices and points at those records. The concept ([../product/concept.md](../product/concept.md)) — what the product includes, in a person's words, with no stack.

The coarsest level of the implementation paper: how the project is built, its modules and build files, which way they depend, which processes run when the project runs and how they reach each other, the stack it was built with, and four project facts that the protocol names but never states itself. Every protocol page that runs the suite, finds a declaration, finds a test, or reads test output takes that fact from the lines below, and from nowhere else.

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
  Purpose: Level 1 — modules, build files, the dependency direction, the processes, and the stack
  Audience: Agents and humans
  Update when: A module, a build file, or a process is added, the dependency direction changes, a project-fact line changes, or a stack choice changes
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

Every build file:

| Build file | What it builds |
|------------|----------------|
| [`<path>`](<relative link>) | <what it builds, and with which tool> |
…

## Which way dependencies go

<The rule in one sentence.>

```
<module>  →  <module>  →  <module>
…
```

<Sentences naming every allowed exception and every forbidden dependency.>

## Processes

<One paragraph: which processes run when the project runs, which one starts which, and how they reach each other.>

| Process | Started by | Entry point | Listens on | Talks to |
|---------|------------|-------------|------------|----------|
| <process> | <a script, another process, or a person> | [`<type, function, or file>`](<relative link>) | <address and port, or —> | <the process it reaches, and how> |
…

```
<process>  →  <process>
…
```

<Sentences linking every launch script and the continuous-integration workflow, each with what it starts.>

## Stack

The choices of how the project is built, each with the decision record that made it. A change to a row is a decision first.

| Choice | Decided in |
|--------|------------|
| <Kind>: <choice> | [ADR-0xx](../paperwork/decisions/ADR-0xx-<slug>.md)<, [ADR-0yy](../paperwork/decisions/ADR-0yy-<slug>.md)> |
…

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
| Build files | yes | `Every build file:`, then one row per build file: the root build file, each module's build file, and the manifest of every part built by its own tool. **Build file** is the path in backticks, linked |
| Dependency section | yes | A diagram in a plain fence, and every exception stated |
| Processes | yes | `## Processes`: the paragraph, then one row per process the project runs, with what starts it, its entry point linked to its source, the address and port it listens on (`—` if none), and what it talks to; then a diagram in a plain fence, and sentences linking every launch script and the continuous-integration workflow |
| Stack | yes | `## Stack`, the two sentences of the Skeleton, then one row per accepted choice of language, build tool, layout, runtime dependency, or kind of program. **Choice** is `<Kind>: <choice>`. **Decided in** links every decision record that made or amended the choice, and nothing else |
| Pointer table | yes | One row per area of the paper, in the order of the abstract's level table |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add module** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step adds a module |
| **Change dependency** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes which module depends on which |
| **Set witness command** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes how the suite is run |
| **Set project fact** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes where tests live, how they are named, which languages the project uses, or the witness output format |
| **Set stack row** | [../../flows/step.md](../../flows/step.md) **Decide** step 7; [../../flows/amendment.md](../../flows/amendment.md) Step 7.2; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 11 |
| **Set process** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a process, its entry point, its port, its peer, or a launch script changes |
| **Set pointer table** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A6), when an area folder of the paper is added, renamed, or removed |

### Add module

**Edit.**

1. Add `| \`<folder>/\` | \`<build id>\` | \`<code root>\` | <role> |` in the build's order.  
2. Add the module's build file to the Build files table.  
3. Update the module-count sentence and the dependency diagram.  
4. If the module adds a language, apply **Set project fact** to **Declarations**.

### Change dependency

**Edit.**

1. Redraw the diagram lines that changed, and rewrite the exception sentences.

### Set witness command

**Edit.**

1. Replace the backticked command, and the alternate form, on the **Witness command** line.

### Set project fact

**Edit.**

1. Replace the text after the label on the **Tests**, **Declarations**, or **Output summary** line with the fact as it now is.

### Set stack row

**Before.** A decision record for the choice exists and is `accepted`.

**Edit.**

1. If the choice's kind already has a row, replace its **Choice** cell with `<Kind>: <choice>` as decided, and add the new record's link to its **Decided in** cell.  
2. Otherwise add `| <Kind>: <choice> | [ADR-0xx](../paperwork/decisions/ADR-0xx-<slug>.md) |` at the bottom of the Stack table.

**Result.** The row names the choice as it now stands, and every record that shaped it.

### Set process

**Before.** You have read the entry point of the process, and the script or process that starts it.

**Edit.**

1. Add, replace, or delete the process's row in the Processes table, so that every cell states the process as it now runs.  
2. Redraw the diagram lines that changed.  
3. If a launch script or the continuous-integration workflow changed, rewrite its sentence.  
4. If the process is built by a manifest of its own, add or replace that manifest's row in the Build files table.

**Result.** The Processes section names every process the project runs, and how each is started and reached.

### Set pointer table

**Edit.**

1. Make the pointer table hold one row per area of the paper, `| <question> | [<area>/](<area>/README.md) |`, in the order of the abstract's level table.

**Result.** Every area of the paper is reachable from the program page.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Each of the four project-fact lines exists exactly once |
| 2 | The witness command runs the whole suite. The **Declarations** pattern finds a known declaration in each listed folder. The **Tests** rule names an existing test file correctly |
| 3 | The module table matches the modules the build declares |
| 4 | The diagram matches the declared module dependencies |
| 5 | Every link in the Stack table resolves to a decision record whose status is `accepted` |
| 6 | Every build file on disk has a row in the Build files table, and every row's link resolves |
| 7 | The Processes table names every process a launch script or another process starts, and every Entry point link resolves |
| 8 | The pointer table has one row per area folder under `docs/architecture/` |

## Keep out

- The reasons for a stack choice: decision records.  
- A module's internals: its level and mechanism pages.
