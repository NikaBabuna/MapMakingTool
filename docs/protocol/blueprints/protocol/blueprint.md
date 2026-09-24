<!--
  File: docs/protocol/blueprints/protocol/blueprint.md
  Purpose: Shape of a blueprint page, and the operations that create and change one
  Audience: Agents
  Update when: The parts every blueprint must have, or the placeholder conventions, change
-->

# Blueprint page

**Shapes:** every `docs/protocol/blueprints/<group>/<kind>.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a flow page ([flow.md](flow.md)) says *when* to write and in what order. A blueprint says *exactly what* the written file looks like, and exactly what each edit changes.

A blueprint is the form for one kind of file. A reader who has never seen that kind must be able to create a legal file, and make every edit a flow asks for, from the blueprint alone, without looking at an existing example.

## Placeholder conventions

Every blueprint uses these, and only these.

| Placeholder | Meaning |
|-------------|---------|
| `<words>` | Replace with the thing described, including the angle brackets |
| `G-0xx`, `F-0xx`, `ADR-0xx` | An id: the prefix, a hyphen, and a three-digit number. Numbering starts at 001 |
| `G-0yy`, `F-0yy` | A second, different id of the same kind |
| `<slug>` | The slug of a name, by the rule in [../paperwork/goal.md](../paperwork/goal.md) (Naming) |
| `YYYY-MM-DD` | Today's date, as a calendar date |
| `<n>`, `<m>`, `<c>` | Whole numbers |
| `a \| b` inside a cell | Exactly one of the listed values |

## Skeleton

````markdown
<!--
  File: docs/protocol/blueprints/<group>/<kind>.md
  Purpose: Shape of <the file> and the operations that write it
  Audience: Agents
  Update when: The shape or an operation of <the file> changes
-->

# <Kind>

**Shapes:** `<exact path>` | every `<path pattern>`  
**Register:** <legal | understanding> · **Human-facing:** <yes | no>  
**Header:** <[document header](../headers/document-header.md) | [source header](../headers/source-header.md) | none — <reason>>  
**Neighbours:** <kind> ([<file>](<link>)) — <how it differs>

<One paragraph: what this file is for, who reads it, and why it is its own kind.>

## Skeleton

```markdown
<the complete file, from its first line to its last, with placeholders>
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| <part> | yes \| no \| when <condition> | <exact format, allowed values, order> |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **<Operation>** | [<flow>](../../flows/<flow>.md) <algorithm or step> |

### <Operation>

**Before.** <what must already be true of the file>

**Edit.**

1. <where in the file, exactly> — <what to write, in a code span or block>

**Result.** <what is true of the file afterwards>

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | <condition a reader can verify by looking> |

## Keep out

- <content that belongs in another file, and which file>
````

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header comment | yes | Per [../headers/document-header.md](../headers/document-header.md). `Purpose` begins `Shape of`. |
| Title | yes | `# <Kind>`: the name of the kind of file, not of one file |
| Key block | yes | Four lines, in this order: **Shapes**, **Register** with **Human-facing**, **Header**, **Neighbours**. **Shapes** gives the exact path, or the path pattern with every exception named. **Neighbours** names at least one kind that is easily confused with this one |
| Opening paragraph | yes | One paragraph. No procedure |
| Placeholder conventions | only in this page | Other blueprints use them and do not restate them |
| Skeleton | yes | The complete file, first line to last, in a fence of four backticks when the file itself contains a fence. A new file is created by copying it and replacing placeholders. Sections that may repeat are shown once, followed by `…` on its own line |
| Parts | yes | One row per part of the Skeleton, in Skeleton order. **Required** is `yes`, `no`, or `when <condition>`. **Rule** gives the exact format and every allowed value |
| Operations index | yes | One row per operation, naming every flow step, or every operation of another blueprint, that invokes it. An operation that nothing invokes is removed |
| Operation sections | yes | One `### <Operation>` per row of the index, with **Before**, **Edit**, **Result**. Each **Edit** item says where (section, and top or bottom), and gives the exact text of every line or row written |
| Check | yes | Conditions [../../flows/global-docsync.md](../../flows/global-docsync.md) and [../../flows/audit.md](../../flows/audit.md) can verify by reading the file |
| Keep out | yes | Each item names the file where that content belongs |

## Operation rules

1. An operation edits exactly one file: the file this blueprint shapes. An edit to a second file is a separate operation in that file's blueprint.  
2. An operation never decides whether it should run. The flow decides. The operation says what the edit is.  
3. An operation never commits. Commits are made only by [../../flows/step.md](../../flows/step.md) CLOSE and by [../../flows/goal.md](../../flows/goal.md) **Seal**.  
4. Every line or row an operation writes is given in full, with placeholders. "Update the table" is not an operation.  
5. An operation that replaces text quotes what it replaces, or gives a rule that finds it exactly (e.g. "the line that begins `**Active Goal:**`").  
6. Operation names are unique within a blueprint. A flow cites an operation as `Apply **<Operation>** of [<blueprint>](<link>).`

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create blueprint** | [../../flows/amendment.md](../../flows/amendment.md) Step 5, when a new kind of file is introduced |
| **Add operation** | [../../flows/amendment.md](../../flows/amendment.md) Step 5, when a flow needs an edit the blueprint does not yet define |
| **Change part** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 |

### Create blueprint

**Before.** The kind of file is not shaped by any existing blueprint. The group it belongs to is the shelf or kind of the file it shapes.

**Edit.**

1. Create `docs/protocol/blueprints/<group>/<kind>.md` by copying the Skeleton above and replacing every placeholder.  
2. The Operations index lists every flow step that will write the file. Each of those flow steps must cite the operation (Amendment Step 8).

**Result.** The group door gains a row, by **Add child** of [../doors/folder-door.md](../doors/folder-door.md).

### Add operation

**Before.** A flow step writes this file in a way no operation describes.

**Edit.**

1. Add a row at the bottom of the Operations index: `| **<Operation>** | [<flow>](../../flows/<flow>.md) <step> |`.  
2. Add a `### <Operation>` section at the end of the operation sections, with **Before**, **Edit**, **Result**.  
3. If the operation writes a line format not yet in **Parts**, add it to the Rule cell of that part.

**Result.** The flow step can cite the operation by name.

### Change part

**Before.** The approved change is known, word for word.

**Edit.**

1. Change the Skeleton, the Parts row, and every operation that writes that part, together, so that all three agree.  
2. Change the Check rows that test that part.

**Result.** Skeleton, Parts, Operations, and Check describe the same file.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | It has every part in the Parts table above, in that order |
| 2 | The Skeleton is a complete file: header comment first, nothing omitted except repeatable sections marked `…` |
| 3 | Every Operations index row has a `###` section, and every `###` section has a row |
| 4 | Every flow named in the Operations index exists and cites that operation |
| 5 | No operation edits a second file, commits, or decides whether to run |
| 6 | Nothing in it names the project it governs ([../../environment/quality.md](../../environment/quality.md) 3.13). Skeletons and examples use placeholders or invented names |

## Keep out

- When to write, and in what order relative to other files: the flow page under `docs/protocol/flows/`.  
- The meaning of Goal, Step, Accept, and the other defined terms: [../../environment/core-definition.md](../../environment/core-definition.md).  
- Examples copied from a real file, which go stale: the real file is its own example.
