<!--
  File: cli/README.md
  Purpose: Landmark index for the CLI Maven module
  Audience: Agents and humans
  Update when: CLI layout or usage changes
-->

# CLI module

Maven artifact `com.aethelgard:cli` — headless runner + **placeholder dispatcher** over a product session.

**Depends on:** `product` (ADR-010). Never depended on by `engine` or `product`. `ui` depends on this module **only** for the in-window console.

Placeholder verbs (unstable — G-005): not a finished operator language.

## Run

From repo root (after `mvnw -pl cli -am package`):

```text
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="--steps 3"
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="status"
```

## Batch flag

| Flag | Default | Meaning |
|------|---------|---------|
| `--steps N` | `0` | Additional generation Steps after create; print `settledWorld()` |

## Verbs (`CommandDispatch`)

| Line | Meaning |
|------|---------|
| `status` | step, size, seed |
| `advance` / `advance N` | generation Steps |
| `dump` | settled world text |
| `at X Y` | cell elevation, plate, velocity |
| `layers` | field names |

## Output

`--steps`: `ProductSession.settledWorld()` for `WorldSpec.DEFAULT`. Verb lines: see [docs/blockers/F-023.md](../docs/blockers/F-023.md).

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md) · [docs/engine/architecture.md](../docs/engine/architecture.md)
