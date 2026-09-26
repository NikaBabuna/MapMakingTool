<!--
  File: docs/architecture/program.md
  Purpose: Level 1 — modules, build files, the dependency direction, the processes, and the stack
  Audience: Agents and humans
  Update when: A module, a build file, or a process is added, the dependency direction changes, a project-fact line changes, or a stack choice changes
-->

# Program

Aethelgard is one Maven build. The parent artifact is `com.aethelgard:aethelgard`, packaging `pom`, at the repository root. The language level is Java 21 (`maven.compiler.release` 21). The build command is the Maven wrapper at the root (`mvnw` / `mvnw.cmd`).

**Witness command:** `./mvnw test` from the repository root (`mvnw.cmd test` on Windows). GitHub Actions runs the same command on `main`.  
**Tests:** JUnit tests in `<module>/src/test/java/`, in the same package as the unit they test, named `<Unit>Test.java`. The web front (`ui/web/`) and the desktop shell (`ui/desktop/`) have no tests.  
**Declarations:** Java: `(class|interface|record|enum) <Name>\b` in `<module>/src/main/java/`. TypeScript: `(function|const|class|interface|type) <Name>\b` in `ui/web/src/`. Rust: `(fn|struct|enum) <name>\b` in `ui/desktop/src-tauri/src/`.  
**Output summary:** lines matching `Tests run:`, `FAIL`, `ERROR`, `BUILD SUCCESS`, or `BUILD FAILURE` in the witness output.

Four Maven modules do four jobs, and two more parts are built by their own tools. The engine is a host for any step-based simulation. The product is the world that plugs into that host, and the session that runs it. The CLI runs a world with no window. The UI module paints a world and serves it on the local machine. The web front (`ui/web/`, Next.js) is the page that shows it, and the desktop shell (`ui/desktop/`, Tauri) is the window that starts everything.

| Module | Artifact | Package root | Role |
|--------|----------|--------------|------|
| `engine/` | `com.aethelgard:engine` | `com.aethelgard.engine` | Pool, events, systems, typed merge, user ports, diagnostics |
| `product/` | `com.aethelgard:product` | `com.aethelgard.product` | World fields, the tectonics generation, and the session |
| `cli/` | `com.aethelgard:cli` | `com.aethelgard.cli` | Headless runner and the shared command language |
| `ui/` | `com.aethelgard:ui` | `com.aethelgard.ui` | Map controller, raster, and the local HTTP host |

Every build file:

| Build file | What it builds |
|------------|----------------|
| [`pom.xml`](../../pom.xml) | The parent: the four modules in the order engine, product, cli, ui; Java release 21; the versions of JUnit and SLF4J; the compiler and test plugins |
| [`engine/pom.xml`](../../engine/pom.xml) | The engine, against the SLF4J API only; JUnit and the simple SLF4J binding for tests |
| [`product/pom.xml`](../../product/pom.xml) | The product, on the engine; the simple SLF4J binding at run time |
| [`cli/pom.xml`](../../cli/pom.xml) | The CLI, on the product and the engine |
| [`ui/pom.xml`](../../ui/pom.xml) | The UI module, on the product, the CLI, and the engine |
| [`ui/web/package.json`](../../ui/web/package.json) | The web front with npm: Next.js 15, React 19, TypeScript 5; scripts `dev`, `build`, `start`, `lint` |
| [`ui/desktop/package.json`](../../ui/desktop/package.json) | The desktop shell's Tauri command line; scripts `dev` (`tauri dev`) and `build` |
| [`ui/desktop/src-tauri/Cargo.toml`](../../ui/desktop/src-tauri/Cargo.toml) | The Rust crate `aethelgard` with library `aethelgard_lib`, on Tauri 2 and its shell plugin |

## Which way dependencies go

The dependency rule is one-way.

```
product  →  engine
cli      →  product, engine
ui       →  cli, product, engine
```

`ui` depends on `cli` only for `CommandDispatch`. `cli` does not depend on `ui`. `engine` never depends on `product`, `cli`, or `ui`. The engine compile classpath has no UI toolkit and no CLI library. The SLF4J API is allowed in `engine`. A logging binding is not; the simple binding reaches the running program through the other modules. The web front and the desktop shell have no build dependency on any Maven module: the page reaches the UI module only over HTTP, and the shell starts it through the Maven wrapper.

