<!--
  File: engine/src/test/java/com/aethelgard/engine/pool/README.md
  Purpose: Door to the tests of creating and stepping an engine, and of its host ports
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Pool tests

Proves how a run starts and steps — creation at step 0, advancing by n, and determinism — and the host ports: the default and a custom Pool compute, and the default and a custom emission policy.

**Paper:** [Setup and lifecycle](../../../../../../../../docs/architecture/engine/setup.md) · [Pool](../../../../../../../../docs/architecture/engine/pool.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The engine and the Pool are proved beside the code that drives them. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

Both are JUnit 5 classes; each test's `@DisplayName` states the outcome it proves. `EngineRunTest` creates engines, advances them, and runs two engines with the same inputs to compare their settled Pools. `HostPortsTest` wires the default and custom computes and emission policies into an engine and checks what each step then does.

**Start reading at:** `EngineRunTest` in [EngineRunTest.java](EngineRunTest.java).

## Depends on

- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md) — the code under test
- [the shared test systems](../README.md) — the systems plugged into the engines

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [EngineRunTest.java](EngineRunTest.java) | Proves how a run starts and steps: creation at step 0, advance by n, and determinism | `EngineRunTest` |
| [HostPortsTest.java](HostPortsTest.java) | Proves the host ports: the default and a custom Pool compute, and the default and a custom emission policy | `HostPortsTest` |
