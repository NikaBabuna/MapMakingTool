<!--
  File: docs/architecture/engine/setup.md
  Purpose: EngineSetup, EngineConfig, Engine — wiring one run, seeding step 0, and the create/advance lifecycle
  Audience: Agents and humans
  Update when: Engine.create, Engine.advance, the EngineSetup ports, or EngineConfig changes
-->

# Setup and lifecycle

Before an engine can step, someone must say which rules it runs and what the world looks like at the very start. This page is that wiring, and the life of an engine from its first step to the last one a caller asks for.

## What it reads

An `EngineConfig`: the initial heartbeat `initialValue`, the category paths `emitCategoryPathsEachUpdate` that the default emission policy fires on every update, and the field seeds `initialFields`. It is the only input to step 0; there is no hidden global.

An `EngineSetup`: nine ports — `categoryTree`, `claimers`, `systems`, `fieldSchema`, `diagnostics`, `userInput`, `userView`, `poolCompute`, `eventEmissionPolicy`.

## What it writes

An `Engine` that has already completed step 0. Every later call of `advance` completes further steps. `advance(n)` with $n < 0$ throws `IllegalArgumentException`. The `Pool` constructor throws `IllegalArgumentException` when `initialFields` names a field the schema does not declare.

## Model

A run is the pair of a configuration and a wiring,

$$\mathcal{C} = (h_0,\; \mathcal{P},\; \Phi_0), \qquad \mathcal{E} = (\mathcal{T},\; \mathit{Stubs},\; \Sigma,\; \tau,\; \mathcal{D},\; \mathcal{I},\; V,\; \Gamma,\; \epsilon),$$

where $h_0$ is the initial heartbeat, $\mathcal{P}$ the list of scripted category paths, $\Phi_0$ the partial map of field seeds, $\mathcal{T}$ the category tree, $\mathit{Stubs}$ the stub claimers, $\Sigma$ the ordered systems, $\tau$ the field schema ($\tau_f$ is the merge type of field $f$, and $\Phi = \operatorname{dom}\tau$), $\mathcal{D}$ the diagnostics, $\mathcal{I}$ the input register, $V$ the view, $\Gamma$ the Pool compute, and $\epsilon$ the emission policy.

The state before step 0 is

$$S_{-1} = \bigl(h_0,\; 0,\; F_{-1}\bigr), \qquad F_{-1}(f) = \begin{cases} \Phi_0(f) & f \in \operatorname{dom}\Phi_0 \\ 0 & \text{otherwise} \end{cases} \quad (f \in \Phi),$$

and it is legal only when $\operatorname{dom}\Phi_0 \subseteq \Phi$. With $\mathrm{step}$ the composition on the [level page](README.md), the lifecycle is

$$S_0 = \mathrm{step}(S_{-1}), \qquad \texttt{advance}(n):\; S_{k+n} = \mathrm{step}^{\,n}(S_k), \qquad \texttt{stepIndex} = k, \qquad u_k = k + 1 .$$

A null port is replaced by its default, so $\mathcal{E}$ is always total:

`EngineSetup` (compact constructor) in [`EngineSetup.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java):

```java
public EngineSetup {
  categoryTree = categoryTree == null ? CategoryTree.empty() : categoryTree;
  claimers = List.copyOf(Objects.requireNonNullElse(claimers, List.of()));
  systems = List.copyOf(Objects.requireNonNullElse(systems, List.of()));
  fieldSchema = fieldSchema == null ? FieldSchema.empty() : fieldSchema;
  diagnostics = diagnostics == null ? EngineDiagnostics.slf4j() : diagnostics;
  userInput = userInput == null ? new UserInput() : userInput;
  userView = userView == null ? UserView.noop() : userView;
  poolCompute = poolCompute == null ? SkeletonPoolCompute.INSTANCE : poolCompute;
  eventEmissionPolicy =
      eventEmissionPolicy == null ? ScriptedEventEmissionPolicy.INSTANCE : eventEmissionPolicy;
}
```

## Procedure

1. The configuration copies its path list and seed map into immutable collections. A null list or map becomes empty. [`EngineConfig`](../../../engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java).
2. The wiring replaces each null port with its default: an empty tree, no stub claimers, no systems, an empty schema, the SLF4J diagnostics bridge, a fresh input register, a view that does nothing, the skeleton compute, and the scripted emission policy. The shorter constructors (three, five, seven, and eight arguments) fill the ports they omit with the same defaults, and `EngineSetup.defaults()` wires every port to its default. [`EngineSetup`](../../../engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java).
3. `Engine.create(config)` is `Engine.create(config, EngineSetup.defaults())`. [`Engine.create`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).
4. `Engine.create(config, setup)` resolves the scripted paths with `CategoryTree.resolveAll`, which creates any category the tree does not hold yet. It then builds the `Pool` from the config, schema, compute, tree, and policy, keeps immutable copies of the resolved emissions, the stub claimers, and the systems, and runs step 0. [`Engine.create`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).
5. `advance()` runs one step. `advance(n)` rejects $n < 0$ and then runs $n$ steps. [`Engine.advance`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).
6. Each step is `runStep`, whose stages are the [level page](README.md). It sets the step index to one more than the last completed index only after merge has applied. [`Engine.runStep`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).
7. A caller reads the run through `stepIndex`, `settled` (the settled `PoolSnapshot`), `lastInputView`, `lastClaimResult`, `lastStepOutput` (the provenanced writes before merge), `lastClaimFinish`, and through `systems`, `fieldSchema`, `userInput`, and `eventBufferEmpty`. [`Engine.settled`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java).

## What is true afterwards

After `create`, `stepIndex()` is 0, `settled().updateCount()` is 1, and `eventBufferEmpty()` is true. After `advance(n)` the index has grown by exactly $n$. The emission list, the claimers, and the systems of a run never change after `create`. The `UserInput` a caller presses between steps is the same object the setup holds, so a press is seen by the next stage.

## Cost

Creation is $O(|\Phi| + |\mathcal{P}|\,d)$, with $d$ the depth of a category path, plus the cost of step 0. Each `advance(n)` costs $n$ steps.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Step driver | `Engine` | `create`, `advance`, `runStep`, `stepIndex`, `settled`, `systems`, `fieldSchema`, `lastClaimResult`, `lastStepOutput`, `lastClaimFinish`, `lastInputView`, `userInput`, `eventBufferEmpty` | [`engine/src/main/java/com/aethelgard/engine/pool/Engine.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/Engine.java) |
| Wiring | `EngineSetup` | `EngineSetup`, `defaults`, `categoryTree`, `claimers`, `systems`, `fieldSchema`, `diagnostics`, `userInput`, `userView`, `poolCompute`, `eventEmissionPolicy` | [`engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/EngineSetup.java) |
| Step-0 seed | `EngineConfig` | `EngineConfig`, `initialValue`, `emitCategoryPathsEachUpdate`, `initialFields` | [`engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/EngineConfig.java) |

Parent: [one engine step](README.md). Why step 0 is seeded from one object: [ADR-005](../../paperwork/decisions/ADR-005-step-zero-config.md).
