/*
 * File: ui/web/src/components/Panel.test.tsx
 * Purpose: Proves that a rail panel collapses and expands, and that the studio remembers a collapsed panel across a reload
 * Audience: Agents / CI
 * Update when: Panel or the panel registry changes
 */

import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { MapTool } from "@/components/MapTool";
import { Panel } from "@/components/Panel";
import { PANELS } from "@/lib/panels";
import { fakeHost } from "@/test/fakeHost";

const INSPECT = PANELS.find((p) => p.id === "inspect")!;
const WORLD = PANELS.find((p) => p.id === "world")!;

// Proves F-068 FR-63 (docs/paperwork/steps/F-068.md).
describe("panel", () => {
  it("a collapsible panel hides its body when collapsed and asks to toggle when its control is pressed", () => {
    const onToggle = vi.fn();
    const { rerender } = render(<Panel descriptor={INSPECT} open onToggle={onToggle}>inspect body</Panel>);
    expect(screen.getByText("inspect body")).toBeTruthy();

    fireEvent.click(screen.getByRole("button", { name: "−" }));
    expect(onToggle).toHaveBeenCalledWith("inspect");

    rerender(<Panel descriptor={INSPECT} open={false} onToggle={onToggle}>inspect body</Panel>);
    expect(screen.queryByText("inspect body")).toBeNull();
  });

  it("a panel that cannot collapse always shows its body", () => {
    render(<Panel descriptor={WORLD} open={false} onToggle={vi.fn()}>world body</Panel>);
    expect(screen.getByText("world body")).toBeTruthy();
    expect(screen.queryByRole("button")).toBeNull();
  });

  it("the studio remembers a collapsed panel after a reload", async () => {
    fakeHost();
    const first = render(<MapTool />);
    const toggle = await screen.findByTitle("Collapse Inspect");
    fireEvent.click(toggle);
    await waitFor(() => expect(screen.getByTitle("Expand Inspect").getAttribute("aria-expanded")).toBe("false"));
    first.unmount();

    render(<MapTool />);
    await waitFor(() => expect(screen.getByTitle("Expand Inspect").getAttribute("aria-expanded")).toBe("false"));
  });
});
