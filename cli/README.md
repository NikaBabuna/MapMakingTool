<!--
  File: cli/README.md
  Purpose: Door to the command-line module, which runs a world without a window
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# CLI module

The command-line module runs one world without a window, in the command language the studio's terminal shares.

**Docs:** [cli](../docs/architecture/cli/README.md) · [runner](../docs/architecture/cli/runner.md) · [language](../docs/architecture/cli/language.md)

| Path | Read it when |
|------|----------------|
| [pom.xml](pom.xml) | You need the command line's build: its artifact, and its dependencies on the product and the engine |
| `src/` | You need the runner's or the command language's code, `src/main/java/com/aethelgard/cli/`, or their tests, `src/test/java/` |
