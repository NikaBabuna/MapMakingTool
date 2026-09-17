<!--
  File: cli/README.md
  Purpose: Landmark index for the CLI Maven module
  Audience: Agents and humans
  Update when: CLI layout or usage changes
-->

# CLI module

Maven artifact `com.aethelgard:cli` — headless runner over a **product session**.

**Depends on:** `product` (ADR-010). Never depended on by `engine` or `product`.

Placeholder flags (unstable — G-005): not a finished operator language.

## Run

From repo root (after `mvnw -pl cli -am package`):

```text
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="--steps 3"
```

## Flags

| Flag | Default | Meaning |
|------|---------|---------|
| `--steps N` | `0` | Additional generation Steps after create (create already completes Step 0) |

## Output

`ProductSession.settledWorld()` — world dump (header, elevation, plates) for `WorldSpec.DEFAULT`.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md) · [docs/engine/architecture.md](../docs/engine/architecture.md)
