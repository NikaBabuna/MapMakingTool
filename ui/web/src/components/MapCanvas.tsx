"use client";

/*
 * File: ui/web/src/components/MapCanvas.tsx
 * Purpose: Paint host raster 1:1 and forward click cells
 * Audience: MapTool
 * Update when: Display scaling or click mapping changes
 */

import { useEffect, useRef } from "react";
import { decodePackedRaster } from "@/lib/raster";

type Props = {
  buffer: ArrayBuffer | null;
  busy: boolean;
  onCell: (x: number, y: number) => void;
};

export function MapCanvas({ buffer, busy, onCell }: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const sizeRef = useRef({ w: 512, h: 512 });

  useEffect(() => {
    if (!buffer || !canvasRef.current) {
      return;
    }
    const { width, height, image } = decodePackedRaster(buffer);
    sizeRef.current = { w: width, h: height };
    const canvas = canvasRef.current;
    if (canvas.width !== width || canvas.height !== height) {
      canvas.width = width;
      canvas.height = height;
    }
    const ctx = canvas.getContext("2d");
    if (!ctx) {
      return;
    }
    ctx.putImageData(image, 0, 0);
  }, [buffer]);

  return (
    <div className={`map-stage${busy ? " is-busy" : ""}`}>
      <canvas
        ref={canvasRef}
        className="map-canvas"
        width={512}
        height={512}
        onClick={(e) => {
          const canvas = canvasRef.current;
          if (!canvas) {
            return;
          }
          const rect = canvas.getBoundingClientRect();
          const x = Math.floor(((e.clientX - rect.left) / rect.width) * sizeRef.current.w);
          const y = Math.floor(((e.clientY - rect.top) / rect.height) * sizeRef.current.h);
          if (x < 0 || y < 0 || x >= sizeRef.current.w || y >= sizeRef.current.h) {
            return;
          }
          onCell(x, y);
        }}
      />
      <div className="map-vignette" aria-hidden />
    </div>
  );
}
