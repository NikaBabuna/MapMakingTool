<!--
  File: docs/protocol/blueprints/cursor-rule.md
  Purpose: Shape of the editor door
  Audience: Agents and humans
  Update when: The editor door shape changes
-->

# Editor door

**Kind:** editor door  
**Document:** `.cursor/rules/protocol.mdc`

## Body

1. The editor front matter that marks the rule always applied.
2. A pointer to `docs/protocol/README.md`.
3. A pointer to the goal index, naming the active Goal by id.
4. One line: docs win; requirements are stored before implementation; a torn Step rolls back.

## Must not

Paste the rooms, the flows, or an Active Goal sentence.
