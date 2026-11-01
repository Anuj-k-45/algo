import java.util.*;

public class Q2_CliqueCounter {

    static final int V = 9;          // Number of vertices
    static boolean[][] graph = new boolean[V + 1][V + 1];
    static boolean[] visited = new boolean[V + 1];

    static ArrayList<Integer> currentClique = new ArrayList<>();

    static int cliqueCount = 0;

    static void addEdge(int u, int v) {
        graph[u][v] = true;
        graph[v][u] = true;
    }

    static void dfs(int v) {

        visited[v] = true;

        System.out.print(v + " ");

        for (int i = 1; i <= V; i++) {

            if (graph[v][i] && !visited[i]) {

                dfs(i);

            }
        }
    }

    static boolean isClique(ArrayList<Integer> subset) {

        if (subset.isEmpty())
            return false;

        for (int i = 0; i < subset.size(); i++) {

            for (int j = i + 1; j < subset.size(); j++) {

                int u = subset.get(i);
                int v = subset.get(j);

                if (!graph[u][v])
                    return false;
            }
        }

        return true;
    }

    static void generateSubsets(int vertex) {

        if (vertex > V) {

            if (isClique(currentClique)) {

                cliqueCount++;

                System.out.println(currentClique);

            }

            return;
        }

        // Exclude current vertex
        generateSubsets(vertex + 1);

        // Include current vertex
        currentClique.add(vertex);

        generateSubsets(vertex + 1);

        currentClique.remove(currentClique.size() - 1);
    }

    public static void main(String[] args) {

        // ---------- Graph from assignment ----------

        addEdge(7, 9);
        addEdge(7, 5);
        addEdge(7, 8);
        addEdge(7, 6);

        addEdge(5, 8);
        addEdge(5, 6);
        addEdge(5, 4);

        addEdge(8, 6);

        addEdge(6, 4);

        addEdge(4, 3);
        addEdge(4, 1);

        addEdge(3, 2);
        addEdge(3, 1);

        addEdge(2, 1);

        // ---------- DFS ----------

        System.out.println("DFS Traversal:");

        dfs(7);

        // Graph has two connected components
        for (int i = 1; i <= V; i++) {

            if (!visited[i])

                dfs(i);
        }

        System.out.println();

        // ---------- Count Cliques ----------

        System.out.println("\nCliques:");

        generateSubsets(1);

        System.out.println("\nTotal Cliques = " + cliqueCount);

    }
}