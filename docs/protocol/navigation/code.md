<!--
  File: docs/protocol/navigation/code.md
  Purpose: How to reach the one piece of source that matters, through the implementation paper, without reading modules
  Audience: Agents
  Update when: The route from the paper to source, or a code-reading rule, changes
-->

# Code

Source is the most expensive read in a repository. The implementation paper, `docs/architecture/`, exists so that an agent opens one page and then one member of one file, instead of a module. This page is that route. The project facts it relies on (where tests live, how declarations are found, how the witness output reports) are the project-fact lines of `docs/architecture/program.md` ([../blueprints/architecture/program.md](../blueprints/architecture/program.md)).

## The route

Take the steps in order. Stop at the first step that answers the question. Most questions about behaviour stop at step 4, on the paper.

| Step | Open | Take |
|-----:|------|------|
| 1 | `docs/architecture/program.md` | The module that owns the behaviour, from the module table. Do not list the module's folders |
| 2 | `docs/architecture/README.md`, the paper abstract | The area whose question matches yours, from its level table |
| 3 | That area's level page, `docs/architecture/<area>/README.md` (and, if it points to one, the chapter's level page) | The stage or row that names the mechanism, and its page |
| 4 | That mechanism page | **What it reads**, **What it writes**, **Model**, **Procedure**, **What is true afterwards**. If this answers the question, stop here |
| 5 | The mechanism page's **Where it lives** section | The unit's name, its members, and its source path |
| 6 | In that one source file, search for the member's declaration, using the **Declarations** line of the program page, or the member's name | The line number of the declaration |
| 7 | Read a line range starting at that line, up to the end of the member | The member's body, and nothing else |

For code that the paper does not describe, step 1 is the code folder's own `README.md` door, listed under **Code** in `docs/navigation.md`.

## Tests

| To find | Do |
|---------|----|
| The tests of a unit | Apply the **Tests** line of the program page: it says where test files live and how they are named after the unit |
| The tests a Step relies on | Read the Test map of that Step record |
| Which Step a test belongs to | Search `docs/paperwork/steps/` for the test's name |
| Why a test failed | Search the saved witness output ([reading.md](reading.md) R10) for the **Output summary** patterns of the program page, then read the one failing test by steps 6–7 |

## Callers and uses

1. Search the source folders (not test folders, not build output) for the unit or member name, file names only.  
2. Open the one or two files that matter, and apply steps 6–7 in each.  
3. If there are more than ten callers, the question is a sweep. Refine the search to the call form before reading anything ([bounds.md](bounds.md) S2).

## Rules

| # | Rule |
|---|------|
| C1 | Do not list a source tree recursively. The module door and any package doors say what each part holds |
| C2 | Do not open a source file because its name looks relevant. Reach it through the paper or through a search hit |
| C3 | Do not read a whole source file of more than 150 lines. Read the member ([reading.md](reading.md) size rules) |
| C4 | Do not read tests to learn behaviour that the paper states. Read a test to learn what is witnessed |
| C5 | When the paper and the source disagree, the source is what is built ([../flows/conflict-resolve.md](../flows/conflict-resolve.md) R10). Note it for Sync or Global docsync. Do not fix the paper during Explain, Investigate, or Audit |
| C6 | During a Step, open only the plan's files and the files a search hit names. A file outside the plan that you must change needs **Amend step requirements** ([../flows/step.md](../flows/step.md)) |
