<!--
  File: docs/architecture/studio/README.md
  Purpose: Level 3 — door to the studio: the controller, the paint, the HTTP host, the desktop shell, and the web front
  Audience: Agents and humans
  Update when: A studio page or chapter is added, or the question it answers changes
-->

# Studio

The studio is how a person watches and drives a world. It does not decide anything about the world: a controller in the Java process holds a session, paints its settled grids into colours, and serves them over HTTP on the local machine; a web page shows the colours and sends the person's actions back; a desktop shell starts both and closes them. These parts are peers; the order in which they start and talk to each other is the Processes section of [../program.md](../program.md).

| Page | Question |
|------|----------|
| [controller.md](controller.md) | How does the studio hold a session, advance it without blocking, play it, and keep the painted map current? |
| [raster.md](raster.md) | How does a grid become one colour per cell? |
| [http.md](http.md) | What does the local HTTP host accept and answer? |
| [desktop.md](desktop.md) | How does the desktop app start and stop the host and the page? |
| [web/](web/README.md) | How does the page show the map and turn the person's input into requests? |

The world the studio shows: [../world/README.md](../world/README.md). The session it holds: [../session/run.md](../session/run.md).
