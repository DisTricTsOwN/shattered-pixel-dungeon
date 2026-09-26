package com.shatteredpixel.shatteredpixeldungeon.devtools;

/** Approved shared footprints: debug rendering and developer PREVIEW-only collision. */
public final class SurvivalHousePropDebug {

    // House-local world pixels, estimated from the bases in the 1536x1024 artwork.
    // Image coordinates convert to local coordinates as (x / 4 - 32, y / 4 - 32).
    // Vertices are ordered around each footprint, starting at vertex 0.
    public static final Footprint[] FOOTPRINTS = {
            new Footprint("BARREL", 360, 185, new float[][]{
                    // Parallel 2-pixel outward offset; center remains (287, 173).
                    {298.457807f, 173}, {294.687419f, 178.278544f},
                    {287, 180.052564f}, {279.312581f, 178.278544f},
                    {275.542193f, 173}, {279.312581f, 167.721456f},
                    {287, 165.947436f}, {294.687419f, 167.721456f}
            }),
            new Footprint("AC", 360, 155, new float[][]{
                    // Translation only: 4 pixels diagonally up-left (-4/sqrt(2) per axis).
                    {292.171573f, 150.171573f}, {305.171573f, 156.171573f},
                    {314.171573f, 149.171573f}, {301.171573f, 143.171573f}
            }),
            new Footprint("TANK", 360, 125, new float[][]{
                    {336, 133}, {333.5f, 136.5f}, {327, 138}, {320.5f, 136.5f},
                    {318, 133}, {320.5f, 129.5f}, {327, 128}, {333.5f, 129.5f}
            }),
            new Footprint("BOARDS", 360, 215, new float[][]{
                    // Translation only: 3 pixels diagonally up-left (-3/sqrt(2) per axis).
                    {264.878680f, 175.878680f}, {268.878680f, 179.878680f},
                    {278.878680f, 175.878680f}, {274.878680f, 171.878680f}
            })
    };

    private SurvivalHousePropDebug() {
    }

    public static final class Footprint {
        public final String name;
        public final float labelX, labelY;
        public final float[][] vertices;

        private Footprint(String name, float labelX, float labelY, float[][] vertices) {
            this.name = name;
            this.labelX = labelX;
            this.labelY = labelY;
            this.vertices = vertices;
        }
    }
}
