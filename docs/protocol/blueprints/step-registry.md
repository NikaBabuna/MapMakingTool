<!--
  File: docs/protocol/blueprints/step-registry.md
  Purpose: Shape of the step registry
  Audience: Agents and humans
  Update when: The step-registry shape changes
-->

# Step registry

**Kind:** step index  
**Document:** `docs/project/features.md`

## Body

Sections per Goal. Each row: id, name, status, link to the Step record. Status is `not started`, `in progress`, `done`, or `rolled back`. A `not started` row has no Step record yet.

## Must not

Hold the requirement text. That lives in the Step record.
