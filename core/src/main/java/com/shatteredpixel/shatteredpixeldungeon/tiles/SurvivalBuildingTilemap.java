/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurvivalTestLevel;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;

public class SurvivalBuildingTilemap extends Tilemap {

    public static final int INTERIOR_FLOOR_TILE = 0;
    public static final int EXTERIOR_WALL_TILE = 1;
    public static final int DOOR_TILE = 2;
    public static final int WINDOW_TILE = 3;

    public SurvivalBuildingTilemap() {
        super(
                Assets.Environment.TILES_SURVIVAL_BUILDING,
                new TextureFilm(
                        Assets.Environment.TILES_SURVIVAL_BUILDING,
                        SurvivalVisuals.CELL_SIZE,
                        SurvivalVisuals.CELL_SIZE
                )
        );

        buildMap();
    }

    private void buildMap() {

        if (!(Dungeon.level instanceof SurvivalTestLevel)) {
            return;
        }

        SurvivalTestLevel survivalLevel =
                (SurvivalTestLevel) Dungeon.level;

        int[] buildingMap = survivalLevel.survivalBuilding();
        int[] visualMap = new int[buildingMap.length];

        for (int i = 0; i < visualMap.length; i++) {

            switch (buildingMap[i]) {

                case SurvivalBuilding.INTERIOR_FLOOR:
                    visualMap[i] = INTERIOR_FLOOR_TILE;
                    break;

                case SurvivalBuilding.EXTERIOR_WALL:
                    visualMap[i] = EXTERIOR_WALL_TILE;
                    break;

                case SurvivalBuilding.DOOR:
                    visualMap[i] = DOOR_TILE;
                    break;

                case SurvivalBuilding.WINDOW:
                    visualMap[i] = WINDOW_TILE;
                    break;

                default:
                    visualMap[i] = -1;
                    break;
            }
        }

        map(visualMap, Dungeon.level.width());
    }
}