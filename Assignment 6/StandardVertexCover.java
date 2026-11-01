import java.util.*;

public class StandardVertexCover {
    static class Edge {
        int u, v;

        Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    public static Set<Integer> findVertexCover(int numVertices, List<Edge> edges) {
        Set<Integer> vertexCover = new TreeSet<>();
        boolean[] visited = new boolean[numVertices + 1];

        // Iterate through all edges
        for (Edge edge : edges) {
            int u = edge.u;
            int v = edge.v;

            // If neither endpoint is already picked, include both endpoints
            if (!visited[u] && !visited[v]) {
                visited[u] = true;
                visited[v] = true;
                vertexCover.add(u);
                vertexCover.add(v);
            }
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

        Set<Integer> cover = findVertexCover(numVertices, edges);

        System.out.println("Standard 2-Approximation Vertex Cover Result:");
        System.out.println("Cover Set : " + cover);
        System.out.println("Cover Size: " + cover.size());
    }
}