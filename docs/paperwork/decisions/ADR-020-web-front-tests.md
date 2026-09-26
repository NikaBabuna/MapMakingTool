<!--
  File: docs/paperwork/decisions/ADR-020-web-front-tests.md
  Purpose: Decision record ADR-020
  Audience: Agents and humans
  Update when: A later ADR amends or supersedes this one
-->

# ADR-020 — Web front tests with Vitest

**Date:** 2026-09-26
**Status:** accepted

The web front has its own tests, and the one witness command runs them with the Java tests.

| Choice | What is now true |
|--------|------------------|
| Runner | Vitest, in a simulated browser (jsdom), with Testing Library to render components and press their keys and buttons. They are development packages of `ui/web` only; the page does not ship them |
| Where | Each test file sits beside the module it tests, as `<name>.test.ts` or `<name>.test.tsx` under `ui/web/src/`. A stand-in for the map host answers the page's requests in memory, so no test needs a running host |
| Witness | The `ui` module's test phase runs `npm test` in `ui/web`, so `./mvnw test` runs every test of the project. `-DskipTests` skips the web tests with the others |
| CI | The workflow sets up Node 22 and runs `npm ci` in `ui/web` before the witness command |

**Why:** The page holds behaviour a person relies on: the keys, the camera, the terminal's history, what happens when the host is gone, and which answer is applied when two arrive out of order. The only checks of it were Java tests that searched its source text for words, which pass whatever the page does. A test that renders the page and presses its keys fails when the behaviour breaks. Running it from the Maven witness keeps one command and one bar for the whole project.

**Goal:** [G-011 Docs restructuring](../goals/G-011-docs-restructuring.md)
