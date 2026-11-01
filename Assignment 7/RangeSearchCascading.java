import java.util.*;

public class RangeSearchCascading {

    static class AugmentedEntry {
        int val;
        int ptrA1; // Index of the smallest element in A1 >= val
        int ptrA2; // Index of the smallest element in A2 >= val

        AugmentedEntry(int val, int ptrA1, int ptrA2) {
            this.val = val;
            this.ptrA1 = ptrA1;
            this.ptrA2 = ptrA2;
        }
    }

    public static AugmentedEntry[] buildCascadingStructure(int[] A, int[] A1, int[] A2) {
        AugmentedEntry[] augmentedA = new AugmentedEntry[A.length];
        int p1 = 0, p2 = 0;

        for (int i = 0; i < A.length; i++) {
            while (p1 < A1.length && A1[p1] < A[i])
                p1++;
            while (p2 < A2.length && A2[p2] < A[i])
                p2++;
            augmentedA[i] = new AugmentedEntry(A[i], p1, p2);
        }
        return augmentedA;
    }

    // Binary search in A for the first index with val >= target in O(log n)
    public static int lowerBound(AugmentedEntry[] A, int target) {
        int low = 0, high = A.length - 1;
        int ans = A.length;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (A[mid].val >= target) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    public static void rangeQuery(AugmentedEntry[] augA, int[] A, int[] A1, int[] A2, int low, int high) {
        // Step 1: O(log n) search on main array A
        int startIdxA = lowerBound(augA, low);

        // Step 2: Retrieve O(1) entry pointers for A1 and A2
        int startIdxA1 = (startIdxA < augA.length) ? augA[startIdxA].ptrA1 : A1.length;
        int startIdxA2 = (startIdxA < augA.length) ? augA[startIdxA].ptrA2 : A2.length;

        // Step 3: Scan and report in O(k)
        List<Integer> resA = new ArrayList<>();
        for (int i = startIdxA; i < A.length && A[i] <= high; i++) {
            resA.add(A[i]);
        }

        List<Integer> resA1 = new ArrayList<>();
        for (int i = startIdxA1; i < A1.length && A1[i] <= high; i++) {
            resA1.add(A1[i]);
        }

        List<Integer> resA2 = new ArrayList<>();
        for (int i = startIdxA2; i < A2.length && A2[i] <= high; i++) {
            resA2.add(A2[i]);
        }

        System.out.println("Query Range: [" + low + ", " + high + "]");
        System.out.println("Results in A  : " + resA);
        System.out.println("Results in A1 : " + resA1);
        System.out.println("Results in A2 : " + resA2);
        System.out.println("Total points reported (k): " + (resA.size() + resA1.size() + resA2.size()));
    }

    public static void main(String[] args) {
        int[] A1 = { 10, 19, 30, 62, 80 };
        int[] A2 = { 3, 23, 37, 62, 90 };
        int[] A = { 3, 10, 19, 23, 30, 37, 59, 62, 80, 90 };

        AugmentedEntry[] augA = buildCascadingStructure(A, A1, A2);

        int queryLow = 15;
        int queryHigh = 40;

        rangeQuery(augA, A, A1, A2, queryLow, queryHigh);
    }
}