<!--
  File: ui/web/README.md
  Purpose: Next.js map tool front (G-006 / G-007)
  Audience: Humans and agents
  Update when: Front layout or launch changes
-->

# Aethelgard web front

Next.js **runner shell** tool for the living map. Talks to Java [`MapHost`](../src/main/java/com/aethelgard/ui/host/MapHost.java) over localhost HTTP. Lives under `ui/web/`.

## Dev loop

1. Start the host (repo root):

```bat
mvnw -pl ui -am install -DskipTests
mvnw -pl ui exec:java -Dexec.mainClass=com.aethelgard.ui.host.MapHostApp
```

2. In another terminal:

```bat
cd ui\web
npm install
npm run dev
```

Open http://localhost:3000. Host default: `http://127.0.0.1:7420` (`NEXT_PUBLIC_MAP_HOST`).

## Behavior

Map-first **runner** (F-053): menu-bar row (File · Edit · View · Simulation · Help); top bar identity · transport (Play/Pause/`1x`…`Fastest`) · view; left **Perf** rail (hub means + steps/sec); World rail (step/seed/reset, Advance ×N, copy seed + Inspect/Legend); layer chips **top-left**; always-on Terminal panel with **Clear**. Rails and terminal are drag-resizable (sizes persist; **View → Reset layout**). Brighter elevation ramps. Play is a client timer → `/api/advance`. Busy gates Advance / Reset world (no Working… map overlay).

Chrome is descriptor-driven (alpha): add a rail panel in `src/lib/panels.ts`, a menu item in `src/lib/menus.ts`, a resizable region in `src/lib/layout.ts`, a key row in `src/lib/shortcuts.ts`.

Shortcuts: Space Play/Pause; A/. Advance; 1–3 layers; [/] speed; N Reset world; `/C focus terminal; D World rail; P Perf rail; R reset view; ? shortcut list.

Docs: [docs/product/style-guide.md](../../docs/product/style-guide.md) · [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
