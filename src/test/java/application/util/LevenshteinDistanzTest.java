package application.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevenshteinDistanzTest {

    String same = "same";

    @Test
    void calculate() {
        assertThat(LevenshteinDistanz.calculate(same, same)).isZero();

        assertThat(LevenshteinDistanz.calculate(same, same + "!")).isEqualTo(1);
        assertThat(LevenshteinDistanz.calculate(same + "!", same)).isEqualTo(1);

        assertThat(LevenshteinDistanz.calculate(same, same + "??")).isEqualTo(2);
    }
}
