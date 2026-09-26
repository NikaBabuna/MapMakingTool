<!--
  File: docs/protocol/environment/map.md
  Purpose: Map of the documentation tree — folder and standing-document roles
  Audience: Agents
  Update when: A docs shelf or standing document role changes
-->

# Map

## Status

This instrument maps the documentation tree. It gives a basic understanding of what each folder is for, and what each standing document in those folders is meant to serve.

It does not record project history. It does not name Goals, Steps, or decisions by id. Paperwork content is project-specific and is omitted; the paperwork folders themselves remain in this map because their roles are not.

This instrument is descriptive. Hard rules of organisation live in [quality.md](quality.md).

## Article 1 — Entrance

The entrance sits at the root of `docs/`. It is not a shelf of substance.

| Document | Purpose |
|----------|---------|
| `docs/README.md` | Index of the documentation tree. Points at each shelf. |
| `docs/navigation.md` | Map for finding a document. Pointers, not law. |

## Article 2 — Shelves

| Shelf | Folder | Purpose |
|-------|--------|---------|
| Protocol | `docs/protocol/` | Conduct. Mode of operation. Product-independent. |
| Product | `docs/product/` | What the product is, for a person, and what it includes and refuses. Binding scope. Not how the program is built. |
| Architecture | `docs/architecture/` | What is built. The implementation paper. |
| Paperwork | `docs/paperwork/` | Who did what, and when. Progress records. |

An entrance maps shelves. It does not hold the substance of those shelves.

## Article 3 — Protocol

| Path | Purpose |
|------|---------|
| [../brief.md](../brief.md) | Global prompt. Roles. Permitted interactions. Pointers to mandatory standards. |
| [../core-workflow.md](../core-workflow.md) | How a turn runs. How a flow is chosen and followed. |
| [README.md](README.md) | Door to this room — the operating environment |
| [map.md](map.md) | This map |
| [phase.md](phase.md) | Structural freedom by phase, and docs depth |
| [quality.md](quality.md) | Standard of organisation and quality |
| [correctness.md](correctness.md) | When a Step’s work is correct |
| [style.md](style.md) | Legal register and understanding register. Confidence claims |
| [core-definition.md](core-definition.md) | Core terminology. Goal and Step types and statuses |
| [../flows/](../flows/) | Bookkeeping algorithms |
| [../blueprints/](../blueprints/README.md) | The exact shape of every document the protocol writes, grouped by the shelf or kind of file, with the operations that create and edit it |
| [../navigation/](../navigation/README.md) | How to find one document, read only the part needed, and leave the rest closed: reading, bounds, pointers, the code route, and walks |

## Article 4 — Product

| Path | Purpose |
|------|---------|
| `README.md` | Door to the conceptual product. |
| `concept.md` | What the product is for. The only scope document: what the product includes, and what it refuses. |
| `journeys.md` | What a person does, and what they see. |
| `glossary.md` | Meaning of domain words. |
| `style-guide.md` | How the screen looks, and the word on a control. |
| `wiki/` | Rules of the domain, for a person. |

## Article 5 — Architecture

| Path | Purpose |
|------|---------|
| `README.md` | Abstract of the implementation paper. Levels and questions. No procedures. Links the paper's glossary and open-questions page. |
| `program.md` | Modules, build files, dependency direction, the processes the project runs and how they reach each other, the stack, and the project facts the protocol relies on: the witness command, where tests live, how declarations are found, and how test output reports. |
| `glossary.md` | The paper's glossary: its public words, and the symbols that the models of several pages share. |
| `open-questions.md` | The paper's open questions: implementation behaviour that is still undecided. |
| `<area>/` | One folder per area of the implementation the project chooses. Its `README.md` is the level page: what one run of that area does, in order, naming one page per mechanism. |
| `<area>/<chapter>/` | Optional. A chapter of procedures inside an area, with its own level page. |

A page at one level names the finer page. The procedure lives on that finer page. Which areas exist is the project's choice, recorded on the abstract.

## Article 6 — Paperwork

Content of these folders is project-specific and is not described here. The folders remain because their roles are fixed.

| Path | Purpose of the folder |
|------|------------------------|
| `README.md` | Door to progress records. |
| `goals.md` | Index of Goals. |
| `goals/` | One file per Goal. |
| `steps.md` | Registry of Steps. |
| `steps/` | One file per Step record, once that Step is approved. |
| `decisions.md` | Index of decisions. |
| `decisions/` | One file per decision record. |
| `changelog.md` | Dated log of structural and Accept events. |
| `roadmap.md` | Ordered direction. Goals, not Accept claims. |
| `backlog.md` | Candidates not yet promoted to a Goal. |

## Exclusion

This instrument does not narrate the current project state. It does not list ids. It does not replace [quality.md](quality.md), [correctness.md](correctness.md), or [../core-workflow.md](../core-workflow.md).
