/*
 * File: ui/desktop/src-tauri/src/lib.rs
 * Purpose: Tauri shell — spawn/stop MapHost; load Next at :3000
 * Audience: Desktop launch
 * Update when: Host spawn or window lifecycle changes
 */

use std::fs;
use std::path::PathBuf;
use std::process::{Child, Command, Stdio};
use std::sync::Mutex;
use tauri::Manager;

struct HostProcess(Mutex<Option<Child>>);

fn repo_root() -> Option<PathBuf> {
  let manifest = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
  // .../ui/desktop/src-tauri → repo
  manifest
    .parent()
    .and_then(|p| p.parent())
    .and_then(|p| p.parent())
    .map(|p| p.to_path_buf())
}

fn pid_file() -> PathBuf {
  std::env::temp_dir().join("aethelgard-maphost.pid")
}

fn stop_map_host_by_pid() {
  let path = pid_file();
  if let Ok(text) = fs::read_to_string(&path) {
    if let Ok(pid) = text.trim().parse::<u32>() {
      #[cfg(windows)]
      {
        let _ = Command::new("taskkill")
          .args(["/PID", &pid.to_string(), "/T", "/F"])
          .stdout(Stdio::null())
          .stderr(Stdio::null())
          .status();
      }
      #[cfg(not(windows))]
      {
        let _ = Command::new("kill")
          .args(["-TERM", &pid.to_string()])
          .stdout(Stdio::null())
          .stderr(Stdio::null())
          .status();
      }
    }
    let _ = fs::remove_file(path);
  }
}

fn start_map_host(root: &PathBuf) -> Result<Child, String> {
  stop_map_host_by_pid();
  let mvnw = if cfg!(windows) {
    root.join("mvnw.cmd")
  } else {
    root.join("mvnw")
  };
  if !mvnw.exists() {
    return Err(format!("Maven wrapper not found at {}", mvnw.display()));
  }
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
  #[cfg(windows)]
  {
    use std::os::windows::process::CommandExt;
    const CREATE_NO_WINDOW: u32 = 0x08000000;
    cmd.creation_flags(CREATE_NO_WINDOW);
  }
  cmd.spawn()
    .map_err(|e| format!("failed to start MapHost: {e}"))
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
  tauri::Builder::default()
    .plugin(tauri_plugin_shell::init())
    .setup(|app| {
      let root = repo_root().ok_or_else(|| "repo root not found".to_string())?;
      let child = start_map_host(&root)?;
      app.manage(HostProcess(Mutex::new(Some(child))));
      Ok(())
    })
    .build(tauri::generate_context!())
    .expect("error while building Aethelgard")
    .run(|app_handle, event| {
      if let tauri::RunEvent::Exit = event {
        stop_map_host_by_pid();
        if let Some(state) = app_handle.try_state::<HostProcess>() {
          if let Ok(mut guard) = state.0.lock() {
            if let Some(mut child) = guard.take() {
              let _ = child.kill();
              let _ = child.wait();
            }
          }
        }
      }
    });
}
