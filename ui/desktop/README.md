<!--
  File: ui/desktop/README.md
  Purpose: Door to the desktop shell, the window that starts the studio
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# Desktop shell

The desktop shell is the studio's window: it starts the web server and the map host, shows the page, and stops the host when it closes.

**Docs:** [desktop](../../docs/architecture/studio/desktop.md) · [processes](../../docs/architecture/program.md)

| Path | Read it when |
|------|----------------|
| [package.json](package.json) | You need the shell's command line and its scripts: `dev` (`tauri dev`) and `build` |
| [package-lock.json](package-lock.json) | You need the exact version of one package; search it for the name |
| `public/` | You need the static folder a packaged build would serve |
| `src-tauri/` | You need the shell's Rust code, `src-tauri/src/`, its crate, `src-tauri/Cargo.toml`, or its window and launch settings, `src-tauri/tauri.conf.json` |
