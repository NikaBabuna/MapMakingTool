<!--
  File: engine/src/test/java/com/aethelgard/engine/events/README.md
  Purpose: Door to the tests of claiming events
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Events tests

Proves how events are claimed: by category ancestry, with unmatched ones reported, and never left in the buffer after a step.

**Paper:** [Events](../../../../../../../../docs/architecture/engine/events.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Claiming decides which systems run, so its rules are proved beside the code that claims. The tests run the code and read no document, and each names beside it the requirements it proves.

## How it works

`EventClaimingTest` is a JUnit 5 class of four tests. It builds category trees and claimers, emits events through engines, and checks which claimer took each event, which events were reported unmatched, and that the buffer is empty after the step.

**Start reading at:** `EventClaimingTest` in [EventClaimingTest.java](EventClaimingTest.java).

## Depends on

- [engine events/](../../../../../../main/java/com/aethelgard/engine/events/README.md) — the code under test
- [engine pool/](../../../../../../main/java/com/aethelgard/engine/pool/README.md), [the shared test systems](../README.md) — the engines the events are emitted in

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [EventClaimingTest.java](EventClaimingTest.java) | Proves how events are claimed: by category ancestry, unmatched ones reported, and never refilled within a step | `EventClaimingTest` |
