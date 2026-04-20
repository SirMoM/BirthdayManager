package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ControllerTest {

  @Test
  void findNextTabOrderIndex_ReturnsTheNextAvailableControl() {
    int nextIndex =
        Controller.findNextTabOrderIndex(2, false, new boolean[] {true, true, true, true});

    assertThat(nextIndex).isEqualTo(3);
  }

  @Test
  void findNextTabOrderIndex_SkipsUnavailableControls() {
    int nextIndex =
        Controller.findNextTabOrderIndex(
            4, false, new boolean[] {true, true, true, true, true, false, true});

    assertThat(nextIndex).isEqualTo(6);
  }

  @Test
  void findNextTabOrderIndex_WrapsBackwardsToTheLastAvailableControl() {
    int nextIndex =
        Controller.findNextTabOrderIndex(
            0, true, new boolean[] {true, true, true, true, true, false, true});

    assertThat(nextIndex).isEqualTo(6);
  }
}
