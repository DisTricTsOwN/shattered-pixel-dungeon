package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;

public final class ZombieNoise {

    private ZombieNoise() {
        // Utility class
    }

    public static int makeNoise(int sourcePos, float radius) {

        if (Dungeon.level == null) {
            return 0;
        }

        int zombiesAlerted = 0;

        for (Char ch : Actor.chars()) {

            if (!(ch instanceof Zombie)) {
                continue;
            }

            Zombie zombie = (Zombie) ch;

            if (!zombie.isAlive()) {
                continue;
            }

            if (Dungeon.level.trueDistance(sourcePos, zombie.pos) <= radius) {
                zombie.hearNoise(sourcePos);
                zombiesAlerted++;
            }
        }

        return zombiesAlerted;
    }
}