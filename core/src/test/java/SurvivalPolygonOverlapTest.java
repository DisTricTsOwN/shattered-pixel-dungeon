import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalPolygonOverlap;
import com.shatteredpixel.shatteredpixeldungeon.devtools.SurvivalHousePropDebug;

/** Dependency-free regression checks; compile with the overlap and prop-definition sources. */
public class SurvivalPolygonOverlapTest {
    private static void check(boolean result, String message) {
        if (!result) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        float[][] small = {{-1,-1},{1,-1},{1,1},{-1,1}};
        check(SurvivalPolygonOverlap.convex(0,0,6,small), "Small prop contained in footprint");
        check(SurvivalPolygonOverlap.convex(0,0,0.5f,small), "Footprint inside prop");
        check(SurvivalPolygonOverlap.convex(7,0,6,small), "Exact edge contact blocks");
        check(!SurvivalPolygonOverlap.convex(7.01f,0,6,small), "Separated rectangles");
        float[][] strip = {{-10,-1},{10,-1},{10,1},{-10,1}};
        check(SurvivalPolygonOverlap.convex(0,0,6,strip), "Crossing without contained corners");
        float[][] diamond = {{0,-10},{10,0},{0,10},{-10,0}};
        check(!SurvivalPolygonOverlap.convex(9,9,1,diamond), "Edge-normal separating axis");
        float[][] reversed = {{-10,0},{0,10},{10,0},{0,-10}};
        check(SurvivalPolygonOverlap.convex(0,0,6,reversed), "Reverse winding");
        check(SurvivalPolygonOverlap.boundary(0,0,6,strip), "House edge crossing");
        check(SurvivalPolygonOverlap.boundary(0,0,6,small), "House vertex containment");
        check(!SurvivalPolygonOverlap.boundary(30,30,6,diamond), "Distant house boundary");
        // Concave notch stays open: do not treat the house as a convex hull.
        float[][] concave = {{0,0},{20,0},{20,20},{14,20},{14,6},{6,6},{6,20},{0,20}};
        check(!SurvivalPolygonOverlap.boundary(10,15,2,concave), "Open concave notch");
        for (SurvivalHousePropDebug.Footprint prop : SurvivalHousePropDebug.FOOTPRINTS) {
            float x = 0, y = 0;
            for (float[] v : prop.vertices) { x += v[0]; y += v[1]; }
            x /= prop.vertices.length;
            y /= prop.vertices.length;
            check(SurvivalPolygonOverlap.convex(x,y,6,prop.vertices), prop.name + " blocks at center");
            check(!SurvivalPolygonOverlap.convex(x+100,y+100,6,prop.vertices), prop.name + " distant");
        }
        System.out.println("All 19 overlap checks passed.");
    }
}
