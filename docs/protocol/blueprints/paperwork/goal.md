<!--
  File: docs/protocol/blueprints/paperwork/goal.md
  Purpose: Shape of one Goal file, the naming rules for Goals, and the operations that edit a Goal file
  Audience: Agents
  Update when: The Goal file shape, the naming rules, or an operation changes
-->

# Goal file

**Shapes:** every `docs/paperwork/goals/G-0xx-<slug>.md`  
**Register:** legal for normative parts; understanding allowed in **Prior** and **Approved** · **Human-facing:** no  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a Step record ([step.md](step.md)) — one job and its requirements. A Goal states claims; Steps prove them. The Goal index ([goal-index.md](goal-index.md)) — which Goal is active.

The durable result across chats: what shall be true when it is done, what is refused, what is already decided, and which Steps will get there. [../../flows/reconcile.md](../../flows/reconcile.md) reads its Planned Steps table as mark M3.

## Naming

| Item | Rule |
|------|------|
| Goal id | **Next Goal id** of [goal-index.md](goal-index.md) |
| Slug | Take the approved name. Remove any part in parentheses. Lower-case it. Replace every run of characters other than `a–z` and `0–9` with one hyphen. Remove hyphens at both ends. If more than five words remain, keep the first five. Show the slug in the Goal proposal so the human approves it with the text. Examples: "Faster search results" gives `faster-search-results`; "Offline mode (sync + cache + retry)" gives `offline-mode` |
| Path | `docs/paperwork/goals/G-0xx-<slug>.md` |

The slug rule also names decision records ([decision-record.md](decision-record.md)).

## Skeleton

```markdown
<!--
  File: docs/paperwork/goals/G-0xx-<slug>.md
  Purpose: Multi-session Goal — <one-line result>
  Audience: Agents and humans
  Update when: Progress changes or Goal definition changes
-->

# G-0xx — <name>

**Status:** `in progress`  
**Type:** `<Polishing | Extension>`  
**Prior:** <what the previous Goal left true>  
**Approved:** YYYY-MM-DD (human). <one sentence of direction>

---

## Result we want

When this Goal is `done`:

1. <claim a witness can support>
…

---

## Out of scope (this Goal)

- <exclusion>
…

---

## Decided for this Goal

| Topic | Decision |
|-------|----------|
| <topic> | <decision> |
…

---

## Product claims (tests by Goal end)

- [ ] <claim 1, word for word as in Result we want>
…

---

## Planned Steps

| Step | Intent | Type | Status |
|------|--------|------|--------|
| F-0xx | <one line> | <Iterative \| Modification \| Documentation \| Cleanup> | not started |
…

---

## Progress

| Metric | Value |
|--------|-------|
| Steps done | 0 / <n> |
| Claim boxes | 0 / <c> |
| Last Accept | — |
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# G-0xx — <name>` |
| `**Status:**` | yes | `` `not started` ``, `` `in progress` ``, `` `done` ``, or `` `abandoned` ``, in backticks |
| `**Type:**` | yes | `` `Polishing` `` or `` `Extension` ``, as approved |
| `**Prior:**` | yes | One or two sentences |
| `**Approved:**` | yes | `YYYY-MM-DD (human).` and one sentence of direction |
| Result we want | yes | Numbered claims, as approved. Each can be supported by a test or by reading a named document |
| Further approved sections | no | Any `## <heading>` section of approved Goal text, placed after Result we want. Not a diary |
| Out of scope | yes | Bullets, as approved. `- none` if there are none |
| Decided for this Goal | yes | Table, as approved. One row `\| none \| — \|` if there are none |
| Product claims | yes | One box per Result claim, in the same order and with the same words. Unchecked: `- [ ] <claim>`. Checked: `- [x] <claim> — witness: <test name \| reading <path>>` |
| Planned Steps | yes | Rows in execution order. **Status** is `not started`, `in progress`, `done`, or `rolled back`, equal to the Step registry row |
| Progress | yes | Rows `Steps done` (`<done> / <planned>`), `Claim boxes` (`<checked> / <claims>`), `Last Accept` (`F-0xx` or `—`). An abandoned Goal also has the row `Abandoned` |
| `---` separators | yes | Between the key block and each `##` section, as in the Skeleton |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create** | [../../flows/goal.md](../../flows/goal.md) Open goal (W1) |
| **Set step status** | [../../flows/step.md](../../flows/step.md) MARK (M3), CLOSE step 3, CLOSE step 5 (revert) |
| **Update progress** | [../../flows/step.md](../../flows/step.md) CLOSE step 3; [../../flows/goal.md](../../flows/goal.md) Close goal |
| **Add planned step** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Remove planned step** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Reorder planned steps** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Add claim** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Remove claim** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Amend text** | [../../flows/goal.md](../../flows/goal.md) Amend goal |
| **Check claim** | [../../flows/goal.md](../../flows/goal.md) Close goal |
| **Set goal status** | [../../flows/goal.md](../../flows/goal.md) Close goal, Abandon goal |
| **Record abandonment** | [../../flows/goal.md](../../flows/goal.md) Abandon goal |

