<!--
  File: cli/README.md
  Purpose: Landmark index for the CLI Maven module
  Audience: Agents and humans
  Update when: CLI layout or usage changes
-->

# CLI module

Maven artifact `com.aethelgard:cli` — headless **simulation runner** over one `ProductSession`, sharing `CommandDispatch` with the studio console (F-049).

**Depends on:** `product` (ADR-010). Never depended on by `engine` or `product`. `ui` depends on this module **only** for the shared command language / console.

## Run

From repo root (after `mvnw -pl cli -am package`):

```text
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="--steps 3"
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="--seed 42 -c \"session get\""
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="session get"
```

## Flags

| Flag | Default | Meaning |
|------|---------|---------|
| `--seed S` | `0` | Recorded RNG seed; geometry stays `WorldSpec.DEFAULT` (8×8) |
| `--steps N` | (omit) | Advance N Steps via `session advance N`, then dump if no `-c` |
| `-c` / `--command LINE` | (none) | Dispatcher line on the same session (repeatable) |

Empty argv → dump at step 0 (seed 0). Bare argv with no `-` flags → one dispatcher line on DEFAULT.

## Commands (`CommandDispatch`)

Noun/verb language (F-048): `session get`, `session advance [N]`, `list pool|schema|systems|diag`, `pool.<field> get`, `schema get`, `systems.<id> get`, `diag…`, plus deprecated aliases (`status`, `advance`, `dump`, …).

## Output

`--steps` / empty argv: `session get dump` (settled world text). `-c` batches: concatenated command outputs; first failure stops with non-zero exit.

**Docs:** [docs/product/architecture.md](../docs/product/architecture.md) · [docs/blockers/F-049.md](../docs/blockers/F-049.md)
