<!--
  File: docs/architecture/world/wiring.md
  Purpose: ProductHost.setup, ProductCategories, GenerationTickPolicy — how the world plugs into the engine: one system, nine phases, one tick, one conflict order
  Audience: Agents and humans
  Update when: ProductHost.setup, ProductCategories.tree, or GenerationTickPolicy.emitEvents changes
-->

# Wiring

The world does not change the engine. It hands the engine a wiring: one category, one tick that fires that category on every step after the first, one system whose nine phases rewrite the world, and a merge rule for each field. This page is that wiring.

## What it reads

Nothing at run time. `ProductHost.setup` builds the wiring from constants: the category path `world/tectonics`, the nine phase objects, the phase-timing ids of the session diagnostics, and the field names of `WorldFields`.

## What it writes

An `EngineSetup` with: the category tree holding `world/tectonics`; no stub claimers; one `EngineSystem` with id `tectonics`; a `FieldSchema` that makes all nine world fields `STATIC`; the default diagnostics, input, view, and compute; and `GenerationTickPolicy` as the emission policy.

## Model

**Tick.** The default compute adds 1 to the heartbeat on every update, and the policy emits the one category once the update count reaches 2:

$$B_k = \begin{cases} (\texttt{world/tectonics}) & u_k = k + 1 \ge 2 \\ () & \text{otherwise} \end{cases} \qquad\Longrightarrow\qquad \text{the system runs in every step } k \ge 1 .$$

The heartbeat starts at $h_0 = 0$, so after the compute of step $k$ it is $h = k + 1$, and a phase reads the generation index as $g = h - 1 = k$.

`GenerationTickPolicy.emitEvents` in [`GenerationTickPolicy.java`](../../../product/src/main/java/com/aethelgard/product/GenerationTickPolicy.java):

```java
if (context.updateCount() >= FIRST_GENERATION_UPDATE) {
  context.emitPath(ProductCategories.TECTONICS);
}
```

**Phases and ranges.** With $\omega$ the write range of a phase:

| # | Phase | $\omega$ | Reads settled | Reads staged, else settled |
|---|-------|----------|---------------|----------------------------|
| 1 | `TraceBoundaries` | `boundaries` | `plates`, `plate_velocity` | — |
| 2 | `BoundaryInteraction` | `area_flux`, `motion_intent` | `plate_registry`, `occupancy`, `lockers` | `boundaries` |
| 3 | `IntegrateVelocity` | `plate_velocity`, `plate_registry` | `plate_velocity`, `plate_registry` | `motion_intent` |
| 4 | `ApplyGeometry` | `plates`, `plate_registry`, `plate_velocity`, `occupancy` | `plates`, `occupancy`, heartbeat | `plate_velocity`, `plate_registry`, `boundaries`, `area_flux`, `lockers` |
| 5 | `Orogeny` | `lockers` | `occupancy`, `lockers`, `plate_registry` | `boundaries` |
| 6 | `RidgeCreate` | `occupancy`, `lockers` | — | `occupancy`, `lockers` |
| 7 | `MarginRelief` | `lockers` | — | `occupancy`, `lockers`, `plates`, `plate_velocity` |
| 8 | `ContinentalCollide` | `lockers` | — | `occupancy`, `lockers`, `plates`, `plate_velocity` |
| 9 | `ThicknessToElevation` | `elevation` | — | `occupancy`, `lockers` |

**Conflict order.** Phases 3–8 share written fields (3 and 4 share velocities and the registry, 4 and 6 share occupancy, 5–8 share lockers), so the conflict set is $Q^{\cap} = \{3, 4, 5, 6, 7, 8\}$. The resolver keeps the members of a fixed preference list that are in the set, which returns the same order for any order of its input:

`ProductHost.setup` (the resolver) in [`ProductHost.java`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java):

```java
List<SubSystem> preferred =
    List.of(
        timedIntegrate,
        timedApply,
        timedOrogeny,
        ridgeCreate,
        marginRelief,
        continentalCollide);
List<SubSystem> ordered = new ArrayList<>();
for (SubSystem sub : preferred) {
  if (conflict.contains(sub)) {
    ordered.add(sub);
  }
}
return ordered;
```

The preference list is the registration order of phases 3–8, and the block is spliced back at phase 3, so the execution order is the registration order $1, 2, \dots, 9$.

**Merge.** Every world field is `STATIC` and only `tectonics` writes it, so $\mathrm{Wr}_f$ has one element and $\mathcal{W}_g(f)$ is the last value staged for $f$ in generation $g$.

## Procedure

1. The category tree is the single path `world/tectonics` and its parent `world`. [`ProductCategories.tree`](../../../product/src/main/java/com/aethelgard/product/ProductCategories.java).
2. The setup builds the nine phases. Phases 1–5 and 9 are wrapped in a `TimingSubSystem` that records their duration under the phase ids `phase.trace`, `phase.interaction`, `phase.integrate`, `phase.apply`, `phase.orogeny`, and `phase.isostasy` ([session diagnostics](../session/diagnostics.md)). Phases 6–8 are not timed. [`ProductHost.setup`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).
3. It builds the system `tectonics` (`TECTONICS_SYSTEM_ID`) on category `world/tectonics`, with the nine phases in order and the preference-list resolver. [`ProductHost.setup`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).
4. It declares the nine fields `STATIC` and returns the setup with `GenerationTickPolicy.INSTANCE` as emission policy and null for every other port, so the engine defaults apply. [`ProductHost.setup`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).
5. On every update, the policy emits `world/tectonics` once the update count is at least `FIRST_GENERATION_UPDATE` (2). [`GenerationTickPolicy.emitEvents`](../../../product/src/main/java/com/aethelgard/product/GenerationTickPolicy.java).

## What is true afterwards

An engine built from this setup runs no phase in step 0 and all nine phases, in order, in every later step. Each world field changes only through the last value a phase staged. The constant `KINEMATICS_SYSTEM_ID` (`kinematics`) names no system; advection runs inside `ApplyGeometry`.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Wiring | `ProductHost` | `setup`, `TECTONICS_SYSTEM_ID`, `KINEMATICS_SYSTEM_ID` | [`product/src/main/java/com/aethelgard/product/ProductHost.java`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java) |
| Category | `ProductCategories` | `TECTONICS`, `tree` | [`product/src/main/java/com/aethelgard/product/ProductCategories.java`](../../../product/src/main/java/com/aethelgard/product/ProductCategories.java) |
| Tick | `GenerationTickPolicy` | `INSTANCE`, `FIRST_GENERATION_UPDATE`, `emitEvents` | [`product/src/main/java/com/aethelgard/product/GenerationTickPolicy.java`](../../../product/src/main/java/com/aethelgard/product/GenerationTickPolicy.java) |

Parent: [one generation](README.md). Why the product plugs in without editing the engine: [ADR-010](../../paperwork/decisions/ADR-010-product-adapters.md). Why the product authors its category tree: [ADR-009](../../paperwork/decisions/ADR-009-category-tree.md).
