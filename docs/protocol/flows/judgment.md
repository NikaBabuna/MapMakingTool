<!--
  File: docs/protocol/flows/judgment.md
  Purpose: Reconcile and rollback
  Audience: Agents and humans
  Update when: A judgment flow changes
-->

# Judgment flows

## Reconcile

On a new chat:

1. Navigation reads the goal index, the active Goal, and the step registry.
2. No `in progress` Step, and no Step record without an Accept, means the agent waits for a proposal.
3. An `in progress` Step, or a Step record with requirements and no Accept, means **Rollback**.

## Rollback

When a Step is torn, or the human ends a Step whose witness will not pass:

1. Return the tree to the last Accept.
2. Clear `in progress` on the step registry and the Goal.
3. Set the Step record to `rolled back`, or delete it if the human does not want that Step kept.
4. Do not commit the failed attempt.
5. Tell the human what was discarded.
