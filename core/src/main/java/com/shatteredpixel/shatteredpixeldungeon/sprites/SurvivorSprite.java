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

    private Animation southWalk;
    private Animation southIdle;
    private boolean hasMovementSample;
    private float previousWorldX;
    private float previousWorldY;

    public SurvivorSprite() {
        super();
        updateArmor();
    }

    @Override
    public void updateArmor() {

        texture( Assets.Sprites.SURVIVOR_SOUTH_WALK_PRODUCTION_TEST );

        // Keep the 128x192 source at the existing 32x48 world-space bounds.
        scale.set( 0.25f );
        TextureFilm frames = new TextureFilm( texture, 128, 192 );

        idle = new Animation( 1, true );
        idle.frames( frames, 0 );

        run = new Animation( 1, true );
        run.frames( frames, 0 );

        // 8x4 atlas: old static sprite in cell 0, Blender frames 1-31 in cells 1-31.
        // Frame 32 duplicates frame 1 and is deliberately excluded.
        southWalk = new Animation( 30, true );
        southWalk.frames( frames, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16,
                17, 18, 19, 20, 21, 22, 23, 24,
                25, 26, 27, 28, 29, 30, 31 );
        southIdle = new Animation( 1, true );
        southIdle.frames( frames, 1 );
        hasMovementSample = false;

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
        updateSouthWalk(position);

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

    private void updateSouthWalk(PointF position) {
        float dx = hasMovementSample ? position.x - previousWorldX : 0f;
        float dy = hasMovementSample ? position.y - previousWorldY : 0f;
        previousWorldX = position.x;
        previousWorldY = position.y;
        hasMovementSample = true;

        // Observe resolved, unrounded movement; input can remain held against a wall.
        // Leave action/death animations and their completion callbacks alone.
        if (curAnim != idle && curAnim != run
                && curAnim != southWalk && curAnim != southIdle) {
            return;
        }

        // Only the SOUTH sector (+Y, within 22.5 degrees) has a walk in this test.
        if (dy > 0.0001f && Math.abs(dx) <= dy * 0.41421356f) {
            // Use the authored SOUTH view, not the random flip set when linking a hero.
            flipHorizontal = false;
            play(southWalk);
        } else if (Math.abs(dx) > 0.0001f || Math.abs(dy) > 0.0001f) {
            play(idle);
        } else if (curAnim == southWalk) {
            play(southIdle);
        } else if (curAnim == run) {
            play(idle);
        }
    }
}
