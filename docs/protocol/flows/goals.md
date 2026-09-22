<!--
  File: docs/protocol/flows/goals.md
  Purpose: Bookkeeping algorithms for Goals
  Audience: Agents and humans
  Update when: A goal flow changes
-->

# Goal flows

## Open goal

After the human approves the Goal text:

1. Write the Goal from the goal blueprint.
2. Add its row on the goal index and set that index’s Active Goal line.
3. Add the row on the goals folder door.
4. Register its Steps on the step registry as `not started`, with no Step record yet.
5. Add the roadmap row.
6. If the idea was on the backlog, mark it promoted.
7. Append a changelog line under Structure.

## Amend goal

After the human approves the new wording:

1. Edit the Goal.
2. Keep the goal index row in agreement.
3. Leave Step records alone unless the amendment changes their intent.

## Close goal

After the last Step is Accepted and the claim boxes hold:

1. Set the Goal’s status to `done` and fill progress.
2. Set its goal-index row to `done` and clear or advance the Active Goal line.
3. Mark the roadmap row done.
4. Append a changelog line.
