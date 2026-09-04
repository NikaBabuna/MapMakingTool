/*
 * File: engine/src/main/java/com/aethelgard/engine/system/SystemConfig.java
 * Purpose: Selects and wires Sub-Systems for one System
 * Audience: Agents / callers / tests
 * Update when: System composition shape changes
 */

package com.aethelgard.engine.system;

import com.aethelgard.engine.event.Category;
import java.util.List;
import java.util.Objects;

/**
 * Grammar for one System: identity, assigned claim category, Sub-Systems, optional conflict
 * resolver.
 *
 * @param id System id (also used as provenance on OUT_SYS)
 * @param assignedCategory category for ancestry claiming
 * @param subSystems ordered vocabulary; conflict resolver may reorder overlapping sets
 * @param conflictResolver required when write-ranges overlap; null when all disjoint
 */
public record SystemConfig(
    String id,
    Category assignedCategory,
    List<SubSystem> subSystems,
    ConflictResolutionSubSystem conflictResolver) {

  public SystemConfig {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(assignedCategory, "assignedCategory");
    subSystems = List.copyOf(Objects.requireNonNull(subSystems, "subSystems"));
  }
}
