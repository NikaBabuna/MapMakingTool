<!--
  File: docs/protocol/flows/global-docsync.md
  Purpose: Whole-tree synchronisation of documents and source — mandatory at Goal close
  Audience: Agents
  Update when: The checks, their order, or the fix authority change
-->

# Global docsync

**Register:** legal. The findings message uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** corrections to false statements, broken links, and missing index rows in standing documents, within the fix authority below, each through an operation of the blueprint that shapes the file. It also writes the findings log and the witness line on the invoking Step record.  
**Container:** always one Step under [step.md](step.md) whose approved Job names Global docsync. This flow never commits by itself. The invoking Step's Accept commit seals it.  
**Distinct from:** Step-local SYNC in [step.md](step.md), which covers only the files one Step touched. This flow covers the whole tree.  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md). Shelf map: [../environment/map.md](../environment/map.md). Structural rules: [../environment/quality.md](../environment/quality.md) Article 3. Shapes: [../blueprints/README.md](../blueprints/README.md).

## When

1. **Close goal** in [goal.md](goal.md) invokes it. This is mandatory at the close of every Goal; or  
2. An approved Step's Job names a whole-tree sync; or  
3. The human directs a whole-tree sync. In that case, the direction is carried out as the Job of a Step (Before, B2).

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | [reconcile.md](reconcile.md) has run in this chat and ended in Branch clean, or the human chose to keep unsealed changes that the invoking Step adopts | Run Reconcile first |
| B2 | A Step is `in progress`, and its record's Job names Global docsync | Stop. Propose a Step under [step.md](step.md) whose Job is Global docsync, or ask the human to add it to the Job of the Step now in progress |
| B3 | For a Goal close, every other planned Step of that Goal is `done` | Stop. Goal close is not yet lawful. Report which Steps are open |

## Fix authority

This flow **may** correct the following in any standing document, without any further approval, always through the blueprint operation named in the check:

| May fix | Example |
|---------|---------|
| A statement that the tree or the source proves false | A door says a folder has a page that it does not have |
| A relative link that does not resolve, when the correct target is certain | A link to a page that was moved, where the page now exists at one new path |
| A missing or stale row on an index or door, when the listed object exists | A Step record with no registry link |
| A Goal pointer on a door that differs from the Goal index | `AGENTS.md` names G-0yy while the index names G-0xx |

This flow **may not** do the following. When one is needed, record it as a finding with the action `needs approval`, and handle it under Step 16:

| May not | Route instead |
|---------|---------------|
| Move, rename, create, or delete a folder or a major document | [amendment.md](amendment.md), Restructure |
| Change source, including tests | A Step under [step.md](step.md), or [bugfix.md](bugfix.md) |
| Add new substance (a new rule, a new product claim, a new mechanism description) | [amendment.md](amendment.md) |
| Decide which of two authorities is right | [conflict-resolve.md](conflict-resolve.md) |
| Change a stored requirement, a claim, or a status line | [step.md](step.md) or [goal.md](goal.md) |

## Procedure (mandatory order)

Carry out every check. For each finding, write one row in the findings log (Step 15) at the moment it is found. A finding is fixed, or marked `needs approval`, before the next check begins.

### Step 1 — Inventory

1. List every `.md` file under `docs/`, tracked or untracked.  
2. Add the root doors (`README.md`, `AGENTS.md`, `.cursor/rules/protocol.mdc`).  
3. List every folder under `docs/` that contains at least one `.md` file.  
4. List every folder outside `docs/` that holds a tracked file, or a folder that does. Leave out the folders the Exempt folders section of `docs/architecture/conventions.md` lists (with every folder below a row that says so), and the places under **Never open** in `docs/navigation.md`. These are the code folders. Add the `README.md` of each code folder that has one to the file list of 1.1.  
5. For each file, note its blueprint, as named by [../blueprints/README.md](../blueprints/README.md) and the group doors.  
6. This inventory is the whole scope of Steps 2–14.

### Step 2 — Doors exist

