/*
 * File: ui/desktop/src-tauri/build.rs
 * Purpose: Runs Tauri's build step before the desktop shell compiles
 * Audience: Cargo
 * Update when: The shell needs another step at build time
 */

fn main() {
  tauri_build::build()
}
