<!--
  File: docs/paperwork/goals/G-011-docs-restructuring.md
  Purpose: Multi-session Goal — four doc shelves (protocol, product, architecture, paperwork)
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-011 — Docs restructuring

**Status:** `in progress`  
**Engine:** no `engine` source edits. No simulation change.  
**Prior:** [G-010](G-010-crust-topology.md) closed crust topology. Docs are still split across `process/`, `project/`, `engine/`, `product/`, and `blockers/`, with conduct, concept, implementation, and history mixed on the same shelves.

**Approved:** 2026-09-22 (user). Direction: four folders, one job each. Each folder is its own Step. What changes inside a folder is negotiated at that Step, not in this Goal.

---

## Result we want

When this Goal is `done`:

1. **Four shelves.** `docs/protocol/` is conduct (product-independent rules). `docs/product/` is what this product is (concept, domain language, wiki) and does not describe implementation. `docs/architecture/` is what is built: a paper in plain English, with the graphs and theory needed to see what each part does. `docs/paperwork/` is history: who did what, and when.
2. **One entrance.** `docs/README.md` and `docs/navigation.md` stay at the docs root and point at the four shelves. `AGENTS.md` stays at the repo root and points into `docs/protocol/`. Cursor’s rule file stays where Cursor reads it and points at the same place.
3. **A file’s folder is its job.** Conduct, concept, machine, and log are not mixed on one shelf.
4. **Suite.** Incremental tests stay green. Same seed and Step count still produce the same fields.

Plain English: four shelves, one entrance. The folder tells you whether you are reading rules, the product idea, the machine, or the log.

---

## Shelf assignment

This is where today’s files go. Page rewrites are Step negotiations.

| Shelf | Moves here |
|-------|------------|
| Protocol | `docs/process/`, `PHASE.md`, `doc-contract.md` |
| Product | `concept.md`, product glossary, wiki, style guide. Scope (`project.md`) lives here as what this product is; protocol only says that scope is binding |
| Architecture | Root architecture roll-up, `docs/engine/` (specs and engine glossary), `product/architecture.md` |
| Paperwork | Goals, session, Step registry, blockers, changelog, decisions, roadmap, backlog |

Roadmap and backlog sit with paperwork (project management). Decisions stay the history; architecture states what is true now.

---

## Out of scope (this Goal)

- Map behavior, tectonics balance, climate, biomes, Explore / Guide / Timeline
- Moving Java types into new packages
- Rewriting page contents beyond what that Step’s negotiation approves
- Rebuilding the engine spec set except to give those files their new home

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Shelves | `protocol/`, `product/`, `architecture/`, `paperwork/` |
| Entrance | `docs/README.md` + `docs/navigation.md` only, besides the four folders |
| Agent door | `AGENTS.md` stays at repo root |
| Depth | Settled in F-062. Architecture needs a fourth level for chapters (`docs/architecture/<area>/<chapter>/<page>`). Other shelves stay within three |
| Step shape | One folder per Step. Internals negotiated at the Step |
| Runtime | Untouched |

---

## Product claims (tests by Goal end)

- [ ] Every doc lives on one of the four shelves, or is the entrance at `docs/` root
- [ ] An agent can tell a file’s job from its folder
- [ ] Incremental suite green; fields for a fixed seed and Step count unchanged

---

## Planned Steps

| Step | Intent | Status |
|------|--------|--------|
| F-062 | Protocol shelf: four rooms, blueprints, flows, session retired | done |
| F-063 | Rewrite every protocol page to full explanations and real blueprints | done |
| F-064 | Conceptual product shelf | done |
| F-065 | Create `docs/architecture/`. Move engine and product implementation docs into the paper | not started |
| F-066 | Paperwork shelf: one file per record, lists stay one file | done |

Protocol goes first so later Steps move files into homes the rules already name.

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 4 / 5 |
| Claim boxes | 0 / 3 |
| Last Accept | F-066 |
