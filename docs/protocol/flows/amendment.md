<!--
  File: docs/protocol/flows/amendment.md
  Purpose: Change a standing document, a protocol instrument, the entrance, a door, scope, or structure so that every dependent stays true
  Audience: Agents
  Update when: The amendment procedure, its sub-procedures, or the dependents it must check change
-->

# Amendment

**Register:** legal. Every message to the human uses the understanding register of [../environment/style.md](../environment/style.md).  
**Writes:** only the files on the approved change list (Step 3), each through an operation of the blueprint that shapes it. Find that blueprint from [../blueprints/README.md](../blueprints/README.md).  
**Container:** always one Step under [step.md](step.md). This flow never commits by itself. The container Step's Accept commit seals the amendment.  
**Sub-procedures in this file:** **Restructure** (Step 6), **Scope** (Step 7), **Protocol pointers** (Step 8). **Decide** lives in [step.md](step.md).  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md). Phase and depth: [../environment/phase.md](../environment/phase.md).

## When

Invoke this flow when one or more of the following targets is to change. Record every class that applies. They are used in Steps 6–8.

| Class | Target | Paths |
|-------|--------|-------|
| A1 | A protocol instrument, flow, navigation page, or blueprint | Any file under `docs/protocol/` |
| A2 | The entrance or an agent door | `docs/README.md`, `docs/navigation.md`, `AGENTS.md`, `README.md`, `.cursor/rules/protocol.mdc` |
| A3 | A folder door | The `README.md` of any landmark folder |
| A4 | Scope | The one line, **In scope**, and **What it is not** of `docs/product/concept.md` |
| A5 | A standing product or architecture page, when the change is not the Sync of a source change | Any file under `docs/product/` or `docs/architecture/` |
| A6 | Structure | A folder or a major document is created, moved, renamed, or deleted |

Do **not** invoke this flow for:

| Change | Owner |
|--------|-------|
| A Step record, the Step registry, a Goal file, the Goal index, the roadmap, the backlog | [step.md](step.md), [goal.md](goal.md) |
| A requirement already stored on a Step record | [step.md](step.md), **Amend step requirements** |
| The architecture or product page that a source Step's Sync must update | [step.md](step.md), SYNC |
| A line appended to the changelog or the decision index | The flow that appends it |

## Before

Every row must hold before Step 3. If a row fails, take the action in the last column and do not continue past it.

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | [reconcile.md](reconcile.md) has run in this chat and ended in Branch clean, or the human chose to keep unsealed changes, or the human has directed this Amendment as repair after a Rollback | Run Reconcile first |
| B2 | The human has stated what should change. A wish ("clean up the docs") is not a statement of change | Ask what should change, under style Article 5 (extraction) |
| B3 | [../environment/phase.md](../environment/phase.md) permits the change. In `alpha`, structure may be created, moved, and renamed. In `beta`, a structural change needs a decision record. In `prod`, it also needs a migration note | Stop. Tell the human which phase rule blocks it |

## Procedure (mandatory order)

### Step 1 — Classify

1. Assign every applicable class from **When** (A1–A6).  
2. If no class applies, this is the wrong flow. Stop and classify again under [../core-workflow.md](../core-workflow.md) Article 2.

### Step 2 — Establish the container Step

Check the cases in order and take the first one that matches.

| Case | Situation | Action |
|------|-----------|--------|
| 2a | A Step is `in progress` on `docs/paperwork/steps.md`, and its record's Job names this change | That Step is the container. Go to Step 3 |
| 2b | A Step is `in progress`, and its Job does not name this change | Do not widen the Step silently. Ask the human to choose: (i) amend that Step's Job under **Amend step requirements** in [step.md](step.md), or (ii) wait until that Step is closed. Await the answer |
| 2c | No Step is `in progress`, and `docs/paperwork/goals.md` names an Active Goal | A new Step is needed. Type `Documentation` if only documents change. Type `Cleanup` if the change only removes or tidies, or if any test file must change (see Step 4.3). Run [step.md](step.md) from SELECT. Build the proposal from the change list in Step 3. If the Active Goal's Planned Steps table does not list this Step, the proposal also carries **Amend goal** ([step.md](step.md) SELECT step 2) |
| 2d | No Step is `in progress`, and no Goal is active | Stop. Tell the human that an Amendment needs a Step, and a Step needs a Goal. Offer to negotiate a Goal under [goal.md](goal.md). Await |

For case 2c, Steps 3 and 4 are carried out *before* the proposal, because the proposal must contain the change list. No file is edited until [step.md](step.md) STORE and MARK have finished.

### Step 3 — Build the change list

Build the list before editing any file.

