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

public class SurvivalTilemap extends Tilemap {

    public static final int ASPHALT_TILE = 0;
    public static final int CONCRETE_TILE = 1;
    public static final int GRASS_TILE = 2;
    public static final int DIRT_TILE = 3;

    public SurvivalTilemap() {
        super(
                Assets.Environment.TILES_SURVIVAL_TEST,
                new TextureFilm(
                        Assets.Environment.TILES_SURVIVAL_TEST,
                        SurvivalVisuals.CELL_SIZE,
                        SurvivalVisuals.CELL_SIZE
                )
        );

        buildMap();
    }

    private void buildMap() {

        if (Dungeon.level == null) {
            return;
        }

        int[] visualMap = new int[Dungeon.level.map.length];

        if (Dungeon.level instanceof SurvivalTestLevel) {

            SurvivalTestLevel survivalLevel =
                    (SurvivalTestLevel) Dungeon.level;

            int[] survivalTerrain = survivalLevel.survivalTerrain();

            for (int i = 0; i < visualMap.length; i++) {

                switch (survivalTerrain[i]) {

                    case SurvivalTerrain.ASPHALT:
                        visualMap[i] = ASPHALT_TILE;
                        break;

                    case SurvivalTerrain.CONCRETE:
                        visualMap[i] = CONCRETE_TILE;
                        break;

                    case SurvivalTerrain.GRASS:
                        visualMap[i] = GRASS_TILE;
                        break;

                    case SurvivalTerrain.DIRT:
                        visualMap[i] = DIRT_TILE;
                        break;

                    default:
                        visualMap[i] = GRASS_TILE;
                        break;
                }

                // Keep logically solid cells invisible for now.
                // Proper survival walls/buildings come later.
                if (!Dungeon.level.passable[i]) {
                    visualMap[i] = -1;
                }
            }

        } else {

            // Safe fallback if this renderer is ever created on another level.
            for (int i = 0; i < visualMap.length; i++) {
                visualMap[i] = Dungeon.level.passable[i]
                        ? ASPHALT_TILE
                        : -1;
            }
        }

        map(visualMap, Dungeon.level.width());
    }
}