1. Every folder from Steps 1.3 and 1.4 contains `README.md`. The only exempt folders are those Step 1.4 left out, by [../environment/quality.md](../environment/quality.md) 3.14 and the conventions page.  
2. A missing door is a `needs approval` finding under Restructure. It is not created silently, because a door states the folder's job.  
3. A folder from Step 1.4 that is of an exempt kind of quality 3.14, but is not listed on the conventions page, is a `needs approval` finding: the route is **Add exemption** of [../blueprints/architecture/conventions.md](../blueprints/architecture/conventions.md), not a new door.

### Step 3 — Doors list their children

1. For each door, list the `.md` files and subfolders beside it.  
2. Compare against the door's table, by its blueprint's **Check** ([../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md), [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md)).  
3. For each missing row, apply **Add child** (or **Add child row**). For each row whose target does not exist, apply **Remove child** (or **Remove child row**). Both are fixes.  
4. For each code door (the doors of Step 1.4), list every file and subfolder beside it, and run the **Check** of [../blueprints/doors/code-door.md](../blueprints/doors/code-door.md).  
5. A missing Contents row is a fix, by **Add entry**; a row whose target does not exist is a fix, by **Remove entry**; a name or link that a rename made false is a fix, by **Relink entry**, when the new name is certain from source. A part that the source proves false is a fix, by **Rewrite overview**. A door that lacks a required part, or is only a list of its files, is `needs approval`, because writing its Why and its wiring states the folder's job.

### Step 4 — Reachability

1. Every file from Step 1.1 is linked from its folder's door, or it is a numbered record linked from its index (`docs/paperwork/goals.md`, `docs/paperwork/steps.md`, `docs/paperwork/decisions.md`).  
2. An unreachable file is a finding. If the file's job fits the folder, fix it by Step 3.3. Otherwise mark it `needs approval`, because it is an orphan or is misplaced.

### Step 5 — Links resolve

1. In every file from Step 1, take every markdown link target that does not begin with `http:`, `https:`, `mailto:`, or `#`, and is not inside a code span or fence.  
2. Strip any `#anchor` part. Resolve the target relative to the folder of the file that contains the link.  
3. The resolved path must exist.  
4. Broken link: if exactly one existing file is the obvious target, fix it by the **Relink** operation of the file's blueprint (**Relink child**, **Relink entry**, **Relink row**, **Relink protocol**, **Relink pointer**, **Relink**). For a status cell on `docs/navigation.md` that has become false, apply **Correct status** of [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md). Otherwise mark it `needs approval`.

### Step 6 — Depth

1. Every path under `docs/` obeys the Depth table in [../environment/phase.md](../environment/phase.md).  
2. A violation is `needs approval` (Restructure).

### Step 7 — The Active Goal line is singular

1. Search the repository for the literal `**Active Goal:**`, excluding `docs/protocol/`.  
2. The only hit allowed is the line in `docs/paperwork/goals.md`.  
3. Any other hit is a fix: replace the sentence by the Goal pointer, by **Correct pointer** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md).  
4. At every place listed in **Where it appears** of [../blueprints/doors/goal-pointer.md](../blueprints/doors/goal-pointer.md), the pointer text agrees with the Active Goal line. Apply **Correct pointer** to any place that differs.

### Step 8 — Paperwork agreement

| # | Compare | Must agree on |
|---|---------|---------------|
| 8.1 | Each row of the table in `docs/paperwork/goals.md` against its Goal file | Name, and the value of **Status** |
| 8.2 | Each row of `docs/paperwork/steps.md` against its record under `docs/paperwork/steps/` | That the link exists. The row status equals the record's **Status** |
| 8.3 | Each Goal file's Planned Steps table against `docs/paperwork/steps.md` | The same Step ids, and the same statuses |
| 8.4 | Each Goal file's Progress table against its Planned Steps and Claims | The counts, and **Last Accept** |
| 8.5 | `docs/paperwork/decisions.md` against `docs/paperwork/decisions/` | One row per file. Numbers contiguous with no gap. Every link resolves |
| 8.6 | `docs/paperwork/roadmap.md` against `docs/paperwork/goals.md` | One row per Goal. Each status word equals the Goal's status |
| 8.7 | Each backlog row marked promoted against the Goal it names | The Goal file exists |

