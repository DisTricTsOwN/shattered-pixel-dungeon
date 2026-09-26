package com.shatteredpixel.shatteredpixeldungeon.tiles;

/** Rectangle overlap helpers. All coordinates must be in the same local space. */
public final class SurvivalPolygonOverlap {
    private SurvivalPolygonOverlap() {
    }

    /** SAT on rectangle axes and every convex polygon edge normal. Touching blocks. */
    public static boolean convex(float x, float y, float halfSize, float[][] polygon) {
        if (separated(x, y, halfSize, polygon, 1, 0)
                || separated(x, y, halfSize, polygon, 0, 1)) return false;
        for (int i = 0; i < polygon.length; i++) {
            float[] a = polygon[i], b = polygon[(i + 1) % polygon.length];
            if (separated(x, y, halfSize, polygon, a[1] - b[1], b[0] - a[0])) {
                return false;
            }
        }
        return true;
    }

    private static boolean separated(float x, float y, float halfSize,
                                     float[][] polygon, float nx, float ny) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (float[] p : polygon) {
            double projection = (double) p[0] * nx + (double) p[1] * ny;
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        double center = (double) x * nx + (double) y * ny;
        double radius = halfSize * (Math.abs((double) nx) + Math.abs((double) ny));
        return max < center - radius || min > center + radius;
    }

    /** Complements corner containment for a possibly concave house polygon. */
    public static boolean boundary(float x, float y, float halfSize, float[][] polygon) {
        for (int i = 0; i < polygon.length; i++) {
            float[] a = polygon[i], b = polygon[(i + 1) % polygon.length];
            if (Math.max(a[0], b[0]) < x - halfSize
                    || Math.min(a[0], b[0]) > x + halfSize
                    || Math.max(a[1], b[1]) < y - halfSize
                    || Math.min(a[1], b[1]) > y + halfSize) continue;
            // Segment-vs-rectangle SAT: rectangle axes above, segment normal below.
            double nx = (double) a[1] - b[1], ny = (double) b[0] - a[0];
            double distance = ((double) x - a[0]) * nx + ((double) y - a[1]) * ny;
            if (Math.abs(distance) <= halfSize * (Math.abs(nx) + Math.abs(ny))) return true;
        }
        return false;
    }
}
