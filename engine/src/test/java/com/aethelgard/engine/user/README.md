<!--
  File: engine/src/test/java/com/aethelgard/engine/user/README.md
  Purpose: Door to the tests of the user layer
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# User tests

Proves how a person's input reaches a step, and how a view sees the result.

**Paper:** [User layer](../../../../../../../../docs/architecture/engine/user.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Input is the one thing that reaches a step from outside, so how it is frozen and consumed is proved beside the code that does it. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`UserLayerTest` is a JUnit 5 class of four tests. It registers actions, presses and releases them between steps, steps engines, and checks what each step's compute saw and what the recording view received.

**Start reading at:** `UserLayerTest` in [UserLayerTest.java](UserLayerTest.java).

## Depends on

- [engine user/](../../../../../../main/java/com/aethelgard/engine/user/README.md) — the code under test
- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md), [the shared test systems](../README.md) — the engines the input reaches

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [UserLayerTest.java](UserLayerTest.java) | Proves how a person's input reaches a step and how a view sees the result | `UserLayerTest` |
