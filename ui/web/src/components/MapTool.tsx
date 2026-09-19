"use client";

/*
 * File: ui/web/src/components/MapTool.tsx
 * Purpose: Simulation runner chrome against MapHost (F-052)
 * Audience: App page
 * Update when: Tool controls, layout, or QoL shortcuts change
 */

import { useCallback, useEffect, useRef, useState } from "react";
import { MapCanvas, resetViewport } from "@/components/MapCanvas";
import { Terminal, type TerminalHandle } from "@/components/Terminal";
import {
  fetchHealth,
  fetchRaster,
  fetchStatus,
  hostBase,
  MapLayerName,
  MapSpeedName,
  postAdvance,
  postCommand,
  postInspect,
  postLayer,
  postNewWorld,
  postSpeed,
  SPEED_MS,
  HostStatus,
  DiagSample,
} from "@/lib/host";
import { IDENTITY_VIEWPORT, Viewport, fittedViewport } from "@/lib/viewport";
import { rgbCss } from "@/lib/raster";

const LAYERS: MapLayerName[] = ["Elevation", "Plates", "Overlay"];
const SPEEDS: MapSpeedName[] = ["1x", "2x", "4x", "Fastest"];
const DOCK_KEY = "aethelgard.dockOpen";
const INSPECT_PANEL_KEY = "aethelgard.panelInspectOpen";
const LEGEND_PANEL_KEY = "aethelgard.panelLegendOpen";
const PERF_PANEL_KEY = "aethelgard.panelPerfOpen";

/** Ordered metric rows for the left Perf rail (extensible). */
const PERF_ROWS: { id: string; label: string; kind: "ns" | "bytes" }[] = [
  { id: "paint.wall", label: "Frame (paint)", kind: "ns" },
  { id: "advance.wall", label: "Advance", kind: "ns" },
  { id: "phase.trace", label: "Phase · trace", kind: "ns" },
  { id: "phase.interaction", label: "Phase · interaction", kind: "ns" },
  { id: "phase.integrate", label: "Phase · integrate", kind: "ns" },
  { id: "phase.apply", label: "Phase · apply", kind: "ns" },
  { id: "phase.orogeny", label: "Phase · orogeny", kind: "ns" },
  { id: "heap.used", label: "Heap used", kind: "bytes" },
  { id: "heap.max", label: "Heap max", kind: "bytes" },
];

function readFlag(key: string, fallback: boolean): boolean {
  if (typeof window === "undefined") {
    return fallback;
  }
  const raw = window.localStorage.getItem(key);
  if (raw === null) {
    return fallback;
  }
  return raw !== "0";
}

function readDockOpen(): boolean {
  return readFlag(DOCK_KEY, true);
}

function isTypingTarget(el: EventTarget | null): boolean {
  if (!(el instanceof HTMLElement)) {
    return false;
  }
  const tag = el.tagName;
  return tag === "INPUT" || tag === "TEXTAREA" || tag === "SELECT" || el.isContentEditable;
}

function formatNs(sample: DiagSample | undefined): string {
  if (!sample || sample.n === 0 || sample.mean == null) {
    return "—";
  }
  const ms = sample.mean / 1_000_000;
  if (ms >= 10) {
    return `${ms.toFixed(1)} ms`;
  }
  if (ms >= 1) {
    return `${ms.toFixed(2)} ms`;
  }
  return `${(sample.mean / 1000).toFixed(0)} µs`;
}

function formatBytes(sample: DiagSample | undefined): string {
  if (!sample || sample.n === 0 || sample.mean == null) {
    return "—";
  }
  const mb = sample.mean / (1024 * 1024);
  return `${mb.toFixed(1)} MiB`;
}

function formatDiag(sample: DiagSample | undefined, kind: "ns" | "bytes"): string {
  return kind === "bytes" ? formatBytes(sample) : formatNs(sample);
}

