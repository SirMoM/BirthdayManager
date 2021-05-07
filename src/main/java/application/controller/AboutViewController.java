package application.controller;

import application.processes.CheckForUpdatesTask;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

    static private String githubURL = "https://github.com/SirMoM/BirthdayManager";
    static private String websiteURL= "https://sirmom.github.io/BirthdayManagerWebsite/";

    public AboutViewController(MainController mainController) {
        super(mainController);
    }

    @Override
    public void updateLocalisation(){
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

    /** Binds the JavaFX Components to their {@link EventHandler}. */
    private void bindComponents(){
        github_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(githubURL));
        website_Hyperlink.addEventHandler(ActionEvent.ANY, actionEvent -> openWebsite(websiteURL));
        checkForUpdates_Button.addEventHandler(ActionEvent.ANY, actionEvent -> getMainController().checkVersionAndAlert());
    }

    private void openWebsite(String url){
        if( isDesktopSupported()){
            try {
                URI website = new URI(url);
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(website);
            } catch (URISyntaxException | IOException exception) {
                LOG.catching(exception);
            }
        }
    }

    /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
    private void assertions(){
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
