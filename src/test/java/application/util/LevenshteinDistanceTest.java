package application.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevenshteinDistanceTest {

    String same = "same";

    @Test
    void calculate() {
        assertThat(LevenshteinDistance.calculate(same, same)).isZero();

        assertThat(LevenshteinDistance.calculate(same, same + "!")).isEqualTo(1);
        assertThat(LevenshteinDistance.calculate(same + "!", same)).isEqualTo(1);

        assertThat(LevenshteinDistance.calculate(same, same + "??")).isEqualTo(2);
    }
}
