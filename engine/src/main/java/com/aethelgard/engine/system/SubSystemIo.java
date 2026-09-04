/*
 * File: engine/src/main/java/com/aethelgard/engine/system/SubSystemIo.java
 * Purpose: Input/output buffers for one Sub-System execution
 * Audience: Sub-System implementations
 * Update when: IO contract changes
 */

package com.aethelgard.engine.system;

import com.aethelgard.engine.pool.PoolSnapshot;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Per-Sub-System view: read settled Pool fields + System staging; write into declared ranges.
 */
public final class SubSystemIo {

  private final PoolSnapshot pool;
  private final Map<String, Long> staging;
  private final Set<String> allowedWrites;

  SubSystemIo(PoolSnapshot pool, Map<String, Long> staging, Set<String> allowedWrites) {
    this.pool = Objects.requireNonNull(pool, "pool");
    this.staging = Objects.requireNonNull(staging, "staging");
    this.allowedWrites = Set.copyOf(allowedWrites);
  }

  /** Pool field after this Step's Pool.update (same snapshot for all Systems). */
  public long readPool(String field) {
    return pool.field(field);
  }

  /** Trivial heartbeat value from the Pool snapshot. */
  public long poolValue() {
    return pool.value();
  }

  /** Staging value written earlier in this System run, or null. */
  public Long readStaging(String field) {
    return staging.get(field);
  }

  public void write(String field, long value) {
    if (!allowedWrites.contains(field)) {
      throw new IllegalArgumentException(
          "Sub-System write outside declared range: " + field + " allowed=" + allowedWrites);
    }
    staging.put(field, value);
  }

  /** Snapshot of staging for tests. */
  Map<String, Long> stagingView() {
    return Map.copyOf(new LinkedHashMap<>(staging));
  }
}
