/*
 * File: product/src/main/java/com/aethelgard/product/world/motion/GeometryApplication.java
 * Purpose: Sub-System — apply area_flux, flood, fission, crumb, death; refresh registry
 * Audience: Product tectonics EngineSystem
 * Update when: Geometry apply / lifecycle rules change
 */

package com.aethelgard.product.world.motion;

import com.aethelgard.engine.system.SubSystem;
import com.aethelgard.engine.system.SubSystemIo;
import com.aethelgard.product.world.boundaries.Boundaries;
import com.aethelgard.product.world.boundaries.BoundaryContact;
import com.aethelgard.product.world.boundaries.BoundaryKind;
import com.aethelgard.product.world.crust.CrustPrecedence;
import com.aethelgard.product.world.fields.Grid;
import com.aethelgard.product.world.fields.Lockers;
import com.aethelgard.product.world.fields.PlateRegistry;
import com.aethelgard.product.world.fields.PlateVelocities;
import com.aethelgard.product.world.fields.WorldFields;
import com.aethelgard.product.world.interaction.AreaFlux;
import com.aethelgard.product.world.topology.SphereTopology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Applies standing {@code area_flux} to {@code plates}, floods sink, fissions, absorbs crumbs,
 * removes dead plates, advects plates and occupancy keys with current velocities, and rewrites
 * dense {@code plate_registry} + {@code plate_velocity}.
 */
public final class GeometryApplication implements SubSystem {

  /** Unowned / destroyed crust during apply; must not remain after flood. */
  public static final int SINK = -1;

  /**
   * Crumb absorb if {@code area * CRUMB_DENOMINATOR < width * height} (0.01% of world). Tuned from
   * the earlier 0.1% so visible microplates survive.
   */
  public static final int CRUMB_DENOMINATOR = 10_000;

  private static final int[][] DIRS = SphereTopology.ORTHO;

  /** Deterministic edge skip/extra for ragged fronts. */
  static boolean isRaggedSkip(long seed, int x, int y) {
    long z = seed ^ (((long) x) * 0x9E3779B97F4A7C15L) ^ (((long) y) * 0xBF58476D1CE4E5B9L);
    z ^= z >>> 30;
    return (z & 3L) == 0L; // ~25% of contact cells skipped
  }

  static boolean isRaggedExtra(long seed, int x, int y) {
    long z = seed ^ (((long) x) * 0x94D049BB133111EBL) ^ (((long) y) * 0x2545F4914F6CDD1DL);
    z ^= z >>> 27;
    return (z & 7L) == 0L; // ~12.5% take an extra orthogonal nibble
  }

  @Override
  public String id() {
    return "apply-geometry";
  }

  @Override
  public Set<String> writeRanges() {
    return Set.of(
        WorldFields.PLATES,
        WorldFields.PLATE_REGISTRY,
        WorldFields.PLATE_VELOCITY,
        WorldFields.OCCUPANCY);
  }

  @Override
  public void execute(SubSystemIo io) {
    Grid plates = requireGrid(io.readPool(WorldFields.PLATES), WorldFields.PLATES);
    // Prefer VelocityIntegration staging so fission inherits edge-driven velocities.
    PlateVelocities velocities = requireVelocities(readField(io, WorldFields.PLATE_VELOCITY));
    PlateRegistry registry = requireRegistry(readField(io, WorldFields.PLATE_REGISTRY));
    Boundaries boundaries = requireBoundaries(readField(io, WorldFields.BOUNDARIES));
    AreaFlux flux = requireFlux(readField(io, WorldFields.AREA_FLUX));
    Grid occupancy = requireGrid(io.readPool(WorldFields.OCCUPANCY), WorldFields.OCCUPANCY);
    Lockers lockers = requireLockers(readField(io, WorldFields.LOCKERS));
    int generationIndex = Math.toIntExact(io.poolValue()) - 1;
    boolean[][] skipOcc = new boolean[plates.height()][plates.width()];
    Result result =
        apply(plates, boundaries, flux, registry, velocities, occupancy, lockers, skipOcc);
    // Re-trace on remapped plates so ridge pairs match post-fission ids.
    Boundaries ridge = Boundaries.trace(result.plates(), result.velocities());
    PlateKinematics.AdvectResult moved =
        PlateKinematics.advect(
            result.plates(),
            occupancy,
            result.velocities(),
            generationIndex,
            ridge,
            boundaries,
            lockers,
            registry,
            skipOcc);
    PlateRegistry after = PlateRegistry.from(moved.plates(), moved.velocities());
    io.write(WorldFields.PLATES, moved.plates());
    io.write(WorldFields.PLATE_REGISTRY, after);
    io.write(WorldFields.PLATE_VELOCITY, moved.velocities());
    io.write(WorldFields.OCCUPANCY, moved.occupancy());
  }

