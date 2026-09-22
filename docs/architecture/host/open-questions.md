<!--
  File: docs/architecture/host/open-questions.md
  Purpose: Host gaps that are still undecided
  Audience: Agents and humans
  Update when: A gap is decided or a new host gap is found
-->

# Open questions

Decided host questions live on the page that states the rule, with the decision record. This page lists only what is still open.

## Still open

| # | Topic | Question |
|---|-------|----------|
| 1 | Non-finishing systems | If a system claims and does not finish, does the step wait, time out, or merge without that output? The loop today runs systems synchronously and throws on an unbalanced barrier. It does not implement a wait or a skip. |
| 4 | Delete Request | There is no delete merge type. If one were added, a concurrent write could either win or lose. That choice is not made. |

## Decided elsewhere

| Topic | Where the rule is |
|-------|-------------------|
| Unmatched events are logged | [events](events.md), [ADR-006](../../paperwork/decisions/ADR-006-unmatched-events.md) |
| Step 0 is seeded from a config object | [pool](pool.md), [ADR-005](../../paperwork/decisions/ADR-005-step-zero-config.md) |
| The product authors the category tree in Java | [events](events.md), [ADR-009](../../paperwork/decisions/ADR-009-category-tree.md) |

Parent: [one engine step](README.md).
