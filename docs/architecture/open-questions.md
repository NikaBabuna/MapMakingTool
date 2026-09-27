<!--
  File: docs/architecture/open-questions.md
  Purpose: Implementation gaps that are still undecided
  Audience: Agents and humans
  Update when: A gap is decided or a new gap is found
-->

# Open questions

Decided questions live on the page that states the rule, with the decision record. This page lists only what is still open.

## Still open

| # | Topic | Question |
|---|-------|----------|
| 1 | Non-finishing systems | If a system claims and does not finish, does the step wait, time out, or merge without that output? The loop runs systems synchronously, and a `run` that throws ends the step before the barrier is checked; no wait or skip exists ([systems](engine/systems.md)) |
| 4 | Delete Request | There is no delete merge rule. If one were added, a concurrent write could either win or lose; that choice is not made ([merge](engine/merge.md)) |
| 5 | A failed step | When a stage throws, the step does not settle: the heartbeat and update count may already have moved, events stay in the buffer, and the next step throws at its start. Should a failed step roll back, or is a failed engine always replaced ([one engine step](engine/README.md))? |
| 6 | Contacts across the poles | The tracer steps only east and south. On the last row, each pair of antipodal cells of different plates is traced twice, with the same direction, so a pair whose north–south velocities differ yields one collide and one separate contact; the first row yields no pole-crossing contact. Is that the intended model of a pole ([boundaries](world/boundaries.md))? |
| 7 | Area budgets | `area_flux` is computed, staged, settled, and dumped every generation, but the geometry pass only checks that it is present and sinks and fills cells from the contacts directly. Should the budgets drive the geometry, or are they a report ([interaction](world/interaction.md))? |
| 8 | Unused inputs of advection | `GeometryApplication.execute` traces the renumbered plates again and passes the contacts to `PlateKinematics.advect`, which only checks them for null; the generation index is only checked to be at least 1. Should they be used or dropped ([advect](world/motion/advect.md))? |
| 9 | Growth of the locker table | The ridge appends one locker per gap every generation, and no locker is ever removed, so the table grows without bound over a long run. Should unused lockers be compacted ([ridge](world/crust/ridge.md))? |
| 10 | Seed distance at the poles | ADR-012 asks that distance agree with the sphere topology, but the seed's site distance is flat north–south and does not reach across a pole. Should it wrap ([seed](world/seed.md))? |
| 11 | Order of the schema | `ProductSession.fieldNames` and `schemaTypes` follow the iteration order of an immutable map, which is unspecified, so `list pool`, `list schema`, and `schema get` may list fields in a different order in another run. Should the schema keep its declaration order ([run](session/run.md))? |
| 12 | Two play timers | The host has a play scheduler behind `/api/play` and `/api/pause`, while the web page plays with its own timer that posts `/api/advance` and never calls those routes. Which one is the studio's play ([controller](studio/controller.md), [tool](studio/web/tool.md))? |
| 13 | Inspecting outside the map | `POST /api/inspect` with a cell outside the map makes the handler throw, and the request gets no answer from it. What should the host answer ([http](studio/http.md))? |

## Decided elsewhere

| Topic | Where the rule is |
|-------|-------------------|
| Unmatched events are reported, never dropped | [events](engine/events.md), [ADR-006](../paperwork/decisions/ADR-006-unmatched-events.md) |
| Step 0 is seeded from one configuration object | [setup](engine/setup.md), [ADR-005](../paperwork/decisions/ADR-005-step-zero-config.md) |
| The product authors the category tree in code | [events](engine/events.md), [ADR-009](../paperwork/decisions/ADR-009-category-tree.md) |
| The map is a sphere on a rectangle | [topology](world/topology.md), [ADR-012](../paperwork/decisions/ADR-012-simulation-runner.md) |
| Crust decides who goes under, and height is read from thickness | [precedence](world/crust/precedence.md), [isostasy](world/crust/isostasy.md), [ADR-013](../paperwork/decisions/ADR-013-crust-topology.md) |
| One command language for the CLI and the studio | [language](cli/language.md), [ADR-010](../paperwork/decisions/ADR-010-product-adapters.md), [ADR-012](../paperwork/decisions/ADR-012-simulation-runner.md) |
| The studio is a local web page served by a Java host | [http](studio/http.md), [ADR-011](../paperwork/decisions/ADR-011-local-webview.md) |
| The shape of this paper | [architecture](README.md), [ADR-017](../paperwork/decisions/ADR-017-architecture-paper.md), [ADR-019](../paperwork/decisions/ADR-019-architecture-paper-by-layer.md) |
| Continuous integration runs the witness command on `main`, with Node for the web front's tests | [program](program.md), [ADR-020](../paperwork/decisions/ADR-020-web-front-tests.md) |

Parent: [architecture](README.md).
