/*
 * File: engine/src/main/java/com/aethelgard/engine/event/CategoryTree.java
 * Purpose: Hierarchical event categories for ancestry claiming
 * Audience: Agents / callers configuring claimers
 * Update when: Category tree authorship model changes
 */

package com.aethelgard.engine.event;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Framework category tree. Paths use {@code /} (e.g. {@code world/combat}). Parent paths are
 * created as needed.
 */
public final class CategoryTree {

  private final Map<String, Category> byPath = new LinkedHashMap<>();

  private CategoryTree() {}

  /** Empty tree. */
  public static CategoryTree empty() {
    return new CategoryTree();
  }

  /** Tree containing each path and its ancestors. */
  public static CategoryTree of(String... paths) {
    CategoryTree tree = new CategoryTree();
    for (String path : paths) {
      tree.ensure(path);
    }
    return tree;
  }

  public static CategoryTree of(Collection<String> paths) {
    CategoryTree tree = new CategoryTree();
    for (String path : paths) {
      tree.ensure(path);
    }
    return tree;
  }

  public Category get(String path) {
    Category category = byPath.get(normalize(path));
    if (category == null) {
      throw new IllegalArgumentException("Unknown category path: " + path);
    }
    return category;
  }

  public boolean contains(String path) {
    return byPath.containsKey(normalize(path));
  }

  public Category ensure(String path) {
    String normalized = normalize(path);
    Category existing = byPath.get(normalized);
    if (existing != null) {
      return existing;
    }
    int slash = normalized.lastIndexOf('/');
    Category parent = null;
    if (slash >= 0) {
      parent = ensure(normalized.substring(0, slash));
    }
    Category created = new Category(normalized, parent);
    byPath.put(normalized, created);
    return created;
  }

  public List<Category> resolveAll(List<String> paths) {
    return paths.stream().map(this::ensure).toList();
  }

  private static String normalize(String path) {
    Objects.requireNonNull(path, "path");
    String trimmed = path.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("category path must not be blank");
    }
    if (trimmed.startsWith("/") || trimmed.endsWith("/")) {
      throw new IllegalArgumentException("category path must not start or end with /: " + path);
    }
    return trimmed;
  }
}
