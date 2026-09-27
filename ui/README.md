<!--
  File: ui/README.md
  Purpose: Door to the UI module: the studio's Java side, with the web front and the desktop shell beside it
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# UI module

Builds the studio: the Java side that holds a world, paints it, and serves it over HTTP, together with the web page that shows it and the desktop window that starts both.

**Paper:** [studio](../docs/architecture/studio/README.md) · [program](../docs/architecture/program.md) · **Conventions:** [conventions.md](../docs/architecture/conventions.md)

## Why

The studio is one area of the paper, so its three parts live in one module folder: the Maven module's Java code, the Next.js web front in `web/`, and the Tauri desktop shell in `desktop/`. Only the Java part is a Maven module, and the web front and the shell are built by their own tools and reach the Java side only over HTTP or by starting it. The world itself belongs in the [product](../product/README.md) module, and the command language in [cli](../cli/README.md).

## How it works

1. `pom.xml` builds the Java packages under `src/main/java/com/aethelgard/ui/`: [controller](src/main/java/com/aethelgard/ui/controller/README.md) holds one session and keeps it painted, [raster](src/main/java/com/aethelgard/ui/raster/README.md) paints the grids into colours, and [http](src/main/java/com/aethelgard/ui/http/README.md) serves the controller on `127.0.0.1:7420`. Their tests are in the same packages under `src/test/java/`.
2. The build's `exec:java` goal names `com.aethelgard.ui.http.MapHostApp` as its main class, and its test phase runs `npm test` in `web/` (`npm.cmd` on Windows), so the witness command also runs the web front's tests; `-DskipTests` skips both.
3. The [desktop shell](desktop/README.md) starts the web front's server and the map host, and opens a window on the [web front](web/README.md), which calls the host over HTTP.

**Start reading at:** `MapHost.bind` in [src/main/java/com/aethelgard/ui/http/MapHost.java](src/main/java/com/aethelgard/ui/http/MapHost.java).

## Depends on

- [product](../product/README.md) — the session and the world's fields that the controller holds and the raster paints
- [cli](../cli/README.md) — `CommandDispatch`, which runs the terminal's lines
- [engine](../engine/README.md) — the engine under the product, on the module's class path

## Used by

- nothing in this repository — a person starts it with `run-product.cmd`, and the witness command runs its tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [pom.xml](pom.xml) | The UI module's build: its dependencies, the map host's main class, and the step that runs the web front's tests | — |
| `src/` | exempt: layout segment — the Java packages [controller](src/main/java/com/aethelgard/ui/controller/README.md), [raster](src/main/java/com/aethelgard/ui/raster/README.md), and [http](src/main/java/com/aethelgard/ui/http/README.md), and their tests, [controller](src/test/java/com/aethelgard/ui/controller/README.md), [raster](src/test/java/com/aethelgard/ui/raster/README.md), and [http](src/test/java/com/aethelgard/ui/http/README.md) | — |
| [web/](web/README.md) | The studio's page, a Next.js app | — |
| [desktop/](desktop/README.md) | The studio's window, a Tauri app that starts the page and the map host | — |
| `target/` | exempt: build output — Maven's build | — |
