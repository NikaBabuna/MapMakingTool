<!--
  File: cli/README.md
  Purpose: Landmark index for the CLI Maven module
  Audience: Agents and humans
  Update when: CLI layout or usage changes
-->

# CLI module

Maven artifact `com.aethelgard:cli` — headless runner over the Pool-System Framework.

**Depends on:** `engine` (one-way). Never depended on by `engine`.

## Run

From repo root (after `mvnw -pl cli -am package`):

```text
java -cp cli/target/classes;engine/target/classes;... com.aethelgard.cli.Main --steps 3 --initial 10
```

Or via Maven:

```text
mvnw -pl cli -am exec:java -Dexec.mainClass=com.aethelgard.cli.Main -Dexec.args="--steps 3 --initial 10"
```

## Flags

| Flag | Default | Meaning |
|------|---------|---------|
| `--steps N` | `0` | Additional Steps after create (create already completes Step 0) |
| `--initial V` | `0` | `EngineConfig` seed value |

## Output

Settled report lines: `stepIndex`, `value`, `updateCount`, and `fields=...` when typed fields exist.

**Docs:** [docs/engine/architecture.md](../docs/engine/architecture.md)
