<!--
  File: docs/paperwork/goals/G-012-code-structure-and-conventions.md
  Purpose: Multi-session Goal — all code organised into folders by job, each introduced by its README, under conventions the protocol makes every agent follow
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-012 — Code structure and conventions

**Status:** `in progress`  
**Type:** `Polishing`  
**Prior:** [G-011](G-011-docs-restructuring.md) closed with the docs on four shelves, an architecture paper by layer that quotes the code line by line, and a test suite that proves requirements by running the code. Two pole-contact tests are red on purpose.  
**Approved:** 2026-09-27 (human). Organise all code, in every language, into folders by job, each folder introduced by its README, under conventions the protocol makes every agent follow and keep current.

---

## Result we want

When this Goal is `done`:

*The protocol, for any project:*

1. **Every folder gets a README and a navigation row.** Whenever an agent creates a folder, in docs or in code, it creates the README and adds the navigation row in the same Step, with no opt-out. The protocol exempts only a few kinds of folder: build output, dependency installs, tool caches, generated files, and path segments that exist only for a language's namespace. The project names its concrete exempt folders on its conventions page. A docs README carries a **Why** part of at most three sentences: why the folder exists, what belongs in it, and what does not. Witness: reading the changed protocol pages.
2. **A code README is the deep dive into its folder.** It says: what the folder does as a whole, and why it is organised this way; how its parts are wired together, and where to start reading; what it depends on and what uses it; for each file and subfolder, what it does and its main types or functions; which architecture page explains the concept. A README that only lists files fails its blueprint's Check. Any Step that changes a folder's code updates that folder's README. Witness: reading the new blueprint and the Step flow.
3. **Paper and READMEs split the work.** The architecture paper states concept, engineering and maths. It may name a type briefly, and points to the code README for the deep dive. The README owns names, wiring, and where each step of a mechanism happens. Witness: reading the map, the quality rules and the mechanism-page blueprint.
4. **Code is organised into folders by job.** A code folder has one job, stated first in its README. A folder that holds two jobs, or more files than the conventions page's limit, gets split. Every Step proposal says where each new file goes and why, citing the conventions page. Witness: reading quality and the Step flow.
5. **The conventions page is a standing, living file every project has.** It lives at `docs/architecture/conventions.md` and has its own blueprint. The protocol names it and binds every agent to follow it, and its contents belong to the project. When a Step introduces something the page doesn't yet cover (a new kind of file, folder, pattern or language), that Step adds the convention to the page, so later agents follow it too. Witness: reading the map, quality, the Step flow and the blueprint.
6. **The whole-tree audit enforces all of this.** It lists every folder on disk, not only those `navigation.md` already names. It checks that each has a README, that the README is true to the folder, and that it has a navigation row. It also checks names and placement against the conventions page. Witness: reading the audit flow.

*This project:*

7. **The conventions page is written.** Java follows classic Java naming, applied consistently to every name: packages, types, methods, fields and constants. TypeScript and Rust follow their own classic conventions. The page also covers test names, where a new file goes, the folder limit, file headers, and the exempt folders. Witness: reading the page.
8. **All code is organised and documented.** `product` is split into packages by job, following the paper's areas. No folder is over the limit, and every name, members included, follows the conventions. Every non-exempt folder has a README that passes its Check, plus a navigation row. Witness: a folder listing, a name sweep, and reading each README.
9. **The paper holds concept and maths.** Code quotes, member-by-member links and code tables have moved into the code READMEs. A page may still name a type in passing, and it points to its README. Witness: a search of the architecture shelf for code blocks and source-file tables finds none outside `program.md`.
10. **Behaviour is unchanged.** Every test except the two known defects is green, and those two fail only on their stated defect. Witness: `./mvnw test`.

---

## What exists now that this Goal touches

The protocol's README rules, which require a plain list and let the agent skip READMEs; the architecture paper's pages; and every source folder, and its README if it has one. The `product` module keeps 39 files in one package, 26 folders have no README, and the 17 READMEs that exist are lists.

---

## What "better" means, measurably

| Measure | Now | After |
|---------|-----|-------|
| Folders an agent may create without a README | any it calls "not a landmark" | none, outside the listed exempt kinds |
| Source folders with no README | 26 | 0, except exemptions listed with a reason |
| Code READMEs that are only a list | 17 of 17 | 0 |
| Docs READMEs that say why the folder exists | few | all |
| Largest single package | 39 files | at or below the conventions page's limit |
| Architecture pages quoting code or listing members | nearly all | none |
| Conventions page | none | one, at a fixed path, kept current by every Step |

---

## What must not change

- What the app does.
- The maths and engineering content of the paper. It moves or stays; it isn't rewritten.
- The Maven modules and which way their dependencies point.
- The product shelf.

---

## Out of scope (this Goal)

