<!--
  File: docs/protocol/blueprints/protocol/flow.md
  Purpose: Shape of a flow page, the rule that document writes cite blueprint operations, and the operations that edit a flow page
  Audience: Agents
  Update when: The flow-page shape, the citation rule, or an operation changes
-->

# Flow page

**Shapes:** every `docs/protocol/flows/<flow>.md` except `docs/protocol/flows/README.md` (a folder door)  
**Register:** legal; its human-message skeletons are in the understanding register · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md), with `Audience: Agents`  
**Neighbours:** a blueprint ([blueprint.md](blueprint.md)) — exactly what a written file looks like. A flow says when to write, which file, in what order, and how to know it is done. An instrument ([instrument.md](instrument.md)) — the law a flow obeys.

The complete algorithm for one situation named in Article 3 of [../../core-workflow.md](../../core-workflow.md). It is an algorithm, not an essay: every step says what to do, to which file, and what happens if a condition fails.

## Write rule

Every step of a flow that creates or edits a document cites the operation that makes the edit, in this form:

`Apply **<Operation>** of [<blueprint>](../blueprints/<group>/<kind>.md).`

A flow never states a document's line format itself. The blueprint does. A flow may state the format of a commit subject or a chat message, because those are not documents.

## Skeleton

Form A, one algorithm per page:

````markdown
<!--
  File: docs/protocol/flows/<flow>.md
  Purpose: <what situation this flow handles>
  Audience: Agents
  Update when: <what change to the procedure changes this page>
-->

# <Flow name>

**Register:** legal. <Which messages use the understanding register.>  
**Writes:** <the files it writes, by blueprint | none>  
**Container:** <the Step or commit that seals its writes | none — it writes nothing>  
**Definitions:** [../environment/core-definition.md](../environment/core-definition.md)<, and other instruments it relies on>

## When

1. <observation that makes this flow the one to run>
…

## Before

| # | Condition | If it fails |
|---|-----------|-------------|
| B1 | <precondition> | <exact action> |
…

## Procedure (mandatory order)

### Step 1 — <Title>

1. <action>. <For a document write:> Apply **<Operation>** of [<blueprint>](../blueprints/<group>/<kind>.md).
…

…

## Human message — <name>

```markdown
<the message skeleton, ending with **Need from you:**>
```

## Done

- <observable result>
…

## Not done

- <false finish>
…
````

Form B, several algorithms on one page (e.g. the Goal flow and the Step flow): the key block, then one `## <Algorithm>` section per algorithm, separated by `---`. Each section has **When.**, **Before.** (the table), **Steps.** (numbered), **Done.**, and **Not done.**, in that order.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Key block | yes | **Register**, **Writes**, then any of **Container**, **Definitions**, **Depends on**, **Hands off to**, **Shape**. **Writes** names every file kind the flow writes, or `none` |
| When | yes | Numbered observations. No "when needed" |
| Before | yes | A table. Every row has an exact action in **If it fails** |
| Procedure | yes | `### Step <n> — <Title>` with numbered actions, or Form B sections. Every document write cites an operation (**Write rule**) |
| Human message | when the flow speaks to the human | A skeleton in a fence. Every suggestion carries a confidence label. Ends with **Need from you:** |
| Done | yes | Observable results |
| Not done | yes | The false finishes this flow is most likely to produce |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create flow** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 and Step 8, when the human approves a new named flow |
| **Amend step** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Add step** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |

### Create flow

**Edit.**

1. Copy the Skeleton of Form A or B, and fill every part.  
2. The flows door gains a row by **Add child** of [../doors/folder-door.md](../doors/folder-door.md), and [../../core-workflow.md](../../core-workflow.md) Article 3 gains a row (Amendment Step 8).

### Amend step

**Edit.**

1. Replace the approved old text of the step with the approved new text.  
2. Search `docs/protocol/` for citations of that step (e.g. `amendment.md` Step 6) and confirm they still hold.

### Add step

**Edit.**

1. Insert `### Step <n> — <Title>` at the approved position.  
2. If that changes the numbers of later steps, search `docs/protocol/` for every citation of those later numbers and update each one in the same edit.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has every required part, in order |
| 2 | Every document write in the Procedure cites an operation that exists in the named blueprint |
| 3 | Every Before row has an action |
| 4 | Every link resolves |

## Keep out

- A second catalogue of flows: [../../core-workflow.md](../../core-workflow.md).  
- Document line formats: blueprints.  
- Product behaviour, and the witness command itself: the command is taken from `docs/architecture/program.md`.
