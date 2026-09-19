/*
 * File: ui/web/src/lib/host.ts
 * Purpose: Typed client for MapHost localhost API
 * Audience: Map tool React UI
 * Update when: Host routes or status JSON change
 */

export const DEFAULT_HOST = "http://127.0.0.1:7420";

export type MapLayerName = "Elevation" | "Plates" | "Overlay";
export type MapSpeedName = "1x" | "2x" | "4x" | "Fastest";

export const SPEED_MS: Record<MapSpeedName, number> = {
  "1x": 250,
  "2x": 125,
  "4x": 62,
  Fastest: 1,
};

export type Inspect = {
  x: number;
  y: number;
  elevation: number;
  plateId: number;
  vx: number;
  vy: number;
};

export type LegendRow = { rgb: number; label: string };

export type HostStatus = {
  step: number;
  seed: number;
  width: number;
  height: number;
  layer: MapLayerName;
  speed: MapSpeedName;
  playing: boolean;
  busy: boolean;
  statusText: string;
  inspect: Inspect | null;
  legend: LegendRow[];
};

export function hostBase(): string {
  const fromEnv = process.env.NEXT_PUBLIC_MAP_HOST;
  if (fromEnv && fromEnv.trim().length > 0) {
    return fromEnv.replace(/\/$/, "");
  }
  return DEFAULT_HOST;
}

async function readJson<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return (await res.json()) as T;
}

export async function fetchStatus(base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/status`, { cache: "no-store" });
  return readJson<HostStatus>(res);
}

export async function fetchRaster(base = hostBase()): Promise<ArrayBuffer> {
  const res = await fetch(`${base}/api/raster`, { cache: "no-store" });
  if (!res.ok) {
    throw new Error(`raster HTTP ${res.status}`);
  }
  return res.arrayBuffer();
}

export async function postAdvance(base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/advance`, { method: "POST" });
  return readJson<HostStatus>(res);
}

export async function postLayer(layer: MapLayerName, base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/layer?layer=${encodeURIComponent(layer)}`, {
    method: "POST",
  });
  return readJson<HostStatus>(res);
}

export async function postSpeed(speed: MapSpeedName, base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/speed?speed=${encodeURIComponent(speed)}`, {
    method: "POST",
  });
  return readJson<HostStatus>(res);
}

export async function postNewWorld(seed: number, base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/new-world?seed=${seed}`, { method: "POST" });
  return readJson<HostStatus>(res);
}

export async function postInspect(x: number, y: number, base = hostBase()): Promise<HostStatus> {
  const res = await fetch(`${base}/api/inspect?x=${x}&y=${y}`, { method: "POST" });
  return readJson<HostStatus>(res);
}

export type CommandResult = {
  exitCode: number;
  ok: boolean;
  output: string;
  status: HostStatus;
};

export async function postCommand(line: string, base = hostBase()): Promise<CommandResult> {
  const res = await fetch(`${base}/api/command`, {
    method: "POST",
    headers: { "Content-Type": "text/plain" },
    body: line,
  });
  const body = (await res.json()) as CommandResult;
  return body;
}

export async function fetchHealth(base = hostBase()): Promise<boolean> {
  try {
    const res = await fetch(`${base}/health`, { cache: "no-store" });
    return res.ok && (await res.text()) === "ok";
  } catch {
    return false;
  }
}
