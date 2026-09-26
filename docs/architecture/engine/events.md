<!--
  File: docs/architecture/engine/events.md
  Purpose: Category, CategoryTree, EngineEvent, EventBuffer, EventClaimer, EventClaiming, ClaimResult, EventEmissionPolicy — which events fire and who claims them
  Audience: Agents and humans
  Update when: EventClaiming.claim, EventClaimer.claims, CategoryTree.ensure, or an emission policy changes
-->

# Events

An event is a notice that something of a certain kind may need doing this step. It is not addressed to anyone: it carries a category from a tree of categories, and every system whose own category is that one or an ancestor of it takes the event up.

## What it reads

The category tree the product builds, the events the compute emits into the buffer during `Pool.update`, and the claimers: the stub `EventClaimer`s of the setup, followed by one claimer per `EngineSystem`, in system order. Each claimer has one assigned category, fixed when it is built.

## What it writes

A `ClaimResult`: for every claimer, the list of events it claimed, and the list of events no claimer claimed. The engine reports each claimed pair through `EngineDiagnostics.eventClaimed` and each unmatched event through `EngineDiagnostics.unmatchedEvent`. Claiming does not change the buffer. `CategoryTree.get` throws `IllegalArgumentException` for an unknown path, and every tree operation throws it for a blank path or one that starts or ends with `/`.

## Model

A category is a path $p = a_1/a_2/\dots/a_n$. Its parent is $a_1/\dots/a_{n-1}$ (none when $n = 1$), and its ancestry is the chain

$$\operatorname{anc}(p) = \{\,p,\; \operatorname{parent}(p),\; \operatorname{parent}^2(p),\; \dots\,\}.$$

Two categories are equal when their paths are equal. For an event $e$ with category $\mathrm{cat}(e)$ and a claimer $\mathit{cl}$ with assigned category $\mathrm{cat}(\mathit{cl})$,

$$\mathit{cl} \triangleright e \iff \mathrm{cat}(\mathit{cl}) \in \operatorname{anc}(\mathrm{cat}(e)),$$

read "$\mathit{cl}$ claims $e$": the event's category is the claimer's category or a descendant of it at any depth. Over the buffer $B_k = (e_1, \dots, e_m)$ and the claimer list $(\mathit{cl}_1, \dots, \mathit{cl}_r)$,

$$B_{\mathit{cl}} = \bigl(e_i : \mathit{cl} \triangleright e_i\bigr)_{i=1..m}, \qquad B_{\bot} = \bigl(e_i : \nexists\, \mathit{cl}.\; \mathit{cl} \triangleright e_i\bigr)_{i=1..m},$$

both in buffer order. An event may belong to several $B_{\mathit{cl}}$. A system $\psi$ runs this step exactly when $B_{\mathit{cl}(\psi)} \neq \emptyset$, where $\mathit{cl}(\psi)$ is its claimer.

`EventClaiming.claim` in [`EventClaiming.java`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaiming.java):

```java
for (EngineEvent event : events) {
  boolean any = false;
  for (EventClaimer claimer : claimers) {
    if (claimer.claims(event)) {
      claimed.get(claimer).add(event);
      any = true;
    }
  }
  if (!any) {
    unmatched.add(event);
  }
}
```

`Category.isSelfOrDescendantOf` in [`Category.java`](../../../engine/src/main/java/com/aethelgard/engine/event/Category.java):

```java
for (Category c = this; c != null; c = c.parent) {
  if (c.equals(ancestor)) {
    return true;
  }
}
return false;
```

## Procedure

