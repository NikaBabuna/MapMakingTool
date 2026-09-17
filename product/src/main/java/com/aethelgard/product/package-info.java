/*
 * File: product/src/main/java/com/aethelgard/product/package-info.java
 * Purpose: Package root for the Aethelgard product
 * Audience: Agents implementing product code
 * Update when: Package ownership or root documentation changes
 */

/**
 * Aethelgard product root package — simulation values only (no Swing).
 *
 * <p>Depends on {@code com.aethelgard.engine}. Must not be depended on by the engine. {@code ui}
 * and {@code cli} depend on this package (ADR-010). World state is Pool fields {@code elevation}
 * and {@code plates} ({@link com.aethelgard.product.Grid}). Generation is a product {@code
 * EngineSystem} on category {@code world/tectonics}. {@link
 * com.aethelgard.product.ProductSession} owns a run. {@link com.aethelgard.product.WorldDump}
 * formats a settled snapshot.
 */
package com.aethelgard.product;
