<!--
  File: docs/protocol/blueprints/product/wiki-page.md
  Purpose: Shape of a domain-rule page (docs/product/wiki/*.md), and the operations that create and edit one
  Audience: Agents
  Update when: The wiki-page shape or an operation changes
-->

# Wiki page

**Shapes:** every `docs/product/wiki/<page>.md` except `docs/product/wiki/README.md` (a folder door, [../doors/folder-door.md](../doors/folder-door.md))  
**Register:** understanding · **Human-facing:** yes  
**Header:** [document header](../headers/document-header.md)  
**Neighbours:** a mechanism page ([../architecture/mechanism-page.md](../architecture/mechanism-page.md)) — how the program computes a rule. A wiki page states the rule, for a person, in terms a person can argue with.

What is true of one part of the domain. Each page owns one part, and says which neighbouring part it does not own.

## Skeleton

```markdown
<!--
  File: docs/product/wiki/<page>.md
  Purpose: <the part of the domain this page owns>
  Audience: Humans and agents
  Update when: <which rule's change should change this page>
-->

# <Part of the domain>

This page owns <part>. It does not own <neighbouring part>. That is the [<neighbour>](<neighbour>.md) page.

<The rules as they stand, in short paragraphs. Quantities with their units.>

## <Rule group>

<Paragraphs, or a two-column table:>

| <Case> | <What is true> |
|--------|----------------|
| <case> | <the current rule> |
…
```

## Parts

| Part | Required | Rule |
|------|----------|------|
| Title | yes | `# <Part of the domain>` in a person's words |
| Ownership paragraph | yes | First paragraph. "This page owns … It does not own … That is the … page", with a link |
| Rules | yes | Paragraphs or tables stating the current rule. Every quantity has its value and unit or range |
| Rule group | no | `## <Rule group>` sections, each one kind of situation |
| Retired rule | no | Named once, in one sentence, as retired: "An older rule did X. That rule is retired." Never kept as a second procedure |

## Operations

| Operation | Invoked by |
|-----------|------------|
| **Create page** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step defines a new part of the domain; [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Amend rule** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step changes a domain rule; [../../flows/amendment.md](../../flows/amendment.md) Step 5 (class A5) |
| **Retire rule** | [../../flows/step.md](../../flows/step.md) SYNC (Ties), when a Step replaces a domain rule |

### Create page

**Edit.**

1. **Write header** of [../headers/document-header.md](../headers/document-header.md).  
2. Copy the Skeleton and fill it.  
3. The wiki door gains a row by **Add child** of [../doors/folder-door.md](../doors/folder-door.md). The neighbour page named in the ownership paragraph gains a link back, by **Amend rule** on that page.

### Amend rule

**Edit.**

1. Replace the old statement of the rule with the current one, in place. Do not add a banner naming the Step.

### Retire rule

**Edit.**

1. Replace the old rule with the new rule, by **Amend rule**.  
2. Directly after it, add one sentence: `An older rule <did what>. That rule is retired.`

## Check

| # | The file is legal only if |
|---|---------------------------|
| 1 | The first paragraph states what the page owns and does not own, with a link |
| 2 | No Step log, status banner, module path, or type name appears |
| 3 | Every quantity has a value and a unit or range |
| 4 | The wiki door has a row for this page |

## Keep out

- A Step log at the top: paperwork.  
- How the program computes the rule: the architecture paper.
