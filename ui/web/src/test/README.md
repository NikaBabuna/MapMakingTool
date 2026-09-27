<!--
  File: ui/web/src/test/README.md
  Purpose: Door to the helpers shared by the web front's tests: the stand-in map host and the simulated browser's setup
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Test helpers

Holds what several of the web front's tests share: a stand-in for the map host, and the setup that completes the simulated browser before every test file.

**Paper:** [client](../../../../docs/architecture/studio/web/client.md) · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

Each test lives beside the code it proves, in [components](../components/README.md) or [lib](../lib/README.md), but a helper that more than one test needs has no single home there, so it lives here. Nothing in this folder is part of the page; a helper used by one test only stays in that test file.

## How it works

`vitest.config.mts` in the web front's folder names `setup.ts` as its setup file, so it runs before every test file. `setup.ts` gives jsdom a canvas context, `ImageData`, and `ResizeObserver`, which it lacks, and after each test cleans up the rendered DOM, clears local storage, and restores every mock and stubbed global. A test that needs the host calls `fakeHost`, which replaces the global `fetch` with a mock that answers the host's routes from a small `FakeHost` state and records each request as a `Call`; `posts` lists the POST paths it saw.

**Start reading at:** `fakeHost` in [fakeHost.ts](fakeHost.ts).

## Depends on

- [lib](../lib/README.md) — `HostStatus` and the other types of the host client, which `fakeHost` answers with

## Used by

- [components](../components/README.md) — `MapTool.test.tsx` and `Panel.test.tsx` call `fakeHost`, and every test file runs after `setup.ts`
- [lib](../lib/README.md) — every test file there runs after `setup.ts`

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [fakeHost.ts](fakeHost.ts) | A stand-in for the map host: answers the page's requests from a small world state and records every call | `fakeHost`, `FakeHost`, `Call`, `posts` |
| [setup.ts](setup.ts) | Gives the simulated browser the APIs jsdom lacks, and resets state after each test | — |
