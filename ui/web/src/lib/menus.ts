/*
 * File: ui/web/src/lib/menus.ts
 * Purpose: Menu-bar descriptor model for the runner (F-053)
 * Audience: MenuBar / MapTool action dispatch
 * Update when: Menus, items, or actions change
 *
 * Alpha: shape may still change (no ADR yet). Items with `enabled: false` are
 * deliberate stubs — infrastructure for later Steps, inert today.
 */

export type MenuActionId =
  | "world.new"
  | "world.randomSeed"
  | "seed.copy"
  | "view.leftRail"
  | "view.rightRail"
  | "view.focusTerminal"
  | "view.resetView"
  | "view.resetLayout"
  | "sim.play"
  | "sim.pause"
  | "sim.advance"
  | "sim.speed.1x"
  | "sim.speed.2x"
  | "sim.speed.4x"
  | "sim.speed.Fastest"
  | "sim.restartUi"
  | "sim.restartEngine"
  | "help.shortcuts";

export type MenuItem = {
  id: string;
  label: string;
  shortcut?: string;
  /** Absent ⇒ stub. */
  action?: MenuActionId;
  enabled: boolean;
  /** Renders a divider above this item. */
  separatorBefore?: boolean;
  /** Item shows a check mark driven by MenuBar `checked`. */
  checkable?: boolean;
};

export type MenuDescriptor = {
  id: string;
  label: string;
  items: MenuItem[];
};

export const MENUS: MenuDescriptor[] = [
  {
    id: "file",
    label: "File",
    items: [
      { id: "file.new", label: "New world", shortcut: "N", action: "world.new", enabled: true },
      { id: "file.randomSeed", label: "Random seed", action: "world.randomSeed", enabled: true },
      { id: "file.open", label: "Open world…", enabled: false, separatorBefore: true },
      { id: "file.save", label: "Save world…", enabled: false },
      { id: "file.exportImage", label: "Export map image…", enabled: false },
      { id: "file.quit", label: "Quit", enabled: false, separatorBefore: true },
    ],
  },
  {
    id: "edit",
    label: "Edit",
    items: [
      { id: "edit.undo", label: "Undo", enabled: false },
      { id: "edit.redo", label: "Redo", enabled: false },
      { id: "edit.copySeed", label: "Copy seed", action: "seed.copy", enabled: true, separatorBefore: true },
      { id: "edit.preferences", label: "Preferences…", enabled: false, separatorBefore: true },
    ],
  },
  {
    id: "view",
    label: "View",
    items: [
      { id: "view.leftRail", label: "Perf rail", shortcut: "P", action: "view.leftRail", enabled: true, checkable: true },
      { id: "view.rightRail", label: "World rail", shortcut: "D", action: "view.rightRail", enabled: true, checkable: true },
      { id: "view.focusTerminal", label: "Focus terminal", shortcut: "`", action: "view.focusTerminal", enabled: true },
      { id: "view.resetView", label: "Reset map view", shortcut: "R", action: "view.resetView", enabled: true, separatorBefore: true },
      { id: "view.resetLayout", label: "Reset layout", action: "view.resetLayout", enabled: true },
    ],
  },
  {
    id: "simulation",
    label: "Simulation",
    items: [
      { id: "sim.play", label: "Play", shortcut: "Space", action: "sim.play", enabled: true },
      { id: "sim.pause", label: "Pause", action: "sim.pause", enabled: true },
      { id: "sim.advance", label: "Advance one step", shortcut: "A", action: "sim.advance", enabled: true },
      { id: "sim.speed.1x", label: "Speed 1x", action: "sim.speed.1x", enabled: true, separatorBefore: true },
      { id: "sim.speed.2x", label: "Speed 2x", action: "sim.speed.2x", enabled: true },
      { id: "sim.speed.4x", label: "Speed 4x", action: "sim.speed.4x", enabled: true },
      { id: "sim.speed.Fastest", label: "Speed Fastest", action: "sim.speed.Fastest", enabled: true },
      { id: "sim.restartUi", label: "Restart UI", action: "sim.restartUi", enabled: true, separatorBefore: true },
      { id: "sim.restartEngine", label: "Restart engine", action: "sim.restartEngine", enabled: true },
      { id: "sim.record", label: "Record history…", enabled: false, separatorBefore: true },
    ],
  },
  {
    id: "help",
    label: "Help",
    items: [
      { id: "help.shortcuts", label: "Shortcuts…", shortcut: "?", action: "help.shortcuts", enabled: true },
      { id: "help.docs", label: "Documentation…", enabled: false },
      { id: "help.about", label: "About Aethelgard", enabled: false, separatorBefore: true },
    ],
  },
];
