"use client";

/*
 * File: ui/web/src/components/MapTool.tsx
 * Purpose: Simulation runner chrome against MapHost (F-053)
 * Audience: App page
 * Update when: Tool controls, layout, or QoL shortcuts change
 */

import { useCallback, useEffect, useRef, useState, type CSSProperties } from "react";
import { MapCanvas, resetViewport } from "@/components/MapCanvas";
import { MenuBar } from "@/components/MenuBar";
import { Panel } from "@/components/Panel";
import { ShortcutsOverlay } from "@/components/ShortcutsOverlay";
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
  postRestartEngine,
  postSpeed,
  SPEED_MS,
  HostStatus,
  DiagSample,
} from "@/lib/host";
import { type MenuActionId } from "@/lib/menus";
import {
  DEFAULT_LAYOUT,
  LayoutRegion,
  LayoutSizes,
  RAIL_OPEN_KEYS,
  clampSize,
  clearLayout,
  readLayout,
  writeLayout,
} from "@/lib/layout";
import { panelOpenKey, panelsFor, defaultPanelOpen, type PanelDescriptor } from "@/lib/panels";
import { IDENTITY_VIEWPORT, Viewport, fittedViewport } from "@/lib/viewport";
import { rgbCss } from "@/lib/raster";

const LAYERS: MapLayerName[] = ["Elevation", "Plates", "Overlay"];
const SPEEDS: MapSpeedName[] = ["1x", "2x", "4x", "Fastest"];

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

