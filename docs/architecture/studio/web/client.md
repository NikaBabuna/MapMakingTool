<!--
  File: docs/architecture/studio/web/client.md
  Purpose: Client — the page's typed calls to the host, and the decoding of the packed raster
  Audience: Agents and humans
  Update when: A host route, the status shape, the raster wire format, the host address rule, or the Next.js configuration changes
-->

# Client

The page talks to the host through a small set of functions, one per route, each returning the host's answer in a typed shape. The map arrives as raw bytes, and one function turns them into an image the canvas can draw.

## What it reads

The host address: `NEXT_PUBLIC_MAP_HOST` when it is set and not blank (a trailing `/` removed), otherwise `DEFAULT_HOST`, `http://127.0.0.1:7420`. The host's responses.

## What it writes

HTTP requests. Typed values: `HostStatus` (with `Inspect`, `LegendRow`, and `HostDiag` of `DiagSample`s), `CommandResult`, and the raster as an `ArrayBuffer`, decoded into an `ImageData`. A response that is not 2xx makes the request throw an `Error` with the response text (or `HTTP <code>`), except for commands, whose JSON is parsed whatever the code. Decoding throws on a body shorter than 8 bytes, or shorter than its header promises, or with a non-positive side.

## Model

**Routes.**

| Request | Answer |
|---------|--------|
| `GET /health` | `true` exactly when the status is 2xx and the body is `ok`; `false` on any failure |
| `GET /api/status` (no cache) | `HostStatus` |
| `GET /api/raster` (no cache) | `ArrayBuffer` |
| `POST /api/advance` | `HostStatus` |
| `POST /api/layer?layer=<label>` | `HostStatus` |
| `POST /api/speed?speed=<label>` | `HostStatus` |
| `POST /api/new-world?seed=<n>` | `HostStatus` |
| `POST /api/restart-engine` | `HostStatus` |
| `POST /api/inspect?x=<x>&y=<y>` | `HostStatus` |
| `POST /api/command`, body the line as `text/plain` | `CommandResult` |

Layer and speed travel as their labels, `MapLayerName` (`"Elevation"`, `"Plates"`, `"Overlay"`) and `MapSpeedName` (`"1x"`, `"2x"`, `"4x"`, `"Fastest"`). `SPEED_MS` maps each speed to the same periods the host uses: 250, 125, 62, and 1 ms.

**Decoding.** The body is read as big-endian signed 32-bit integers $W, H, x_0, \dots, x_{WH-1}$ ([http](../http.md)), and each $x_i$ becomes four bytes of the image, red, green, blue, and opaque alpha:

$$\bigl(\mathrm{red}, \mathrm{green}, \mathrm{blue}, \mathrm{alpha}\bigr)_i = \bigl((x_i \gg 16) \mathbin{\&} 255,\;\; (x_i \gg 8) \mathbin{\&} 255,\;\; x_i \mathbin{\&} 255,\;\; 255\bigr).$$

## Procedure

1. The host address is resolved as What it reads states, and every request takes it as a default argument.
2. Each request builds its URL, sends the request, and parses the answer as JSON, throwing on a status that is not 2xx. The raster request returns the bytes instead, a command parses its JSON whatever the code, and the health check compares the text.
3. Decoding reads the header, checks the length, allocates an `ImageData` of $W \times H$, and fills it.
4. A packed colour is also written as the CSS text `rgb(r, g, b)`, for legend swatches.
5. The Next.js configuration turns on React strict mode.

## What is true afterwards

The page sees exactly the status the host sent, in typed form, and a decoded image whose pixels equal the host's raster. Requests never time out on their own: a host that does not answer leaves the request pending.

Code: [lib/](../../../../ui/web/src/lib/README.md) · [web/](../../../../ui/web/README.md)
Parent: [web front](README.md). The host it calls: [http](../http.md).
