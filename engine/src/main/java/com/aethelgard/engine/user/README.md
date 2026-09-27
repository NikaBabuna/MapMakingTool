<!--
  File: engine/src/main/java/com/aethelgard/engine/user/README.md
  Purpose: Door to the user layer: the register of a person's actions, the frozen input view of a step, and the view port that sees the settled Pool
  Audience: Agents and humans
  Update when: A file or subfolder is added, removed, or renamed, or the folder's job, wiring, or dependencies change
-->

# User

Connects a person to a run without a window toolkit: a register of named actions pressed or latched between steps, the frozen view of them a step's compute reads, and a port that is shown the settled Pool after each step.

**Paper:** [User layer](../../../../../../../../docs/architecture/engine/user.md) · [Determinism](../../../../../../../../docs/architecture/engine/determinism.md) · **Conventions:** [conventions.md](../../../../../../../../docs/architecture/conventions.md)

## Why

Input arrives at any time, but a step must see one fixed set of it, or two runs with the same presses could differ. So input is registered between steps and frozen into a view at the start of each step, in one package the engine calls at fixed stages. Drawing belongs to the product's own view code, not here.

## How it works

1. A caller declares actions on a `UserInput` with `register`, each of an `InputKind`: held while pressed, or latched until consumed. It then calls `press`, `release`, and `consume` between steps.
2. At the start of a step, `stage` freezes the active actions into an immutable `InputView`, which the compute asks with `isActive`.
3. After compute, `consumePersistentPresentIn` releases every latched action the step saw.
4. After the step settles, the engine calls `UserView.onSettled` with the settled `PoolSnapshot`; the default port does nothing, and `RecordingUserView` keeps every frame for tests.

**Start reading at:** `UserInput.stage` in [UserInput.java](UserInput.java).

## Depends on

- [pool/](../pool/README.md) — the `PoolSnapshot` the view port is shown; the pool and user packages are peers ([conventions](../../../../../../../../docs/architecture/conventions.md))

## Used by

- [pool/](../pool/README.md) — the engine stages the view, hands it to the compute, consumes latches, and calls the view port
- [the user tests](../../../../../../test/java/com/aethelgard/engine/user/README.md) — how input reaches a step, and how a view sees the result

## Where each step happens

### [User layer](../../../../../../../../docs/architecture/engine/user.md)

| Step | Member | File |
|------|--------|------|
| 1. Actions are declared with their kind | `UserInput.register` | [UserInput.java](UserInput.java) |
| 2. Between steps, actions are pressed, released, or consumed | `UserInput.press` | [UserInput.java](UserInput.java) |
| 3. At stage time, the view is built | `UserInput.stage` | [UserInput.java](UserInput.java) |
| 4. The compute asks the immutable view | `InputView.isActive` | [InputView.java](InputView.java) |
| 5. After compute, the latched actions in the view are consumed | `UserInput.consumePersistentPresentIn` | [UserInput.java](UserInput.java) |
| 6. After settle, the view port is shown the settled snapshot | `UserView.onSettled`, `RecordingUserView.onSettled` | [UserView.java](UserView.java), [RecordingUserView.java](RecordingUserView.java) |

### [Determinism](../../../../../../../../docs/architecture/engine/determinism.md)

| Step | Member | File |
|------|--------|------|
| 2. The input view is computed from the register alone | `UserInput.stage` | [UserInput.java](UserInput.java) |

## Contents

| Path | What it does | Main types or functions |
|------|--------------|-------------------------|
| [UserInput.java](UserInput.java) | The register of named actions, and the stage that freezes them | `UserInput`, `register`, `press`, `release`, `stage` |
| [InputKind.java](InputKind.java) | Whether an action is held or latched | `InputKind` |
| [InputView.java](InputView.java) | The actions active in one step | `InputView`, `isActive` |
| [UserView.java](UserView.java) | The port shown the settled Pool after each step | `UserView`, `onSettled`, `noop` |
| [RecordingUserView.java](RecordingUserView.java) | A view that records every settled frame, for tests | `RecordingUserView`, `frames` |
