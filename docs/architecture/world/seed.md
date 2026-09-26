<!--
  File: docs/architecture/world/seed.md
  Purpose: ProductHost.create, Plates, PlateVelocities.seed, Occupancy.seed — step 0: the first world from a size and a seed
  Audience: Agents and humans
  Update when: ProductHost.create, Plates.seed, Plates.assign, Plates.dist2, Plates.mix, or PlateVelocities.seed changes
-->

# Seed

The first world is a still ocean cut into a dozen or more plates. A single number, the seed, decides how many plates there are, where each one's centre lies, and which way each one drifts; every cell belongs to the nearest centre. Nothing moves in step 0, and all later generations start from this world.

## What it reads

A `WorldSpec` $(W, H, s)$: the width, the height, and the seed. `ProductHost.create()` uses `WorldSpec.DEFAULT`.

## What it writes

An `Engine` whose step 0 has settled with these nine field values: flat `elevation`, the nearest-site `plates`, the seeded `plate_velocity`, the `plate_registry` counted from them, the traced `boundaries`, `area_flux` and `motion_intent` from those contacts, one `occupancy` key per cell, and `lockers` all at $T_{\mathrm{ocean}}$. A null spec throws `NullPointerException`; the site and weight helpers throw `IllegalArgumentException` for a side below 1 or a row outside the map.

## Model

**Plate count.** $N(s) = 12 + (s \bmod 13) \in [12, 24]$, with $\bmod$ the floor modulus.

**Mixing.** With the 64-bit constants $\phi_1 = \texttt{0x9E3779B97F4A7C15}$, $\phi_2 = \texttt{0xBF58476D1CE4E5B9}$, and $\phi_3 = \texttt{0x94D049BB133111EB}$, arithmetic modulo $2^{64}$, $\oplus$ exclusive or, and $\gg$ the unsigned shift,

$$\mu(s, i, j) = \mathrm{sm}\bigl(s \oplus i\phi_1 \oplus j\phi_2\bigr), \qquad \mathrm{sm}(z) = z_3 \oplus (z_3 \gg 31),$$

$$z_1 = z + \phi_1, \quad z_2 = (z_1 \oplus (z_1 \gg 30))\,\phi_2, \quad z_3 = (z_2 \oplus (z_2 \gg 27))\,\phi_3 ,$$

the SplitMix64 finaliser of a key built from the seed, the site index $i$, and an axis $j$.

`Plates.splitmix64` in [`Plates.java`](../../../product/src/main/java/com/aethelgard/product/Plates.java):

```java
z += MIX_GOLDEN;
z = (z ^ (z >>> 30)) * MIX_SILVER;
z = (z ^ (z >>> 27)) * MIX_BRONZE;
return z ^ (z >>> 31);
```

**Sites.** Site $i < N$ is $(x_i, y_i) = \bigl(\mu(s,i,0) \bmod W,\; \mu(s,i,1) \bmod H\bigr)$.

**Distance.** Rows are weighted by the latitude factor

$$q(y) = \max\Bigl(1,\; \operatorname{round}\bigl(1024 \sin\tfrac{\pi (y + 1/2)}{H}\bigr)\Bigr),$$

so an east–west step counts fully at the equator and shrinks toward the poles. The squared distance from cell $(x, y)$ to site $i$ wraps east–west and is flat north–south:

$$\delta_x = \min\bigl(|x - x_i|,\; W - |x - x_i|\bigr), \qquad d^2_i(x, y) = \Bigl\lfloor \frac{\delta_x\, q(y)}{1024} \Bigr\rfloor^2 + (y - y_i)^2 .$$

`Plates.dist2` in [`Plates.java`](../../../product/src/main/java/com/aethelgard/product/Plates.java):

```java
long dx = toroidalDelta(x, sx, width);
long dy = (long) y - (long) sy;
long dxw = (dx * cosQ(y, height)) / COS_SCALE;
return dxw * dxw + dy * dy;
```

**Plates.** Every cell takes the nearest site, and the lowest index wins a tie:

$$P_0(c) = \min \operatorname*{arg\,min}_{i < N} d^2_i(c).$$

**Velocities.** With $u(z) = (z \bmod 3) - 1 \in \{-1, 0, 1\}$, plate $i$ drifts by $v_i = \bigl(u(\mu(s,i,2)),\; u(\mu(s,i,3))\bigr)$. If every $v_i = (0, 0)$, then $v_0 := (1, 0)$, so at least one plate moves.

