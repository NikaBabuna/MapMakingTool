<!--
  File: ui/desktop/src-tauri/src/README.md
  Purpose: Door to the desktop shell's Rust code: the window's start, the map host's start and stop, and the binary's entry point
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Desktop shell source

Starts the studio's window and looks after the map host behind it: it starts the host on launch and stops it on quit.

**Paper:** [desktop](../../../../docs/architecture/studio/desktop.md) · **Conventions:** [conventions.md](../../../../docs/architecture/conventions.md)

## Why

The shell's Rust code is small and has one job, keeping one Java process alive exactly as long as one window, so it is one library file and a two-line binary. Tauri's layout puts the binary in `main.rs` and the code in the library, `lib.rs`, so the same code can later be built for other targets. The window's settings, permissions, and build step are configuration and belong in [src-tauri](../README.md), and the host itself is Java, in the [http](../../../../ui/src/main/java/com/aethelgard/ui/http/README.md) package.

## How it works

`main` in `main.rs` calls `aethelgard_lib::run`. `run` builds the Tauri app with the shell plugin. At setup it finds the repository root with `repo_root`, calls `start_map_host`, and keeps the child process in the app state `HostProcess`. `start_map_host` first calls `stop_map_host_by_pid`, which reads the process id from `pid_file`, ends that process tree, and deletes the file; it then runs the Maven wrapper with the `ui` module's `exec:java` goal on `com.aethelgard.ui.http.MapHostApp`. On the exit event, `run` calls `stop_map_host_by_pid` again, then kills and waits for the child in `HostProcess`.

**Start reading at:** `run` in [lib.rs](lib.rs).

## Depends on

- [http](../../../../ui/src/main/java/com/aethelgard/ui/http/README.md) — `MapHostApp`, started by its class name through the Maven wrapper, and the process-id file it writes

## Used by

- [src-tauri](../README.md) — `Cargo.toml` builds this code as the crate `aethelgard` and its library `aethelgard_lib`

## Where each step happens

### [Desktop shell](../../../../docs/architecture/studio/desktop.md)

| Step | Member | File |
|------|--------|------|
| 1. The binary's entry point calls the library's run, with no console in a release build on Windows | `main` | [main.rs](main.rs) |
| 2. The run builds the app, finds the root, starts the host, and keeps the child | `run`, `repo_root`, `HostProcess` | [lib.rs](lib.rs) |
| 3. Starting the host stops any old one, then runs the Maven wrapper on the host's entry point | `start_map_host` | [lib.rs](lib.rs) |
| 4. Stopping by process id reads the file, ends the process tree, and deletes the file | `stop_map_host_by_pid`, `pid_file` | [lib.rs](lib.rs) |
| 6. On exit, the run stops the host by its process id, then kills and waits for the child | `run` | [lib.rs](lib.rs) |

The build script of step 1 and the window of step 5 are in [src-tauri](../README.md).

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [lib.rs](lib.rs) | Starts the map host with the window, stops it on quit, and runs the Tauri app | `run`, `start_map_host`, `stop_map_host_by_pid`, `HostProcess` |
| [main.rs](main.rs) | Starts the desktop shell, with no console window in a release build | `main` |
