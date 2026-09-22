<!--
  File: docs/architecture/program.md
  Purpose: Level 1 — modules and the dependency direction
  Audience: Agents and humans
  Update when: A module is added or the dependency direction changes
-->

# Program

Aethelgard is one Maven build. The parent artifact is `com.aethelgard:aethelgard`, packaging `pom`, at the repository root. The language level is Java 21 (`maven.compiler.release` 21). The build command is the Maven wrapper at the root (`mvnw` / `mvnw.cmd`). GitHub Actions runs that witness with `mvnw test`.

Four modules do four jobs. The engine is a host for a step-based simulation. The product is the world that plugs into that host. The CLI runs a world with no window. The UI paints a world and serves it on localhost.

| Module | Artifact | Package root | Role |
|--------|----------|--------------|------|
| `engine/` | `com.aethelgard:engine` | `com.aethelgard.engine` | Pool, events, systems, typed merge, user ports, diagnostics |
| `product/` | `com.aethelgard:product` | `com.aethelgard.product` | World fields and the tectonics generation |
| `cli/` | `com.aethelgard:cli` | `com.aethelgard.cli` | Headless runner and the shared command language |
| `ui/` | `com.aethelgard:ui` | `com.aethelgard.ui` | Raster, map controller, HTTP host, Next, Tauri |

Engine packages under that root: `pool`, `event`, `diag`, `system`, `merge`, `user`.

## Which way dependencies go

The dependency rule is one-way.

```
ui  →  product  →  engine
cli →  product  →  engine
ui  →  cli
```

`ui` depends on `cli` only for `CommandDispatch`. `cli` does not depend on `ui`. `engine` never depends on `product`, `cli`, or `ui`. The engine compile classpath has no UI toolkit and no CLI library. The SLF4J API is allowed in `engine`. A logging binding is not.

## Where the rest of the paper is

| Question | Page |
|----------|------|
| What one engine step does | [host/](host/README.md) |
| What one generation writes | [world/](world/README.md) |
| How a settled world is shown and driven | [studio/](studio/README.md) |
