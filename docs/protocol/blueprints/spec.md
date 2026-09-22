<!--
  File: docs/protocol/blueprints/spec.md
  Purpose: Shape of one framework spec topic
  Audience: Agents and humans
  Update when: The spec shape changes
-->

# Framework spec

These are the topic files in `docs/engine/specs/`, other than that folder’s door. Today:

| File | The topic it owns |
|------|-------------------|
| `overview.md` | The core idea of the framework |
| `architecture-diagram.md` | The picture of how the parts connect |
| `step-lifecycle.md` | The order of one framework step |
| `systems.md` | Systems, sub-systems, and how conflicts resolve |
| `events.md` | Events, categories, and emission |
| `merge-types.md` | How writes to a field combine |
| `determinism.md` | What is guaranteed to repeat |
| `user-layer.md` | What the user sees and sends |
| `pool-engine.md` | The pool as the engine object, and the host ports |
| `open-questions.md` | Gaps that are still undecided. Not a dumping ground for finished work |

One topic per file is the point. A change to merge behavior edits `merge-types.md` and does not also rewrite `events.md` “while you are there.”

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
