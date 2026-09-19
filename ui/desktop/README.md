<!--
  File: ui/desktop/README.md
  Purpose: Tauri 2 desktop shell for Aethelgard (G-006 / F-026)
  Audience: Humans and agents
  Update when: Desktop launch or host spawn changes
-->

# Aethelgard desktop

Tauri 2 shell under `ui/desktop/`. On start it spawns Java `MapHostApp` (port 7420) and opens a webview to the Next tool at **http://localhost:3000**. On quit it stops the host (PID file in the system temp dir).

`tauri dev` runs `npm run --prefix ../web dev` (`beforeDevCommand`), then waits until `:3000` answers before opening the window. That path is relative to `ui/desktop` (where you run `npm run dev`).

## Launch (recommended)

From the **repo root** in cmd:

```bat
run-product.cmd
```

That installs Java modules, ensures `ui/web` + `ui/desktop` npm deps, then `tauri dev` (which starts Next, then the window).

## Manual

1. `mvnw -pl ui -am install -DskipTests`
2. `cd ui\web && npm install` (once)
3. `cd ui\desktop && npm install && npm run dev`

Do **not** start Next in a separate broken `start` window — Tauri owns that process in dev.

Swing map UI was **removed** in F-026. Headless `MapController` + `MapHost` remain for tests and the HTTP API.

Docs: [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
