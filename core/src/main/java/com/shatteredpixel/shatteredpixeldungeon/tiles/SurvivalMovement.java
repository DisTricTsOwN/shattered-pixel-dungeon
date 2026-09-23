/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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

        directionX /= length;
        directionY /= length;

        worldX += directionX * MOVE_SPEED * elapsed;
        worldY += directionY * MOVE_SPEED * elapsed;
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
}