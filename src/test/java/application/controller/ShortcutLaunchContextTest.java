package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import application.util.localisation.LangResourceKeys;
import org.junit.jupiter.api.Test;

class ShortcutLaunchContextTest {

  @Test
  void aboutContext_ReturnsTheAboutPopupViewAndTitle() {
    assertThat(ShortcutLaunchContext.ABOUT.getFxmlPath())
        .isEqualTo("/application/view/AboutView.fxml");
    assertThat(ShortcutLaunchContext.ABOUT.getTitleKey()).isEqualTo(LangResourceKeys.about);
  }

  @Test
  void preferencesContext_ReturnsThePreferencesPopupViewAndTitle() {
    assertThat(ShortcutLaunchContext.PREFERENCES.getFxmlPath())
        .isEqualTo("/application/view/PreferencesView.fxml");
    assertThat(ShortcutLaunchContext.PREFERENCES.getTitleKey())
        .isEqualTo(LangResourceKeys.preferences_MenuItem);
  }
}
