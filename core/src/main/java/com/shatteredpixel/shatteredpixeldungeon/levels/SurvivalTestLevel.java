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
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalTerrain;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalBuilding;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalHouseCollision;

public class SurvivalTestLevel extends Level {

    private static final int MAP_WIDTH = 96;
    private static final int MAP_HEIGHT = 64;
    private int[] survivalTerrain;

public int[] survivalTerrain() {
    return survivalTerrain;
}
private int[] survivalBuilding;

public int[] survivalBuilding() {
    return survivalBuilding;
}

private void setSurvivalBuilding(int x, int y, int building) {
    survivalBuilding[cell(x, y)] = building;
}
private int[] survivalHouseCollision;

public int[] survivalHouseCollision() {
    return survivalHouseCollision;
}

private void setSurvivalHouseCollision(int x, int y, int collision) {
    survivalHouseCollision[cell(x, y)] = collision;
}

private void fillSurvivalBuildingRect(int x, int y, int w, int h, int building) {
    for (int yy = y; yy < y + h; yy++) {
        for (int xx = x; xx < x + w; xx++) {
            setSurvivalBuilding(xx, yy, building);
        }
    }
}

private void setSurvivalTerrain(int x, int y, int terrain) {
    survivalTerrain[cell(x, y)] = terrain;
}

private void fillSurvivalRect(int x, int y, int w, int h, int terrain) {
    for (int yy = y; yy < y + h; yy++) {
        for (int xx = x; xx < x + w; xx++) {
            setSurvivalTerrain(xx, yy, terrain);
        }
    }
}

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
        survivalTerrain = new int[length()];
        survivalBuilding = new int[length()];
        survivalHouseCollision = new int[length()];

// Grass is the default outdoor surface.
for (int i = 0; i < survivalTerrain.length; i++) {
    survivalTerrain[i] = SurvivalTerrain.GRASS;
}

// Main east/west road.
fillSurvivalRect(
        1, 24,
        MAP_WIDTH - 2, 12,
        SurvivalTerrain.ASPHALT
);

// Main north/south road.
fillSurvivalRect(
        42, 1,
        12, MAP_HEIGHT - 2,
        SurvivalTerrain.ASPHALT
);

// Concrete sidewalks alongside the roads.
fillSurvivalRect(
        1, 22,
        MAP_WIDTH - 2, 2,
        SurvivalTerrain.CONCRETE
);

fillSurvivalRect(
        1, 36,
        MAP_WIDTH - 2, 2,
        SurvivalTerrain.CONCRETE
);

fillSurvivalRect(
        40, 1,
        2, MAP_HEIGHT - 2,
        SurvivalTerrain.CONCRETE
);

fillSurvivalRect(
        54, 1,
        2, MAP_HEIGHT - 2,
        SurvivalTerrain.CONCRETE
);

// A dirt patch for testing the fourth surface.
fillSurvivalRect(
        65, 44,
        14, 10,
        SurvivalTerrain.DIRT
);

        /*
         * OUTDOOR PLAYABLE AREA
         *
         * Leave a solid wall border around the entire test level.
         */
        fillRect(1, 1, MAP_WIDTH - 2, MAP_HEIGHT - 2, Terrain.EMPTY);

                /*
         * HOUSE
         *
         * Exterior:
         * x = 4..13
         * y = 3..8
         */
        fillRect(4, 3, 10, 6, Terrain.WALL);
        fillRect(5, 4, 8, 4, Terrain.EMPTY);

        // Survival visual structure.
        fillSurvivalBuildingRect(
                4, 3,
                10, 6,
                SurvivalBuilding.EXTERIOR_WALL
        );

        // Interior floor replaces the wall visual inside the structure.
        fillSurvivalBuildingRect(
                5, 4,
                8, 4,
                SurvivalBuilding.INTERIOR_FLOOR
        );

        // House front door.
        setTerrain(9, 8, Terrain.DOOR);
        setSurvivalBuilding(9, 8, SurvivalBuilding.DOOR);

        // Front-facing windows.
        setSurvivalBuilding(6, 8, SurvivalBuilding.WINDOW);
        setSurvivalBuilding(12, 8, SurvivalBuilding.WINDOW);

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
public void buildFlagMaps() {

    super.buildFlagMaps();

    if (survivalHouseCollision == null) {
        return;
    }

    for (int i = 0; i < survivalHouseCollision.length; i++) {

        switch (survivalHouseCollision[i]) {

            case SurvivalHouseCollision.SOLID:
            case SurvivalHouseCollision.WINDOW:
            case SurvivalHouseCollision.PROP:
                passable[i] = false;
                solid[i] = true;
                break;

            case SurvivalHouseCollision.WALKABLE:
            case SurvivalHouseCollision.DOOR:
                passable[i] = true;
                solid[i] = false;
                break;
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