/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/FieldSchema.java
 * Purpose: Declared field names and merge types for a run
 * Audience: Agents / callers / tests
 * Update when: Schema wiring shape changes
 */

package com.aethelgard.engine.merge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Field name → {@link FieldType} for typed merge. */
public final class FieldSchema {

  private final Map<String, FieldType> types;

  private FieldSchema(Map<String, FieldType> types) {
    this.types = Map.copyOf(types);
  }

  public static FieldSchema empty() {
    return new FieldSchema(Map.of());
  }

  public static FieldSchema of(Map<String, FieldType> types) {
    Objects.requireNonNull(types, "types");
    return new FieldSchema(new LinkedHashMap<>(types));
  }

  public static FieldSchema of(String name, FieldType type) {
    return of(Map.of(name, type));
  }

  public FieldType typeOf(String field) {
    FieldType type = types.get(field);
    if (type == null) {
      throw new IllegalArgumentException("unknown field: " + field);
    }
    return type;
  }

  public boolean has(String field) {
    return types.containsKey(field);
  }

  public Map<String, FieldType> asMap() {
    return types;
  }

  public boolean isEmpty() {
    return types.isEmpty();
  }
}
