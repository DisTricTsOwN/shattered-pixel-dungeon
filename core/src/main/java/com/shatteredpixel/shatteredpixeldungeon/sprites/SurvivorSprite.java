/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class SurvivorSprite extends HeroSprite {

    public SurvivorSprite() {
        super();
        updateArmor();
    }

    @Override
    public void updateArmor() {

        texture( Assets.Sprites.SURVIVOR );

        TextureFilm frames = new TextureFilm( texture, 32, 48 );

        idle = new Animation( 1, true );
        idle.frames( frames, 0 );

        run = new Animation( 1, true );
        run.frames( frames, 0 );

        attack = new Animation( 1, false );
        attack.frames( frames, 0 );

        die = new Animation( 1, false );
        die.frames( frames, 0 );

        zap = attack.clone();
        operate = idle.clone();

        play( idle );
    }
}