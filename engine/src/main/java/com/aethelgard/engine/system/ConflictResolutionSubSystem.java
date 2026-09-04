/*
 * File: engine/src/main/java/com/aethelgard/engine/system/ConflictResolutionSubSystem.java
 * Purpose: Deterministic order for overlapping Sub-System write-ranges
 * Audience: Agents / tests
 * Update when: Conflict-resolution contract changes
 */

package com.aethelgard.engine.system;

import java.util.List;

/**
 * Config-supplied resolver for a System whose Sub-Systems have overlapping write-ranges.
 *
 * <p>Operates on the full conflict set at once — one deterministic order for N-way conflicts.
 */
@FunctionalInterface
public interface ConflictResolutionSubSystem {

  /**
   * @param conflictSet Sub-Systems that share at least one write-range with another in the set
   * @return deterministic execution order covering the conflict set
   */
  List<SubSystem> resolveOrder(List<SubSystem> conflictSet);
}
