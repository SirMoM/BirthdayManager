package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ShortcutViewFxmlTest {

  @Test
  void shortcutHeaderLabel_IsMouseTransparentSoItDoesNotBlockTheBackButton() throws IOException {
    try (InputStream inputStream =
        this.getClass().getResourceAsStream("/application/view/ShortcutView.fxml")) {
      assertThat(inputStream).isNotNull();

      String fxml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      assertThat(fxml)
          .contains("fx:id=\"shortcuts_Label\"")
          .contains("mouseTransparent=\"true\"");
    }
  }
}
