package com.shatteredpixel.shatteredpixeldungeon.tiles;

/** Rendering-only classification of a house-local feet point. */
public final class SurvivalHouseOcclusion {
    private SurvivalHouseOcclusion() {
    }

    /** Behind means above the finite P7-P8-P0 chain in screen/world Y coordinates.
     * The endpoint X limits keep the Survivor in front alongside either wall.
     * On the chain itself the Survivor remains in front until the feet cross it.
     */
    public static boolean behind(float feetX, float feetY, float[][] polygon) {
        float[] left = polygon[7], peak = polygon[8], right = polygon[0];
        if (feetX < left[0] || feetX > right[0]) return false;
        float[] a = feetX <= peak[0] ? left : peak;
        float[] b = feetX <= peak[0] ? peak : right;
        double boundaryY = a[1] + ((double) feetX - a[0])
                * ((double) b[1] - a[1]) / ((double) b[0] - a[0]);
        return feetY < boundaryY;
    }
}
