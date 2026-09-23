/*
 * Shattered Pixel Dungeon
 * Survival prototype
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Image;
import com.watabou.noosa.Group;

public class SurvivalHouse extends Group {

    // Logical footprint of the current test house.
    public static final int HOUSE_X = 4;
    public static final int HOUSE_Y = 3;
    public static final int HOUSE_WIDTH = 10;
    public static final int HOUSE_HEIGHT = 6;

    private final Group lowerStructure;
    private final Group upperStructure;
    private final Group roof;

    public SurvivalHouse() {

        super();

        /*
         * LOWER STRUCTURE
         *
         * Foundation, floor, lower walls, porch, etc.
         * These can remain visible when the player enters.
         */
        lowerStructure = new Group();
        add(lowerStructure);

        /*
         * UPPER STRUCTURE
         *
         * Exterior wall faces, windows, trim and other
         * raised portions of the building.
         */
        upperStructure = new Group();
        add(upperStructure);

        /*
         * ROOF
         *
         * Kept completely separate so it can later fade
         * or disappear when the Survivor enters the house.
         */
        roof = new Group();
        add(roof);
        buildPrototype();
    }
private void buildPrototype() {

    Image house = new Image(
            Assets.Environment.SURVIVAL_HOUSE_DETAILED
    );

    /*
     * Temporary first-pass scale.
     * The source artwork is intentionally high resolution.
     */
    house.scale.set(0.25f);

    /*
 * Temporary positioning controls for aligning the
 * detailed artwork with the logical house footprint.
 */
float houseOffsetX = -32f;
float houseOffsetY = 32f;

house.x = footprintX() + houseOffsetX;

house.y =
        footprintY()
                + footprintHeight()
                - house.height * house.scale.y
                + houseOffsetY;

    upperStructure.add(house);
}
    public float footprintX() {
        return HOUSE_X * SurvivalVisuals.CELL_SIZE;
    }

    public float footprintY() {
        return HOUSE_Y * SurvivalVisuals.CELL_SIZE;
    }

    public float footprintWidth() {
        return HOUSE_WIDTH * SurvivalVisuals.CELL_SIZE;
    }

    public float footprintHeight() {
        return HOUSE_HEIGHT * SurvivalVisuals.CELL_SIZE;
    }

    public Group lowerStructure() {
        return lowerStructure;
    }

    public Group upperStructure() {
        return upperStructure;
    }

    public Group roof() {
        return roof;
    }
}