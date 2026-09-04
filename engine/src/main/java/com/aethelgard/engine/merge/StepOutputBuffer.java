/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/StepOutputBuffer.java
 * Purpose: Step-level collection of provenanced System writes
 * Audience: Agents implementing the engine loop
 * Update when: Output buffer shape changes
 */

package com.aethelgard.engine.merge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Shared Step output buffer: field → provenanced writes from independent Systems. */
public final class StepOutputBuffer {

  private final Map<String, List<ProvenancedWrite>> writes = new LinkedHashMap<>();

  public void add(String field, ProvenancedWrite write) {
    Objects.requireNonNull(field, "field");
    Objects.requireNonNull(write, "write");
    writes.computeIfAbsent(field, k -> new ArrayList<>()).add(write);
  }

  public Map<String, List<ProvenancedWrite>> asMap() {
    Map<String, List<ProvenancedWrite>> copy = new LinkedHashMap<>();
    for (var e : writes.entrySet()) {
      copy.put(e.getKey(), List.copyOf(e.getValue()));
    }
    return Map.copyOf(copy);
  }

  public boolean isEmpty() {
    return writes.isEmpty();
  }
}
