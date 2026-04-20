package application.controller;

import application.util.localisation.LangResourceKeys;
import java.util.List;
import java.util.Optional;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class ShortcutDefinition {
  public static final String OPEN_FILE_ACTION = "open-file";
  public static final String SAVE_FILE_ACTION = "save-file";
  public static final String NEW_BIRTHDAY_ACTION = "new-birthday";
  public static final String DELETE_BIRTHDAY_ACTION = "delete-birthday";

  private static final List<ShortcutDefinition> DEFAULT_DEFINITIONS =
      List.of(
          new ShortcutDefinition(
              OPEN_FILE_ACTION,
              new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
              LangResourceKeys.shortcut_openFile),
          new ShortcutDefinition(
              SAVE_FILE_ACTION,
              new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
              LangResourceKeys.shortcut_saveFile),
          new ShortcutDefinition(
              NEW_BIRTHDAY_ACTION,
              new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
              LangResourceKeys.shortcut_newBirthday),
          new ShortcutDefinition(
              DELETE_BIRTHDAY_ACTION,
              new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN),
              LangResourceKeys.shortcut_deleteBirthdays));

  private final String actionId;
  private final KeyCombination keyCombination;
  private final LangResourceKeys descriptionKey;

  private ShortcutDefinition(
      final String actionId,
      final KeyCombination keyCombination,
      final LangResourceKeys descriptionKey) {
    this.actionId = actionId;
    this.keyCombination = keyCombination;
    this.descriptionKey = descriptionKey;
  }

  public static List<ShortcutDefinition> defaultDefinitions() {
    return DEFAULT_DEFINITIONS;
  }

  public static Optional<ShortcutDefinition> findByActionId(final String actionId) {
    return DEFAULT_DEFINITIONS.stream()
        .filter(definition -> definition.getActionId().equals(actionId))
        .findFirst();
  }

  public String getActionId() {
    return this.actionId;
  }

  public KeyCombination getKeyCombination() {
    return this.keyCombination;
  }

  public LangResourceKeys getDescriptionKey() {
    return this.descriptionKey;
  }
}
