import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MonotonePolygonTriangulation extends JPanel {

    static class Point {
        double x, y;
        int id;
        boolean isLeftChain;

        Point(int id, double x, double y, boolean isLeftChain) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.isLeftChain = isLeftChain;
        }
    }

    static class Diagonal {
        Point p1, p2;
        Diagonal(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private List<Point> vertices;
    private List<Diagonal> diagonals;

    public MonotonePolygonTriangulation(List<Point> vertices, List<Diagonal> diagonals) {
        this.vertices = vertices;
        this.diagonals = diagonals;
    }

    // Cross product to test orientation (strictly convex turn towards interior)
    private static double crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static List<Diagonal> triangulateMonotone(List<Point> pts) {
        List<Diagonal> diagonals = new ArrayList<>();
        List<Point> sorted = new ArrayList<>(pts);

        // Sort vertices from top (max Y) to bottom (min Y)
        sorted.sort((a, b) -> {
            if (Double.compare(b.y, a.y) != 0) return Double.compare(b.y, a.y);
            return Double.compare(a.x, b.x);
        });

        Deque<Point> stack = new ArrayDeque<>();
        stack.push(sorted.get(0));
        stack.push(sorted.get(1));

        for (int j = 2; j < sorted.size() - 1; j++) {
            Point uj = sorted.get(j);
            Point top = stack.peek();

            if (uj.isLeftChain != top.isLeftChain) {
                // Case 1: uj is on a different chain than the top of the stack
                Point lastPopped = null;
                while (!stack.isEmpty()) {
                    lastPopped = stack.pop();
                    if (!stack.isEmpty()) {
                        diagonals.add(new Diagonal(uj, lastPopped));
                    }
                }
                stack.push(top);
                stack.push(uj);
            } else {
                // Case 2: uj is on the same chain
                Point popped = stack.pop();
                while (!stack.isEmpty()) {
                    Point next = stack.peek();
                    double cp = crossProduct(next, popped, uj);
                    boolean isStrictlyInterior = uj.isLeftChain ? (cp < 0) : (cp > 0);

                    if (isStrictlyInterior) {
                        diagonals.add(new Diagonal(uj, next));
                        popped = stack.pop();
                    } else {
                        break;
                    }
                }
                stack.push(popped);
                stack.push(uj);
            }
        }

        // Process the lowest vertex un
        Point un = sorted.get(sorted.size() - 1);
        if (!stack.isEmpty()) stack.pop(); // Remove bottom-most
        while (stack.size() > 1) {
            Point p = stack.pop();
            diagonals.add(new Diagonal(un, p));
        }

        return diagonals;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Diagonals
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        for (Diagonal d : diagonals) {
            g2.drawLine((int) d.p1.x, (int) d.p1.y, (int) d.p2.x, (int) d.p2.y);
        }

        // Draw Polygon Boundary
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(3));
        for (int i = 0; i < vertices.size(); i++) {
            Point p1 = vertices.get(i);
            Point p2 = vertices.get((i + 1) % vertices.size());
            g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }

        // Draw Vertices
        g2.setColor(Color.BLACK);
        for (Point p : vertices) {
            g2.fillOval((int) p.x - 4, (int) p.y - 4, 8, 8);
            g2.drawString("P" + p.id, (int) p.x + 6, (int) p.y);
        }
    }

    public static void main(String[] args) {
        // Define a sample y-monotone polygon in clockwise order
        List<Point> poly = Arrays.asList(
            new Point(1, 300, 50, true),   // Top-most vertex
            new Point(2, 450, 150, false), // Right chain
            new Point(3, 400, 280, false),
            new Point(4, 480, 420, false),
            new Point(5, 300, 500, false), // Bottom-most vertex
            new Point(6, 180, 360, true),  // Left chain
            new Point(7, 240, 220, true),
            new Point(8, 150, 120, true)
        );

        List<Diagonal> diagonals = triangulateMonotone(poly);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Y-Monotone Polygon Triangulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(650, 600);
            frame.add(new MonotonePolygonTriangulation(poly, diagonals));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}