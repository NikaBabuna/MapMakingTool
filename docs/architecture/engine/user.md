<!--
  File: docs/architecture/engine/user.md
  Purpose: UserInput, InputKind, InputView, UserView, RecordingUserView — the engine's two ports toward a person
  Audience: Agents and humans
  Update when: UserInput.stage, UserInput.consumePersistentPresentIn, or the UserView contract changes
-->

# User layer

The engine talks to a person through two ports, and neither is a window. Input is a register of named actions that is read once, before compute; the view is told once, after the step has settled, what the settled state is. Neither port can change the Pool directly.

## What it reads

Between steps, callers `press` and `release` named actions on the shared `UserInput`, after declaring each one with `register` and an `InputKind`. At stage time the engine reads the register. After settle, the engine hands `UserView.onSettled` the settled `PoolSnapshot`.

## What it writes

`stage` returns an `InputView`, the frozen set of actions active for this step. `consumePersistentPresentIn` clears persistent latches. The view writes nothing back. Refusals: `press`, `release`, and `consume` of an unregistered action throw `IllegalArgumentException`, and so does `register` of an action already registered with the other kind. `RecordingUserView.lastFrame` throws `IllegalStateException` before any frame.

## Model

The register is $\mathcal{I} = (\mathrm{kind}, \mathit{Held}, \mathit{Latched})$: a kind map $\mathrm{kind} : \mathit{Act} \to \{\mathsf{P}, \mathsf{NP}\}$ over the registered actions $\mathit{Act}$ (persistent, non-persistent), the set of held actions $\mathit{Held} \subseteq \mathit{Act}$, and the set of latched actions $\mathit{Latched} \subseteq \mathit{Act}$. Between steps, for an action $a$,

$$\texttt{press}(a):\; \mathit{Held} \leftarrow \mathit{Held} \cup \{a\},\;\; \mathit{Latched} \leftarrow \mathit{Latched} \cup \{a\} \text{ if } \mathrm{kind}(a) = \mathsf{P}; \qquad \texttt{release}(a):\; \mathit{Held} \leftarrow \mathit{Held} \setminus \{a\}; \qquad \texttt{consume}(a):\; \mathit{Latched} \leftarrow \mathit{Latched} \setminus \{a\}.$$

Stage and consume at step $k$ are

$$I_k = \{\, a : \mathrm{kind}(a) = \mathsf{NP},\ a \in \mathit{Held} \,\} \;\cup\; \{\, a : \mathrm{kind}(a) = \mathsf{P},\ a \in \mathit{Latched} \,\}, \qquad \mathit{Latched} \leftarrow \mathit{Latched} \setminus \{\, a \in I_k : \mathrm{kind}(a) = \mathsf{P} \,\}.$$

So a persistent press is seen by exactly one step, however briefly it was held, and a non-persistent action is seen by every step staged while it is held. All actions in $I_k$ are simultaneous: $I_k$ is a set, with no order among its members.

`UserInput.stage` in [`UserInput.java`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java):

```java
Set<String> active = new LinkedHashSet<>();
for (var e : kinds.entrySet()) {
  String action = e.getKey();
  if (e.getValue() == InputKind.NON_PERSISTENT) {
    if (held.contains(action)) {
      active.add(action);
    }
  } else if (latched.contains(action)) {
    active.add(action);
  }
}
return new InputView(active);
```

## Procedure

1. A caller declares actions with `register(action, kind)`. Registering the same kind again changes nothing. [`UserInput.register`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java).
2. Between steps the caller calls `press` and `release`, and may `consume` a latch early. `isHeld`, `isLatched`, and `kindOf` read the register. [`UserInput.press`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java).
3. At stage time the engine builds the view: held non-persistent actions and latched persistent actions. [`UserInput.stage`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java).
4. The view is an immutable set, and a compute asks it with `isActive`. [`InputView.isActive`](../../../engine/src/main/java/com/aethelgard/engine/user/InputView.java).
5. After compute, the engine consumes every persistent action that was in the view. [`UserInput.consumePersistentPresentIn`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java).
6. After settle, the engine calls the view port with the settled snapshot. The default port does nothing, and `RecordingUserView` keeps every frame for tests. [`UserView.onSettled`](../../../engine/src/main/java/com/aethelgard/engine/user/UserView.java), [`RecordingUserView.onSettled`](../../../engine/src/main/java/com/aethelgard/engine/user/RecordingUserView.java).

## What is true afterwards

`Engine.lastInputView()` is the view the step used. No persistent action of that view is still latched. The view port saw a snapshot in which merge had already been applied. It never sees the post-compute, pre-merge snapshot that the systems used.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Register | `UserInput` | `register`, `press`, `release`, `consume`, `isHeld`, `isLatched`, `kindOf`, `stage`, `consumePersistentPresentIn` | [`engine/src/main/java/com/aethelgard/engine/user/UserInput.java`](../../../engine/src/main/java/com/aethelgard/engine/user/UserInput.java) |
| Kind | `InputKind` | `PERSISTENT`, `NON_PERSISTENT` | [`engine/src/main/java/com/aethelgard/engine/user/InputKind.java`](../../../engine/src/main/java/com/aethelgard/engine/user/InputKind.java) |
| Sample | `InputView` | `InputView`, `active`, `InputView.empty`, `isActive` | [`engine/src/main/java/com/aethelgard/engine/user/InputView.java`](../../../engine/src/main/java/com/aethelgard/engine/user/InputView.java) |
| Frame port | `UserView` | `UserView.onSettled`, `noop` | [`engine/src/main/java/com/aethelgard/engine/user/UserView.java`](../../../engine/src/main/java/com/aethelgard/engine/user/UserView.java) |
| Test sink | `RecordingUserView` | `RecordingUserView.onSettled`, `frames`, `lastFrame` | [`engine/src/main/java/com/aethelgard/engine/user/RecordingUserView.java`](../../../engine/src/main/java/com/aethelgard/engine/user/RecordingUserView.java) |

Parent: [one engine step](README.md).
