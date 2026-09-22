<!--
  File: docs/architecture/studio/README.md
  Purpose: Level 4 — session, raster, and HTTP host
  Audience: Agents and humans
  Update when: A studio page is added or the question it answers changes
-->

# Studio

The studio is how a settled world is held, painted, and driven. It does not decide crust. It reads the fields the generation wrote. The UI module is `com.aethelgard.ui`. Paint is `ElevationRaster`. HTTP is `MapHost`.

| Page | Question |
|------|----------|
| [session.md](session.md) | Who owns the engine, and how does an advance stay single-threaded? |
| [raster.md](raster.md) | How does a grid become packed RGB? |
| [host.md](host.md) | How do HTTP, play, and the command line reach that session? |

The generation those fields come from: [../world/README.md](../world/README.md). The modules: [../program.md](../program.md).