### Create

**Before.** The human approved the Goal text, the type, and the slug. The Goal id and the Step ids are assigned.

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md), with `Purpose: Multi-session Goal — <one-line result>`.  
2. Copy the Skeleton. Fill the key block, Result we want, any further approved sections, Out of scope, and Decided for this Goal with the approved text, word for word.  
3. Product claims: one `- [ ] <claim>` per Result claim.  
4. Planned Steps: one row per planned Step, `| F-0xx | <intent> | <type> | not started |`.  
5. Progress: `0 / <number of planned Steps>`, `0 / <number of claims>`, `—`.

**Result.** A complete Goal file with status `in progress`.

### Set step status

**Edit.**

1. In Planned Steps, in the row whose Step is `F-0xx`, replace the Status cell with `in progress`, `done`, or `not started` (for a revert).

### Update progress

**Edit.**

1. `Steps done`: replace the first number with the count of Planned Steps rows whose Status is `done`.  
2. `Claim boxes`: replace the first number with the count of `- [x]` lines.  
3. `Last Accept`: replace the value with the id of the Step being closed.

### Add planned step

**Edit.**

1. Insert `| F-0xx | <intent> | <type> | not started |` at the approved position in Planned Steps. `F-0xx` is **Next Step id** of [step-registry.md](step-registry.md).  
2. In Progress, add 1 to the second number of `Steps done`.

### Remove planned step

**Before.** The row's Status is `not started`.

**Edit.**

1. Delete the row.  
2. In Progress, subtract 1 from the second number of `Steps done`.

### Reorder planned steps

**Edit.**

1. Move the rows into the approved order. Do not change any id.

### Add claim

**Edit.**

1. Add the claim to Result we want, at the approved position, and renumber the list.  
2. Add `- [ ] <claim>` at the same position in Product claims.  
3. In Progress, add 1 to the second number of `Claim boxes`.

### Remove claim

**Edit.**

1. Remove the claim from Result we want, and renumber the list.  
2. Remove its box from Product claims.  
3. In Progress, subtract 1 from the second number of `Claim boxes`, and recount the first.

### Amend text

**Edit.**

1. Replace the approved old text with the approved new text, in the section where it stands. If the name changed, replace it in the title.

### Check claim

**Before.** The claim's witness holds now.

**Edit.**

1. Replace `- [ ] <claim>` with `- [x] <claim> — witness: <test name | reading <path>>`.

### Set goal status

**Edit.**

1. Replace the value of `**Status:**` with `` `done` `` or `` `abandoned` ``.

### Record abandonment

**Edit.**

1. Add the row `| Abandoned | YYYY-MM-DD — <the human's reason, in one clause> |` at the bottom of Progress.

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | Its path follows **Naming**, and the title's id matches the path |
| 2 | Result claims and Product claims boxes agree in number, order, and words |
| 3 | Every `- [x]` box names a witness |
| 4 | Planned Steps statuses equal the Step registry rows for the same ids |
| 5 | Progress counts equal the counts of `done` rows and `- [x]` boxes |
| 6 | If `**Status:**` is `` `done` ``, every Planned Steps row is `done` and every box is `- [x]` |

## Keep out

- Step requirement tables: the Step record.  
- A session section or a diary of the chat.  
- The Active Goal line: the Goal index.
