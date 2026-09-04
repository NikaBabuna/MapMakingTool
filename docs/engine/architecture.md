<!--
  File: docs/engine/architecture.md
  Purpose: Engine package layout and code structure
  Audience: Agents implementing engine code
  Update when: Source layout is created or changes
-->

# Engine architecture

**Status:** active (F-003 events + diagnostics)  
**Roll-up:** [../architecture.md](../architecture.md)

---

## Modules

| Module | Artifact | Role | Status |
|--------|----------|------|--------|
| **engine** | `com.aethelgard:engine` | Pool-System Framework | Active — events + diagnostics (F-003) |
| **cli** | _(planned)_ | Headless runner (N Steps, settled state) | Not created — F-007 |
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
| `com.aethelgard.engine.pool` | `EngineConfig`, `EngineSetup`, `Pool`, `Engine`, `PoolSnapshot` | F-002–F-003 |
| `com.aethelgard.engine.event` | Categories, buffer, stub claimers, `EventClaiming`, `ClaimResult` | F-003 |
| `com.aethelgard.engine.diag` | `EngineDiagnostics`, SLF4J bridge, `RecordingDiagnostics`, `noop()` | F-003 |

### Implemented Step order (through F-003)

```text
stepStarted → Pool.update (may emit) → ancestry claim → unmatched diagnostics
  → clear buffer → stepSettled
```

**Not yet in code** (specs describe the full loop): Sub-Systems, claim/finish barrier, typed merge apply, User View, Input View. Those belong to F-004–F-006.

### Pool heartbeat (F-002)

| Type | Role |
|------|------|
| `EngineConfig` | Step 0 seed + optional scripted emission paths each update |
| `Pool` | Shared state; `update()` once per Step (may emit events) |
| `Engine` | Step loop: update → claim → clear buffer → settle |
| `PoolSnapshot` | Immutable settled state after a completed Step |

**`stepIndex()` rule:** 0-based index of the last completed Step. `create` completes Step 0 → `0`. Each `advance()` increments by 1.

**Trivial Pool rule:** `value = value + 1` on each `update()`. Domain state replaces this later.

### Events + claiming (F-003)

| Type | Role |
|------|------|
| `Category` / `CategoryTree` | Path hierarchy (`world/combat`); ancestry claiming |
| `EngineEvent` | Buffer notification with a category |
| `EventBuffer` | Filled once per Step during Pool compute; cleared on settle |
| `EventClaimer` | Stub: assigned category; claims self + descendants (full Systems in F-004) |
| `EventClaiming` | Ancestry dispatch; multiple claimers may claim the same event |
| `ClaimResult` | Observable claimed / unmatched sets (`Engine.lastClaimResult()`) |

Scripted emissions: `EngineConfig.emitCategoryPathsEachUpdate` resolved via `EngineSetup.categoryTree()`.

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
    src/test/java/com/aethelgard/engine/
      ScaffoldWitnessTest.java   # F-001
      CiWitnessTest.java          # F-009
      pool/                       # F-002
      event/                      # F-003
```

---

## Witness

From repo root: `mvnw.cmd test` (Windows) or `./mvnw test` (Unix). Incremental suite for G-001 Steps lives under `engine` tests.

### CI (F-009)

GitHub Actions workflow [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) runs the **same Maven witness** on every `push` and `pull_request` to `main`: Temurin JDK 21 + `./mvnw -B test`. Local Accept and remote CI share one bar.
