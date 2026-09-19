"use client";

/*
 * File: ui/web/src/components/MapCanvas.tsx
 * Purpose: Paint host raster with pan/zoom; forward click cells
 * Audience: MapTool
 * Update when: Display scaling or viewport interaction changes
 */

import { useEffect, useRef, useState } from "react";
import { decodePackedRaster } from "@/lib/raster";
import {
  IDENTITY_VIEWPORT,
  Viewport,
  cssTransform,
  panBy,
  stageToCell,
  zoomAt,
} from "@/lib/viewport";

type Props = {
  buffer: ArrayBuffer | null;
  busy: boolean;
  viewport: Viewport;
  onViewportChange: (next: Viewport) => void;
  onCell: (x: number, y: number) => void;
};

export function MapCanvas({ buffer, busy, viewport, onViewportChange, onCell }: Props) {
  const stageRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const sizeRef = useRef({ w: 512, h: 512, dw: 512, dh: 512 });
  const dragRef = useRef<{ x: number; y: number; moved: boolean } | null>(null);
  const [panning, setPanning] = useState(false);
  const viewportRef = useRef(viewport);
  viewportRef.current = viewport;

  useEffect(() => {
    if (!buffer || !canvasRef.current) {
      return;
    }
    const { width, height, image } = decodePackedRaster(buffer);
    sizeRef.current.w = width;
    sizeRef.current.h = height;
    const canvas = canvasRef.current;
    if (canvas.width !== width || canvas.height !== height) {
      canvas.width = width;
      canvas.height = height;
    }
    sizeRef.current.dw = width;
    sizeRef.current.dh = height;
    const ctx = canvas.getContext("2d");
    if (!ctx) {
      return;
    }
    ctx.putImageData(image, 0, 0);
  }, [buffer]);

  function stagePoint(clientX: number, clientY: number): { x: number; y: number } | null {
    const stage = stageRef.current;
    if (!stage) {
      return null;
    }
    const rect = stage.getBoundingClientRect();
    return { x: clientX - rect.left, y: clientY - rect.top };
  }

  return (
    <div
      ref={stageRef}
      className={`map-stage${busy ? " is-busy" : ""}${panning ? " is-panning" : ""}`}
      onWheel={(e) => {
        e.preventDefault();
        const pt = stagePoint(e.clientX, e.clientY);
        if (!pt) {
          return;
        }
        const factor = e.deltaY < 0 ? 1.12 : 1 / 1.12;
        onViewportChange(zoomAt(viewportRef.current, pt.x, pt.y, factor));
      }}
      onPointerDown={(e) => {
        if (e.button !== 0) {
          return;
        }
        (e.currentTarget as HTMLDivElement).setPointerCapture(e.pointerId);
        dragRef.current = { x: e.clientX, y: e.clientY, moved: false };
        setPanning(true);
      }}
      onPointerMove={(e) => {
        if (!dragRef.current) {
          return;
        }
        const dx = e.clientX - dragRef.current.x;
        const dy = e.clientY - dragRef.current.y;
        if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
          dragRef.current.moved = true;
        }
        dragRef.current.x = e.clientX;
        dragRef.current.y = e.clientY;
        if (dragRef.current.moved) {
          onViewportChange(panBy(viewportRef.current, dx, dy));
        }
      }}
      onPointerUp={(e) => {
        const wasDrag = dragRef.current?.moved ?? false;
        dragRef.current = null;
        setPanning(false);
        try {
          (e.currentTarget as HTMLDivElement).releasePointerCapture(e.pointerId);
        } catch {
          /* already released */
        }
        if (wasDrag) {
          return;
        }
        const pt = stagePoint(e.clientX, e.clientY);
        if (!pt) {
          return;
        }
        const cell = stageToCell(
          viewportRef.current,
          pt.x,
          pt.y,
          sizeRef.current.w,
          sizeRef.current.h,
          sizeRef.current.dw,
          sizeRef.current.dh,
        );
        if (cell) {
          onCell(cell.x, cell.y);
        }
      }}
      onPointerCancel={() => {
        dragRef.current = null;
        setPanning(false);
      }}
    >
      <div className="map-viewport" style={{ transform: cssTransform(viewport) }}>
        <canvas ref={canvasRef} className="map-canvas" width={512} height={512} />
      </div>
      <div className="map-busy" aria-hidden={!busy}>
        Working…
      </div>
    </div>
  );
}

export function resetViewport(): Viewport {
  return IDENTITY_VIEWPORT;
}
