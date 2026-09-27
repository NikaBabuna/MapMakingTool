<!--
  File: docs/architecture/studio/http.md
  Purpose: HTTP host — the local HTTP host: routes, status JSON, packed raster body, threads, and the process entry
  Audience: Agents and humans
  Update when: A route, the status JSON, the packed body, the raster cache, or the process entry changes
-->

# HTTP host

The web page cannot call Java directly, so the Java process listens on a local port and answers a handful of requests. Some read the state, one returns the painted map as raw bytes, and the rest pass a person's action to the controller. It listens only on the loopback address, so nothing outside the machine can reach it.

## What it reads

HTTP requests on `127.0.0.1`: the method, the path, query parameters, and for some routes a text body. From the controller: its status, raster, legend, inspected cell, and the session's diagnostics.

## What it writes

HTTP responses, and calls into the [controller](controller.md). Refusals: a wrong method answers 405 `method not allowed`; a missing or unparsable parameter answers 400 `error: …`; a refused command answers 400 with the command's result. A failure the handler does not catch, for example inspecting a cell outside the map, leaves the request without a response from the handler.

## Model

**Routes.** Every response carries `Access-Control-Allow-Origin: *`. An `OPTIONS` request to any route is a CORS preflight, answered 204 with `Access-Control-Allow-Methods: GET, POST, OPTIONS` and `Access-Control-Allow-Headers: Content-Type`.

| Method, path | Input | Effect | Answer |
|--------------|-------|--------|--------|
| `GET /health` | — | — | `ok` |
| `GET /api/status` | — | — | Status JSON |
| `GET /api/raster` | — | — | Packed raster; headers `X-Width`, `X-Height`; type `application/octet-stream` |
| `POST /api/advance` | — | A background step | Status JSON |
| `POST /api/play`, `POST /api/pause` | — | Play, pause | Status JSON |
| `POST /api/layer` | `?layer=` or body | Set the layer | Status JSON, or 400 `error: unknown layer` |
| `POST /api/speed` | `?speed=` or body | Set the speed | Status JSON, or 400 `error: unknown speed` |
| `POST /api/new-world` | `?seed=` | New world (ignored while busy) | Status JSON, or 400 `error: seed required`, `error: bad seed` |
| `POST /api/restart-engine` | — | Restart (also while busy) | Status JSON |
| `POST /api/inspect` | `?x=`, `?y=` | Inspect the cell | Status JSON, or 400 `error: x and y required`, `error: bad coordinates` |
| `POST /api/command` | body: one line | Run the line | `{"exitCode":n,"ok":b,"output":s,"status":{…}}`; 200 if ok, else 400 |

A layer or speed matches its constant name or its label, ignoring case: `"overlay"` and `"Overlay"` both select `OVERLAY`.

**Status JSON.** Its shape, with placeholders:

```text
{"step":k,"seed":s,"width":W,"height":H,"layer":"Elevation","speed":"1x",
 "playing":false,"busy":false,"statusText":"Step k",
 "inspect":null | {"x":…,"y":…,"elevation":…,"plateId":…,"vx":…,"vy":…},
 "legend":[{"rgb":n,"label":"…"},…],
 "diag":{"<collector id>":{"last":n|null,"mean":n|null,"n":m},…}}
```

**Packed raster.** The body is $8 + 4WH$ bytes of big-endian 32-bit integers: $W$, $H$, then the $WH$ packed colours in row-major order,

$$\mathrm{body} = \mathrm{be32}(W)\,\|\,\mathrm{be32}(H)\,\|\,\mathop{\big\|}_{i=0}^{WH-1} \mathrm{be32}(\mathrm{rgb}_i).$$

The host keeps one body and its key $(k, \mathit{gen}, \mathit{layer})$: the step, the paint generation, and the layer. A raster request with the same key and size gets the same bytes; any other request refills the body in place, allocating only when the size changes:

$$\mathrm{body}_{t} = \begin{cases} \mathrm{body}_{t-1} & (k, \mathit{gen}, \mathit{layer})_t = (k, \mathit{gen}, \mathit{layer})_{t-1} \\ \mathrm{pack}(\mathit{raster}_t) & \text{otherwise.} \end{cases}$$

**Threads.** Requests run on a cached pool of daemon threads named `map-host-http`. Background steps run on one daemon thread, `map-host-advance`, and play ticks on `map-play` ([controller](controller.md)).

## Procedure

1. The process entry takes the port from its first argument (default 7420), and writes its process id to `aethelgard-maphost.pid` in the system temporary folder. It starts the host for `WorldSpec.VIEW`, prints `Aethelgard map host http://127.0.0.1:<port>`, and registers a shutdown hook that closes the host and deletes the file. It then blocks for ever.
2. Starting a host for a spec builds the play scheduler, the advance thread, and a controller that uses them, then binds. Starting a host for a given controller wraps it and does not own its threads. The view host listens on a free port for `WorldSpec.VIEW`.
3. Binding creates the server on `127.0.0.1:port` (0 picks a free port), registers the twelve routes, sets the request pool, and starts.
4. Every handler first answers a preflight, then checks the method, drains or reads the body, reads and decodes its parameters, calls the controller, and answers.
5. There is one handler per route of the table. Layer and speed names are resolved as the Model states.
6. The status object is written field by field. Strings are escaped for quotes, backslashes, and control characters, and each diagnostics collector gives its last value, mean, and count.
7. A raster request answers the cached body: the key is compared, and the body is refilled in place when it differs. Packing into a new array, and reading one integer back, are also available alone.
8. Closing stops the server and its pool, and, when the host owns them, the play scheduler and the advance thread. The host also answers its port, its base URL, its controller, and, for tests, the number of body allocations and the cached body.

## What is true afterwards

A status request never waits for a step: it reads the controller's captured values and the diagnostics hub. A raster response always matches the status's step and layer at the moment it was packed. Repeated raster requests between two paints return the same bytes without allocating. Every action answers with the status after the action was accepted, not after its step finished: an advance reports `busy: true` until a later status shows it done.

Code: [http/](../../../ui/src/main/java/com/aethelgard/ui/http/README.md)
Parent: [studio](README.md). The client that calls it: [web client](web/client.md). Why a local host and a web page: [ADR-011](../../paperwork/decisions/ADR-011-local-webview.md).
