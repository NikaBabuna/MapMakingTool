<!--
  File: docs/protocol/blueprints/protocol/navigation-page.md
  Purpose: Shape of the pages of the navigation room (reading, bounds, pointers, code, walks), and the operations that edit them
  Audience: Agents
  Update when: A navigation page's shape, or an operation, changes
-->

# Navigation page

**Shapes:** `docs/protocol/navigation/reading.md`, `bounds.md`, `pointers.md`, `code.md`, and `walks.md`. The room's `README.md` is a folder door ([../doors/folder-door.md](../doors/folder-door.md)).  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md), with `Audience: Agents`  
**Neighbours:** the doc map ([../doors/doc-map.md](../doors/doc-map.md)) — *where* things are in this repository, including its heavy places. A navigation page — *how* to find and read them cheaply. A flow page ([flow.md](flow.md)) — what to write.

These pages keep an agent's context small: they say how to read part of a file, what each kind of turn may open, which page answers which question, how to reach source through the paper, and what such a read costs. They never tell the agent to edit a file. A step that edits belongs in a flow. Paths particular to this repository's code (heavy folders, large files) live in the doc map, not here.

## Skeleton

`reading.md`:

```markdown
<!--
  File: docs/protocol/navigation/reading.md
  Purpose: How to take only the needed part of a file, so the context window holds answers and not whole files
  Audience: Agents
  Update when: A reading technique, a size threshold, or a search pattern changes
-->

# Reading

<Why reads are costly, and the aim. Pointers to bounds, pointers, and the map.>

## The reading ladder

| Rung | Action | What enters your context | Use it when |
|-----:|--------|--------------------------|-------------|
| <n> | <action> | <cost> | <condition> |
…

## Size rules

| File size | First contact | Then |
|-----------|---------------|------|
| <range> | <what to read first> | <what next> |
…

## Reading rules

| # | Rule |
|---|------|
| R<n> | <rule> |
…

## Search patterns

| To find | Search for | In | You get |
|---------|------------|----|---------|
| <target> | <pattern> | <narrowest place> | <what comes back> |
…

## Keep out of your context

- <thing>
```

`bounds.md`:

```markdown
<!--
  File: docs/protocol/navigation/bounds.md
  Purpose: What to open for each kind of turn, what to leave closed, and when to stop reading
  Audience: Agents
  Update when: A flow's open set, the startup read set, or a stop signal changes
-->

# Bounds

<The two failure modes, in one or two sentences.>

**Rule.** <the open-set rule in one sentence>

## Startup read set

<One sentence tying it to core-workflow Article 1, with its approximate size.>

| Order | Open | How |
|------:|------|-----|
| <n> | <page> | <whole \| range \| search, and when> |
…

**Leave closed at startup:** <list>

## Open set by flow

| Flow | Open | May also open, only when | Leave closed |
|------|------|--------------------------|--------------|
| <flow> | <pages> | <page, and the condition> | <pages> |
…

## Always closed

| Class | Why | Use instead |
|-------|-----|-------------|
| <class> | <reason> | <cheaper read> |
…

## Stop signals

| # | Signal | Do |
|---|--------|----|
| S<n> | <observable signal> | <action> |
…
```

`pointers.md`:

```markdown
<!--
  File: docs/protocol/navigation/pointers.md
  Purpose: For each common question, the one page that answers it, and when to stop
  Audience: Agents
  Update when: A standing pointer path changes, or a common question gains a home
-->

# Pointers

<One paragraph: open the page, read by the ladder, stop when the last column holds; what to do when the question is missing.>

## <Area: Conduct | Paperwork | Scope and product | Architecture and code | Repository state>

| Question | Open | You are done when |
|----------|------|-------------------|
| <question an agent asks> | <the one page, section, or search> | <the observable stopping point> |
…
```

`code.md`:

```markdown
<!--
  File: docs/protocol/navigation/code.md
  Purpose: How to reach the one piece of source that matters, through the implementation paper, without reading modules
  Audience: Agents
  Update when: The route from the paper to source, or a code-reading rule, changes
-->

# Code

<Why source is the costliest read, and what the paper is for.>

## The route

| Step | Open | Take |
|-----:|------|------|
| <n> | <page or file> | <what to take from it> |
…

## Tests

| To find | Do |
|---------|----|
| <target> | <action> |
…

## Callers and uses

1. <step>
…

## Rules

| # | Rule |
|---|------|
| C<n> | <rule> |
…
```

