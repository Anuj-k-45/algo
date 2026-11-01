import java.util.*;


public class Q2_MaximalCliques {

    static int totalMaximalCliques = 0;

    static final int V = 9;
    static boolean[][] graph = new boolean[V + 1][V + 1];

    // Add an undirected edge
    static void addEdge(int u, int v) {
        graph[u][v] = true;
        graph[v][u] = true;
    }

    // Return neighbors of a vertex
    static Set<Integer> getNeighbors(int v) {
        Set<Integer> neighbors = new HashSet<>();

        for (int i = 1; i <= V; i++) {
            if (graph[v][i]) {
                neighbors.add(i);
            }
        }

        return neighbors;
    }

    // Bron–Kerbosch algorithm
    static void bronKerbosch(Set<Integer> R,
                             Set<Integer> P,
                             Set<Integer> X) {

        // Found a maximal clique
        if (P.isEmpty() && X.isEmpty()) {
            totalMaximalCliques++;
            System.out.println(R);
            return;
        }

        // Copy P because we'll modify it
        Set<Integer> pCopy = new HashSet<>(P);

        for (int v : pCopy) {

            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);

            Set<Integer> newP = new HashSet<>(P);
            newP.retainAll(getNeighbors(v));

            Set<Integer> newX = new HashSet<>(X);
            newX.retainAll(getNeighbors(v));

            bronKerbosch(newR, newP, newX);

            P.remove(v);
            X.add(v);
        }
    }

    public static void main(String[] args) {

        addEdge(7, 9);
        addEdge(7, 5);
        addEdge(7, 8);
        addEdge(7, 6);

        addEdge(5, 6);
        addEdge(5, 8);
        addEdge(5, 4);

        addEdge(8, 6);

        addEdge(6, 4);

        addEdge(4, 1);
        addEdge(4, 3);

        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(1, 3);

        Set<Integer> R = new HashSet<>();
        Set<Integer> P = new HashSet<>();
        Set<Integer> X = new HashSet<>();

        for (int i = 1; i <= V; i++)
            P.add(i);

        System.out.println("Maximal Cliques:");

        bronKerbosch(R, P, X);

        System.out.println("Total Maximal Cliques: " + totalMaximalCliques);
    }
}