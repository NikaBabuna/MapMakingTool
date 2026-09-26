/*
 * File: ui/web/src/components/Terminal.test.tsx
 * Purpose: Proves the studio terminal: its prompt, running a line and showing the answer, history of the last 32 lines, and Clear
 * Audience: Agents / CI
 * Update when: The terminal's behaviour changes
 */

import { fireEvent, render, screen } from "@testing-library/react";
import { act } from "react";
import { describe, expect, it, vi } from "vitest";
import { Terminal } from "@/components/Terminal";
import type { CommandResult, HostStatus } from "@/lib/host";

const STATUS = { step: 0 } as HostStatus;

function answer(line: string): CommandResult {
  return line === "nope"
    ? { exitCode: 2, ok: false, output: "error: unknown command: nope", status: STATUS }
    : { exitCode: 0, ok: true, output: `ran ${line}`, status: STATUS };
}

async function type(line: string) {
  const input = screen.getByPlaceholderText("help");
  fireEvent.change(input, { target: { value: line } });
  await act(async () => {
    fireEvent.keyDown(input, { key: "Enter" });
  });
}

function input(): HTMLInputElement {
  return screen.getByPlaceholderText("help") as HTMLInputElement;
}

function transcript(): string[] {
  return Array.from(document.querySelectorAll(".terminal-line")).map((el) => el.textContent ?? "");
}

// Proves F-068 FR-70 (docs/paperwork/steps/F-068.md).
describe("terminal", () => {
  it("shows the aethelgard> prompt", () => {
    render(<Terminal onRun={vi.fn()} />);
    expect(document.querySelector(".terminal-prompt")?.textContent).toBe("aethelgard>");
  });

  it("sends a line on Enter and shows the line and its answer, refused or not", async () => {
    const onRun = vi.fn(async (line: string) => answer(line));
    render(<Terminal onRun={onRun} />);

    await type("session get");
    await type("nope");

    expect(onRun).toHaveBeenNthCalledWith(1, "session get");
    expect(onRun).toHaveBeenNthCalledWith(2, "nope");
    expect(transcript()).toEqual([
      "aethelgard> session get",
      "ran session get",
      "aethelgard> nope",
      "error: unknown command: nope\n(exit 2)",
    ]);
    expect(input().value).toBe("");
  });

  it("walks back through the last 32 lines with ↑ and forward with ↓", async () => {
    render(<Terminal onRun={vi.fn(async (line: string) => answer(line))} />);
    for (let i = 1; i <= 40; i++) {
      await type(`line ${i}`);
    }

    fireEvent.keyDown(input(), { key: "ArrowUp" });
    expect(input().value).toBe("line 40");
    for (let i = 0; i < 50; i++) {
      fireEvent.keyDown(input(), { key: "ArrowUp" });
    }
    expect(input().value).toBe("line 9");

    fireEvent.keyDown(input(), { key: "ArrowDown" });
    expect(input().value).toBe("line 10");
    for (let i = 0; i < 50; i++) {
      fireEvent.keyDown(input(), { key: "ArrowDown" });
    }
    expect(input().value).toBe("");
  });

  it("Clear empties the transcript", async () => {
    render(<Terminal onRun={vi.fn(async (line: string) => answer(line))} />);
    await type("status");
    expect(transcript().length).toBe(2);

    fireEvent.click(screen.getByRole("button", { name: "Clear" }));

    expect(transcript()).toEqual([]);
  });
});