  /** Full geometry pass (also used by tests). Area-only collide loser. */
  public static Result apply(
      Grid plates,
      Boundaries boundaries,
      AreaFlux flux,
      PlateRegistry registry,
      PlateVelocities velocities) {
    return apply(plates, boundaries, flux, registry, velocities, null, null, null);
  }

  /** Geometry pass with crust-buoyancy sink and SEPARATE/collide skip-occupancy marks. */
  public static Result apply(
      Grid plates,
      Boundaries boundaries,
      AreaFlux flux,
      PlateRegistry registry,
      PlateVelocities velocities,
      Grid occupancy,
      Lockers lockers,
      boolean[][] skipOccupancyExport) {
    Objects.requireNonNull(plates, "plates");
    Objects.requireNonNull(boundaries, "boundaries");
    Objects.requireNonNull(flux, "flux");
    Objects.requireNonNull(registry, "registry");
    Objects.requireNonNull(velocities, "velocities");
    int width = plates.width();
    int height = plates.height();
    int[][] cells = copyCells(plates);
    long seed = velocities.seed();
    applyCollide(
        cells, width, height, boundaries, registry, seed, occupancy, lockers, skipOccupancyExport);
    applySeparate(cells, width, height, boundaries, seed, skipOccupancyExport);
    floodSink(cells, width, height);
    Lifecycle life = fissionAndCrumbs(cells, width, height, velocities);
    return remapDense(cells, width, height, life.vx(), life.vy(), velocities.seed());
  }

  public record Result(Grid plates, PlateRegistry registry, PlateVelocities velocities) {}

  private record Lifecycle(int[] vx, int[] vy) {}

  private static void applyCollide(
      int[][] cells,
      int width,
      int height,
      Boundaries boundaries,
      PlateRegistry registry,
      long seed,
      Grid occupancy,
      Lockers lockers,
      boolean[][] skipOccupancyExport) {
    for (BoundaryContact c : boundaries.contacts()) {
      if (c.kind() != BoundaryKind.COLLIDE) {
        continue;
      }
      if (isRaggedSkip(seed, c.x(), c.y())) {
        continue;
      }
      int lose;
      if (occupancy != null && lockers != null) {
        lose = CrustPrecedence.collideLoser(c, occupancy, lockers, registry, width, height);
        if (lose == CrustPrecedence.NONE) {
          continue;
        }
      } else {
        lose = AreaFlux.loser(c.plateA(), c.plateB(), registry);
      }
      int x = c.x();
      int y = c.y();
      if (lose == c.plateB()) {
        int[] b = SphereTopology.neighbor(c.x(), c.y(), c.nx(), c.ny(), width, height);
        x = b[0];
        y = b[1];
      }
      if (cells[y][x] == lose) {
        cells[y][x] = SINK;
        markSkip(skipOccupancyExport, x, y);
        if (isRaggedExtra(seed, x, y)) {
          nibbleSink(cells, width, height, x, y, lose, skipOccupancyExport);
        }
      }
    }
  }

