<!--
  File: docs/navigation.md
  Purpose: Documentation map — authoritative index of where things are, and which places are expensive to read
  Audience: Agents and humans
  Update when: Any doc or folder is added, moved, or removed, or a place becomes heavy
-->

# Navigation

**Phase:** alpha ([protocol/environment/phase.md](protocol/environment/phase.md))  
**Goal index:** [paperwork/goals.md](paperwork/goals.md) — G-011 Docs restructuring · last completed G-010  
**Protocol:** [protocol/README.md](protocol/README.md) · begin at [protocol/brief.md](protocol/brief.md)  
**Reading:** [protocol/navigation/README.md](protocol/navigation/README.md) — how to read this map, and the repository, without filling your context

Folder indexes are **README.md** in each landmark directory. Prefer those links when entering a folder.

---

## Entry points

| Audience | Start |
|----------|-------|
| Agents | [../AGENTS.md](../AGENTS.md) |
| Humans | [../README.md](../README.md) |
| Docs tree | [README.md](README.md) |
| Protocol | [protocol/brief.md](protocol/brief.md) |
| Flows | [protocol/flows/README.md](protocol/flows/README.md) |
| Reading efficiently | [protocol/navigation/README.md](protocol/navigation/README.md) |

---

## Protocol (`docs/protocol/`)

**Folder:** [protocol/README.md](protocol/README.md)

| Page | Status |
|------|--------|
| [brief.md](protocol/brief.md) | Active — the global prompt. Read first |
| [core-workflow.md](protocol/core-workflow.md) | Active — the startup sequence and the flow catalogue |
| [environment/](protocol/environment/README.md) | Active — map, phase, quality, correctness, style, core definitions |
| [navigation/](protocol/navigation/README.md) | Active — reading, bounds, pointers, code route, walks |
| [blueprints/](protocol/blueprints/README.md) | Active — the shape of every document, and the operations that edit it, in seven groups |
| [flows/](protocol/flows/README.md) | Active — the twelve named flows |

---

## Paperwork (`docs/paperwork/`)

**Folder:** [paperwork/README.md](paperwork/README.md)

| Page | Status |
|------|--------|
| [goals.md](paperwork/goals.md) | Active — the only Active Goal line, and one row per Goal ever opened |
| [goals/](paperwork/goals/README.md) | Active — one file per Goal. The index holds status |
| [steps.md](paperwork/steps.md) | Active — step registry: status and one sentence per Step, grouped by Goal |
| [steps/](paperwork/steps/README.md) | Active — one file per Step |
| [decisions.md](paperwork/decisions.md) | Active — decision index: one sentence per decision, and the next number |
| [decisions/](paperwork/decisions/README.md) | Active — one file per decision |
| [changelog.md](paperwork/changelog.md) | Active — structure, scope, and Goal events, newest first |
| [roadmap.md](paperwork/roadmap.md) | Active — the order of Goals |
| [backlog.md](paperwork/backlog.md) | Active — ideas not yet promoted to a Goal, and where earlier ideas went |

Goal files and Step records are listed on their indexes. This map does not duplicate every id.

---

## Architecture (`docs/architecture/`)

**Folder:** [architecture/README.md](architecture/README.md)

| Page | Status |
|------|--------|
| [README.md](architecture/README.md) | Active — abstract: the levels, the question each answers, and how a page states and cites |
| [program.md](architecture/program.md) | Active — modules, build files, dependency direction, processes, the stack, and the witness command |
| [glossary.md](architecture/glossary.md) | Active — implementation words, and the symbols several models share |
| [open-questions.md](architecture/open-questions.md) | Active — implementation behaviour still undecided |
| [engine/](architecture/engine/README.md) | Active — one engine step, and one page per engine mechanism |
| [world/](architecture/world/README.md) | Active — one generation, its wiring, fields, topology, seed, and phases |
| [world/motion/](architecture/world/motion/README.md) | Active — integrate, sink, flood, fission, and advect |
| [world/crust/](architecture/world/crust/README.md) | Active — precedence, subduction, orogeny, ridge, margin, collision, and isostasy |
| [session/](architecture/session/README.md) | Active — the run, its diagnostics, and its dump |
| [cli/](architecture/cli/README.md) | Active — one run of the CLI, the runner, and the command language |
| [studio/](architecture/studio/README.md) | Active — controller, raster, HTTP host, and desktop shell |
| [studio/web/](architecture/studio/web/README.md) | Active — the web front: tool, client, canvas, viewport, chrome, terminal, and styles |

---

## Product (`docs/product/`)

**Folder:** [product/README.md](product/README.md)

