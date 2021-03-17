package application.util;

import java.util.Arrays;

public class LevenshteinDistanz {

  private LevenshteinDistanz() throws IllegalAccessException {
    throw new IllegalAccessException("Utility class");
  }

  public static int calculate(String strA, String strB) {
    // Check early break conditiones
    if (strA == null || strB == null) {
      throw new NullPointerException("String not filled!");
    }
    if (strA.length() == 0) return strB.length();

    if (strB.length() == 0) return strA.length();

    // Creating the Matrix for the Calculation of the Levenshtein-Distance
    int[][] dp = new int[strA.length() + 1][strB.length() + 1];

    for (int i = 0; i <= strA.length(); i++) {
      for (int j = 0; j <= strB.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          // Def. https://dzone.com/articles/the-levenshtein-algorithm-1
          dp[i][j] =
              min(
                  dp[i - 1][j - 1] + costOfSubstitution(strA.charAt(i - 1), strB.charAt(j - 1)),
                  dp[i - 1][j] + 1,
                  dp[i][j - 1] + 1);
        }
      }
    }

    return dp[strA.length()][strB.length()];
  }

  private static int costOfSubstitution(char a, char b) {
    return a == b ? 0 : 1;
  }

  private static int min(int... numbers) {
    return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
  }
}
