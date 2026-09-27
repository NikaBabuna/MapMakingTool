<!--
  File: .cursor/README.md
  Purpose: Door to the Cursor editor's project configuration: the rules it gives every agent in this repository
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Cursor

Holds the Cursor editor's configuration for this repository: today, the rule that sends every agent to the protocol.

**Paper:** none — editor configuration, which the paper does not describe · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

Cursor reads its project configuration from this folder by name. It holds only what the editor reads; the protocol the rule points to lives in [docs/protocol](../docs/protocol/README.md), and the repository's own agent door is [AGENTS.md](../AGENTS.md).

## How it works

Cursor loads every rule file in [rules](rules/README.md) and applies the rules marked `alwaysApply` to every chat in this repository.

**Start reading at:** [rules/protocol.mdc](rules/protocol.mdc).

## Depends on

- nothing in this repository

## Used by

- nothing in this repository — the Cursor editor reads it

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [rules/](rules/README.md) | The rules Cursor gives every agent in this repository | — |
