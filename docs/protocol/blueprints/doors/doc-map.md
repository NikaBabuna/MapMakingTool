<!--
  File: docs/protocol/blueprints/doors/doc-map.md
  Purpose: Shape of the repository map (docs/navigation.md), and the operations that edit it
  Audience: Agents
  Update when: The map shape or its operations change
-->

# Doc map

**Shapes:** `docs/navigation.md`  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** the docs index ([docs-index.md](docs-index.md)) — shelves only. A folder door ([folder-door.md](folder-door.md)) — one folder only. The navigation room (`docs/protocol/navigation/`) — *how* to read. The map says *where* things are, and which places are expensive.

The map of what exists. An agent who knows a page's name but not its path finds its row here and opens that one file. It also names the code doors, and the heavy places that must not be read whole, so that the navigation room can stay free of paths particular to this repository.

## Skeleton

```markdown
<!--
  File: docs/navigation.md
  Purpose: Documentation map — authoritative index of where things are, and which places are expensive to read
  Audience: Agents and humans
  Update when: Any doc or folder is added, moved, or removed, or a place becomes heavy
-->

# Navigation

**Phase:** <alpha | beta | prod> ([protocol/environment/phase.md](protocol/environment/phase.md))  
**Goal index:** [paperwork/goals.md](paperwork/goals.md) — <goal pointer>  
**Protocol:** [protocol/README.md](protocol/README.md) · begin at [protocol/brief.md](protocol/brief.md)  
**Reading:** [protocol/navigation/README.md](protocol/navigation/README.md) — how to read this map, and the repository, without filling your context

Folder indexes are **README.md** in each landmark directory. Prefer those links when entering a folder.

---

## Entry points

| Audience | Start |
|----------|-------|
| Agents | [../AGENTS.md](../AGENTS.md) |
| Humans | [../README.md](../README.md) |
| Docs tree | [README.md](README.md) |
| Protocol | [protocol/brief.md](protocol/brief.md) |
| Flows | [protocol/flows/README.md](protocol/flows/README.md) |
| Reading efficiently | [protocol/navigation/README.md](protocol/navigation/README.md) |

---

## <Shelf name> (`docs/<folder>/`)

**Folder:** [<folder>/README.md](<folder>/README.md)

| Page | Status |
|------|--------|
| [<page>.md](<folder>/<page>.md) | Active — <what it is for> |
| [<subfolder>/](<folder>/<subfolder>/README.md) | Active — <what it holds> |
…

---
…

## Code

Enter code through the door of its folder, or through the paper page that describes it ([protocol/navigation/code.md](protocol/navigation/code.md)). Do not list source trees.

| Folder | Door | What it holds | Described by |
|--------|------|---------------|--------------|
| `<folder>/` | [../<folder>/README.md](../<folder>/README.md) | <one sentence> | <architecture page, or "Its door"> |
…

---

## Heavy places

Reading any of these whole fills a context window with little of value. The rules for reading them are in [protocol/navigation/bounds.md](protocol/navigation/bounds.md). Sizes are approximate.

### Never open

| Path | What it is | Size | Instead |
|------|------------|------|---------|
| `<path>` | <what it is> | ~<n> files | <what to read instead> |
…

### Open narrowly

| Path | What it is | Size | How to read it |
|------|------------|------|----------------|
| `<path>` | <what it is> | ~<n> lines | <search what, read which range> |
…
```

Shelf sections come in the order of the shelf table in `docs/README.md`, each followed by `---`. **Code** and **Heavy places** come after the last shelf.

## Parts

