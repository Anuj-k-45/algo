import java.util.*;

public class ConvexHull {

    public static class Point {
        double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point))
                return false;
            Point other = (Point) obj;
            return this.x == other.x && this.y == other.y;
        }
    }

    private static double crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    private static double distanceSq(Point a, Point b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    //Divide and Conquer
    public static List<Point> divideAndConquer(List<Point> points) {
        if (points.size() < 3)
            return new ArrayList<>(points);
        List<Point> sorted = new ArrayList<>(points);
        sorted.sort(
                (p1, p2) -> Double.compare(p1.x, p2.x) != 0 ? Double.compare(p1.x, p2.x) : Double.compare(p1.y, p2.y));
        return divideAndConquerRec(sorted);
    }

    private static List<Point> divideAndConquerRec(List<Point> points) {
        if (points.size() <= 5)
            return jarvisMarch(points);

        int mid = points.size() / 2;
        List<Point> leftHull = divideAndConquerRec(points.subList(0, mid));
        List<Point> rightHull = divideAndConquerRec(points.subList(mid, points.size()));

        return mergeHulls(leftHull, rightHull);
    }

    private static List<Point> mergeHulls(List<Point> left, List<Point> right) {
        int n1 = left.size(), n2 = right.size();
        int rpn = 0, lpn = 0;

        for (int i = 1; i < n1; i++)
            if (left.get(i).x > left.get(rpn).x)
                rpn = i;
        for (int i = 1; i < n2; i++)
            if (right.get(i).x < right.get(lpn).x)
                lpn = i;

        int indA = rpn, indB = lpn;
        boolean done = false;
        while (!done) {
            done = true;
            while (crossProduct(right.get(indB), left.get(indA), left.get((indA + 1) % n1)) >= 0) {
                indA = (indA + 1) % n1;
            }
            while (crossProduct(left.get(indA), right.get(indB), right.get((n2 + indB - 1) % n2)) <= 0) {
                indB = (n2 - 1 + indB) % n2;
                done = false;
            }
        }
        int upperA = indA, upperB = indB;

        indA = rpn;
        indB = lpn;
        done = false;
        while (!done) {
            done = true;
            while (crossProduct(left.get(indA), right.get(indB), right.get((indB + 1) % n2)) >= 0) {
                indB = (indB + 1) % n2;
            }
            while (crossProduct(right.get(indB), left.get(indA), left.get((n1 + indA - 1) % n1)) <= 0) {
                indA = (n1 - 1 + indA) % n1;
                done = false;
            }
        }
        int lowerA = indA, lowerB = indB;

        List<Point> merged = new ArrayList<>();
        int ind = upperA;
        merged.add(left.get(upperA));
        while (ind != lowerA) {
            ind = (ind + 1) % n1;
            merged.add(left.get(ind));
        }

        ind = lowerB;
        merged.add(right.get(lowerB));
        while (ind != upperB) {
            ind = (ind + 1) % n2;
            merged.add(right.get(ind));
        }
        return merged;
    }

    //Graham's Scan
    public static List<Point> grahamScan(List<Point> points) {
        int n = points.size();
        if (n < 3)
            return new ArrayList<>(points);

        List<Point> pts = new ArrayList<>(points);
        Point minPoint = pts.get(0);
        for (Point p : pts) {
            if (p.y < minPoint.y || (p.y == minPoint.y && p.x < minPoint.x)) {
                minPoint = p;
            }
        }

        final Point pivot = minPoint;
        pts.sort((p1, p2) -> {
            if (p1.equals(pivot))
                return -1;
            if (p2.equals(pivot))
                return 1;
            double cp = crossProduct(pivot, p1, p2);
            if (cp == 0)
                return Double.compare(distanceSq(pivot, p1), distanceSq(pivot, p2));
            return cp > 0 ? -1 : 1;
        });

        List<Point> uniquePts = new ArrayList<>();
        for (int i = 0; i < pts.size(); i++) {
            while (i < pts.size() - 1 && crossProduct(pivot, pts.get(i), pts.get(i + 1)) == 0) {
                i++;
            }
            uniquePts.add(pts.get(i));
        }

        if (uniquePts.size() < 3)
            return uniquePts;

        Stack<Point> stack = new Stack<>();
        stack.push(uniquePts.get(0));
        stack.push(uniquePts.get(1));
        stack.push(uniquePts.get(2));

        for (int i = 3; i < uniquePts.size(); i++) {
            Point top = stack.pop();
            while (!stack.isEmpty() && crossProduct(stack.peek(), top, uniquePts.get(i)) <= 0) {
                top = stack.pop();
            }
            stack.push(top);
            stack.push(uniquePts.get(i));
        }

        return new ArrayList<>(stack);
    }

    //Jarvis' March
    public static List<Point> jarvisMarch(List<Point> points) {
        int n = points.size();
        if (n < 3)
            return new ArrayList<>(points);

        List<Point> hull = new ArrayList<>();
        int l = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).x < points.get(l).x) {
                l = i;
            }
        }

        int p = l, q;
        do {
            hull.add(points.get(p));
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (crossProduct(points.get(p), points.get(i), points.get(q)) > 0) {
                    q = i;
                }
            }
            p = q;
        } while (p != l);

        return hull;
    }

    public static void main(String[] args) {
        List<List<Point>> testSets = new ArrayList<>();

        testSets.add(Arrays.asList(
                new Point(0, 0), new Point(0, 4), new Point(-4, 0),
                new Point(5, 0), new Point(0, -6), new Point(1, 0)));

        testSets.add(Arrays.asList(
                new Point(1, 1), new Point(2, 2), new Point(2, 0),
                new Point(0, 2), new Point(0, 0), new Point(3, 3),
                new Point(0.5, 0.5), new Point(1.5, 1.5)));

        testSets.add(Arrays.asList(
                new Point(0, 3), new Point(2, 2), new Point(1, 1),
                new Point(2, 1), new Point(3, 0), new Point(0, 0),
                new Point(3, 3)));

        for (int i = 0; i < testSets.size(); i++) {
            System.out.println();
            System.out.println("TEST SET " + (i + 1) + ": " + testSets.get(i));
            System.out.println();

            System.out.println("Divide and Conquer Hull:");
            System.out.println(divideAndConquer(testSets.get(i)));
            System.out.println();

            System.out.println("Graham's Scan Hull:");
            System.out.println(grahamScan(testSets.get(i)));
            System.out.println();

            System.out.println("Jarvis' March Hull:");
            System.out.println(jarvisMarch(testSets.get(i)));
            System.out.println();
        }
    }
}