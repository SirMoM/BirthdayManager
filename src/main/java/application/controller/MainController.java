/**
 *
 */
package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.CheckForUpdatesTask;
import application.processes.ExportToCalenderTask;
import application.processes.LoadPersonsTask;
import application.processes.ExportToCalenderTask;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class MainController {
    private static final String CSV_FILE_EXTENSION = "*.csv";
    private static final String USER_HOME = "user.home";
    private final Logger LOG;
    final EventHandler<ActionEvent> openPreferencesHander = event -> {
        MainController.this.openPreferences();
        MainController.this.LOG.trace("Open Preferences");
    };
    private final Stage stage;
    final EventHandler<ActionEvent> exportToFileHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

        try {
            fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getParentFile());
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
            MainController.this.LOG.debug(MainController.this.getSessionInfos().getSaveFile());
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
                        fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getParentFile());
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
                fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getParentFile());
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
            MainController.this.LOG.debug("Desktop is not supported");
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
                MainController.this.LOG.debug("EXPORTED");
                final Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Exported");
                alert.setHeaderText("All bithdays have been exported to " + saveFile.getAbsolutePath() + "!");
                alert.showAndWait();
            });
            if (MainController.this.getActiveController() instanceof BirthdaysOverviewController) {
                ((BirthdaysOverviewController) MainController.this.getActiveController()).getProgressbar().progressProperty().bind(exportToCalenderTask.workDoneProperty());
            }
        } catch (final IOException ioException) {
            MainController.this.LOG.catching(Level.ERROR, ioException);
        }
        if (exportToCalenderTask != null) {
            new Thread(exportToCalenderTask).start();
        }
    };
    final EventHandler<ActionEvent> openFromFileChooserHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
        fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), CSV_FILE_EXTENSION));

        try {
            fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getParentFile());
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
            MainController.this.LOG.catching(ioException);
        }
    };
    final EventHandler<ActionEvent> openFromRecentHandler = event -> {
        final String lastUsedFilePath = PropertyManager.getProperty(PropertyFields.LAST_OPEND);
        final File birthdayFile = new File(lastUsedFilePath);

        try {
            MainController.this.openFile(birthdayFile);
        } catch (final IOException ioException) {
            MainController.this.LOG.catching(ioException);
        }
    };

    @FXML
    private MenuItem changeLanguage_MenuItem;

    /** @param stage the mainstage for the application */
    public MainController(final Stage stage) {
        this.stage = stage;
        this.sessionInfos = new SessionInfos(this);
        this.LOG = LogManager.getLogger(this.getClass().getName());
        this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/icons8-birthday-50.png")));

        stage.setOnCloseRequest(this.closeAppHandler);
    }

    public Controller getActiveController() {
        return this.activeController;
    }

    private void setActiveController(final Controller activeController) {
        this.activeController = activeController;
    }

    /** @return the session infos for the spezific App-Instance */
    public SessionInfos getSessionInfos() {
        return this.sessionInfos;
    }

    /** @return the main stage of this app */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Swiches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @see BirthdaysOverviewController
     */
    public void goToBirthdaysOverview() {
        this.setActiveController(new BirthdaysOverviewController(this));
        try {
            this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());

        } catch (final Exception exception) {
            this.LOG.catching(Level.ERROR, exception);
        }
        this.getSessionInfos().updateSubLists();
    }

    /**
     * Swiches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @see EditBirthdayViewController
     */
    public void goToEditBirthdayView() {
        this.setActiveController(new NewBirthdayViewController(this));
        try {
            this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.getActiveController());
        } catch (final Exception exception) {
            this.LOG.catching(Level.ERROR, exception);
        }
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
            this.LOG.catching(Level.ERROR, exception);
        }
    }

    /**
     * Swiches scenes to the BirthdayOverview. Generates a new Controller.
     *
     * @see EditBirthdayViewController
     */
    public void goToSearchView() {
        this.setActiveController(new SearchViewController(this));
        try {
            this.replaceSceneContent("/application/view/SearchView.fxml", this.getActiveController());
        } catch (final Exception exception) {
            this.LOG.catching(Level.ERROR, exception);
        }
    }

    /**
     * @param fxmlPath the path of the FXML-File representing the view
     * @param controller the associated Controller
     */
    public void gotoNextScene(final String fxmlPath, final Initializable controller) {
        try {
            this.replaceSceneContent(fxmlPath, controller);
        } catch (final Exception exception) {
            this.LOG.catching(Level.ERROR, exception);
        }
    }

    /**
     * @param selectedFile used to fill the birthday list
     * @throws IOException
     */
    private void openFile(final File selectedFile) throws IOException {
        MainController.this.LOG.debug("Opend file:" + selectedFile.getAbsolutePath());
        this.getSessionInfos().setSaveFile(selectedFile);

        final LoadPersonsTask loadPersonsTask = new LoadPersonsTask(selectedFile, selectedFile.getAbsolutePath().endsWith(".csv"));
        loadPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            PersonManager.getInstance().getPersons().clear();
            LoadPersonsTask.Result result = loadPersonsTask.getValue();
            PersonManager.getInstance().getPersons().addAll(result.getPersons());
            for (Person.PersonCouldNotBeParsedException error : result.getErrors()) {
                logAndAlertParsingError(error);
            }
            MainController.this.LOG.debug("Loaded birthdays from File");
            MainController.this.sessionInfos.updateSubLists();
        });
        new Thread(loadPersonsTask).start();

        MainController.this.sessionInfos.getRecentFileName().set(selectedFile.getName());
        MainController.this.sessionInfos.getFileToOpenName().set(selectedFile.getName());

        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_OPEND, getSessionInfos().getSaveFile().getAbsolutePath());
        try {
            PropertyManager.getInstance().storeProperties("Saved recent file.");
        } catch (final IOException ioException) {
            this.LOG.catching(Level.FATAL, ioException);
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
            final Stage stage = new Stage();
            stage.setTitle("Preferences");
            stage.setScene(scene);
            setStyle(scene);
            stage.show();
        } catch (final IOException ioException) {
            this.LOG.log(Level.ERROR, "Failed to create new Window.", ioException);
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
     * @param fxmlPath the path of the FXML-File representing the view
     * @param controller the associated Controller
     * @throws IOException if the FXML-File could not be loaded
     */
    private void replaceSceneContent(final String fxmlPath, final Initializable controller) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(fxmlPath));
        loader.setController(controller);
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        this.stage.setScene(scene);
        setStyle();

        // Show the GUI
        this.stage.show();
    }

    public void settingsChanged() {
        Scene scene = getStage().getScene();
        goToBirthdaysOverview();
        this.getActiveController().updateLocalisation();

    }

    /** Starts the application with the BirthdaysOverview and possibly loaded file */
    public void start() {
        this.setActiveController(new BirthdaysOverviewController(this));
        checkVersionAndAlert();
        try {
            this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());
            if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START))) {
                String file = PropertyManager.getProperty(PropertyFields.FILE_ON_START);
                if (!PropertyManager.getProperty(PropertyFields.FILE_ON_START).endsWith(".csv")) {
                    PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
                    file = null;
                }
                if (file != null && !file.isEmpty()) {
                    this.openFile(new File(PropertyManager.getProperty(PropertyFields.FILE_ON_START)));
                } else {
                    this.LOG.warn("Should have opend a file upon start but no file to open was found!");
                }
            }

        } catch (final Exception exception) {
            this.LOG.catching(Level.ERROR, exception);
        }
    }
    // TODO "real" Alert
    private void checkVersionAndAlert() {
        CheckForUpdatesTask checkForUpdatesTask = new CheckForUpdatesTask();
        checkForUpdatesTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            String msg = checkForUpdatesTask.getValue();
            if (msg != null) {
                final Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("New version!");
                alert.setContentText(msg);
                alert.showAndWait();
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
}
