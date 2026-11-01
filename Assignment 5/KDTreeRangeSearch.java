import java.util.*;

class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double get(int axis) {
        return (axis == 0) ? x : y;
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}

class Range {
    double xMin, xMax;
    double yMin, yMax;

    public Range(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public boolean contains(Point p) {
        return p.x >= xMin && p.x <= xMax && p.y >= yMin && p.y <= yMax;
    }

    @Override
    public String toString() {
        return String.format("X:[%.1f, %.1f], Y:[%.1f, %.1f]", xMin, xMax, yMin, yMax);
    }
}

class KDNode {
    Point point;
    KDNode left;
    KDNode right;
    int axis; // 0 for x-split, 1 for y-split

    public KDNode(Point point, int axis) {
        this.point = point;
        this.axis = axis;
        this.left = null;
        this.right = null;
    }
}

public class KDTreeRangeSearch {

    // Helper method to build a balanced KD-Tree from a list of points
    public static KDNode buildTree(List<Point> points, int depth) {
        if (points == null || points.isEmpty()) {
            return null;
        }

        int axis = depth % 2;
        // Sort points based on current axis
        points.sort(Comparator.comparingDouble(p -> p.get(axis)));

        int medianIdx = points.size() / 2;
        KDNode node = new KDNode(points.get(medianIdx), axis);

        node.left = buildTree(new ArrayList<>(points.subList(0, medianIdx)), depth + 1);
        node.right = buildTree(new ArrayList<>(points.subList(medianIdx + 1, points.size())), depth + 1);

        return node;
    }

    /**
     * Recursive function to search and report all points below 'v' in range 'R'
     * 
     * @param v        Root of subtree
     * @param R        Query range
     * @param reported List collecting matching points
     */
    public static void SearchKDTree(KDNode v, Range R, List<Point> reported) {
        if (v == null) {
            return;
        }

        // 1. Check if point at current node v lies inside range R
        if (R.contains(v.point)) {
            reported.add(v.point);
        }

        // 2. Decide whether to recurse into left child
        double minBound = (v.axis == 0) ? R.xMin : R.yMin;
        if (minBound <= v.point.get(v.axis)) {
            SearchKDTree(v.left, R, reported);
        }

        // 3. Decide whether to recurse into right child
        double maxBound = (v.axis == 0) ? R.xMax : R.yMax;
        if (maxBound >= v.point.get(v.axis)) {
            SearchKDTree(v.right, R, reported);
        }
    }

    public static void main(String[] args) {
        // Sample Dataset of 2D Points
        List<Point> pointList = Arrays.asList(
                new Point(2, 3), new Point(5, 4), new Point(9, 6),
                new Point(4, 7), new Point(8, 1), new Point(7, 2),
                new Point(6, 8), new Point(1, 9), new Point(3, 5));

        // Build KD-Tree
        KDNode root = buildTree(new ArrayList<>(pointList), 0);

        System.out.println("=== Question 1: KD-Tree Range Search Test Cases ===\n");

        Range r1 = new Range(3, 7, 2, 7);
        runTest("Test Set 1", root, r1);

        Range r2 = new Range(1, 5, 4, 9);
        runTest("Test Set 2", root, r2);

        Range r3 = new Range(6, 10, 0, 5);
        runTest("Test Set 3", root, r3);
    }

    private static void runTest(String testName, KDNode root, Range range) {
        List<Point> result = new ArrayList<>();
        SearchKDTree(root, range, result);
        System.out.println(testName + " Range: " + range);
        System.out.println("Reported Points (" + result.size() + "): " + result);
        System.out.println("");
    }
}