<!--
  File: engine/src/test/java/com/aethelgard/engine/README.md
  Purpose: Door to the engine's tests: the shared test systems, and one test package per engine package
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Engine tests

Proves what the engine promises, one test package per engine package, and holds the small systems those tests share.

**Paper:** [One engine step](../../../../../../../docs/architecture/engine/README.md) · **Conventions:** [conventions.md](../../../../../../../docs/architecture/conventions.md)

## Why

Each engine package is proved in the test package of the same name, so its tests can reach what it keeps package-private. The systems several of those tests run are built once, here at the root, so no test package depends on another. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`TestSystems` builds the small `SubSystem`s and `EngineSystem`s the tests plug into an engine: `sub` makes a sub-system with a declared write range, `writing` a system that writes one field, and `system` a system from given sub-systems. The test packages below use them to build engines and step them.

**Start reading at:** `TestSystems` in [TestSystems.java](TestSystems.java).

## Depends on

- [engine systems/](../../../../../main/java/com/aethelgard/engine/systems/README.md), [engine events/](../../../../../main/java/com/aethelgard/engine/events/README.md) — the system, sub-system, and category types the helpers build

## Used by

- [pool/](pool/README.md), [events/](events/README.md), [merge/](merge/README.md), [systems/](systems/README.md), [user/](user/README.md), [diagnostics/](diagnostics/README.md) — the tests that plug the helpers into an engine

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [TestSystems.java](TestSystems.java) | Builds the small systems and sub-systems the engine tests run | `TestSystems`, `sub`, `writing`, `system` |
| [pool/](pool/README.md) | The tests of creating and stepping an engine, and of its host ports | — |
| [events/](events/README.md) | The tests of claiming events | — |
| [merge/](merge/README.md) | The tests of the merge rules | — |
| [systems/](systems/README.md) | The tests of running systems | — |
| [user/](user/README.md) | The tests of the user layer | — |
| [diagnostics/](diagnostics/README.md) | The tests of the engine's reports | — |
