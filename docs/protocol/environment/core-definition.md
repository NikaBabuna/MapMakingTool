<!--
  File: docs/protocol/environment/core-definition.md
  Purpose: Core terminology and the Goal–Step system
  Audience: Agents
  Update when: Goal or Step types, statuses, or core terms change
-->

# Core definition

## Status

This instrument defines the core terminology of the protocol and the Goal–Step system. It binds every agent.

How Goals and Steps are negotiated, stored, and closed is prescribed by [core-workflow.md](../core-workflow.md) and by the Goal and Step flow files under [flows/](../flows/). This instrument defines what those objects are. It does not restate those algorithms.

## Article 1 — Goal

A **Goal** is a durable result across chats. It states what shall be true when it is done, what is refused, which decisions are already made, and which Steps will get there.

A Goal is not a Step. Enforceable requirements that a check must witness live on Step records. The Goal states claims. The Steps prove them.

### 1.1 Goal types

Every Goal has exactly one type.

| Type | Meaning |
|------|---------|
| **Polishing** | Improve what already exists. No new product capability is the primary aim. |
| **Extension** | Add something that did not exist. New capability, surface, or shelf substance is the primary aim. |

Negotiation of each type is prescribed by the Goal flow. This Article only classifies.

### 1.2 Goal claims

A **claim** is a Goal-level statement that must hold when the Goal is `done`. Claims are checkboxes on the Goal. A claim is checked only when a witness exists. Chat does not check a claim.

### 1.3 Active Goal

The **Active Goal** is the single Goal marked current on the Goal index. Doors may name its id and must point at that index. They shall not keep a second copy of the Active Goal sentence.

## Article 2 — Step

A **Step** is one job under a Goal: agree the work, store the requirements, do the work, witness it, record it.

Only one Step shall be `in progress` at a time. That mark is the lock between chats.

### 2.1 Step types

Every Step has exactly one type.

| Type | Meaning |
|------|---------|
| **Iterative** | An aim is stated. The agent tweaks and improves. The Step continues until the human marks it complete. If the Step modified source, [correctness.md](correctness.md) for source modification must also hold before Accept. |
| **Modification** | The work was previously agreed. It is implemented in one pass under the stored requirements, then witnessed. |
| **Documentation** | Documents only. No source change. Correctness is protocol adherence under [correctness.md](correctness.md) Article 4. No new code test is required solely to search documents for phrases. |
| **Cleanup** | Remove dead matter, restore structure, or tidy organisation. It shall not introduce new product behaviour. Witness per the kind of files touched (source or document) under [correctness.md](correctness.md). |

### 2.2 Requirements

A **requirement** (functional requirement, FR) is an enforceable claim stored on the Step record after human approval. Chat is not a store of requirements. A requirement that cannot fail a witness is not a requirement.

### 2.3 Registry and record

| Object | Role |
|--------|------|
| **Registry** | The index of Steps. A row may exist before a record. |
| **Record** | The file that holds the approved job, type, requirements, and proof map. It exists only after the Step is approved and stored. |

## Article 3 — Accept, Witness, Seal, Safe point, Torn

| Term | Definition |
|------|------------|
| **Witness** | Evidence that a claim or requirement holds. For source-touching Steps: code tests mapped to requirements. For documentation Steps: adherence to the protocol instruments named in [correctness.md](correctness.md). For Iterative Steps: human completion mark, plus the applicable witness when source changed. |
| **Accept** | The applicable standard in [correctness.md](correctness.md) holds for the Step. A status line, commit message, or Goal paragraph does not Accept. The status line records an Accept that has already occurred. |
| **Accept commit** | The commit that seals an Accepted Step. Its subject begins `Accept F-<number>: `. It contains the Step's work and its `done` marks. It is made only by the Step flow, stage CLOSE. |
| **Seal commit** | The commit that seals paperwork the Goal flow wrote while no Step was `in progress`. Its subject begins `Seal: `. It contains only the files that Goal flow algorithm names. It is made only by the Goal flow, algorithm **Seal**. |
| **Safe point** | The newest commit on the current branch that is an Accept commit or a Seal commit. Rollback returns to it. |
| **Unsealed change** | A tracked file modified, added, or deleted, or an untracked file not ignored by the repository, that differs from the safe point; or a commit on the current branch after the safe point. |
| **Torn** | Any of: (a) a Step is marked `in progress` in any mark file; (b) a Step record holds requirements and its status is neither `done` nor `rolled back`; (c) an unsealed change exists while no Step is `in progress`. Torn work is not continued. Cases (a) and (b) are rolled back at once. Case (c) is rolled back only with the human's consent, because the change may be the human's own. |

## Article 4 — Status values

### 4.1 Goal status

| Value | Meaning |
|-------|---------|
| `not started` | Approved text may exist in draft negotiation only; the Goal is not active work |
| `in progress` | The Goal is active. Steps may run |
| `done` | Every planned Step is Accepted and every claim checkbox holds by witness |
| `abandoned` | The human ended the Goal without completing its claims |

### 4.2 Step status

| Value | Meaning |
|-------|---------|
| `not started` | Registry row may exist. No record, or no approval to implement |
| `in progress` | Requirements are stored. Work is underway. `done` is false |
| `done` | Accept holds. The Step was closed under the Step flow |
| `rolled back` | The attempt was discarded by returning to the safe point |

## Article 5 — Structural vocabulary

| Term | Definition |
|------|------------|
| **Shelf** | A top-level docs folder with one job: protocol, product, architecture, or paperwork |
| **Entrance** | The docs-root map. Not a fifth shelf of substance |
| **Door** | A `README.md` that introduces its folder. A door under `docs/` says what the folder is for, why it exists, and what each child is for. The door of a code folder is the deep dive into its code: its one job, its wiring, the member behind each step the paper describes, and what each file holds. Every folder has a door, except the kinds [quality.md](quality.md) 3.14 exempts |
| **Flow** | A bookkeeping algorithm: when, before, steps, done, not done |
| **Blueprint** | The mandatory shape of a document a flow writes, and the named operations by which that document is created and edited. A flow that writes a document names the blueprint and the operation |
| **Paperwork** | Progress records: Goals, Steps, decisions, changelog, roadmap, backlog |

## Article 6 — Session

A **session** file is not part of this protocol. Do not create one. The Active Goal and the Step marked `in progress` are the whole of “what we are doing now.”

## Article 7 — Authority of this instrument

Where chat and this instrument disagree, this instrument wins until the human approves an amendment under the Amendment flow. Where this instrument and [correctness.md](correctness.md) both speak to Accept, correctness determines Accept; this instrument defines the objects correctness judges.

## Exclusion

This instrument does not name product features, witness commands, or particular Goal or Step ids. It defines the terms and the Goal–Step system.