**Crust.** $O_0(x, y) = yW + x$, $T_0 \equiv T_{\mathrm{ocean}}$ on $WH$ lockers, and $E_0 \equiv 0$.

## Procedure

1. `create(spec)` rejects a null spec and builds every step-0 value. [`ProductHost.create`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).
2. The elevation is the all-zero grid. [`Grid.zeros`](../../../product/src/main/java/com/aethelgard/product/Grid.java).
3. `Plates.seed` computes $N$ with `count`, places each site with `siteX` and `siteY`, which reduce `mix` modulo the side, and assigns the cells. [`Plates.seed`](../../../product/src/main/java/com/aethelgard/product/Plates.java).
4. `assign` gives every cell the site of least `dist2`, keeping the first (lowest) index on a tie. `dist2` wraps the column difference and scales it by `cosQ` of the cell's row, with `COS_SCALE` = 1024. [`Plates.assign`](../../../product/src/main/java/com/aethelgard/product/Plates.java).
5. The occupancy gives cell $(x, y)$ the key $yW + x$, and the locker table holds $WH$ columns at $T_{\mathrm{ocean}}$. [`Occupancy.seed`](../../../product/src/main/java/com/aethelgard/product/Occupancy.java).
6. The velocities apply `unit` to the mix of axis `AXIS_VX` (2) and axis `AXIS_VY` (3) for each of the $N$ plates, and force plate 0 east when no plate moves. [`PlateVelocities.seed`](../../../product/src/main/java/com/aethelgard/product/PlateVelocities.java).
7. The registry is counted from the plates. The contacts are traced ([boundaries](boundaries.md)), and the budgets and intents are computed from them with the area-only loser, because no crust is passed ([interaction](interaction.md)). [`ProductHost.create`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).
8. `create` builds an `EngineConfig` with heartbeat 0, no scripted paths, and the nine values as field seeds, and calls `Engine.create` with `ProductHost.setup()`. Step 0 fires no tick, so the settled fields are exactly the seeds. [`ProductHost.create`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java).

## What is true afterwards

After `create`, the step index is 0; every cell holds a plate id below $N$, with $12 \le N \le 24$; at least one plate moves; every cell has elevation 0 because every locker is exactly $T_{\mathrm{ocean}}$; and the budgets and intents describe the step-0 contacts. The same spec always builds the same world. The site distance ignores the polar wrap of the [topology](topology.md): a site near one pole does not reach across it. Two helpers of `Plates` are not called by the pipeline: `hasForeignNeighbor`, which clips at the map edges, and the deprecated `dist2Cylinder`, which has no latitude weight.

## Cost

Assignment is $O(W H N)$ distance evaluations, and each one computes $q(y)$ with a sine: about $5 \times 10^7$ for the $1920 \times 1080$ window with 24 plates.

## Where it lives

| Piece | Type | Members | Path |
|-------|------|---------|------|
| Step-0 world | `ProductHost` | `create` | [`product/src/main/java/com/aethelgard/product/ProductHost.java`](../../../product/src/main/java/com/aethelgard/product/ProductHost.java) |
| Partition | `Plates` | `COS_SCALE`, `count`, `mix`, `siteX`, `siteY`, `cosQ`, `assign`, `seed`, `dist2`, `dist2Cylinder`, `hasForeignNeighbor`, `splitmix64`, `toroidalDelta` | [`product/src/main/java/com/aethelgard/product/Plates.java`](../../../product/src/main/java/com/aethelgard/product/Plates.java) |
| Drift | `PlateVelocities` | `PlateVelocities.seed`, `unit`, `AXIS_VX`, `AXIS_VY` | [`product/src/main/java/com/aethelgard/product/PlateVelocities.java`](../../../product/src/main/java/com/aethelgard/product/PlateVelocities.java) |
| Step-0 keys | `Occupancy` | `Occupancy.seed` | [`product/src/main/java/com/aethelgard/product/Occupancy.java`](../../../product/src/main/java/com/aethelgard/product/Occupancy.java) |

Parent: [one generation](README.md). The value types: [fields](fields.md). Why a world starts all oceanic: [ADR-013](../../paperwork/decisions/ADR-013-crust-topology.md).
