/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Random;

public class Zombie extends Mob {

    {
        // Temporary sprite until the zombie artwork is implemented.
        spriteClass = RatSprite.class;

        HP = HT = 12;
        defenseSkill = 2;

        maxLvl = 5;

        // Zombies begin roaming instead of sleeping.
        state = WANDERING;
    }

        public void hearNoise(int cell) {

        if (state == HUNTING || state == FLEEING) {
            return;
        }

        target = cell;
        state = INVESTIGATING;
    }
    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 5);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    @Override
    public int drRoll() {
        return super.drRoll();
    }
}