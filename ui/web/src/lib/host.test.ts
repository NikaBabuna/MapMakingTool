/*
 * File: ui/web/src/lib/host.test.ts
 * Purpose: Proves what the page's host client returns: the typed status, the command result, the routes it calls, and its failures
 * Audience: Agents / CI
 * Update when: The host client or the host's routes change
 */

import { describe, expect, it, vi } from "vitest";
import {
  fetchHealth,
  fetchStatus,
  hostBase,
  postCommand,
  postInspect,
  postLayer,
  postNewWorld,
  type CommandResult,
  type HostStatus,
} from "@/lib/host";

const STATUS: HostStatus = {
  step: 3,
  seed: 42,
  width: 8,
  height: 8,
  layer: "Plates",
  speed: "2x",
  playing: false,
  busy: false,
  statusText: "Step 3",
  inspect: { x: 1, y: 2, elevation: -3, plateId: 4, vx: 1, vy: -1 },
  legend: [{ rgb: 0x24262a, label: "Boundary" }],
  diag: { "advance.wall": { last: 10, mean: 12, n: 3 } },
};

function answer(body: unknown, status = 200): Response {
  const text = typeof body === "string" ? body : JSON.stringify(body);
  return new Response(text, { status });
}

// Proves F-068 FR-64 (docs/paperwork/steps/F-068.md).
describe("host client", () => {
  it("returns the status the host sent, in typed form", async () => {
    const fetchMock = vi.fn(async () => answer(STATUS));
    vi.stubGlobal("fetch", fetchMock);

    const status = await fetchStatus("http://host");

    expect(status).toEqual(STATUS);
    expect(fetchMock).toHaveBeenCalledWith("http://host/api/status", { cache: "no-store" });
  });

  it("sends each action to its route and returns the answer", async () => {
    const calls: string[] = [];
    vi.stubGlobal("fetch", vi.fn(async (url: string, init?: RequestInit) => {
      calls.push(`${init?.method ?? "GET"} ${url}`);
      return answer(STATUS);
    }));

    await postLayer("Overlay", "http://host");
    await postNewWorld(7, "http://host");
    await postInspect(3, 4, "http://host");

    expect(calls).toEqual([
      "POST http://host/api/layer?layer=Overlay",
      "POST http://host/api/new-world?seed=7",
      "POST http://host/api/inspect?x=3&y=4",
    ]);
  });

  it("returns the command result, refused or not", async () => {
    const refused: CommandResult = { exitCode: 2, ok: false, output: "error: unknown command: nope", status: STATUS };
    const fetchMock = vi.fn(async () => answer(refused, 400));
    vi.stubGlobal("fetch", fetchMock);

    const result = await postCommand("nope", "http://host");

    expect(result).toEqual(refused);
    expect(fetchMock).toHaveBeenCalledWith("http://host/api/command", expect.objectContaining({ method: "POST", body: "nope" }));
  });

  it("reports a refused action with the host's message, and an unreachable host as not healthy", async () => {
    vi.stubGlobal("fetch", vi.fn(async () => answer("error: unknown layer", 400)));
    await expect(postLayer("Plates", "http://host")).rejects.toThrow("error: unknown layer");

    vi.stubGlobal("fetch", vi.fn(async () => {
      throw new TypeError("fetch failed");
    }));
    expect(await fetchHealth("http://host")).toBe(false);

    vi.stubGlobal("fetch", vi.fn(async () => answer("ok")));
    expect(await fetchHealth("http://host")).toBe(true);
  });

  it("talks to the local host unless the environment names another", () => {
    vi.stubEnv("NEXT_PUBLIC_MAP_HOST", "");
    expect(hostBase()).toBe("http://127.0.0.1:7420");
    vi.stubEnv("NEXT_PUBLIC_MAP_HOST", "http://elsewhere:9000/");
    expect(hostBase()).toBe("http://elsewhere:9000");
    vi.unstubAllEnvs();
  });
});
