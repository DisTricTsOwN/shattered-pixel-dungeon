package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class WndDevSpawnItem extends WndOptions {

    private static final String[] CATEGORY_NAMES = {
            "Weapons",
            "Armor",
            "Missile Weapons",
            "Wands",
            "Rings",
            "Artifacts",
            "Food",
            "Potions",
            "Seeds",
            "Scrolls",
            "Runestones",
            "Trinkets"
    };

    private static final Generator.Category[] CATEGORIES = {
            Generator.Category.WEAPON,
            Generator.Category.ARMOR,
            Generator.Category.MISSILE,
            Generator.Category.WAND,
            Generator.Category.RING,
            Generator.Category.ARTIFACT,
            Generator.Category.FOOD,
            Generator.Category.POTION,
            Generator.Category.SEED,
            Generator.Category.SCROLL,
            Generator.Category.STONE,
            Generator.Category.TRINKET
    };

    public WndDevSpawnItem() {
        super(
                "Spawn Item",
                "Select an item category:",
                CATEGORY_NAMES
        );
    }

   @Override
protected void onSelect(int index) {

    if (index < 0 || index >= CATEGORIES.length) {
        return;
    }

    Generator.Category category = CATEGORIES[index];

    if (category == Generator.Category.WEAPON) {

        GameScene.show(new WndOptions(
                "Weapons",
                "Select a weapon tier:",
                "Tier 1",
                "Tier 2",
                "Tier 3",
                "Tier 4",
                "Tier 5"
        ) {
            @Override
            protected void onSelect(int tier) {

                Generator.Category[] weaponTiers = {
                        Generator.Category.WEP_T1,
                        Generator.Category.WEP_T2,
                        Generator.Category.WEP_T3,
                        Generator.Category.WEP_T4,
                        Generator.Category.WEP_T5
                };

                if (tier >= 0 && tier < weaponTiers.length) {
                    GameScene.show(
                            new WndDevSpawnCategory(weaponTiers[tier])
                    );
                }
            }
        });

        return;
    }
    


    if (category == Generator.Category.MISSILE) {

        GameScene.show(new WndOptions(
                "Missile Weapons",
                "Select a missile weapon tier:",
                "Tier 1",
                "Tier 2",
                "Tier 3",
                "Tier 4",
                "Tier 5"
        ) {
            @Override
            protected void onSelect(int tier) {

                Generator.Category[] missileTiers = {
                        Generator.Category.MIS_T1,
                        Generator.Category.MIS_T2,
                        Generator.Category.MIS_T3,
                        Generator.Category.MIS_T4,
                        Generator.Category.MIS_T5
                };

                if (tier >= 0 && tier < missileTiers.length) {
                    GameScene.show(
                            new WndDevSpawnCategory(missileTiers[tier])
                    );
                }
            }
        });

        return;
    }

    GameScene.show(new WndDevSpawnCategory(category));
}
}