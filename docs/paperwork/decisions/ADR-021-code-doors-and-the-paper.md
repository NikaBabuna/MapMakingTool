<!--
  File: docs/paperwork/decisions/ADR-021-code-doors-and-the-paper.md
  Purpose: Decision record ADR-021
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-021 — Code doors and the paper

**Date:** 2026-09-27
**Status:** accepted

Every folder has a door that introduces it, the door of a code folder is the deep dive into that code, and the implementation paper keeps to concept, engineering, and mathematics.

| Choice | What is now true |
|--------|------------------|
| A door for every folder | Every folder an agent creates gets its `README.md` door and its row in `docs/navigation.md` in the same Step. No agent judges that a folder needs no door. Only six kinds of folder are exempt (quality 3.14), and the project lists its concrete exempt folders on `docs/architecture/conventions.md` |
| Code doors | The door of a code folder is the deep dive into its code: its one job, why it is organised so, how its parts are wired and where to start reading, what it depends on and what uses it, the member that performs each step the paper describes, and what each file holds. A door that only lists its files fails its Check |
| The paper | A mechanism page states the concept, the engineering, and the mathematics. It may name a type in passing. It quotes no source and names no member; its `Code:` line leads to the code doors, which map each step to its member |
| Organisation | Code is kept one job per folder. A folder that holds two jobs, or more source files than the limit on the conventions page, is split by job |
| Conventions | Every project keeps `docs/architecture/conventions.md`. Code follows it, and a Step that brings in something it does not cover adds the convention in the same Step |

**Why:** A folder got a door only when the agent judged it a landmark, and the door's blueprint allowed nothing but a list, so most code folders had no introduction and the rest listed their files without explaining them; one package held 39 files. Names followed habit, not a written rule. The paper, meanwhile, quoted source and named every member, so every rename made it false in several places, and it grew into a second copy of the code. Code-level detail now sits in the door beside the code, where a change to that code is made, and the paper keeps to ideas that change slowly.

**Supersedes:** the Code row of ADR-019, by which every procedure step names its member and central formulas are quoted from source. The rest of ADR-019 stands.

**Goal:** [G-012 Code structure and conventions](../goals/G-012-code-structure-and-conventions.md)
