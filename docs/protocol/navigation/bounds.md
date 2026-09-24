<!--
  File: docs/protocol/navigation/bounds.md
  Purpose: What to open for each kind of turn, what to leave closed, and when to stop reading
  Audience: Agents
  Update when: A flow's open set, the startup read set, or a stop signal changes
-->

# Bounds

There are two failure modes. One is reading thirty files to discover which one mattered. The other is reading none and guessing. Both are navigation failures.

**Rule.** Open the page the flow names, or the page a door or pointer names. Read it by the ladder in [reading.md](reading.md). Stop when the question is answered. Open nothing that the open set below does not name, unless a named page points you to it for the question at hand.

## Startup read set

[../core-workflow.md](../core-workflow.md) Article 1 begins every chat by reading the protocol. "The protocol" there means exactly this set, in this order. It is about 670 lines, of a protocol of about 7,000.

| Order | Open | How |
|------:|------|-----|
| 1 | [../brief.md](../brief.md) | Whole |
| 2 | [../core-workflow.md](../core-workflow.md) | Whole |
| 3 | [../environment/core-definition.md](../environment/core-definition.md) | Whole |
| 4 | [../environment/style.md](../environment/style.md) | Whole. Every message to the human depends on it |
| 5 | [../flows/reconcile.md](../flows/reconcile.md) | Whole. Then run it. Its reads are searches: the Active Goal line, the `in progress` rows, the `**Status:**` lines, `git log` subjects, and `git status --porcelain` |
| 6 | [../flows/rollback.md](../flows/rollback.md) | Only if Reconcile ended in Branch torn |
| 7 | [../flows/status-report.md](../flows/status-report.md) and [../blueprints/messages/status-report.md](../blueprints/messages/status-report.md) | Whole. Then its Gather order: the concept's title and first paragraph (a range, not the page), and the Active Goal file's Planned Steps and Progress sections (headings, then those ranges) |

**Leave closed at startup:** every other flow, every other blueprint, quality, correctness, phase, map, the architecture shelf, the product shelf beyond the concept's first paragraph, every Step record, and all source.

## Open set by flow

When the human speaks and [../core-workflow.md](../core-workflow.md) Article 2 selects a flow, open that flow's set. Read each item by the ladder: a large flow one algorithm or stage at a time, and a blueprint by its key block plus the one operation you apply.

| Flow | Open | May also open, only when | Leave closed |
|------|------|--------------------------|--------------|
| Reconcile | As in the startup set | — | Record bodies. Read status lines by search |
| Rollback | [../flows/rollback.md](../flows/rollback.md) | — | Everything else. Rollback reads git, not documents |
| Status report | As in the startup set, order 7 | — | Architecture, flows, and source |
| Investigate | [../flows/investigate.md](../flows/investigate.md); the evidence items written in its Step 2 | An item you add to the Evidence list first | Anything not on the Evidence list |
| Explain | [../flows/explain.md](../flows/explain.md); [../environment/map.md](../environment/map.md) if the owning shelf is unclear; that shelf's door; the one owning page | Source, when the question is about behaviour and the paper is silent ([code.md](code.md)) | Sibling pages of the owning page |
| Audit | [../flows/audit.md](../flows/audit.md); the paths in the direction; the criteria sections of [../environment/quality.md](../environment/quality.md) and [../environment/correctness.md](../environment/correctness.md) (or the human's criteria) | A sibling file a criterion must compare against (e.g. a link target) | Anything outside the direction |
| Goal flow | The one algorithm section of [../flows/goal.md](../flows/goal.md) you are running; the Active Goal file; for each write, the key block and the named operation of its blueprint | [../blueprints/paperwork/goal.md](../blueprints/paperwork/goal.md) Skeleton, when opening a Goal | Other Goals' files, Step records, the architecture shelf |
| Step flow | The stage of [../flows/step.md](../flows/step.md) you are in; the Step record; the plan's files, by member ([code.md](code.md)); the test files the Test map names; at SYNC, the one owning page per Ties row; at WITNESS, the summary lines of the witness output | [../environment/correctness.md](../environment/correctness.md) Article 3 or 4 at WITNESS; the last Accepted Step's Witness section for counts | Other Step records, other modules, and pages that SYNC does not name |
| Bugfix | [../flows/bugfix.md](../flows/bugfix.md); the confirmation evidence; the failing test; the member under suspicion; plus the Step flow set | Callers of that member, as a file list first | The rest of the module |
| Amendment | [../flows/amendment.md](../flows/amendment.md); each target file; the reference search results (matching lines only); the key block and named operations of each target's blueprint | [../environment/phase.md](../environment/phase.md) for class A6 | Files the search did not name |
| Global docsync | [../flows/global-docsync.md](../flows/global-docsync.md), one check at a time; for each check, the listings and searches it names | A file body, only where a check needs content that no search can show | Nothing is off limits, but nothing is read whole without a reason. Use R12 of [reading.md](reading.md): scripts and searches |
| Conflict resolve | [../flows/conflict-resolve.md](../flows/conflict-resolve.md); the two quoted passages and their surrounding section | — | The rest of both files |

## Always closed

These are never opened, listed, or searched unless the human asks about them by name. Their concrete paths in this repository are the **Heavy places** section of [../../navigation.md](../../navigation.md).

| Class | Why | Use instead |
|-------|-----|-------------|
| Installed dependencies | Thousands of files that are not this project's code | The lock file, searched for one package name |
| Build output | Generated from source. Reading it tells you nothing the source does not | The source. For a failing test, its summary line in the witness output |
| Version-control internals | Binary and duplicated | git commands in summary form (R11 of [reading.md](reading.md)) |
| Lock files | Thousands of generated lines | Search for the one package name |
| Local tool installs | Not part of the repository | — |
| Generated fixtures (e.g. golden files) | Long data, not prose | Open only when a test on that fixture fails, and read only the failing region |

## Stop signals

When one of these happens, stop reading, and do what the right column says.

| # | Signal | Do |
|---|--------|----|
| S1 | Five files opened for one question, and it is still not answered | Stop. Return to [pointers.md](pointers.md) and restate the question. If it is still unclear, ask the human which part of the tree owns it |
| S2 | The answer would need more than ten files read | It is a sweep. Use search or a script (R12 of [reading.md](reading.md)), not reads |
| S3 | A single read returned more than 300 lines you did not plan for | You skipped a rung. Next time take headings first and read a range |
| S4 | A command is about to print more than 100 lines | Redirect it to a scratch file outside the repository, and search that file (R10 of [reading.md](reading.md)) |
| S5 | You are about to re-open a file you already hold | Don't, unless it changed or has left your context (R4 of [reading.md](reading.md)) |
| S6 | You want to open a page "to understand the project" | Don't. The Status report's sources already told you what the project is. Open a page only for a question |
