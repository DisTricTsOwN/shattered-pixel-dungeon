package com.shatteredpixel.shatteredpixeldungeon.windows;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

public class WndDevSpawnCategory extends WndOptions {

    private final Generator.Category category;

    public WndDevSpawnCategory(Generator.Category category) {
        super(
                "Spawn Item",
                "Select an item:",
                getItemNames(category)
        );

        this.category = category;
    }

    private static String[] getItemNames(Generator.Category category) {

        String[] names = new String[category.classes.length];

        for (int i = 0; i < category.classes.length; i++) {

            @SuppressWarnings("unchecked")
Class<? extends Item> itemClass =
        (Class<? extends Item>) category.classes[i];

Item item = Reflection.newInstance(itemClass);

            if (item != null) {
                names[i] = item.name();
            } else {
                names[i] = category.classes[i].getSimpleName();
            }
        }

        return names;
    }

    @Override
    protected void onSelect(int index) {

        if (index < 0 || index >= category.classes.length) {
            return;
        }

        @SuppressWarnings("unchecked")
Class<? extends Item> itemClass =
        (Class<? extends Item>) category.classes[index];

Item item = Reflection.newInstance(itemClass);

        if (item == null) {
            GLog.w("Unable to create that item.");
            return;
        }

        if (item.collect()) {
            GLog.p(item.name() + " spawned.");
        } else {
            Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
            GLog.w("Inventory full. Item dropped at your feet.");
        }
    }
}