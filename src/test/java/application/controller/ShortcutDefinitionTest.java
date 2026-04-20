package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import application.util.localisation.LangResourceKeys;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShortcutDefinitionTest {

  @Test
  void defaultDefinitions_ExposeTheSupportedShortcutActionsInDisplayOrder() {
    List<ShortcutDefinition> definitions = ShortcutDefinition.defaultDefinitions();

    assertThat(definitions)
        .extracting(ShortcutDefinition::getActionId)
        .containsExactly("open-file", "save-file", "new-birthday", "delete-birthday");
  }

  @Test
  void defaultDefinitions_UsePlatformShortcutModifiersAndLocalizedDescriptions() {
    List<ShortcutDefinition> definitions = ShortcutDefinition.defaultDefinitions();

    assertThat(definitions)
        .extracting(definition -> definition.getKeyCombination().getName())
        .containsExactly("Shortcut+O", "Shortcut+S", "Shortcut+N", "Shortcut+D");
    assertThat(definitions)
        .extracting(ShortcutDefinition::getDescriptionKey)
        .containsExactly(
            LangResourceKeys.shortcut_openFile,
            LangResourceKeys.shortcut_saveFile,
            LangResourceKeys.shortcut_newBirthday,
            LangResourceKeys.shortcut_deleteBirthdays);
  }

  @Test
  void findByActionId_ReturnsTheMatchingDefinition() {
    assertThat(ShortcutDefinition.findByActionId("save-file"))
        .hasValueSatisfying(
            definition -> {
              assertThat(definition.getActionId()).isEqualTo("save-file");
              assertThat(definition.getDescriptionKey())
                  .isEqualTo(LangResourceKeys.shortcut_saveFile);
            });
  }
}
