<!--
  File: ui/README.md
  Purpose: Landmark index for the UI Maven module
  Audience: Agents and humans
  Update when: UI layout or usage changes
-->

# UI module

Maven artifact `com.aethelgard:ui` — basic skeleton Step advance / settled view.

**Depends on:** `engine` (one-way). Never depended on by `engine`.

## Headless logic

`UiController` — create run, `advance()`, `settledText()` via User View. **No Swing.** Covered by tests.

## Interactive

From the repo root in **cmd** (recommended):

```bat
run-ui.cmd
```

Or manually — **install** (not just package), then run only `ui`:

```bat
mvnw -pl ui -am install -DskipTests
mvnw -pl ui exec:java
```

Why: `package` builds jars under `target/`, but a later `mvnw -pl ui exec:java` resolves `engine` from your local Maven repo (`.m2`). Without `install`, Maven cannot find `com.aethelgard:engine:0.1.0-SNAPSHOT`.

Do **not** use `mvnw -pl ui -am exec:java` alone — Maven may run `exec:java` on the parent aggregator and fail.

Optional seed: `mvnw -pl ui exec:java -Dexec.args="--initial 10"`

Window: settled text + **Advance** button (`SkeletonFrame`). Do not construct `JFrame` in tests.

**Docs:** [docs/engine/architecture.md](../docs/engine/architecture.md)