1. The product builds the tree with `CategoryTree.of` (from paths) or `CategoryTree.empty`. `ensure` trims a path, rejects a blank path or a leading or trailing `/`, returns the existing category, or creates it after recursively ensuring its parent. `contains` asks without creating, and `get` throws for an unknown path. [`CategoryTree.ensure`](../../../engine/src/main/java/com/aethelgard/engine/event/CategoryTree.java).
2. When the engine is created, `resolveAll` ensures each scripted path, so a scripted path need not exist beforehand. [`CategoryTree.resolveAll`](../../../engine/src/main/java/com/aethelgard/engine/event/CategoryTree.java).
3. During compute, the wired policy decides which categories fire. The default emits every resolved scripted category, through `PoolComputeContext.emitScripted`. A product replaces it by implementing `emitEvents`. [`ScriptedEventEmissionPolicy.emitEvents`](../../../engine/src/main/java/com/aethelgard/engine/pool/ScriptedEventEmissionPolicy.java), [`EventEmissionPolicy.emitEvents`](../../../engine/src/main/java/com/aethelgard/engine/pool/EventEmissionPolicy.java).
4. Each emission wraps the category in an `EngineEvent`, which rejects a null category, and appends it to the buffer in emission order. [`EventBuffer.add`](../../../engine/src/main/java/com/aethelgard/engine/event/EventBuffer.java), [`EngineEvent`](../../../engine/src/main/java/com/aethelgard/engine/event/EngineEvent.java).
5. The engine gathers the claimers, stubs first and then one per system, and calls `claim`. It gives every claimer an empty list, then offers each event, in buffer order, to each claimer, in list order. [`EventClaiming.claim`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaiming.java).
6. A claimer claims an event when the event's category is its assigned category or a descendant: the walk up the event category's parent chain meets the assigned category. [`EventClaimer.claims`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaimer.java), [`Category.isSelfOrDescendantOf`](../../../engine/src/main/java/com/aethelgard/engine/event/Category.java).
7. The lists become a `ClaimResult`, whose constructor copies them into immutable collections. [`ClaimResult`](../../../engine/src/main/java/com/aethelgard/engine/event/ClaimResult.java).
8. After merge, the engine empties the buffer. [`EventBuffer.clear`](../../../engine/src/main/java/com/aethelgard/engine/event/EventBuffer.java).

## What is true afterwards

Every event has been offered to every claimer, and every event is either claimed at least once or reported as unmatched, never dropped silently. A system with an empty claim list does not run. No event can be added between compute and the next step, so a consequence that another system should notice fires only when the next step's compute reads the settled fields and emits again.

Claimers are map keys by identity, because `EventClaimer` does not define equality; the engine finds a system's list through the one claimer object that system holds. The `ClaimResult` map is an immutable copy whose iteration order is unspecified, so the order of `eventClaimed` reports across claimers is unspecified. Field values do not depend on it.

## Cost

Claiming is $O(m \cdot r \cdot d)$ for $m$ events, $r$ claimers, and category depth $d$.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Category | `Category` | `path`, `parent`, `isSelfOrDescendantOf`, `equals`, `hashCode`, `toString` | [`engine/src/main/java/com/aethelgard/engine/event/Category.java`](../../../engine/src/main/java/com/aethelgard/engine/event/Category.java) |
| Tree | `CategoryTree` | `empty`, `of`, `get`, `contains`, `ensure`, `resolveAll` | [`engine/src/main/java/com/aethelgard/engine/event/CategoryTree.java`](../../../engine/src/main/java/com/aethelgard/engine/event/CategoryTree.java) |
| Event | `EngineEvent` | `EngineEvent`, `category` | [`engine/src/main/java/com/aethelgard/engine/event/EngineEvent.java`](../../../engine/src/main/java/com/aethelgard/engine/event/EngineEvent.java) |
| Buffer | `EventBuffer` | `add`, `events`, `isEmpty`, `clear` | [`engine/src/main/java/com/aethelgard/engine/event/EventBuffer.java`](../../../engine/src/main/java/com/aethelgard/engine/event/EventBuffer.java) |
| Claimer | `EventClaimer` | `EventClaimer`, `id`, `assigned`, `claims`, `EventClaimer.toString` | [`engine/src/main/java/com/aethelgard/engine/event/EventClaimer.java`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaimer.java) |
| Dispatch | `EventClaiming` | `claim` | [`engine/src/main/java/com/aethelgard/engine/event/EventClaiming.java`](../../../engine/src/main/java/com/aethelgard/engine/event/EventClaiming.java) |
| Result | `ClaimResult` | `ClaimResult`, `claimedByClaimer`, `unmatched`, `ClaimResult.empty` | [`engine/src/main/java/com/aethelgard/engine/event/ClaimResult.java`](../../../engine/src/main/java/com/aethelgard/engine/event/ClaimResult.java) |
| Emission port | `EventEmissionPolicy` | `emitEvents` | [`engine/src/main/java/com/aethelgard/engine/pool/EventEmissionPolicy.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/EventEmissionPolicy.java) |
| Default emission | `ScriptedEventEmissionPolicy` | `INSTANCE`, `ScriptedEventEmissionPolicy.emitEvents` | [`engine/src/main/java/com/aethelgard/engine/pool/ScriptedEventEmissionPolicy.java`](../../../engine/src/main/java/com/aethelgard/engine/pool/ScriptedEventEmissionPolicy.java) |

Parent: [one engine step](README.md). Who runs after a claim: [systems](systems.md). Why unmatched events are reported: [ADR-006](../../paperwork/decisions/ADR-006-unmatched-events.md). Why the product authors the tree in code: [ADR-009](../../paperwork/decisions/ADR-009-category-tree.md).
