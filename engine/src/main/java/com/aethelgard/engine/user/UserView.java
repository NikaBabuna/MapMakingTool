/*
 * File: engine/src/main/java/com/aethelgard/engine/user/UserView.java
 * Purpose: Read-only port — frame from settled Pool each Step
 * Audience: Agents / callers / tests / Engine
 * Update when: User View contract changes
 */

package com.aethelgard.engine.user;

import com.aethelgard.engine.pool.PoolSnapshot;

/**
 * Decoupled from merge: reads settled Pool only and produces the user-visible frame (any means).
 *
 * <p>Must not write Pool state.
 */
@FunctionalInterface
public interface UserView {

  void onSettled(PoolSnapshot settled);

  static UserView noop() {
    return settled -> {};
  }
}
