<!--
  File: docs/architecture/engine/user.md
  Purpose: User layer — the engine's two ports toward a person
  Audience: Agents and humans
  Update when: How input is registered, staged, or consumed, or what the view port is shown, changes
-->

# User layer

The engine talks to a person through two ports, and neither is a window. Input is a register of named actions that is read once, before compute; the view is told once, after the step has settled, what the settled state is. Neither port can change the Pool directly.

## What it reads

Between steps, callers press and release named actions on the shared `UserInput`, after declaring each one with an `InputKind`. At stage time the engine reads the register. After settle, the engine hands the `UserView` port the settled `PoolSnapshot`.

## What it writes

Staging returns an `InputView`, the frozen set of actions active for this step, and consuming clears persistent latches. The view writes nothing back. Refusals: pressing, releasing, or consuming an unregistered action throws `IllegalArgumentException`, and so does registering an action already registered with the other kind. Asking a `RecordingUserView` for its last frame before any frame throws `IllegalStateException`.

## Model

The register is $\mathcal{I} = (\mathrm{kind}, \mathit{Held}, \mathit{Latched})$: a kind map $\mathrm{kind} : \mathit{Act} \to \{\mathsf{P}, \mathsf{NP}\}$ over the registered actions $\mathit{Act}$ (persistent, non-persistent), the set of held actions $\mathit{Held} \subseteq \mathit{Act}$, and the set of latched actions $\mathit{Latched} \subseteq \mathit{Act}$. Between steps, for an action $a$,

$$\texttt{press}(a):\; \mathit{Held} \leftarrow \mathit{Held} \cup \{a\},\;\; \mathit{Latched} \leftarrow \mathit{Latched} \cup \{a\} \text{ if } \mathrm{kind}(a) = \mathsf{P}; \qquad \texttt{release}(a):\; \mathit{Held} \leftarrow \mathit{Held} \setminus \{a\}; \qquad \texttt{consume}(a):\; \mathit{Latched} \leftarrow \mathit{Latched} \setminus \{a\}.$$

Stage and consume at step $k$ are

$$I_k = \{\, a : \mathrm{kind}(a) = \mathsf{NP},\ a \in \mathit{Held} \,\} \;\cup\; \{\, a : \mathrm{kind}(a) = \mathsf{P},\ a \in \mathit{Latched} \,\}, \qquad \mathit{Latched} \leftarrow \mathit{Latched} \setminus \{\, a \in I_k : \mathrm{kind}(a) = \mathsf{P} \,\}.$$

So a persistent press is seen by exactly one step, however briefly it was held, and a non-persistent action is seen by every step staged while it is held. All actions in $I_k$ are simultaneous: $I_k$ is a set, with no order among its members.

## Procedure

1. A caller declares each action with its kind. Registering the same kind again changes nothing.
2. Between steps the caller presses and releases actions, and may consume a latch early. The register can be asked whether an action is held or latched, and of which kind it is.
3. At stage time the engine builds the view: held non-persistent actions and latched persistent actions.
4. The view is an immutable set, and a compute asks it whether an action is active.
5. After compute, the engine consumes every persistent action that was in the view.
6. After settle, the engine calls the view port with the settled snapshot. The default port does nothing, and `RecordingUserView` keeps every frame for tests.

## What is true afterwards

The engine's last input view is the view the step used. No persistent action of that view is still latched. The view port saw a snapshot in which merge had already been applied. It never sees the post-compute, pre-merge snapshot that the systems used.

Code: [engine/user/](../../../engine/src/main/java/com/aethelgard/engine/user/README.md)  
Parent: [one engine step](README.md).
