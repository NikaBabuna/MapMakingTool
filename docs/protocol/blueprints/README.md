<!--
  File: docs/protocol/blueprints/README.md
  Purpose: Door to the blueprints — which group shapes which files
  Audience: Agents
  Update when: A blueprint group is added or removed, or a group starts shaping different files
-->

# Blueprints

The exact shape of every document the protocol writes, and the named operations that create and edit it. A flow says when to write and which operation to apply. The blueprint says exactly what that edit is.

**Why:** The shapes of documents are kept apart from the flows that write them, so a flow names an operation and never restates a shape. One blueprint per kind of document belongs here, grouped by the shelf of the documents it shapes. When and in what order to write belongs to the flows.

| Page | Read it when |
|------|----------------|
| [protocol/](protocol/README.md) | You are writing a protocol instrument, a flow page, a navigation page, or a blueprint |
| [doors/](doors/README.md) | You are writing a folder door, `docs/README.md`, `docs/navigation.md`, `AGENTS.md`, `.cursor/rules/protocol.mdc`, the repository `README.md`, or the Goal pointer on those doors |
| [headers/](headers/README.md) | You are writing the comment at the top of a document or a source file |
| [paperwork/](paperwork/README.md) | You are writing a file under `docs/paperwork/`, or need the next Goal, Step, or ADR id |
| [product/](product/README.md) | You are writing a file under `docs/product/` |
| [architecture/](architecture/README.md) | You are writing a file under `docs/architecture/`, or need the witness command line |
| [messages/](messages/README.md) | You are delivering the startup Status report |
