<!--
  File: docs/protocol/environment/correctness.md
  Purpose: What Accept means, what is incremental, and which states are blocked
  Audience: Agents and humans
  Update when: The definition of Accept changes
-->

# Correctness

A Step is correct only when all of the following are true at once. Any one of them failing means the Step is not Acceptable, no matter how complete the prose looks.

```
Accept(F-00n)  ⇔  this Step’s checks are green
               ∧  every earlier Accepted Step’s checks are still green
               ∧  the Sync flow has been applied to what this Step changed
               ∧  the approved requirements are stored in the Step record
               ∧  the work is inside the scope document
               ∧  the Step belongs to the active Goal
```

Compiling is not enough. A paragraph that says “clean” is not a requirement. A green suite obtained by deleting an older check is not Accept.

## Incremental suite

Each Accept adds to the bar. Later Steps keep every earlier Step’s checks green.

| Situation | Result |
|-----------|--------|
| This Step’s checks green, an earlier Step’s checks red | **Reject.** It is a regression. Fix it, or roll back |
| An earlier check deleted or skipped so the suite can pass | **Forbidden.** Restore the check |
| A requirement removed from an Accepted Step without the human’s approval and a written reason | **Forbidden** |
| This Step green, docs still describing the previous Step’s world | **Not Accept.** Run **Sync**, then judge again |

The witness command for this repository is the full suite through the Maven wrapper: `./mvnw test` or `mvnw.cmd test`. That command is a fact about this project’s implementation. The rule above is the protocol: whatever the command is, it must cover this Step and every earlier Accepted Step. A documentation-only Step does not invent a new program whose only job is to search documents for phrases. The existing suite is the witness that behavior still holds.

## Blocked states

Do not claim `done` in any of these states. Do the thing in the right-hand column instead.

| State | What it means | What to do |
|-------|----------------|------------|
| No human approval for the Step | The job was never agreed | Negotiate. Do not write the Step record yet |
| Approved requirements are not in `F-0xx.md` | The store is still chat | **Store step** before any implementation |
| No `in progress` mark, but files are already changing | The Step can look finished by accident | Mark first. If the edit is already wild, treat it as torn |
| A stored requirement has no check | The requirement is a wish | Add the check and map it in the Step record |
| New checks green, old checks red | Regression | Fix, or roll back |
| Checks weakened to pass | The bar was moved in secret | Restore the old checks |
| Requirements too vague to fail a check | They cannot witness anything | Rewrite them with the human until each one can fail |
| Status says `done` and the suite is red or was never run | False Accept | Demote the status, or roll back |
| Docs still describe the previous world | The prior is lying | **Sync** |
| Torn marks, half-written files | Failure wearing a success mask | **Rollback** |

## Legal states of a Step

| State | What an agent may claim |
|-------|-------------------------|
| Not started | Nothing has been agreed. The registry row may exist with no Step record |
| `fr-approved` | The requirements are on disk. Implementation has not started |
| `in progress` | Work is underway. The agent must not claim `done` |
| `done` | Accept holds. The Step may be committed and marked done |
| `rolled back` | The attempt was discarded. The tree matches the last Accept. Say so |

> Failure must never look like success.
