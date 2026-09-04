/*
 * File: engine/src/main/java/com/aethelgard/engine/user/InputKind.java
 * Purpose: Persistence behavior for a named user action
 * Audience: Agents / callers / tests
 * Update when: Input kind set changes
 */

package com.aethelgard.engine.user;

/** How an action behaves in the User Input register. */
public enum InputKind {
  /** Latches on press; stays active until {@link UserInput#consume(String)}. */
  PERSISTENT,
  /** Active in Input View only while physically held at staging. */
  NON_PERSISTENT
}
