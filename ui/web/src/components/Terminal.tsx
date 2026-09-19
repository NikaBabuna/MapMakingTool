"use client";

/*
 * File: ui/web/src/components/Terminal.tsx
 * Purpose: In-app terminal drawer on shared CommandDispatch (F-050)
 * Audience: MapTool
 * Update when: Terminal UX or transcript shape changes
 */

import { useEffect, useRef, useState, type KeyboardEvent } from "react";
import type { CommandResult } from "@/lib/host";

const HISTORY_CAP = 32;
const LOG_CAP = 40;

const EMPTY_HINT =
  "Noun/verb: help · session get · list pool · session advance [N] · diag get";

export type TerminalEntry = {
  kind: "command" | "output" | "error";
  text: string;
};

type TerminalProps = {
  open: boolean;
  /** Runs one dispatcher line; MapTool refreshes map/status from the result. */
  onRun: (line: string) => Promise<CommandResult>;
};

export function Terminal({ open, onRun }: TerminalProps) {
  const [line, setLine] = useState("");
  const [log, setLog] = useState<TerminalEntry[]>([]);
  const [history, setHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState(-1);
  const inputRef = useRef<HTMLInputElement>(null);
  const logRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (open) {
      inputRef.current?.focus();
    }
  }, [open]);

  useEffect(() => {
    const el = logRef.current;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  }, [log]);

  async function run() {
    const trimmed = line.trim();
    if (!trimmed) {
      return;
    }
    try {
      const result = await onRun(trimmed);
      const output = result.output || `(exit ${result.exitCode})`;
      const entries: TerminalEntry[] = [
        { kind: "command", text: `aethelgard> ${trimmed}` },
        {
          kind: result.ok ? "output" : "error",
          text: result.ok
            ? output
            : output.includes(`exit ${result.exitCode}`)
              ? output
              : `${output}\n(exit ${result.exitCode})`,
        },
      ];
      setLog((prev) => [...prev, ...entries].slice(-LOG_CAP));
      setHistory((prev) => {
        if (prev[0] === trimmed) {
          return prev;
        }
        return [trimmed, ...prev].slice(0, HISTORY_CAP);
      });
      setHistoryIndex(-1);
      setLine("");
    } catch (err) {
      const message = err instanceof Error ? err.message : String(err);
      setLog((prev) =>
        [
          ...prev,
          { kind: "command", text: `aethelgard> ${trimmed}` },
          { kind: "error", text: `error: ${message}` },
        ].slice(-LOG_CAP),
      );
    }
  }

  function onKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter") {
      e.preventDefault();
      void run();
      return;
    }
    if (e.key === "ArrowUp") {
      e.preventDefault();
      if (history.length === 0) {
        return;
      }
      const next = Math.min(historyIndex + 1, history.length - 1);
      setHistoryIndex(next);
      setLine(history[next] ?? "");
      return;
    }
    if (e.key === "ArrowDown") {
      e.preventDefault();
      if (historyIndex <= 0) {
        setHistoryIndex(-1);
        setLine("");
        return;
      }
      const next = historyIndex - 1;
      setHistoryIndex(next);
      setLine(history[next] ?? "");
    }
  }

  return (
    <div
      className="console-drawer terminal"
      hidden={!open}
      role="region"
      aria-label="Terminal"
    >
      <div className="terminal-titlebar">
        <span className="terminal-title">Terminal</span>
        <span className="terminal-hint">↑↓ history · Enter run · help</span>
      </div>
      <div className="console-log terminal-log" ref={logRef}>
        {log.length === 0 ? (
          <p className="terminal-empty">{EMPTY_HINT}</p>
        ) : (
          log.map((entry, i) => (
            <div key={`${entry.kind}-${i}-${entry.text.slice(0, 24)}`} className={`terminal-line is-${entry.kind}`}>
              {entry.text}
            </div>
          ))
        )}
      </div>
      <div className="console-row terminal-input-row">
        <span className="terminal-prompt" aria-hidden>
          aethelgard&gt;
        </span>
        <input
          ref={inputRef}
          className="terminal-input"
          value={line}
          placeholder="help"
          spellCheck={false}
          autoComplete="off"
          onChange={(e) => {
            setLine(e.target.value);
            setHistoryIndex(-1);
          }}
          onKeyDown={onKeyDown}
        />
        <button type="button" className="btn terminal-run" onClick={() => void run()}>
          Run
        </button>
      </div>
    </div>
  );
}
