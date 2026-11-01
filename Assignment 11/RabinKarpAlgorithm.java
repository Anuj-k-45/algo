import java.util.ArrayList;
import java.util.List;

public class RabinKarpAlgorithm {

    private static final int BASE = 256;
    private static final int PRIME = 257;

    private static long calculateHash(String str, int len) {
        long hash = 0;
        for (int i = 0; i < len; i++) {
            int rank = str.charAt(i);
            hash = (hash * BASE + rank) % PRIME;
        }
        return hash;
    }

    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || n < m) {
            return matches;
        }

        long hFactor = 1;
        for (int i = 0; i < m - 1; i++) {
            hFactor = (hFactor * BASE) % PRIME;
        }

        long pHash = calculateHash(pattern, m);
        long tHash = calculateHash(text, m);

        System.out.println("Pattern Hash Value: " + pHash);
        System.out.println("----------------------------------------");

        for (int i = 0; i <= n - m; i++) {
            System.out.printf("Window [%d..%d] (\"%s\") -> Hash: %d",
                    i, i + m - 1, text.substring(i, i + m), tHash);

            if (pHash == tHash) {
                boolean fullMatch = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        fullMatch = false;
                        break;
                    }
                }

                if (fullMatch) {
                    System.out.print(" --> [MATCH FOUND]");
                    matches.add(i);
                } else {
                    System.out.print(" --> [SPURIOUS HASH COLLISION]");
                }
            }
            System.out.println();

            if (i < n - m) {
                int leadingChar = text.charAt(i);
                int trailingChar = text.charAt(i + m);

                tHash = (tHash - (leadingChar * hFactor) % PRIME + PRIME) % PRIME;
                tHash = (tHash * BASE + trailingChar) % PRIME;
            }
        }

        return matches;
    }

    public static void main(String[] args) {
        String text = "DAACABCDBA";
        String pattern = "CAB";

        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);
        System.out.println("Base (b): " + BASE + ", Prime (p): " + PRIME);
        System.out.println("========================================");

        List<Integer> occurrences = search(text, pattern);

        System.out.println("========================================");
        if (occurrences.isEmpty()) {
            System.out.println("Pattern not found in text.");
        } else {
            System.out.println("Pattern found at index(es): " + occurrences);
        }
    }
}