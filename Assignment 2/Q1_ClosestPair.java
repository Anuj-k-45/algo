import java.util.*;

class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

public class Q1_ClosestPair {

    // Calculate Euclidean distance
    static double distance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Brute force for small number of points
    static double bruteForce(Point[] points, int left, int right) {
        double min = Double.MAX_VALUE;

        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                min = Math.min(min, distance(points[i], points[j]));
            }
        }

        return min;
    }

    // Find minimum distance in strip
    static double stripClosest(ArrayList<Point> strip, double d) {

        strip.sort(Comparator.comparingDouble(p -> p.y));

        double min = d;

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1;
                 j < strip.size() &&
                 (strip.get(j).y - strip.get(i).y) < min;
                 j++) {

                min = Math.min(min,
                        distance(strip.get(i), strip.get(j)));
            }
        }

        return min;
    }

    // Recursive Divide and Conquer
    // dl, dr, d = min left distance, min right distance, min distance
    static double closestUtil(Point[] points, int left, int right) {

        // Base case
        if (right - left <= 2)
            return bruteForce(points, left, right);

        int mid = (left + right) / 2;
        Point midPoint = points[mid];

        // Solve left and right recursively
        double dl = closestUtil(points, left, mid);
        double dr = closestUtil(points, mid + 1, right);

        double d = Math.min(dl, dr);

        // Build strip
        ArrayList<Point> strip = new ArrayList<>();

        for (int i = left; i <= right; i++) {
            if (Math.abs(points[i].x - midPoint.x) < d)
                strip.add(points[i]);
        }

        return Math.min(d, stripClosest(strip, d));
    }

    static double closest(Point[] points) {

        Arrays.sort(points, Comparator.comparingDouble(p -> p.x));

        return closestUtil(points, 0, points.length - 1);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of points: ");
        int n = sc.nextInt();

        Point[] points = new Point[n];

        System.out.println("Enter x and y coordinates:");

        for (int i = 0; i < n; i++) {
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            points[i] = new Point(x, y);
        }

        double ans = closest(points);

        System.out.printf("Closest distance = %.4f\n", ans);
        sc.close();
    }
}
