/*
 * File: engine/src/main/java/com/aethelgard/engine/merge/FieldType.java
 * Purpose: Declared merge type for a Pool field
 * Audience: Agents implementing typed merge
 * Update when: Merge types change (Delete Request later)
 */

package com.aethelgard.engine.merge;

/** How conflicting System writes to a field resolve at Pool merge. */
public enum FieldType {
  /** Exactly one value wins — deterministic pick-one by system id. */
  STATIC,
  /** All conflicting values sum (commutative). */
  INCREMENT,
  /** New writes are no-ops; standing Pool value kept. */
  CONSTANT,
  /** Exactly one write valid per Step — deterministic pick-one by system id. */
  DESTRUCTIVE
}
