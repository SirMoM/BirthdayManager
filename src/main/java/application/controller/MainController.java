package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.CheckForUpdatesTask;
import application.processes.ExportToCalenderTask;
import application.processes.LoadPersonsTask;
import application.processes.SaveBirthdaysToFileTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class MainController {
    private static final String CSV_FILE_EXTENSION = "*.csv";
    private static final String USER_HOME = "user.home";
    private static final Logger LOG = LogManager.getLogger(MainController.class.getName());

    final EventHandler<ActionEvent> openPreferencesHandler = event -> {
        LOG.trace("Open Preferences");
        MainController.this.openPreferences();
    };
    private final Stage stage;
    final EventHandler<ActionEvent> exportToFileHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

        try {
            fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPENED)).getParentFile());
        } catch (final NullPointerException nullPointerException) {
            fileChooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
        }
        // TODO Extension Filters with internationalisation
        fileChooser.setInitialFileName("Birthdays");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", CSV_FILE_EXTENSION), new ExtensionFilter("All Files", "*.*"));

        final File saveFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());

        new Thread(new SaveBirthdaysToFileTask(saveFile)).start();
    };
    private final SessionInfos sessionInfos;
    final EventHandler closeAppHandler = event -> {
        final boolean autosave = Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.AUTOSAVE));

        if (PersonManager.getInstance().getPersons().isEmpty()) {
            Platform.exit();
            System.exit(0);
        }

        if (autosave && MainController.this.getSessionInfos().getSaveFile() != null) {
            SaveBirthdaysToFileTask saveBirthdaysToFileTask = new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile());
            saveBirthdaysToFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
                Platform.exit();
                System.exit(0);
            });
            new Thread(saveBirthdaysToFileTask).start();
        } else {
            LOG.debug(MainController.this.getSessionInfos().getSaveFile());
            final LangResourceManager lRM = new LangResourceManager();
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(lRM.getLocaleString(LangResourceKeys.save_before_exit));
            alert.setContentText(lRM.getLocaleString(LangResourceKeys.save_before_exit_question));

            final ButtonType okButton = new ButtonType(lRM.getLocaleString(LangResourceKeys.yes), ButtonBar.ButtonData.YES);
            final ButtonType noButton = new ButtonType(lRM.getLocaleString(LangResourceKeys.no), ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(okButton, noButton);

            alert.showAndWait().ifPresent(type -> {
                if (type.getButtonData() == ButtonType.YES.getButtonData() && MainController.this.getSessionInfos().getSaveFile() == null) {

                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
                    fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), CSV_FILE_EXTENSION));

                    try {
                        fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPENED)).getParentFile());
                    } catch (final NullPointerException nullPointerException) {
                        fileChooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
                    }

                    // if the chooser is "x'ed" the file is null
                    final File selectedFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());

                    if (selectedFile == null) {
                        final Alert error = new Alert(AlertType.ERROR);
                        error.showAndWait();
                        return;
                    } else {
                        MainController.this.getSessionInfos().setSaveFile(selectedFile);
                        SaveBirthdaysToFileTask saveBirthdaysToFileTask = new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile());
                        saveBirthdaysToFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
                            Platform.exit();
                            System.exit(0);
                        });
                        new Thread(saveBirthdaysToFileTask).start();
                    }
                }
            });
        }
    };
    final EventHandler<ActionEvent> closeFileHandler = event -> MainController.this.sessionInfos.resetSubLists();
    final EventHandler<ActionEvent> saveToFileHandler = event -> {
        if (MainController.this.getSessionInfos().getSaveFile() == null) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
            fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), CSV_FILE_EXTENSION));
            try {
                fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPENED)).getParentFile());
            } catch (final NullPointerException nullPointerException) {
                fileChooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
            }

            // if the chooser is "x'ed" the file is null
            final File selectedFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());
            MainController.this.getSessionInfos().setSaveFile(selectedFile);
        }
        if (MainController.this.getSessionInfos().getSaveFile() == null) {
            final Alert error = new Alert(AlertType.ERROR);
            error.showAndWait();
            return;
        } else {
            new Thread(new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile())).start();
        }
    };
    public final EventHandler<ActionEvent> openFileExternal = event -> {
        // first check if Desktop is supported by Platform or not
        if (!Desktop.isDesktopSupported()) {
            LOG.debug("Desktop is not supported");
            return;
        }

        final File file = getSessionInfos().getSaveFile();

        final Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.EDIT) && file != null) {
            try {
                desktop.edit(file);
            } catch (final IOException ioException) {
                ioException.printStackTrace();
            }
        }
    };
    final EventHandler<ActionEvent> openFromFileChooserHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
        fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), CSV_FILE_EXTENSION));

        try {
            fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPENED)).getParentFile());
        } catch (final NullPointerException nullPointerException) {
            fileChooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
        }

        // if the chooser is "x'ed" the file is null
        final File selectedFile = fileChooser.showOpenDialog(MainController.this.getStage().getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        try {
            MainController.this.openFile(selectedFile);
        } catch (final IOException ioException) {
            LOG.catching(ioException);
        }
    };
    final EventHandler<ActionEvent> openFromRecentHandler = event -> {
        final String lastUsedFilePath = PropertyManager.getProperty(PropertyFields.LAST_OPENED);
        final File birthdayFile = new File(lastUsedFilePath);

        try {
            MainController.this.openFile(birthdayFile);
        } catch (final IOException ioException) {
            LOG.catching(ioException);
        }
    };
    private final Stack<SceneAndController> lastScenes;
    private Controller activeController = null;
    final EventHandler<ActionEvent> exportToCalendarHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

        fileChooser.setInitialDirectory(new File(System.getProperty(USER_HOME)));
        fileChooser.setInitialFileName("birthdays.ics");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Calendars", "*.ics"));

        final File saveFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());
        if (saveFile == null) {
            return;
        }
        ExportToCalenderTask exportToCalenderTask = null;
        try {
            exportToCalenderTask = new ExportToCalenderTask(saveFile);
            exportToCalenderTask.setOnSucceeded(x -> {
                LOG.debug("EXPORTED");
                final Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Exported");
                alert.setHeaderText("All birthdays have been exported to " + saveFile.getAbsolutePath() + "!");
                alert.showAndWait();
            });
            if (MainController.this.getActiveController() instanceof BirthdaysOverviewController) {
                ((BirthdaysOverviewController) MainController.this.getActiveController()).getProgressbar().progressProperty().bind(exportToCalenderTask.workDoneProperty());
            }
        } catch (final IOException ioException) {
            LOG.catching(Level.ERROR, ioException);
        }
        if (exportToCalenderTask != null) {
            new Thread(exportToCalenderTask).start();
        }
    };
    @FXML
    private MenuItem changeLanguage_MenuItem;

    /**
     * @param stage the main stage for the application
     */
    public MainController(final Stage stage) {
        lastScenes = new Stack<>();
        this.sessionInfos = new SessionInfos(this);
        PersonManager.getInstance().setSessionInfos(sessionInfos);

        this.stage = stage;
        this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/icons8-birthday-50.png")));
        stage.setOnCloseRequest(this.closeAppHandler);
    }

    public Controller getActiveController() {
        return this.activeController;
    }

    private void setActiveController(final Controller activeController) {
        this.activeController = activeController;
    }

    /**
     * @return the session infos for the specific App-Instance
     */
    public SessionInfos getSessionInfos() {
        return this.sessionInfos;
    }

    /**
     * @return the main stage of this app
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Switches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @see BirthdaysOverviewController
     */
    public void goToBirthdaysOverview() {
        this.setActiveController(new BirthdaysOverviewController(this));
        try {
            this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());
            this.stage.setTitle("Birthday Manager");
        } catch (final Exception exception) {
            LOG.catching(Level.ERROR, exception);
        }
        getActiveController().placeFocus();
    }

    /**
     * Switches scenes to the EditBirthdayView. Generates a new Controller.
     *
     * @see EditBirthdayViewController
     */
    public void goToEditBirthdayView() {
        this.setActiveController(new NewBirthdayViewController(this));

        try {
            this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.getActiveController());
        } catch (final Exception exception) {
            LOG.catching(Level.ERROR, exception);
        }
        getActiveController().placeFocus();
    }

    /**
     * Switches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @param indexPerson The index of the person to edit.
     * @see EditBirthdayViewController
     */
    public void goToEditBirthdayView(final int indexPerson) {
        this.setActiveController(new EditBirthdayViewController(this, indexPerson));
        try {
            this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.getActiveController());

        } catch (final Exception exception) {
            LOG.catching(Level.ERROR, exception);
        }
        getActiveController().placeFocus();
    }

    /**
     * Switches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @see EditBirthdayViewController
     */
    public void goToSearchView() {
        this.setActiveController(new SearchViewController(this));
        try {
            this.replaceSceneContent("/application/view/SearchView.fxml", this.getActiveController());
        } catch (final Exception exception) {
            LOG.catching(Level.ERROR, exception);
        }
        getActiveController().placeFocus();
    }

    /**
     * @param selectedFile used to fill the birthday list
     * @throws IOException
     */
    private void openFile(final File selectedFile) throws IOException {
        LOG.debug("Open file: {}", selectedFile.getAbsolutePath());

        this.getSessionInfos().setSaveFile(selectedFile);

        final LoadPersonsTask loadPersonsTask = new LoadPersonsTask(selectedFile, selectedFile.getAbsolutePath().endsWith(".csv"));
        loadPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            PersonManager.getInstance().getPersons().clear();
            sessionInfos.updateSubLists();
            LoadPersonsTask.Result result = loadPersonsTask.getValue();
            PersonManager.getInstance().getPersons().addAll(result.getPersons());
            for (Person.PersonCouldNotBeParsedException error : result.getErrors()) {
                logAndAlertParsingError(error);
            }
            LOG.debug("Loaded birthdays from File");
        });
        new Thread(loadPersonsTask).start();

        MainController.this.sessionInfos.getRecentFileName().set(selectedFile.getName());
        MainController.this.sessionInfos.getFileToOpenName().set(selectedFile.getName());

        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_OPENED, getSessionInfos().getSaveFile().getAbsolutePath());
        try {
            PropertyManager.getInstance().storeProperties("Saved recent file.");
        } catch (final IOException ioException) {
            LOG.catching(Level.ERROR, ioException);
        }
    }

    /**
     * Opens the preferences Window.
     *
     * <ul>
     *   <li>Creates a new {@link Stage}
     *   <li>Create a new {@link Scene}
     *   <li>Create a new {@link PreferencesViewController}
     * </ul>
     */
    public void openPreferences() {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(this.getClass().getResource("/application/view/PreferencesView.fxml"));
            fxmlLoader.setController(new PreferencesViewController(this));
            final Scene scene = new Scene(fxmlLoader.load());
            final Stage prefStage = new Stage();
            prefStage.setTitle("Preferences");
            prefStage.setScene(scene);
            setStyle(scene);
            prefStage.show();
        } catch (final IOException ioException) {
            LOG.log(Level.ERROR, "Failed to create new Window.", ioException);
        }
    }

    protected void setStyle(Scene scene) {
        scene.getStylesheets().clear();
        if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.DARK_MODE))) {
            scene.getStylesheets().add("dark-mode.css");
        }
    }

    protected void setStyle() {
        Scene scene = this.getStage().getScene();
        scene.getStylesheets().clear();
        if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.DARK_MODE))) {
            scene.getStylesheets().add("dark-mode.css");
        }
    }

    /**
     * @param fxmlPath   the path of the FXML-File representing the view
     * @param controller the associated Controller
     * @throws IOException if the FXML-File could not be loaded
     */
    private void replaceSceneContent(final String fxmlPath, final Initializable controller) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(fxmlPath));
        loader.setController(controller);
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        lastScenes.push(new SceneAndController((Controller) controller, scene));
        this.stage.setScene(scene);
        setStyle();

        // Show the GUI
        this.stage.show();
    }

    public void goToLastScene() {
        SceneAndController currentSceneAndController = lastScenes.pop();
        SceneAndController sceneAndController = lastScenes.peek();
        LOG.info("Going back to {} view.", sceneAndController.scene);

        this.stage.setScene(sceneAndController.scene);
        this.setActiveController(sceneAndController.controller);
        setStyle();

        this.stage.show();

        getActiveController().placeFocus();
    }

    public void settingsChanged() {
        goToBirthdaysOverview();
        this.getActiveController().updateLocalisation();
    }

    /**
     * Starts the application with the BirthdaysOverview and possibly load a file
     */
    public void start() {
        this.setActiveController(new BirthdaysOverviewController(this));

        boolean openFileOnStart = Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START));

        try {
            this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());

            if (openFileOnStart) {
                LOG.info("Try to open file on start!");
                String filePathString = PropertyManager.getProperty(PropertyFields.FILE_ON_START);

                if (filePathString == null) {
                    LOG.debug("Should have opened a file upon start but no file to open was found!");
                } else if (filePathString.isEmpty()) {
                    LOG.debug("Empty file path can't open!");
                    // If it is not an csv file remove from auto start!
                } else if (filePathString.endsWith(".csv")) {
                    this.openFile(new File(filePathString));
                    LOG.debug("Opening File {}", filePathString);
                } else {
                    PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
                    LOG.warn("Should have opened a file upon start but file to open was wasn't a csv file!");
                }
            }
        } catch (final Exception exception) {
            LOG.catching(Level.ERROR, exception);
        }
        getActiveController().placeFocus();
        checkVersionAndAlert();
    }

    private void checkVersionAndAlert() {
        if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.NEW_VERSION_REMINDER))) {
            LOG.debug("Don't check for new version!");
            return;
        }
        CheckForUpdatesTask checkForUpdatesTask = new CheckForUpdatesTask();
        checkForUpdatesTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            String msg = checkForUpdatesTask.getValue();
            if (msg != null) {
                final Alert alert = new Alert(AlertType.INFORMATION);
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

            }
        });
        new Thread(checkForUpdatesTask).start();
    }

    private void logAndAlertParsingError(Person.PersonCouldNotBeParsedException personCouldNotBeParsedException) {
        LOG.warn(personCouldNotBeParsedException);
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR: Parsing failed");
        alert.setContentText(personCouldNotBeParsedException.getMessage());
        alert.showAndWait();
    }

    private class SceneAndController {
        final Controller controller;
        final Scene scene;

        public SceneAndController(Controller controller, Scene scene) {
            this.scene = scene;
            this.controller = controller;
        }
    }
}