`walks.md`:

```markdown
<!--
  File: docs/protocol/navigation/walks.md
  Purpose: Worked examples of reading this repository at low cost, one question at a time
  Audience: Agents
  Update when: A walked path changes (a page moves, a door changes), or a new common walk is added
-->

# Walks

<One paragraph: what a walk is, and that counts are approximate.>

## W<n> — <the question, as an agent would ask it>

| # | Action | Reads |
|--:|--------|------:|
| <n> | <action> | <~lines> |
…

**Total:** <about n lines, compared with the cost of the naive read>.
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Opening | yes | Says what the page is for, and points at its sibling pages and at the doc map |
| Numbered rules | reading, bounds, code | Rules are numbered `R<n>`, `S<n>`, `C<n>`. A number is never reused or renumbered, because other pages cite it (e.g. "R10 of reading.md") |
| Pointer rows | pointers | Each row names one page, section, or search, and a stopping point a reader can observe |
| Open-set rows | bounds | One row per flow named in Article 3 of `docs/protocol/core-workflow.md` |
| Walk | walks | One real question. Each action is a rung of the ladder. The total compares against the naive read |
| Edit instructions | never | A navigation page never tells the agent to change a file |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add pointer** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Relink pointer** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3 |
| **Add open-set row** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when a flow is added |
| **Amend open set** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when a flow's pages or the startup sequence change |
| **Add bound** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Add reading rule** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Add search pattern** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Add walk** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Relink walk** | [../../flows/amendment.md](../../flows/amendment.md) Step 5.3, when a page a walk passes through moves |
| **Amend section** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1), when the human approves new text for one section of a navigation page, such as a route, a rules table, or a walk |

### Add pointer

**Edit.**

1. In `pointers.md`, in the section of the question's area, add `| <question> | <page, section, or search> | <stopping point> |`.

### Relink pointer

**Edit.**

1. Replace the moved path in that row's **Open** cell.

### Add open-set row

**Edit.**

1. In `bounds.md`, **Open set by flow**, add `| <Flow> | <pages> | <page, and when> | <pages> |` in the order of Article 3 of `docs/protocol/core-workflow.md`.

### Amend open set

**Edit.**

1. Replace the cells of that flow's row, or the rows of **Startup read set**, with the pages the flow or startup sequence now reads. Update the approximate size in the sentence above the startup table.

### Add bound

**Edit.**

1. In `bounds.md`, add a row to **Always closed** (`| <class> | <why> | <use instead> |`) or to **Stop signals** (`| S<next> | <signal> | <do> |`).

### Add reading rule

**Edit.**

1. In `reading.md`, add `| R<next> | <rule> |` at the bottom of **Reading rules**. In `code.md`, a code rule is `| C<next> | <rule> |`.

### Add search pattern

**Edit.**

1. In `reading.md`, add `| <to find> | <pattern> | <narrowest place> | <what comes back> |` to **Search patterns**.

### Add walk

**Edit.**

1. In `walks.md`, add `## W<next> — <question>` and its table, following the Skeleton. Walk the question for real, and count the reads, before writing it.

### Relink walk

**Edit.**

1. In each walk that passes through the moved page, replace its path. If the route itself changed, walk the question again and rewrite the rows.

### Amend section

**Before.** The approved old text and new text of the section are known, word for word.

**Edit.**

1. Replace the approved old text with the approved new text, inside that section only. Keep the section's heading, and keep every row number or rule number that other pages cite.  
2. Search `docs/protocol/` for citations of the section's rows or rules, and confirm each still holds.

**Result.** The section states the approved text, and every citation of it still holds.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Every link and every named path resolves |
| 2 | No row tells the agent to edit a file |
| 3 | `bounds.md` has one open-set row per flow in Article 3 of `docs/protocol/core-workflow.md` |
| 4 | No page names anything particular to the project ([../../environment/quality.md](../../environment/quality.md) 3.13): no heavy folder or large file (the doc map holds those), no language pattern or test convention (the program page holds those), and no module, area, or record id |
| 5 | Every rule number cited elsewhere in `docs/protocol/` exists and says what the citation relies on |

## Keep out

- Write steps: flows.  
- A list of every page: the doc map.  
- Paths of this repository's heavy folders and large files: the doc map, **Heavy places**.
