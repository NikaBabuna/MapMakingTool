<!--
  File: docs/architecture/engine/events.md
  Purpose: Events — which events fire and who claims them
  Audience: Agents and humans
  Update when: How categories are resolved, how events are emitted or claimed, or an emission policy changes
-->

# Events

An event is a notice that something of a certain kind may need doing this step. It is not addressed to anyone: it carries a category from a tree of categories, and every system whose own category is that one or an ancestor of it takes the event up.

## What it reads

The category tree the product builds, the events the compute emits into the buffer during the Pool's update, and the claimers: the stub `EventClaimer`s of the setup, followed by one claimer per `EngineSystem`, in system order. Each claimer has one assigned category, fixed when it is built.

## What it writes

A `ClaimResult`: for every claimer, the list of events it claimed, and the list of events no claimer claimed. The engine reports each claimed pair and each unmatched event to its [diagnostics](diagnostics.md). Claiming does not change the buffer. Reading an unknown path from the tree throws `IllegalArgumentException`, and every tree operation throws it for a blank path or one that starts or ends with `/`.

## Model

A category is a path $p = a_1/a_2/\dots/a_n$. Its parent is $a_1/\dots/a_{n-1}$ (none when $n = 1$), and its ancestry is the chain

$$\operatorname{anc}(p) = \{\,p,\; \operatorname{parent}(p),\; \operatorname{parent}^2(p),\; \dots\,\}.$$

Two categories are equal when their paths are equal. For an event $e$ with category $\mathrm{cat}(e)$ and a claimer $\mathit{cl}$ with assigned category $\mathrm{cat}(\mathit{cl})$,

$$\mathit{cl} \triangleright e \iff \mathrm{cat}(\mathit{cl}) \in \operatorname{anc}(\mathrm{cat}(e)),$$

read "$\mathit{cl}$ claims $e$": the event's category is the claimer's category or a descendant of it at any depth. Over the buffer $B_k = (e_1, \dots, e_m)$ and the claimer list $(\mathit{cl}_1, \dots, \mathit{cl}_r)$,

$$B_{\mathit{cl}} = \bigl(e_i : \mathit{cl} \triangleright e_i\bigr)_{i=1..m}, \qquad B_{\bot} = \bigl(e_i : \nexists\, \mathit{cl}.\; \mathit{cl} \triangleright e_i\bigr)_{i=1..m},$$

both in buffer order. An event may belong to several $B_{\mathit{cl}}$. A system $\psi$ runs this step exactly when $B_{\mathit{cl}(\psi)} \neq \emptyset$, where $\mathit{cl}(\psi)$ is its claimer.

## Procedure

1. The product builds the tree from paths, or starts from an empty tree. Ensuring a path trims it, rejects a blank path or a leading or trailing `/`, and returns the existing category or creates it after ensuring its parent. The tree can be asked whether it holds a path without creating it, and reading an unknown path throws.
2. When the engine is created, each scripted path is ensured, so a scripted path need not exist beforehand.
3. During compute, the wired policy decides which categories fire. The default emits every resolved scripted category. A product replaces it by implementing the `EventEmissionPolicy` port.
4. Each emission wraps the category in an `EngineEvent`, which rejects a null category, and appends it to the buffer in emission order.
5. The engine gathers the claimers, stubs first and then one per system, and runs the claiming. It gives every claimer an empty list, then offers each event, in buffer order, to each claimer, in list order.
6. A claimer claims an event when the event's category is its assigned category or a descendant: the walk up the event category's parent chain meets the assigned category.
7. The lists become a `ClaimResult`, whose constructor copies them into immutable collections.
8. After merge, the engine empties the buffer.

## What is true afterwards

Every event has been offered to every claimer, and every event is either claimed at least once or reported as unmatched, never dropped silently. A system with an empty claim list does not run. No event can be added between compute and the next step, so a consequence that another system should notice fires only when the next step's compute reads the settled fields and emits again.

Claimers are map keys by identity, because `EventClaimer` does not define equality; the engine finds a system's list through the one claimer object that system holds. The `ClaimResult` map is an immutable copy whose iteration order is unspecified, so the order of the claim reports across claimers is unspecified. Field values do not depend on it.

## Cost

Claiming is $O(m \cdot r \cdot d)$ for $m$ events, $r$ claimers, and category depth $d$.

Code: [engine/events/](../../../engine/src/main/java/com/aethelgard/engine/events/README.md) · [engine/pool/](../../../engine/src/main/java/com/aethelgard/engine/pool/README.md)  
Parent: [one engine step](README.md). Who runs after a claim: [systems](systems.md). Why unmatched events are reported: [ADR-006](../../paperwork/decisions/ADR-006-unmatched-events.md). Why the product authors the tree in code: [ADR-009](../../paperwork/decisions/ADR-009-category-tree.md).
