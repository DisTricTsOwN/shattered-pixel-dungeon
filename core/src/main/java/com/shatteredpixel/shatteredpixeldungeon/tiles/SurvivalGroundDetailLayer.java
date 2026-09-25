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
 * Renders large-scale ground details above the base survival terrain.
 *
 * These details are visual only. They do not affect collision,
 * navigation, or the hidden logical grid.
 */
public class SurvivalGroundDetailLayer extends Group {

    public SurvivalGroundDetailLayer() {

        super();

        if (!(Dungeon.level instanceof SurvivalTestLevel)) {
            return;
        }

                int mapWidth = Dungeon.level.width();
        int mapHeight = Dungeon.level.height();

        SurvivalTestLevel level = (SurvivalTestLevel) Dungeon.level;
        int[] surfaceMap = level.survivalTerrain();

        for (int y = 2; y < mapHeight - 2; y += 4) {
            for (int x = 2; x < mapWidth - 2; x += 4) {

                int hash = groundHash(x, y);

                // Leave many areas untouched so the terrain stays natural.
                if ((hash & 3) != 0) {
                    continue;
                }

                int variant = Math.floorMod(hash, 3);

                float worldX =
                        x * SurvivalVisuals.CELL_SIZE
                                + Math.floorMod(hash >> 4, 49) - 24;

                float worldY =
                        y * SurvivalVisuals.CELL_SIZE
                                + Math.floorMod(hash >> 10, 49) - 24;

                if (!patchFitsGrass(
                        variant,
                        worldX,
                        worldY,
                        mapWidth,
                        mapHeight,
                        surfaceMap)) {
                    continue;
                }

                addLargePatch(variant, worldX, worldY);
            }
        }
                // Scatter smaller grass details between the large patches.
        for (int y = 1; y < mapHeight - 1; y += 3) {
            for (int x = 1; x < mapWidth - 1; x += 3) {

                int hash = groundHash(x + 317, y + 911);

                if ((hash & 3) != 0) {
                    continue;
                }

                int variant = Math.floorMod(hash, 4);

                float worldX =
                        x * SurvivalVisuals.CELL_SIZE
                                + Math.floorMod(hash >> 5, 33) - 16;

                float worldY =
                        y * SurvivalVisuals.CELL_SIZE
                                + Math.floorMod(hash >> 11, 33) - 16;

                if (!smallDetailFitsGrass(
        variant,
        worldX,
        worldY,
        mapWidth,
        mapHeight,
        surfaceMap)) {
    continue;
}

addSmallDetail(variant, worldX, worldY);
            }
        }
    }
private void addSmallDetail(int variant, float worldX, float worldY) {

    Image detail =
            new Image(Assets.Environment.SURVIVAL_GROUND_DETAILS);

    switch (variant) {

        case 0:
            detail.frame(
                    detail.texture.uvRect(
                            0, 225,
                            250, 512
                    )
            );
            break;

        case 1:
            detail.frame(
                    detail.texture.uvRect(
                            250, 225,
                            500, 512
                    )
            );
            break;

        case 2:
            detail.frame(
                    detail.texture.uvRect(
                            500, 225,
                            750, 512
                    )
            );
            break;

        default:
            detail.frame(
                    detail.texture.uvRect(
                            750, 225,
                            1001, 512
                    )
            );
            break;
    }

    detail.scale.set(0.18f);

    detail.x = worldX;
    detail.y = worldY;

    add(detail);
}
        private void addLargePatch(int variant, float worldX, float worldY) {

        Image patch =
                new Image(Assets.Environment.SURVIVAL_GROUND_DETAILS);

        switch (variant) {

            case 0:
                patch.frame(
                        patch.texture.uvRect(
                                0, 0,
                                350, 225
                        )
                );
                break;

            case 1:
                patch.frame(
                        patch.texture.uvRect(
                                350, 0,
                                700, 225
                        )
                );
                break;

            default:
                patch.frame(
                        patch.texture.uvRect(
                                700, 0,
                                1001, 225
                        )
                );
                break;
        }

        patch.scale.set(0.25f);

        patch.x = worldX;
        patch.y = worldY;

        add(patch);
    }
private boolean patchFitsGrass(
        int variant,
        float worldX,
        float worldY,
        int mapWidth,
        int mapHeight,
        int[] surfaceMap) {

    float patchWidth;

    switch (variant) {
        case 0:
        case 1:
            patchWidth = 350f * 0.25f;
            break;

        default:
            patchWidth = 301f * 0.25f;
            break;
    }

    float patchHeight = 225f * 0.25f;

    float right = worldX + patchWidth - 0.01f;
    float bottom = worldY + patchHeight - 0.01f;

    int leftCell = (int) Math.floor(
            worldX / SurvivalVisuals.CELL_SIZE);
    int rightCell = (int) Math.floor(
            right / SurvivalVisuals.CELL_SIZE);

    int topCell = (int) Math.floor(
            worldY / SurvivalVisuals.CELL_SIZE);
    int bottomCell = (int) Math.floor(
            bottom / SurvivalVisuals.CELL_SIZE);

    if (leftCell < 0
            || topCell < 0
            || rightCell >= mapWidth
            || bottomCell >= mapHeight) {
        return false;
    }

    for (int yy = topCell; yy <= bottomCell; yy++) {
        for (int xx = leftCell; xx <= rightCell; xx++) {

            int cell = xx + yy * mapWidth;

            if (surfaceMap[cell] != SurvivalTerrain.GRASS) {
                return false;
            }
        }
    }

    return true;
}
private boolean smallDetailFitsGrass(
        int variant,
        float worldX,
        float worldY,
        int mapWidth,
        int mapHeight,
        int[] surfaceMap) {

    float detailWidth;

    switch (variant) {
        case 0:
        case 1:
        case 2:
            detailWidth = 250f * 0.18f;
            break;

        default:
            detailWidth = 251f * 0.18f;
            break;
    }

    float detailHeight = 287f * 0.18f;

    float right = worldX + detailWidth - 0.01f;
    float bottom = worldY + detailHeight - 0.01f;

    int leftCell = (int) Math.floor(
            worldX / SurvivalVisuals.CELL_SIZE);
    int rightCell = (int) Math.floor(
            right / SurvivalVisuals.CELL_SIZE);

    int topCell = (int) Math.floor(
            worldY / SurvivalVisuals.CELL_SIZE);
    int bottomCell = (int) Math.floor(
            bottom / SurvivalVisuals.CELL_SIZE);

    if (leftCell < 0
            || topCell < 0
            || rightCell >= mapWidth
            || bottomCell >= mapHeight) {
        return false;
    }

    for (int yy = topCell; yy <= bottomCell; yy++) {
        for (int xx = leftCell; xx <= rightCell; xx++) {

            int cell = xx + yy * mapWidth;

            if (surfaceMap[cell] != SurvivalTerrain.GRASS) {
                return false;
            }
        }
    }

    return true;
}
    private int groundHash(int x, int y) {

        int hash = x * 73428767;
        hash ^= y * 912931;
        hash ^= 0x5F356495;

        hash ^= hash >>> 13;
        hash *= 1274126177;
        hash ^= hash >>> 16;

        return hash;
    }
}