package com.shatteredpixel.shatteredpixeldungeon.items.firearms;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class Ammo9mm extends Item {

    {
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