<!--
  File: docs/protocol/blueprints/architecture.md
  Purpose: Shape of the implementation paper
  Audience: Agents and humans
  Update when: The paper shape changes
-->

# Implementation paper

The paper is `docs/architecture/`. It is arranged by abstraction. A page states its own mechanism and points at the finer page. The procedure lives on that finer page.

| Level | Path | What a page here owns |
|-------|------|------------------------|
| Abstract | `docs/architecture/README.md` | The four levels and the question each answers. No procedures |
| Program | `docs/architecture/program.md` | Modules and the dependency direction |
| Host | `docs/architecture/host/` | One engine step, and one page per host mechanism |
| World | `docs/architecture/world/` | One generation, and one page per procedure that writes the grids |
| Studio | `docs/architecture/studio/` | Session, raster, and the HTTP host |

`docs/architecture/world/crust/<page>.md` is the fourth level the depth rule allows. Other shelves stay within three.

A paper is what an agent reads instead of every source file. It says what the piece is for, what it reads, what it writes, and what is true afterwards. It names the type and the quantities that mechanism owns. Where a page and the source disagree, the page is wrong and gets fixed. The changelog keeps the history. A stack of Step ids is not the explanation.

**Write or edit it when.** **Record source** runs, or a structural decision changes the layout. Edit the page that owns the mechanism. Leave the parent as a pointer.

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | What this level is for, before any table |
| Reads / writes | The inputs and the outputs, so a caller knows the boundary |
| Procedure | The order and the quantities. On a parent, this is the list of children, not their procedures |
| Afterwards | What a later step can rely on |
| Where it lives | The type and the source path |
| Pointers | Parent, child, and the ADR that records why, when a decision exists |

## Skeleton

```markdown
# <Mechanism>

<What this level is for.>

## What it reads

<inputs>

## What it writes

<outputs>

## Procedure

1. <step> — <quantity or rule>

## What is true afterwards

<fact a later step can rely on>

## Where it lives

`<type>` in `<source path>`.

Parent: [<parent>](README.md). Finer page: [<child>](child.md).
```

## Keep out

A status banner that replaces the explanation. Requirement tables. Protocol law. A second copy of a child's procedure.