| Part | Required | Rule |
|------|----------|------|
| Header block | yes | Four lines, **Phase**, **Goal index**, **Protocol**, **Reading**, each ending in two spaces except the last. **Phase** equals the `**Current phase:**` value in `phase.md`. **Goal index** carries the pointer of [goal-pointer.md](goal-pointer.md) |
| Door sentence | yes | As in the Skeleton |
| Entry points | yes | Exactly the six rows of the Skeleton |
| Shelf section | yes, one per shelf | Title `## <Shelf name> (\`docs/<folder>/\`)`, then `**Folder:**` linking the shelf door, then one table with columns `Page` and `Status` |
| Shelf table rows | yes | One row for every `.md` page directly in the shelf folder (except its door), and one row for every subfolder, linking its door. Deeper pages may be listed. Numbered records are never listed. Their index is |
| Status cell | yes | `Active — <fact>`, `Draft — <fact>`, or `Historical — <fact>`. The fact says what the page is for, or why a reader should not rely on it. It is not a changelog |
| Code | yes | One row per code folder that has a door: every module in `docs/architecture/program.md`, every front-end or shell folder with its own door, and every tool folder at the repository root with a door |
| Never open | yes | One row per folder that holds installed dependencies, build output, version-control internals, or local tools, whenever it exists on disk. The file count is approximate |
| Open narrowly | yes | One row per tracked text file of more than 500 lines, per folder of generated test data, and per folder of numbered records or of protocol pages an agent must enter by one file. **How to read it** names the search, and what range to read |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add row** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3; [../../flows/step.md](../../flows/step.md) SYNC (Entrance) |
| **Remove row** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3; [../../flows/step.md](../../flows/step.md) SYNC (Entrance) |
| **Relink row** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |
| **Correct status** | [../../flows/step.md](../../flows/step.md) SYNC (Entrance); [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 5 |
| **Add shelf section** | [../../flows/amendment.md](../../flows/amendment.md) Step 6.3 |
| **Add code folder** | [../../flows/step.md](../../flows/step.md) SYNC (Entrance), when a Step adds a module or a code folder with a door |
| **Add heavy place** | [../../flows/step.md](../../flows/step.md) SYNC (Entrance), when a Step creates a generated folder or grows a tracked text file past 500 lines; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 13 |
| **Remove heavy place** | [../../flows/step.md](../../flows/step.md) SYNC (Entrance), when a heavy place is deleted or shrinks to 500 lines or fewer; [../../flows/global-docsync.md](../../flows/global-docsync.md) Step 13 |
| **Set goal pointer** | **Set pointer**, **Clear pointer**, and **Correct pointer** of [goal-pointer.md](goal-pointer.md) |
| **Set phase** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when `phase.md` changes phase |

### Add row

**Before.** A standing page or subfolder was created on a shelf.

**Edit.**

1. In that shelf's section, add `| [<page>.md](<folder>/<page>.md) | Active — <what it is for> |`, or for a subfolder `| [<sub>/](<folder>/<sub>/README.md) | Active — <what it holds> |`.

### Remove row

**Before.** The page or subfolder was deleted or moved off the shelf.

**Edit.**

1. Delete its row.

### Relink row

**Edit.**

1. Replace the link text and target in that row with the new name and path.

### Correct status

**Before.** A Status cell states something the tree or the paperwork proves false.

**Edit.**

1. Replace the fact after `— ` with a true one. Keep the leading word unless the page's standing changed.

### Add shelf section

**Before.** A folder was created directly under `docs/`.

**Edit.**

1. Insert a section in the Skeleton shape, at the position of that shelf in `docs/README.md`'s shelf table, with `---` between it and its neighbours.

### Add code folder

**Before.** The folder has its own `README.md` door.

**Edit.**

1. Add to **Code**: `| \`<folder>/\` | [../<folder>/README.md](../<folder>/README.md) | <what it holds, in one sentence> | <the architecture page that describes it, or "Its door"> |`, in the order of `docs/architecture/program.md` for modules, and after the modules for other folders.

### Add heavy place

**Edit.**

1. For a folder of dependencies, build output, version-control internals, or local tools, add to **Never open**: `| \`<path>/\` | <what it is> | ~<n> files | <what to read instead> |`.  
2. For a large file, a folder of generated test data, or a folder an agent must enter one file at a time, add to **Open narrowly**: `| \`<path>\` | <what it is> | ~<n> lines | <search what, then read which range> |`.  
3. Measure the size when you add the row: count files for a folder, lines for a file. Round to two significant figures.

### Remove heavy place

**Edit.**

1. Delete its row.

### Set goal pointer

**Edit.** Apply **Set pointer** or **Clear pointer** of [goal-pointer.md](goal-pointer.md) to the **Goal index** line.

### Set phase

**Edit.**

1. Replace the value after `**Phase:** ` with the new phase word.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every link resolves |
| 2 | Every shelf has a section, and every page directly on a shelf and every subfolder of a shelf has a row |
| 3 | No Status cell is false against the tree |
| 4 | The **Phase** and **Goal index** lines agree with `phase.md` and with [goal-pointer.md](goal-pointer.md) |
| 5 | Every code folder with a door appears under **Code** |
| 6 | Every existing folder of dependencies, build output, version-control internals, or local tools appears under **Never open** |
| 7 | Every tracked text file of more than 500 lines appears under **Open narrowly** |

## Keep out

- How to read, search, and budget: the navigation room.  
- The text of a mechanism: architecture pages.  
- A blueprint's skeleton: blueprints.  
- The `**Active Goal:**` sentence.