## Processes

A person starts the studio with one script, which starts the desktop shell; the shell starts the web server and the map host, and opens a window on the page. The page then talks to the map host over HTTP on the loopback address. The command-line program is a separate, independent process with its own world.

| Process | Started by | Entry point | Listens on | Talks to |
|---------|------------|-------------|------------|----------|
| Desktop shell | [`run-product.cmd`](../../run-product.cmd) step 4, `npm run dev` in `ui/desktop` (that is, `tauri dev`) | [`run`](../../ui/desktop/src-tauri/src/lib.rs) | — | Starts the web server and the map host; stops the map host on quit ([desktop](studio/desktop.md)) |
| Web server | The shell's `beforeDevCommand`, `npm run --prefix ../web dev` (that is, `next dev`) | [`HomePage`](../../ui/web/src/app/page.tsx) | `localhost:3000` | Serves the page to the webview |
| Map host | The shell, `mvnw -pl ui exec:java -Dexec.mainClass=com.aethelgard.ui.host.MapHostApp` | [`MapHostApp.main`](../../ui/src/main/java/com/aethelgard/ui/host/MapHostApp.java) | `127.0.0.1:7420` | Answers the page's requests; owns the studio's world ([http](studio/http.md)) |
| Webview | The shell's window | [`MapTool`](../../ui/web/src/components/MapTool.tsx) | — | Loads the page from the web server; calls the map host ([web front](studio/web/README.md)) |
| CLI | A person or a script, `mvnw -pl cli exec:java -Dexec.mainClass=com.aethelgard.cli.Main` | [`Main.main`](../../cli/src/main/java/com/aethelgard/cli/Main.java) | — | Nothing; it owns its own world and prints ([cli](cli/README.md)) |

```
person → run-product.cmd → desktop shell ─┬→ web server  :3000 ─── page ───┐
                                          ├→ map host    :7420              │
                                          └→ window (webview) ←─────────────┘
webview ── HTTP ──→ map host
person → CLI (own world, no window)
```

[`run-product.cmd`](../../run-product.cmd) runs four steps: it installs the Java modules the UI needs (`mvnw.cmd -pl ui -am install -DskipTests`), installs the web front's and the shell's npm packages when their folders have none, and runs `npm run dev` in `ui/desktop`. [`run-ui.cmd`](../../run-ui.cmd) calls it with the same arguments. The workflow file [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml) describes a job that checks out the repository, sets up JDK 21, and runs `./mvnw -B test` on every push and pull request to `main`. The file is a Markdown text with that job in a fenced block, and as a whole it is not valid YAML ([open questions](open-questions.md)).

## Stack

The choices of how the project is built, each with the decision record that made it. A change to a row is a decision first.

| Choice | Decided in |
|--------|------------|
| Language: Java 21 | [ADR-003](../paperwork/decisions/ADR-003-java.md), [ADR-007](../paperwork/decisions/ADR-007-modules-and-java-21.md) |
| Build: Maven, run through the wrapper at the repository root | [ADR-007](../paperwork/decisions/ADR-007-modules-and-java-21.md) |
| Layout: one repository, with the engine and the product as sibling modules | [ADR-001](../paperwork/decisions/ADR-001-monorepo.md), [ADR-007](../paperwork/decisions/ADR-007-modules-and-java-21.md), [ADR-010](../paperwork/decisions/ADR-010-product-adapters.md) |
| Engine logging: the SLF4J API only, with no binding in `engine` | [ADR-008](../paperwork/decisions/ADR-008-diagnostics.md) |
| Interactive front: a Tauri 2 desktop shell, a Next.js page, and a Java HTTP host on localhost | [ADR-011](../paperwork/decisions/ADR-011-local-webview.md) |

## Where the rest of the paper is

| Question | Page |
|----------|------|
| What does one engine step do? | [engine/](engine/README.md) |
| What does one generation of the world write? | [world/](world/README.md) |
| Who owns a running world, and how is it observed and printed? | [session/](session/README.md) |
| How does the command line drive a world? | [cli/](cli/README.md) |
| How is a world shown and driven in the studio? | [studio/](studio/README.md) |