  private static void applySeparate(
      int[][] cells,
      int width,
      int height,
      Boundaries boundaries,
      long seed,
      boolean[][] skipOccupancyExport) {
    for (BoundaryContact c : boundaries.contacts()) {
      if (c.kind() != BoundaryKind.SEPARATE) {
        continue;
      }
      if (isRaggedSkip(seed, c.x(), c.y())) {
        continue;
      }
      claimSinkNear(cells, width, height, c.x(), c.y(), c.plateA(), skipOccupancyExport);
      int[] b = SphereTopology.neighbor(c.x(), c.y(), c.nx(), c.ny(), width, height);
      claimSinkNear(cells, width, height, b[0], b[1], c.plateB(), skipOccupancyExport);
      if (isRaggedExtra(seed, c.x(), c.y())) {
        nibbleClaim(cells, width, height, c.x(), c.y(), c.plateA(), skipOccupancyExport);
        nibbleClaim(cells, width, height, b[0], b[1], c.plateB(), skipOccupancyExport);
      }
    }
  }

  /** Extra orthogonal claim for ragged SEPARATE fronts. */
  private static void nibbleClaim(
      int[][] cells,
      int width,
      int height,
      int ox,
      int oy,
      int plate,
      boolean[][] skipOccupancyExport) {
    for (int[] d : DIRS) {
      int[] n = SphereTopology.neighbor(ox, oy, d[0], d[1], width, height);
      int id = cells[n[1]][n[0]];
      if (id >= 0 && id != plate) {
        cells[n[1]][n[0]] = plate;
        markSkip(skipOccupancyExport, n[0], n[1]);
        return;
      }
    }
  }

  /** Extra orthogonal sink for ragged COLLIDE fronts. */
  private static void nibbleSink(
      int[][] cells,
      int width,
      int height,
      int ox,
      int oy,
      int lose,
      boolean[][] skipOccupancyExport) {
    for (int[] d : DIRS) {
      int[] n = SphereTopology.neighbor(ox, oy, d[0], d[1], width, height);
      if (cells[n[1]][n[0]] == lose) {
        cells[n[1]][n[0]] = SINK;
        markSkip(skipOccupancyExport, n[0], n[1]);
        return;
      }
    }
  }

  private static void claimSinkNear(
      int[][] cells,
      int width,
      int height,
      int ox,
      int oy,
      int plate,
      boolean[][] skipOccupancyExport) {
    if (cells[oy][ox] == SINK) {
      cells[oy][ox] = plate;
      markSkip(skipOccupancyExport, ox, oy);
      return;
    }
    for (int[] d : DIRS) {
      int[] n = SphereTopology.neighbor(ox, oy, d[0], d[1], width, height);
      if (cells[n[1]][n[0]] == SINK) {
        cells[n[1]][n[0]] = plate;
        markSkip(skipOccupancyExport, n[0], n[1]);
        return;
      }
    }
  }

  private static void markSkip(boolean[][] skipOccupancyExport, int x, int y) {
    if (skipOccupancyExport != null) {
      skipOccupancyExport[y][x] = true;
    }
  }

