/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;

public class SurvivalTestLevel extends Level {

    private static final int MAP_WIDTH = 32;
    private static final int MAP_HEIGHT = 22;

    {
        color1 = 0x59635A;
        color2 = 0x303633;
    }

    @Override
    public String tilesTex() {
        // Temporary. The survival renderer draws our real prototype terrain.
        return Assets.Environment.TILES_SEWERS;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_SEWERS;
    }

    @Override
    protected boolean build() {

        setSize(MAP_WIDTH, MAP_HEIGHT);

        /*
         * OUTDOOR PLAYABLE AREA
         *
         * Leave a solid wall border around the entire test level.
         */
        fillRect(1, 1, 30, 20, Terrain.EMPTY);

        /*
         * HOUSE
         *
         * Exterior:
         * x = 4..13
         * y = 3..8
         */
        fillRect(4, 3, 10, 6, Terrain.WALL);
        fillRect(5, 4, 8, 4, Terrain.EMPTY);

        // House front door.
        setTerrain(9, 8, Terrain.DOOR);

        /*
         * CONVENIENCE STORE
         *
         * Exterior:
         * x = 20..29
         * y = 13..19
         */
        fillRect(20, 13, 10, 7, Terrain.WALL);
        fillRect(21, 14, 8, 5, Terrain.EMPTY);

        // Store entrance.
        setTerrain(24, 13, Terrain.DOOR);

        /*
         * ALLEY OBSTACLES
         *
         * These give us something obvious to navigate around.
         */
        setTerrain(17, 14, Terrain.WALL);
        setTerrain(17, 15, Terrain.WALL);
        setTerrain(17, 16, Terrain.WALL);

        /*
         * START POSITION
         *
         * Bottom-left portion of the neighborhood.
         */
        int start = cell(3, 18);

        transitions.add(
                new LevelTransition(
                        this,
                        start,
                        LevelTransition.Type.REGULAR_ENTRANCE
                )
        );

        map[start] = Terrain.ENTRANCE;

        return true;
    }

    private int cell(int x, int y) {
        return y * width() + x;
    }

    private void setTerrain(int x, int y, int terrain) {
        map[cell(x, y)] = terrain;
    }

    private void fillRect(int x, int y, int w, int h, int terrain) {

        for (int yy = y; yy < y + h; yy++) {
            for (int xx = x; xx < x + w; xx++) {
                setTerrain(xx, yy, terrain);
            }
        }
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        // Intentionally empty for the first geometry test.
    }

    @Override
    protected void createItems() {
        // Intentionally empty for the first geometry test.
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cell(3, 18);
    }
}