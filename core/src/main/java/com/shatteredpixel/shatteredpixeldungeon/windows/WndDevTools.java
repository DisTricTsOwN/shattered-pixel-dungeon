package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.items.firearms.Pistol;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Zombie;
import com.watabou.utils.PathFinder;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ZombieNoise;

public class WndDevTools extends Window {

    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;

    private int pos;

    public WndDevTools() {

        super();

        addButton(new RedButton("Duplicate Item") {
            @Override
            protected void onClick() {

                hide();

                GameScene.selectItem(new WndBag.ItemSelector() {

                    @Override
                    public String textPrompt() {
                        return "Select an item to duplicate";
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item != null;
                    }

                    @Override
                    public void onSelect(Item item) {

                        if (item == null) {
                            return;
                        }

                        Item duplicate = item.duplicate();

                        if (duplicate == null) {
                            GLog.w("Unable to duplicate that item.");
                            return;
                        }

                        if (duplicate.collect()) {
                            GLog.p("Item duplicated.");
                        } else {
                            GLog.w("Your inventory is full.");
                        }
                    }
                });
            }
        });

       addButton(new RedButton("Heal Player") {
    @Override
    protected void onClick() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Dungeon.hero.HP = Dungeon.hero.HT;
            GLog.p("Player fully healed.");
        }
    }
}); 
        
   addButton(new RedButton("Reveal Map") {
    @Override
    protected void onClick() {

        if (Dungeon.level == null) {
            return;
        }

        int length = Dungeon.level.length();

        for (int i = 0; i < length; i++) {
            if (Dungeon.level.discoverable[i]) {
                Dungeon.level.mapped[i] = true;
                Dungeon.level.discover(i);
            }
        }

        GameScene.updateFog();
        GLog.p("Map revealed.");
    }
});    

addButton(new RedButton("Spawn Zombie") {
    @Override
    protected void onClick() {

        if (Dungeon.hero == null || Dungeon.level == null) {
            GLog.w("Unable to spawn zombie.");
            return;
        }

        Zombie zombie = new Zombie();
        int spawnPos = -1;

        for (int offset : PathFinder.NEIGHBOURS8) {

            int cell = Dungeon.hero.pos + offset;

            if (cell >= 0
                    && cell < Dungeon.level.length()
                    && Dungeon.level.passable[cell]
                    && Actor.findChar(cell) == null) {

                spawnPos = cell;
                break;
            }
        }

        if (spawnPos == -1) {
            GLog.w("No open space near the player.");
            return;
        }

        zombie.pos = spawnPos;
        zombie.state = zombie.WANDERING;

        GameScene.add(zombie);

        GLog.p("Zombie spawned.");
    }
});

addButton(new RedButton("Spawn Pistol") {
    @Override
    protected void onClick() {

        Pistol pistol = new Pistol();

        if (pistol.collect()) {
            GLog.p("Pistol added to inventory.");
        } else {
            Dungeon.level.drop(pistol, Dungeon.hero.pos).sprite.drop();
            GLog.w("Inventory full. Pistol dropped at your feet.");
        }
    }
});

addButton(new RedButton("Make Noise") {
    @Override
    protected void onClick() {

        if (Dungeon.hero == null) {
            return;
        }

        int zombiesAlerted = ZombieNoise.makeNoise(Dungeon.hero.pos, 6f);

        if (zombiesAlerted > 0) {
            GLog.p("Noise made. Zombies investigating: " + zombiesAlerted);
        } else {
            GLog.w("Noise made, but no zombies heard it.");
        }
    }
});
        
        addButton(new RedButton("Spawn Item") {
    @Override
    protected void onClick() {
        hide();
        GameScene.show(new WndDevSpawnItem());
    }
});
                addButton(new RedButton("Back") {
            @Override
            protected void onClick() {
                hide();
                GameScene.show(new WndGame());
            }
        });
        resize(WIDTH, pos);
    }

    private void addButton(RedButton btn) {
        add(btn);
        btn.setRect(0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT);
        pos += BTN_HEIGHT;
    }
}