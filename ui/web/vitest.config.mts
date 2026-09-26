/*
 * File: ui/web/vitest.config.mts
 * Purpose: Runs the web front's tests in a simulated browser (jsdom) with the app's path alias
 * Audience: Agents / CI
 * Update when: The test runner, its environment, or the source layout changes
 */

import { fileURLToPath } from "node:url";
import { defineConfig } from "vitest/config";

export default defineConfig({
  oxc: {
    jsx: { runtime: "automatic" },
  },
  resolve: {
    alias: { "@": fileURLToPath(new URL("./src", import.meta.url)) },
  },
  test: {
    environment: "jsdom",
    include: ["src/**/*.test.{ts,tsx}"],
    setupFiles: ["src/test/setup.ts"],
  },
});
