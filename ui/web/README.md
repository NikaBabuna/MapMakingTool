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

Map-first **runner shell** (F-051): gray chrome; top bar identity · transport (Play/Pause/`1x`…`Fastest`) · view; World rail (step/seed/reset + Inspect/Legend); layer chips on the map; continuous Terminal drawer. Physical atlas elevation colors. Play is a client timer → `/api/advance`. Busy shows Working… and ignores extra Advance / Reset world.

Shortcuts: Space Play/Pause; A/. Advance; 1–3 layers; [/] speed; N Reset world; `/C terminal; D World rail; R reset view.

Docs: [docs/product/style-guide.md](../../docs/product/style-guide.md) · [docs/product/architecture.md](../../docs/product/architecture.md) · [docs/product/flows.md](../../docs/product/flows.md)
