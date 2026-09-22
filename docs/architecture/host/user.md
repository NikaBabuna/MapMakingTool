<!--
  File: docs/architecture/host/user.md
  Purpose: UserInput, InputView, UserView
  Audience: Agents and humans
  Update when: The user ports change
-->

# User layer

The engine has two ports toward a person, and neither one is a window. Input is sampled once, before compute. The view is called once, after the step has settled. The engine contains no UI toolkit.

## What it reads

`UserInput` is a register of named actions. `stage` copies the active set into an `InputView`. `PoolCompute` reads that view. After compute, `consumePersistentPresentIn` clears persistent actions that were in the view.

`UserView.onSettled` receives the `PoolSnapshot` taken after field apply.

## What it writes

`InputView` is a frozen set for one step. The Pool's compute may change heartbeat and events because of it. `UserView` writes nothing back into the Pool. The relationship is read-only.

## Procedure

| Kind | Behavior |
|------|----------|
| `PERSISTENT` | Latches on press and stays until the step that saw it consumes it. |
| `NON_PERSISTENT` | Present only while held at `stage`. A press and release between two samples is absent. |

Every action in the view is simultaneous for that compute. There is no ordering among them inside the step.

Defaults from `EngineSetup`: an empty `UserInput`, and `UserView.noop()`. `RecordingUserView` stores snapshots for tests.

## What is true afterwards

`Engine.lastInputView()` is the view just sampled. Persistent actions that were in it are no longer latched. The user view has seen a snapshot in which merge has already been applied. It has not seen the post-compute, pre-merge snapshot that systems used.

## Where it lives

| Piece | Type | Path |
|-------|------|------|
| Register | `UserInput` | `engine/.../user/UserInput.java` |
| Kind | `InputKind` | `engine/.../user/InputKind.java` |
| Sample | `InputView` | `engine/.../user/InputView.java` |
| Frame port | `UserView` | `engine/.../user/UserView.java` |
| Test sink | `RecordingUserView` | `engine/.../user/RecordingUserView.java` |

Parent: [one engine step](README.md). The studio that implements a view: [../studio/README.md](../studio/README.md).
