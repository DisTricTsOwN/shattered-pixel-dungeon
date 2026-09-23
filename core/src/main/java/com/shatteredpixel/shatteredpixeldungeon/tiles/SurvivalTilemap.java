/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;

public class SurvivalTilemap extends Tilemap {

    private static final int FLOOR_TILE = 0;

    public SurvivalTilemap(String texture) {
        super(
                texture,
                new TextureFilm(
                        texture,
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

        for (int i = 0; i < visualMap.length; i++) {
            visualMap[i] = FLOOR_TILE;
        }

        map(visualMap, Dungeon.level.width());
    }
}