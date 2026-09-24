<!--
  File: docs/project/project.md
  Purpose: Hard scope lock
  Audience: Agents and humans
  Update when: Scope changes (also decisions.md if technical)
-->

# Project scope

**Product name:** Aethelgard  
**Repository:** MapMakingTool  
**Phase:** alpha

---

## One line

Procedural fantasy world generator — simulate tectonics, climate, and terrain so maps stay physically consistent, with a scrubbable history of how the world formed.

---

## In scope

| Area | Description |
|------|-------------|
| **Engine** | Pool-System Framework — step-based simulation, typed merge, events, systems; **host ports** so product Systems live outside `engine` |
| **Product** | Aethelgard — world generation, guided nudging, timeline/history exploration (G-003: first elevation slice) |
| **Process** | Agent-assisted development under repository protocol |

---

## Out of scope (for now)

<!-- User may refine -->

- Published standalone engine library / separate package
- Multiplayer, cloud hosting, account systems
- Noise-only or hand-painted terrain as the **core** generation model
- _(add exclusions as needed)_

---

## Stack (intent)

| Choice | Status |
|--------|--------|
| Language: Java | Accepted — see ADR-003 in [../paperwork/decisions.md](../paperwork/decisions.md) |
| Build: Maven + wrapper | Accepted — scaffolded in F-001 |
| Layout: monorepo (engine + product) | Accepted — see ADR-001, ADR-007 |
| JDK | Java 21 — recorded in [../architecture/program.md](../architecture/program.md) |
| Agent process | Goal / Step — see [../protocol/environment/core-definition.md](../protocol/environment/core-definition.md) |
| Interactive UI: Tauri 2 + Next.js + Java HTTP host | Accepted for G-006 — see ADR-011 (Swing map **removed**) |

---

## Expansion

Scope changes belong here first. Technical scope changes also get an entry in [../paperwork/decisions.md](../paperwork/decisions.md).
