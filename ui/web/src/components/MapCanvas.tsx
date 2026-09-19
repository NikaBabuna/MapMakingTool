"use client";

/*
 * File: ui/web/src/components/MapCanvas.tsx
 * Purpose: Paint host raster with toroidal pan/zoom; forward click cells
 * Audience: MapTool
 * Update when: Display scaling or viewport interaction changes
 */

import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { decodePackedRaster } from "@/lib/raster";
import {
  Viewport,
  fitScale,
  fittedViewport,
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
  onStageMetrics?: (metrics: { stageW: number; stageH: number; displayW: number; displayH: number }) => void;
};

export function MapCanvas({
  buffer,
  busy,
  viewport,
  onViewportChange,
  onCell,
  onStageMetrics,
}: Props) {
  const stageRef = useRef<HTMLDivElement>(null);
  const sourceRef = useRef<HTMLCanvasElement>(null);
  const viewRef = useRef<HTMLCanvasElement>(null);
  const sizeRef = useRef({ w: 1920, h: 1080, dw: 1920, dh: 1080 });
  const stageSizeRef = useRef({ w: 1, h: 1 });
  const dragRef = useRef<{ x: number; y: number; moved: boolean } | null>(null);
  const [panning, setPanning] = useState(false);
  const viewportRef = useRef(viewport);
  viewportRef.current = viewport;

  const metricsCbRef = useRef(onStageMetrics);
  metricsCbRef.current = onStageMetrics;

  function paint() {
    const source = sourceRef.current;
    const view = viewRef.current;
    const stage = stageRef.current;
    if (!source || !view || !stage) {
      return;
    }
    const stageW = stage.clientWidth;
    const stageH = stage.clientHeight;
    if (stageW <= 0 || stageH <= 0) {
      return;
    }
    if (view.width !== stageW || view.height !== stageH) {
      view.width = stageW;
      view.height = stageH;
    }
    const ctx = view.getContext("2d");
    if (!ctx) {
      return;
    }
    const { dw, dh } = sizeRef.current;
    const { scale, tx, ty } = viewportRef.current;
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, stageW, stageH);
    ctx.imageSmoothingEnabled = false;
    ctx.setTransform(scale, 0, 0, scale, tx, ty);
    // Horizontal loop tiles only; N/S of the map band stay blank (cylinder / polar edge)
    for (let i = -1; i <= 1; i++) {
      ctx.drawImage(source, i * dw, 0);
    }
  }

  function reportMetrics() {
    const { dw, dh } = sizeRef.current;
    metricsCbRef.current?.({
      stageW: stageSizeRef.current.w,
      stageH: stageSizeRef.current.h,
      displayW: dw,
      displayH: dh,
    });
  }

  useEffect(() => {
    if (!buffer || !sourceRef.current) {
      return;
    }
    const { width, height, image } = decodePackedRaster(buffer);
    sizeRef.current.w = width;
    sizeRef.current.h = height;
    sizeRef.current.dw = width;
    sizeRef.current.dh = height;
    const source = sourceRef.current;
    if (source.width !== width || source.height !== height) {
      source.width = width;
      source.height = height;
    }
    const ctx = source.getContext("2d");
    if (!ctx) {
      return;
    }
    ctx.putImageData(image, 0, 0);
    paint();
    reportMetrics();
  }, [buffer]);

  useLayoutEffect(() => {
    const stage = stageRef.current;
    if (!stage) {
      return;
    }
    const sync = () => {
      const stageW = stage.clientWidth;
      const stageH = stage.clientHeight;
      stageSizeRef.current = { w: stageW, h: stageH };
      reportMetrics();
      paint();
    };
    sync();
    const ro = new ResizeObserver(sync);
    ro.observe(stage);
    return () => ro.disconnect();
  }, []);

  useEffect(() => {
    paint();
  }, [viewport]);

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
        const { dw, dh } = sizeRef.current;
        const minScale = fitScale(stageSizeRef.current.w, stageSizeRef.current.h, dw, dh);
        const factor = e.deltaY < 0 ? 1.12 : 1 / 1.12;
        onViewportChange(
          zoomAt(
            viewportRef.current,
            pt.x,
            pt.y,
            factor,
            minScale,
            dw,
            dh,
            stageSizeRef.current.h,
          ),
        );
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
          const { dw, dh } = sizeRef.current;
          const stageH = stageSizeRef.current.h;
          onViewportChange(panBy(viewportRef.current, dx, dy, dw, dh, stageH));
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
      <canvas ref={sourceRef} className="map-source" width={1920} height={1080} aria-hidden />
      <canvas ref={viewRef} className="map-view" />
      <div className="map-busy" aria-hidden={!busy}>
        Working…
      </div>
    </div>
  );
}

export function resetViewport(
  stageW: number,
  stageH: number,
  displayW: number,
  displayH: number,
): Viewport {
  return fittedViewport(stageW, stageH, displayW, displayH);
}
