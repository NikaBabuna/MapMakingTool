<!--
  File: docs/protocol/blueprints/step.md
  Purpose: Shape of a Step record
  Audience: Agents and humans
  Update when: The Step-record shape changes
-->

# Step record

**Kind:** Step record  
**Documents:** `docs/blockers/F-0xx.md` (for example `F-062.md`). The blockers door indexes them.

## Body

1. Goal id and status: `fr-approved`, then `in progress`, then `done` or `rolled back`.
2. The approved job, one paragraph.
3. Requirement table: id, measurable statement, status.
4. Test map: each requirement to a check. Empty until implementation.
5. Sync checklist for this Step.
6. Witness checklist.

## Must not

Be written before the human approves the job and the requirements. Be the only copy of a mechanism.
