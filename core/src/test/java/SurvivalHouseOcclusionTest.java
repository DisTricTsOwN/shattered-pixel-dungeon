import com.shatteredpixel.shatteredpixeldungeon.tiles.SurvivalHouseOcclusion;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Standalone rendering classification checks; reads the approved source, never copies it. */
public class SurvivalHouseOcclusionTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/devtools/SurvivalHouseCollisionTest.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        String geometry = source.substring(source.indexOf("PREVIEW_POLYGON ="));
        geometry = geometry.substring(0, geometry.indexOf("};"));
        Matcher vertices = Pattern.compile("\\{([\\d.]+)f?, ([\\d.]+)f?\\}").matcher(geometry);
        List<float[]> points = new ArrayList<>();
        while (vertices.find()) points.add(new float[]{Float.parseFloat(vertices.group(1)),
                Float.parseFloat(vertices.group(2))});
        float[][] p = points.toArray(new float[0][]);
        check(p.length == 9, "Read approved P0-P8 geometry");
        for (int[] edge : new int[][]{{7,8},{8,0}}) {
            float x = (p[edge[0]][0] + p[edge[1]][0]) / 2;
            float y = (p[edge[0]][1] + p[edge[1]][1]) / 2;
            check(!SurvivalHouseOcclusion.behind(x,y+1,p), "Visible before rear crossing");
            check(SurvivalHouseOcclusion.behind(x,y-1,p), "Occluded after rear crossing");
            check(!SurvivalHouseOcclusion.behind(x,y+1,p), "Visible again on return");
        }
        check(!SurvivalHouseOcclusion.behind(p[8][0],p[8][1],p), "On P8 stays in front");
        // Traverse the rear from either side; no history/hysteresis can leave a stale layer.
        for (int direction : new int[]{1,-1}) {
            boolean entered = false, exited = false;
            for (int i = 0; i <= 400; i++) {
                float fraction = (direction == 1 ? i : 400-i) / 400f;
                float x = p[7][0]-20 + fraction * (p[0][0]-p[7][0]+40);
                boolean behind = SurvivalHouseOcclusion.behind(x,p[8][1]-10,p);
                if (behind) entered = true;
                else if (entered) exited = true;
                check(behind == (x >= p[7][0] && x <= p[0][0]), "Rear traversal bounds");
            }
            check(entered && exited, "Entry and exit in each direction");
        }
        check(!SurvivalHouseOcclusion.behind(p[7][0]-1,170,p), "Left-side grass stays in front");
        check(!SurvivalHouseOcclusion.behind(p[0][0]+1,170,p), "Right-side grass stays in front");
        System.out.println("Rear occlusion checks passed, including both traversal directions.");
    }
}
