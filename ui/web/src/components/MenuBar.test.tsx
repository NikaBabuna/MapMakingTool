/*
 * File: ui/web/src/components/MenuBar.test.tsx
 * Purpose: Proves the menu bar the style guide describes: its menus in order, live items that act, and dim stub items that do nothing
 * Audience: Agents / CI
 * Update when: A menu or menu item changes
 */

import { fireEvent, render, screen, within } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { MenuBar } from "@/components/MenuBar";

// The menus of the style guide: live items, then the items shown dim.
const MENUS: Record<string, { live: string[]; dim: string[] }> = {
  File: { live: ["New world", "Random seed"], dim: ["Open world…", "Save world…", "Export map image…", "Quit"] },
  Edit: { live: ["Copy seed"], dim: ["Undo", "Redo", "Preferences…"] },
  View: { live: ["Perf rail", "World rail", "Focus terminal", "Reset map view", "Reset layout"], dim: [] },
  Simulation: {
    live: ["Play", "Pause", "Advance one step", "Speed 1x", "Speed 2x", "Speed 4x", "Speed Fastest", "Restart UI", "Restart engine"],
    dim: ["Record history…"],
  },
  Help: { live: ["Shortcuts…"], dim: ["Documentation…", "About Aethelgard"] },
};

function open(title: string) {
  fireEvent.click(screen.getByRole("menuitem", { name: title }));
  return screen.getByRole("menu", { name: title });
}

function itemLabels(menu: HTMLElement): string[] {
  return within(menu).getAllByRole("menuitem").map((el) => el.querySelector(".menu-label")?.textContent ?? "");
}

// Proves F-068 FR-65 (docs/paperwork/steps/F-068.md).
describe("menu bar", () => {
  it("reads File, Edit, View, Simulation, Help, along the top", () => {
    render(<MenuBar onAction={vi.fn()} />);
    const bar = screen.getByRole("menubar");
    const titles = Array.from(bar.querySelectorAll(".menu-title")).map((el) => el.textContent);
    expect(titles).toEqual(["File", "Edit", "View", "Simulation", "Help"]);
  });

  it("holds the style guide's items: stubs visible, dim and inert; live items fire their action", () => {
    const onAction = vi.fn();
    render(<MenuBar onAction={onAction} />);

    for (const [title, { live, dim }] of Object.entries(MENUS)) {
      const menu = open(title);
      expect(new Set(itemLabels(menu))).toEqual(new Set([...live, ...dim]));
      for (const label of dim) {
        const item = within(menu).getByText(label).closest("button") as HTMLButtonElement;
        expect(item.disabled, `${title} › ${label} is dim`).toBe(true);
        fireEvent.click(item);
      }
      expect(onAction).not.toHaveBeenCalled();
      fireEvent.click(screen.getByRole("menuitem", { name: title }));
    }

    const file = open("File");
    fireEvent.click(within(file).getByText("New world").closest("button") as HTMLButtonElement);
    expect(onAction).toHaveBeenCalledWith("world.new");
    expect(screen.queryByRole("menu")).toBeNull();

    const sim = open("Simulation");
    fireEvent.click(within(sim).getByText("Restart engine").closest("button") as HTMLButtonElement);
    expect(onAction).toHaveBeenLastCalledWith("sim.restartEngine");
  });

  it("closes an open menu on Esc", () => {
    render(<MenuBar onAction={vi.fn()} />);
    open("View");
    fireEvent.keyDown(window, { key: "Escape" });
    expect(screen.queryByRole("menu")).toBeNull();
  });
});