1. For each target the human named, write one row:

   | Path | Action | Blueprint and operation | What changes |
   |------|--------|-------------------------|--------------|
   | `<path>` | `edit` \| `create` \| `move from <old path>` \| `rename from <old path>` \| `delete` | `<group>/<kind>.md` → **<Operation>** | `<one sentence>` |

   The blueprint is the one [../blueprints/README.md](../blueprints/README.md) names for that path. If no operation of that blueprint makes the change, add a row *before* it: path `docs/protocol/blueprints/<group>/<kind>.md`, action `edit`, blueprint and operation `protocol/blueprint.md` → **Add operation**. The new operation is then used by the row after it.

2. For every path whose action is `move`, `rename`, or `delete`, and for every heading that other files link to by anchor, find every reference to that path:
   1. Search the whole repository for the file name and for its path relative to the repository root. Exclude every path listed under **Never open** in `docs/navigation.md`.  
   2. Whatever the search returns, open and check these by hand: `docs/navigation.md`, `docs/README.md`, the door of the file's current folder, the door of its new folder, `docs/protocol/environment/map.md`, `docs/protocol/brief.md`, `docs/protocol/core-workflow.md`, every file under `docs/protocol/blueprints/` and `docs/protocol/flows/`, `AGENTS.md`, `README.md`, `.cursor/rules/protocol.mdc`, and every test file (where tests live is the **Tests** line of `docs/architecture/program.md`).  
   3. Add each hit as a row with action `fix reference`, and the **Relink** operation of that file's blueprint (**Relink child**, **Relink row**, **Relink protocol**, **Relink pointer**, **Relink**, or, for a flow or instrument, **Amend step** / **Amend article**).
3. For class A1, also search `docs/protocol/` for every defined term the change renames or removes, and for every citation of an Article, rule, or step number the change touches. Add each hit as a row with action `fix term`, applying the [../blueprints/protocol/instrument.md](../blueprints/protocol/instrument.md) **Citation rule**.  
4. Keep the list in the proposal message. After STORE, the Step record carries it under `### Change list`.

### Step 4 — Check the list against the approval

1. Every change the human approved appears on the list.  
2. Nothing appears on the list that the human did not approve, other than rows with action `fix reference` or `fix term`.  
3. If a row touches a test file (by the **Tests** line of `docs/architecture/program.md`), the container Step touches source. It cannot be `Documentation`. It is `Cleanup`, and [../environment/correctness.md](../environment/correctness.md) Article 3 applies. A test that opens a moved path is retargeted to the new path. It is never deleted, skipped, or softened.  
4. If the list needs a change the human did not approve, stop. Show the extra rows and ask. Do not edit.

### Step 5 — Edit in safe order

Apply each row by its blueprint operation, in this order, so that at no moment does a door point at a missing path:

1. Create new folders. Give each landmark folder its door by **Create door** of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md).  
2. Create new files by the **Create** operation of their blueprint. For moved files, use `git mv` for tracked files so that history follows the file, then apply **Update path** of [../blueprints/headers/document-header.md](../blueprints/headers/document-header.md).  
3. Apply every `edit`, `fix reference`, and `fix term` row.  
4. Apply `delete` rows last.  
5. Preserve historical prose in paperwork (changelog lines, ADR text, Step records, Goal files). Update only their links, so that the links resolve. Do not rewrite what a record says happened.  
6. The operations a change-list row may name, by class:

   | Class | File | Operations |
   |-------|------|------------|
   | A1 | An instrument | **Add article**, **Amend article**, **Repeal article**, **Add rule**, **Set phase** of [../blueprints/protocol/instrument.md](../blueprints/protocol/instrument.md) |
   | A1 | A flow page | **Create flow**, **Amend step**, **Add step** of [../blueprints/protocol/flow.md](../blueprints/protocol/flow.md) |
   | A1 | A navigation page | **Add pointer**, **Relink pointer**, **Add bound**, **Add reading rule**, **Add search pattern**, **Add walk**, **Relink walk**, **Add open-set row**, **Amend open set** of [../blueprints/protocol/navigation-page.md](../blueprints/protocol/navigation-page.md) |
   | A1 | A blueprint | **Create blueprint**, **Add operation**, **Change part** of [../blueprints/protocol/blueprint.md](../blueprints/protocol/blueprint.md) |
   | A2 | An entrance or root door | The operations of [../blueprints/doors/docs-index.md](../blueprints/doors/docs-index.md), [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md), [../blueprints/doors/agent-door.md](../blueprints/doors/agent-door.md), [../blueprints/doors/cursor-rule.md](../blueprints/doors/cursor-rule.md), or [../blueprints/doors/repo-readme.md](../blueprints/doors/repo-readme.md) |
   | A3 | A folder door | **Create door**, **Add child**, **Remove child**, **Relink child** of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md) |
   | A4 | Scope | The scope operations of [../blueprints/product/concept.md](../blueprints/product/concept.md) (Step 7) |
   | A5 | The concept | **Amend facet**, **Add facet** of [../blueprints/product/concept.md](../blueprints/product/concept.md) |
   | A5 | The journeys | **Amend journey**, **Add not-built row** of [../blueprints/product/journeys.md](../blueprints/product/journeys.md) |
   | A5 | The domain glossary | **Amend word**, **Remove word** of [../blueprints/product/glossary.md](../blueprints/product/glossary.md) |
   | A5 | The style guide | **Add area** of [../blueprints/product/style-guide.md](../blueprints/product/style-guide.md) |
   | A5 | A wiki page | **Create page**, **Amend rule** of [../blueprints/product/wiki-page.md](../blueprints/product/wiki-page.md) |
   | A5 | The paper abstract | **Add level**, **Change question**, **Set conventions** of [../blueprints/architecture/abstract.md](../blueprints/architecture/abstract.md) |
   | A6 | The paper abstract | **Relink level** of [../blueprints/architecture/abstract.md](../blueprints/architecture/abstract.md) |
   | A5 | The program page | **Set process** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md) |
   | A6 | The program page | **Set pointer table** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md) |
   | A5, A6 | A level page | **Create**, **Rewrite** of [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md) |
   | A5 | The paper glossary | **Add symbol**, **Amend symbol**, **Remove symbol** of [../blueprints/architecture/glossary.md](../blueprints/architecture/glossary.md) |
   | A5 | The open questions | **Add question** of [../blueprints/architecture/open-questions.md](../blueprints/architecture/open-questions.md) |
   | A5 | A mechanism page | **Relink** of [../blueprints/architecture/mechanism-page.md](../blueprints/architecture/mechanism-page.md) |
   | any | Any document header | **Update path**, **Update purpose** of [../blueprints/headers/document-header.md](../blueprints/headers/document-header.md) |

   A row that needs an operation not in this table follows Step 3.1: the blueprint gains it by **Add operation** first.

