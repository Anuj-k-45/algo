import java.util.*;

class GraphEdge {
    int target;
    int weight;

    public GraphEdge(int target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}

public class TSPEulerian {
    public static void main(String[] args) {
        int V = 6;
        List<List<GraphEdge>> adj = new ArrayList<>();
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

        // Step 1: Find MST using Prim's
        List<List<GraphEdge>> mst = runPrims(V, adj, startVertex);

        // Step 2: Form Eulerian Multigraph by duplicating MST edges
        Map<Integer, List<Integer>> eulerianGraph = buildEulerianMultigraph(mst, V);

        // Step 3: Find Eulerian Tour using Hierholzer's Algorithm
        List<Integer> eulerTour = findEulerianTour(eulerianGraph, startVertex);

        // Step 4: Shortcut Eulerian Tour to form Hamiltonian Cycle (TSP Tour)
        List<Integer> tspTour = shortcutTour(eulerTour, startVertex);

        // Step 5: Calculate total cost
        int totalCost = calculateCost(adj, tspTour);

        System.out.println("=== TSP Approximation using Eulerian Tour & Shortcutting ===");
        System.out.println("Eulerian Tour: " + eulerTour);
        System.out.println("Final TSP Tour (Hamiltonian Cycle): " + tspTour);
        System.out.println("Total Tour Cost: " + totalCost);
    }

    private static void addEdge(List<List<GraphEdge>> adj, int u, int v, int w) {
        adj.get(u).add(new GraphEdge(v, w));
        adj.get(v).add(new GraphEdge(u, w));
    }

    private static List<List<GraphEdge>> runPrims(int V, List<List<GraphEdge>> adj, int start) {
        boolean[] inMST = new boolean[V + 1];
        int[] parent = new int[V + 1];
        int[] key = new int[V + 1];
        Arrays.fill(key, Integer.MAX_VALUE);

        PriorityQueue<GraphEdge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        key[start] = 0;
        pq.add(new GraphEdge(start, 0));
        parent[start] = -1;

        while (!pq.isEmpty()) {
            int u = pq.poll().target;
            if (inMST[u])
                continue;
            inMST[u] = true;

            for (GraphEdge edge : adj.get(u)) {
                int v = edge.target;
                int weight = edge.weight;
                if (!inMST[v] && weight < key[v]) {
                    key[v] = weight;
                    parent[v] = u;
                    pq.add(new GraphEdge(v, key[v]));
                }
            }
        }

        List<List<GraphEdge>> mst = new ArrayList<>();
        for (int i = 0; i <= V; i++)
            mst.add(new ArrayList<>());
        for (int i = 2; i <= V; i++) {
            int p = parent[i];
            int w = key[i];
            mst.get(p).add(new GraphEdge(i, w));
            mst.get(i).add(new GraphEdge(p, w));
        }
        return mst;
    }

    private static Map<Integer, List<Integer>> buildEulerianMultigraph(List<List<GraphEdge>> mst, int V) {
        Map<Integer, List<Integer>> multigraph = HashMap.newHashMap(V + 1);
        for (int i = 1; i <= V; i++) {
            multigraph.put(i, new ArrayList<>());
        }
        for (int u = 1; u <= V; u++) {
            for (GraphEdge edge : mst.get(u)) {
                multigraph.get(u).add(edge.target);
            }
        }
        return multigraph;
    }

    private static List<Integer> findEulerianTour(Map<Integer, List<Integer>> graph, int start) {
        Map<Integer, List<Integer>> adjCopy = new HashMap<>();
        for (var entry : graph.entrySet()) {
            adjCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        List<Integer> tour = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int curr = stack.peek();
            if (adjCopy.get(curr) != null && !adjCopy.get(curr).isEmpty()) {
                int next = adjCopy.get(curr).remove(0);
                // Remove reverse edge for undirected multigraph representation
                adjCopy.get(next).remove(Integer.valueOf(curr));
                stack.push(next);
            } else {
                tour.add(stack.pop());
            }
        }
        Collections.reverse(tour);
        return tour;
    }

    private static List<Integer> shortcutTour(List<Integer> eulerTour, int start) {
        List<Integer> hamiltonian = new ArrayList<>();
        boolean[] visited = new boolean[10]; // assuming max 10 vertices
        for (int v : eulerTour) {
            if (!visited[v]) {
                visited[v] = true;
                hamiltonian.add(v);
            }
        }
        hamiltonian.add(start); // Complete the cycle
        return hamiltonian;
    }

    private static int calculateCost(List<List<GraphEdge>> adj, List<Integer> tour) {
        int cost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int u = tour.get(i);
            int v = tour.get(i + 1);
            for (GraphEdge e : adj.get(u)) {
                if (e.target == v) {
                    cost += e.weight;
                    break;
                }
            }
        }
        return cost;
    }
}