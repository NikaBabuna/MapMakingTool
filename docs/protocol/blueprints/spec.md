<!--
  File: docs/protocol/blueprints/spec.md
  Purpose: Shape of one framework spec topic
  Audience: Agents and humans
  Update when: The spec shape changes
-->

# Framework spec

These are the host mechanism pages under `docs/architecture/host/`, other than that folder’s door. Today:

| File | The topic it owns |
|------|-------------------|
| `pool.md` | The pool update and the host ports |
| `events.md` | Events, categories, and emission |
| `systems.md` | Systems, sub-systems, and how conflicts resolve |
| `merge.md` | How writes to a field combine |
| `determinism.md` | What is guaranteed to repeat |
| `user.md` | What the user layer samples and what it may read |
| `diagnostics.md` | The engine diagnostics port |
| `open-questions.md` | Gaps that are still undecided. Not a dumping ground for finished work |

One topic per file is the point. A change to merge behavior edits `merge.md` and does not also rewrite `events.md` while you are there. World procedures live under `docs/architecture/world/`. They are not these pages.

**Write or edit it when.** Framework behavior in that topic changes, or an open question is decided (move the answer into the topic file, and remove or close the question).

## What each part is for

| Part | Why it is there |
|------|-----------------|
| Opening | The guarantee in a few sentences |
| The rules | Tables and ordered steps. Quantities and illegal cases are explicit, so a check can be written from the page |
| Code status | One line if it helps: whether the code matches this page. The line does not replace the rules. If the code has moved on, fix the rules or say the page is behind, with the fact |
| Open questions | Only in `open-questions.md`. A decided question does not stay “open” with the answer buried under it |

## Skeleton

```markdown
# <Topic>

<The guarantee.>

## <Rule>

| Case | What happens |
|------|----------------|
| <case> | <result> |

Illegal: <case the framework rejects>.
```

## Keep out

Product world rules. Those are wiki pages. A Step diary.
