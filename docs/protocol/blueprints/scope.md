<!--
  File: docs/protocol/blueprints/scope.md
  Purpose: Shape of the scope document
  Audience: Agents and humans
  Update when: The scope shape changes
-->

# Scope

This is `docs/project/project.md`. It is the hard bound on what the project is. Protocol says this document binds. Protocol does not restate the product. If an agent wants to build something this file does not list, the agent does not build it and apologize later. The **Scope** flow edits this file first, with the human.

**Write or edit it when.** **Scope** runs.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Name, repository, phase | Identity, so the file is not an anonymous list |
| One line | The product in a sentence a stranger can repeat |
| In scope | A table. Area and what that area includes. Work outside the table is not allowed |
| Out of scope | Explicit refusals. The useful rows are the ones someone will otherwise assume are included |
| Stack | Choices already made, each pointing at the ADR that accepted it. This section is a record of decisions, not a conduct rule |
| Expansion | One sentence: changes land here first, and technical ones also get an ADR |

## Skeleton

```markdown
# Project scope

**Product name:** <name>
**Phase:** alpha

## One line

<sentence>

## In scope

| Area | Description |
|------|-------------|
| <area> | <what is included> |

## Out of scope (for now)

- <exclusion>

## Stack (intent)

| Choice | Status |
|--------|--------|
| <choice> | Accepted — see ADR-0xx |

## Expansion

Scope changes belong here first. Technical changes also get an ADR.
```

## Keep out

How a system works. Step status. The protocol rules themselves.
