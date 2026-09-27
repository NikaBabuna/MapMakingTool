<!--
  File: engine/src/test/java/com/aethelgard/engine/merge/README.md
  Purpose: Door to the tests of the merge rules
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Merge tests

Proves how conflicting writes settle: the four built-in rules, the provenance of each write, and a custom rule.

**Paper:** [Merge](../../../../../../../../docs/architecture/engine/merge.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The merge rules decide what a field holds after a step, so they are proved beside the code that applies them. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`MergeRulesTest` is a JUnit 5 class of seven tests. It runs engines whose systems write the same field, under each built-in rule and a custom one, and checks the settled value and the id each write carried.

**Start reading at:** `MergeRulesTest` in [MergeRulesTest.java](MergeRulesTest.java).

## Depends on

- [engine merge/](../../../../../../main/java/com/aethelgard/engine/merge/README.md) — the code under test
- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md), [the shared test systems](../README.md) — the engines and systems that write

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MergeRulesTest.java](MergeRulesTest.java) | Proves how conflicting writes settle: the four built-in rules, provenance, and a custom rule | `MergeRulesTest` |
