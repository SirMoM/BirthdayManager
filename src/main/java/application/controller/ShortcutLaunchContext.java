package application.controller;

import application.util.localisation.LangResourceKeys;

public enum ShortcutLaunchContext {
  ABOUT("/application/view/AboutView.fxml", LangResourceKeys.about),
  PREFERENCES("/application/view/PreferencesView.fxml", LangResourceKeys.preferences_MenuItem);

  private final String fxmlPath;
  private final LangResourceKeys titleKey;

  ShortcutLaunchContext(final String fxmlPath, final LangResourceKeys titleKey) {
    this.fxmlPath = fxmlPath;
    this.titleKey = titleKey;
  }

  public String getFxmlPath() {
    return this.fxmlPath;
  }

  public LangResourceKeys getTitleKey() {
    return this.titleKey;
  }
}
