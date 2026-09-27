<!--
  File: engine/src/test/java/com/aethelgard/engine/systems/README.md
  Purpose: Door to the tests of running systems
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Systems tests

Proves how systems run: only when they claim, on one shared snapshot, with sub-systems in a defined order, counted by the barrier.

**Paper:** [Systems](../../../../../../../../docs/architecture/engine/systems.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The systems carry every simulation's work, so their rules are proved beside the code that runs them, in the same package, which lets a test reach the package-private ordering. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`SystemRunTest` is a JUnit 5 class of eight tests. It builds systems from the shared test systems, steps engines, and checks which systems ran, what each sub-system read, the order of sub-systems with overlapping writes, and the barrier's counts.

**Start reading at:** `SystemRunTest` in [SystemRunTest.java](SystemRunTest.java).

## Depends on

- [engine systems/](../../../../../../main/java/com/aethelgard/engine/systems/README.md) — the code under test
- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md), [the shared test systems](../README.md) — the engines and the systems they run

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [SystemRunTest.java](SystemRunTest.java) | Proves how systems run: only when they claim, on one shared snapshot, sub-systems in a defined order, counted by the barrier | `SystemRunTest` |
