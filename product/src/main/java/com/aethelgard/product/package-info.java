/*
 * File: product/src/main/java/com/aethelgard/product/package-info.java
 * Purpose: Package root for the Aethelgard product
 * Audience: Agents implementing product code
 * Update when: Package ownership or root documentation changes
 */

/**
 * Aethelgard product root package.
 *
 * <p>Depends on {@code com.aethelgard.engine}. Must not be depended on by the engine. World state
 * is Pool fields {@code elevation} and {@code plates} ({@link com.aethelgard.product.Grid}).
 * Generation is a product {@code EngineSystem} on category {@code world/tectonics}.
 * {@link com.aethelgard.product.WorldDump} formats a settled snapshot.
 * {@link com.aethelgard.product.ElevationRaster} and {@link com.aethelgard.product.MapController}
 * drive the product map window ({@link com.aethelgard.product.ProductApp}).
 */
package com.aethelgard.product;
