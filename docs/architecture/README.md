<!--
  File: docs/architecture/README.md
  Purpose: Abstract of the implementation paper — four levels, no procedures
  Audience: Agents and humans
  Update when: A level is added or a level's question changes
-->

# Architecture

This shelf is the implementation paper. It states the engineering the program runs: the modules, the engine step, the world generation, and the studio that shows the result. A page at one level names the finer page and links it. The procedure lives on that finer page.

| Level | Question | Page |
|-------|----------|------|
| Program | What runs, and which way do the modules depend? | [program.md](program.md) |
| Host | What does one engine step do, with or without a world? | [host/](host/README.md) |
| World | What does one generation write into the grids? | [world/](world/README.md) |
| Studio | How do settled grids become pixels, polls, and commands? | [studio/](studio/README.md) |

Words for the host: [host/glossary.md](host/glossary.md). Gaps still undecided: [host/open-questions.md](host/open-questions.md). Why the shelf has this shape: [ADR-017](../paperwork/decisions/ADR-017-architecture-paper.md).
