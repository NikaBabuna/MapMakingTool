<!--
  File: docs/engine/architecture.md
  Purpose: Engine package layout and code structure
  Audience: Agents implementing engine code
  Update when: Source layout is created or changes
-->

# Engine architecture

**Status:** active (F-002 Pool heartbeat)  
**Roll-up:** [../architecture.md](../architecture.md)

---

## Modules

| Module | Artifact | Role | Status |
|--------|----------|------|--------|
| **engine** | `com.aethelgard:engine` | Pool-System Framework | Active — Pool Step loop (F-002) |
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

`cli`, `ui`, and `product` may depend on `engine`. **`engine` must never depend on them** (never the reverse). Engine stays free of UI toolkits and CLI libraries on the compile classpath.

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
| `com.aethelgard.engine.pool` | `EngineConfig`, `Pool`, `Engine`, `PoolSnapshot` | F-002 |

Further packages (`.event`, `.system`, …) appear when their Step lands.

### Pool heartbeat (F-002)

| Type | Role |
|------|------|
| `EngineConfig` | Caller-supplied Step 0 seed (ADR-005) |
| `Pool` | Shared state; `update()` once per Step |
| `Engine` | Step loop: `create` → Step 0; `advance` / `advance(n)` |
| `PoolSnapshot` | Immutable settled state after a completed Step |

**`stepIndex()` rule:** 0-based index of the last completed Step. `create` completes Step 0 → `0`. Each `advance()` increments by 1.

**Trivial Pool rule (F-002 only):** `value = value + 1` on each `update()`. Domain state replaces this later.

Logging / diagnostics deferred to F-003.

---

## Source layout (current)

```
MapMakingTool/
  pom.xml
  mvnw / mvnw.cmd
  .mvn/wrapper/
  engine/
    pom.xml
    src/main/java/com/aethelgard/engine/
      pool/   # F-002
    src/test/java/com/aethelgard/engine/
      pool/
```

---

## Witness

From repo root: `mvnw.cmd test` (Windows) or `./mvnw test` (Unix). Incremental suite for G-001 Steps lives under `engine` tests.

### CI (F-009)

GitHub Actions workflow [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) runs the **same Maven witness** on every `push` and `pull_request` to `main`: Temurin JDK 21 + `./mvnw -B test`. Local Accept and remote CI share one bar.
