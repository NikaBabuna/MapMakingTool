<!--
  File: docs/protocol/environment/quality.md
  Purpose: Standard of quality — attitude, structural hard rules, sync, conduct
  Audience: Agents
  Update when: A quality rule or the order of judgment changes
-->

# Quality

## Status

This instrument defines the standard of quality under the protocol. It binds every agent. It states the attitude with which work shall be approached, and the hard rules by which finished work is judged.

Work that fails this standard is of poor quality, even if a witness later appears green under [correctness.md](correctness.md).

This instrument is product-independent. It does not name a domain, a language, or a build tool. It defines structure, role, sync, and conduct.

## Article 1 — Aim

The agent’s duty is not merely to discharge a task. The agent shall organise the result. Organisation is part of the work.

The agent shall prefer clear structure: one job in one place, a reason a later agent can read from the layout itself, and no scattering of the same concern across unrelated homes.

## Article 2 — Order of judgment

Quality is judged in this order. A later head does not excuse failure of an earlier head.

| Order | Head | Rule |
|------:|------|------|
| 1 | Completeness | The objective of the approved Step shall be complete. Completeness is determined solely by [correctness.md](correctness.md). |
| 2 | Structure | The result shall be neatly organised under the hard rules in Article 3. |
| 3 | Synchronisation | Documents and source shall remain a single prior, under Article 4. |
| 4 | Conduct | The protocol shall have been followed to the letter, under Article 5. |

## Article 3 — Structural hard rules

Other shelves may hold product-specific content. Their structure and their role are not product-specific. Source likewise has structure and organisation; those are not optional. The following rules are mandatory.

### 3.A Documents and shelves

| # | Rule |
|---|------|
| 3.1 | The folder in which a file sits is that file’s job. A file shall not perform the job of another folder. |
| 3.2 | Shelves have fixed roles: protocol is conduct; product is what the product is for a person; architecture is what is built; paperwork is who did what and when; scope is what the product includes and refuses. An entrance maps shelves; it is not a shelf of substance. |
| 3.3 | Conduct, concept, machine, and log shall not be mixed on one shelf. |
| 3.4 | A parent document names and points to the finer document. The procedure lives on the finer document. A parent shall not duplicate a child’s procedure. |
| 3.5 | Every folder an agent is expected to open shall have a door: a `README.md` in that folder. The shape of that door is prescribed by [../blueprints/doors/folder-door.md](../blueprints/doors/folder-door.md). A folder without such a door is forbidden. |
| 3.6 | Every document an agent is expected to open shall be reachable from a door. Orphan pages are forbidden. |
| 3.7 | A structural choice among documents shall be intelligible from the tree and the doors. The agent shall not rely on chat memory to explain why a document lives where it lives. |

### 3.B Source

| # | Rule |
|---|------|
| 3.8 | Source shall be organised with the same duty of clarity as documents. A type, package, or module shall have one job. Unrelated concerns shall not share a home. |
| 3.9 | Dependency direction shall be deliberate and readable from the layout. A lower layer shall not depend on a higher layer. |
| 3.10 | The agent shall not leave source in a dump: unexplained folders, placeholder packages, or files whose purpose is not apparent from name and place. |
| 3.11 | A structural choice in source shall be intelligible from the tree and from the documents that describe that tree. Chat memory is not an explanation. |

### 3.C Scope of edits

| # | Rule |
|---|------|
| 3.12 | The agent shall touch only the files the approved job and the active flow name. Drive-by edits are poor quality. |

### 3.D Independence of the protocol

| # | Rule |
|---|------|
| 3.13 | Every page under `docs/protocol/` is project-independent. It states the mechanism of the protocol, and names no product, domain, programming language, build tool, framework, module, code path, architecture area, or record id of the project it governs. The protocol may name the paths of its own shelves, doors, and paperwork, because those are part of the mechanism. A protocol rule that depends on a project fact names the project page and line that states the fact (for example, the project-fact lines of `docs/architecture/program.md`, or the **Heavy places** of `docs/navigation.md`), and never the fact itself. Examples in protocol pages use neutral placeholders or invented names. |

## Article 4 — Synchronisation

Every Step requires document synchronisation. Source that leaves the documents describing it false is of poor quality.

Which documents change, when they change, and the minimum set of files to edit, are prescribed by the flows under [flows/](../flows/). This Article establishes the duty. The flows establish the procedure.

## Article 5 — Conduct as quality

Work performed outside a lawful flow, without a stored requirement, by an invented write sequence, or in breach of [brief.md](../brief.md), [style.md](style.md), [core-workflow.md](../core-workflow.md), or [correctness.md](correctness.md), is of poor quality.

Compliance with the protocol is not optional courtesy. It is a quality criterion.

## Article 6 — Tools of judgment

| Question | Instrument |
|----------|------------|
| Is the Step complete and Acceptable? | [correctness.md](correctness.md) |
| Was the turn and the write sequence lawful? | [core-workflow.md](../core-workflow.md) and [flows/](../flows/) |
| Does the document have the required shape? | The blueprint for that document, found from [../blueprints/README.md](../blueprints/README.md), and its **Check** section |
| Is communication lawful? | [style.md](style.md) |
| Is the organisation clear and role-correct? | This instrument, Articles 1 and 3 |

## Exclusion

This instrument does not list product features, module names, or witness commands. Those belong to the project. This instrument defines how the agent shall organise and judge work in any repository under this protocol.
