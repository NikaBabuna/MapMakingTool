<!--
  File: docs/architecture/README.md
  Purpose: Abstract of the implementation paper — levels, conventions, no procedures
  Audience: Agents and humans
  Update when: A level is added or a level's question changes, or the conventions of a page change
-->

# Architecture

This shelf is the implementation paper: what the program is, from its build and its running processes down to each rule that changes the world, as the code stands. A page at one level names the finer page and links it. The procedure lives on that finer page.

**Why:** The paper keeps to what changes slowly — the concepts, the engineering, and the mathematics of the program — so it stays true when code is renamed or moved. The names of types and members, the wiring between files, and the member behind each step live in the README of each code folder, beside the code they describe. How code is written is on [conventions.md](conventions.md), and what the product is for a person is on the product shelf.

| Level | Question | Page |
|-------|----------|------|
| Program | What is built, from which files, which way do the modules depend, and which processes run? | [program.md](program.md) |
| Engine | What does one engine step do, with any simulation plugged in? | [engine/](engine/README.md) |
| World | What does one generation of the world write, phase by phase? | [world/](world/README.md) |
| Session | Who owns a running world, and how is it measured and printed? | [session/](session/README.md) |
| CLI | How does the command line drive a world? | [cli/](cli/README.md) |
| Studio | How is a world painted, served, shown, and driven by a person? | [studio/](studio/README.md) |

Every mechanism page explains its mechanism twice. It first says in plain words what the mechanism is for, and what it reads and writes. It then gives a Model: the mechanism as mathematics, written in LaTeX, where a symbol that several pages share is defined in the glossary's Symbols table. The Procedure walks the steps in the order the code runs them, in plain words. A page may name a type in passing, but it quotes no source and names no member: its `Code:` line leads to the README of each code folder that performs it, which maps every step to the member behind it. A level page lists its children in the order the code runs them. The paper describes the program, not its tests.

Words and symbols: [glossary.md](glossary.md). Behaviour that is still undecided: [open-questions.md](open-questions.md). How the code is named, where a file goes, and how a file is headed: [conventions.md](conventions.md). Why the shelf is one paper arranged by abstraction: [ADR-017](../paperwork/decisions/ADR-017-architecture-paper.md). Why it is arranged by layer, with models and code: [ADR-019](../paperwork/decisions/ADR-019-architecture-paper-by-layer.md). Why the paper keeps to concept and mathematics, and code detail lives in the code doors: [ADR-021](../paperwork/decisions/ADR-021-code-doors-and-the-paper.md).
