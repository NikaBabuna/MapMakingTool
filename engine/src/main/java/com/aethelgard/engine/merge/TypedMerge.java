/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java
 * Purpose: Resolve provenanced System writes by field type and apply rules
 * Audience: Agents implementing the engine
 * Update when: Merge rules change
 */

package com.aethelgard.engine.merge;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * System → Pool typed merge.
 *
 * <p><b>Static / Destructive pick-one:</b> lexicographically smallest {@code systemId} wins.
 * Resolvers see {@link ProvenancedWrite} pairs, not bare values.
 */
public final class TypedMerge {

  private TypedMerge() {}

  /**
   * Merges Step output into new field values.
   *
   * @param schema field types
   * @param standing current Pool field values
   * @param buffer provenanced writes this Step
   * @return full field map after merge (includes untouched standing fields)
   */
  public static Map<String, Long> merge(
      FieldSchema schema, Map<String, Long> standing, StepOutputBuffer buffer) {
    Map<String, Long> result = new LinkedHashMap<>(standing);
    for (var entry : buffer.asMap().entrySet()) {
      String field = entry.getKey();
      List<ProvenancedWrite> writers = entry.getValue();
      FieldType type = schema.typeOf(field);
      long standingValue = standing.getOrDefault(field, 0L);
      result.put(field, resolve(type, standingValue, writers));
    }
    return Map.copyOf(result);
  }

  private static long resolve(FieldType type, long standing, List<ProvenancedWrite> writers) {
    return switch (type) {
      case INCREMENT -> {
        long sum = 0L;
        for (ProvenancedWrite w : writers) {
          sum += w.value();
        }
        yield standing + sum;
      }
      case CONSTANT -> standing;
      case STATIC, DESTRUCTIVE -> pickOne(writers).value();
    };
  }

  /** Deterministic pick-one: lowest systemId (lexicographic). */
  public static ProvenancedWrite pickOne(List<ProvenancedWrite> writers) {
    return writers.stream()
        .min(Comparator.comparing(ProvenancedWrite::systemId))
        .orElseThrow(() -> new IllegalStateException("empty writer set"));
  }
}
