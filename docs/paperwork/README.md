<!--
  File: docs/paperwork/README.md
  Purpose: Door to the paperwork shelf
  Audience: Agents and humans
  Update when: A child of this folder is added or removed
-->

# Paperwork

Who did what, and when: the records of how this project has progressed. A record with its own id is one file, and a list read as a sequence is one file, for the reason in [ADR-016](decisions/ADR-016-paperwork-shelf.md).

Work is organised as Goals, each made of Steps. The Goal index says which Goal is active and what each one set out to do, the Step registry says which Step is in progress and what each one did, and each has a folder holding one record per id. Decisions record why a choice was made, and are indexed the same way. The changelog, the roadmap, and the backlog are single lists: what moved and when, the order of the Goals, and the ideas that are not Goals yet.

| Page | Read it when |
|------|----------------|
| [goals.md](goals.md) | You need the active Goal, or what each Goal set out to make true and whether it is done |
| [goals/](goals/README.md) | You need one Goal in full: its result, what it refuses, its decisions, its claims, and its planned Steps |
| [steps.md](steps.md) | You need which Step is in progress, or what each Step did, grouped by Goal |
| [steps/](steps/README.md) | You need what a Step was approved to do, its requirements, and the tests that prove them |
| [decisions.md](decisions.md) | You need which decisions exist, what each decided, or the next number |
| [decisions/](decisions/README.md) | You need why a choice was made, what was rejected, and what it replaced |
| [changelog.md](changelog.md) | You need what moved in the structure, the phase, or the scope, and when |
| [roadmap.md](roadmap.md) | You need the order of the Goals, and what is intended after the current one |
| [backlog.md](backlog.md) | You need an idea that is not a Goal yet, or where an old idea went |
