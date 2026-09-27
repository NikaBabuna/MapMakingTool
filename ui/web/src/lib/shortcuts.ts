/*
 * File: ui/web/src/lib/shortcuts.ts
 * Purpose: Single source for runner keyboard shortcuts
 * Audience: MapTool key handler, ShortcutsOverlay, menu labels
 * Update when: A shortcut is added, removed, or rebound
 */

export type Shortcut = {
  keys: string;
  action: string;
};

export const SHORTCUTS: Shortcut[] = [
  { keys: "Space", action: "Play / Pause" },
  { keys: "A or .", action: "Advance one step" },
  { keys: "1 / 2 / 3", action: "Elevation / Plates / Overlay layer" },
  { keys: "[ / ]", action: "Speed slower / faster" },
  { keys: "N", action: "Reset world (confirm past Step 0)" },
  { keys: "` or C", action: "Focus terminal" },
  { keys: "D", action: "Toggle World rail" },
  { keys: "P", action: "Toggle Perf rail" },
  { keys: "R", action: "Reset map view" },
  { keys: "?", action: "Show this shortcut list" },
  { keys: "Esc", action: "Close menu / dialog" },
];