A difference in counts, links, or copied status words is a fix, made by the operation of the index's blueprint ([../blueprints/paperwork/](../blueprints/paperwork/README.md)): the Goal file or the Step record wins over the index copy ([conflict-resolve.md](conflict-resolve.md) R8). A difference between a Goal file and a Step record about whether something was Accepted is `needs approval`.

### Step 9 — Architecture and code against source

For every page under `docs/architecture/`:

1. Every path in backticks that names a repository file or folder exists.  
2. Every program name in backticks that the page presents as a declared unit (a type, function, or module) is declared in source. Search with the **Declarations** line of `docs/architecture/program.md`.  
3. The module table in `docs/architecture/program.md` matches the modules the build declares, and its four project-fact lines exist ([../blueprints/architecture/program.md](../blueprints/architecture/program.md) Check).  
4. Every ordered list that claims to be the order the code runs (the stage list of each level page, [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md) Form A) matches the order in the source it names. Open that source file and compare.  
5. A missing path or a missing type is a fix, by **Rewrite section** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md), when the rename is certain from source. Otherwise it is `needs approval`. A wrong order is `needs approval`, because it may be a defect in the source rather than in the paper. After approval, apply **Reorder stages** of [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md).  
6. Every mechanism page's `Code:` line links code doors, and each of them has a `###` for the page under Where each step happens, with one row per Procedure step it performs ([../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md) Check 9). A missing or stale step map is a fix, by **Set step map** of [../blueprints/doors/code-door.md](../blueprints/doors/code-door.md), when the member behind each step is certain from source. Otherwise it is `needs approval`.

For every code folder from Step 1.4, against `docs/architecture/conventions.md`:

7. The folder holds no more source files than the **Folder limit** line allows, counted as that line says.  
8. Every folder name, file name, and declared name in it follows the Names section for its language. Find declarations with the **Declarations** line of `docs/architecture/program.md`, and compare each name with the rule for its kind of name.  
9. Every source file begins with a header in the form the File headers section gives for its language, and no `Purpose:` line names a Goal, Step, or decision id.  
10. Every finding of 7–9 is `needs approval`, because fixing it changes source. The findings message proposes the Step that would fix it.

### Step 10 — Product shelf

For every page under `docs/product/`:

1. The page names no source path and no program type. The product shelf is for a person, not the program ([../environment/quality.md](../environment/quality.md) 3.2). Any hit is `needs approval`.  
2. Every "not built", "not yet", or "cannot yet" statement is still true after the Goals now `done`. Check it against the Result and Claims of every Goal closed since the page last changed. A statement that has become false is `needs approval`, because rewording a product promise is substance.

### Step 11 — Scope

1. Read **In scope** and **What it is not** in `docs/product/concept.md`.  
2. For each Step of the Goal being closed (or the invoking Step), the Job lies inside **In scope** and outside **What it is not**.  
3. Every row of the **Stack** table in `docs/architecture/program.md` links decision records that exist.  
4. A broken link is a fix, by **Set stack row** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md). Work outside scope is `needs approval` ([conflict-resolve.md](conflict-resolve.md) R6).

### Step 12 — Protocol pointers

