package application.util;

import java.util.Arrays;

public class LevenshteinDistanz {

    private LevenshteinDistanz() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }

    public static int calculate(String x, String y) {
        // Check early break conditiones
        if (x == null || y == null) {
            throw new NullPointerException("String not filled!");
        }
        if (x.length() == 0) return y.length();

        if (y.length() == 0) return x.length();

        // Creating the Matrix for the Calculation of the Levenshtein-Distance
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    // Def. https://dzone.com/articles/the-levenshtein-algorithm-1
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1),
                            y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
}
