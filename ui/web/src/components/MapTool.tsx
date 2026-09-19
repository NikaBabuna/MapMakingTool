"use client";

/*
 * File: ui/web/src/components/MapTool.tsx
 * Purpose: Studio cartography tool chrome against MapHost
 * Audience: App page
 * Update when: Tool controls, layout, or QoL shortcuts change
 */

import { useCallback, useEffect, useRef, useState, type KeyboardEvent } from "react";
import { MapCanvas, resetViewport } from "@/components/MapCanvas";
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
} from "@/lib/host";
import { IDENTITY_VIEWPORT, Viewport, fittedViewport } from "@/lib/viewport";
import { rgbCss } from "@/lib/raster";

const LAYERS: MapLayerName[] = ["Elevation", "Plates", "Overlay"];
const SPEEDS: MapSpeedName[] = ["Slow", "Normal", "Fast"];
const DOCK_KEY = "aethelgard.dockOpen";
const INSPECT_PANEL_KEY = "aethelgard.panelInspectOpen";
const LEGEND_PANEL_KEY = "aethelgard.panelLegendOpen";

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

export function MapTool() {
  const [status, setStatus] = useState<HostStatus | null>(null);
  const [raster, setRaster] = useState<ArrayBuffer | null>(null);
  const [seedText, setSeedText] = useState("0");
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState<MapSpeedName>("Normal");
  const [consoleLine, setConsoleLine] = useState("");
  const [consoleLog, setConsoleLog] = useState<string[]>([]);
  const [consoleOpen, setConsoleOpen] = useState(false);
  const [consoleHistory, setConsoleHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const consoleInputRef = useRef<HTMLInputElement>(null);
  const [dockOpen, setDockOpen] = useState(true);
  const [inspectOpen, setInspectOpen] = useState(true);
  const [legendOpen, setLegendOpen] = useState(true);
  const [viewport, setViewport] = useState<Viewport>(IDENTITY_VIEWPORT);
  const stageMetricsRef = useRef({
    stageW: 1,
    stageH: 1,
    displayW: 1920,
    displayH: 1080,
  });
  const fittedOnceRef = useRef(false);
  const [error, setError] = useState<string | null>(null);
  const [online, setOnline] = useState(false);
  const [confirmNew, setConfirmNew] = useState(false);
  const playRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const busyRef = useRef(false);

  useEffect(() => {
    setDockOpen(readDockOpen());
    setInspectOpen(readFlag(INSPECT_PANEL_KEY, true));
    setLegendOpen(readFlag(LEGEND_PANEL_KEY, true));
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

  const refresh = useCallback(async () => {
    const next = await fetchStatus();
    setStatus(next);
    setSeedText(String(next.seed));
    setSpeed(next.speed);
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

  useEffect(() => {
    if (consoleOpen) {
      consoleInputRef.current?.focus();
    }
  }, [consoleOpen]);

  async function onConsole() {
    const line = consoleLine.trim();
    if (!line) {
      return;
    }
    try {
      const result = await postCommand(line);
      setConsoleLog((prev) =>
        [`aethelgard> ${line}`, result.output || `(exit ${result.exitCode})`, ...prev].slice(0, 40),
      );
      setConsoleHistory((prev) => {
        if (prev[0] === line) {
          return prev;
        }
        return [line, ...prev].slice(0, 32);
      });
      setHistoryIndex(-1);
      setStatus(result.status);
      busyRef.current = result.status.busy;
      const bytes = await fetchRaster();
      setRaster(bytes);
      setConsoleLine("");
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  function onConsoleKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") {
      e.preventDefault();
      void onConsole();
      return;
    }
    if (e.key === "ArrowUp") {
      e.preventDefault();
      if (consoleHistory.length === 0) {
        return;
      }
      const next = Math.min(historyIndex + 1, consoleHistory.length - 1);
      setHistoryIndex(next);
      setConsoleLine(consoleHistory[next] ?? "");
      return;
    }
    if (e.key === "ArrowDown") {
      e.preventDefault();
      if (historyIndex <= 0) {
        setHistoryIndex(-1);
        setConsoleLine("");
        return;
      }
      const next = historyIndex - 1;
      setHistoryIndex(next);
      setConsoleLine(consoleHistory[next] ?? "");
    }
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
        setConsoleOpen((o) => !o);
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

  return (
    <div className={`studio${dockOpen ? "" : " dock-closed-root"}`}>
      <header className="studio-bar">
        <h1 className="studio-brand">Aethelgard</h1>
        <div className={`host-pill${online ? " is-on" : ""}`} title={hostBase()}>
          <span className="host-dot" />
          {online ? "Host linked" : "Host offline"}
        </div>

        <div className="studio-toolbar" role="toolbar" aria-label="Map controls">
          <label className="field">
            <span>Layer</span>
            <select
              value={status?.layer ?? "Elevation"}
              onChange={(e) => void onLayer(e.target.value as MapLayerName)}
            >
              {LAYERS.map((layer) => (
                <option key={layer} value={layer}>
                  {layer}
                </option>
              ))}
            </select>
          </label>

          <button type="button" className="btn primary" disabled={busy} onClick={() => void onAdvance()}>
            Advance
          </button>
          <button
            type="button"
            className={`btn${playing ? " is-pressed" : ""}`}
            onClick={() => setPlaying((p) => !p)}
            aria-pressed={playing}
          >
            {playing ? "Pause" : "Play"}
          </button>

          <label className="field">
            <span>Speed</span>
            <select value={speed} onChange={(e) => void onSpeed(e.target.value as MapSpeedName)}>
              {SPEEDS.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </label>

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
          <button type="button" className="btn" disabled={busy} onClick={randomSeed} title="Random seed">
            Random
          </button>
          <button type="button" className="btn" disabled={busy} onClick={requestNewWorld}>
            New world
          </button>

          <button
            type="button"
            className={`btn${dockOpen ? " is-pressed" : ""}`}
            aria-pressed={dockOpen}
            onClick={() => setDockOpen((o) => !o)}
            title="Toggle dock (D)"
          >
            Dock
          </button>
          <button
            type="button"
            className={`btn${consoleOpen ? " is-pressed" : ""}`}
            aria-pressed={consoleOpen}
            onClick={() => setConsoleOpen((o) => !o)}
            title="Toggle console (` / C)"
          >
            Console
          </button>
          <button type="button" className="btn" onClick={fitView} title="Reset view (R)">
            Reset view
          </button>

          <div className={`status-chip${busy ? " is-busy" : ""}`} aria-live="polite">
            {statusText}
          </div>
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

      <div className={`studio-work${dockOpen ? "" : " dock-closed"}`}>
        <MapCanvas
          buffer={raster}
          busy={busy}
          viewport={viewport}
          onViewportChange={setViewport}
          onStageMetrics={onStageMetrics}
          onCell={(x, y) => void onCell(x, y)}
        />

        {dockOpen ? (
          <aside className="side-rail" aria-label="Studio panels">
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

        <div
          className="console-drawer terminal"
          hidden={!consoleOpen}
          role="region"
          aria-label="Terminal console"
        >
          <div className="terminal-titlebar">
            <span className="terminal-title">Console</span>
            <span className="terminal-hint">↑↓ history · Enter run</span>
          </div>
          <pre className="console-log terminal-log">
            {consoleLog.join("\n") || "Placeholder verbs: status · advance · dump · at X Y · layers"}
          </pre>
          <div className="console-row terminal-input-row">
            <span className="terminal-prompt" aria-hidden>
              aethelgard&gt;
            </span>
            <input
              ref={consoleInputRef}
              className="terminal-input"
              value={consoleLine}
              placeholder="status"
              spellCheck={false}
              autoComplete="off"
              onChange={(e) => {
                setConsoleLine(e.target.value);
                setHistoryIndex(-1);
              }}
              onKeyDown={onConsoleKeyDown}
            />
            <button type="button" className="btn terminal-run" onClick={() => void onConsole()}>
              Run
            </button>
          </div>
        </div>
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
