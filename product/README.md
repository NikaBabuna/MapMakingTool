<!--
  File: product/README.md
  Purpose: Landmark index for the product Maven module
  Audience: Agents and humans
  Update when: Product layout or host wiring changes
-->

# Product module

Maven artifact `com.aethelgard:product` — Aethelgard world generation on the Pool-System host.

**Depends on:** `engine` (one-way). **No Swing.** `ui` and `cli` depend on this module (ADR-010).

Package root: `com.aethelgard.product`.

`ProductHost` constructs an `Engine` via product `EngineSetup`. `ProductSession` owns a run (serialized `advance`, grid reads, settled dump). Generation: `elevation` + Voronoi `plates` + CONSTANT `plate_velocity`, `world/tectonics` after Step 0, kinematics (advection) and tectonics (`Orogeny`: converge / diverge / transform). `WorldDump` prints a settled snapshot.

The map **window** lives in [`ui/`](../ui/README.md).

## Interactive map

From the repo root in **cmd**:

```bat
run-product.cmd
```

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md) · [wiki/world](../docs/product/wiki/world.md) · [wiki/elevation](../docs/product/wiki/elevation.md)
