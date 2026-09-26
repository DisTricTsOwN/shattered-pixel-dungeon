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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalMovement;
import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalVisuals;
import com.watabou.noosa.Camera;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

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

       @Override
    public PointF worldToCamera( int cell ) {

        final int csize = SurvivalVisuals.CELL_SIZE;

        return new PointF(
                PixelScene.align(
                        Camera.main,
                        ((cell % Dungeon.level.width()) + 0.5f) * csize
                                - width() * 0.5f
                ),
                PixelScene.align(
                        Camera.main,
                        ((cell / Dungeon.level.width()) + 0.5f) * csize
                                - height()
                )
        );
    }

    @Override
    public void place(int p) {
        super.place(p);
        Camera.main.panFollow(this, 20f);
    }

    @Override
    public void update() {
        super.update();

        PointF position = SurvivalMovement.position();

        x = PixelScene.align(
                Camera.main,
                position.x - width() * 0.5f
        );

        y = PixelScene.align(
                Camera.main,
                // The bottom-center of the frame is the feet/ground anchor.
                // Collision is already centered on position, not on sprite bounds.
                position.y
                        - height()
        );
        if (SurvivalMovement.isMoving()
        && Camera.main.followTarget() != this) {
    Camera.main.panFollow(this, 20f);
}
    }
}
