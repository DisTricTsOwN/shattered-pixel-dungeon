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

int mapWidth = Dungeon.level.width();
int mapHeight = Dungeon.level.height();

float worldWidth =
        mapWidth * SurvivalVisuals.CELL_SIZE;

float worldHeight =
        mapHeight * SurvivalVisuals.CELL_SIZE;

float panelSize = 61.44f;
int sourceSize = 1024;

int panelY = 0;

for (float y = 0; y < worldHeight; y += panelSize, panelY++) {

    int panelX = 0;

    for (float x = 0; x < worldWidth; x += panelSize, panelX++) {

        Image grass =
                new Image(Assets.Environment.SURVIVAL_GRASS_BASE);

        int hash = groundHash(panelX, panelY);

        int cropSize = 512;

        int maxOffset = sourceSize - cropSize;

        int sourceX =
                Math.floorMod(hash, maxOffset + 1);

        int sourceY =
                Math.floorMod(hash >> 12, maxOffset + 1);

        grass.frame(
                grass.texture.uvRect(
                        sourceX,
                        sourceY,
                        sourceX + cropSize,
                        sourceY + cropSize
                )
        );

        float cropScale =
                panelSize / cropSize;

        grass.scale.set(cropScale);

        grass.x = x;
        grass.y = y;

        add(grass);
    }
}
    }
    private int groundHash(int x, int y) {

    int hash = x * 73428767;
    hash ^= y * 912931;
    hash ^= 0x41C64E6D;

    hash ^= hash >>> 13;
    hash *= 1274126177;
    hash ^= hash >>> 16;

    return hash;
}
}
