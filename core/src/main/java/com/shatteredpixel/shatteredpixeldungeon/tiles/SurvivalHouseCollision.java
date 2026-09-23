/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

/**
 * Defines the physical parts of a survival house independently
 * from its rendered artwork.
 */
public final class SurvivalHouseCollision {

    public static final int NONE = 0;
    public static final int SOLID = 1;
    public static final int WALKABLE = 2;
    public static final int DOOR = 3;
    public static final int WINDOW = 4;
    public static final int PROP = 5;

    private SurvivalHouseCollision() {
    }
}