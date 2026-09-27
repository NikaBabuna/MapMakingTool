<!--
  File: docs/protocol/navigation/walks.md
  Purpose: Worked examples of reading a repository under this protocol at low cost, one question at a time
  Audience: Agents
  Update when: A walked path changes (a protocol page moves, a door changes), or a new common walk is added
-->

# Walks

Each walk is one common question, followed through the doors with the rules of [reading.md](reading.md). The **Reads** column says how much enters your context at each action. Protocol page sizes are approximate. Project pages are counted in units (a line, a section, a page), because their sizes belong to the project.

## W1 — A new chat starts

**Question.** Is anything torn, and what should I tell the human?

| # | Action | Reads |
|--:|--------|-------|
| 1 | Read the startup set of [bounds.md](bounds.md), orders 1–5, whole | ~530 lines of protocol |
| 2 | Search `docs/paperwork/goals.md` for `**Active Goal:**` | 1 line |
| 3 | Search `docs/paperwork/steps.md` for `\| in progress \|` | 0–1 lines |
| 4 | Search `docs/paperwork/steps/` for `^\*\*Status:\*\*` | 1 line per record |
| 5 | `git log --format="%h %s"`, reading only until the first `Accept F-` or `Seal: ` subject; then `git status --porcelain` | A few lines |
| 6 | If clean: read the Status report flow and its blueprint | ~140 lines of protocol |
| 7 | Read the title and first paragraph of `docs/product/concept.md` | 1 short range |
| 8 | In the Active Goal file, list headings, then read Planned Steps and Progress | 2 sections |

**Total:** under 1,000 lines. Reading the whole protocol instead is about 7,000 lines, before a single project page.

## W2 — Where does the code do X?

| # | Action | Reads |
|--:|--------|-------|
| 1 | Take the module from the module table of `docs/architecture/program.md` | 1 table |
| 2 | Take the area from the level table of `docs/architecture/README.md` | 1 table |
| 3 | Open `docs/architecture/<area>/README.md`, and take the stage or row that names X's mechanism | 1 page |
| 4 | Open that mechanism page. If its sections answer the question, stop | 1 page |
| 5 | Open the door on the page's `Code:` line, and take the step's row from **Where each step happens**: the member and its file. Search that file for the member, using the **Declarations** line of the program page | 1 table row, 1 line |
| 6 | Read from that line to the end of the member | 1 member |

**Total:** three short pages, one row of a door, and one member. Listing and opening the module instead costs every file in it.

## W3 — What did Step F-0xx require?

| # | Action | Reads |
|--:|--------|-------|
| 1 | The path follows from the id: `docs/paperwork/steps/F-0xx.md`. No search needed | — |
| 2 | List its headings | 1 line per heading |
| 3 | Read the requirements section and the test map section. Records written before the current blueprint may name them differently; take them from the heading list | 2 sections |

## W4 — Add a changelog line during a Step's SYNC

| # | Action | Reads |
|--:|--------|-------|
| 1 | The Step flow names **Add line** of `docs/protocol/blueprints/paperwork/changelog.md`. Read that blueprint's key block and its **Line kinds** section | ~30 lines of protocol |
| 2 | Read the first 20 lines of `docs/paperwork/changelog.md`, which hold the header and the newest lines | 20 lines |
| 3 | Insert the line. Do not read the older history | — |

## W5 — Run the tests

| # | Action | Reads |
|--:|--------|-------|
| 1 | Search `docs/architecture/program.md` for `**Witness command:**` and `**Output summary:**` | 2 lines |
| 2 | Run the command, sending all output to a scratch file outside the repository | Nothing |
| 3 | Search the scratch file for the **Output summary** patterns | The summary and failure lines |
| 4 | Only if something failed: read the failing test ([code.md](code.md) steps 6–7) | 1 member |

A full build log can put thousands of lines into context. The summary is usually a few dozen.

## W6 — Is a new feature in scope?

| # | Action | Reads |
|--:|--------|-------|
| 1 | List the headings of `docs/product/concept.md`, then read the **In scope** and **What it is not** sections only | 2 sections |
| 2 | Look for the feature in **In scope** and **What it is not**. If it is in neither, it is out ([../flows/conflict-resolve.md](../flows/conflict-resolve.md) R6) | — |

## W7 — Write a Step record at STORE

| # | Action | Reads |
|--:|--------|-------|
| 1 | Read the key block, **Skeleton**, and **Create** of `docs/protocol/blueprints/paperwork/step.md` | ~60 lines of protocol |
| 2 | Write the file from the approved proposal, which is already in context | — |
| 3 | For M2 and M3, read only **Mark in progress** of the step-registry blueprint and **Set step status** of the goal-file blueprint. Then search each target file for the row, and edit it | ~20 lines of protocol, 2 lines of paperwork |

Do not open other Step records to copy their shape. The blueprint is the shape.
