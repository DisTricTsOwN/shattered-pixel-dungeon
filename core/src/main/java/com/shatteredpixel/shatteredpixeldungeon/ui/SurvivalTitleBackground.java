package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class SurvivalTitleBackground extends Component {

    private Image background;
    
    private Image particles;

    private float particleBaseX;
    private float particleBaseY;
    private float particleTime;

    public SurvivalTitleBackground(float width, float height) {
        super();

        this.width = width;
        this.height = height;

        // Main title artwork
        background = new Image(Assets.Splashes.SURVIVAL_TITLE);
        add(background);

        

        // Atmospheric foreground particles
        particles = new Image(Assets.Splashes.SURVIVAL_PARTICLES);
        particles.alpha(0.80f);
        add(particles);

        layoutBackground();
    }

    private void layoutBackground() {

        float scaleX = width / background.width();
        float scaleY = height / background.height();

        float scale = Math.max(scaleX, scaleY);

        background.scale.set(scale);

        background.x = (width - background.width()) / 2f;
        background.y = (height - background.height()) / 2f;

       

        // Slight overscan keeps the particle image edges outside the
        // visible screen while the layer drifts.
        float particleScaleX = width / particles.width();
        float particleScaleY = height / particles.height();
        float particleScale =
                Math.max(particleScaleX, particleScaleY) * 1.12f;

        particles.scale.set(particleScale);

        particleBaseX = (width - particles.width()) / 2f;
        particleBaseY = (height - particles.height()) / 2f;

        particles.x = particleBaseX;
        particles.y = particleBaseY;
    }

    @Override
    public void update() {
        super.update();

        particleTime += Game.elapsed;

        // Very slow drifting motion.
        particles.x = particleBaseX
                + (float)Math.sin(particleTime * 0.18f) * 6f;

        particles.y = particleBaseY
                + (float)Math.cos(particleTime * 0.13f) * 3f;
    }
}