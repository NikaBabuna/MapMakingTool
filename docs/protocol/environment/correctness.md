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

1. every code test that witnesses this Step’s claims is green, except a test the Step record lists under **Known defects**; and
2. every code test that witnesses every earlier Accepted Step remains green, except a test a Step record lists under **Retired tests** or **Known defects**.

Compiling is not sufficient. A narrative that the work is “clean” is not sufficient. A green suite obtained by deleting, skipping, or softening an earlier test is not correct; that conduct is forbidden.

An earlier test is retired only by a Step the human approved to retire it. That Step’s record lists the test under **Retired tests**, with the requirement whose test now covers its claim, or the reason no test covers it: the test read a document, it read another file’s source or configuration as text, the build itself enforces the claim, the claim is an internal detail beneath the outcome a requirement states, or the claim is no longer the product’s.

A **Known defect** is a test that fails because the code is wrong, when the human has directed that the code not change in the Step that found it. The test stays enabled and red. The record lists it under **Known defects**, with the requirement it witnesses and the failing assertion, until a later Step fixes the code. The requirement it witnesses is recorded as `known defect`, not `met`.

### 3.3 Mapping

The Step record shall map each functional requirement to the test or tests that witness it, each linked to the file that holds it. Each test names, beside it in its file, the requirements it proves; one test may prove several. Absence of that mapping is absence of proof.

## Article 4 — Document modification

A Step that amends documents alone, and does not modify source, is correct if and only if the protocol has been followed to the letter: [brief.md](../brief.md), [quality.md](quality.md), [style.md](style.md), [core-workflow.md](../core-workflow.md), the active flow under [flows/](../flows/), and the blueprint under [blueprints/](../blueprints/) that the flow names.

A code test runs code. It does not read a document, nor another file’s source or configuration as text. A document fact is witnessed by reading the document, not by a code test.

## Article 5 — Accept

Accept of a Step means that the standard applicable under Article 2 holds.

A status line, a commit message, or a paragraph in a Goal does not Accept. The applicable standard Accepts. The status line records an Accept that has already occurred.

## Article 6 — Relationship to quality

This instrument judges correctness of the Step’s kind under Article 2. [quality.md](quality.md) judges organisation, synchronisation duty, and conduct as quality. Poor quality does not become correct by renaming it. Incorrect work does not become Acceptable by being neatly filed.

## Exclusion

This instrument does not define shelf layouts, product features, or the text of particular tests. It defines when a Step’s implementation is correct.
