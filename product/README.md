<!--
  File: product/README.md
  Purpose: Door to the product module: the world that runs on the engine, and the session that runs it
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Product module

The world that runs on the engine: its fields, the tectonic generation that rewrites them once per step, and the session that owns a running world.

**Paper:** [World](../docs/architecture/world/README.md) · [Session](../docs/architecture/session/README.md) · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

The engine is a general stepper that knows nothing of plates or crust; everything that makes the world this world lives in this module, so the engine stays reusable and the world stays in one place. Its code is split the way the paper is: the `world` area, with one package per mechanism and one per chapter, and the `session` area. Drawing, the command line, and the HTTP host are not here; they use this module from the ui and cli modules.

## How it works

The code is under `src/main/java/com/aethelgard/product/`, in two areas:

1. [world/](src/main/java/com/aethelgard/product/world/README.md) wires the generation into the engine: step 0, the nine phases in their order, and the tick. Below it, one package per part of the paper:
   - [world/fields/](src/main/java/com/aethelgard/product/world/fields/README.md) — the values every phase reads and writes, and their step-0 seeds; the lowest layer
   - [world/topology/](src/main/java/com/aethelgard/product/world/topology/README.md) — how the map joins as a sphere
   - [world/boundaries/](src/main/java/com/aethelgard/product/world/boundaries/README.md) — the contacts between plates
   - [world/interaction/](src/main/java/com/aethelgard/product/world/interaction/README.md) — area budgets and velocity intents from the contacts
   - [world/motion/](src/main/java/com/aethelgard/product/world/motion/README.md) — velocities, the geometry pass, and advection
   - [world/crust/](src/main/java/com/aethelgard/product/world/crust/README.md) — thickening, new ocean, margins, arcs and sutures, subduction, and height
2. [session/](src/main/java/com/aethelgard/product/session/README.md) owns a running world, answers reads, and prints the world as text; [session/diagnostics/](src/main/java/com/aethelgard/product/session/diagnostics/README.md) measures it.

The phase packages of the world are peers and may call each other; the fields and the topology call nothing above them ([conventions](../docs/architecture/conventions.md)).

The tests are under `src/test/java/com/aethelgard/product/`, in the package of the code they test: [world/](src/test/java/com/aethelgard/product/world/README.md), [world/topology/](src/test/java/com/aethelgard/product/world/topology/README.md), [world/boundaries/](src/test/java/com/aethelgard/product/world/boundaries/README.md), [world/crust/](src/test/java/com/aethelgard/product/world/crust/README.md), [world/motion/](src/test/java/com/aethelgard/product/world/motion/README.md), [session/](src/test/java/com/aethelgard/product/session/README.md), and [session/diagnostics/](src/test/java/com/aethelgard/product/session/diagnostics/README.md). `src/test/resources/worlds/` holds the stored dump of the 8 by 8, seed-0 world after 3 steps.

**Start reading at:** `ProductHost.setup` in [ProductHost.java](src/main/java/com/aethelgard/product/world/ProductHost.java).

## Depends on

- [engine](../engine/README.md) — the stepper, its pool, events, systems, merge, and diagnostics ports

## Used by

- [cli](../cli/README.md) — the headless runner and the command language drive a session
- [ui](../ui/README.md) — the studio's controller, raster, and HTTP host drive a session

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [pom.xml](pom.xml) | The module's build: its artifact, and its dependency on the engine | — |
| `src/` | The code and the tests, introduced above; exempt: layout segment | — |
| `target/` | What the build writes; exempt: build output | — |
