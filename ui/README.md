<!--
  File: ui/README.md
  Purpose: Door to the UI module, the studio's Java side, with the web front and the desktop shell beside it
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# UI module

The UI module is the studio's Java side: the map controller, the raster, and the local HTTP host that the page talks to.

**Docs:** [studio](../docs/architecture/studio/README.md) · [program](../docs/architecture/program.md)

| Path | Read it when |
|------|----------------|
| [pom.xml](pom.xml) | You need the UI module's build: its dependencies, the map host's entry point, and the step that runs the web front's tests |
| `src/` | You need the controller, the raster, or the host's code, `src/main/java/com/aethelgard/ui/`, or their tests, `src/test/java/` |
| [web/](web/README.md) | You need the page the studio shows |
| [desktop/](desktop/README.md) | You need the window that starts the studio |
