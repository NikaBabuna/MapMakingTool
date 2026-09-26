<!--
  File: ui/web/README.md
  Purpose: Door to the web front, the studio's page
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# Web front

The web front is the studio's page: it shows the map and drives the world through the local map host.

**Docs:** [web front](../../docs/architecture/studio/web/README.md) · [processes](../../docs/architecture/program.md)  
**Look:** [style guide](../../docs/product/style-guide.md)

| Path | Read it when |
|------|----------------|
| `src/` | You need the page's code, `src/app/`, `src/components/`, and `src/lib/`, or its tests, `*.test.ts` and `*.test.tsx` beside the code they test, with their stand-in host in `src/test/` |
| [package.json](package.json) | You need the page's packages or its scripts: `dev`, `build`, `start`, `lint`, and `test` |
| [package-lock.json](package-lock.json) | You need the exact version of one package; search it for the name |
| [next.config.ts](next.config.ts) | You need the Next.js settings |
| [tsconfig.json](tsconfig.json) | You need the TypeScript settings or the `@` path alias |
| [next-env.d.ts](next-env.d.ts) | You need the types Next.js generates for the page |
| [vitest.config.mts](vitest.config.mts) | You need how the tests run: the simulated browser, the alias, and the setup file |
| [.env.example](.env.example) | You need to point the page at a map host other than `127.0.0.1:7420` |
