/*
 * File: engine/src/main/java/com/aethelgard/engine/user/UserInput.java
 * Purpose: Typed register of pressed / latched named actions
 * Audience: Agents / callers / tests / Engine
 * Update when: Input register API changes
 */

package com.aethelgard.engine.user;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * User Input register. Meaning depends on {@link InputKind}, not raw hold alone.
 *
 * <p>No OS / UI toolkit — callers press and release named string actions.
 */
public final class UserInput {

  private final Map<String, InputKind> kinds = new LinkedHashMap<>();
  private final Set<String> held = new LinkedHashSet<>();
  private final Set<String> latched = new LinkedHashSet<>();

  /** Declares an action and its persistence kind (idempotent if same kind). */
  public UserInput register(String action, InputKind kind) {
    Objects.requireNonNull(action, "action");
    Objects.requireNonNull(kind, "kind");
    InputKind existing = kinds.putIfAbsent(action, kind);
    if (existing != null && existing != kind) {
      throw new IllegalArgumentException(
          "action already registered as " + existing + ": " + action);
    }
    return this;
  }

  public void press(String action) {
    requireRegistered(action);
    held.add(action);
    if (kinds.get(action) == InputKind.PERSISTENT) {
      latched.add(action);
    }
  }

  public void release(String action) {
    requireRegistered(action);
    held.remove(action);
  }

  /** Clears a persistent latch (no-op for non-persistent). */
  public void consume(String action) {
    requireRegistered(action);
    latched.remove(action);
  }

  public boolean isHeld(String action) {
    return held.contains(action);
  }

  public boolean isLatched(String action) {
    return latched.contains(action);
  }

  public InputKind kindOf(String action) {
    return kinds.get(action);
  }

  /**
   * Builds the Input View for this Step: non-persistent if held; persistent if latched.
   */
  public InputView stage() {
    Set<String> active = new LinkedHashSet<>();
    for (var e : kinds.entrySet()) {
      String action = e.getKey();
      if (e.getValue() == InputKind.NON_PERSISTENT) {
        if (held.contains(action)) {
          active.add(action);
        }
      } else if (latched.contains(action)) {
        active.add(action);
      }
    }
    return new InputView(active);
  }

  /** Consumes every persistent action that was active in {@code view}. */
  public void consumePersistentPresentIn(InputView view) {
    Objects.requireNonNull(view, "view");
    for (String action : view.active()) {
      if (kinds.get(action) == InputKind.PERSISTENT) {
        consume(action);
      }
    }
  }

  private void requireRegistered(String action) {
    Objects.requireNonNull(action, "action");
    if (!kinds.containsKey(action)) {
      throw new IllegalArgumentException("unregistered action: " + action);
    }
  }
}
