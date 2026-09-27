<!--
  File: ui/desktop/src-tauri/capabilities/README.md
  Purpose: Door to the desktop shell's permission sets: what the window may ask of Tauri
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Capabilities

Holds the permission sets Tauri grants to the desktop shell's window, one file per set.

**Paper:** none — the permission sets are Tauri configuration, which the paper does not describe · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

Tauri denies a window every native call unless a capability file grants it, and it reads those files from this folder by convention. Each set is a separate file, so a permission can be traced to the set and the window it serves. Settings of the window itself belong in `tauri.conf.json` in [src-tauri](../README.md).

## How it works

At build time Tauri reads every file here. `default.json` is the set `default`: it applies to the window `main` and grants `core:default`, Tauri's base permissions, and `shell:allow-open`, which lets the page open a link with the system's handler through the shell plugin that `run` registers.

**Start reading at:** `permissions` in [default.json](default.json).

## Depends on

- nothing in this repository

## Used by

- [src-tauri](../README.md) — the Tauri build grants these sets to the window `main` that `tauri.conf.json` declares

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [default.json](default.json) | The one permission set: Tauri's base permissions and opening links, for the window `main` | — |
