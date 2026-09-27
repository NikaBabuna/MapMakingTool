<!--
  File: product/src/test/java/com/aethelgard/product/session/diagnostics/README.md
  Purpose: Door to the tests of the session diagnostics
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Diagnostics tests

Proves what the session's diagnostics record, how much they keep, and that they never change the world.

**Paper:** [Session diagnostics](../../../../../../../../../docs/architecture/session/diagnostics.md) · **Conventions:** [conventions.md](../../../../../../../../../docs/architecture/conventions.md)

## Why

Measuring a world must never change it, so that promise is proved beside the code that measures. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`SessionDiagnosticsTest` is a JUnit 5 class of three tests. It advances sessions and reads the hub's collectors: timings are kept only while a collector is enabled, a clear empties them, a ring keeps only its newest samples, and the world is the same whether diagnostics are enabled, disabled, or cleared.

**Start reading at:** `SessionDiagnosticsTest` in [SessionDiagnosticsTest.java](SessionDiagnosticsTest.java).

## Depends on

- [session/diagnostics/](../../../../../../../main/java/com/aethelgard/product/session/diagnostics/README.md) — the code under test
- [session/](../../../../../../../main/java/com/aethelgard/product/session/README.md), [world/fields/](../../../../../../../main/java/com/aethelgard/product/world/fields/README.md) — the session that is measured, and the values compared

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [SessionDiagnosticsTest.java](SessionDiagnosticsTest.java) | Proves what the session's diagnostics record, how much they keep, and that they never change the world | `SessionDiagnosticsTest` |
