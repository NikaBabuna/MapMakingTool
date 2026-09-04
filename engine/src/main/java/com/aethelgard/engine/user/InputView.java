/*
 * File: engine/src/main/java/com/aethelgard/engine/user/InputView.java
 * Purpose: Frozen snapshot of active inputs for one Step's Pool compute
 * Audience: Pool / tests
 * Update when: Input View shape changes
 */

package com.aethelgard.engine.user;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Staged "now" for one Step — everything gathered counts as simultaneous at compute time.
 *
 * @param active action names present in this snapshot
 */
public record InputView(Set<String> active) {

  public InputView {
    active = Set.copyOf(new LinkedHashSet<>(Objects.requireNonNull(active, "active")));
  }

  public static InputView empty() {
    return new InputView(Set.of());
  }

  public boolean isActive(String action) {
    return active.contains(action);
  }
}
