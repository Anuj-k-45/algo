import java.util.*;

class Point {
    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}

class KDNode {
    Point point;
    KDNode left;
    KDNode right;

    public KDNode(Point point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }
}

public class KDTreeBuilder {

    /**
     * Recursive function to build a two-dimensional kd-tree.
     * Follows algorithm BUILDKDTREE(P, depth).
     */
    public static KDNode buildKDTree(List<Point> P, int depth) {
        if (P == null || P.isEmpty()) {
            return null;
        }

        // Base case: if P contains only one point
        if (P.size() == 1) {
            System.out.println("Depth " + depth + ": Leaf node created with point " + P.get(0));
            return new KDNode(P.get(0));
        }

        // Determine partition type based on depth
        boolean isEvenDepth = (depth % 2 == 0);
        String partitionType = isEvenDepth ? "Vertical (X-axis)" : "Horizontal (Y-axis)";

        // Sort points based on the current dimension
        if (isEvenDepth) {
            P.sort(Comparator.comparingInt(p -> p.x));
        } else {
            P.sort(Comparator.comparingInt(p -> p.y));
        }

        // Find median index (floor division for balanced partition)
        int medianIdx = (P.size() - 1) / 2;
        Point medianPoint = P.get(medianIdx);

        // Divide points into two sublists P1 and P2
        List<Point> P1 = new ArrayList<>(P.subList(0, medianIdx + 1));
        List<Point> P2 = new ArrayList<>(P.subList(medianIdx + 1, P.size()));

        // Print split details
        System.out.println();
        System.out.println("Depth: " + depth + " | Partition Type: " + partitionType);
        System.out.println("Median Point (Splitting Point): " + medianPoint);
        System.out.println("Left/Below Subset (P1): " + P1);
        System.out.println("Right/Above Subset (P2): " + P2);
        System.out.println();

        // Create internal node storing median point
        KDNode node = new KDNode(medianPoint);

        // Recursive calls for left and right children
        node.left = buildKDTree(P1, depth + 1);
        node.right = buildKDTree(P2, depth + 1);

        return node;
    }

    // Helper method to print tree structure (In-order Traversal)
    public static void printTree(KDNode node, String prefix, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "├── " : "└── ") + node.point);
            printTree(node.left, prefix + (isLeft ? "│   " : "    "), true);
            printTree(node.right, prefix + (isLeft ? "│   " : "    "), false);
        }
    }

    public static void main(String[] args) {
        // Sample input given in Question 2
        List<Point> points = new ArrayList<>(Arrays.asList(
            new Point(1, 9),
            new Point(2, 3),
            new Point(4, 1),
            new Point(3, 7),
            new Point(5, 4),
            new Point(6, 8),
            new Point(7, 2),
            new Point(8, 8),
            new Point(7, 9),
            new Point(9, 6)
        ));

        KDNode root = buildKDTree(points, 0);

        System.out.println("\n=== Final KD-Tree Structure ===");
        printTree(root, "", false);
    }
}