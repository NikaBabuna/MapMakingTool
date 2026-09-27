<!--
  File: ui/desktop/public/README.md
  Purpose: Door to the static page a packaged desktop build would bundle
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Public

Holds the static page that a packaged build of the desktop shell would bundle, which forwards to the web front.

**Paper:** [desktop](../../../docs/architecture/studio/desktop.md) · **Conventions:** [conventions.md](../../../docs/architecture/conventions.md)

## Why

Tauri needs a folder of static files for a packaged build, named by `frontendDist`, even though the studio runs its page from the web front's development server. This folder is that placeholder, kept apart from the web front so the web front stays a normal Next.js app. The real page belongs in the [web front](../../web/README.md).

## How it works

`index.html` holds one line of text and a refresh to `http://localhost:3000`, so a window that loads it lands on the web front when its server is running. In development the window loads `devUrl` directly and never opens this page.

**Start reading at:** the `refresh` meta tag in [index.html](index.html).

## Depends on

- [web front](../../web/README.md) — the page it forwards to, served on port 3000

## Used by

- [src-tauri](../src-tauri/README.md) — `frontendDist` in `tauri.conf.json` names this folder

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [index.html](index.html) | The page the desktop shell bundles, which forwards to the web front at localhost:3000 | — |
