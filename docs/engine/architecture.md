<!--
  File: docs/engine/architecture.md
  Purpose: Engine package layout and code structure
  Audience: Agents implementing engine code
  Update when: Source layout is created or changes
-->

# Engine architecture

**Status:** active (F-001 scaffold)  
**Roll-up:** [../architecture.md](../architecture.md)

---

## Modules

| Module | Artifact | Role | Status |
|--------|----------|------|--------|
| **engine** | `com.aethelgard:engine` | Pool-System Framework | Scaffolded (F-001) |
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

## Package root

| Root | Module |
|------|--------|
| `com.aethelgard.engine` | `engine` |

Subpackages appear when a Step needs them (e.g. `.pool`, `.event`, `.system`, `.merge`, `.input`, `.view`). Do not pre-carve empty packages.

---

## Source layout (current)

```
MapMakingTool/
  pom.xml                 # parent aggregator
  mvnw / mvnw.cmd
  .mvn/wrapper/
  engine/
    pom.xml
    src/main/java/com/aethelgard/engine/
    src/test/java/com/aethelgard/engine/
```

---

## Witness

From repo root: `mvnw.cmd test` (Windows) or `./mvnw test` (Unix). Incremental suite for G-001 Steps lives under `engine` tests.

### CI (F-009)

GitHub Actions workflow [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) runs the **same Maven witness** on every `push` and `pull_request` to `main`: Temurin JDK 21 + `./mvnw -B test`. Local Accept and remote CI share one bar.
