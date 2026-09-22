# Agents

**Phase:** alpha — see [docs/PHASE.md](docs/PHASE.md)

Repository protocol for AI agents. Theory: [docs/process/protocol-overview.md](docs/process/protocol-overview.md).

**Active Goal:** none · Last completed: [G-010 Crust topology](docs/project/goals/G-010-crust-topology.md) (`done`) · Prior: [G-009 Simulation runner harden](docs/project/goals/G-009-simulation-runner-harden.md) (`done`) · [G-008](docs/project/goals/G-008-boundary-tectonics-studio.md) (`done`) 
**Current Session:** [docs/project/session.md](docs/project/session.md)

---

## Read order (non-trivial work)

1. [docs/PHASE.md](docs/PHASE.md)
2. [docs/process/global-prompt.md](docs/process/global-prompt.md)
3. [docs/process/rules.md](docs/process/rules.md)
4. [docs/process/step-procedure.md](docs/process/step-procedure.md) — **binding Step loop**
5. [docs/process/quality.md](docs/process/quality.md)
6. [docs/project/session.md](docs/project/session.md) — reconcile / torn-Step check
7. [docs/project/goals.md](docs/project/goals.md) — active Goal
8. [docs/project/project.md](docs/project/project.md)
9. [docs/architecture.md](docs/architecture.md)
10. [docs/navigation.md](docs/navigation.md)
11. Step-relevant: [docs/project/features.md](docs/project/features.md), specs, [docs/blockers/](docs/blockers/)

---

## Default on new chat / “continue”

1. Reconcile Session + Step marks — if torn, **rollback** and report  
2. Confirm Active Goal text matches [docs/project/goals.md](docs/project/goals.md) on AGENTS / navigation / README / session / PHASE; fix drift before new work  
3. Follow active Goal; if none, **propose next Goal** (not a lone Step); if Session empty under an active Goal, propose next `F-0xx`  
4. No code until user approves job + FRs; store FRs in `docs/blockers/F-0xx.md` first  
5. Witness is incremental — prior Step tests must stay green  
6. After Accept: SYNC checklist ([docs/process/step-procedure.md](docs/process/step-procedure.md)); re-check entry points vs `goals.md`  

Full procedure: [docs/process/step-procedure.md](docs/process/step-procedure.md)

---

## Commits

After a successful Step final check (green witness + **full** doc sync), commit that Step.
