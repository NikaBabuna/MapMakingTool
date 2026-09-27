/*
 * File: ui/web/src/app/page.tsx
 * Purpose: The studio's one route, which renders the map tool
 * Audience: Next.js
 * Update when: The route renders something other than the map tool
 */

import { MapTool } from "@/components/MapTool";

export default function HomePage() {
  return <MapTool />;
}
