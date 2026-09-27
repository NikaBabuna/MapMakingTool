<!--
  File: engine/src/test/java/com/aethelgard/engine/diagnostics/README.md
  Purpose: Door to the tests of the engine's reports
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Diagnostics tests

Proves what the engine's diagnostics report of a step, and that reporting never changes the Pool.

**Paper:** [Diagnostics](../../../../../../../../docs/architecture/engine/diagnostics.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

An unmatched event must always be reported, and a report must never change a run, so both are proved beside the port that reports. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`EngineDiagnosticsTest` is a JUnit 5 class of two tests. It steps engines with a recording binding and checks the reports in order, and checks that no binding changes the Pool.

**Start reading at:** `EngineDiagnosticsTest` in [EngineDiagnosticsTest.java](EngineDiagnosticsTest.java).

## Depends on

- [engine diagnostics/](../../../../../../main/java/com/aethelgard/engine/diagnostics/README.md) — the code under test
- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md), [the shared test systems](../README.md) — the engines that report

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [EngineDiagnosticsTest.java](EngineDiagnosticsTest.java) | Proves what the engine's diagnostics report of a step, and that they never change the Pool | `EngineDiagnosticsTest` |
