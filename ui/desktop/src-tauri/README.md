<!--
  File: ui/desktop/src-tauri/README.md
  Purpose: Door to the desktop shell's Tauri crate: its manifest, build step, window configuration, permissions, and code
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Tauri crate

The Rust crate of the desktop shell, with everything Tauri needs to build it and open its window.

**Paper:** [desktop](../../../docs/architecture/studio/desktop.md) · **Conventions:** [conventions.md](../../../docs/architecture/conventions.md)

## Why

Tauri expects one folder that holds the crate's manifest, its build script, the app's configuration, its permission sets, and its icons, with the Rust code below it in `src/`. Keeping that layout lets the Tauri command line in [the desktop folder](../README.md) find everything by convention. The code belongs in [src](src/README.md), and the shell's npm command line belongs one folder up.

## How it works

`npm run dev` in `ui/desktop` runs `tauri dev`, which reads `tauri.conf.json`. Its `beforeDevCommand` starts the web front's development server, and Tauri waits for `devUrl`, `http://localhost:3000`. Cargo builds the crate `Cargo.toml` describes, first running `build.rs`, which calls `tauri_build::build` to generate the app's context and the permission schemas into `gen/`. Tauri grants the window `main` the permissions listed in `capabilities/`, and opens it on the page. `frontendDist` names `../public` for a packaged build.

**Start reading at:** `devUrl` in [tauri.conf.json](tauri.conf.json).

## Depends on

- [web front](../../web/README.md) — the page the window shows, started by `beforeDevCommand`
- [public](../public/README.md) — the static page a packaged build would serve

## Used by

- [the desktop folder](../README.md) — its `tauri` command line builds and runs this crate

## Where each step happens

### [Desktop shell](../../../docs/architecture/studio/desktop.md)

| Step | Member | File |
|------|--------|------|
| 1. The build script runs Tauri's build step | `main` | [build.rs](build.rs) |
| 5. The window, the identifier, the security policy, the development URL, and the command run before development | `productName`, `identifier`, `app.windows`, `app.security.csp`, `build.devUrl`, `build.beforeDevCommand`, `build.frontendDist` | [tauri.conf.json](tauri.conf.json) |

The other steps are in [src](src/README.md).

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [Cargo.toml](Cargo.toml) | The desktop shell's Rust crate: its name, its library, and its Tauri dependencies | — |
| [Cargo.lock](Cargo.lock) | The exact versions of every crate, written by Cargo; search it for one crate's name | — |
| [build.rs](build.rs) | Runs Tauri's build step before the desktop shell compiles | `main` |
| [tauri.conf.json](tauri.conf.json) | The app's name, window, security policy, development URL, and bundle | — |
| [capabilities/](capabilities/README.md) | The permission sets granted to the shell's window | — |
| [src/](src/README.md) | The shell's Rust code | — |
| `icons/` | exempt: generated — the app's icons, written by the Tauri icon tool | — |
| `gen/` | exempt: generated — schemas Tauri writes on every build | — |
| `target/` | exempt: build output — Cargo's build | — |
