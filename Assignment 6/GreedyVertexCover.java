import java.util.*;

public class GreedyVertexCover {
    static class Edge {
        int u, v;

        Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public static Set<Integer> findGreedyVertexCover(int numVertices, List<Edge> edges) {
        Set<Integer> vertexCover = new TreeSet<>();
        List<Edge> remainingEdges = new ArrayList<>(edges);

        while (!remainingEdges.isEmpty()) {
            // Step 1: Calculate the current degree for all vertices
            int[] degrees = new int[numVertices + 1];
            for (Edge edge : remainingEdges) {
                degrees[edge.u]++;
                degrees[edge.v]++;
            }

            // Step 2: Pick the vertex with the maximum current degree
            int maxVertex = -1;
            int maxDegree = 0;
            for (int i = 1; i <= numVertices; i++) {
                if (degrees[i] > maxDegree) {
                    maxDegree = degrees[i];
                    maxVertex = i;
                }
            }

            if (maxDegree == 0)
                break;

            // Step 3: Add chosen vertex to cover & remove incident edges
            vertexCover.add(maxVertex);
            final int chosen = maxVertex;
            remainingEdges.removeIf(edge -> edge.u == chosen || edge.v == chosen);
        }

        return vertexCover;
    }

    public static void main(String[] args) {
        int numVertices = 7;
        List<Edge> edges = new ArrayList<>();

        // Graph edges as given in the assignment[cite: 1]
        edges.add(new Edge(1, 2));
        edges.add(new Edge(1, 4));
        edges.add(new Edge(2, 3));
        edges.add(new Edge(2, 5));
        edges.add(new Edge(3, 5));
        edges.add(new Edge(3, 6));
        edges.add(new Edge(3, 7));
        edges.add(new Edge(5, 6));

        Set<Integer> cover = findGreedyVertexCover(numVertices, edges);

        System.out.println("Greedy Approximation Vertex Cover Result:");
        System.out.println("Cover Set : " + cover);
        System.out.println("Cover Size: " + cover.size());
    }
}