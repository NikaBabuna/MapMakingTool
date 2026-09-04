<!--
  File: docs/engine/specs/architecture-diagram.md
  Purpose: Pool-System architecture at a glance
  Audience: Agents and humans
  Update when: Top-level architecture changes
-->

# Architecture at a glance

```mermaid
flowchart LR
    UI[User Input] -->|staged each step| IV[Input View]
    IV -->|read during computation| P((Pool))
    P -->|settled state each step| UV[User View]

    P -->|writes| EB[[Event Buffer]]
    EB -->|category match| SA[System A]
    EB -->|category match| SB[System B]

    SA -->|OUT_SYS| OB[[Output Buffer]]
    SB -->|OUT_SYS| OB
    OB -->|typed merge, apply| P
```

The Pool sits at the center. User Input stages into an Input View the Pool reads mid-computation; User View reads the Pool once a Step settles. Systems are dispatched by Pool events and feed results back through typed merge.

Related: [step-lifecycle.md](step-lifecycle.md), [systems.md](systems.md), [events.md](events.md), [merge-types.md](merge-types.md).