1. Every flow named in Article 3 of [../core-workflow.md](../core-workflow.md) has a file under `docs/protocol/flows/` and a row in [README.md](README.md) in this folder. No file exists there without a row.  
2. Every blueprint, and every operation, that a flow cites exists under `docs/protocol/blueprints/`.  
3. Every blueprint's Operations index names only flow steps that exist and cite it.  
4. Every instrument named in [../brief.md](../brief.md) and in Article 3 of [../environment/map.md](../environment/map.md) exists.  
5. Independence ([../environment/quality.md](../environment/quality.md) 3.13): search `docs/protocol/` for the product name (the title of `docs/product/concept.md`), for every module folder in the module table of `docs/architecture/program.md`, for every area folder under `docs/architecture/`, and for every word in the table of `docs/product/glossary.md`, as whole words. Also search it for record ids: `G-`, `F-`, or `ADR-` followed by three digits. Placeholders such as `G-0xx` are not ids. Every hit that names the project, rather than using the word in its ordinary sense, is `needs approval`.  
6. A missing file or operation is `needs approval`. A stale row or pointer is a fix, by the door's or the page's **Relink** operation.

### Step 13 — Blueprint conformance

1. For every file in the inventory, run the **Check** section of its blueprint.  
2. For `docs/navigation.md`, measure before checking: list the folders on disk that hold dependencies, build output, version-control internals, or local tools, and count the lines of every tracked text file. A missing row is fixed by **Add heavy place**, a row for a place that no longer qualifies by **Remove heavy place**, and a code door of Step 1.4 with no row under **Code** by **Add code folder**, of [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md).  
3. A failed row that one of the fixes above covers is fixed by that operation. Any other failed row is `needs approval`, with the blueprint and the row number in the finding.

### Step 14 — Changelog and decisions for this Goal

1. For each Step of the Goal whose record ticks the **Changelog** Sync box with anything other than `N/A`, `docs/paperwork/changelog.md` contains a line naming that Step id.  
2. For each Step whose record ticks the **Decision** Sync box with anything other than `N/A`, `docs/paperwork/decisions.md` contains the ADR it names.  
3. The changelog contains the *Goal opened* line for this Goal.  
4. A missing changelog line is a fix, by **Insert missing line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md). A missing ADR is `needs approval`.

### Step 15 — Findings log

Apply **Add docsync log** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md) on the invoking Step record, with one row per finding, or `No findings.`

### Step 16 — Findings that need approval

1. If any row reads `needs approval`, stop before Step 17. Send the findings message (below).  
2. For each row, the human either approves a route (Amendment, Step, Bugfix, Conflict resolve) or accepts the finding as a known gap. Write the human's decision into that row of the findings log.  
3. A Goal close cannot proceed while an unaccepted `needs approval` row exists that falsifies a claim of that Goal.

### Step 17 — Witness

1. Run the witness command (the **Witness command** line of `docs/architecture/program.md`) from the repository root.  
2. It must be green. If it is red, the fixes in this flow broke a test that reads a document. Restore the text the test requires, or treat the case as a Conflict resolve (a test encodes a document fact, and the document became false). Never edit the test under this flow.  
3. Apply **Add witness line** (*Suite run*) of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md) on the invoking Step record.

## Human message — findings that need approval

```markdown
## Global docsync findings

**What I checked:** the whole docs tree, the root doors, and every code folder and its door, against the source, the paperwork, the conventions page, and each file's blueprint.

**Fixed without asking:** <count> — broken links, stale rows, and wrong Goal pointers. Listed on the Step record.

**Need your decision:**

| # | Where | What is false | What I suggest | Confidence |
|---|-------|---------------|----------------|------------|
| 1 | <path> | <plain words> | <route> | `certain` \| `probable` \| `uncertain` |

**Need from you:** a decision on each numbered row
```

## Done

- Steps 1–14 each ran over the whole inventory.  
- Every finding is fixed through a blueprint operation, or carries the human's decision.  
- The findings log and the witness line are on the invoking Step record.  
- The witness command is green.  
- Nothing was committed by this flow. Control returns to the invoking flow ([goal.md](goal.md) **Close goal**, or [step.md](step.md)).

## Not done

- A check was skipped or run over part of the tree.  
- A Goal was marked `done` while a door, an index, or an architecture page still describes an earlier state.  
- A `needs approval` item was fixed without approval.  
- A file was corrected without a blueprint operation.  
- A test was edited under this flow.  
- The findings log is missing from the invoking Step record.
