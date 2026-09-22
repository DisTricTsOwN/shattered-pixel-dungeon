package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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