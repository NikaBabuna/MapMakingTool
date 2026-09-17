<!--
  File: docs/engine/architecture.md
  Purpose: Engine package layout and code structure
  Audience: Agents implementing engine code
  Update when: Source layout is created or changes
-->

# Engine architecture

**Status:** active (F-012 EventEmissionPolicy; **G-002 done**; **G-005** F-019: `ui`/`cli` → `product`)  
**Roll-up:** [../architecture.md](../architecture.md)

---

## Modules

| Module | Artifact | Role | Status |
|--------|----------|------|--------|
| **engine** | `com.aethelgard:engine` | Pool-System Framework | Active — G-002 host ports |
| **cli** | `com.aethelgard:cli` | Headless product session runner | Active — F-019 |
| **ui** | `com.aethelgard:ui` | Aethelgard map view | Active — F-019 |
| **product** | `com.aethelgard:product` | Aethelgard domain host + session | Active — F-021 |

Parent aggregator: `com.aethelgard:aethelgard` (`packaging` `pom`) at repo root.

### One-way dependency rule

```
ui  →  product  →  engine
cli →  product  →  engine
```

**F-019 / ADR-010:** `ui` and `cli` depend on `product`. `product` depends on `engine`. **`engine` must never depend on them.** `cli` does not depend on `ui`. Engine stays free of UI toolkits and CLI libraries on the compile classpath. **SLF4J API** is allowed in `engine`; logging *bindings* are not (ADR-008).

---

## Java

| Setting | Value |
|---------|-------|
| Language level | **Java 21** (`maven.compiler.release` 21) |
| JDK used at scaffold | `C:\Users\USER\.jdks\jdk-21.0.11+10` (latest LTS on machine) |
| Build | Maven Wrapper (`mvnw` / `mvnw.cmd`) from repo root |

---

## Packages

| Package | Contents | Step |
|---------|----------|------|
| `com.aethelgard.engine` | Package root | F-001 |
| `com.aethelgard.engine.pool` | `EngineConfig`, `EngineSetup`, `Pool`, `PoolCompute`, `EventEmissionPolicy`, `PoolComputeContext`, `SkeletonPoolCompute`, `ScriptedEventEmissionPolicy`, `Engine`, `PoolSnapshot` | F-002–F-012 |
| `com.aethelgard.engine.event` | Categories, buffer, stub claimers, `EventClaiming`, `ClaimResult` | F-003 |
| `com.aethelgard.engine.diag` | `EngineDiagnostics`, SLF4J bridge, `RecordingDiagnostics`, `noop()` | F-003 |
| `com.aethelgard.engine.system` | `EngineSystem`, `SystemConfig`, `SubSystem`, conflict-resolution hook, `ClaimFinishBarrier` | F-004–F-005 |
| `com.aethelgard.engine.merge` | `FieldMergeType`, `FieldType` (defaults), `FieldSchema`, provenance, `StepOutputBuffer`, `TypedMerge` | F-004–F-011 |
| `com.aethelgard.engine.user` | `UserInput`, `InputKind`, `InputView`, `UserView`, `RecordingUserView` | F-006 |

### Implemented Step order (through F-012)

```text
stepStarted → stage Input View → PoolCompute (reads Input View; may emit via EventEmissionPolicy)
  → consume persistent present in view → ancestry claim
  → claiming Systems run Sub-Systems (same Pool snapshot; claim/finish counters)
  → claim/finish barrier (claimCount == finishCount) → typed merge (FieldMergeType) → apply fields
  → clear event buffer → User View(settled) → stepSettled
```

