package com.shatteredpixel.shatteredpixeldungeon.items.firearms;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Ammo9mm extends Item {

    {
    image = ItemSpriteSheet.AMMO_9MM;
    stackable = true;
}

    @Override
    public String name() {
        return "9mm Ammo";
    }

    @Override
    public String desc() {
        return "9mm pistol ammunition.";
    }
}