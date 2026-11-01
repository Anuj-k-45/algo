import java.util.*;

/*
 * Sweep Line Algorithm:
 * Status Structure - ArrayList
 * List re-sorted when - sweep line reaches a new event point
 * 
 */

public class SweepLineIntersection {

    // ---------------------------------------------------------
    // Current y-coordinate of the sweep line.
    // This value changes whenever we process a new event.
    // ---------------------------------------------------------

    static double sweepY;

    static final double EPS = 1e-9;

    // ---------------------------------------------------------
    // POINT
    // ---------------------------------------------------------

    static class Point {

        double x;
        double y;

        Point(double x, double y) {

            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {

            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    // ---------------------------------------------------------
    // LINE SEGMENT
    // ---------------------------------------------------------

    static class Segment {

        int id;

        Point start;
        Point end;

        Segment(int id, Point p1, Point p2) {

            this.id = id;

            /*
             * We always keep the upper endpoint as "start".
             *
             * If y values are equal, the left point becomes
             * the start point.
             */

            if (p1.y > p2.y) {

                start = p1;
                end = p2;

            } else if (p2.y > p1.y) {

                start = p2;
                end = p1;

            } else {

                if (p1.x < p2.x) {

                    start = p1;
                    end = p2;

                } else {

                    start = p2;
                    end = p1;
                }
            }
        }

        /*
         * Returns the x-coordinate where this segment
         * intersects the current sweep line.
         *
         * Used for ordering segments inside
         * the status structure.
         */

        double getXatSweepLine() {

            // Vertical segment

            if (Math.abs(start.x - end.x) < EPS)
                return start.x;

            // Horizontal segment

            if (Math.abs(start.y - end.y) < EPS)
                return Math.min(start.x, end.x);

            double slope =
                    (end.x - start.x) /
                    (end.y - start.y);

            return start.x +
                    slope * (sweepY - start.y);
        }

        @Override
        public String toString() {

            return "S" + id;
        }
    }

    // ---------------------------------------------------------
    // EVENT
    // ---------------------------------------------------------

    static class Event implements Comparable<Event> {

        Point point;

        Segment segment;

        boolean isStart;

        Event(Point point,
              Segment segment,
              boolean isStart) {

            this.point = point;
            this.segment = segment;
            this.isStart = isStart;
        }

        /*
         * Events are processed
         *
         * Highest Y first
         *
         * If Y is same
         * Smaller X first
         */

        @Override
        public int compareTo(Event other) {

            if (Math.abs(point.y - other.point.y) > EPS)
                return Double.compare(other.point.y, point.y);

            return Double.compare(point.x, other.point.x);
        }
    }

    // EVENT QUEUE
    static PriorityQueue<Event> eventQueue = new PriorityQueue<>();


    // STATUS STRUCTURE
    static ArrayList<Segment> status = new ArrayList<>();


    // ALL INTERSECTION POINTS
    static ArrayList<Point> intersections = new ArrayList<>();

  
    /* ORIENTATION
    * 0 -> Collinear
    * 1 -> Clockwise
    * 2 -> Counter Clockwise
    */

    static int orientation(Point p, Point q, Point r) {

        double value =
                (q.y - p.y) * (r.x - q.x)
              - (q.x - p.x) * (r.y - q.y);

        if (Math.abs(value) < EPS)
            return 0;

        return value > 0 ? 1 : 2;
    }


    // CHECK WHETHER A POINT LIES ON A LINE SEGMENT
    static boolean onSegment(Point p, Point q, Point r) {

        return q.x <= Math.max(p.x, r.x) + EPS &&
               q.x >= Math.min(p.x, r.x) - EPS &&
               q.y <= Math.max(p.y, r.y) + EPS &&
               q.y >= Math.min(p.y, r.y) - EPS;
    }


    // CHECK WHETHER TWO SEGMENTS INTERSECT
    static boolean doIntersect(Segment s1, Segment s2) {

        Point p1 = s1.start;
        Point q1 = s1.end;

        Point p2 = s2.start;
        Point q2 = s2.end;

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);

        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 &&
            o3 != o4)
            return true;

        if (o1 == 0 && onSegment(p1, p2, q1))
            return true;

        if (o2 == 0 && onSegment(p1, q2, q1))
            return true;

        if (o3 == 0 && onSegment(p2, p1, q2))
            return true;

        if (o4 == 0 && onSegment(p2, q1, q2))
            return true;

        return false;
    }

    // ---------------------------------------------------------
    // FIND THE EXACT INTERSECTION POINT
    // OF TWO LINE SEGMENTS
    // ---------------------------------------------------------

    static Point intersectionPoint(Segment s1, Segment s2) {

        double x1 = s1.start.x;
        double y1 = s1.start.y;

        double x2 = s1.end.x;
        double y2 = s1.end.y;

        double x3 = s2.start.x;
        double y3 = s2.start.y;

        double x4 = s2.end.x;
        double y4 = s2.end.y;

        double denominator =
                (x1 - x2) * (y3 - y4)
              - (y1 - y2) * (x3 - x4);

        // Parallel lines

        if (Math.abs(denominator) < EPS)
            return null;

        double px =
                ((x1 * y2 - y1 * x2) * (x3 - x4)
                - (x1 - x2) * (x3 * y4 - y3 * x4))
                / denominator;

        double py =
                ((x1 * y2 - y1 * x2) * (y3 - y4)
                - (y1 - y2) * (x3 * y4 - y3 * x4))
                / denominator;

        return new Point(px, py);
    }

