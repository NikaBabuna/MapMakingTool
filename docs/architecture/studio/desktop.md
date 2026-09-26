<!--
  File: docs/architecture/studio/desktop.md
  Purpose: The Tauri desktop shell (lib.rs, main.rs, build.rs, tauri.conf.json) — starting the map host, opening the page, and stopping the host on quit
  Audience: Agents and humans
  Update when: run, start_map_host, stop_map_host_by_pid, or tauri.conf.json changes
-->

# Desktop shell

The desktop app is a window around the web page, plus a caretaker for the Java process behind it. When it starts, it stops any map host left over from an earlier run, starts a fresh one, and opens the page. When it quits, it stops the host again, so no background process outlives the window.

## What it reads

The repository root, derived at build time from the shell's own manifest folder (three folders up). The process-id file `aethelgard-maphost.pid` in the system temporary folder, which the host writes ([http](http.md)). Its configuration in `tauri.conf.json`.

## What it writes

A running map host process, started through the Maven wrapper; a native window titled `"Aethelgard"`, 1280×840 and resizable, showing `http://localhost:3000`; and, on exit, the termination of the host process tree and the removal of the process-id file. Refusals: a missing repository root or Maven wrapper fails the start with an error, and the app does not open.

## Model

The shell's lifecycle is the sequence

$$\mathrm{start}:\; \mathrm{stop}(\mathrm{pid}) \;\to\; \mathrm{spawn}\bigl(\texttt{mvnw -pl ui exec:java -Dexec.mainClass=…MapHostApp}\bigr) \;\to\; \mathrm{window}(\texttt{devUrl}); \qquad \mathrm{exit}:\; \mathrm{stop}(\mathrm{pid}) \;\to\; \mathrm{kill}(\mathrm{child}) \;\to\; \mathrm{wait}(\mathrm{child}),$$

where $\mathrm{stop}(\mathrm{pid})$ reads the process id from the file, ends that process and its children (`taskkill /PID <pid> /T /F` on Windows, `kill -TERM <pid>` elsewhere), and deletes the file. The spawned child is the Maven wrapper; the host is the Java process it starts, whose own id is the one in the file, so the exit path stops both.

`start_map_host` (the spawn) in [`lib.rs`](../../../ui/desktop/src-tauri/src/lib.rs):

```rust
let mut cmd = Command::new(&mvnw);
cmd.current_dir(root)
  .args([
    "-pl",
    "ui",
    "exec:java",
    "-Dexec.mainClass=com.aethelgard.ui.host.MapHostApp",
  ])
  .stdout(Stdio::null())
  .stderr(Stdio::null());
```

## Procedure

1. The binary's `main` calls `aethelgard_lib::run`, and release builds on Windows run without a console. The build script runs `tauri_build::build`. [`main`](../../../ui/desktop/src-tauri/src/main.rs), [`build.rs`](../../../ui/desktop/src-tauri/build.rs).
2. `run` builds the Tauri app with the shell plugin. At setup it finds the root with `repo_root`, starts the host with `start_map_host`, and keeps the child handle in the app state `HostProcess`. [`run`](../../../ui/desktop/src-tauri/src/lib.rs).
3. `start_map_host` first calls `stop_map_host_by_pid`, then runs `mvnw.cmd` or `mvnw` in the repository root with the `ui` module's `exec:java` goal on `MapHostApp`. The output is discarded, and on Windows no console window opens. [`start_map_host`](../../../ui/desktop/src-tauri/src/lib.rs).
4. `stop_map_host_by_pid` reads `pid_file`, ends the process tree, and deletes the file. [`stop_map_host_by_pid`](../../../ui/desktop/src-tauri/src/lib.rs), [`pid_file`](../../../ui/desktop/src-tauri/src/lib.rs).
5. The window is configured in `tauri.conf.json`: product `"Aethelgard"`, identifier `com.aethelgard.desktop`, one window `main` of 1280×840, no content security policy, and development URL `http://localhost:3000`. Before development, it runs `npm run --prefix ../web dev`, which starts the [web front](web/README.md). [`tauri.conf.json`](../../../ui/desktop/src-tauri/tauri.conf.json).
6. On the exit event, `run` stops the host by its process id, then kills and waits for the child it spawned. [`run`](../../../ui/desktop/src-tauri/src/lib.rs).

## What is true afterwards

While the window is open, a map host listens on port 7420 and the page on port 3000. After it closes, neither the host nor the wrapper that started it is left running, and the process-id file is gone. The root comes from the build machine's path, so the shell runs from the repository it was built in. `frontendDist` names a static folder `../public` for a packaged build, whose `beforeBuildCommand` is empty.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Shell | `HostProcess` | `run`, `repo_root`, `pid_file`, `stop_map_host_by_pid`, `start_map_host` | [`ui/desktop/src-tauri/src/lib.rs`](../../../ui/desktop/src-tauri/src/lib.rs) |
| Binary entry | — | `main` | [`ui/desktop/src-tauri/src/main.rs`](../../../ui/desktop/src-tauri/src/main.rs) |
| Build script | — | `main` | [`ui/desktop/src-tauri/build.rs`](../../../ui/desktop/src-tauri/build.rs) |
| Configuration | — | `devUrl`, `beforeDevCommand`, `frontendDist`, `windows` | [`ui/desktop/src-tauri/tauri.conf.json`](../../../ui/desktop/src-tauri/tauri.conf.json) |

Parent: [studio](README.md). Why a desktop shell around a local web page: [ADR-011](../../paperwork/decisions/ADR-011-local-webview.md).
