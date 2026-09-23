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

    private static final int VARIANTS_PER_TERRAIN = 4;

    private static final int ASPHALT_START = 0;
    private static final int CONCRETE_START = 4;
    private static final int GRASS_START = 8;
    private static final int DIRT_START = 12;

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

                int variant = terrainVariant(i);

                switch (survivalTerrain[i]) {

                    case SurvivalTerrain.ASPHALT:
                        visualMap[i] = ASPHALT_START + variant;
                        break;

                    case SurvivalTerrain.CONCRETE:
                        visualMap[i] = CONCRETE_START + variant;
                        break;

                    case SurvivalTerrain.GRASS:
                        visualMap[i] = GRASS_START + variant;
                        break;

                    case SurvivalTerrain.DIRT:
                        visualMap[i] = DIRT_START + variant;
                        break;

                    default:
                        visualMap[i] = GRASS_START + variant;
                        break;
                }

                // Solid logical cells remain visually empty until the
                // survival wall/building renderer is implemented.
                if (!Dungeon.level.passable[i]) {
                    visualMap[i] = -1;
                }
            }

        } else {

            for (int i = 0; i < visualMap.length; i++) {
                visualMap[i] = Dungeon.level.passable[i]
                        ? ASPHALT_START + terrainVariant(i)
                        : -1;
            }
        }

        map(visualMap, Dungeon.level.width());
    }

    /*
     * Produces a stable visual variant from the cell coordinates.
     *
     * This is deterministic:
     * the same cell always receives the same variant instead of
     * changing appearance whenever the renderer rebuilds.
     */
    private int terrainVariant(int cell) {

        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();

        int hash = x * 73428767
                ^ y * 912931
                ^ x * y * 19349663;

        return Math.floorMod(hash, VARIANTS_PER_TERRAIN);
    }
}