| Page | Status |
|------|--------|
| [concept.md](product/concept.md) | Active — what the product is for, and the binding scope: what it includes and refuses |
| [journeys.md](product/journeys.md) | Active — what a person does |
| [glossary.md](product/glossary.md) | Active — world words |
| [style-guide.md](product/style-guide.md) | Active — screen language |
| [wiki/](product/wiki/README.md) | Active — world rules |
| [wiki/world.md](product/wiki/world.md) | Active — the map's shape, edges, and seed |
| [wiki/tectonics.md](product/wiki/tectonics.md) | Active — plates and crust |
| [wiki/elevation.md](product/wiki/elevation.md) | Active — what height means |

---

## Code

Enter code through the door of its folder, or through the paper page that describes it ([protocol/navigation/code.md](protocol/navigation/code.md)). Do not list source trees.

| Folder | Door | What it holds | Described by |
|--------|------|---------------|--------------|
| `engine/` | [../engine/README.md](../engine/README.md) | The step-based host: pool, events, systems, merge, user ports, diagnostics. Each package has its own door | [architecture/engine/](architecture/engine/README.md) |
| `product/` | [../product/README.md](../product/README.md) | World fields, the tectonics generation, and the session | [architecture/world/](architecture/world/README.md), [architecture/session/](architecture/session/README.md) |
| `cli/` | [../cli/README.md](../cli/README.md) | The headless runner and the shared command language | [architecture/cli/](architecture/cli/README.md) |
| `ui/` | [../ui/README.md](../ui/README.md) | Raster, map controller, and the Java HTTP host | [architecture/studio/](architecture/studio/README.md) |
| `ui/web/` | [../ui/web/README.md](../ui/web/README.md) | The Next.js studio front | [architecture/studio/web/](architecture/studio/web/README.md); the look is [product/style-guide.md](product/style-guide.md) |
| `ui/desktop/` | [../ui/desktop/README.md](../ui/desktop/README.md) | The Tauri desktop shell | [architecture/studio/desktop.md](architecture/studio/desktop.md) |
| `.github/` | [../.github/README.md](../.github/README.md) | The CI workflow | [architecture/program.md](architecture/program.md) |
| `.cursor/` | [../.cursor/README.md](../.cursor/README.md) | The editor's always-on rule | Its door |

---

## Heavy places

Reading any of these whole fills a context window with little of value. The rules for reading them are in [protocol/navigation/bounds.md](protocol/navigation/bounds.md). Sizes are approximate.

### Never open

| Path | What it is | Size | Instead |
|------|------------|------|---------|
| `ui/web/node_modules/`, `ui/desktop/node_modules/` | Installed JavaScript packages | ~9,200 files | Search the lock file for one package name |
| `engine/target/`, `product/target/`, `cli/target/`, `ui/target/` | Maven build output | ~400 files | The source. For a failed test, the summary line of the witness output |
| `ui/desktop/src-tauri/target/`, `ui/desktop/src-tauri/gen/` | Rust and Tauri build output | ~3,500 files | The source under `ui/desktop/src-tauri/src/` |
| `ui/web/.next/` | Next.js build output | ~100 files | The source under `ui/web/src/` |
| `.git/` | Version control internals | ~5,300 files | `git log --format="%h %s"`, `git status --porcelain`, `git diff --stat` |
| `.tools/` | A local Maven install, not part of the repository | — | — |

### Open narrowly

| Path | What it is | Size | How to read it |
|------|------------|------|----------------|
| `ui/desktop/src-tauri/Cargo.lock` | Rust lock file | ~4,700 lines | Search for one crate name |
| `ui/web/package-lock.json` | JavaScript lock file | ~1,000 lines | Search for one package name |
| `ui/web/src/components/MapTool.tsx` | Web studio component | ~970 lines | Search the member, read its range |
| `ui/web/src/app/globals.css` | Web stylesheet | ~960 lines | Search the selector, read its range |
| `ui/src/main/java/com/aethelgard/ui/host/MapHost.java` | Java HTTP host | ~670 lines | Search the member, read its range |
| `product/src/main/java/com/aethelgard/product/ApplyGeometry.java` | Geometry phase | ~610 lines | Search the member, read its range |
| `cli/src/main/java/com/aethelgard/cli/CommandDispatch.java` | Command language | ~540 lines | Search the member, read its range |
| `product/src/test/resources/worlds/` | Golden world dumps (test data) | Data, not prose | Only when a dump test fails: its first line, then the failing region |
| `docs/paperwork/steps/` | Step records | ~66 files, ~4,200 lines | One record by id. Search a field across them |
| `docs/paperwork/decisions/` | Decision records | ~18 files | One record, chosen from the index |
| `docs/protocol/blueprints/` | Blueprints | ~35 files, ~4,000 lines | The group door, then one blueprint's key block and one operation |
| `docs/protocol/flows/` | Flows | 12 files, ~2,000 lines | Only the flow the turn selected, one stage or algorithm at a time |
