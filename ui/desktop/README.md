<!--
  File: ui/desktop/README.md
  Purpose: Door to the desktop shell, the window that starts the studio
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Desktop shell

The studio's window: it starts the web server and the map host, shows the page, and stops the host when it closes.

**Paper:** [desktop](../../docs/architecture/studio/desktop.md) · [processes](../../docs/architecture/program.md) · **Conventions:** [conventions.md](../../docs/architecture/conventions.md)

## Why

The shell is a Tauri app, built by Tauri's own command line from npm rather than by Maven, so it has its own folder with its own `package.json`. This level holds only that command line; the Rust crate, its configuration, and its code are in [src-tauri](src-tauri/README.md), and the page it shows is the [web front](../web/README.md).

## How it works

`run-product.cmd` installs this folder's npm packages when they are missing and runs `npm run dev`, which is `tauri dev`. Tauri then builds and runs the crate in [src-tauri](src-tauri/README.md): it starts the web front's development server, starts the map host, and opens the window. `npm run build` would build a packaged app, whose static page is in [public](public/README.md).

**Start reading at:** `scripts` in [package.json](package.json).

## Depends on

- [src-tauri](src-tauri/README.md) — the crate the `tauri` command line builds and runs

## Used by

- nothing in this repository — `run-product.cmd` at the repository root runs `npm run dev` here

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [package.json](package.json) | The shell's npm package: the Tauri command line, and the scripts `dev` and `build` | — |
| [package-lock.json](package-lock.json) | The exact version of every npm package, written by npm; search it for one package's name | — |
| [public/](public/README.md) | The static page a packaged build would bundle | — |
| [src-tauri/](src-tauri/README.md) | The shell's Rust crate, its configuration, and its code | — |
| `node_modules/` | exempt: dependency install — the installed npm packages | — |
