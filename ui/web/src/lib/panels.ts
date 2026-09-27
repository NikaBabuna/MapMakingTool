/*
 * File: ui/web/src/lib/panels.ts
 * Purpose: Panel registry for the runner rails
 * Audience: MapTool / Panel
 * Update when: Panels are added, removed, moved between rails, or reordered
 *
 * Alpha: descriptor shape may still change (no ADR yet). Add a panel by adding
 * a descriptor here plus a body case in MapTool — rails need no new JSX.
 */

export type PanelDock = "left" | "right";

export type PanelDescriptor = {
  /** Stable id: persistence key, `data-panel` value, body switch key. */
  id: string;
  title: string;
  dock: PanelDock;
  /** Ascending within a rail. */
  order: number;
  defaultOpen: boolean;
  /** False keeps the body always visible (no collapse control). */
  collapsible: boolean;
};

export const PANELS: PanelDescriptor[] = [
  { id: "perf", title: "Perf", dock: "left", order: 10, defaultOpen: true, collapsible: true },
  { id: "world", title: "World", dock: "right", order: 10, defaultOpen: true, collapsible: false },
  { id: "inspect", title: "Inspect", dock: "right", order: 20, defaultOpen: true, collapsible: true },
  { id: "legend", title: "Legend", dock: "right", order: 30, defaultOpen: true, collapsible: true },
];

export function panelsFor(dock: PanelDock): PanelDescriptor[] {
  return PANELS.filter((panel) => panel.dock === dock).sort((a, b) => a.order - b.order);
}

export function panelOpenKey(id: string): string {
  return `aethelgard.panel.${id}.open`;
}

export function defaultPanelOpen(): Record<string, boolean> {
  const state: Record<string, boolean> = {};
  for (const panel of PANELS) {
    state[panel.id] = panel.defaultOpen;
  }
  return state;
}
