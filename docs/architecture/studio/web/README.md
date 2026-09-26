<!--
  File: docs/architecture/studio/web/README.md
  Purpose: Level 4 — door to the web front: the root component and its loops, the host client, the canvas, the camera, the chrome, the terminal, and the stylesheet
  Audience: Agents and humans
  Update when: A web page is added, or the question it answers changes
-->

# Web front

The web front is one page, built with Next.js and React, that shows the map and turns a person's clicks and keys into requests to the local [HTTP host](../http.md). It holds no world of its own: every number it shows comes from the host's status, and every picture from the host's raster. The root component orders everything else; its loops and its handlers are [tool.md](tool.md).

| Page | Question |
|------|----------|
| [tool.md](tool.md) | What does the root component hold, and how do its poll, play, and action loops keep the page current? |
| [client.md](client.md) | How does the page call the host, and how does it decode the raster bytes? |
| [canvas.md](canvas.md) | How is the raster drawn with an endless east–west wrap, and how do pointer and wheel become pan, zoom, and inspection? |
| [viewport.md](viewport.md) | What is the camera, and how are zoom, pan, and a click turned into map coordinates? |
| [chrome.md](chrome.md) | How are the menus, the rails and their panels, the resizable layout, and the shortcut list declared and rendered? |
| [terminal.md](terminal.md) | How does the terminal run command lines and keep its history and transcript? |
| [styles.md](styles.md) | How does the stylesheet lay out the screen, and what are its tokens? |

The page's look and its words are the product's: [the style guide](../../../product/style-guide.md). The level above: [studio](../README.md).
