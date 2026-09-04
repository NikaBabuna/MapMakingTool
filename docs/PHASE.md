<!--
  File: docs/PHASE.md
  Purpose: Current project phase and structural change rules
  Audience: Agents and humans
  Update when: Phase changes or alpha rules refine
-->

# Project phase

**Current phase:** `alpha`

---

## Phases

| Phase | Structure & core docs | Features & code |
|-------|----------------------|-----------------|
| **alpha** | Fluid — move/rename with changelog | Spike; witness once code exists |
| **beta** | Locked — structural changes need ADR | Goal / Session / Step per protocol |
| **prod** | Locked + migration notes | Semver, changelog, CI |

---

## Alpha rules (active)

**Allowed:** create, move, rename docs and folders; rewrite core specs.

**Required on structural change:**

1. Update [navigation.md](navigation.md)
2. Log under Structure in [project/changelog.md](project/changelog.md)
3. Non-obvious moves → [project/decisions.md](project/decisions.md)
4. **Folder README:** every landmark folder has a `README.md` explaining what lives there; [navigation.md](navigation.md) links those READMEs

**Landmark folders** (require README): `docs/` and each docs namespace (`process/`, `project/`, `project/goals/`, `engine/`, `engine/specs/`, `product/`, `product/wiki/`, `blockers/`); code module roots (`engine/`, `cli/`, `ui/`) and owned packages under `engine/src/main/java/.../engine/{pool,event,diag,system,merge,user}/`; `.github/`, `.github/workflows/`, `.cursor/`, `.cursor/rules/`.

**Exempt:** `target/`, `.tools/`, `.mvn/`, intermediate Java path segments (`src/`, `main/`, `java/`, `com/`, …).

**Not required yet:** blocker loop for pure doc/layout work.

---

## Beta gate (future)

- [ ] [navigation.md](navigation.md) matches disk (including landmark folder READMEs)
- [ ] [engine/architecture.md](engine/architecture.md) agrees with source layout
- [ ] No `TBD` in scope or core architecture
- [ ] Process docs canonical under [process/](process/)

---

## Structural aesthetics

- Namespaces: `process/`, `project/`, `engine/`, `product/`
- Max three levels under `docs/`
- kebab-case files; `G-0xx` Goals; `F-0xx` Steps; `ADR-0xx` decisions
- No empty-authority docs — list *planned* in navigation instead
- Landmark folders carry a `README.md`; navigation links those READMEs ([PHASE.md](PHASE.md))

---

## Agent work model

Goal → Session → Step. Binding procedure: [process/step-procedure.md](process/step-procedure.md).  
Active Goal: [project/goals.md](project/goals.md).
