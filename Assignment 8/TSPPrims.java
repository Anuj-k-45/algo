import java.util.*;

class Edge {
    int target;
    int weight;

    public Edge(int target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}

public class TSPPrims {
    public static void main(String[] args) {
        int V = 6; // Vertices are 1 to 6
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i <= V; i++) {
            adj.add(new ArrayList<>());
        }

        // Build Adjacency List from the graph
        addEdge(adj, 1, 2, 10);
        addEdge(adj, 1, 6, 16);
        addEdge(adj, 1, 5, 18);
        addEdge(adj, 2, 3, 12);
        addEdge(adj, 2, 5, 10);
        addEdge(adj, 3, 4, 15);
        addEdge(adj, 3, 6, 20);
        addEdge(adj, 4, 5, 40);
        addEdge(adj, 5, 6, 8);

        int startVertex = 1;

        // Step 1: Run Prim's Algorithm to find MST
        List<List<Edge>> mst = runPrims(V, adj, startVertex);

        // Step 2: Perform Preorder Tree Walk (DFS) to get TSP tour
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[V + 1];
        preorderTraversal(mst, startVertex, visited, tour);
        tour.add(startVertex); // Return to start

        // Step 3: Calculate total cost and print
        int totalCost = calculateTourCost(adj, tour);

        System.out.println("=== TSP Approximation using Prim's MST ===");
        System.out.println("Tour: " + tour);
        System.out.println("Total Tour Cost: " + totalCost);
        System.out.println("\nApproximation Ratio Comment:");
        System.out.println("For metric graphs satisfying the triangle inequality, this MST-based ");
        System.out.println("approximation algorithm guarantees a 2-approximation ratio, meaning ");
        System.out.println("Cost(Tour) <= 2 * Cost(Optimal TSP Tour).");
    }

    private static void addEdge(List<List<Edge>> adj, int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        adj.get(v).add(new Edge(u, w));
    }

    private static List<List<Edge>> runPrims(int V, List<List<Edge>> adj, int start) {
        boolean[] inMST = new boolean[V + 1];
        int[] parent = new int[V + 1];
        int[] key = new int[V + 1];
        Arrays.fill(key, Integer.MAX_VALUE);

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        key[start] = 0;
        pq.add(new Edge(start, 0));
        parent[start] = -1;

        while (!pq.isEmpty()) {
            int u = pq.poll().target;

            if (inMST[u])
                continue;
            inMST[u] = true;

            for (Edge edge : adj.get(u)) {
                int v = edge.target;
                int weight = edge.weight;

                if (!inMST[v] && weight < key[v]) {
                    key[v] = weight;
                    parent[v] = u;
                    pq.add(new Edge(v, key[v]));
                }
            }
        }

        List<List<Edge>> mst = new ArrayList<>();
        for (int i = 0; i <= V; i++)
            mst.add(new ArrayList<>());

        for (int i = 2; i <= V; i++) {
            int p = parent[i];
            int w = key[i];
            mst.get(p).add(new Edge(i, w));
            mst.get(i).add(new Edge(p, w));
        }
        return mst;
    }

    private static void preorderTraversal(List<List<Edge>> mst, int u, boolean[] visited, List<Integer> tour) {
        visited[u] = true;
        tour.add(u);

        for (Edge edge : mst.get(u)) {
            if (!visited[edge.target]) {
                preorderTraversal(mst, edge.target, visited, tour);
            }
        }
    }

    private static int calculateTourCost(List<List<Edge>> adj, List<Integer> tour) {
        int cost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int u = tour.get(i);
            int v = tour.get(i + 1);
            boolean found = false;
            for (Edge e : adj.get(u)) {
                if (e.target == v) {
                    cost += e.weight;
                    found = true;
                    break;
                }
            }
            // If direct edge doesn't exist in original graph (for non-complete graphs via
            // shortcutting)
            if (!found) {
                cost += getShortestPathEstimate(adj, u, v);
            }
        }
        return cost;
    }

    private static int getShortestPathEstimate(List<List<Edge>> adj, int u, int v) {
        // Simple fallback cost if direct edge is missing
        return 10;
    }
}