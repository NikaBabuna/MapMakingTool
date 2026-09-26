<!--
  File: docs/architecture/studio/web/client.md
  Purpose: host.ts, raster.ts, next.config.ts — the page's typed calls to the host, and the decoding of the packed raster
  Audience: Agents and humans
  Update when: A host route, the status shape, the raster wire format, the host address rule, or the Next.js configuration changes
-->

# Client

The page talks to the host through a small set of functions, one per route, each returning the host's answer in a typed shape. The map arrives as raw bytes, and one function turns them into an image the canvas can draw.

## What it reads

The host address: `NEXT_PUBLIC_MAP_HOST` when it is set and not blank (a trailing `/` removed), otherwise `DEFAULT_HOST`, `http://127.0.0.1:7420`. The host's responses.

## What it writes

HTTP requests. Typed values: `HostStatus` (with `Inspect`, `LegendRow`, and `HostDiag` of `DiagSample`s), `CommandResult`, and the raster as an `ArrayBuffer`, decoded into an `ImageData`. A response that is not 2xx makes `readJson` throw an `Error` with the response text (or `HTTP <code>`), except for commands, whose JSON is parsed whatever the code. `decodePackedRaster` throws on a body shorter than 8 bytes, or shorter than its header promises, or with a non-positive side.

## Model

**Routes.**

| Function | Request | Answer |
|----------|---------|--------|
| `fetchHealth` | `GET /health` | `true` exactly when the status is 2xx and the body is `ok`; `false` on any failure |
| `fetchStatus` | `GET /api/status` (no cache) | `HostStatus` |
| `fetchRaster` | `GET /api/raster` (no cache) | `ArrayBuffer` |
| `postAdvance` | `POST /api/advance` | `HostStatus` |
| `postLayer(layer)` | `POST /api/layer?layer=<label>` | `HostStatus` |
| `postSpeed(speed)` | `POST /api/speed?speed=<label>` | `HostStatus` |
| `postNewWorld(seed)` | `POST /api/new-world?seed=<n>` | `HostStatus` |
| `postRestartEngine` | `POST /api/restart-engine` | `HostStatus` |
| `postInspect(x, y)` | `POST /api/inspect?x=<x>&y=<y>` | `HostStatus` |
| `postCommand(line)` | `POST /api/command`, body the line as `text/plain` | `CommandResult` |

Layer and speed travel as their labels, `MapLayerName` (`"Elevation"`, `"Plates"`, `"Overlay"`) and `MapSpeedName` (`"1x"`, `"2x"`, `"4x"`, `"Fastest"`). `SPEED_MS` maps each speed to the same periods the host uses: 250, 125, 62, and 1 ms.

**Decoding.** The body is read as big-endian signed 32-bit integers $W, H, x_0, \dots, x_{WH-1}$ ([http](../http.md)), and each $x_i$ becomes four bytes of the image, red, green, blue, and opaque alpha:

$$\bigl(\mathrm{red}, \mathrm{green}, \mathrm{blue}, \mathrm{alpha}\bigr)_i = \bigl((x_i \gg 16) \mathbin{\&} 255,\;\; (x_i \gg 8) \mathbin{\&} 255,\;\; x_i \mathbin{\&} 255,\;\; 255\bigr).$$

`decodePackedRaster` (the pixel loop) in [`raster.ts`](../../../../ui/web/src/lib/raster.ts):

```ts
for (let i = 0; i < width * height; i++) {
  const rgb = view.getInt32(o, false);
  o += 4;
  image.data[p++] = (rgb >> 16) & 0xff;
  image.data[p++] = (rgb >> 8) & 0xff;
  image.data[p++] = rgb & 0xff;
  image.data[p++] = 255;
}
```

## Procedure

1. `hostBase` resolves the host address. Every request function takes it as a default argument. [`hostBase`](../../../../ui/web/src/lib/host.ts).
2. Each request function builds its URL, sends the request, and parses the answer through `readJson`, except `fetchRaster`, which returns the bytes, `postCommand`, which parses JSON directly, and `fetchHealth`, which compares the text. [`fetchStatus`](../../../../ui/web/src/lib/host.ts), [`postCommand`](../../../../ui/web/src/lib/host.ts).
3. `decodePackedRaster` reads the header, checks the length, allocates an `ImageData` of $W \times H$, and fills it. [`decodePackedRaster`](../../../../ui/web/src/lib/raster.ts).
4. `rgbCss` turns a packed colour into the CSS text `rgb(r, g, b)` for legend swatches. [`rgbCss`](../../../../ui/web/src/lib/raster.ts).
5. The Next.js configuration turns on React strict mode. [`next.config.ts`](../../../../ui/web/next.config.ts).

## What is true afterwards

The page sees exactly the status the host sent, in typed form, and a decoded image whose pixels equal the host's raster. Requests never time out on their own: a host that does not answer leaves the request pending.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Host calls | `HostStatus` | `DEFAULT_HOST`, `MapLayerName`, `MapSpeedName`, `SPEED_MS`, `Inspect`, `LegendRow`, `DiagSample`, `HostDiag`, `HostStatus`, `hostBase`, `readJson`, `fetchStatus`, `fetchRaster`, `postAdvance`, `postLayer`, `postSpeed`, `postNewWorld`, `postRestartEngine`, `postInspect`, `CommandResult`, `postCommand`, `fetchHealth` | [`ui/web/src/lib/host.ts`](../../../../ui/web/src/lib/host.ts) |
| Raster decode | — | `decodePackedRaster`, `rgbCss` | [`ui/web/src/lib/raster.ts`](../../../../ui/web/src/lib/raster.ts) |
| Next.js configuration | — | `nextConfig` | [`ui/web/next.config.ts`](../../../../ui/web/next.config.ts) |

Parent: [web front](README.md). The host it calls: [http](../http.md).
