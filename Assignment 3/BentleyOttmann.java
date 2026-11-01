import java.util.*;

/**
 * Bentley-Ottmann Plane Sweep Algorithm for Line Segment Intersection.
 *
 * Based on: "Computational Geometry: Algorithms and Applications" (de Berg et al.),
 * Chapter 2 - Line Segment Intersection.
 *
 * Given a set S of n closed line segments in the plane, this program reports
 * every intersection point, together with the segments that meet there.
 * Running time: O((n + k) log n), where k is the number of intersection points
 * (output-sensitive / intersection-sensitive algorithm).
 *
 * Author: (Student submission)
 */
public class BentleyOttmann {

    /* ---------------------------------------------------------------- *
     *  Geometry primitives
     * ---------------------------------------------------------------- */

    static final double EPS = 1e-7;

    /** A point in the plane. */
    static class Pt {
        double x, y;
        Pt(double x, double y) { this.x = x; this.y = y; }

        boolean close(Pt o) {
            return Math.abs(x - o.x) < 1e-6 && Math.abs(y - o.y) < 1e-6;
        }

        @Override
        public String toString() {
            return String.format("(%.4f, %.4f)", x, y);
        }
    }

    /**
     * A closed line segment. "upper" is defined as the endpoint that comes
     * first in the sweep order (higher y; if equal y, smaller x). This
     * matches the convention used in the DCEL/plane-sweep lecture notes.
     */
    static class Seg {
        Pt upper, lower;
        int id;             // 1-based index into the original input list, used for reporting

        Seg(Pt a, Pt b, int id) {
            if (a.y > b.y + EPS || (Math.abs(a.y - b.y) < EPS && a.x < b.x - EPS)) {
                upper = a; lower = b;
            } else {
                upper = b; lower = a;
            }
            this.id = id;
        }

        /** x-coordinate of this segment at height y (segment must span y). */
        double xAt(double y) {
            if (Math.abs(upper.y - lower.y) < EPS) return upper.x; // horizontal
            double t = (upper.y - y) / (upper.y - lower.y);
            return upper.x + t * (lower.x - upper.x);
        }

        @Override
        public String toString() { return "s" + id; }
    }

    /** Event point ordering ≺ :  higher y first; ties broken by smaller x first. */
    static Comparator<Pt> EVENT_ORDER = (p, q) -> {
        if (Math.abs(p.y - q.y) > EPS) return Double.compare(q.y, p.y);
        return Double.compare(p.x, q.x);
    };

    /** Data stored with every event point: the point itself and U(p), the
     *  segments whose *upper* endpoint is p. */
    static class EventData {
        Pt pt;
        List<Seg> upper = new ArrayList<>();
        EventData(Pt pt) { this.pt = pt; }
    }

    /* ---------------------------------------------------------------- *
     *  Algorithm state
     * ---------------------------------------------------------------- */

    private double sweepY;                       // current y of the sweep line
    private final List<Seg> status = new ArrayList<>();   // status structure T
    // Event queue Q, keyed by (rounded) coordinates so approximately-equal
    // points (e.g. a computed intersection that coincides with an input
    // endpoint) are correctly merged into a single event.
    private final Map<String, EventData> eventMap = new LinkedHashMap<>();

    private final List<Object[]> results = new ArrayList<>(); // {Pt, List<Seg>}

    private static String keyOf(Pt p) {
        return String.format("%.6f_%.6f", p.x, p.y);
    }

    /* -------------------- Event queue helpers ------------------------ */

    /** Insert an event point into Q if not already present, optionally
     *  registering "seg" as a segment whose upper endpoint is p. */
    private void insertEvent(Pt p, Seg seg) {
        // check for an existing near-equal key (handles floating point drift)
        String exact = keyOf(p);
        EventData ed = eventMap.get(exact);
        if (ed == null) {
            for (EventData cand : eventMap.values()) {
                if (cand.pt.close(p)) { ed = cand; break; }
            }
        }
        if (ed == null) {
            ed = new EventData(p);
            eventMap.put(exact, ed);
        }
        if (seg != null) ed.upper.add(seg);
    }

    /** Pop the next event point (smallest under ≺) from Q. */
    private EventData popNextEvent() {
        EventData best = null;
        String bestKey = null;
        for (Map.Entry<String, EventData> e : eventMap.entrySet()) {
            if (best == null || EVENT_ORDER.compare(e.getValue().pt, best.pt) < 0) {
                best = e.getValue();
                bestKey = e.getKey();
            }
        }
        if (best != null) eventMap.remove(bestKey);
        return best;
    }

