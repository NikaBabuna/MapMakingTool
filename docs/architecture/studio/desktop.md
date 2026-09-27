<!--
  File: docs/architecture/studio/desktop.md
  Purpose: Desktop shell — starting the map host, opening the page, and stopping the host on quit
  Audience: Agents and humans
  Update when: How the shell starts or stops the map host, or its window configuration, changes
-->

# Desktop shell

The desktop app is a window around the web page, plus a caretaker for the Java process behind it. When it starts, it stops any map host left over from an earlier run, starts a fresh one, and opens the page. When it quits, it stops the host again, so no background process outlives the window.

## What it reads

The repository root, derived at build time from the shell's own manifest folder (three folders up). The process-id file `aethelgard-maphost.pid` in the system temporary folder, which the host writes ([http](http.md)). Its configuration in `ui/desktop/src-tauri/tauri.conf.json`.

## What it writes

A running map host process, started through the Maven wrapper; a native window titled `"Aethelgard"`, 1280×840 and resizable, showing `http://localhost:3000`; and, on exit, the termination of the host process tree and the removal of the process-id file. Refusals: a missing repository root or Maven wrapper fails the start with an error, and the app does not open.

## Model

The shell's lifecycle is the sequence

$$\mathrm{start}:\; \mathrm{stop}(\mathrm{pid}) \;\to\; \mathrm{spawn}\bigl(\texttt{mvnw -pl ui exec:java -Dexec.mainClass=…MapHostApp}\bigr) \;\to\; \mathrm{window}(\texttt{devUrl}); \qquad \mathrm{exit}:\; \mathrm{stop}(\mathrm{pid}) \;\to\; \mathrm{kill}(\mathrm{child}) \;\to\; \mathrm{wait}(\mathrm{child}),$$

where $\mathrm{stop}(\mathrm{pid})$ reads the process id from the file, ends that process and its children (`taskkill /PID <pid> /T /F` on Windows, `kill -TERM <pid>` elsewhere), and deletes the file. The spawned child is the Maven wrapper; the host is the Java process it starts, whose own id is the one in the file, so the exit path stops both.

## Procedure

1. The binary's entry point calls the shell library's run, and release builds on Windows run without a console. The build script runs Tauri's build step.
2. The run builds the Tauri app with the shell plugin. At setup it finds the repository root, starts the host, and keeps the child process in the app's state.
3. Starting the host first stops any host by its process id, then runs `mvnw.cmd` or `mvnw` in the repository root with the `ui` module's `exec:java` goal on `MapHostApp`. The output is discarded, and on Windows no console window opens.
4. Stopping by process id reads the process-id file, ends the process tree, and deletes the file.
5. The window is configured in `ui/desktop/src-tauri/tauri.conf.json`: product `"Aethelgard"`, identifier `com.aethelgard.desktop`, one window `main` of 1280×840, no content security policy, and development URL `http://localhost:3000`. Before development, it runs `npm run --prefix ../web dev`, which starts the [web front](web/README.md).
6. On the exit event, the run stops the host by its process id, then kills and waits for the child it spawned.

## What is true afterwards

While the window is open, a map host listens on port 7420 and the page on port 3000. After it closes, neither the host nor the wrapper that started it is left running, and the process-id file is gone. The root comes from the build machine's path, so the shell runs from the repository it was built in. `frontendDist` names a static folder `../public` for a packaged build, whose `beforeBuildCommand` is empty.

Code: [src-tauri/](../../../ui/desktop/src-tauri/README.md) · [src-tauri/src/](../../../ui/desktop/src-tauri/src/README.md)
Parent: [studio](README.md). Why a desktop shell around a local web page: [ADR-011](../../paperwork/decisions/ADR-011-local-webview.md).
