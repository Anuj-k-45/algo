import java.util.*;

/**
 * MatrixSetCover.java
 * Description: Implements the greedy approximation algorithm for the Set Cover
 * problem to cover all non-zero entries in a 2D integer matrix using
 * rows/columns as sets.
 */
public class MatrixSetCover {

    // Helper class to represent a matrix coordinate (Row, Col)
    static class Point {
        int r, c;

        Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Point))
                return false;
            Point p = (Point) o;
            return r == p.r && c == p.c;
        }

        @Override
        public int hashCode() {
            return Objects.hash(r, c);
        }

        @Override
        public String toString() {
            return "(" + r + "," + c + ")";
        }
    }

    // Helper class to represent a candidate subset (Row or Column)
    static class Subset {
        String name;
        Set<Point> coveredPoints;

        Subset(String name, Set<Point> coveredPoints) {
            this.name = name;
            this.coveredPoints = coveredPoints;
        }
    }

    public static void main(String[] args) {
        // Sample input matrix
        int[][] matrix = {
                { 0, 5, 0, 2, 4, 0, 6, 1 },
                { 8, 0, 5, 3, 0, 2, 1, 0 },
                { 0, 6, 0, 5, 3, 4, 0, 8 },
                { 0, 1, 5, 0, 9, 0, 0, 3 }
        };

        int rows = matrix.length;
        int cols = matrix[0].length;

        System.out.println("Input Matrix:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }

        // Step 1: Define the Universe (U) - all coordinates containing non-zero entries
        Set<Point> universe = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    universe.add(new Point(i, j));
                }
            }
        }

        System.out.println("\nTotal non-zero elements to cover (Universe Size): " + universe.size());

        // Step 2: Define candidate subsets (Treating each Row and Column as a set)
        List<Subset> availableSubsets = new ArrayList<>();

        // Add Row subsets
        for (int i = 0; i < rows; i++) {
            Set<Point> rowPoints = new HashSet<>();
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    rowPoints.add(new Point(i, j));
                }
            }
            if (!rowPoints.isEmpty()) {
                availableSubsets.add(new Subset("Row " + i, rowPoints));
            }
        }

        // Add Column subsets
        for (int j = 0; j < cols; j++) {
            Set<Point> colPoints = new HashSet<>();
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] != 0) {
                    colPoints.add(new Point(i, j));
                }
            }
            if (!colPoints.isEmpty()) {
                availableSubsets.add(new Subset("Col " + j, colPoints));
            }
        }

        // Step 3: Greedy Set Cover Approximation Algorithm
        Set<Point> uncovered = new HashSet<>(universe);
        List<Subset> selectedSubsets = new ArrayList<>();

        System.out.println("\n--- Greedy Selection Iterations ---");
        int step = 1;
        while (!uncovered.isEmpty()) {
            Subset bestSubset = null;
            int maxCoveredCount = -1;

            // Greedily pick the subset that covers the maximum number of uncovered elements
            for (Subset subset : availableSubsets) {
                Set<Point> intersection = new HashSet<>(subset.coveredPoints);
                intersection.retainAll(uncovered);

                if (intersection.size() > maxCoveredCount) {
                    maxCoveredCount = intersection.size();
                    bestSubset = subset;
                }
            }

            if (bestSubset == null || maxCoveredCount == 0) {
                break; // Safety break
            }

            selectedSubsets.add(bestSubset);
            uncovered.removeAll(bestSubset.coveredPoints);

            System.out.println("Step " + step++ + ": Selected [" + bestSubset.name +
                    "] | Covers new elements: " + maxCoveredCount +
                    " | Remaining Uncovered: " + uncovered.size());
        }

        // Step 4: Display Final Results

        System.out.println("Minimum number of sets required: " + selectedSubsets.size());
        System.out.print("Selected Sets: ");
        for (int i = 0; i < selectedSubsets.size(); i++) {
            System.out.print(selectedSubsets.get(i).name + (i < selectedSubsets.size() - 1 ? ", " : ""));
        }
    }
}