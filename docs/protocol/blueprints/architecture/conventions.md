<!--
  File: docs/protocol/blueprints/architecture/conventions.md
  Purpose: Shape of the code conventions page, and the operations that create and edit it
  Audience: Agents
  Update when: The shape of the code conventions page or one of its operations changes
-->

# Code conventions page

**Shapes:** `docs/architecture/conventions.md`  
**Register:** legal, plain · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the Conventions paragraph of the paper abstract ([abstract.md](abstract.md)) — how a page of the paper states a mechanism and cites code, not how code is written. The program page ([program.md](program.md)) — the project facts the protocol reads: where tests live, how a test file is named, the witness command, the stack. The source header ([../headers/source-header.md](../headers/source-header.md)) — the four header lines themselves; this page gives only their comment form per language.

How the code of one project is written: how each kind of name is spelled, which folder a new file goes into, how large a folder may grow, the comment form of a file header in each language, and which folders need no README. Every project governed by the protocol has this page at this path; what it says belongs to the project. The page is living: when a Step brings in something the page does not cover, that Step adds the convention, so every later agent follows it.

## Skeleton

```markdown
<!--
  File: docs/architecture/conventions.md
  Purpose: Code conventions — names, where a file goes, file headers, and the folders that need no README
  Audience: Agents and humans
  Update when: A convention is added or changed, or a language, a kind of file, or an exempt folder enters or leaves the project
-->

# Code conventions

How the code of this project is written: the names, the place of each file, the file headers, and the folders that need no README. Every Step that writes code follows this page. When a Step brings in something this page does not cover — a new language, a new kind of file or folder, or a new pattern — that Step adds the convention here, so the agents after it follow it too.

## Languages

| Language | Where it lives | Base convention |
|----------|----------------|-----------------|
| <language or kind of file> | <folders, from the repository root> | <the established convention of that language that this page follows, or `this page only`> |
…

## Names

### <Language>

| Kind of name | Rule | Example |
|--------------|------|---------|
| <kind of name> | <how it is spelled, and what word it uses> | `<a name that follows the rule>` |
…

…

## Where a file goes

<One to three sentences: each folder of code has one job, and how the folders of the code follow the areas of the implementation paper.>

| Language | Folder | What goes there |
|----------|--------|-----------------|
| <language> | `<folder or folder pattern>` | <the one job of that folder> |
…

**Folder limit:** a folder holds at most <n> source files, not counting <the files that are not counted>. A folder over the limit, or a folder that holds two jobs, is split by job.

## Tests

Where test files live and how a test file is named: the **Tests** line of [program.md](program.md).

| Language | Rule | Example |
|----------|------|---------|
| <language> | <how one test case is named and titled> | `<a test name that follows the rule>` |
…

## File headers

Every source file begins with the four lines of the source header, in its language's comment form.

| Language or kind of file | Form | First line |
|--------------------------|------|------------|
| <language or kind of file> | <comment form, or `none — <reason>`> | `<the first line of a header in that form>` |
…

<Rules for the header's content: a Purpose line states one responsibility and never a record id or history; where record ids may appear in code.>

## Exempt folders

These folders need no README.

| Folder | Kind | Reason |
|--------|------|--------|
| `<path or pattern, from the repository root>` | <build output \| dependency install \| tool cache \| wrapper internals \| layout segment \| generated> | <why a README would not help there> |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | As in the Skeleton |
| Title | yes | `# Code conventions` |
| Opening paragraph | yes | As in the Skeleton, word for word |
| Languages | yes | `## Languages`, then the table. One row per language or kind of file present in the project's code, including build files and scripts. **Base convention** names the established convention the page follows for that language, or `this page only` |
| Names | yes | `## Names`, then one `### <Language>` subsection per row of Languages that has names of its own, in the order of Languages. Each subsection is one table. One row per kind of name (for example: folder, file, type, function, variable, constant, test). **Rule** states the spelling and the kind of word (noun, verb, question). **Example** is a name that follows the rule |
| Where a file goes | yes | `## Where a file goes`, one to three sentences, the table, then the **Folder limit** line. One table row per kind of folder: its path or pattern and its one job. The limit is one whole number, followed by the files it does not count |
| Tests | yes | `## Tests`, the pointer sentence of the Skeleton, then one row per language that has tests. The page does not restate where tests live or how a test file is named |
| File headers | yes | `## File headers`, the sentence of the Skeleton, the table, then the content rules. One row per language or kind of file. **Form** is the comment syntax, or `none — <reason>` for a kind of file that allows no comment. The content rules say that a Purpose line carries no record id and no history, and name the only places in code where a record id may appear |
| Exempt folders | yes | `## Exempt folders`, the sentence of the Skeleton, then one row per folder or folder pattern. **Kind** is exactly one of the six values of the Skeleton. `layout segment` is a folder that holds no file of its own, only folders, and exists for the layout or namespace of a language or build tool. **Reason** is one sentence |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.2, when the project has no code conventions page |
| **Add convention** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a language, a kind of file or folder, or a pattern enters the project and no row covers it |
| **Change convention** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when the human approves a different rule |
| **Add exemption** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when a folder of an exempt kind appears |
| **Remove exemption** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 3.1 (class A5), when an exempt folder is gone, or no longer of an exempt kind |

