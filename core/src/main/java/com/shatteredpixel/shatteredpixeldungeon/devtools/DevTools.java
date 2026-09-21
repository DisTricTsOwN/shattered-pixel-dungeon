package com.shatteredpixel.shatteredpixeldungeon.devtools;

public final class DevTools {

    private static final boolean ENABLED =
            Boolean.parseBoolean(System.getProperty("district.devtools", "false"));

    private DevTools() {
        // Utility class
    }

    public static boolean isEnabled() {
        return ENABLED;
    }
}