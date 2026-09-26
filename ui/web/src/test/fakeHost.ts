/*
 * File: ui/web/src/test/fakeHost.ts
 * Purpose: A stand-in for the map host: answers the page's requests from a small world state and records every call
 * Audience: Agents / CI
 * Update when: The host's routes or status shape change
 */

import { vi } from "vitest";
import type { CommandResult, HostStatus, MapLayerName, MapSpeedName } from "@/lib/host";

export type Call = { method: string; path: string; body?: string };

export type FakeHost = {
  state: {
    step: number;
    seed: number;
    width: number;
    height: number;
    layer: MapLayerName;
    speed: MapSpeedName;
    busy: boolean;
    online: boolean;
  };
  calls: Call[];
  /** Makes the next status request wait until {@code release} is called, then answer with the status of that moment. */
  holdNextStatus(): { held: () => boolean; release: () => void };
  status(): HostStatus;
};

export function fakeHost(initial: Partial<FakeHost["state"]> = {}): FakeHost {
  const state: FakeHost["state"] = {
    step: 0,
    seed: 0,
    width: 16,
    height: 8,
    layer: "Elevation",
    speed: "1x",
    busy: false,
    online: true,
    ...initial,
  };
  const calls: Call[] = [];
  let pendingHold: { snapshot: HostStatus | null; resolve: (() => void) | null } | null = null;

  const status = (): HostStatus => ({
    step: state.step,
    seed: state.seed,
    width: state.width,
    height: state.height,
    layer: state.layer,
    speed: state.speed,
    playing: false,
    busy: state.busy,
    statusText: state.busy ? "Working..." : `Step ${state.step}`,
    inspect: null,
    legend: [{ rgb: 0x24262a, label: "Boundary" }],
    diag: {},
  });

  const json = (body: unknown, code = 200) => new Response(JSON.stringify(body), { status: code });

  const raster = () => {
    const view = new DataView(new ArrayBuffer(8 + state.width * state.height * 4));
    view.setInt32(0, state.width, false);
    view.setInt32(4, state.height, false);
    return new Response(view.buffer, { status: 200 });
  };

  const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = new URL(String(input));
    const method = init?.method ?? "GET";
    const body = typeof init?.body === "string" ? init.body : undefined;
    calls.push({ method, path: url.pathname + url.search, body });
    if (!state.online) {
      throw new TypeError("fetch failed");
    }
    const q = url.searchParams;
    switch (url.pathname) {
      case "/health":
        return new Response("ok", { status: 200 });
      case "/api/status": {
        if (pendingHold && pendingHold.resolve === null) {
          const hold = pendingHold;
          hold.snapshot = status();
          await new Promise<void>((resolve) => {
            hold.resolve = resolve;
          });
          return json(hold.snapshot);
        }
        return json(status());
      }
      case "/api/raster":
        return raster();
      case "/api/advance":
        state.step += 1;
        return json(status());
      case "/api/layer":
        state.layer = q.get("layer") as MapLayerName;
        return json(status());
      case "/api/speed":
        state.speed = q.get("speed") as MapSpeedName;
        return json(status());
      case "/api/new-world":
        state.seed = Number(q.get("seed"));
        state.step = 0;
        return json(status());
      case "/api/restart-engine":
        state.step = 0;
        return json(status());
      case "/api/inspect":
        return json(status());
      case "/api/command": {
        const match = /^session advance (\d+)$/.exec(body ?? "");
        if (match) {
          state.step += Number(match[1]);
        }
        const result: CommandResult = { exitCode: 0, ok: true, output: `step=${state.step}`, status: status() };
        return json(result);
      }
      default:
        return new Response("not found", { status: 404 });
    }
  });
  vi.stubGlobal("fetch", fetchMock);

  return {
    state,
    calls,
    status,
    holdNextStatus() {
      pendingHold = { snapshot: null, resolve: null };
      const hold = pendingHold;
      return {
        held: () => hold.resolve !== null,
        release: () => {
          pendingHold = null;
          hold.resolve?.();
        },
      };
    },
  };
}

/** The POST calls made so far, as "path" strings. */
export function posts(host: FakeHost): string[] {
  return host.calls.filter((c) => c.method === "POST").map((c) => c.path);
}
