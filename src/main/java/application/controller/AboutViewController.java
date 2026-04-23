package application.controller;

import static java.awt.Desktop.isDesktopSupported;

import application.processes.CheckForUpdatesTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AboutViewController extends Controller {

  private static final Logger LOG = LogManager.getLogger(AboutViewController.class.getName());
  private static final String githubURL = "https://github.com/SirMoM/BirthdayManager";
  private static final String websiteURL = "https://sirmom.github.io/BirthdayManagerWebsite/";
  @FXML private ResourceBundle resources;
  @FXML private URL location;
  @FXML private Label about_Label;
  @FXML private ImageView icon_ImageView;
  @FXML private Label appName_Label;
  @FXML private Label version_Label;
  @FXML private Label createdBy_Label;
  @FXML private Hyperlink website_Hyperlink;
  @FXML private Hyperlink github_Hyperlink;
  @FXML private Button checkForUpdates_Button;
  @FXML private Button showShortcuts_Button;
  @FXML private Label changelog_Label;
  @FXML private TextArea changelog_TextArea;

  public AboutViewController(MainController mainController) {
    super(mainController);
  }

  @Override
  public void updateLocalisation() {
    LangResourceManager lrm = new LangResourceManager();
    about_Label.setText(lrm.getLocaleString(LangResourceKeys.about));
    appName_Label.setText(lrm.getLocaleString(LangResourceKeys.appName_Label));
    String version = lrm.getLocaleString(LangResourceKeys.version_Label);
    version = String.format(version, CheckForUpdatesTask.getCurrentVersion());
    version_Label.setText(version);
    createdBy_Label.setText(lrm.getLocaleString(LangResourceKeys.createdBy_Label));
    website_Hyperlink.setText(lrm.getLocaleString(LangResourceKeys.visitWebsite));
    github_Hyperlink.setText(lrm.getLocaleString(LangResourceKeys.visitGithub));
    checkForUpdates_Button.setText(lrm.getLocaleString(LangResourceKeys.checkForUpdates));
    showShortcuts_Button.setText(lrm.getLocaleString(LangResourceKeys.showShortcuts_Button));
    changelog_Label.setText(lrm.getLocaleString(LangResourceKeys.changelog_Label));
  }

  @Override
  public void placeFocus() {
    checkForUpdates_Button.requestFocus();
  }

  /** Binds the JavaFX Components to their {@link EventHandler}. */
  private void bindComponents() {
    github_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(githubURL));
    website_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(websiteURL));
    checkForUpdates_Button.addEventHandler(ActionEvent.ANY, actionEvent -> checkVersionAndAlert());
    showShortcuts_Button.addEventHandler(
        ActionEvent.ANY,
        actionEvent ->
            getMainController()
                .showShortcutView(
                    (Stage) showShortcuts_Button.getScene().getWindow(),
                    ShortcutLaunchContext.ABOUT));
  }

  private void loadChangelog() {
    try (InputStream inputStream =
        this.getClass().getResourceAsStream("/application/changelog.txt")) {
      if (inputStream == null) {
        LOG.error("Could not load bundled changelog resource.");
        changelog_TextArea.clear();
        return;
      }
      changelog_TextArea.setText(readBundledText(inputStream));
    } catch (IOException ioException) {
      LOG.log(Level.ERROR, "Could not read bundled changelog resource.", ioException);
      changelog_TextArea.clear();
    }
  }

  static String readBundledText(final InputStream inputStream) throws IOException {
    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }

  private void checkVersionAndAlert() {
    final LangResourceManager lrm = new LangResourceManager();
    CheckForUpdatesTask checkForUpdatesTask = new CheckForUpdatesTask();
    checkForUpdatesTask.addEventHandler(
        WorkerStateEvent.WORKER_STATE_SUCCEEDED,
        event -> {
          String latestVersion = checkForUpdatesTask.getValue();
          if (latestVersion != null) {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String msg =
                String.format(
                    lrm.getLocaleString(LangResourceKeys.updateAvailable_Message), latestVersion);
            alert.setTitle(lrm.getLocaleString(LangResourceKeys.updateAvailable_Title));
            Button button =
                new Button(lrm.getLocaleString(LangResourceKeys.openDownloadPage_Button));
            button.setOnAction(
                actionEvent -> {
                  String url = "https://github.com/SirMoM/BirthdayManager/packages";
                  if (Desktop.isDesktopSupported()
                      && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                      Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException | URISyntaxException exception) {
                      LOG.warn("Could not open update URL {} in browser.", url, exception);
                    }
                  }
                });
            CheckBox checkBox =
                new CheckBox(lrm.getLocaleString(LangResourceKeys.dontRemindAgain_CheckBox));
            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            gridPane.add(button, 1, 0);
            gridPane.add(checkBox, 1, 1);
            gridPane.setAlignment(Pos.CENTER);
            alert.getDialogPane().setContent(gridPane);
            alert.setHeaderText(msg);
            alert.showAndWait();
            if (MainController.shouldPersistUpdateReminder(checkBox.isSelected())) {
              PropertyManager.getInstance()
                  .getProperties()
                  .setProperty(
                      PropertyFields.NEW_VERSION_REMINDER, String.valueOf(checkBox.isSelected()));
              try {
                PropertyManager.getInstance().storeProperties("");
              } catch (IOException e) {
                LOG.warn("Failed to persist update reminder preference from about view.", e);
              }
            }

          } else {
            final Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(lrm.getLocaleString(LangResourceKeys.noNewVersionFound_Title));
            alert.setHeaderText(lrm.getLocaleString(LangResourceKeys.noNewVersionFound_Title));
            alert.showAndWait();
          }
        });
    new Thread(checkForUpdatesTask).start();
  }

  private void openWebsite(String url) {
    if (isDesktopSupported()) {
      try {
        URI website = new URI(url);
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(website);
      } catch (URISyntaxException | IOException exception) {
        LOG.warn("Could not open website URL {}.", url, exception);
      }
    }
  }

  /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
  private void assertions() {
    assert about_Label != null
        : "fx:id=\"about_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert icon_ImageView != null
        : "fx:id=\"icon_ImageView\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert appName_Label != null
        : "fx:id=\"appName_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert version_Label != null
        : "fx:id=\"version_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert createdBy_Label != null
        : "fx:id=\"createdBy_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert website_Hyperlink != null
        : "fx:id=\"website_Hyperlink\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert github_Hyperlink != null
        : "fx:id=\"github_Hyperlink\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert checkForUpdates_Button != null
        : "fx:id=\"checkForUpdates_Button\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert showShortcuts_Button != null
        : "fx:id=\"showShortcuts_Button\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert changelog_Label != null
        : "fx:id=\"changelog_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
    assert changelog_TextArea != null
        : "fx:id=\"changelog_TextArea\" was not injected: check your FXML file 'AboutView.fxml'.";
  }

  @Override
  @FXML
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.assertions();
    this.updateLocalisation();
    this.loadChangelog();
    this.bindComponents();
  }
}
