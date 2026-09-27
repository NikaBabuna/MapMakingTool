<!--
  File: .cursor/rules/README.md
  Purpose: Door to the Cursor rules: the always-on rule that sends every agent to the protocol
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Cursor rules

Holds the rules the Cursor editor gives every agent in this repository, one rule per file.

**Paper:** none — editor configuration, which the paper does not describe · **Conventions:** [conventions.md](../../docs/architecture/conventions.md)

## Why

Cursor reads rules only from this folder. The one rule here carries no conduct of its own: it points to the protocol, so the protocol stays in one place, [docs/protocol](../../docs/protocol/README.md), and this folder never has to change when the protocol does.

## How it works

`protocol.mdc` has front matter with `alwaysApply: true`, so Cursor adds it to every chat. Its text sends the agent to the global prompt, [brief.md](../../docs/protocol/brief.md), and names the goal index, as the repository's agent door [AGENTS.md](../../AGENTS.md) does.

**Start reading at:** the front matter of [protocol.mdc](protocol.mdc).

## Depends on

- nothing in this repository

## Used by

- nothing in this repository — the Cursor editor reads it

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [protocol.mdc](protocol.mdc) | The always-on rule that sends every agent to the protocol's global prompt | — |
