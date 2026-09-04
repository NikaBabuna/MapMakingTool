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

```text
mvnw -pl ui -am exec:java -Dexec.mainClass=com.aethelgard.ui.SkeletonApp
```

Optional: `-Dexec.args="--initial 10"`.

Window: settled text + **Advance** button (`SkeletonFrame`). Do not construct `JFrame` in tests.

**Docs:** [docs/engine/architecture.md](../docs/engine/architecture.md)