Systems run **synchronously** and always finish (non-finishing policy deferred past G-001). Delete Request merge type omitted (open question #4). No Swing/CLI in `engine`.

### Host extension points (G-002)

Product (and tests) plug into the engine **without editing `engine` source** for ordinary feature growth:

| Port | Wire via | Default | Purpose |
|------|----------|---------|---------|
| `PoolCompute` | `EngineSetup.poolCompute` | `SkeletonPoolCompute` | What the Pool does each update |
| `FieldMergeType` | `FieldSchema` | `FieldType` enum values | How conflicting field writes merge |
| `EventEmissionPolicy` | `EngineSetup.eventEmissionPolicy` | `ScriptedEventEmissionPolicy` | Which events fire this Step |

Custom `PoolCompute` may ignore the emission policy and call `emit` / `emitPath` itself. Default skeleton always calls `applyEmissionPolicy()`.

### Pool heartbeat (F-002) + pluggable compute (F-010) + emission (F-012)

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + optional scripted emission paths + optional typed field seeds |
| `Pool` | Shared state; `update()` once per Step delegates to `PoolCompute`; typed fields apply after merge |
| `PoolCompute` | Pluggable update strategy (host extension point) |
| `EventEmissionPolicy` | Pluggable which-events-fire strategy (host extension point) |
| `PoolComputeContext` | API for compute/policy: value, fields, Input View, category tree, emit / emitPath / emitScripted |
| `SkeletonPoolCompute` | **Default** G-001 demo: `value += 1`, optional `nudge` (+100), then `applyEmissionPolicy()` |
| `ScriptedEventEmissionPolicy` | **Default** emission: emit resolved `emitCategoryPathsEachUpdate` |
| `Engine` | Step loop driver |
| `PoolSnapshot` | Immutable settled state (`value`, `updateCount`, `fields`) |

**`stepIndex()` rule:** 0-based index of the last completed Step. `create` completes Step 0 → `0`. Each `advance()` increments by 1.

**Default Pool rule (`SkeletonPoolCompute`):** `value = value + 1` on each `update()`. If Input View has action `nudge` active, also `value += 100`. Then the wired emission policy runs. Callers replace compute and/or emission policy via `EngineSetup` without editing `Pool`.

### User layer (F-006)

| Type | Role |
|------|------|
| `UserInput` | Named-action register; `press` / `release` / `consume`; stages `InputView` |
| `InputKind` | `PERSISTENT` (latch until consume) / `NON_PERSISTENT` (held at stage only) |
| `InputView` | Frozen active-action set for one Step's Pool compute |
| `UserView` | `onSettled(PoolSnapshot)` — read-only frame port |
| `RecordingUserView` | Test sink (no UI toolkit) |

Wire via `EngineSetup` (`userInput`, `userView`). Defaults: empty `UserInput` + `UserView.noop()`.

### Events + claiming (F-003)

| Type | Role |
|------|------|
| `Category` / `CategoryTree` | Path hierarchy (`world/combat`); ancestry claiming |
| `EngineEvent` | Buffer notification with a category |
| `EventBuffer` | Filled once per Step during Pool compute; cleared on settle |
| `EventClaimer` | Stub or System claim identity; claims self + descendants |
| `EventClaiming` | Ancestry dispatch; multiple claimers may claim the same event |
| `ClaimResult` | Observable claimed / unmatched sets (`Engine.lastClaimResult()`) |

Scripted emissions: `EngineConfig.emitCategoryPathsEachUpdate` resolved via `EngineSetup.categoryTree()`, then emitted by default `ScriptedEventEmissionPolicy`. Replace via `EngineSetup.eventEmissionPolicy`.

### Systems (F-004)

| Type | Role |
|------|------|
| `SystemConfig` | Id, assigned category, Sub-Systems, optional conflict resolver |
| `SubSystem` | Declared write-ranges; reads Pool snapshot / System staging; writes OUT_SYS staging |
| `ConflictResolutionSubSystem` | Full conflict-set → deterministic Sub-System order |
| `EngineSystem` | Claims via wrapped `EventClaimer`; runs Sub-Systems; returns OUT_SYS |

**Independence:** every claiming System receives the same post-`update` `PoolSnapshot`; none reads another System's OUT_SYS in that Step.

### Claim/finish barrier (F-005)

| Type | Role |
|------|------|
| `ClaimFinishBarrier` | Per-Step claim/finish counters; `requireBalanced()` before merge |
| `ClaimFinishSnapshot` | Observable after settle (`Engine.lastClaimFinish()`) |

**Rule:** claim count = Systems that claimed ≥1 event; each such System finishes exactly once. Stub `EventClaimer`s do not count. Merge is forbidden unless `claimCount == finishCount`.

**Determinism:** same config + setup + N advances → equal settled Pool; independent Systems may be registered in any order without changing settled fields.

### Typed merge (F-004 / F-011)

| Type | Role |
|------|------|
| `FieldMergeType` | Pluggable merge rule interface (`merge(standing, writers) → Object`) |
| `FieldType` | Default implementations: STATIC, INCREMENT, CONSTANT, DESTRUCTIVE |
| `FieldSchema` | Field name → `FieldMergeType` (defaults or custom) |
| `ProvenancedWrite` | `(systemId, Object value)` entering the Step output buffer |
| `StepOutputBuffer` | Field → list of provenanced writes |
| `TypedMerge` | Applies each field's `FieldMergeType`; **pickOne** = lex-min `systemId` |

**Values:** typed fields are `Object` (Long still works). **Increment** default requires `Long`. Product adds custom merge types by implementing `FieldMergeType` and putting them in the schema — no engine enum edits.

Delete Request: not implemented (open question #4).

### Diagnostics (F-003 / ADR-008)

| Type | Role |
|------|------|
| `EngineDiagnostics` | Port: step start/settle, emit, claim, unmatched |
| `Slf4jDiagnostics` | Default — DEBUG lifecycle/emit/claim; WARN unmatched |
| `RecordingDiagnostics` | Test sink (no stdout scraping) |
| `noop()` / `NoopDiagnostics` | Silent sink |

Compose with `EngineDiagnostics.compose(...)`. Does not affect Pool determinism.

---

## Source layout (current)

```
MapMakingTool/
  pom.xml
  mvnw / mvnw.cmd
  .mvn/wrapper/
  .github/workflows/ci.yml
  engine/
    pom.xml
    src/main/java/com/aethelgard/engine/
      pool/
      event/
      diag/
      system/
      merge/
      user/
    src/test/java/com/aethelgard/engine/
      ScaffoldWitnessTest.java   # F-001
      CiWitnessTest.java          # F-009
      pool/                       # F-002
      event/                      # F-003
      system/                     # F-004–F-005
      user/                       # F-006
  cli/
    pom.xml
    src/main/java/com/aethelgard/cli/   # F-007
    src/test/java/com/aethelgard/cli/
  ui/
    pom.xml
    src/main/java/com/aethelgard/ui/    # F-008
    src/test/java/com/aethelgard/ui/
  product/
    pom.xml
    src/main/java/com/aethelgard/product/   # F-013
    src/test/java/com/aethelgard/product/
```

### CLI (F-019)

| Type | Role |
|------|------|
| `CliRunner` | Parse `--steps N`; `ProductSession.ofDefault()`; print `settledWorld()` |
| `Main` | Process entry → `CliRunner` → exit code |
| `CliOptions` / `CliResult` | Placeholder flags (unstable) |

Artifact `com.aethelgard:cli` depends on `product`. Run headlessly from tests via `CliRunner.run(args)`. Heartbeat `--initial` is retired.

### UI (F-019)

| Type | Role |
|------|------|
| `ElevationRaster` | Headless RGB of an elevation grid (absolute ramp) |
| `MapController` | Headless advance / raster / busy via `ProductSession` (no Swing) |
| `MapFrame` | Swing shell — map + Advance (not constructed in tests) |
| `ProductApp` | `main` entry on the EDT |

Artifact `com.aethelgard:ui` depends on `product`. Launch: `run-product.cmd` / `run-ui.cmd`.

---

## Witness

From repo root: `mvnw.cmd test` (Windows) or `./mvnw test` (Unix). Incremental suite lives under `engine`, `cli`, `ui`, and `product` tests.

### CI (F-009)

GitHub Actions workflow [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) runs the **same Maven witness** on every `push` and `pull_request` to `main`: Temurin JDK 21 + `./mvnw -B test`. Local Accept and remote CI share one bar.
