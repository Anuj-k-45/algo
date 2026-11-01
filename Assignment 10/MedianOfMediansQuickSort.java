import java.util.Random;

public class MedianOfMediansQuickSort {

    public static void main(String[] args) {
        int n = 100;
        int[] arr = new int[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(500) + 1;
        }

        System.out.println("Original Array (100 elements):");
        printArray(arr);

        quickSort(arr, 0, arr.length - 1);

        System.out.println("\nSorted Array:");
        printArray(arr);

        boolean isSorted = true;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                isSorted = false;
                break;
            }
        }
        System.out.println("\nVerification (Is sorted): " + isSorted);
    }

    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int n = high - low + 1;
            int median = select(arr, low, high, (n + 1) / 2);
            int pIndex = partitionAroundPivot(arr, low, high, median);

            quickSort(arr, low, pIndex - 1);
            quickSort(arr, pIndex + 1, high);
        }
    }

    static int select(int[] arr, int low, int high, int k) {
        if (low == high) {
            return arr[low];
        }

        int n = high - low + 1;
        int numGroups = (n + 4) / 5;
        int[] medians = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int subLow = low + i * 5;
            int subHigh = Math.min(subLow + 4, high);
            insertionSort(arr, subLow, subHigh);
            int mid = subLow + (subHigh - subLow) / 2;
            medians[i] = arr[mid];
        }

        int medOfMed = (numGroups == 1) ? medians[0] : select(medians, 0, numGroups - 1, (numGroups + 1) / 2);

        int pivotIndex = partitionAroundPivot(arr, low, high, medOfMed);
        int rank = pivotIndex - low + 1;

        if (k == rank) {
            return arr[pivotIndex];
        } else if (k < rank) {
            return select(arr, low, pivotIndex - 1, k);
        } else {
            return select(arr, pivotIndex + 1, high, k - rank);
        }
    }

    static int partitionAroundPivot(int[] arr, int low, int high, int pivotValue) {
        int pivotIndex = low;
        for (int i = low; i <= high; i++) {
            if (arr[i] == pivotValue) {
                pivotIndex = i;
                break;
            }
        }
        swap(arr, pivotIndex, high);

        int i = low;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivotValue) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, high);
        return i;
    }

    static void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%4d", arr[i]);
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
    }
}