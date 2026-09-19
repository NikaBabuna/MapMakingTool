<!--
  File: ui/desktop/README.md
  Purpose: Tauri 2 desktop shell for Aethelgard (G-006 / F-026)
  Audience: Humans and agents
  Update when: Desktop launch or host spawn changes
-->

# Aethelgard desktop

Tauri 2 shell under `ui/desktop/`. On start it spawns Java `MapHostApp` (port 7420) and opens a webview to the Next tool at **http://localhost:3000**. On quit it stops the host (PID file in the system temp dir).

## Launch (recommended)

From the **repo root** in cmd:

```bat
run-product.cmd
```

That installs Java modules, starts `ui/web` (`npm run dev`), then `ui/desktop` (`npm run dev` / `tauri dev`).

## Manual

1. `mvnw -pl ui -am install -DskipTests`
2. `cd ui\web && npm run dev`
3. `cd ui\desktop && npm install && npm run dev`

Swing map UI was **removed** in F-026. Headless `MapController` + `MapHost` remain for tests and the HTTP API.

Docs: [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
