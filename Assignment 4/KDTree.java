import java.util.*;

class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class KDNode {
    Point point;
    KDNode left;
    KDNode right;

    KDNode(Point point) {
        this.point = point;
    }
}

public class KDTree {

    public static KDNode buildKDTree(List<Point> points, int depth) {

        if (points.size() == 0)
            return null;

        int axis = depth % 2;

        if (axis == 0) {
            points.sort(Comparator.comparingInt(p -> p.x));
        } else {
            points.sort(Comparator.comparingInt(p -> p.y));
        }

        int mid = points.size() / 2;
        Point median = points.get(mid);

        if (axis == 0) {
            System.out.println("Depth " + depth + " : X-Partition -> Node = (" + median.x + ", " + median.y + ")");
        } else {
            System.out.println("Depth " + depth + " : Y-Partition -> Node = (" + median.x + ", " + median.y + ")");
        }

        KDNode root = new KDNode(median);

        List<Point> leftPoints = new ArrayList<>(points.subList(0, mid));
        List<Point> rightPoints = new ArrayList<>(points.subList(mid + 1, points.size()));

        root.left = buildKDTree(leftPoints, depth + 1);
        root.right = buildKDTree(rightPoints, depth + 1);

        return root;
    }

    public static void preorder(KDNode root) {

        if (root == null)
            return;

        System.out.println("(" + root.point.x + ", " + root.point.y + ")");

        preorder(root.left);
        preorder(root.right);
    }

    public static void main(String[] args) {

        List<Point> points = new ArrayList<>();

        points.add(new Point(1, 9));
        points.add(new Point(2, 3));
        points.add(new Point(4, 1));
        points.add(new Point(3, 7));
        points.add(new Point(5, 4));
        points.add(new Point(6, 8));
        points.add(new Point(7, 2));
        points.add(new Point(8, 8));
        points.add(new Point(7, 9));
        points.add(new Point(9, 6));

        KDNode root = buildKDTree(points, 0);

        System.out.println("\nPreorder Traversal:");

        preorder(root);
    }
}