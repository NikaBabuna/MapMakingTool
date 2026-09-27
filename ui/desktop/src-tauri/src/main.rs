/*
 * File: ui/desktop/src-tauri/src/main.rs
 * Purpose: Starts the desktop shell, with no console window in a release build
 * Audience: Cargo; the desktop shell's launch
 * Update when: The shell's entry point changes
 */

#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

fn main() {
  aethelgard_lib::run();
}
