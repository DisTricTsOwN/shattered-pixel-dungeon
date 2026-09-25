/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurvivalTestLevel;
import com.watabou.noosa.Group;

/**
 * Renders the high-detail base ground for survival terrain.
 *
 * The hidden 32px terrain grid remains responsible for terrain identity,
 * collision, navigation, and procedural generation.
 *
 * This layer controls only the visual appearance of natural ground.
 */
public class SurvivalGroundBaseLayer extends Group {

    public SurvivalGroundBaseLayer() {

        super();

        if (!(Dungeon.level instanceof SurvivalTestLevel)) {
            return;
        }

        SurvivalTestLevel level =
                (SurvivalTestLevel) Dungeon.level;

        int[] surfaceMap = level.survivalTerrain();

        if (surfaceMap == null) {
            return;
        }
    }
}
