<!--
  File: docs/engine/specs/user-layer.md
  Purpose: User View, User Input, Input View
  Audience: Agents implementing engine and product UI
  Update when: User-facing layer contract changes
-->

# User-facing layer

## User View

Decoupled from the Pool and System engine. Reads the **settled** Pool once per Step and produces the frame the user sees — by any means. Merge machinery does not apply: relationship to Pool is **read only**.

The user never observes a Step half-applied. Multi-Step consequence chains appear as distinct complete frames.

## User Input and Input View

**User Input** is a register of currently or previously pressed inputs. Meaning is a function of **input type**, not raw signal alone — persistence behavior lives in the type.

| Kind | Behavior |
|------|----------|
| **Persistent** | Latches on press; stays until consumed, regardless of physical hold |
| **Non-persistent** | Present only while physically held |

The Pool samples input once per Step. If the Step window is coarser than a press-and-release, non-persistent input can vanish between samples. Persistent input closes that gap by design.

**Input View** is the staged snapshot the Pool reads at compute time. Everything gathered during the Step counts as one simultaneous instant — one coherent "now" instead of asynchronous smear.
