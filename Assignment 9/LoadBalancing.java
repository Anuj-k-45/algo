import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LoadBalancing.java
 * Description: Implements a greedy approximation algorithm to minimize 
 * the maximum load (makespan) across a set of m machines.
 */
public class LoadBalancing {
    public static void main(String[] args) {
        int m = 3; // Number of machines
        int[] jobs = {6, 4, 3, 2, 2, 2}; // Job processing times sequence

        // Track machine loads and assigned jobs
        int[] machineLoads = new int[m];
        @SuppressWarnings("unchecked")
        List<Integer>[] machineJobs = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            machineJobs[i] = new ArrayList<>();
        }

        System.out.println("Number of Machines: " + m);
        System.out.println("Job Size Sequence:  " + Arrays.toString(jobs));
        System.out.println("\n--- Assignment Trace ---");

        // Greedy Approximation Algorithm: Assign each job to the machine with the minimum current load
        for (int i = 0; i < jobs.length; i++) {
            int jobSize = jobs[i];
            
            // Find machine with the minimum load
            int minLoadIndex = 0;
            for (int j = 1; j < m; j++) {
                if (machineLoads[j] < machineLoads[minLoadIndex]) {
                    minLoadIndex = j;
                }
            }

            // Assign job
            machineLoads[minLoadIndex] += jobSize;
            machineJobs[minLoadIndex].add(jobSize);

            System.out.println("Job " + (i + 1) + " (Size: " + jobSize + ") -> Assigned to Machine " + (minLoadIndex + 1) + 
                               " | Updated Loads: " + Arrays.toString(machineLoads));
        }

        // Calculate the maximum load (Makespan)
        int maxLoad = machineLoads[0];
        for (int load : machineLoads) {
            if (load > maxLoad) {
                maxLoad = load;
            }
        }

        // Display Final Results
        for (int i = 0; i < m; i++) {
            System.out.println("Machine " + (i + 1) + " -> Jobs Assigned: " + machineJobs[i] + " | Total Load: " + machineLoads[i]);
        }
        System.out.println("Maximum Load on any Machine (Makespan): " + maxLoad);
    }
}