### Step 6 — Restructure (only if A6 applies)

1. **Depth.** Before creating a path, check it against the Depth table in [../environment/phase.md](../environment/phase.md). If the path is not allowed, stop and ask.  
2. **Doors.** Every landmark folder that was created has a door (Step 5.1). On the door of each parent folder, apply **Add child** for each new child and **Remove child** for each removed child, of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md). For an architecture area or chapter, use the level-page and abstract blueprints instead: **Add child row** of [../blueprints/architecture/level-page.md](../blueprints/architecture/level-page.md), **Add level** of [../blueprints/architecture/abstract.md](../blueprints/architecture/abstract.md).  
3. **Entrance.** On `docs/navigation.md`, apply **Add row**, **Remove row**, or **Add shelf section** of [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md). If a top-level folder under `docs/` appeared, disappeared, or changed job, apply **Add shelf**, **Remove shelf**, or **Change role** of [../blueprints/doors/docs-index.md](../blueprints/doors/docs-index.md).  
4. **Map.** If a shelf or a standing-document role changed, apply **Amend article** of [../blueprints/protocol/instrument.md](../blueprints/protocol/instrument.md) to the matching Article of `docs/protocol/environment/map.md`.  
5. **Changelog.** Apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Step structure change*, naming the container Step.  
6. **Decision.** Run **Decide** in [step.md](step.md) if either of these holds: a reader could not have guessed the new location from the tree and its doors, or the phase is `beta` or later. The changelog line then ends with ` ADR-0xx.`  
7. **Old path.** Repeat the search from Step 3.2 for each old path. The only hits allowed are historical prose in paperwork. Fix every other hit.

### Step 7 — Scope (only if A4 applies)

1. Edit `docs/product/concept.md` before any other file of the work that needs the new scope, by **Add in-scope area**, **Amend in-scope area**, **Add exclusion**, **Remove exclusion**, or **Change one line** of [../blueprints/product/concept.md](../blueprints/product/concept.md). If the one line changed, also apply **Sync one line** of [../blueprints/doors/repo-readme.md](../blueprints/doors/repo-readme.md).  
2. If the change is technical (a language, a build tool, a module split, a new kind of program, a new runtime dependency, or a Stack row), run **Decide** in [step.md](step.md), then apply **Set stack row** of [../blueprints/architecture/program.md](../blueprints/architecture/program.md) to `docs/architecture/program.md`.  
3. Apply **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Scope change*.  
4. Work that depends on the new scope does not begin until step 1 is saved to disk.

### Step 8 — Protocol pointers (only if A1 applies)

