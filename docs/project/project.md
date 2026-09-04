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
| **Engine** | Pool-System Framework — step-based simulation, typed merge, events, systems |
| **Product** | Aethelgard — world generation, guided nudging, timeline/history exploration |
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
| Language: Java | Accepted — see ADR-003 in [decisions.md](decisions.md) |
| Build: Maven + wrapper | Accepted — scaffolded in F-001 |
| Layout: monorepo (engine + product) | Accepted — see ADR-001, ADR-007 |
| JDK | Java 21 — recorded in [../engine/architecture.md](../engine/architecture.md) |
| Agent process | Goal / Session / Step — see ADR-004 |

---

## Expansion

Scope changes belong here first. Technical scope changes also get an entry in [decisions.md](decisions.md).
