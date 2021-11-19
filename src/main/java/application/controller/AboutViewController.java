package application.controller;

import application.processes.CheckForUpdatesTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.awt.Desktop.isDesktopSupported;

public class AboutViewController extends Controller {

    private static final Logger LOG = LogManager.getLogger(CheckForUpdatesTask.class.getName());
    static private final String githubURL = "https://github.com/SirMoM/BirthdayManager";
    static private final String websiteURL = "https://sirmom.github.io/BirthdayManagerWebsite/";
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label about_Label;
    @FXML
    private ImageView icon_ImageView;
    @FXML
    private Label appName_Label;
    @FXML
    private Label version_Label;
    @FXML
    private Label createdBy_Label;
    @FXML
    private Hyperlink website_Hyperlink;
    @FXML
    private Hyperlink github_Hyperlink;
    @FXML
    private Button checkForUpdates_Button;

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
    }

    @Override
    public void placeFocus() {
        checkForUpdates_Button.requestFocus();
    }

    /**
     * Binds the JavaFX Components to their {@link EventHandler}.
     */
    private void bindComponents() {
        github_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(githubURL));
        website_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(websiteURL));
        checkForUpdates_Button.addEventHandler(ActionEvent.ANY, actionEvent -> checkVersionAndAlert());
    }

    private void checkVersionAndAlert() {
        CheckForUpdatesTask checkForUpdatesTask = new CheckForUpdatesTask();
        checkForUpdatesTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            String msg = checkForUpdatesTask.getValue();
            if (msg != null) {
                final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New version!");
                Button button = new Button("Open Download page!");
                button.setOnAction(actionEvent -> {
                    String url = "https://github.com/SirMoM/BirthdayManager/packages";
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (IOException | URISyntaxException exception) {
                            LOG.catching(Level.DEBUG, exception);
                            LOG.debug("Could not open url {}", url);
                        }
                    }
                });
                CheckBox checkBox = new CheckBox("Don't remind me again");
                GridPane gridPane = new GridPane();
                gridPane.setMaxWidth(Double.MAX_VALUE);
                gridPane.add(button, 1, 0);
                gridPane.add(checkBox, 1, 1);
                gridPane.setAlignment(Pos.CENTER);
                alert.getDialogPane().setContent(gridPane);
                alert.setHeaderText(msg);
                if (checkBox.isSelected()) {
                    PropertyManager.getInstance().getProperties().setProperty(PropertyFields.NEW_VERSION_REMINDER, String.valueOf(checkBox.isSelected()));
                    try {
                        PropertyManager.getInstance().storeProperties("");
                    } catch (IOException e) {
                        LOG.catching(e);
                    }
                }
                alert.showAndWait();

            } else {
                final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No new version found!");
                alert.setHeaderText("No new version found!");
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
                LOG.catching(exception);
            }
        }
    }

    /**
     * All assertions for the controller. Checks if all FXML-Components have been loaded properly.
     */
    private void assertions() {
        assert about_Label != null : "fx:id=\"about_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert icon_ImageView != null : "fx:id=\"icon_ImageView\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert appName_Label != null : "fx:id=\"appName_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert version_Label != null : "fx:id=\"version_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert createdBy_Label != null : "fx:id=\"createdBy_Label\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert website_Hyperlink != null : "fx:id=\"website_Hyperlink\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert github_Hyperlink != null : "fx:id=\"github_Hyperlink\" was not injected: check your FXML file 'AboutView.fxml'.";
        assert checkForUpdates_Button != null : "fx:id=\"checkForUpdates_Button\" was not injected: check your FXML file 'AboutView.fxml'.";
    }


    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.assertions();
        this.updateLocalisation();
        this.bindComponents();
    }
}
