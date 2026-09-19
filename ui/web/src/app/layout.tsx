import type { Metadata } from "next";
import { Fraunces, Sora } from "next/font/google";
import "./globals.css";

const display = Fraunces({
  subsets: ["latin"],
  variable: "--font-display-loaded",
});

const ui = Sora({
  subsets: ["latin"],
  variable: "--font-ui-loaded",
});

export const metadata: Metadata = {
  title: "Aethelgard",
  description: "Living map tool — Next front over the Java MapHost",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className={`${display.variable} ${ui.variable}`}>
        <main>{children}</main>
      </body>
    </html>
  );
}