| If the change… | Then also apply |
|----------------|-----------------|
| Adds, removes, or renames a standard (quality, correctness, style, core-workflow) | **Amend article** of [../blueprints/protocol/instrument.md](../blueprints/protocol/instrument.md) on the standards table of [../brief.md](../brief.md) and on Article 3 of [../environment/map.md](../environment/map.md) |
| Adds, removes, or renames an environment page | **Add child** / **Remove child** of [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md) on [../environment/README.md](../environment/README.md); **Amend article** on Article 3 of [../environment/map.md](../environment/map.md), and on Article 5 of [../core-workflow.md](../core-workflow.md) if that page is a lower instrument |
| Adds, removes, or renames a flow | **Create flow** of [../blueprints/protocol/flow.md](../blueprints/protocol/flow.md); **Amend article** on Articles 2 and 3 of [../core-workflow.md](../core-workflow.md); **Add child** / **Remove child** on [README.md](README.md) in this folder; **Add open-set row** or **Amend open set** of [../blueprints/protocol/navigation-page.md](../blueprints/protocol/navigation-page.md) on [../navigation/bounds.md](../navigation/bounds.md) |
| Changes which pages a flow reads, or the startup sequence | **Amend open set** of [../blueprints/protocol/navigation-page.md](../blueprints/protocol/navigation-page.md) on [../navigation/bounds.md](../navigation/bounds.md) |
| Adds, removes, or renames a blueprint | **Create blueprint** of [../blueprints/protocol/blueprint.md](../blueprints/protocol/blueprint.md); **Add child** / **Remove child** on the group door; **Amend step** on every flow that must cite it |
| Changes the meaning of a defined term | **Amend article** on [../environment/core-definition.md](../environment/core-definition.md), and every `fix term` row from Step 3.3 |
| Changes the phase | **Set phase** of [../blueprints/protocol/instrument.md](../blueprints/protocol/instrument.md); **Set phase** of [../blueprints/doors/doc-map.md](../blueprints/doors/doc-map.md); **Set status** of [../blueprints/doors/repo-readme.md](../blueprints/doors/repo-readme.md); **Add line** of [../blueprints/paperwork/changelog.md](../blueprints/paperwork/changelog.md), kind *Phase change* |
| Moves the global prompt or the protocol door | **Relink protocol** of [../blueprints/doors/agent-door.md](../blueprints/doors/agent-door.md) and of [../blueprints/doors/cursor-rule.md](../blueprints/doors/cursor-rule.md) |

Protocol text stays in the legal register ([../environment/style.md](../environment/style.md) Article 2).

### Step 9 — Verify

1. Every `create`, `edit`, `move`, and `rename` target exists. Every `delete` target does not.  
2. Run the **Check** of the blueprint of every file created or edited. Every row must hold.  
3. In every file created or edited, every relative link resolves to an existing file.  
4. Run the witness command (the **Witness command** line of `docs/architecture/program.md`) from the repository root. It must be green. If it is red because a test opens a path this Amendment changed, apply Step 4.3 and run it again. Do not delete, skip, or soften any test.  
5. On the container Step record, apply **Tick sync box** of [../blueprints/paperwork/step.md](../blueprints/paperwork/step.md) to each box this Amendment satisfied (Ties, Entrance, Changelog, Decision), or with `N/A: <reason>`.

### Step 10 — Return to the container Step

Continue [step.md](step.md) at WITNESS. The Amendment is sealed only when that Step's Accept commit exists.

## Human message — Amendment proposal

When case 2c applies, this skeleton is part of the [step.md](step.md) proposal. Otherwise it is sent alone. Every row carries a confidence label.

```markdown
## Amendment proposal

**What I want to change:** <plain words>

**Why:** <the problem it fixes>

**Files:**

| Path | Action | What changes | Confidence |
|------|--------|--------------|------------|
| <path> | <action> | <sentence> | `certain` \| `probable` \| `uncertain` |

**References I will also fix:** <paths, or none>

**Container Step:** <F-0xx, existing | F-0xx, new, type <type>, under G-0xx>

**Not changing:** <what stays as it is>

**Need from you:** approval of the file list<, and of adding F-0xx to G-0xx>
```

## Done

- Every row of the change list is applied through its blueprint operation, in the Step 5 order.  
- Every written file passes its blueprint's **Check**.  
- Disk, entrance, doors, map, and dependents describe the same tree. No live reference names a removed path.  
- The witness command is green.  
- The container Step record lists the change and its Sync boxes.  
- Control has returned to [step.md](step.md). Nothing was committed by this flow.

## Not done

- A file was edited before the container Step was `in progress`.  
- A file was edited without a blueprint operation.  
- A path was removed and a door, map, flow, blueprint, or test still names it.  
- A test was deleted, skipped, or softened to make the suite green.  
- A row that the human did not approve was applied (other than `fix reference` or `fix term`).  
- This flow made a commit of its own.  
- Historical paperwork prose was rewritten rather than only relinked.
