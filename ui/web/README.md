<!--
  File: ui/web/README.md
  Purpose: Next.js map tool front (G-006 / G-007)
  Audience: Humans and agents
  Update when: Front layout or launch changes
-->

# Aethelgard web front

Next.js **studio cartography** tool for the living map. Talks to Java [`MapHost`](../src/main/java/com/aethelgard/ui/host/MapHost.java) over localhost HTTP. Lives under `ui/web/`.

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

Map-first multi-panel studio: thin top bar, full-bleed map with neatline/graticule/coords HUD, right rail of Inspect + Legend panel cards, rebuilt **Terminal** drawer (`Terminal.tsx`, `aethelgard>` prompt, noun/verb hints, ↑/↓ history). Layers, Advance, Play/Pause (client timer → `/api/advance`), speed, seed + Random + New world, pan/zoom, inspect, legend. Busy shows Working… overlay and ignores extra Advance / New world.

Shortcuts: Space Play; A/. Advance; 1–3 layers; [/] speed; N New world; `/C console; D dock; R reset view.

Docs: [docs/product/style-guide.md](../../docs/product/style-guide.md) · [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
