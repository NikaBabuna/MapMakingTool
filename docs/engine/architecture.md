<!--
  File: docs/engine/architecture.md
  Purpose: Engine package layout and code structure
  Audience: Agents implementing engine code
  Update when: Source layout is created or changes
-->

# Engine architecture

**Status:** active (F-007 CLI runner)  
**Roll-up:** [../architecture.md](../architecture.md)

---

## Modules

| Module | Artifact | Role | Status |
|--------|----------|------|--------|
| **engine** | `com.aethelgard:engine` | Pool-System Framework | Active — user layer (F-006) |
| **cli** | `com.aethelgard:cli` | Headless runner (N Steps, settled state) | Active — F-007 |
| **ui** | _(planned)_ | Basic Step advance / view | Not created — F-008 |
| **product** | _(planned)_ | Aethelgard domain Systems / views | After G-001 |

Parent aggregator: `com.aethelgard:aethelgard` (`packaging` `pom`) at repo root.

### One-way dependency rule

```
product  →  engine  ←  cli
                ↑
                ui
```

`cli`, `ui`, and `product` may depend on `engine`. **`engine` must never depend on them** (never the reverse). Engine stays free of UI toolkits and CLI libraries on the compile classpath. **SLF4J API** is allowed in `engine`; logging *bindings* are not (ADR-008).

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
| `com.aethelgard.engine.pool` | `EngineConfig`, `EngineSetup`, `Pool`, `Engine`, `PoolSnapshot` | F-002–F-006 |
| `com.aethelgard.engine.event` | Categories, buffer, stub claimers, `EventClaiming`, `ClaimResult` | F-003 |
| `com.aethelgard.engine.diag` | `EngineDiagnostics`, SLF4J bridge, `RecordingDiagnostics`, `noop()` | F-003 |
| `com.aethelgard.engine.system` | `EngineSystem`, `SystemConfig`, `SubSystem`, conflict-resolution hook, `ClaimFinishBarrier` | F-004–F-005 |
| `com.aethelgard.engine.merge` | `FieldType`, `FieldSchema`, provenance, `StepOutputBuffer`, `TypedMerge` | F-004 |
| `com.aethelgard.engine.user` | `UserInput`, `InputKind`, `InputView`, `UserView`, `RecordingUserView` | F-006 |

### Implemented Step order (through F-006)

```text
stepStarted → stage Input View → Pool.update (reads Input View; may emit)
  → consume persistent present in view → ancestry claim
  → claiming Systems run Sub-Systems (same Pool snapshot; claim/finish counters)
  → claim/finish barrier (claimCount == finishCount) → typed merge → apply fields
  → clear event buffer → User View(settled) → stepSettled
```

Systems run **synchronously** and always finish (non-finishing policy deferred past G-001). Delete Request merge type omitted (open question #4). No Swing/CLI in `engine`.

### Pool heartbeat (F-002)

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + optional scripted emission paths + optional typed field seeds |
| `Pool` | Shared state; `update()` once per Step; typed fields apply after merge |
| `Engine` | Step loop driver |
| `PoolSnapshot` | Immutable settled state (`value`, `updateCount`, `fields`) |

**`stepIndex()` rule:** 0-based index of the last completed Step. `create` completes Step 0 → `0`. Each `advance()` increments by 1.

**Trivial Pool rule:** `value = value + 1` on each `update()`. If Input View has action `nudge` active, also `value += 100` (F-006 skeleton demo). Domain state replaces this later.

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

Scripted emissions: `EngineConfig.emitCategoryPathsEachUpdate` resolved via `EngineSetup.categoryTree()`.

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

### Typed merge (F-004)

| Type | Role |
|------|------|
| `FieldSchema` / `FieldType` | STATIC, INCREMENT, CONSTANT, DESTRUCTIVE |
| `ProvenancedWrite` | `(systemId, value)` entering the Step output buffer |
| `StepOutputBuffer` | Field → list of provenanced writes |
| `TypedMerge` | Resolve by type; **Static/Destructive pick-one = lexicographically smallest `systemId`** |

Increment: standing + sum of writes. Constant: keep standing. Delete Request: not implemented.

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
```

### CLI (F-007)

| Type | Role |
|------|------|
| `CliRunner` | Parse `--steps` / `--initial`; create engine; advance N; format settled report |
| `Main` | Process entry → `CliRunner` → exit code |
| `CliOptions` / `CliResult` | Options and exit/output |

Artifact `com.aethelgard:cli` depends on `engine`. Run headlessly from tests via `CliRunner.run(args)`.
---

## Witness

From repo root: `mvnw.cmd test` (Windows) or `./mvnw test` (Unix). Incremental suite for G-001 Steps lives under `engine` tests.

### CI (F-009)

GitHub Actions workflow [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) runs the **same Maven witness** on every `push` and `pull_request` to `main`: Temurin JDK 21 + `./mvnw -B test`. Local Accept and remote CI share one bar.
