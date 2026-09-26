<!--
  File: engine/README.md
  Purpose: Code module index for the Pool-System Framework
  Audience: Agents and humans
  Update when: Engine packages or layout change
-->

# Engine module

Maven artifact `com.aethelgard:engine` — Pool-System Framework (Java 21). Clean host after G-002.

**Docs:** [docs/architecture/engine/README.md](../docs/architecture/engine/README.md) · [program](../docs/architecture/program.md)

| Package | Role |
|---------|------|
| `com.aethelgard.engine.pool` | Step loop, config, Pool, `PoolCompute`, `EventEmissionPolicy`, snapshots |
| `com.aethelgard.engine.event` | Categories, buffer, stub claimers |
| `com.aethelgard.engine.diag` | Diagnostics / SLF4J bridge |
| `com.aethelgard.engine.system` | Systems, Sub-Systems, conflict-resolution hook |
| `com.aethelgard.engine.merge` | `FieldMergeType`, defaults, provenance, typed merge |
| `com.aethelgard.engine.user` | User Input, Input View, User View |

**Host ports:** `PoolCompute`, `FieldMergeType`, `EventEmissionPolicy` — product plugs in via `EngineSetup` / schema without editing this module.

**Witness:** from repo root, `mvnw.cmd test` / `./mvnw test`.  
**CI:** [../.github/workflows/README.md](../.github/workflows/README.md).

Do not add UI/CLI/product dependencies here.
