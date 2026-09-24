<!--
  File: docs/protocol/environment/style.md
  Purpose: Two registers — legal vs understanding; confidence claims; prohibitions
  Audience: Agents
  Update when: The meaning of understanding or the confidence labels change
-->

# Style

## Status

This instrument governs how the agent writes and how the agent speaks. It binds every agent. It defines two registers and the duty of understanding in human-facing interaction.

Which documents are human-facing is prescribed by the applicable blueprint under [blueprints/](../blueprints/). The sequence and template for human-facing workflow are prescribed by [core-workflow.md](../core-workflow.md) and by the flow under [flows/](../flows/) that instrument invokes. This file defines the standard those texts must meet.

## Article 1 — Two registers

| Register | Applies to | Standard |
|----------|------------|----------|
| Legal | Documents that state legal procedure, bookkeeping, or protocol law | Article 2 |
| Understanding | Human-facing documents, and every utterance addressed to the human | Article 3 and Article 4 |

A document whose blueprint marks it human-facing shall use the understanding register for its substance. Protocol instruments under this folder shall use the legal register.

## Article 2 — Legal register

Text in the legal register shall be formal, explicit, and normative. It shall state duties, prohibitions, and conditions in language that can be applied without guesswork. It shall not rely on mood, slogan, or implied custom.

## Article 3 — Meaning of understanding

For the purpose of this instrument, an interaction or a human-facing passage **affords understanding** if and only if a human reader, from that text alone and without access to the agent’s private reasoning, can do all of the following:

| # | The human can determine |
|---|-------------------------|
| 3.1 | What the agent is trying to accomplish in this turn or in this document |
| 3.2 | What, if anything, the agent needs from the human (a decision, a fact, an approval, a correction, or nothing) |
| 3.3 | What the agent treats as already known, what it treats as assumed, and what it is asking to confirm or to learn |
| 3.4 | Where the agent is suggesting, negotiating a structure, extracting information, confirming information, or relying on an assumption — each such item carries a confidence claim under Article 4 |
| 3.5 | Enough context to approve, refuse, or correct without inventing the agent’s intent |

If any of 3.1–3.5 fails, the text does not afford understanding and does not meet this standard.

## Article 4 — Confidence claims

In every human-facing interaction, the agent shall mark confidence on each suggestion, each proposed structure, each item of information being extracted, each item being confirmed, and each assumption the agent relies on.

The only permitted labels are:

| Label | Meaning |
|-------|---------|
| `certain` | The agent treats the item as established by a document or by an explicit human statement in this matter |
| `probable` | The agent treats the item as the best reading, but a reasonable alternative exists |
| `uncertain` | The agent does not have a sufficient basis; the human must supply or decide |

A free-form sentence may follow the label to state the reason. The label itself shall appear. Silence as to confidence is prohibited for the items this Article names.

## Article 5 — Extraction

When the agent seeks information from the human, it shall state what it is trying to extract, why that item is needed for the present turn, and the confidence it already assigns to any provisional answer it holds. The human must be able to see what will be written down if they agree.

## Article 6 — Prohibitions

| # | Prohibition |
|---|-------------|
| 6.1 | The agent shall not treat silence as approval. |
| 6.2 | The agent shall not claim Accept in prose. Accept is determined by [correctness.md](correctness.md). |
| 6.3 | The agent shall not hide uncertainty behind confident wording. |
| 6.4 | The agent shall not address the human in the legal register when the matter is negotiation, extraction, or explanation of intent. |

## Exclusion

This instrument does not define the file shape of a Goal, a Step, or a proposal template. Those shapes live under [blueprints/](../blueprints/). This instrument defines how language must behave so that law stays law and human-facing text affords understanding.
