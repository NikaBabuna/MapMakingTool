<!--
  File: ui/web/README.md
  Purpose: Door to the web front, the studio's page
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# Web front

The studio's page: a Next.js app that shows the map and drives the world through the local map host.

**Paper:** [web front](../../docs/architecture/studio/web/README.md) · [client](../../docs/architecture/studio/web/client.md) · [processes](../../docs/architecture/program.md) · **Conventions:** [conventions.md](../../docs/architecture/conventions.md)

## Why

The page is a Next.js app, built and tested by npm rather than by Maven, so it has its own folder with the files Next.js, TypeScript, and Vitest look for at its root. This level holds only that configuration; the code is under `src/`, sorted into [app](src/app/README.md), [components](src/components/README.md), [lib](src/lib/README.md), and [test](src/test/README.md). How the page looks and what its controls say belong to [the style guide](../../docs/product/style-guide.md).

## How it works

`npm run dev` starts the Next.js development server on port 3000, which the desktop shell's `beforeDevCommand` runs; `next.config.ts` turns on React strict mode. Next.js serves the route in [app](src/app/README.md), which renders `MapTool` from [components](src/components/README.md). `tsconfig.json` maps the alias `@` to `src/`. `npm test` runs `vitest run`, which `vitest.config.mts` sets up with jsdom, the same alias, and the setup file in [test](src/test/README.md); the UI module's build runs it in its test phase. The page reads the map host's address from `NEXT_PUBLIC_MAP_HOST`, and `.env.example` shows how to set it.

**Start reading at:** `nextConfig` in [next.config.ts](next.config.ts).

## Depends on

- [map host](../src/main/java/com/aethelgard/ui/http/README.md) — every route, called over HTTP by the host client in `src/lib/host.ts`

## Used by

- [desktop shell](../desktop/src-tauri/README.md) — its `beforeDevCommand` starts this page's server, and its window shows the page
- [the UI module's build](../README.md) — its test phase runs `npm test` here

## Where each step happens

### [Client](../../docs/architecture/studio/web/client.md)

| Step | Member | File |
|------|--------|------|
| 5. The Next.js configuration turns on React strict mode | `nextConfig` | [next.config.ts](next.config.ts) |

The other steps are in [lib](src/lib/README.md).

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [package.json](package.json) | The page's npm package: its dependencies, and the scripts `dev`, `build`, `start`, `lint`, and `test` | — |
| [package-lock.json](package-lock.json) | The exact version of every npm package, written by npm; search it for one package's name | — |
| [next.config.ts](next.config.ts) | The Next.js settings of the web front | `nextConfig` |
| [next-env.d.ts](next-env.d.ts) | The types Next.js generates for the page, written by Next.js | — |
| [tsconfig.json](tsconfig.json) | The TypeScript settings, and the `@` path alias | — |
| `tsconfig.tsbuildinfo` | TypeScript's incremental build record, written by the compiler and not committed | — |
| [vitest.config.mts](vitest.config.mts) | Runs the web front's tests in a simulated browser (jsdom) with the app's path alias | — |
| [.env.example](.env.example) | Shows the one setting the page reads from its environment: the map host's address | — |
| `src/` | exempt: layout segment — the page's code, in [app](src/app/README.md), [components](src/components/README.md), [lib](src/lib/README.md), and [test](src/test/README.md) | — |
| `.next/` | exempt: build output — Next.js writes it on every build | — |
| `node_modules/` | exempt: dependency install — the installed npm packages | — |
