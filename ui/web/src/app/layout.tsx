/*
 * File: ui/web/src/app/layout.tsx
 * Purpose: The page's root layout: its fonts, its title, and the global styles
 * Audience: Next.js; agents changing the page's frame
 * Update when: The page's fonts, title, or global stylesheet change
 */

import type { Metadata } from "next";
import { IBM_Plex_Mono, IBM_Plex_Sans } from "next/font/google";
import "./globals.css";

const ui = IBM_Plex_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "600"],
  variable: "--font-ui-loaded",
});

const mono = IBM_Plex_Mono({
  subsets: ["latin"],
  weight: ["400", "500"],
  variable: "--font-mono-loaded",
});

export const metadata: Metadata = {
  title: "Aethelgard",
  description: "Studio cartography tool — Next front over the Java MapHost",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className={`${ui.variable} ${mono.variable}`}>
        {children}
      </body>
    </html>
  );
}
