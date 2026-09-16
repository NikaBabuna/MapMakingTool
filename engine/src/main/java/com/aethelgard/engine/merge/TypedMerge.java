/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/TypedMerge.java
 * Purpose: Resolve provenanced System writes by field merge type and apply
 * Audience: Agents implementing the engine
 * Update when: Merge orchestration changes
 */

package com.aethelgard.engine.merge;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * System → Pool typed merge.
 *
 * <p>Each field's {@link FieldMergeType} decides resolution. Default types: {@link FieldType}.
 * Static/Destructive pick-one helpers remain here for reuse.
 */
public final class TypedMerge {

  private TypedMerge() {}

  /**
   * Merges Step output into new field values.
   *
   * @param schema field merge types
   * @param standing current Pool field values
   * @param buffer provenanced writes this Step
   * @return full field map after merge (includes untouched standing fields)
   */
  public static Map<String, Object> merge(
      FieldSchema schema, Map<String, Object> standing, StepOutputBuffer buffer) {
    Map<String, Object> result = new LinkedHashMap<>(standing);
    for (var entry : buffer.asMap().entrySet()) {
      String field = entry.getKey();
      List<ProvenancedWrite> writers = entry.getValue();
      FieldMergeType type = schema.typeOf(field);
      Object standingValue = standing.get(field);
      Object merged = type.merge(standingValue, writers);
      result.put(field, Objects.requireNonNull(merged, "merge result for " + field));
    }
    return Map.copyOf(result);
  }

  /** Deterministic pick-one: lowest systemId (lexicographic). */
  public static ProvenancedWrite pickOne(List<ProvenancedWrite> writers) {
    return writers.stream()
        .min(Comparator.comparing(ProvenancedWrite::systemId))
        .orElseThrow(() -> new IllegalStateException("empty writer set"));
  }
}
