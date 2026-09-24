<!--
  File: docs/protocol/environment/correctness.md
  Purpose: When a Step’s implementation is correct — source vs document standards
  Audience: Agents
  Update when: The definition of correctness or Accept changes
-->

# Correctness

## Status

This instrument defines when implemented work is correct under the protocol. It is the ground of judgment for a Step’s substance. It binds every agent.

How Goals and Steps are found, how a torn Step is recognised, and how rollback is performed, are prescribed by [core-workflow.md](../core-workflow.md) and by the flows that instrument invokes. This file does not restate those procedures.

This instrument is product-independent. It does not name a witness command. The project supplies the command that runs the suite.

## Article 1 — Unit of judgment

Correctness is determined per Step. The store of claims for that Step is the Step record. Chat is not a store of claims.

## Article 2 — Two kinds of modification

A Step falls under exactly one of the following heads, according to what it is authorised to change. The standard of correctness differs by head.

| Kind | What the Step touches | Standard of correctness |
|------|------------------------|-------------------------|
| Source modification | Source, or source together with documents | Article 3 |
| Document modification | Documents only; no source change | Article 4 |

A Step that touches source shall not be judged under Article 4 alone.

## Article 3 — Source modification

### 3.1 Functional requirements

The approved functional requirements in the Step record are the claims. Each claim shall be witnessed by one or more code tests. A claim without a code test is not a requirement under this instrument; it is a wish.

### 3.2 Green suite

Source work is correct if and only if:

1. every code test that witnesses this Step’s claims is green; and
2. every code test that witnesses every earlier Accepted Step remains green.

Compiling is not sufficient. A narrative that the work is “clean” is not sufficient. A green suite obtained by deleting, skipping, or softening an earlier test is not correct; that conduct is forbidden.

### 3.3 Mapping

The Step record shall map each functional requirement to the test or tests that witness it. Absence of that mapping is absence of proof.

## Article 4 — Document modification

A Step that amends documents alone, and does not modify source, is correct if and only if the protocol has been followed to the letter: [brief.md](../brief.md), [quality.md](quality.md), [style.md](style.md), [core-workflow.md](../core-workflow.md), the active flow under [flows/](../flows/), and the blueprint under [blueprints/](../blueprints/) that the flow names.

No new code test is required solely to search documents for phrases. Where the project’s existing suite already encodes a document fact, that suite shall remain green.

## Article 5 — Accept

Accept of a Step means that the standard applicable under Article 2 holds.

A status line, a commit message, or a paragraph in a Goal does not Accept. The applicable standard Accepts. The status line records an Accept that has already occurred.

## Article 6 — Relationship to quality

This instrument judges correctness of the Step’s kind under Article 2. [quality.md](quality.md) judges organisation, synchronisation duty, and conduct as quality. Poor quality does not become correct by renaming it. Incorrect work does not become Acceptable by being neatly filed.

## Exclusion

This instrument does not define shelf layouts, product features, or the text of particular tests. It defines when a Step’s implementation is correct.
