<!--
  File: docs/paperwork/decisions/ADR-019-architecture-paper-by-layer.md
  Purpose: Decision record ADR-019
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-019 — Architecture paper by layer

**Date:** 2026-09-25
**Status:** accepted

The implementation paper describes the whole program as its code stands, one area per layer of the code, and every page backs its words with a model and with the code itself.

| Choice | What is now true |
|--------|------------------|
| Areas | `docs/architecture/` holds `program.md` and one area per layer: `engine/`, `world/`, `session/`, `cli/`, and `studio/`, whose chapter `studio/web/` is the web front. The paper's glossary and open-questions page sit at the root of the shelf and cover the whole program |
| Two statements | Every mechanism page states its mechanism in plain words and as a mathematical model written in LaTeX. A symbol that several models share is defined once, in the glossary's Symbols table |
| Code | Every procedure step names the member that performs it, linked to its source file. The lines that compute a central formula are quoted word for word, at most fifteen lines at a time |
| Program | `program.md` names every build file and every process the program runs, with what starts it, where it listens, and what it talks to |
| Scope | The paper covers all main source: Java, TypeScript, CSS, Rust, and the shell's configuration. It describes no test |

**Why:** The paper arranged by abstraction described the engine, the world, and a part of the studio. The session, the command line, the web front, and the desktop shell were described only by their code doors, or not at all, and the area named `host` collided with the HTTP host of the studio. A page written in prose alone left every formula to be reconstructed from the code. A page that did not name the code it described could drift from it without anyone seeing.

**Supersedes:** the level table of ADR-017, whose areas were Host, World, and Studio. The rest of ADR-017 stands.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)
