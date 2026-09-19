"use client";

/*
 * File: ui/web/src/components/ShortcutsOverlay.tsx
 * Purpose: Keyboard shortcut list dialog (F-053 QoL)
 * Audience: MapTool
 * Update when: SHORTCUTS or dialog chrome changes
 */

import { SHORTCUTS } from "@/lib/shortcuts";

type ShortcutsOverlayProps = {
  onClose: () => void;
};

export function ShortcutsOverlay({ onClose }: ShortcutsOverlayProps) {
  return (
    <div className="shortcuts-backdrop" role="presentation" onClick={onClose}>
      <div
        className="shortcuts-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="shortcuts-title"
        onClick={(e) => e.stopPropagation()}
      >
        <header className="shortcuts-head">
          <h2 id="shortcuts-title">Shortcuts</h2>
          <button type="button" className="btn" onClick={onClose}>
            Close
          </button>
        </header>
        <dl className="shortcuts-grid">
          {SHORTCUTS.map((row) => (
            <div key={row.keys}>
              <dt className="mono">{row.keys}</dt>
              <dd>{row.action}</dd>
            </div>
          ))}
        </dl>
      </div>
    </div>
  );
}
