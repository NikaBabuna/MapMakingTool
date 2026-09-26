/*
 * File: engine/src/test/java/com/aethelgard/engine/merge/MergeRulesTest.java
 * Purpose: Proves how conflicting writes settle: the four built-in rules, provenance, and a custom rule
 * Audience: Agents / CI
 * Update when: A merge rule, FieldMergeType, or provenance changes
 */

package com.aethelgard.engine.merge;

import static com.aethelgard.engine.TestSystems.writing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aethelgard.engine.diag.EngineDiagnostics;
import com.aethelgard.engine.event.CategoryTree;
import com.aethelgard.engine.pool.Engine;
import com.aethelgard.engine.pool.EngineConfig;
import com.aethelgard.engine.pool.EngineSetup;
import com.aethelgard.engine.system.EngineSystem;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MergeRulesTest {

  private static final CategoryTree TREE = CategoryTree.of("world");

  /** Proves F-068 FR-13 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("STATIC and DESTRUCTIVE keep the write of the lowest system id, whatever the registration order")
  void pickOneRulesTakeTheLowestSystemId() {
    for (FieldType rule : List.of(FieldType.STATIC, FieldType.DESTRUCTIVE)) {
      Engine engine =
          run(rule, 0L, List.of(
              writing(TREE, "world", "zebra", "f", 1L),
              writing(TREE, "world", "mole", "f", 2L),
              writing(TREE, "world", "aardvark", "f", 3L)));
      assertEquals(3L, engine.settled().field("f"), rule + ": aardvark sorts first");
    }
  }

  /** Proves F-068 FR-13 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("INCREMENT adds every write to the standing value")
  void incrementAddsEveryWrite() {
    Engine engine =
        run(FieldType.INCREMENT, 10L, List.of(
            writing(TREE, "world", "a", "f", 3L),
            writing(TREE, "world", "b", "f", 5L)));
    assertEquals(18L, engine.settled().field("f"));

    engine.advance();
    assertEquals(26L, engine.settled().field("f"));
  }

  /** Proves F-068 FR-13 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("CONSTANT keeps the standing value whatever is written")
  void constantKeepsTheStandingValue() {
    Engine engine = run(FieldType.CONSTANT, 7L, List.of(writing(TREE, "world", "a", "f", 999L)));
    engine.advance(3);

    assertEquals(7L, engine.settled().field("f"));
  }

  /** Proves F-068 FR-13 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("Conflicting writes carry the id of the system that made them")
  void conflictingWritesCarryProvenance() {
    Engine engine =
        run(FieldType.STATIC, 0L, List.of(
            writing(TREE, "world", "alpha", "f", 10L),
            writing(TREE, "world", "beta", "f", 20L)));

    Map<String, Object> bySystem =
        engine.lastStepOutput().asMap().get("f").stream()
            .collect(Collectors.toMap(ProvenancedWrite::systemId, ProvenancedWrite::value));
    assertEquals(Map.of("alpha", 10L, "beta", 20L), bySystem);
  }

  /** Proves F-068 FR-14 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("A custom merge type governs its field")
  void customMergeTypeGovernsItsField() {
    FieldMergeType largest =
        (standing, writers) ->
            writers.stream().map(w -> (Long) w.value()).max(Long::compare).orElse((Long) standing);
    Engine engine =
        Engine.create(
            new EngineConfig(0L, List.of("world"), Map.of("peak", 0L)),
            new EngineSetup(
                TREE, List.of(),
                List.of(
                    writing(TREE, "world", "a", "peak", 4L),
                    writing(TREE, "world", "b", "peak", 9L),
                    writing(TREE, "world", "c", "peak", 6L)),
                FieldSchema.of("peak", largest),
                EngineDiagnostics.noop()));

    assertEquals(9L, engine.settled().field("peak"));
  }

  /** Proves F-068 FR-14 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("STATIC keeps values that are not numbers")
  void staticKeepsNonNumericValues() {
    record Region(String name, Set<Integer> cells) {}
    Region region = new Region("north", Set.of(1, 2, 3));
    Engine engine = run(FieldType.STATIC, "none", List.of(writing(TREE, "world", "a", "f", region)));

    assertEquals(region, engine.settled().field("f"));
  }

  /** Proves F-068 FR-14 (docs/paperwork/steps/F-068.md). */
  @Test
  @DisplayName("INCREMENT refuses a write that is not a number")
  void incrementRefusesNonNumericWrites() {
    assertThrows(
        IllegalArgumentException.class,
        () -> run(FieldType.INCREMENT, 0L, List.of(writing(TREE, "world", "a", "f", "three"))));
  }

  private static Engine run(FieldType rule, Object standing, List<EngineSystem> systems) {
    return Engine.create(
        new EngineConfig(0L, List.of("world"), Map.of("f", standing)),
        new EngineSetup(TREE, List.of(), systems, FieldSchema.of("f", rule), EngineDiagnostics.noop()));
  }
}