    // ---------------------------------------------------------
    // SORT THE STATUS STRUCTURE
    //
    // Segments are ordered according to
    // where they intersect the sweep line.
    // ---------------------------------------------------------

    static void sortStatus() {

        Collections.sort(status, new Comparator<Segment>() {

            @Override
            public int compare(Segment s1,
                               Segment s2) {

                return Double.compare(
                        s1.getXatSweepLine(),
                        s2.getXatSweepLine());
            }

        });
    }

    // ---------------------------------------------------------
    // ADD A NEW INTERSECTION EVENT
    // IF IT HAS NOT ALREADY BEEN FOUND
    // ---------------------------------------------------------

    static void findNewEvent(Segment s1,
                             Segment s2) {

        if (s1 == null || s2 == null)
            return;

        if (!doIntersect(s1, s2))
            return;

        Point intersection =
                intersectionPoint(s1, s2);

        if (intersection == null)
            return;

        // Only future events are added.

        if (intersection.y > sweepY + EPS)
            return;

        // Avoid duplicates

        for (Point p : intersections) {

            if (Math.abs(p.x - intersection.x) < EPS &&
                Math.abs(p.y - intersection.y) < EPS)
                return;
        }

        intersections.add(intersection);

        eventQueue.add(
                new Event(
                        intersection,
                        null,
                        false));
    }

    // ---------------------------------------------------------
    // HANDLE ONE EVENT POINT
    //
    // Similar to the HANDLEEVENTPOINT()
    // procedure shown in the PPT.
    // ---------------------------------------------------------

    static void handleEventPoint(Event event) {

        sweepY = event.point.y;

        // -------------------------------------------------
        // START POINT
        // -------------------------------------------------

        if (event.segment != null &&
                event.isStart) {

            status.add(event.segment);

            sortStatus();

            int index =
                    status.indexOf(event.segment);

            // Check left neighbour

            if (index > 0) {

                findNewEvent(
                        status.get(index - 1),
                        event.segment);
            }

            // Check right neighbour

            if (index < status.size() - 1) {

                findNewEvent(
                        event.segment,
                        status.get(index + 1));
            }

            return;
        }

        // -------------------------------------------------
        // END POINT
        // -------------------------------------------------

        if (event.segment != null &&
                !event.isStart) {

            int index =
                    status.indexOf(event.segment);

            Segment left = null;
            Segment right = null;

            if (index > 0)
                left = status.get(index - 1);

            if (index < status.size() - 1)
                right = status.get(index + 1);

            status.remove(event.segment);

            findNewEvent(left, right);

            return;
        }

        // -------------------------------------------------
        // INTERSECTION EVENT
        // -------------------------------------------------

        sortStatus();
    }

    // ---------------------------------------------------------
    // MAIN SWEEP LINE ALGORITHM
    // ---------------------------------------------------------

    static void sweepLine(ArrayList<Segment> segments) {

        eventQueue.clear();

        status.clear();

        intersections.clear();

        // Insert every segment endpoint
        // into the event queue.

        for (Segment segment : segments) {

            eventQueue.add(
                    new Event(
                            segment.start,
                            segment,
                            true));

            eventQueue.add(
                    new Event(
                            segment.end,
                            segment,
                            false));
        }

        // Process events one by one.

        while (!eventQueue.isEmpty()) {

            Event currentEvent =
                    eventQueue.poll();

            handleEventPoint(currentEvent);
        }

        System.out.println(
                "\n--------------------------------");

        System.out.println(
                "Intersection Points");

        System.out.println(
                "--------------------------------");

        for (Point point : intersections) {

            System.out.println(point);
        }

        System.out.println(
                "\nTotal Intersections = "
                        + intersections.size());
    }
    
        // ---------------------------------------------------------
    // MAIN METHOD
    // ---------------------------------------------------------

    public static void main(String[] args) {

        ArrayList<Segment> segments = new ArrayList<>();

        /*
         * --------------------------------------------------
         * TEST CASE 1
         * (Given in Assignment)
         * --------------------------------------------------
         */

        segments.add(new Segment(
                1,
                new Point(1,5),
                new Point(4,5)));

        segments.add(new Segment(
                2,
                new Point(2,5),
                new Point(10,1)));

        segments.add(new Segment(
                3,
                new Point(3,2),
                new Point(10,3)));

        segments.add(new Segment(
                4,
                new Point(6,4),
                new Point(9,4)));

        segments.add(new Segment(
                5,
                new Point(7,1),
                new Point(8,1)));

        System.out.println("========== TEST CASE 1 ==========");

        sweepLine(segments);

        /*
         * --------------------------------------------------
         * TEST CASE 2
         * --------------------------------------------------
         */

        segments.clear();

        segments.add(new Segment(
                1,
                new Point(1,1),
                new Point(6,6)));

        segments.add(new Segment(
                2,
                new Point(1,6),
                new Point(6,1)));

        segments.add(new Segment(
                3,
                new Point(3,0),
                new Point(3,7)));

        System.out.println("\n========== TEST CASE 2 ==========");

        sweepLine(segments);

        /*
         * --------------------------------------------------
         * TEST CASE 3
         * --------------------------------------------------
         */

        segments.clear();

        segments.add(new Segment(
                1,
                new Point(0,0),
                new Point(5,5)));

        segments.add(new Segment(
                2,
                new Point(0,5),
                new Point(5,0)));

        segments.add(new Segment(
                3,
                new Point(6,6),
                new Point(8,8)));

        System.out.println("\n========== TEST CASE 3 ==========");

        sweepLine(segments);

    }

}