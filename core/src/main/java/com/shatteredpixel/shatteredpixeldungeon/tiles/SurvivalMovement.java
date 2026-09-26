/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHouseCollisionTest;
import com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHousePropDebug;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.PointF;

/**
 * Continuous world-space position and movement state for the
 * Survival prototype.
 *
 * The normal SPD hero position remains cell-based for now.
 * This class lets the Survival renderer experiment with
 * sub-cell movement without replacing the original system.
 */
public final class SurvivalMovement {

    private static final float MOVE_SPEED = 96f;
    private static final float COLLISION_HALF_SIZE = 6f;
    private static float worldX;
    private static float worldY;
    private static boolean initialized;
    private static boolean moving;

    private SurvivalMovement() {
    }

    public static void reset() {
        initialized = false;
    }

    public static void ensureInitialized() {

        if (initialized || Dungeon.hero == null) {
            return;
        }

        PointF start =
                SurvivalVisuals.cellCenterToWorld(Dungeon.hero.pos);

        worldX = start.x;
        worldY = start.y;

        initialized = true;
    }

    public static void move(float directionX, float directionY, float elapsed) {
        moving = false;

        ensureInitialized();

        if (!initialized) {
            return;
        }

        float length = (float) Math.sqrt(
                directionX * directionX
                        + directionY * directionY
        );

        if (length <= 0f) {
            return;
        }

        moving = true;
        directionX /= length;
        directionY /= length;

                float moveX = directionX * MOVE_SPEED * elapsed;
        float moveY = directionY * MOVE_SPEED * elapsed;

        PointF resolved = resolveMovement(
                worldX,
                worldY,
                moveX,
                moveY
        );

        worldX += resolved.x;
        worldY += resolved.y;

        syncHeroCell();
    }


private static PointF resolveMovement(
        float startX,
        float startY,
        float moveX,
        float moveY) {

    // Nothing blocks the requested movement.
    if (canOccupy(startX + moveX, startY + moveY)) {
        return new PointF(moveX, moveY);
    }

    // Try the polygon collision surface.
    PointF slide = slideAlongPolygon(
            startX,
            startY,
            moveX,
            moveY,
            SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE,
            SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE,
            SurvivalHouseCollisionTest.activePolygon()
    );

    // Preview only: compare each prop's best valid slide with the house candidate.
    // Strict improvement preserves the house/definition order on ties.
    if (SurvivalHouseCollisionTest.isPreview()) {
        float bestProgress = slide == null ? -1f : moveX * slide.x + moveY * slide.y;
        for (SurvivalHousePropDebug.Footprint prop : SurvivalHousePropDebug.FOOTPRINTS) {
            PointF candidate = slideAlongPolygon(startX, startY, moveX, moveY,
                    SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE,
                    SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE, prop.vertices);
            if (candidate != null) {
                float progress = moveX * candidate.x + moveY * candidate.y;
                if (progress > bestProgress) {
                    slide = candidate;
                    bestProgress = progress;
                }
            }
        }
    }

    if (slide != null) {
        return slide;
    }

    // Fallback for straight walls and normal tile collision.
    float resolvedX = 0f;
    float resolvedY = 0f;

    if (canOccupy(startX + moveX, startY)) {
        resolvedX = moveX;
    }

    if (canOccupy(
            startX + resolvedX,
            startY + moveY
    )) {
        resolvedY = moveY;
    }

    return new PointF(resolvedX, resolvedY);
}

