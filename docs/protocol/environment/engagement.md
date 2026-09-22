<!--
  File: docs/protocol/environment/engagement.md
  Purpose: How the agent talks to the human, and when it must ask
  Audience: Agents and humans
  Update when: What the agent must ask, or how a reply is shaped, changes
-->

# Engagement

The human is the authority for starting work and for changing a stored requirement. The agent is the authority for following the flows once that approval exists. Mixing those roles is how unapproved code lands, and how approved work stalls because the agent asked permission for every line.

## What a reply contains

Every proposal, plan, stop, and completion is in plain English, so the human can judge the meaning and not only see that files changed.

| Part | What it answers |
|------|-----------------|
| What changed | Which behavior or which documents are different |
| What it means | What a person will observe, or what an agent will do differently next time |
| What was witnessed | This Step’s checks, and that earlier Steps still hold. For a documentation Step, say that the existing suite was run and that no new phrase-test was added |
| What remains | Which claims of the Goal are still open |

The shape of that message is also the reply blueprint. This page is the rule. The blueprint is the form.

## What the agent may do without asking

- Write or update documents that the active flow already names, including writing an approved requirement list into the Step record
- Fix a document the flow says is tied to a file the Step already changed

## What requires asking first

| Situation | Why it is not free |
|-----------|-------------------|
| Starting a Step | The job and the requirements are the human’s approval. No implementation before both are approved |
| Changing a stored requirement | The file is the contract. Softening it to get a green suite is forbidden |
| A refactor wider than the approved job | It hides unrelated change inside a Step that was about something else |
| Expanding scope | Scope is a document. The **Scope** flow edits it first, with the human |
| Continuing after the witness keeps failing | The human decides whether to keep spending the Step or to roll back |
| A package layout the implementation paper does not already name | New structure is a decision, not a guess |

Asking means stating the choice in the reply shape above, then waiting. Do not do the thing and mention it afterwards.
