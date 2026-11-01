import java.util.*;

class PolygonPoint {
    double x, y;

    public PolygonPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

public class PolygonMonotonicity {

    /**
     * Checks if a polygon is monotone with respect to the specified axis ('x' or
     * 'y')
     */
    public static boolean isMonotone(List<PolygonPoint> polygon, char axis) {
        int n = polygon.size();
        if (n < 3)
            return false;

        // Extract coordinate values along the chosen axis
        double[] coords = new double[n];
        for (int i = 0; i < n; i++) {
            coords[i] = (axis == 'x' || axis == 'X') ? polygon.get(i).x : polygon.get(i).y;
        }

        // Count local extrema (turning points in coordinate value along the perimeter)
        int localExtremaCount = 0;

        for (int i = 0; i < n; i++) {
            double prev = coords[(i - 1 + n) % n];
            double curr = coords[i];
            double next = coords[(i + 1) % n];

            // Ignore flat edges (strictly equal coordinates)
            if (curr == prev || curr == next) {
                continue;
            }

            // Local maximum or local minimum check
            boolean isLocalMax = (curr > prev) && (curr > next);
            boolean isLocalMin = (curr < prev) && (curr < next);

            if (isLocalMax || isLocalMin) {
                localExtremaCount++;
            }
        }

        // A monotone polygon along an axis has exactly 2 local extrema (1 min, 1 max)
        return localExtremaCount <= 2;
    }

    public static void testPolygon(String name, List<PolygonPoint> polygon) {
        boolean xMono = isMonotone(polygon, 'x');
        boolean yMono = isMonotone(polygon, 'y');

        System.out.println("Polygon: " + name);
        System.out.println("Vertices: " + polygon);
        System.out.println("X-Monotone: " + (xMono ? "YES" : "NO"));
        System.out.println("Y-Monotone: " + (yMono ? "YES" : "NO"));
        if (xMono && yMono) {
            System.out.println("Classification: Both X-Monotone and Y-Monotone (Convex/Strict)");
        } else if (xMono) {
            System.out.println("Classification: Strictly X-Monotone");
        } else if (yMono) {
            System.out.println("Classification: Strictly Y-Monotone");
        } else {
            System.out.println("Classification: Non-Monotone");
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        System.out.println("=== Question 2: Polygon Monotonicity Tests ===\n");

        // 1. X-Monotone Polygon (C-shape oriented vertically)
        List<PolygonPoint> xMonotonePoly = Arrays.asList(
                new PolygonPoint(0, 0),
                new PolygonPoint(4, 0),
                new PolygonPoint(4, 1),
                new PolygonPoint(2, 2),
                new PolygonPoint(4, 3),
                new PolygonPoint(4, 4),
                new PolygonPoint(0, 4));
        testPolygon("X-Monotone Polygon", xMonotonePoly);

        // 2. Y-Monotone Polygon (U-shape open to the right)
        List<PolygonPoint> yMonotonePoly = Arrays.asList(
                new PolygonPoint(0, 0),
                new PolygonPoint(4, 0),
                new PolygonPoint(4, 4),
                new PolygonPoint(3, 4),
                new PolygonPoint(2, 1),
                new PolygonPoint(1, 4),
                new PolygonPoint(0, 4));
        testPolygon("Y-Monotone Polygon", yMonotonePoly);

        // 3. Non-Monotone Polygon (Star / Double-notched shape)
        List<PolygonPoint> nonMonotonePoly = Arrays.asList(
                new PolygonPoint(0, 2),
                new PolygonPoint(2, 0),
                new PolygonPoint(1, 2),
                new PolygonPoint(2, 4),
                new PolygonPoint(3, 2),
                new PolygonPoint(4, 4),
                new PolygonPoint(5, 2),
                new PolygonPoint(4, 0),
                new PolygonPoint(3, 2));
        testPolygon("Non-Monotone Polygon", nonMonotonePoly);
    }
}