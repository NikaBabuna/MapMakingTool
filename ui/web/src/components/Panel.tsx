"use client";

/*
 * File: ui/web/src/components/Panel.tsx
 * Purpose: Generic rail panel chrome driven by a PanelDescriptor
 * Audience: MapTool
 * Update when: Panel chrome or collapse behavior changes
 */

import type { ReactNode } from "react";
import type { PanelDescriptor } from "@/lib/panels";

type PanelProps = {
  descriptor: PanelDescriptor;
  open: boolean;
  onToggle: (id: string) => void;
  children: ReactNode;
};

export function Panel({ descriptor, open, onToggle, children }: PanelProps) {
  const body = descriptor.collapsible ? open : true;
  return (
    <article className="studio-panel" data-panel={descriptor.id}>
      <header className="panel-chrome">
        <h2>{descriptor.title}</h2>
        {descriptor.collapsible ? (
          <button
            type="button"
            className="panel-toggle"
            aria-expanded={open}
            onClick={() => onToggle(descriptor.id)}
            title={open ? `Collapse ${descriptor.title}` : `Expand ${descriptor.title}`}
          >
            {open ? "−" : "+"}
          </button>
        ) : null}
      </header>
      {body ? <div className="panel-body">{children}</div> : null}
    </article>
  );
}
