package com.shatteredpixel.shatteredpixeldungeon.items.firearms;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ZombieNoise;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Pistol extends Item {

    public static final String AC_FIRE = "FIRE";
    private static final int MAGAZINE_CAPACITY = 10;

private int roundsLoaded = MAGAZINE_CAPACITY;
private static final String ROUNDS_LOADED = "rounds_loaded";

    {
        defaultAction = AC_FIRE;
        usesTargeting = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);

        actions.add(AC_FIRE);

        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (action.equals(AC_FIRE)) {
            curUser = hero;
            curItem = this;

            GameScene.selectCell(shooter);

        } else {
            super.execute(hero, action);
        }
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer target) {

            if (target == null) {
                return;
            }

            if (roundsLoaded <= 0) {
    GLog.w("The pistol is empty.");
    return;
}

roundsLoaded--;

            Ballistica shot = new Ballistica(
        curUser.pos,
        target,
        Ballistica.STOP_TARGET
);

int impactCell = shot.collisionPos;
Char enemy = Actor.findChar(impactCell);

// The gunshot happens at the shooter's location.
ZombieNoise.makeNoise(curUser.pos, 8f);

if (enemy != null && enemy != curUser) {

    int damage = Random.NormalIntRange(5, 8);

    enemy.damage(damage, Pistol.this);

    GLog.p("Pistol hit for " + damage + " damage. "
        + roundsLoaded + "/" + MAGAZINE_CAPACITY + " rounds.");

} else {

    GLog.w("Pistol shot missed. "
        + roundsLoaded + "/" + MAGAZINE_CAPACITY + " rounds.");
}

// Prototype firing speed.
// One shot currently costs one standard game tick.
curUser.spendAndNext(Actor.TICK);
        }

        @Override
        public String prompt() {
            return "Select a target";
        }
    };

        

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ROUNDS_LOADED, roundsLoaded);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        if (bundle.contains(ROUNDS_LOADED)) {
            roundsLoaded = bundle.getInt(ROUNDS_LOADED);
        } else {
            roundsLoaded = MAGAZINE_CAPACITY;
        }
    }
}
