package application.controller;

import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ShortcutViewController extends Controller {
  private final ShortcutLaunchContext shortcutLaunchContext;

  @FXML private Label shortcuts_Label;
  @FXML private GridPane shortcuts_GridPane;
  @FXML private Button back_Button;

  public ShortcutViewController(
      final MainController mainController, final ShortcutLaunchContext shortcutLaunchContext) {
    super(mainController);
    this.shortcutLaunchContext = shortcutLaunchContext;
  }

  @Override
  public void updateLocalisation() {
    final LangResourceManager resourceManager = new LangResourceManager();
    this.shortcuts_Label.setText(resourceManager.getLocaleString(LangResourceKeys.shortcuts));
    this.back_Button.setText(resourceManager.getLocaleString(LangResourceKeys.back_Button));
    this.populateShortcutRows(resourceManager);
  }

  @Override
  public void placeFocus() {
    this.back_Button.requestFocus();
  }

  private void bindComponents() {
    this.back_Button.addEventHandler(
        ActionEvent.ANY,
        event ->
            this.getMainController()
                .restorePopupView(
                    (Stage) this.back_Button.getScene().getWindow(), this.shortcutLaunchContext));
  }

  private void populateShortcutRows(final LangResourceManager resourceManager) {
    this.shortcuts_GridPane.getChildren().clear();
    Label shortcutColumnLabel =
        new Label(resourceManager.getLocaleString(LangResourceKeys.shortcut_Column));
    Label actionColumnLabel =
        new Label(resourceManager.getLocaleString(LangResourceKeys.action_Column));
    shortcutColumnLabel.setStyle("-fx-font-weight: bold;");
    actionColumnLabel.setStyle("-fx-font-weight: bold;");
    GridPane.setHalignment(shortcutColumnLabel, HPos.RIGHT);
    this.shortcuts_GridPane.add(shortcutColumnLabel, 0, 0);
    this.shortcuts_GridPane.add(actionColumnLabel, 1, 0);

    for (int index = 0; index < ShortcutDefinition.defaultDefinitions().size(); index++) {
      ShortcutDefinition definition = ShortcutDefinition.defaultDefinitions().get(index);

      Label shortcutKeyLabel = new Label(definition.getKeyCombination().getDisplayText());
      Label descriptionLabel =
          new Label(resourceManager.getLocaleString(definition.getDescriptionKey()));
      descriptionLabel.setWrapText(true);

      GridPane.setHalignment(shortcutKeyLabel, HPos.RIGHT);
      this.shortcuts_GridPane.add(shortcutKeyLabel, 0, index + 1);
      this.shortcuts_GridPane.add(descriptionLabel, 1, index + 1);
    }
  }

  private void assertions() {
    assert shortcuts_Label != null
        : "fx:id=\"shortcuts_Label\" was not injected: check your FXML file 'ShortcutView.fxml'.";
    assert shortcuts_GridPane != null
        : "fx:id=\"shortcuts_GridPane\" was not injected: check your FXML file 'ShortcutView.fxml'.";
    assert back_Button != null
        : "fx:id=\"back_Button\" was not injected: check your FXML file 'ShortcutView.fxml'.";
  }

  @Override
  @FXML
  public void initialize(final URL location, final ResourceBundle resources) {
    this.assertions();
    this.updateLocalisation();
    this.bindComponents();
  }
}
