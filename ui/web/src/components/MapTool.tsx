"use client";

/*
 * File: ui/web/src/components/MapTool.tsx
 * Purpose: Living-map tool chrome against MapHost
 * Audience: App page
 * Update when: Tool controls or play loop change
 */

import { useCallback, useEffect, useRef, useState } from "react";
import { MapCanvas } from "@/components/MapCanvas";
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
import { rgbCss } from "@/lib/raster";

const LAYERS: MapLayerName[] = ["Elevation", "Plates", "Overlay"];
const SPEEDS: MapSpeedName[] = ["Slow", "Normal", "Fast"];

export function MapTool() {
  const [status, setStatus] = useState<HostStatus | null>(null);
  const [raster, setRaster] = useState<ArrayBuffer | null>(null);
  const [seedText, setSeedText] = useState("0");
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState<MapSpeedName>("Normal");
  const [consoleLine, setConsoleLine] = useState("");
  const [consoleLog, setConsoleLog] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [online, setOnline] = useState(false);
  const playRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const busyRef = useRef(false);

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

  async function onNewWorld() {
    if (busyRef.current) {
      return;
    }
    const parsed = Number.parseInt(seedText, 10);
    if (Number.isNaN(parsed)) {
      return;
    }
    setPlaying(false);
    try {
      const next = await postNewWorld(parsed);
      setStatus(next);
      busyRef.current = next.busy;
      const bytes = await fetchRaster();
      setRaster(bytes);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
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

  const busy = status?.busy ?? false;
  const statusText = status?.statusText ?? (online ? "Connecting…" : "Host offline");

  return (
    <div className="tool-shell">
      <header className="tool-brand">
        <div>
          <p className="eyebrow">Living map</p>
          <h1 className="brand">Aethelgard</h1>
        </div>
        <div className={`host-pill${online ? " is-on" : ""}`}>
          <span className="host-dot" />
          {online ? "Host linked" : "Host offline"}
        </div>
      </header>

      <div className="tool-toolbar" role="toolbar" aria-label="Map controls">
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
          className="btn"
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
                void onNewWorld();
              }
            }}
          />
        </label>
        <button type="button" className="btn" disabled={busy} onClick={() => void onNewWorld()}>
          New world
        </button>

        <div className={`status-chip${busy ? " is-busy" : ""}`} aria-live="polite">
          {statusText}
        </div>
      </div>

      {error ? <p className="error-banner">{error}</p> : null}

      <div className="tool-body">
        <MapCanvas buffer={raster} busy={busy} onCell={(x, y) => void onCell(x, y)} />

        <aside className="side-panel">
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

          <section className="console">
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
          </section>
        </aside>
      </div>
    </div>
  );
}
