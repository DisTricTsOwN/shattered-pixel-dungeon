package com.shatteredpixel.shatteredpixeldungeon.devtools;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurvivalTestLevel;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalHouse;

/** Temporary developer collision experiment; no state is saved to settings or games. */
public final class SurvivalHouseCollisionTest {

    // Shared P0-P8 geometry for the overlay and experimental collision. Treat as read-only.
    // Initial 3-pixel clearance, then 3 more at P1/P2/P3/P6 using parallel-edge
    // intersections. Lock step face P4-P5 and rear P7-P8-P0 exactly; adjoining
    // edges taper to those fixed endpoints without adding corners.
    public static final float[][] PREVIEW_POLYGON = {
            {326.477540f, 133.353421f}, {196.211150f, 219.068398f}, {182.652722f, 227.696488f},
            {118.431744f, 202.610168f}, {98.078604f, 210.380582f}, {39.215021f, 181.483913f},
            {40.291120f, 170.329020f}, {0.979952f, 151.005853f}, {138, 60}
    };

    private static boolean preview = false;

    private SurvivalHouseCollisionTest() {
    }

    public static void reset() {
        preview = false;
    }

    public static void toggle() {
        if (DevTools.isEnabled() && Dungeon.level instanceof SurvivalTestLevel) {
            preview = !preview;
        }
    }

    public static boolean isPreview() {
        return DevTools.isEnabled() && Dungeon.level instanceof SurvivalTestLevel && preview;
    }

    public static float[][] activePolygon() {
        return isPreview() ? PREVIEW_POLYGON : SurvivalHouse.COLLISION_POLYGON;
    }
}
