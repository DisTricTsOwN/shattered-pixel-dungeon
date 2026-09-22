/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.PointF;

/**
 * Shared visual-scale settings and coordinate conversions
 * for the survival presentation layer.
 *
 * Gameplay continues to operate on logical level cells.
 */
public final class SurvivalVisuals {

    public static final int CELL_SIZE = 32;

    private SurvivalVisuals() {
        // Utility class.
    }

    public static PointF cellToWorld( int cell ) {
        return new PointF(
                cell % Dungeon.level.width(),
                cell / Dungeon.level.width()
        ).scale( CELL_SIZE );
    }

    public static PointF cellCenterToWorld( int cell ) {
        return new PointF(
                (cell % Dungeon.level.width() + 0.5f) * CELL_SIZE,
                (cell / Dungeon.level.width() + 0.5f) * CELL_SIZE
        );
    }

    public static int worldToCell( float x, float y ) {
        return (int)(x / CELL_SIZE)
                + (int)(y / CELL_SIZE) * Dungeon.level.width();
    }
}