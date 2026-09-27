<!--
  File: ui/src/test/java/com/aethelgard/ui/http/README.md
  Purpose: Door to the tests of the studio's HTTP host: status and raster answers, actions, refusals, preflight, and the raster cache
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# HTTP host tests

Proves what the studio's HTTP host answers, by sending real requests to a host on a free port.

**Paper:** [http](../../../../../../../../docs/architecture/studio/http.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

The routes are only worth proving through the network, so these tests start a real `MapHost` on the loopback address and call it with the JDK's HTTP client, as the page does. They sit in the host's own package so they can read its test side, `MapHost.packedBodyAllocations` and `MapHost.cachedPackedBody`. What the controller does behind a route is proved by the [controller tests](../controller/README.md).

## How it works

Each test builds a `MapController` on an 8 by 8 `WorldSpec`, seed 0, with `PlayScheduler.idle`, and wraps it with `MapHost.start` on port 0. Steps run on the caller, or on an `ArrayDeque` of tasks when a test needs to see an advance still in flight. The helpers `get`, `post`, and `raster` send the requests, and the tests read status codes, headers, and the text of the status JSON. `rasterMatchesTheStatus` unpacks the body with a `ByteBuffer` and compares it with the controller's own `ElevationRaster`.

**Start reading at:** `MapHostTest.statusDescribesTheWorld` in [MapHostTest.java](MapHostTest.java).

## Depends on

- [http](../../../../../../main/java/com/aethelgard/ui/http/README.md) — `MapHost`, the code under test
- [controller](../../../../../../main/java/com/aethelgard/ui/controller/README.md) — `MapController` and `PlayScheduler`, the controller each host wraps
- [raster](../../../../../../main/java/com/aethelgard/ui/raster/README.md) — `ElevationRaster` and `MapLayer`, to compare the raster body
- [product world fields](../../../../../../../../product/src/main/java/com/aethelgard/product/world/fields/README.md) — `WorldSpec`

## Used by

- nothing in this repository — the witness command runs these tests

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [MapHostTest.java](MapHostTest.java) | Proves the host's status and raster answers, its actions and refusals, the command route, preflight, and the raster cache | `MapHostTest` |
