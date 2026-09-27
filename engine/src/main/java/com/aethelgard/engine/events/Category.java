/*
 * File: engine/src/main/java/com/aethelgard/engine/events/Category.java
 * Purpose: Named node in the event category tree
 * Audience: Agents implementing engine / claimers
 * Update when: Category identity model changes
 */

package com.aethelgard.engine.events;

import java.util.Objects;

/** A category path in the tree (e.g. {@code world/combat}). */
public final class Category {

  private final String path;
  private final Category parent;

  Category(String path, Category parent) {
    this.path = Objects.requireNonNull(path, "path");
    this.parent = parent;
  }

  public String path() {
    return path;
  }

  public Category parent() {
    return parent;
  }

  /** True if {@code this} is {@code ancestor} or a descendant of {@code ancestor}. */
  public boolean isSelfOrDescendantOf(Category ancestor) {
    Objects.requireNonNull(ancestor, "ancestor");
    for (Category c = this; c != null; c = c.parent) {
      if (c.equals(ancestor)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Category category)) {
      return false;
    }
    return path.equals(category.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return path;
  }
}
