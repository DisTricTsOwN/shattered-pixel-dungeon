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

/**
 * Shared visual-scale settings for the survival rendering prototype.
 *
 * Gameplay continues to use the existing logical dungeon cells.
 * CELL_SIZE controls how large one logical cell appears in the
 * survival presentation layer.
 */
public final class SurvivalVisuals {

    public static final int CELL_SIZE = 32;

    private SurvivalVisuals() {
        // Utility class.
    }
}