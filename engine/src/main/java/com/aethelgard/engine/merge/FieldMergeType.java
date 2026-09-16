/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/FieldMergeType.java
 * Purpose: Pluggable System→Pool merge rule for one field
 * Audience: Engine defaults / product custom types
 * Update when: Merge-type port contract changes
 */

package com.aethelgard.engine.merge;

import java.util.List;

/**
 * How conflicting provenanced writes resolve for a field.
 *
 * <p>Engine defaults: {@link FieldType}. Product may supply custom implementations without editing
 * the engine.
 */
@FunctionalInterface
public interface FieldMergeType {

  /**
   * Merges this Step's writes for one field.
   *
   * @param standing current Pool value (may be {@code null} if never set)
   * @param writers non-empty provenanced writes this Step
   * @return value to store in the Pool (must not be {@code null})
   */
  Object merge(Object standing, List<ProvenancedWrite> writers);
}
