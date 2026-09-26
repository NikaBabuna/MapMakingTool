/*
 * File: ui/web/src/components/MapTool.test.tsx
 * Purpose: Proves the studio page against a stand-in host: the style guide's keys, stale answers ignored, the offline banner, and the World rail
 * Audience: Agents / CI
 * Update when: MapTool's keys, polling, or World rail change
 */

import { act, fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { MapTool } from "@/components/MapTool";
import { SHORTCUTS } from "@/lib/shortcuts";
import { fakeHost, posts, type FakeHost } from "@/test/fakeHost";

/** The number the World rail shows under "Step". */
function railStep(): string {
  return screen.getByText("Step").nextElementSibling?.textContent ?? "";
}

function layerPressed(name: string): boolean {
  const group = screen.getByRole("group", { name: "Map layer" });
  return within(group).getByRole("button", { name }).getAttribute("aria-pressed") === "true";
}

function key(k: string, target: Window | HTMLElement = window) {
  act(() => {
    fireEvent.keyDown(target, { key: k });
  });
}

async function loaded(host: FakeHost, step = 0) {
  render(<MapTool />);
  await waitFor(() => expect(railStep()).toBe(String(step)));
  return host;
}

// The map stage has a size, so the view can be fitted and zoomed.
const clientSize = {
  width: Object.getOwnPropertyDescriptor(HTMLElement.prototype, "clientWidth"),
  height: Object.getOwnPropertyDescriptor(HTMLElement.prototype, "clientHeight"),
};
beforeEach(() => {
  Object.defineProperty(HTMLElement.prototype, "clientWidth", {
    configurable: true,
    get() {
      return (this as HTMLElement).classList.contains("map-stage") ? 960 : 0;
    },
  });
  Object.defineProperty(HTMLElement.prototype, "clientHeight", {
    configurable: true,
    get() {
      return (this as HTMLElement).classList.contains("map-stage") ? 540 : 0;
    },
  });
});
afterEach(() => {
  if (clientSize.width) Object.defineProperty(HTMLElement.prototype, "clientWidth", clientSize.width);
  if (clientSize.height) Object.defineProperty(HTMLElement.prototype, "clientHeight", clientSize.height);
});

// Proves F-068 FR-66 (docs/paperwork/steps/F-068.md).
describe("keys", () => {
  it("the shortcut list names exactly the keys tested here", () => {
    expect(SHORTCUTS.map((s) => s.keys)).toEqual([
      "Space", "A or .", "1 / 2 / 3", "[ / ]", "N", "` or C", "D", "P", "R", "?", "Esc",
    ]);
  });

  it("Space plays and pauses", async () => {
    await loaded(fakeHost());
    const play = screen.getByRole("button", { name: "Play" });
    key(" ");
    expect(play.getAttribute("aria-pressed")).toBe("true");
    key(" ");
    expect(play.getAttribute("aria-pressed")).toBe("false");
  });

  it("A and . advance the world one step", async () => {
    const host = await loaded(fakeHost());
    key("a");
    await waitFor(() => expect(railStep()).toBe("1"));
    key(".");
    await waitFor(() => expect(railStep()).toBe("2"));
    expect(posts(host).filter((p) => p === "/api/advance")).toHaveLength(2);
  });

  it("1, 2 and 3 switch to Elevation, Plates and Overlay without advancing", async () => {
    const host = await loaded(fakeHost());
    key("2");
    await waitFor(() => expect(layerPressed("Plates")).toBe(true));
    key("3");
    await waitFor(() => expect(layerPressed("Overlay")).toBe(true));
    key("1");
    await waitFor(() => expect(layerPressed("Elevation")).toBe(true));
    expect(posts(host)).toEqual(["/api/layer?layer=Plates", "/api/layer?layer=Overlay", "/api/layer?layer=Elevation"]);
    expect(host.state.step).toBe(0);
  });

  it("] and [ make the world faster and slower", async () => {
    const host = await loaded(fakeHost());
    key("]");
    await waitFor(() => expect(host.state.speed).toBe("2x"));
    key("]");
    await waitFor(() => expect(host.state.speed).toBe("4x"));
    key("[");
    await waitFor(() => expect(host.state.speed).toBe("2x"));
  });

  it("N starts a new world at once at step 0, and asks first once the world has moved", async () => {
    const host = await loaded(fakeHost({ seed: 5 }));
    key("n");
    await waitFor(() => expect(posts(host)).toContain("/api/new-world?seed=5"));

    host.state.step = 4;
    await waitFor(() => expect(railStep()).toBe("4"));
    const before = posts(host).length;
    key("N");
    const dialog = await screen.findByRole("alertdialog");
    expect(dialog).toBeTruthy();
    expect(posts(host).length).toBe(before);
    key("Escape");
    expect(screen.queryByRole("alertdialog")).toBeNull();
    expect(posts(host).length).toBe(before);
  });

  it("` and C focus the terminal", async () => {
    await loaded(fakeHost());
    const terminal = screen.getByPlaceholderText("help");
    key("`");
    expect(document.activeElement).toBe(terminal);
    (document.activeElement as HTMLElement).blur();
    key("c");
    expect(document.activeElement).toBe(terminal);
  });

  it("D hides and shows the World rail, and P the Perf rail", async () => {
    await loaded(fakeHost());
    key("d");
    expect(screen.queryByRole("complementary", { name: "World" })).toBeNull();
    key("d");
    expect(screen.getByRole("complementary", { name: "World" })).toBeTruthy();
    key("p");
    expect(screen.queryByRole("complementary", { name: "Performance" })).toBeNull();
    key("p");
    expect(screen.getByRole("complementary", { name: "Performance" })).toBeTruthy();
  });

  it("R resets the view of the map", async () => {
    await loaded(fakeHost());
    const scale = () => document.querySelector(".map-hud-scale")?.textContent;
    key("r");
    const fitted = scale();
    act(() => {
      fireEvent.wheel(document.querySelector(".map-stage") as HTMLElement, { deltaY: -100, clientX: 200, clientY: 200 });
    });
    expect(scale()).not.toBe(fitted);
    key("R");
    expect(scale()).toBe(fitted);
  });

  it("? shows the list of keys, and Esc closes it", async () => {
    await loaded(fakeHost());
    key("?");
    expect(screen.getByRole("dialog", { name: "Shortcuts" })).toBeTruthy();
    key("Escape");
    expect(screen.queryByRole("dialog", { name: "Shortcuts" })).toBeNull();
  });

  it("typing in a field ignores the keys", async () => {
    const host = await loaded(fakeHost());
    const seed = screen.getByLabelText("Seed");
    for (const k of ["2", "a", "n", " ", "d"]) {
      key(k, seed);
    }
    expect(posts(host)).toEqual([]);
    expect(screen.getByRole("complementary", { name: "World" })).toBeTruthy();
    expect(screen.getByRole("button", { name: "Play" }).getAttribute("aria-pressed")).toBe("false");
  });
});

// Proves F-068 FR-67 (docs/paperwork/steps/F-068.md).
describe("stale answers", () => {
  it("a status answer that left before a layer change is not applied after it", async () => {
    const host = await loaded(fakeHost());
    const hold = host.holdNextStatus();
    await waitFor(() => expect(hold.held()).toBe(true), { timeout: 2000 });

    key("2");
    await waitFor(() => expect(layerPressed("Plates")).toBe(true));
    await act(async () => {
      hold.release();
      await new Promise((resolve) => setTimeout(resolve, 20));
    });

    expect(layerPressed("Plates")).toBe(true);
  });
});

// Proves F-068 FR-68 (docs/paperwork/steps/F-068.md).
describe("offline", () => {
  it("shows a red dot and a banner with Retry, and Retry brings the world back with a green dot", async () => {
    const host = fakeHost({ online: false });
    render(<MapTool />);
    const banner = await screen.findByRole("alert");
    expect(within(banner).getByRole("button", { name: "Retry" })).toBeTruthy();
    expect(screen.getByLabelText("Host offline")).toBeTruthy();

    host.state.online = true;
    host.state.step = 3;
    await act(async () => {
      fireEvent.click(within(banner).getByRole("button", { name: "Retry" }));
    });

    await waitFor(() => expect(screen.queryByRole("alert")).toBeNull());
    expect(screen.getByLabelText("Host online")).toBeTruthy();
    expect(railStep()).toBe("3");
  });
});

// Proves F-068 FR-69 (docs/paperwork/steps/F-068.md).
describe("world rail", () => {
  it("Random picks a seed, Reset world grows it, and Copy puts the seed on the clipboard", async () => {
    const host = await loaded(fakeHost({ seed: 9 }));
    vi.spyOn(Math, "random").mockReturnValue(0.5);
    fireEvent.click(screen.getByRole("button", { name: "Random" }));
    expect((screen.getByLabelText("Seed") as HTMLInputElement).value).toBe("1073741823");

    fireEvent.click(screen.getByRole("button", { name: "Reset world" }));
    await waitFor(() => expect(posts(host)).toContain("/api/new-world?seed=1073741823"));

    const writeText = vi.fn(async () => undefined);
    vi.stubGlobal("navigator", { ...navigator, clipboard: { writeText } });
    await act(async () => {
      fireEvent.click(screen.getByRole("button", { name: "Copy" }));
    });
    expect(writeText).toHaveBeenCalledWith("1073741823");
    expect(screen.getByRole("button", { name: "Copied" })).toBeTruthy();
  });

  it("Advance ×N takes exactly N steps", async () => {
    const host = await loaded(fakeHost());
    fireEvent.change(screen.getByLabelText("Steps"), { target: { value: "7" } });
    await act(async () => {
      fireEvent.click(screen.getByRole("button", { name: "Advance ×N" }));
    });

    await waitFor(() => expect(railStep()).toBe("7"));
    expect(host.calls.filter((c) => c.path === "/api/command").map((c) => c.body)).toEqual(["session advance 7"]);
  });
});
