/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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

float proposedX = worldX + moveX;

if (canOccupy(proposedX, worldY)) {
    worldX = proposedX;
}

float proposedY = worldY + moveY;

if (canOccupy(worldX, proposedY)) {
    worldY = proposedY;
}

        syncHeroCell();
    }

        private static boolean canOccupy(float x, float y) {

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
