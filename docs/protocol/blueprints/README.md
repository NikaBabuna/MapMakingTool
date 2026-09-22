<!--
  File: docs/protocol/blueprints/readme.md
  Purpose: Shape of a folder door
  Audience: Agents and humans
  Update when: The door shape changes
-->

# Folder door

A door is the `README.md` inside a folder an agent is expected to open. Its only job is to stop the agent reading every file in the folder to learn what they are. If the door is an essay, it has failed, and the agent will skip it and read the children anyway.

**Files.** Every landmark `README.md`. Examples that exist today: `docs/protocol/README.md`, `docs/protocol/environment/README.md`, `docs/project/README.md`, `docs/project/goals/README.md`, `docs/engine/README.md`, `docs/engine/specs/README.md`, `docs/product/README.md`, `docs/product/wiki/README.md`, `docs/blockers/README.md`, and the doors of the code modules, `.github/`, and `.cursor/`. The entrance lists any the table missed. `docs/README.md` itself is the docs index, not this blueprint.

**Write or edit it when.** A landmark folder is created, or a child is added or removed.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| First sentence | What this folder is, in the reader’s vocabulary. Not a history of how it got here |
| Table | One row per child: the link, and the question that child answers |
| Nothing else | Status history, Step ids, and rules belong in the child that owns them |

## Skeleton

```markdown
# <Folder name>

<One sentence: what this folder is for.>

| Page | Read it when |
|------|----------------|
| [child.md](child.md) | <the question this child answers> |
```

## Keep out

The Active Goal sentence. A restatement of a flow. A list of what is inside other folders.
