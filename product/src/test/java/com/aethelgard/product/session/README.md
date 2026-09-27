<!--
  File: product/src/test/java/com/aethelgard/product/session/README.md
  Purpose: Door to the tests of the session and the world dump
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Session tests

Proves what a session is — one world it owns, reports, and advances one caller at a time — and that the world dump is a function of the settled fields.

**Paper:** [Run](../../../../../../../../docs/architecture/session/run.md) · [Dump](../../../../../../../../docs/architecture/session/dump.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The session is the one object the command line and the studio hold, so what it promises is proved in the package of the code that keeps the promise. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

Both are JUnit 5 classes; each test's `@DisplayName` states the outcome it proves. `ProductSessionTest` creates sessions, advances them from several threads, and checks what they report. `WorldDumpTest` prints worlds from their fields, and checks that equal fields give equal text, that a change in one cell or one crust column changes it, and that printing changes no field and no diagnostic.

**Start reading at:** `ProductSessionTest` in [ProductSessionTest.java](ProductSessionTest.java).

## Depends on

- [session/](../../../../../../main/java/com/aethelgard/product/session/README.md) — the code under test
- [world/](../../../../../../main/java/com/aethelgard/product/world/README.md), [world/fields/](../../../../../../main/java/com/aethelgard/product/world/fields/README.md) — the host and the values the tests compare

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [ProductSessionTest.java](ProductSessionTest.java) | Proves what a session is: one world it owns, reports, and advances one caller at a time | `ProductSessionTest` |
| [WorldDumpTest.java](WorldDumpTest.java) | Proves that the world dump is a function of the settled fields, and that printing it changes nothing | `WorldDumpTest` |
| [diagnostics/](diagnostics/README.md) | The tests of the session diagnostics | — |
