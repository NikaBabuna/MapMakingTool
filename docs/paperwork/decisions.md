<!--
  File: docs/paperwork/decisions.md
  Purpose: Index of decision records
  Audience: Agents and humans
  Update when: A decision is added or its status changes
-->

# Decisions

One file per decision. The next number is one higher than the last row. Each row says in one sentence what was decided. The record holds the whole decision, the reason for it, and what it replaced. A decision that a later one changed in part stays `accepted`, and its row names the later decision.

| ID | Title | Status | Decided | Doc |
|----|-------|--------|---------|-----|
| ADR-001 | Monorepo for engine and product | accepted | The engine and the product live in one repository through alpha and beta. | [ADR-001-monorepo.md](decisions/ADR-001-monorepo.md) |
| ADR-002 | Documentation namespaces | accepted | Documentation is grouped into folders by what it describes, and the rules of the world live in a product wiki. Amended by ADR-014. | [ADR-002-documentation-namespaces.md](decisions/ADR-002-documentation-namespaces.md) |
| ADR-003 | Java as implementation language | accepted | The implementation language is Java. | [ADR-003-java.md](decisions/ADR-003-java.md) |
| ADR-004 | Goal / Session / Step agent procedure | accepted | Work is organised as Goals made of Steps, each Step approved, witnessed, and recorded before it closes, and torn work is rolled back. Amended by ADR-014. | [ADR-004-goal-and-step.md](decisions/ADR-004-goal-and-step.md) |
| ADR-005 | Step 0 seeded by config object | accepted | The engine's first step is set up only from a configuration object the caller supplies. | [ADR-005-step-zero-config.md](decisions/ADR-005-step-zero-config.md) |
| ADR-006 | Unmatched events are logged | accepted | An event that no system claims is logged, never silently dropped. | [ADR-006-unmatched-events.md](decisions/ADR-006-unmatched-events.md) |
| ADR-007 | Multi-module layout and Java 21 | accepted | One Maven build of sibling modules on Java 21, in which the engine never depends on the other modules. Amended by ADR-010. | [ADR-007-modules-and-java-21.md](decisions/ADR-007-modules-and-java-21.md) |
| ADR-008 | Engine diagnostics via SLF4J + capturable port | accepted | The engine reports what it does through a diagnostics port on the SLF4J API that tests can capture, and carries no logging binding of its own. | [ADR-008-diagnostics.md](decisions/ADR-008-diagnostics.md) |
| ADR-009 | Product authors the category tree in Java | accepted | The product, not the engine, builds the tree of event categories, in Java code. | [ADR-009-category-tree.md](decisions/ADR-009-category-tree.md) |
| ADR-010 | UI and CLI are product adapters | accepted | The window and the command line are adapters over the product, sharing one session and one command language. Amended by ADR-011. Amended by ADR-012. | [ADR-010-product-adapters.md](decisions/ADR-010-product-adapters.md) |
| ADR-011 | Local webview UI (Tauri + Next + Java HTTP host) | accepted | The map front is a local web page inside a desktop shell, talking to the Java world over localhost, and the old Swing window is removed. | [ADR-011-local-webview.md](decisions/ADR-011-local-webview.md) |
| ADR-012 | Simulation runner harden (G-009 locks) | accepted | Rifts are filled only by the two plates that part, the poles join as on a sphere, one command language serves every front, and costs are recorded and queryable. | [ADR-012-simulation-runner.md](decisions/ADR-012-simulation-runner.md) |
| ADR-013 | Crust topology (G-010 locks) | accepted | Crust is material carried by the plates, born thin at ridges and consumed only where ocean meets thicker crust, and height is read from its thickness. | [ADR-013-crust-topology.md](decisions/ADR-013-crust-topology.md) |
| ADR-014 | Protocol shelves; session retired | accepted | Conduct lives in one protocol folder, work has two levels, Goal and Step, and the Active Goal line lives only on the Goal index. Amended by ADR-016. | [ADR-014-protocol-shelves.md](decisions/ADR-014-protocol-shelves.md) |
| ADR-015 | Product pages are conceptual | accepted | The product pages explain the product to a person and never describe the program. | [ADR-015-conceptual-product.md](decisions/ADR-015-conceptual-product.md) |
| ADR-016 | Paperwork shelf | accepted | Progress records live on one shelf, where a record with its own id is one file and a list read as a sequence is one file. Amended by ADR-018. | [ADR-016-paperwork-shelf.md](decisions/ADR-016-paperwork-shelf.md) |
| ADR-017 | Architecture paper by abstraction | accepted | The implementation is written as one paper arranged by level of abstraction, each page pointing at the finer page. Amended by ADR-019. | [ADR-017-architecture-paper.md](decisions/ADR-017-architecture-paper.md) |
| ADR-018 | Scope lives in the concept | accepted | The product's binding scope is part of the concept page, and the stack is listed on the program page. | [ADR-018-scope-in-concept.md](decisions/ADR-018-scope-in-concept.md) |
| ADR-019 | Architecture paper by layer | accepted | The implementation paper covers the whole program, one area per layer of the code, and every page states its mechanism in plain words and as a model and names the code that performs it. | [ADR-019-architecture-paper-by-layer.md](decisions/ADR-019-architecture-paper-by-layer.md) |
| ADR-020 | Web front tests with Vitest | accepted | The web front is tested with Vitest in a simulated browser, beside its source, and the Maven witness command runs those tests with the Java ones. | [ADR-020-web-front-tests.md](decisions/ADR-020-web-front-tests.md) |

How to write one: [../protocol/blueprints/paperwork/decision-record.md](../protocol/blueprints/paperwork/decision-record.md).
