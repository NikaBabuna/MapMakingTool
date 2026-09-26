/*
 * File: ui/web/src/test/setup.ts
 * Purpose: Gives the simulated browser the few APIs jsdom lacks (canvas drawing, ImageData, ResizeObserver) and resets state between tests
 * Audience: Agents / CI
 * Update when: A component starts using a browser API jsdom does not provide
 */

import { cleanup } from "@testing-library/react";
import { afterEach, vi } from "vitest";

// Canvas: jsdom has no 2D context. Drawing calls are accepted and do nothing.
const noop = () => undefined;
const context2d = new Proxy(
  {},
  {
    get: (_target, prop) => (prop === "canvas" ? undefined : noop),
    set: () => true,
  },
);
Object.defineProperty(HTMLCanvasElement.prototype, "getContext", {
  configurable: true,
  value: () => context2d,
});

// ImageData: the pixel container the raster decoder fills.
if (typeof globalThis.ImageData === "undefined") {
  class TestImageData {
    readonly width: number;
    readonly height: number;
    readonly data: Uint8ClampedArray;
    constructor(width: number, height: number) {
      this.width = width;
      this.height = height;
      this.data = new Uint8ClampedArray(width * height * 4);
    }
  }
  (globalThis as unknown as { ImageData: unknown }).ImageData = TestImageData;
}

// ResizeObserver: the map stage observes its own size.
if (typeof globalThis.ResizeObserver === "undefined") {
  class TestResizeObserver {
    observe = noop;
    unobserve = noop;
    disconnect = noop;
  }
  (globalThis as unknown as { ResizeObserver: unknown }).ResizeObserver = TestResizeObserver;
}

afterEach(() => {
  cleanup();
  window.localStorage.clear();
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
});