export function MapTool() {
  const [status, setStatus] = useState<HostStatus | null>(null);
  const [raster, setRaster] = useState<ArrayBuffer | null>(null);
  const [seedText, setSeedText] = useState("0");
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState<MapSpeedName>("1x");
  const [dockOpen, setDockOpen] = useState(true);
  const [inspectOpen, setInspectOpen] = useState(true);
  const [legendOpen, setLegendOpen] = useState(true);
  const [perfOpen, setPerfOpen] = useState(true);
  const [viewport, setViewport] = useState<Viewport>(IDENTITY_VIEWPORT);
  const stageMetricsRef = useRef({
    stageW: 1,
    stageH: 1,
    displayW: 1920,
    displayH: 1080,
  });
  const fittedOnceRef = useRef(false);
  const terminalRef = useRef<TerminalHandle>(null);
  const [error, setError] = useState<string | null>(null);
  const [online, setOnline] = useState(false);
  const [confirmNew, setConfirmNew] = useState(false);
  const playRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const busyRef = useRef(false);

  useEffect(() => {
    setDockOpen(readDockOpen());
    setInspectOpen(readFlag(INSPECT_PANEL_KEY, true));
    setLegendOpen(readFlag(LEGEND_PANEL_KEY, true));
    setPerfOpen(readFlag(PERF_PANEL_KEY, true));
  }, []);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(DOCK_KEY, dockOpen ? "1" : "0");
  }, [dockOpen]);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(INSPECT_PANEL_KEY, inspectOpen ? "1" : "0");
  }, [inspectOpen]);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(LEGEND_PANEL_KEY, legendOpen ? "1" : "0");
  }, [legendOpen]);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(PERF_PANEL_KEY, perfOpen ? "1" : "0");
  }, [perfOpen]);

  const refresh = useCallback(async () => {
    const next = await fetchStatus();
    setStatus(next);
    setSeedText(String(next.seed));
    setSpeed(next.speed as MapSpeedName);
    busyRef.current = next.busy;
    const bytes = await fetchRaster();
    setRaster(bytes);
    return next;
  }, []);

  const connect = useCallback(async () => {
    const ok = await fetchHealth();
    setOnline(ok);
    if (!ok) {
      setError(`Map host offline at ${hostBase()}. Start MapHostApp on port 7420.`);
      return;
    }
    setError(null);
    await refresh();
  }, [refresh]);

  useEffect(() => {
    void connect();
    const id = setInterval(() => {
      void (async () => {
        const ok = await fetchHealth();
        setOnline(ok);
        if (!ok) {
          return;
        }
        try {
          const next = await fetchStatus();
          setStatus(next);
          busyRef.current = next.busy;
          if (!next.busy) {
            const bytes = await fetchRaster();
            setRaster(bytes);
          }
        } catch {
          /* keep last frame */
        }
      })();
    }, 200);
    return () => clearInterval(id);
  }, [connect]);

  useEffect(() => {
    if (playRef.current) {
      clearInterval(playRef.current);
      playRef.current = null;
    }
    if (!playing) {
      return;
    }
    playRef.current = setInterval(() => {
      if (busyRef.current) {
        return;
      }
      void (async () => {
        try {
          const next = await postAdvance();
          setStatus(next);
          busyRef.current = next.busy;
        } catch (err) {
          setPlaying(false);
          setError(err instanceof Error ? err.message : String(err));
        }
      })();
    }, SPEED_MS[speed]);
    return () => {
      if (playRef.current) {
        clearInterval(playRef.current);
        playRef.current = null;
      }
    };
  }, [playing, speed]);

  function fitView() {
    const m = stageMetricsRef.current;
    setViewport(resetViewport(m.stageW, m.stageH, m.displayW, m.displayH));
  }

  const onStageMetrics = useCallback(
    (next: { stageW: number; stageH: number; displayW: number; displayH: number }) => {
      const prev = stageMetricsRef.current;
      if (
        prev.stageW === next.stageW &&
        prev.stageH === next.stageH &&
        prev.displayW === next.displayW &&
        prev.displayH === next.displayH
      ) {
        return;
      }
      stageMetricsRef.current = next;
      if (!fittedOnceRef.current && next.stageW > 1 && next.displayW > 0) {
        fittedOnceRef.current = true;
        setViewport(fittedViewport(next.stageW, next.stageH, next.displayW, next.displayH));
      }
    },
    [],
  );

  async function onAdvance() {
    if (busyRef.current) {
      return;
    }
    try {
      const next = await postAdvance();
      setStatus(next);
      busyRef.current = next.busy;
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  async function onLayer(layer: MapLayerName) {
    try {
      const next = await postLayer(layer);
      setStatus(next);
      const bytes = await fetchRaster();
      setRaster(bytes);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  async function onSpeed(nextSpeed: MapSpeedName) {
    setSpeed(nextSpeed);
    try {
      const next = await postSpeed(nextSpeed);
      setStatus(next);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  function bumpSpeed(dir: -1 | 1) {
    const idx = SPEEDS.indexOf(speed);
    const next = SPEEDS[Math.min(SPEEDS.length - 1, Math.max(0, idx + dir))];
    if (next && next !== speed) {
      void onSpeed(next);
    }
  }

  async function doNewWorld() {
    if (busyRef.current) {
      return;
    }
    const parsed = Number.parseInt(seedText, 10);
    if (Number.isNaN(parsed)) {
      return;
    }
    setPlaying(false);
    setConfirmNew(false);
    try {
      const next = await postNewWorld(parsed);
      setStatus(next);
      busyRef.current = next.busy;
      const bytes = await fetchRaster();
      setRaster(bytes);
      fitView();
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  function requestNewWorld() {
    if (busyRef.current) {
      return;
    }
    const step = status?.step ?? 0;
    if (step > 0) {
      setConfirmNew(true);
      return;
    }
    void doNewWorld();
  }

  function randomSeed() {
    const next = Math.floor(Math.random() * 2_147_483_647);
    setSeedText(String(next));
  }

  async function onCell(x: number, y: number) {
    try {
      const next = await postInspect(x, y);
      setStatus(next);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  async function onTerminalRun(line: string) {
    const result = await postCommand(line);
    setStatus(result.status);
    busyRef.current = result.status.busy;
    const bytes = await fetchRaster();
    setRaster(bytes);
    return result;
  }

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (confirmNew) {
        if (e.key === "Escape") {
          setConfirmNew(false);
        }
        return;
      }
      if (isTypingTarget(e.target)) {
        return;
      }
      const key = e.key;
      if (key === " " || key === "Spacebar") {
        e.preventDefault();
        setPlaying((p) => !p);
        return;
      }
      if (key === "a" || key === "A" || key === ".") {
        e.preventDefault();
        void onAdvance();
        return;
      }
      if (key === "1") {
        void onLayer("Elevation");
        return;
      }
      if (key === "2") {
        void onLayer("Plates");
        return;
      }
      if (key === "3") {
        void onLayer("Overlay");
        return;
      }
      if (key === "[") {
        bumpSpeed(-1);
        return;
      }
      if (key === "]") {
        bumpSpeed(1);
        return;
      }
      if (key === "n" || key === "N") {
        e.preventDefault();
        requestNewWorld();
        return;
      }
      if (key === "`" || key === "c" || key === "C") {
        e.preventDefault();
        terminalRef.current?.focus();
        return;
      }
      if (key === "d" || key === "D") {
        e.preventDefault();
        setDockOpen((o) => !o);
        return;
      }
      if (key === "r" || key === "R") {
        e.preventDefault();
        fitView();
      }
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  });

  const busy = status?.busy ?? false;
  const statusText = status?.statusText ?? (online ? "Connecting…" : "Host offline");
  const diag = status?.diag;

  return (
    <div className={`studio${dockOpen ? "" : " dock-closed-root"}`}>
      <header className="studio-bar runner-bar">
        <div className="runner-slot runner-identity">
          <h1 className="studio-brand">Aethelgard</h1>
          <span
            className={`host-dot${online ? " is-on" : ""}`}
            title={online ? `Online · ${hostBase()}` : "Offline"}
            aria-label={online ? "Host online" : "Host offline"}
          />
        </div>

        <div className="runner-slot runner-transport" role="toolbar" aria-label="Transport">
          <button
            type="button"
            className={`btn${playing ? " is-pressed" : ""}`}
            disabled={busy && !playing}
            onClick={() => setPlaying(true)}
            aria-pressed={playing}
          >
            Play
          </button>
          <button
            type="button"
            className="btn"
            disabled={!playing}
            onClick={() => setPlaying(false)}
          >
            Pause
          </button>
          <label className="field speed-field">
            <span className="sr-only">Speed</span>
            <select
              value={speed}
              aria-label="Speed"
              onChange={(e) => void onSpeed(e.target.value as MapSpeedName)}
            >
              {SPEEDS.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="runner-slot runner-view">
          <button type="button" className="btn" onClick={fitView} title="Reset view (R)">
            Reset view
          </button>
          <button
            type="button"
            className={`btn${dockOpen ? " is-pressed" : ""}`}
            aria-pressed={dockOpen}
            onClick={() => setDockOpen((o) => !o)}
            title="Toggle world panel (D)"
          >
            World
          </button>
        </div>
      </header>

      {error ? (
        <p className="error-banner" role="alert">
          <span>{error}</span>
          <button type="button" className="btn" onClick={() => void connect()}>
            Retry
          </button>
        </p>
      ) : null}

      <div className="studio-body">
        <div className={`studio-work${dockOpen ? "" : " dock-closed"}`}>
          <aside className="side-rail perf-rail" aria-label="Performance">
            <article className="studio-panel" data-panel="perf">
              <header className="panel-chrome">
                <h2>Perf</h2>
                <button
                  type="button"
                  className="panel-toggle"
                  aria-expanded={perfOpen}
                  onClick={() => setPerfOpen((o) => !o)}
                  title={perfOpen ? "Collapse Perf" : "Expand Perf"}
                >
                  {perfOpen ? "−" : "+"}
                </button>
              </header>
              {perfOpen ? (
                <div className="panel-body">
                  <dl className="perf-grid">
                    {PERF_ROWS.map((row) => (
                      <div key={row.id} data-diag={row.id}>
                        <dt>{row.label}</dt>
                        <dd className="mono">{formatDiag(diag?.[row.id], row.kind)}</dd>
                      </div>
                    ))}
                  </dl>
                  <p className="muted perf-hint">Means from DiagnosticsHub · same session as stats</p>
                </div>
              ) : null}
            </article>
          </aside>

          <MapCanvas
            buffer={raster}
            viewport={viewport}
            onViewportChange={setViewport}
            onStageMetrics={onStageMetrics}
            onCell={(x, y) => void onCell(x, y)}
            layer={status?.layer ?? "Elevation"}
            layers={LAYERS}
            onLayer={(name) => void onLayer(name as MapLayerName)}
          />

          {dockOpen ? (
            <aside className="side-rail world-rail" aria-label="World">
              <article className="studio-panel" data-panel="world">
                <header className="panel-chrome">
                  <h2>World</h2>
                </header>
                <div className="panel-body">
                  <dl className="inspect-grid world-grid">
                    <div>
                      <dt>Step</dt>
                      <dd>{status?.step ?? "—"}</dd>
                    </div>
                    <div>
                      <dt>Size</dt>
                      <dd>{status ? `${status.width}×${status.height}` : "—"}</dd>
                    </div>
                    <div>
                      <dt>Seed</dt>
                      <dd className="mono">{status?.seed ?? "—"}</dd>
                    </div>
                  </dl>
                  <label className="field seed">
                    <span>Seed</span>
                    <input
                      value={seedText}
                      disabled={busy}
                      onChange={(e) => setSeedText(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter") {
                          requestNewWorld();
                        }
                      }}
                    />
                  </label>
                  <div className="world-actions">
                    <button type="button" className="btn" disabled={busy} onClick={randomSeed} title="Random seed">
                      Random
                    </button>
                    <button type="button" className="btn" disabled={busy} onClick={requestNewWorld}>
                      Reset world
                    </button>
                  </div>
                  <p className={`status-chip${busy ? " is-busy" : ""}`} aria-live="polite">
                    {statusText}
                  </p>
                </div>
              </article>

              <article className="studio-panel" data-panel="inspect">
                <header className="panel-chrome">
                  <h2>Inspect</h2>
                  <button
                    type="button"
                    className="panel-toggle"
                    aria-expanded={inspectOpen}
                    onClick={() => setInspectOpen((o) => !o)}
                    title={inspectOpen ? "Collapse Inspect" : "Expand Inspect"}
                  >
                    {inspectOpen ? "−" : "+"}
                  </button>
                </header>
                {inspectOpen ? (
                  <div className="panel-body">
                    {status?.inspect ? (
                      <dl className="inspect-grid">
                        <div>
                          <dt>Cell</dt>
                          <dd>
                            {status.inspect.x}, {status.inspect.y}
                          </dd>
                        </div>
                        <div>
                          <dt>Elevation</dt>
                          <dd>{status.inspect.elevation}</dd>
                        </div>
                        <div>
                          <dt>Plate</dt>
                          <dd>{status.inspect.plateId}</dd>
                        </div>
                        <div>
                          <dt>Velocity</dt>
                          <dd>
                            {status.inspect.vx}, {status.inspect.vy}
                          </dd>
                        </div>
                      </dl>
                    ) : (
                      <p className="muted">Click the map to inspect a cell.</p>
                    )}
                  </div>
                ) : null}
              </article>

              <article className="studio-panel" data-panel="legend">
                <header className="panel-chrome">
                  <h2>Legend</h2>
                  <button
                    type="button"
                    className="panel-toggle"
                    aria-expanded={legendOpen}
                    onClick={() => setLegendOpen((o) => !o)}
                    title={legendOpen ? "Collapse Legend" : "Expand Legend"}
                  >
                    {legendOpen ? "−" : "+"}
                  </button>
                </header>
                {legendOpen ? (
                  <div className="panel-body">
                    <ul className="legend-list">
                      {(status?.legend ?? []).map((row) => (
                        <li key={`${row.rgb}-${row.label}`}>
                          <span className="swatch" style={{ background: rgbCss(row.rgb) }} />
                          {row.label}
                        </li>
                      ))}
                    </ul>
                  </div>
                ) : null}
              </article>
            </aside>
          ) : null}
        </div>

        <Terminal ref={terminalRef} onRun={onTerminalRun} />
      </div>

      {confirmNew ? (
        <div className="confirm-backdrop" role="presentation">
          <div className="confirm-dialog" role="alertdialog" aria-labelledby="new-world-title">
            <p id="new-world-title">Create a new world with seed {seedText}? Current progress will be lost.</p>
            <div className="confirm-actions">
              <button type="button" className="btn" onClick={() => setConfirmNew(false)}>
                Cancel
              </button>
              <button type="button" className="btn primary" onClick={() => void doNewWorld()}>
                New world
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
