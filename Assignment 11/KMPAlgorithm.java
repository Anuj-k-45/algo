import java.util.ArrayList;
import java.util.List;

public class KMPAlgorithm {

    private static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || n < m) {
            return matches;
        }

        int[] lps = computeLPSArray(pattern);
        int i = 0;
        int j = 0;

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    public static void main(String[] args) {
        String text = "DAACABCDBA";
        String pattern = "CAB";

        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);
        System.out.println("----------------------------------------");

        List<Integer> occurrences = search(text, pattern);

        if (occurrences.isEmpty()) {
            System.out.println("Pattern not found in the text.");
        } else {
            System.out.println("Pattern found at starting indices:");
            for (int index : occurrences) {
                System.out.println("- Index " + index + " (Substring: "
                        + text.substring(index, index + pattern.length()) + ")");
            }
        }
    }
}