    /* -------------------- Geometry helpers ---------------------------- */

    /** True if point p lies on (closed) segment s. */
    private static boolean onSegment(Seg s, Pt p) {
        double minx = Math.min(s.upper.x, s.lower.x), maxx = Math.max(s.upper.x, s.lower.x);
        double miny = Math.min(s.upper.y, s.lower.y), maxy = Math.max(s.upper.y, s.lower.y);
        if (p.x < minx - 1e-6 || p.x > maxx + 1e-6 || p.y < miny - 1e-6 || p.y > maxy + 1e-6)
            return false;
        double dx1 = s.lower.x - s.upper.x, dy1 = s.lower.y - s.upper.y;
        double dx2 = p.x - s.upper.x,      dy2 = p.y - s.upper.y;
        double cross = dx1 * dy2 - dy1 * dx2;
        double len = Math.hypot(dx1, dy1);
        if (len < 1e-9) return p.close(s.upper);
        return Math.abs(cross) / len < 1e-6;
    }

    /** Standard segment-segment intersection (returns null if none / parallel). */
    private static Pt segIntersect(Seg a, Seg b) {
        double x1 = a.upper.x, y1 = a.upper.y, x2 = a.lower.x, y2 = a.lower.y;
        double x3 = b.upper.x, y3 = b.upper.y, x4 = b.lower.x, y4 = b.lower.y;
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(d) < 1e-12) return null; // parallel or collinear (overlap not handled)
        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / d;
        double u = ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / d;
        if (t < -1e-9 || t > 1 + 1e-9 || u < -1e-9 || u > 1 + 1e-9) return null;
        double px = x1 + t * (x2 - x1);
        double py = y1 + t * (y2 - y1);
        return new Pt(px, py);
    }

    /** Ordering key used to sort segments in the status structure at the
     *  current sweepY: primarily by x, then by direction (angle), then id. */
    private Comparator<Seg> statusOrder() {
        return (s1, s2) -> {
            double x1 = s1.xAt(sweepY), x2 = s2.xAt(sweepY);
            if (Math.abs(x1 - x2) > EPS) return Double.compare(x1, x2);
            double a1 = Math.atan2(s1.lower.y - s1.upper.y, s1.lower.x - s1.upper.x);
            double a2 = Math.atan2(s2.lower.y - s2.upper.y, s2.lower.x - s2.upper.x);
            if (Math.abs(a1 - a2) > EPS) return Double.compare(a1, a2);
            return Integer.compare(s1.id, s2.id);
        };
    }

    /* ---------------------------------------------------------------- *
     *  Core algorithm: FindIntersections(S)
     * ---------------------------------------------------------------- */

    List<Object[]> findIntersections(List<Seg> segs) {
        eventMap.clear();
        status.clear();
        results.clear();

        // 1. Initialize event queue with segment endpoints.
        for (Seg s : segs) {
            insertEvent(s.upper, s);   // upper endpoint carries the segment
            insertEvent(s.lower, null); // lower endpoint is a plain event
        }

        // 2. Sweep.
        EventData ed;
        while ((ed = popNextEvent()) != null) {
            handleEventPoint(ed.pt, ed.upper);
        }
        return results;
    }

    /** HandleEventPoint(p) */
    private void handleEventPoint(Pt p, List<Seg> U) {
        sweepY = p.y;

        // L(p): segments in status whose lower endpoint is p.
        // C(p): segments in status that contain p in their interior.
        List<Seg> L = new ArrayList<>();
        List<Seg> C = new ArrayList<>();
        for (Seg s : status) {
            if (onSegment(s, p)) {
                if (s.lower.close(p)) L.add(s);
                else if (!s.upper.close(p)) C.add(s);
            }
        }

        // Report intersection if more than one segment meets at p.
        List<Seg> all = new ArrayList<>();
        for (Seg s : L) if (!all.contains(s)) all.add(s);
        for (Seg s : U) if (!all.contains(s)) all.add(s);
        for (Seg s : C) if (!all.contains(s)) all.add(s);
        if (all.size() > 1) {
            results.add(new Object[]{p, all});
        }

        // Delete L(p) ∪ C(p) from T.
        status.removeAll(L);
        status.removeAll(C);

        // Insert U(p) ∪ C(p) into T, ordered as just below the sweep line.
        List<Seg> toInsert = new ArrayList<>();
        for (Seg s : U) if (!toInsert.contains(s)) toInsert.add(s);
        for (Seg s : C) if (!toInsert.contains(s)) toInsert.add(s);

        if (toInsert.isEmpty()) {
            // No segment continues through p: find the (old) left/right
            // neighbors of p in T and test them for a future crossing.
            sweepY = p.y;
            status.sort(statusOrder());
            int idx = 0;
            while (idx < status.size() && status.get(idx).xAt(sweepY) < p.x - EPS) idx++;
            Seg sl = idx - 1 >= 0 ? status.get(idx - 1) : null;
            Seg sr = idx < status.size() ? status.get(idx) : null;
            if (sl != null && sr != null) findNewEvent(sl, sr, p);
        } else {
            // Determine insertion index using position AT p.
            sweepY = p.y;
            status.sort(statusOrder());
            int idx = 0;
            while (idx < status.size() && status.get(idx).xAt(sweepY) < p.x - EPS) idx++;

            // Order the segments being inserted by their slope just BELOW p.
            sweepY = p.y - EPS;
            toInsert.sort(statusOrder());

            status.addAll(idx, toInsert);
            sweepY = p.y - EPS;
            status.sort(statusOrder());

            Seg leftmost = toInsert.get(0);
            Seg rightmost = toInsert.get(toInsert.size() - 1);
            int li = status.indexOf(leftmost);
            int ri = status.indexOf(rightmost);
            if (li - 1 >= 0) findNewEvent(status.get(li - 1), leftmost, p);
            if (ri + 1 < status.size()) findNewEvent(rightmost, status.get(ri + 1), p);
        }
    }

    /** FindNewEvent(sl, sr, p): register a future intersection of sl, sr if
     *  it lies below the sweep line (or on it, to the right of p). */
    private void findNewEvent(Seg sl, Seg sr, Pt p) {
        if (sl == sr) return;
        Pt ip = segIntersect(sl, sr);
        if (ip == null) return;
        if (ip.y < p.y - EPS || (Math.abs(ip.y - p.y) < EPS && ip.x > p.x + 1e-9)) {
            insertEvent(ip, null);
        }
    }

    /* ---------------------------------------------------------------- *
     *  Driver / test harness
     * ---------------------------------------------------------------- */

    static void runTest(String name, double[][][] raw) {
        List<Seg> segs = new ArrayList<>();
        for (int i = 0; i < raw.length; i++) {
            Pt a = new Pt(raw[i][0][0], raw[i][0][1]);
            Pt b = new Pt(raw[i][1][0], raw[i][1][1]);
            segs.add(new Seg(a, b, i + 1));
        }

        System.out.println("=== " + name + " ===");
        System.out.println("Input segments (" + segs.size() + "):");
        for (Seg s : segs) {
            System.out.printf("  s%d : [%.1f, %.1f] -> [%.1f, %.1f]%n",
                    s.id, raw[s.id - 1][0][0], raw[s.id - 1][0][1],
                    raw[s.id - 1][1][0], raw[s.id - 1][1][1]);
        }

        BentleyOttmann bo = new BentleyOttmann();
        List<Object[]> res = bo.findIntersections(segs);

        // sort results for readable / deterministic output: top-to-bottom, left-to-right
        res.sort((r1, r2) -> EVENT_ORDER.compare((Pt) r1[0], (Pt) r2[0]));

        System.out.println("Number of intersection points found: " + res.size());
        int k = 1;
        for (Object[] r : res) {
            Pt p = (Pt) r[0];
            @SuppressWarnings("unchecked")
            List<Seg> segsHere = (List<Seg>) r[1];
            StringBuilder sb = new StringBuilder();
            for (Seg s : segsHere) sb.append(s).append(" ");
            System.out.printf("  %d. Point %s   segments: { %s}%n", k++, p, sb.toString());
        }
        System.out.println();
    }

    public static void main(String[] args) {

        // -------------------------------
        // Test Case 1 (Assignment Sample)
        // -------------------------------
        double[][][] test1 = {
            { {1, 5}, {4, 5} },
            { {2, 5}, {10, 1} },
            { {3, 2}, {10, 3} },
            { {6, 4}, {9, 4} },
            { {7, 1}, {8, 1} }
        };

        runTest("Test Case 1 (Assignment Sample)", test1);

        // -------------------------------
        // Test Case 2
        // -------------------------------
        double[][][] test2 = {
            { {1, 1}, {6, 6} },
            { {1, 6}, {6, 1} },
            { {3, 0}, {3, 7} }
        };

        runTest("Test Case 2", test2);

        // -------------------------------
        // Test Case 3
        // -------------------------------
        double[][][] test3 = {
            { {0, 0}, {5, 5} },
            { {0, 5}, {5, 0} },
            { {6, 6}, {8, 8} }
        };

        runTest("Test Case 3", test3);
    }
}
