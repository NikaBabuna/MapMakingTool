/*
 * File: engine/src/main/java/com/aethelgard/engine/pool/PoolSnapshot.java
 * Purpose: Immutable settled Pool state after a completed Step
 * Audience: Agents / callers / tests
 * Update when: Observable Pool fields change
 */

package com.aethelgard.engine.pool;

import java.util.Map;
import java.util.Objects;

/**
 * Settled view of the Pool after a Step completes. Callers never observe a half-applied Step.
 *
 * @param value current trivial Pool heartbeat value
 * @param updateCount how many times {@link Pool#update} has run (equals completed Steps)
 * @param fields typed field map after merge apply (empty when no schema)
 */
public record PoolSnapshot(long value, int updateCount, Map<String, Long> fields) {

  public PoolSnapshot {
    fields = Map.copyOf(Objects.requireNonNullElse(fields, Map.of()));
  }

  /** Convenience for F-002-era snapshots with no typed fields. */
  public PoolSnapshot(long value, int updateCount) {
    this(value, updateCount, Map.of());
  }

  public long field(String name) {
    Long v = fields.get(name);
    if (v == null) {
      throw new IllegalArgumentException("unknown or unset field: " + name);
    }
    return v;
  }

  public long fieldOrZero(String name) {
    return fields.getOrDefault(name, 0L);
  }
}
