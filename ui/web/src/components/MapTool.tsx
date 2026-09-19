"use client";

/*
 * File: ui/web/src/components/MapTool.tsx
 * Purpose: Studio cartography tool chrome against MapHost
 * Audience: App page
 * Update when: Tool controls, layout, or QoL shortcuts change
 */

import { useCallback, useEffect, useRef, useState } from "react";
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
import { IDENTITY_VIEWPORT, Viewport } from "@/lib/viewport";
import { rgbCss } from "@/lib/raster";

const LAYERS: MapLayerName[] = ["Elevation", "Plates", "Overlay"];
const SPEEDS: MapSpeedName[] = ["Slow", "Normal", "Fast"];
const DOCK_KEY = "aethelgard.dockOpen";

function readDockOpen(): boolean {
  if (typeof window === "undefined") {
    return true;
  }
  const raw = window.localStorage.getItem(DOCK_KEY);
  if (raw === null) {
    return true;
  }
  return raw !== "0";
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
  const [dockOpen, setDockOpen] = useState(true);
  const [viewport, setViewport] = useState<Viewport>(IDENTITY_VIEWPORT);
  const [error, setError] = useState<string | null>(null);
  const [online, setOnline] = useState(false);
  const [confirmNew, setConfirmNew] = useState(false);
  const playRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const busyRef = useRef(false);

  useEffect(() => {
    setDockOpen(readDockOpen());
  }, []);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }
    window.localStorage.setItem(DOCK_KEY, dockOpen ? "1" : "0");
  }, [dockOpen]);

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
      setViewport(resetViewport());
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

  async function onConsole() {
    const line = consoleLine.trim();
    if (!line) {
      return;
    }
    try {
      const result = await postCommand(line);
      setConsoleLog((prev) => [`> ${line}`, result.output || `(exit ${result.exitCode})`, ...prev].slice(0, 40));
      setStatus(result.status);
      busyRef.current = result.status.busy;
      const bytes = await fetchRaster();
      setRaster(bytes);
      setConsoleLine("");
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
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
        setViewport(resetViewport());
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
          <button type="button" className="btn" onClick={() => setViewport(resetViewport())} title="Reset view (R)">
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
          onCell={(x, y) => void onCell(x, y)}
        />

        {dockOpen ? (
          <aside className="side-dock" aria-label="Inspect and legend">
            <section>
              <h2>Inspect</h2>
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
            </section>

            <section>
              <h2>Legend</h2>
              <ul className="legend-list">
                {(status?.legend ?? []).map((row) => (
                  <li key={`${row.rgb}-${row.label}`}>
                    <span className="swatch" style={{ background: rgbCss(row.rgb) }} />
                    {row.label}
                  </li>
                ))}
              </ul>
            </section>
          </aside>
        ) : null}

        <div className="console-drawer" hidden={!consoleOpen} role="region" aria-label="Console">
          <h2>Console</h2>
          <div className="console-row">
            <input
              value={consoleLine}
              placeholder="status · advance · dump · at X Y · layers"
              onChange={(e) => setConsoleLine(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  void onConsole();
                }
              }}
            />
            <button type="button" className="btn" onClick={() => void onConsole()}>
              Run
            </button>
          </div>
          <pre className="console-log">{consoleLog.join("\n") || "Placeholder verbs only."}</pre>
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