        private static PointF slideAlongPolygon(
        float startX,
        float startY,
        float moveX,
        float moveY,
        float originX,
        float originY,
        float[][] polygon) {

    float moveLength =
            (float) Math.sqrt(
                    moveX * moveX + moveY * moveY
            );

    if (moveLength <= 0f) {
        return null;
    }

    PointF bestSlide = null;
    float bestProgress = -1f;

    for (int i = 0; i < polygon.length; i++) {

        int next = (i + 1) % polygon.length;

        float edgeX =
                polygon[next][0] - polygon[i][0];

        float edgeY =
                polygon[next][1] - polygon[i][1];

        float edgeLength =
                (float) Math.sqrt(
                        edgeX * edgeX + edgeY * edgeY
                );

        if (edgeLength <= 0f) {
            continue;
        }

        float tangentX = edgeX / edgeLength;
        float tangentY = edgeY / edgeLength;

        float dot =
                moveX * tangentX
                        + moveY * tangentY;

        if (Math.abs(dot) < 0.0001f) {
            continue;
        }

        // Point the tangent in the same general direction
        // as the player's requested movement.
        if (dot < 0f) {
            tangentX = -tangentX;
            tangentY = -tangentY;
        }

        // Keep normal movement speed while following the surface.
        float slideX = tangentX * moveLength;
        float slideY = tangentY * moveLength;

        float targetX = startX + slideX;
        float targetY = startY + slideY;

        // This edge is useless if sliding along it still collides.
        if (!canOccupy(targetX, targetY)) {
            continue;
        }

        // Find the closest point on this edge to the Survivor.
        float ax = originX + polygon[i][0];
        float ay = originY + polygon[i][1];

        float bx = originX + polygon[next][0];
        float by = originY + polygon[next][1];

        float segmentX = bx - ax;
        float segmentY = by - ay;

        float segmentLengthSquared =
                segmentX * segmentX
                        + segmentY * segmentY;

        if (segmentLengthSquared <= 0f) {
            continue;
        }

        float t =
                ((startX - ax) * segmentX
                        + (startY - ay) * segmentY)
                        / segmentLengthSquared;

        t = Math.max(0f, Math.min(1f, t));

        float closestX = ax + segmentX * t;
        float closestY = ay + segmentY * t;

        float distanceX = startX - closestX;
        float distanceY = startY - closestY;

        float distanceSquared =
                distanceX * distanceX
                        + distanceY * distanceY;

        // Only consider surfaces close enough to actually
        // be responsible for the collision.
        float contactDistance =
                COLLISION_HALF_SIZE + moveLength + 2f;

        if (distanceSquared
                > contactDistance * contactDistance) {
            continue;
        }

        // Prefer the valid edge which follows the player's
        // requested direction most closely.
        float progress =
                moveX * slideX
                        + moveY * slideY;

        if (progress > bestProgress) {
            bestProgress = progress;
            bestSlide = new PointF(slideX, slideY);
        }
    }

    return bestSlide;
}
        private static boolean canOccupy(float x, float y) {
if (hitsDetailedHouse(x, y)) {
    return false;
}
    if (SurvivalHouseCollisionTest.isPreview()) {
        float localX = x - SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;
        float localY = y - SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;
        // Preserve CURRENT's original corner-only house test. In PREVIEW also
        // catch edge crossings/containment, including the concave porch outline.
        if (SurvivalPolygonOverlap.boundary(localX, localY, COLLISION_HALF_SIZE,
                SurvivalHouseCollisionTest.activePolygon())) return false;
        for (SurvivalHousePropDebug.Footprint prop : SurvivalHousePropDebug.FOOTPRINTS) {
            if (SurvivalPolygonOverlap.convex(localX, localY, COLLISION_HALF_SIZE,
                    prop.vertices)) return false;
        }
    }
    return canOccupyPoint(
            x - COLLISION_HALF_SIZE,
            y - COLLISION_HALF_SIZE
    ) && canOccupyPoint(
            x + COLLISION_HALF_SIZE,
            y - COLLISION_HALF_SIZE
    ) && canOccupyPoint(
            x - COLLISION_HALF_SIZE,
            y + COLLISION_HALF_SIZE
    ) && canOccupyPoint(
            x + COLLISION_HALF_SIZE,
            y + COLLISION_HALF_SIZE
    );
}
private static boolean hitsDetailedHouse(float x, float y) {

    float houseOriginX =
            SurvivalHouse.HOUSE_X * SurvivalVisuals.CELL_SIZE;

    float houseOriginY =
            SurvivalHouse.HOUSE_Y * SurvivalVisuals.CELL_SIZE;

    float[][] polygon = SurvivalHouseCollisionTest.activePolygon();

    // Test the four corners of the Survivor's 12x12 footprint.
    return pointInsideHouse(
            x - COLLISION_HALF_SIZE,
            y - COLLISION_HALF_SIZE,
            houseOriginX,
            houseOriginY,
            polygon
    ) || pointInsideHouse(
            x + COLLISION_HALF_SIZE,
            y - COLLISION_HALF_SIZE,
            houseOriginX,
            houseOriginY,
            polygon
    ) || pointInsideHouse(
            x - COLLISION_HALF_SIZE,
            y + COLLISION_HALF_SIZE,
            houseOriginX,
            houseOriginY,
            polygon
    ) || pointInsideHouse(
            x + COLLISION_HALF_SIZE,
            y + COLLISION_HALF_SIZE,
            houseOriginX,
            houseOriginY,
            polygon
    );
}

private static boolean pointInsideHouse(
        float worldX,
        float worldY,
        float originX,
        float originY,
        float[][] polygon) {

    float localX = worldX - originX;
    float localY = worldY - originY;

    boolean inside = false;

    for (int i = 0, j = polygon.length - 1;
            i < polygon.length;
            j = i++) {

        float xi = polygon[i][0];
        float yi = polygon[i][1];

        float xj = polygon[j][0];
        float yj = polygon[j][1];

        boolean crosses =
                (yi > localY) != (yj > localY);

        if (crosses) {

            float edgeX =
                    (xj - xi)
                            * (localY - yi)
                            / (yj - yi)
                            + xi;

            if (localX < edgeX) {
                inside = !inside;
            }
        }
    }

    return inside;
}
private static boolean canOccupyPoint(float x, float y) {

    if (Dungeon.level == null) {
        return false;
    }

    float worldWidth =
            Dungeon.level.width() * SurvivalVisuals.CELL_SIZE;

    float worldHeight =
            (Dungeon.level.length() / Dungeon.level.width())
                    * SurvivalVisuals.CELL_SIZE;

    if (x < 0f || y < 0f
            || x >= worldWidth
            || y >= worldHeight) {
        return false;
    }

    int cell = SurvivalVisuals.worldToCell(x, y);

    if (cell < 0 || cell >= Dungeon.level.length()) {
        return false;
    }

    if (Dungeon.level.map[cell] == Terrain.DOOR) {
        Door.enter(cell);
    }

    return !Dungeon.level.solid[cell];
}

        private static void syncHeroCell() {

        if (Dungeon.hero == null || Dungeon.level == null) {
            return;
        }

        int cell = SurvivalVisuals.worldToCell(worldX, worldY);

        if (cell != Dungeon.hero.pos) {
            Dungeon.hero.move(cell, false);
        }
    }

    public static PointF position() {
        ensureInitialized();
        return new PointF(worldX, worldY);
    }

    public static float x() {
        ensureInitialized();
        return worldX;
    }

    public static float y() {
        ensureInitialized();
        return worldY;
    }
    public static boolean isMoving() {
    return moving;
}
}
