<!--
  File: docs/protocol/blueprints/protocol/instrument.md
  Purpose: Shape of a protocol instrument, and the operations that amend one
  Audience: Agents
  Update when: The instrument shape, the citation rule, or an operation changes
-->

# Protocol instrument

**Shapes:** `docs/protocol/brief.md`, `docs/protocol/core-workflow.md`, and every `docs/protocol/environment/<page>.md` except `docs/protocol/environment/README.md` (a folder door)  
**Register:** legal · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md), with `Audience: Agents`  
**Neighbours:** a flow page ([flow.md](flow.md)) — the procedure for one situation. An instrument states law: definitions, standards, duties, and prohibitions.

The binding law of the protocol. Other pages cite instruments by Article number and by rule number (e.g. "style Article 3", "quality 3.5"). Those citations must never silently change meaning.

## Skeleton

Form A, an article instrument (every instrument except `phase.md`):

```markdown
<!--
  File: docs/protocol/<path>.md
  Purpose: <what this instrument governs>
  Audience: Agents
  Update when: <the kind of rule whose change changes this page>
-->

# <Instrument name>

## Status

This instrument <what it governs>. It binds every agent.

<Which neighbouring instruments or flows own related matters, with links, and what this one does not restate.>

## Article 1 — <Title>

<Normative text: duties with "shall", prohibitions with "shall not", conditions with "if". Enumerable rules in a table with a first column `#`, numbered `<article>.<n>`.>

…

## Exclusion

This instrument does not <what it leaves to others>. It defines <what it defines>.
```

Form B, `docs/protocol/environment/phase.md`:

```markdown
<!--
  File: docs/protocol/environment/phase.md
  Purpose: Structural freedom by phase, and docs depth rules
  Audience: Agents
  Update when: The phase or the depth rule changes
-->

# Phase

**Current phase:** <alpha | beta | prod>

<One paragraph: what phase is, and that it is not a Goal.>

| Phase | Structure | How work proceeds |
|-------|-----------|-------------------|
| **<phase>** | <rule> | <rule> |
…

## Depth

| Path shape | Allowed |
|------------|---------|
| `<path shape>` | <Yes — reason \| No> |
…

## Folder doors

<The rule, and what is exempt.>

## <Phase> structural change

1. <what must hold after a folder or major document is created, moved, or renamed>
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# <Instrument name>` |
| Status | Form A | First section. Says what it governs and that it binds every agent |
| Articles | Form A | `## Article <n> — <Title>`, numbered from 1 in order. Rule tables have a first column `#` with values `<n>.<m>` (or `<n>.A`, `<n>.B` for sub-groups) |
| Repealed article | when repealed | The heading stays as `## Article <n> — Repealed`, followed by one sentence naming where the matter now lives |
| Exclusion | Form A | Last section |
| Current phase line | Form B | Directly under the title. The phase word appears on this line |
| Register | yes | Legal ([../../environment/style.md](../../environment/style.md) Article 2): explicit, normative, applicable without guesswork |

## Citation rule

1. An Article number or a rule number, once published, never changes meaning. Never renumber.  
2. A new Article is added at the end, before **Exclusion**, with the next number.  
3. A new rule in a table takes the next number in that table's sequence.  
4. A removed Article or rule keeps its number, marked `Repealed`, with a pointer.  
5. After any amendment, search `docs/protocol/` and `docs/paperwork/steps/` for citations of the changed number (e.g. `core-workflow.md` Article 4, `quality 3.5`) and confirm each still holds.

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Add article** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Amend article** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Repeal article** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Add rule** | [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A1) |
| **Set phase** | [../../flows/amendment.md](../../flows/amendment.md) Step 8, when the human approves a phase change |

### Add article

**Edit.**

1. Insert `## Article <next number> — <Title>` and its text directly above `## Exclusion`.  
2. Apply **Citation rule** step 5.

### Amend article

**Edit.**

1. Replace the approved old text with the approved new text, inside that Article only.  
2. Apply **Citation rule** step 5.

### Repeal article

**Edit.**

1. Replace the Article's heading with `## Article <n> — Repealed` and its body with `Repealed on YYYY-MM-DD. The matter now lives in [<page>](<link>).`  
2. Apply **Citation rule** step 5.

### Add rule

**Edit.**

1. Add a row at the end of the rule table, numbered `<article>.<next number in that table>`.

### Set phase

**Edit.**

1. In `phase.md`, replace the value after `**Current phase:** ` with the new phase word.  
2. The doc map and the repository README then need **Set phase** / **Set status** of their blueprints.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Form A: Status first, Articles numbered 1..n with no gap except repealed ones, Exclusion last |
| 2 | Form B: the Current phase line is directly under the title |
| 3 | Every citation of this instrument elsewhere in `docs/protocol/` resolves to an Article or rule that says what the citation relies on |

## Keep out

- Procedures with ordered file edits: flow pages.  
- Document shapes: blueprints.  
- Product, language, build system, or domain names ([../../brief.md](../../brief.md) Exclusion).
