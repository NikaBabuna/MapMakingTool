<!--
  File: engine/README.md
  Purpose: Code module index for the Pool-System Framework
  Audience: Agents and humans
  Update when: Engine packages or layout change
-->

# Engine module

Maven artifact `com.aethelgard:engine` — Pool-System Framework (Java 21).

**Docs:** [docs/engine/README.md](../docs/engine/README.md) · [architecture](../docs/engine/architecture.md)

| Package | Role |
|---------|------|
| `com.aethelgard.engine.pool` | Step loop, config, Pool, snapshots |
| `com.aethelgard.engine.event` | Categories, buffer, stub claimers |
| `com.aethelgard.engine.diag` | Diagnostics / SLF4J bridge |
| `com.aethelgard.engine.system` | Systems, Sub-Systems, conflict-resolution hook |
| `com.aethelgard.engine.merge` | Field types, provenance, typed merge |

**Witness:** from repo root, `mvnw.cmd test` / `./mvnw test`.  
**CI:** [../.github/workflows/README.md](../.github/workflows/README.md).

Do not add UI/CLI/product dependencies here.
