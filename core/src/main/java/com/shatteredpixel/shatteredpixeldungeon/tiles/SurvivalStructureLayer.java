/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurvivalTestLevel;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;

public class SurvivalStructureLayer extends Group {

    private static final int WALL_HEIGHT = 64;

    public SurvivalStructureLayer() {

        super();

        if (!(Dungeon.level instanceof SurvivalTestLevel)) {
            return;
        }

        SurvivalTestLevel survivalLevel =
                (SurvivalTestLevel) Dungeon.level;

        int[] buildingMap = survivalLevel.survivalBuilding();

        for (int cell = 0; cell < buildingMap.length; cell++) {

            if (buildingMap[cell] != SurvivalBuilding.EXTERIOR_WALL) {
                continue;
            }

            addWall(cell);
        }
    }

    private void addWall(int cell) {

        int mapWidth = Dungeon.level.width();

        int cellX = cell % mapWidth;
        int cellY = cell / mapWidth;

        /*
         * Temporary diagnostic wall.
         *
         * This Image has no texture yet. The important part of this
         * class is establishing world placement independently from
         * the 32x32 tilemap.
         */
        Image wall = new Image(Assets.Environment.SURVIVAL_WALL_3Q);

        wall.x = cellX * SurvivalVisuals.CELL_SIZE;

        /*
         * Anchor the bottom of the wall to its logical cell.
         *
         * A 64px wall therefore rises 32px above the cell while
         * remaining attached to the same gameplay position.
         */
        wall.y =
                cellY * SurvivalVisuals.CELL_SIZE
                        - (WALL_HEIGHT - SurvivalVisuals.CELL_SIZE);

        add(wall);
    }
}