- Fixing the pole-contact defects.
- Changing logic or breaking up large classes. This Goal only moves, renames, and rewrites headers and READMEs.
- New features and the product shelf.

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| Type | Polishing (human) |
| Reach | All code, any language: Java, TypeScript, Rust, and the build and script files, not only the Java part (human) |
| Folder rule | README and navigation row for every folder an agent creates, enforced by protocol for any project (human) |
| Conventions | Project-specific content in a file the protocol always names and requires. Agents extend it when they introduce something new (human) |
| Java naming | Classic Java, consistent across every name including members (human) |
| Paper vs README | Paper is concept and maths, and may name a type briefly. Code READMEs are the deep dive (human) |
| Docs READMEs | Short, required Why part of at most three sentences (agent, at the human's request) |
| Conventions path | `docs/architecture/conventions.md` (agent's suggestion, approved) |
| Package layout | Mirrors the paper's areas, so a page and its code folder share a name (agent's suggestion, approved) |
| Record ids in code | Only in the test comments that name their requirements (agent's suggestion, approved) |

---

## Product claims (tests by Goal end)

- [ ] **Every folder gets a README and a navigation row.** Whenever an agent creates a folder, in docs or in code, it creates the README and adds the navigation row in the same Step, with no opt-out. The protocol exempts only a few kinds of folder: build output, dependency installs, tool caches, generated files, and path segments that exist only for a language's namespace. The project names its concrete exempt folders on its conventions page. A docs README carries a **Why** part of at most three sentences: why the folder exists, what belongs in it, and what does not. Witness: reading the changed protocol pages.
- [ ] **A code README is the deep dive into its folder.** It says: what the folder does as a whole, and why it is organised this way; how its parts are wired together, and where to start reading; what it depends on and what uses it; for each file and subfolder, what it does and its main types or functions; which architecture page explains the concept. A README that only lists files fails its blueprint's Check. Any Step that changes a folder's code updates that folder's README. Witness: reading the new blueprint and the Step flow.
- [ ] **Paper and READMEs split the work.** The architecture paper states concept, engineering and maths. It may name a type briefly, and points to the code README for the deep dive. The README owns names, wiring, and where each step of a mechanism happens. Witness: reading the map, the quality rules and the mechanism-page blueprint.
- [ ] **Code is organised into folders by job.** A code folder has one job, stated first in its README. A folder that holds two jobs, or more files than the conventions page's limit, gets split. Every Step proposal says where each new file goes and why, citing the conventions page. Witness: reading quality and the Step flow.
- [ ] **The conventions page is a standing, living file every project has.** It lives at `docs/architecture/conventions.md` and has its own blueprint. The protocol names it and binds every agent to follow it, and its contents belong to the project. When a Step introduces something the page doesn't yet cover (a new kind of file, folder, pattern or language), that Step adds the convention to the page, so later agents follow it too. Witness: reading the map, quality, the Step flow and the blueprint.
- [ ] **The whole-tree audit enforces all of this.** It lists every folder on disk, not only those `navigation.md` already names. It checks that each has a README, that the README is true to the folder, and that it has a navigation row. It also checks names and placement against the conventions page. Witness: reading the audit flow.
- [ ] **The conventions page is written.** Java follows classic Java naming, applied consistently to every name: packages, types, methods, fields and constants. TypeScript and Rust follow their own classic conventions. The page also covers test names, where a new file goes, the folder limit, file headers, and the exempt folders. Witness: reading the page.
- [ ] **All code is organised and documented.** `product` is split into packages by job, following the paper's areas. No folder is over the limit, and every name, members included, follows the conventions. Every non-exempt folder has a README that passes its Check, plus a navigation row. Witness: a folder listing, a name sweep, and reading each README.
- [ ] **The paper holds concept and maths.** Code quotes, member-by-member links and code tables have moved into the code READMEs. A page may still name a type in passing, and it points to its README. Witness: a search of the architecture shelf for code blocks and source-file tables finds none outside `program.md`.
- [ ] **Behaviour is unchanged.** Every test except the two known defects is green, and those two fail only on their stated defect. Witness: `./mvnw test`.

---

## Planned Steps

| Step | Intent | Type | Status |
|------|--------|------|--------|
| F-069 | The conventions page: its blueprint, its place on the protocol map, and this project's conventions written into it | Documentation | done |
| F-070 | The protocol mechanism: the folder rule, the Why part of docs READMEs, the code README blueprint, the organisation rule, the paper/README split, keeping conventions current, and the Step flow and audit changes | Documentation | done |
| F-071 | The product module: packages by job, renames, tests moved to match, READMEs taking over the code detail of the world and session pages, those pages reduced to concept and maths, navigation rows | Cleanup | done |
| F-072 | The engine and command-line modules: the same treatment, with their pages | Cleanup | not started |
| F-073 | The ui module (Java host, web front, desktop shell) and the build and tooling folders: the same, with the studio pages | Cleanup | not started |
| F-074 | Whole-tree audit against the new rules, Why parts added to every docs README, then close the Goal | Documentation | not started |

The conventions page comes first so the protocol never links to a file that does not exist yet.

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 3 / 6 |
| Claim boxes | 0 / 10 |
| Last Accept | F-071 |
