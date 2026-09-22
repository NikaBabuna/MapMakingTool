<!--
  File: docs/protocol/environment/conduct.md
  Purpose: Hard rules that apply on every Step
  Audience: Agents and humans
  Update when: A hard rule is added or retired
-->

# Conduct

These rules apply no matter which flow is running. Each one exists because the opposite failure mode already happened, or is the obvious way for an agent to look successful while being wrong.

| # | Rule | Why it is a rule |
|---|------|------------------|
| 1 | Follow [dispatch.md](dispatch.md). Do not invent a write sequence | Invented sequences skip the record, and the next chat cannot see what was decided |
| 2 | Store approved requirements in the Step record before any implementation of that Step | Chat is not a store. A later chat will not remember the list |
| 3 | Mark the Step `in progress` before implementation | An unmarked half-edit looks like a finished tree. The mark is what makes a torn Step visible |
| 4 | Only one Step is `in progress` | Two open Steps make rollback ambiguous |
| 5 | Accept only when this Step’s witness holds and every earlier Accepted Step’s witness still holds | The suite is cumulative. See [correctness.md](correctness.md) |
| 6 | Do not delete or soften a requirement or a check to make a witness pass | That manufactures a green suite. It is forbidden even if the human is impatient; ask, and amend on purpose |
| 7 | Do not claim Accept from a status line, a commit message, or a paragraph in a Goal | The witness Accepts. The status line records an Accept that already happened |
| 8 | Domain language is written on the product shelf | If the meaning of a world rule lives only in chat or only in source, the next agent will invent a second meaning |
| 9 | A torn Step is rolled back. It is not continued | Failure must never look like success |
| 10 | Touch only the files the approved job and the active flow name | Drive-by edits rot documents the Step did not understand |

Breaking one of these is not a style issue. Stop, say which rule, and either repair it inside the flow or roll back.
