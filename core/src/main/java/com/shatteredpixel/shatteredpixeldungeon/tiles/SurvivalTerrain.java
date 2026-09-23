/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

public final class SurvivalTerrain {

    /*
     * Survival-specific terrain IDs.
     *
     * These deliberately live outside SPD's normal Terrain values.
     * Starting high prevents collisions with existing dungeon terrain IDs.
     */
    public static final int ASPHALT  = 1000;
    public static final int CONCRETE = 1001;
    public static final int GRASS    = 1002;
    public static final int DIRT     = 1003;

    private SurvivalTerrain() {
        // Utility class
    }
}