function writeFlag(key: string, value: boolean): void {
  if (typeof window === "undefined") {
    return;
  }
  window.localStorage.setItem(key, value ? "1" : "0");
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

/** Steps per second implied by the mean advance wall time. */
function formatStepRate(sample: DiagSample | undefined): string {
  if (!sample || sample.n === 0 || sample.mean == null || sample.mean <= 0) {
    return "—";
  }
  const perSecond = 1_000_000_000 / sample.mean;
  return perSecond >= 10 ? `${perSecond.toFixed(0)} /s` : `${perSecond.toFixed(1)} /s`;
}

export function MapTool() {
  const [status, setStatus] = useState<HostStatus | null>(null);
  const [raster, setRaster] = useState<ArrayBuffer | null>(null);
  const [seedText, setSeedText] = useState("0");
  const [seedCopied, setSeedCopied] = useState(false);
  const [advanceCount, setAdvanceCount] = useState("10");
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState<MapSpeedName>("1x");
  const [leftRailOpen, setLeftRailOpen] = useState(true);
  const [dockOpen, setDockOpen] = useState(true);
  const [panelOpen, setPanelOpen] = useState<Record<string, boolean>>(defaultPanelOpen);
  const [layout, setLayout] = useState<LayoutSizes>(DEFAULT_LAYOUT);
  const [shortcutsOpen, setShortcutsOpen] = useState(false);
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
  /** Bumps on explicit layer changes so in-flight poll/raster applies cannot overwrite (F-054). */
  const applyGenRef = useRef(0);

  useEffect(() => {
    setLeftRailOpen(readFlag(RAIL_OPEN_KEYS.left, true));
    setDockOpen(readFlag(RAIL_OPEN_KEYS.right, true));
    setPanelOpen((prev) => {
      const next = { ...prev };
      for (const id of Object.keys(prev)) {
        next[id] = readFlag(panelOpenKey(id), prev[id]);
      }
      return next;
    });
    setLayout(readLayout());
  }, []);

  useEffect(() => {
    writeFlag(RAIL_OPEN_KEYS.left, leftRailOpen);
  }, [leftRailOpen]);

  useEffect(() => {
    writeFlag(RAIL_OPEN_KEYS.right, dockOpen);
  }, [dockOpen]);

  function togglePanel(id: string) {
    setPanelOpen((prev) => {
      const next = { ...prev, [id]: !prev[id] };
      writeFlag(panelOpenKey(id), next[id]);
      return next;
    });
  }

  const refresh = useCallback(async () => {
    const gen = applyGenRef.current;
    const next = await fetchStatus();
    if (gen !== applyGenRef.current) {
      return next;
    }
    setStatus(next);
    setSeedText(String(next.seed));
    setSpeed(next.speed as MapSpeedName);
    busyRef.current = next.busy;
    const bytes = await fetchRaster();
    if (gen !== applyGenRef.current) {
      return next;
    }
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
          const gen = applyGenRef.current;
          const next = await fetchStatus();
          if (gen !== applyGenRef.current) {
            return;
          }
          setStatus(next);
          busyRef.current = next.busy;
          if (!next.busy) {
            const bytes = await fetchRaster();
            if (gen !== applyGenRef.current) {
              return;
            }
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

  /** Drag one layout edge; pointer delta is applied to the region size and clamped. */
  function beginResize(region: LayoutRegion, event: React.PointerEvent<HTMLDivElement>) {
    event.preventDefault();
    const startX = event.clientX;
    const startY = event.clientY;
    const startSize = layout[region];

    function onMove(move: PointerEvent) {
      const delta =
        region === "leftRail"
          ? move.clientX - startX
          : region === "rightRail"
            ? startX - move.clientX
            : startY - move.clientY;
      setLayout((prev) => ({ ...prev, [region]: clampSize(region, startSize + delta) }));
    }
    function onUp() {
      window.removeEventListener("pointermove", onMove);
      window.removeEventListener("pointerup", onUp);
      setLayout((prev) => {
        writeLayout(prev);
        return prev;
      });
    }
    window.addEventListener("pointermove", onMove);
    window.addEventListener("pointerup", onUp);
  }

  function resetLayout() {
    clearLayout();
    setLayout({ ...DEFAULT_LAYOUT });
  }

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

  /** Advance N steps through the shared dispatcher (`session advance N`). */
  async function onAdvanceMany() {
    if (busyRef.current) {
      return;
    }
    const count = Number.parseInt(advanceCount, 10);
    if (!Number.isFinite(count) || count < 1) {
      return;
    }
    try {
      await onTerminalRun(`session advance ${count}`);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  async function onLayer(layer: MapLayerName) {
    const gen = ++applyGenRef.current;
    try {
      const next = await postLayer(layer);
      if (gen !== applyGenRef.current) {
        return;
      }
      setStatus(next);
      const bytes = await fetchRaster();
      if (gen !== applyGenRef.current) {
        return;
      }
      setRaster(bytes);
    } catch (err) {
      if (gen !== applyGenRef.current) {
        return;
      }
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
    const gen = ++applyGenRef.current;
    try {
      const next = await postNewWorld(parsed);
      if (gen !== applyGenRef.current) {
        return;
      }
      setStatus(next);
      busyRef.current = next.busy;
      const bytes = await fetchRaster();
      if (gen !== applyGenRef.current) {
        return;
      }
      setRaster(bytes);
      fitView();
      setError(null);
    } catch (err) {
      if (gen !== applyGenRef.current) {
        return;
      }
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  async function onRestartEngine() {
    setPlaying(false);
    const gen = ++applyGenRef.current;
    try {
      const next = await postRestartEngine();
      if (gen !== applyGenRef.current) {
        return;
      }
      setStatus(next);
      busyRef.current = next.busy;
      const bytes = await fetchRaster();
      if (gen !== applyGenRef.current) {
        return;
      }
      setRaster(bytes);
      setError(null);
    } catch (err) {
      if (gen !== applyGenRef.current) {
        return;
      }
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

  async function copySeed() {
    const value = String(status?.seed ?? seedText);
    try {
      await navigator.clipboard.writeText(value);
      setSeedCopied(true);
      window.setTimeout(() => setSeedCopied(false), 1200);
    } catch {
      setError("Clipboard unavailable — seed not copied.");
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

  async function onTerminalRun(line: string) {
    const gen = ++applyGenRef.current;
    const result = await postCommand(line);
    if (gen !== applyGenRef.current) {
      return result;
    }
    setStatus(result.status);
    busyRef.current = result.status.busy;
    const bytes = await fetchRaster();
    if (gen !== applyGenRef.current) {
      return result;
    }
    setRaster(bytes);
    return result;
  }

  function onMenuAction(action: MenuActionId) {
    switch (action) {
      case "world.new":
        requestNewWorld();
        return;
      case "world.randomSeed":
        randomSeed();
        return;
      case "seed.copy":
        void copySeed();
        return;
      case "view.leftRail":
        setLeftRailOpen((o) => !o);
        return;
      case "view.rightRail":
        setDockOpen((o) => !o);
        return;
      case "view.focusTerminal":
        terminalRef.current?.focus();
        return;
      case "view.resetView":
        fitView();
        return;
      case "view.resetLayout":
        resetLayout();
        return;
      case "sim.play":
        setPlaying(true);
        return;
      case "sim.pause":
        setPlaying(false);
        return;
      case "sim.advance":
        void onAdvance();
        return;
      case "sim.speed.1x":
        void onSpeed("1x");
        return;
      case "sim.speed.2x":
        void onSpeed("2x");
        return;
      case "sim.speed.4x":
        void onSpeed("4x");
        return;
      case "sim.speed.Fastest":
        void onSpeed("Fastest");
        return;
      case "sim.restartUi":
        window.location.reload();
        return;
      case "sim.restartEngine":
        void onRestartEngine();
        return;
      case "help.shortcuts":
        setShortcutsOpen(true);
        return;
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
      if (shortcutsOpen) {
        if (e.key === "Escape") {
          setShortcutsOpen(false);
        }
        return;
      }
      if (isTypingTarget(e.target)) {
        return;
      }
      const key = e.key;
      if (key === "?") {
        e.preventDefault();
        setShortcutsOpen(true);
        return;
      }
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
        e.preventDefault();
        void onLayer("Elevation");
        return;
      }
      if (key === "2") {
        e.preventDefault();
        void onLayer("Plates");
        return;
      }
      if (key === "3") {
        e.preventDefault();
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
      if (key === "p" || key === "P") {
        e.preventDefault();
        setLeftRailOpen((o) => !o);
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

  const shellVars: CSSProperties = {
    ["--perf-w" as string]: `${layout.leftRail}px`,
    ["--dock-w" as string]: `${layout.rightRail}px`,
    ["--terminal-h" as string]: `${layout.terminal}px`,
  };

  function panelBody(id: string) {
    if (id === "perf") {
      return (
        <>
          <dl className="perf-grid">
            {PERF_ROWS.map((row) => (
              <div key={row.id} data-diag={row.id}>
                <dt>{row.label}</dt>
                <dd className="mono">{formatDiag(diag?.[row.id], row.kind)}</dd>
              </div>
            ))}
            <div data-diag="advance.rate">
              <dt>Steps / sec</dt>
              <dd className="mono">{formatStepRate(diag?.["advance.wall"])}</dd>
            </div>
          </dl>
          <p className="muted perf-hint">Means from DiagnosticsHub · same session as stats</p>
        </>
      );
    }
    if (id === "world") {
      return (
        <>
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
            <button type="button" className="btn" onClick={() => void copySeed()} title="Copy seed to clipboard">
              {seedCopied ? "Copied" : "Copy"}
            </button>
            <button type="button" className="btn" disabled={busy} onClick={requestNewWorld}>
              Reset world
            </button>
          </div>
          <div className="world-actions">
            <label className="field advance-n">
              <span>Steps</span>
              <input
                value={advanceCount}
                disabled={busy}
                inputMode="numeric"
                onChange={(e) => setAdvanceCount(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    void onAdvanceMany();
                  }
                }}
              />
            </label>
            <button type="button" className="btn" disabled={busy} onClick={() => void onAdvanceMany()} title="Advance N steps">
              Advance ×N
            </button>
          </div>
          <p className={`status-chip${busy ? " is-busy" : ""}`} aria-live="polite">
            {statusText}
          </p>
        </>
      );
    }
    if (id === "inspect") {
      return status?.inspect ? (
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
      );
    }
    if (id === "legend") {
      return (
        <ul className="legend-list">
          {(status?.legend ?? []).map((row) => (
            <li key={`${row.rgb}-${row.label}`}>
              <span className="swatch" style={{ background: rgbCss(row.rgb) }} />
              {row.label}
            </li>
          ))}
        </ul>
      );
    }
    return null;
  }

  function renderPanel(descriptor: PanelDescriptor) {
    return (
      <Panel
        key={descriptor.id}
        descriptor={descriptor}
        open={panelOpen[descriptor.id] ?? descriptor.defaultOpen}
        onToggle={togglePanel}
      >
        {panelBody(descriptor.id)}
      </Panel>
    );
  }

  return (
    <div className="studio" style={shellVars}>
      <MenuBar
        onAction={onMenuAction}
        checked={{ "view.leftRail": leftRailOpen, "view.rightRail": dockOpen }}
      />

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
            className={`btn${leftRailOpen ? " is-pressed" : ""}`}
            aria-pressed={leftRailOpen}
            onClick={() => setLeftRailOpen((o) => !o)}
            title="Toggle Perf rail (P)"
          >
            Perf
          </button>
          <button
            type="button"
            className={`btn${dockOpen ? " is-pressed" : ""}`}
            aria-pressed={dockOpen}
            onClick={() => setDockOpen((o) => !o)}
            title="Toggle World rail (D)"
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
        <div className="studio-work">
          {leftRailOpen ? (
            <>
              <aside className="side-rail perf-rail" aria-label="Performance">
                {panelsFor("left").map(renderPanel)}
              </aside>
              <div
                className="rail-splitter"
                data-splitter="leftRail"
                role="separator"
                aria-orientation="vertical"
                aria-label="Resize Perf rail"
                onPointerDown={(e) => beginResize("leftRail", e)}
              />
            </>
          ) : null}

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
            <>
              <div
                className="rail-splitter"
                data-splitter="rightRail"
                role="separator"
                aria-orientation="vertical"
                aria-label="Resize World rail"
                onPointerDown={(e) => beginResize("rightRail", e)}
              />
              <aside className="side-rail world-rail" aria-label="World">
                {panelsFor("right").map(renderPanel)}
              </aside>
            </>
          ) : null}
        </div>

        <div
          className="rail-splitter is-horizontal"
          data-splitter="terminal"
          role="separator"
          aria-orientation="horizontal"
          aria-label="Resize terminal"
          onPointerDown={(e) => beginResize("terminal", e)}
        />

        <Terminal ref={terminalRef} onRun={onTerminalRun} />
      </div>

      {shortcutsOpen ? <ShortcutsOverlay onClose={() => setShortcutsOpen(false)} /> : null}

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
