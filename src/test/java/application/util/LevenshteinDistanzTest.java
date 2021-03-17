package application.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LevenshteinDistanzTest {

  String same = "same";

  @Test
  void calculate() {
    assertThat(LevenshteinDistanz.calculate(same, same)).isEqualTo(0);

    assertThat(LevenshteinDistanz.calculate(same, same + "!")).isEqualTo(1);
    assertThat(LevenshteinDistanz.calculate(same + "!", same)).isEqualTo(1);

    assertThat(LevenshteinDistanz.calculate(same, same + "??")).isEqualTo(2);
  }
}
