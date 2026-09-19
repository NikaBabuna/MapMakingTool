<!--
  File: ui/web/README.md
  Purpose: Next.js map tool front (G-006 / F-025)
  Audience: Humans and agents
  Update when: Front layout or launch changes
-->

# Aethelgard web front

Next.js tool UI for the living map. Talks to Java [`MapHost`](../src/main/java/com/aethelgard/ui/host/MapHost.java) over localhost HTTP. Lives under `ui/web/` so the repo root stays clean.

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

Layers, Advance, Play/Pause (client timer → `/api/advance`), speed, seed + New world, inspect, legend, Console. Busy shows Working... and ignores extra Advance / New world.

Docs: [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
