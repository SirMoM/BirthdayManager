package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PreferencesViewFxmlTest {

  @Test
  void preferencesView_UsesAShortcutsButtonInsteadOfAStandaloneHyperlink() throws IOException {
    try (InputStream inputStream =
        this.getClass().getResourceAsStream("/application/view/PreferencesView.fxml")) {
      assertThat(inputStream).isNotNull();

      String fxml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      assertThat(fxml).contains("Button fx:id=\"showShortcuts_Button\"");
      assertThat(fxml).doesNotContain("Hyperlink fx:id=\"showShortcuts_Hyperlink\"");
    }
  }

  @Test
  void preferencesView_GroupsFooterButtonsIntoSingleCenteredRow() throws IOException {
    try (InputStream inputStream =
        this.getClass().getResourceAsStream("/application/view/PreferencesView.fxml")) {
      assertThat(inputStream).isNotNull();

      String fxml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      assertThat(fxml).contains("<HBox alignment=\"CENTER\"");
      assertThat(fxml)
          .contains("fx:id=\"showShortcuts_Button\"")
          .contains("fx:id=\"cancel_button\"")
          .contains("fx:id=\"save_button\"");
    }
  }
}