### Create

**Before.** `docs/architecture/conventions.md` does not exist. The human approved the conventions.

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md), as in the Skeleton.  
2. Copy the Skeleton. Keep the title and the opening paragraph word for word.  
3. Languages: one row per language or kind of file in the project's code.  
4. Names: one `### <Language>` subsection per language with names of its own, each with one row per kind of name, each row with an example.  
5. Where a file goes: the sentences, one row per kind of folder, and the **Folder limit** line with its number.  
6. Tests: one row per language with tests.  
7. File headers: one row per language or kind of file, then the content rules.  
8. Exempt folders: one row per exempt folder or pattern.

**Result.** The project has its code conventions page.

### Add convention

**Before.** The human approved the new convention, as part of the Step that brings the new thing in.

**Edit.**

1. If the language or kind of file has no row in Languages, add `| <language> | <folders> | <base convention> |` at the bottom of the Languages table, and add `### <Language>` with the table header of the Skeleton at the end of Names.  
2. Add the row to the table the convention belongs to — ``| <kind of name> | <rule> | `<example>` |`` in that language's Names subsection, ``| <language> | `<folder>` | <job> |`` in Where a file goes, ``| <language> | <rule> | `<example>` |`` in Tests, or ``| <language or kind of file> | <form> | `<first line>` |`` in File headers — below the row it relates to, or at the bottom.

**Result.** The new thing has a written convention.

### Change convention

**Before.** The human approved the new rule. The rows it replaces are known.

**Edit.**

1. In the row of that convention, replace the Rule, Form, or What goes there cell with the approved rule, and the Example or First line cell with a name that follows it. For the folder limit, replace the number or the list of files not counted on the **Folder limit** line.

**Result.** The row states the rule now in force.

### Add exemption

**Before.** The folder is of one of the six kinds.

**Edit.**

1. Add ``| `<path or pattern>` | <kind> | <reason> |`` at the bottom of the Exempt folders table.

**Result.** The folder is listed as needing no README.

### Remove exemption

**Before.** The folder no longer exists, or it now holds files of its own that need a README.

**Edit.**

1. Delete that folder's row.

**Result.** Every row names a folder that is still exempt.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every language named by the **Declarations** line or the Stack of `docs/architecture/program.md` has a Languages row |
| 2 | Every Languages row with names of its own has a Names subsection, and every Names row has an example that follows its rule |
| 3 | The **Folder limit** line gives one whole number and the files it does not count |
| 4 | The Tests section links `program.md` and restates neither where tests live nor how a test file is named |
| 5 | Every File headers row gives a form or `none — <reason>`, and the content rules name where record ids may appear |
| 6 | Every Exempt folders row has exactly one of the six kinds |
| 7 | The page names no record id and tells no history |

## Keep out

- Where tests live and how a test file is named: the **Tests** line of `docs/architecture/program.md`.  
- The four header lines and their meaning: [../headers/source-header.md](../headers/source-header.md).  
- The shape of a folder's README: [../doors/folder-door.md](../doors/folder-door.md).  
- How a page of the paper states a mechanism and cites code: the Conventions paragraph of the paper abstract.  
- A history of renames: the changelog.
