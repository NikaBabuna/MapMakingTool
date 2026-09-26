<!--
  File: docs/architecture/session/README.md
  Purpose: Level 3 — door to the session: owning one run of a world, observing it, and printing it
  Audience: Agents and humans
  Update when: A session page is added, or the question it answers changes
-->

# Session

A session is one run of one world. It owns the engine, lets only one caller at a time advance it or read it, measures what each step costs, and can print the settled world as text. The command line and the studio both drive a world only through a session. These pages have no fixed order among them; the order of a single step is [the generation](../world/README.md) inside [the engine step](../engine/README.md).

| Page | Question |
|------|----------|
| [run.md](run.md) | Who owns the engine, how does one step at a time stay one step at a time, and what can a caller read? |
| [diagnostics.md](diagnostics.md) | How are step time, memory, paint time, and phase times collected without touching the world? |
| [dump.md](dump.md) | What is the canonical text of a settled world? |

The coarser level: [../program.md](../program.md). The two callers: [../cli/README.md](../cli/README.md) and [../studio/README.md](../studio/README.md).