  private static void floodSink(int[][] cells, int width, int height) {
    boolean progress = true;
    while (progress) {
      progress = false;
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (cells[y][x] != SINK) {
            continue;
          }
          int best = pickFloodOwner(cells, width, height, x, y);
          if (best >= 0) {
            cells[y][x] = best;
            progress = true;
          }
        }
      }
    }
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (cells[y][x] == SINK) {
          throw new IllegalStateException("sink remained after neighbor flood at (" + x + "," + y + ")");
        }
      }
    }
  }

  private static int pickFloodOwner(int[][] cells, int width, int height, int x, int y) {
    int bestId = -1;
    int bestContact = -1;
    for (int[] d : DIRS) {
      int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
      int id = cells[n[1]][n[0]];
      if (id < 0) {
        continue;
      }
      int contact = countContact(cells, width, height, x, y, id);
      if (contact > bestContact || (contact == bestContact && (bestId < 0 || id < bestId))) {
        bestContact = contact;
        bestId = id;
      }
    }
    return bestId;
  }

  private static int countContact(int[][] cells, int width, int height, int x, int y, int id) {
    int n = 0;
    for (int[] d : DIRS) {
      int[] p = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
      if (cells[p[1]][p[0]] == id) {
        n++;
      }
    }
    return n;
  }

  private static Lifecycle fissionAndCrumbs(
      int[][] cells, int width, int height, PlateVelocities velocities) {
    int maxId = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        maxId = Math.max(maxId, cells[y][x]);
      }
    }
    List<Integer> vx = new ArrayList<>();
    List<Integer> vy = new ArrayList<>();
    for (int i = 0; i <= maxId; i++) {
      if (i < velocities.count()) {
        vx.add(velocities.vx(i));
        vy.add(velocities.vy(i));
      } else {
        vx.add(0);
        vy.add(0);
      }
    }
    boolean[][] seen = new boolean[height][width];
    int nextId = maxId + 1;
    int plateCount = vx.size();
    for (int plate = 0; plate < plateCount; plate++) {
      List<List<int[]>> components = new ArrayList<>();
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          if (seen[y][x] || cells[y][x] != plate) {
            continue;
          }
          List<int[]> component = new ArrayList<>();
          floodComponent(cells, seen, width, height, x, y, plate, component);
          components.add(component);
        }
      }
      if (components.size() <= 1) {
        continue;
      }
      for (int c = 1; c < components.size(); c++) {
        int newId = nextId++;
        vx.add(vx.get(plate));
        vy.add(vy.get(plate));
        for (int[] cell : components.get(c)) {
          cells[cell[1]][cell[0]] = newId;
        }
      }
    }
    absorbCrumbs(cells, width, height, vx.size());
    return new Lifecycle(toArray(vx), toArray(vy));
  }

  private static void absorbCrumbs(int[][] cells, int width, int height, int plateCount) {
    long world = (long) width * height;
    boolean[][] seen = new boolean[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (seen[y][x]) {
          continue;
        }
        int id = cells[y][x];
        if (id < 0 || id >= plateCount) {
          seen[y][x] = true;
          continue;
        }
        List<int[]> component = new ArrayList<>();
        floodComponent(cells, seen, width, height, x, y, id, component);
        // Crumb bar 0.01% of W×H.
        if (component.size() * (long) CRUMB_DENOMINATOR >= world) {
          continue;
        }
        int neighbor = longestNeighbor(cells, width, height, component, id);
        if (neighbor < 0) {
          continue;
        }
        for (int[] cell : component) {
          cells[cell[1]][cell[0]] = neighbor;
        }
      }
    }
  }

  private static int longestNeighbor(
      int[][] cells, int width, int height, List<int[]> component, int self) {
    int[] contact = new int[256];
    int maxId = self;
    for (int[] cell : component) {
      int x = cell[0];
      int y = cell[1];
      for (int[] d : DIRS) {
        int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
        int id = cells[n[1]][n[0]];
        if (id < 0 || id == self) {
          continue;
        }
        if (id >= contact.length) {
          contact = Arrays.copyOf(contact, id + 1);
        }
        contact[id]++;
        maxId = Math.max(maxId, id);
      }
    }
    int best = -1;
    int bestN = -1;
    for (int id = 0; id <= maxId; id++) {
      int n = id < contact.length ? contact[id] : 0;
      if (n > bestN || (n == bestN && n > 0 && (best < 0 || id < best))) {
        bestN = n;
        best = id;
      }
    }
    return bestN > 0 ? best : -1;
  }

  private static void floodComponent(
      int[][] cells,
      boolean[][] seen,
      int width,
      int height,
      int sx,
      int sy,
      int id,
      List<int[]> out) {
    ArrayList<int[]> stack = new ArrayList<>();
    stack.add(new int[] {sx, sy});
    seen[sy][sx] = true;
    while (!stack.isEmpty()) {
      int[] cur = stack.remove(stack.size() - 1);
      out.add(cur);
      int x = cur[0];
      int y = cur[1];
      for (int[] d : DIRS) {
        int[] n = SphereTopology.neighbor(x, y, d[0], d[1], width, height);
        int nx = n[0];
        int ny = n[1];
        if (seen[ny][nx] || cells[ny][nx] != id) {
          continue;
        }
        seen[ny][nx] = true;
        stack.add(new int[] {nx, ny});
      }
    }
  }

  private static Result remapDense(
      int[][] cells, int width, int height, int[] vxIn, int[] vyIn, long seed) {
    int maxId = -1;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        maxId = Math.max(maxId, cells[y][x]);
      }
    }
    int[] area = new int[maxId + 1];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int id = cells[y][x];
        if (id < 0) {
          throw new IllegalStateException("sink remained at (" + x + "," + y + ")");
        }
        area[id]++;
      }
    }
    int[] oldToNew = new int[maxId + 1];
    Arrays.fill(oldToNew, -1);
    List<Integer> vx = new ArrayList<>();
    List<Integer> vy = new ArrayList<>();
    List<Integer> areas = new ArrayList<>();
    for (int old = 0; old <= maxId; old++) {
      if (area[old] <= 0) {
        continue;
      }
      oldToNew[old] = vx.size();
      areas.add(area[old]);
      if (old < vxIn.length) {
        vx.add(vxIn[old]);
        vy.add(vyIn[old]);
      } else {
        vx.add(0);
        vy.add(0);
      }
    }
    if (vx.isEmpty()) {
      throw new IllegalStateException("no living plates after geometry");
    }
    int[][] next = new int[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        next[y][x] = oldToNew[cells[y][x]];
      }
    }
    Grid grid = new Grid(next);
    PlateVelocities velocities = new PlateVelocities(seed, toArray(vx), toArray(vy));
    PlateRegistry registry = new PlateRegistry(seed, toArray(areas), toArray(vx), toArray(vy));
    return new Result(grid, registry, velocities);
  }

  private static int[][] copyCells(Grid plates) {
    int[][] cells = new int[plates.height()][plates.width()];
    for (int y = 0; y < plates.height(); y++) {
      for (int x = 0; x < plates.width(); x++) {
        cells[y][x] = plates.get(x, y);
      }
    }
    return cells;
  }

  private static int[] toArray(List<Integer> list) {
    int[] out = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      out[i] = list.get(i);
    }
    return out;
  }

  private static Object readField(SubSystemIo io, String field) {
    Object staged = io.readStaging(field);
    return staged != null ? staged : io.readPool(field);
  }

  private static Grid requireGrid(Object value, String field) {
    if (value instanceof Grid grid) {
      return grid;
    }
    throw new IllegalStateException(
        "field '" + field + "' must be Grid, was " + value.getClass().getName());
  }

  private static PlateRegistry requireRegistry(Object value) {
    if (value instanceof PlateRegistry registry) {
      return registry;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_REGISTRY
            + "' must be PlateRegistry, was "
            + value.getClass().getName());
  }

  private static PlateVelocities requireVelocities(Object value) {
    if (value instanceof PlateVelocities velocities) {
      return velocities;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.PLATE_VELOCITY
            + "' must be PlateVelocities, was "
            + value.getClass().getName());
  }

  private static Boundaries requireBoundaries(Object value) {
    if (value instanceof Boundaries boundaries) {
      return boundaries;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.BOUNDARIES
            + "' must be Boundaries, was "
            + (value == null ? "null" : value.getClass().getName()));
  }

  private static AreaFlux requireFlux(Object value) {
    if (value instanceof AreaFlux flux) {
      return flux;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.AREA_FLUX
            + "' must be AreaFlux, was "
            + (value == null ? "null" : value.getClass().getName()));
  }

  private static Lockers requireLockers(Object value) {
    if (value instanceof Lockers lockers) {
      return lockers;
    }
    throw new IllegalStateException(
        "field '"
            + WorldFields.LOCKERS
            + "' must be Lockers, was "
            + (value == null ? "null" : value.getClass().getName()));
  }
}
