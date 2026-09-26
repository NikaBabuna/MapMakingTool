<!--
  File: product/README.md
  Purpose: Door to the product module, the world that runs on the engine
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# Product module

The product module is the world that runs on the engine: its fields, the tectonics generation, and the session that owns a running world.

**Docs:** [world](../docs/architecture/world/README.md) · [session](../docs/architecture/session/README.md)  
**Rules:** [wiki](../docs/product/wiki/README.md)

| Path | Read it when |
|------|----------------|
| [pom.xml](pom.xml) | You need the product's build: its artifact, and its dependency on the engine |
| `src/` | You need the world's code or its tests. The code is `src/main/java/com/aethelgard/product/`. The tests are `src/test/java/`, one class per outcome, and `src/test/resources/worlds/` holds the stored dump of the 8 by 8, seed-0 world after 3 steps |
