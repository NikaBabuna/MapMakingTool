<!--
  File: engine/README.md
  Purpose: Door to the engine module, the host that runs a step-based simulation
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# Engine module

The engine module is a host for any step-based simulation: it runs the steps, and knows nothing of the world it runs.

**Docs:** [engine](../docs/architecture/engine/README.md) · [program](../docs/architecture/program.md)

| Path | Read it when |
|------|----------------|
| [pom.xml](pom.xml) | You need the engine's build: its artifact, and its one library, the SLF4J API |
| `src/` | You need the engine's code or its tests. The code is `src/main/java/com/aethelgard/engine/`, one package per job, each with its own door: [pool](src/main/java/com/aethelgard/engine/pool/README.md), [event](src/main/java/com/aethelgard/engine/event/README.md), [system](src/main/java/com/aethelgard/engine/system/README.md), [merge](src/main/java/com/aethelgard/engine/merge/README.md), [user](src/main/java/com/aethelgard/engine/user/README.md), [diag](src/main/java/com/aethelgard/engine/diag/README.md). The tests are `src/test/java/`, one class per outcome |
