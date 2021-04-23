package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAWeek;
import application.model.RecentItems;
import application.processes.LoadPersonsTask;
import application.processes.SaveBirthdaysToFileTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.WeekTableCallback;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class BirthdaysOverviewController extends Controller {
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Logger LOG = LogManager.getLogger(BirthdaysOverviewController.class.getName());
    final EventHandler<ActionEvent> newBirthdayHandler = event -> BirthdaysOverviewController.this.getMainController().goToEditBirthdayView();
    private final EventHandler<ActionEvent> importBirthdaysHandler = event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
        fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.txt_file), "*.txt"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), "*.csv"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.all_files), "*.*"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // if the chooser is "x'ed" the file is null
        final File selectedFile = fileChooser.showOpenDialog(getMainController().getStage().getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        LOG.debug("Importing file: {}", selectedFile.getAbsolutePath());

        try {
            final LoadPersonsTask loadPersonsTask = new LoadPersonsTask(selectedFile, selectedFile.getAbsolutePath().endsWith(".csv"));
            loadPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
                LoadPersonsTask.Result result = loadPersonsTask.getValue();
                PersonManager.getInstance().getPersons().addAll(result.getPersons());
                getMainController().getSessionInfos().updateSubLists();

                for (Person.PersonCouldNotBeParsedException error : result.getErrors()) {
                    logAndAlertParsingError(error);
                }
                LOG.debug("Imported birthdays from File");
            });
            new Thread(loadPersonsTask).start();
        } catch (IOException ioException) {
            LOG.catching(Level.ERROR, ioException);
        }
    };
    protected boolean showNextBirthdays;
    Callback<ListView<Person>, ListCell<Person>> colorCellFactory = new Callback<ListView<Person>, ListCell<Person>>() {

        @Override
        public ListCell<Person> call(final ListView<Person> param) {

            return new ListCell<Person>() {
                @Override
                protected void updateItem(final Person item, final boolean empty) {
                    String firstColorString = PropertyManager.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR);
                    String secondColorString = PropertyManager.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR);
                    super.updateItem(item, empty);
                    this.styleProperty().bind(new SimpleStringProperty(""));
                    if (!empty && item != null) {
                        if (item.getBirthday().isEqual(LocalDate.now().withYear(item.getBirthday().getYear()))) {
                            this.styleProperty().bind(Bindings.when(this.selectedProperty()).then(new SimpleStringProperty("-fx-background-color: -fx-selection-bar;")).otherwise(new SimpleStringProperty("-fx-background-color: " + firstColorString)));
                        } else if (LocalDate.now().withYear(item.getBirthday().getYear()).isBefore(item.getBirthday()) && item.getBirthday().isBefore(LocalDate.now().withYear(item.getBirthday().getYear()).plusDays(7))) {

                            this.styleProperty().bind(Bindings.when(this.selectedProperty()).then(new SimpleStringProperty("-fx-background-color: -fx-selection-bar;")).otherwise(new SimpleStringProperty("-fx-background-color: " + secondColorString)));
                        }
                        this.setText(item + "\t \t \t " + (new LangResourceManager().getLocaleString(LangResourceKeys.age)) + ":\t" + (LocalDate.now().getYear() - item.getBirthday().getYear()));

                    } else {
                        this.setText(null);
                    }
                }
            };
        }
    };
    private MenuItem recentFiles_MenuItem;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Menu file_menu;
    @FXML
    private MenuItem openFile_MenuItem;
    @FXML
    private Menu openRecent_MenuItem;
    @FXML
    private MenuItem closeFile_MenuItem;
    @FXML
    private MenuItem saveFile_MenuItem;
    @FXML
    private MenuItem saveAsFile_MenuItem;
    @FXML
    private MenuItem exportToCalendar_MenuItem;
    @FXML
    private MenuItem preferences_MenuItem;
    @FXML
    private MenuItem quit_MenuItem;
    @FXML
    private Menu edit_menu;
    @FXML
    private MenuItem showNextBirthdays_MenuItem;
    @FXML
    private MenuItem showLastBirthdays_MenuItem;
    @FXML
    private MenuItem newBirthday_MenuItem;
    @FXML
    private MenuItem importBirthdays_MenuItem;
    @FXML
    private MenuItem deleteBirthdays_MenuItem;
    @FXML
    private Menu help_menu;
    @FXML
    private MenuItem debug;
    @FXML
    private MenuItem refresh_MenuItem;
    @FXML
    private MenuItem openFileExternal_Button;
    @FXML
    private Button expandRightSide_Button;
    @FXML
    private Label nextBirthday_Label;
    @FXML
    private MenuItem searchBirthdayMenuItem;
    @FXML
    private ListView<Person> nextBdaysList;
    final EventHandler<ActionEvent> openBirthday = event -> {
        final ObservableList<Person> selectedItems = BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
            BirthdaysOverviewController.this.getMainController().goToEditBirthdayView(indexOf);
        }
    };
    final EventHandler<ActionEvent> showRecentBirthdaysHandler = eventHandler -> {
        BirthdaysOverviewController.this.nextBdaysList.setItems(BirthdaysOverviewController.this.getMainController().getSessionInfos().getRecentBirthdays());
        BirthdaysOverviewController.this.nextBirthday_Label.setText(new LangResourceManager().getLocaleString(LangResourceKeys.str_recentBirthday_Label));
        BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
        BirthdaysOverviewController.this.nextBdaysList.setCellFactory(null);
        BirthdaysOverviewController.this.nextBdaysList.refresh();
    };
    final EventHandler<ActionEvent> showNextBirthdaysHandler = event -> {
        BirthdaysOverviewController.this.nextBdaysList.setItems(BirthdaysOverviewController.this.getMainController().getSessionInfos().getNextBirthdays());
        BirthdaysOverviewController.this.nextBirthday_Label.setText(new LangResourceManager().getLocaleString(LangResourceKeys.str_nextBirthday_Label));
        BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
        BirthdaysOverviewController.this.nextBdaysList.setCellFactory(BirthdaysOverviewController.this.colorCellFactory);
        BirthdaysOverviewController.this.nextBdaysList.refresh();
    };
    private final EventHandler<Event> birthdayDoubleClickHandler = event -> {
        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            if (((MouseEvent) event).getClickCount() >= 2) {
                final ObservableList<Person> selectedItems = BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
                final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
                BirthdaysOverviewController.this.getMainController().goToEditBirthdayView(indexOf);
            }
        } else if (event.getEventType().equals(KeyEvent.KEY_PRESSED) && ((KeyEvent) event).getCode() == KeyCode.ENTER) {
            final ObservableList<Person> selectedItems = BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
            final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
            BirthdaysOverviewController.this.getMainController().goToEditBirthdayView(indexOf);
        }
    };
    private final EventHandler<ActionEvent> deletePersonHandler = event -> {
        final ObservableList<Person> selectedItems = BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            final int indexOf = PersonManager.getInstance().getPersons().indexOf(selectedItems.get(0));
            PersonManager.getInstance().deletePerson(PersonManager.getInstance().getPersonFromIndex(indexOf));
            BirthdaysOverviewController.this.getMainController().getSessionInfos().updateSubLists();

            if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU))) {
                final SaveBirthdaysToFileTask task = new SaveBirthdaysToFileTask(getMainController().getSessionInfos().getSaveFile());
                task.setOnSucceeded(event1 -> {
                    if (event1.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                        LOG.debug("Saved changes to file (via write thru)");
                    }
                });

                new Thread(task).start();
            }

            BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
            BirthdaysOverviewController.this.nextBdaysList.setCellFactory(null);
            BirthdaysOverviewController.this.nextBdaysList.refresh();
            BirthdaysOverviewController.this.nextBdaysList.setCellFactory(BirthdaysOverviewController.this.colorCellFactory);
            BirthdaysOverviewController.this.nextBdaysList.refresh();
        }
    };
    @FXML
    private MenuItem openBirthday_MenuItem;
    @FXML
    private TabPane rightSide_TapView;
    @FXML
    private Tab week_tap;
    @FXML
    private TableView<PersonsInAWeek> week_tableView;
    @FXML
    private TableColumn<PersonsInAWeek, String> monday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> tuesday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> wednesday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> thursday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> friday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> saturday_column1;
    @FXML
    private TableColumn<PersonsInAWeek, String> sunday_column1;
    @FXML
    private Tab month_tap;
    @FXML
    private TableColumn<PersonsInAWeek, String> monday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> tuesday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> wednesday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> thursday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> friday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> saturday_column2;
    @FXML
    private TableColumn<PersonsInAWeek, String> sunday_column2;
    @FXML
    private Label openedFile_label;
    @FXML
    private Font x3;
    @FXML
    private Color x4;
    @FXML
    private Label date_label;
    @FXML
    private ProgressBar progressbar;

    public BirthdaysOverviewController(final MainController mainController) {
        super(mainController);
    }

    /**
     * All assertions for the controller. Checks if all FXML-Components have been loaded properly.
     */
    private void assertion() {
        assert file_menu != null : "fx:id=\"file_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert openFile_MenuItem != null : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert openRecent_MenuItem != null : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert closeFile_MenuItem != null : "fx:id=\"closeFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert saveFile_MenuItem != null : "fx:id=\"saveFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert saveAsFile_MenuItem != null : "fx:id=\"saveAsFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert exportToCalendar_MenuItem != null : "fx:id=\"exportToCalendar_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert preferences_MenuItem != null : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert quit_MenuItem != null : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert edit_menu != null : "fx:id=\"edit_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert showNextBirthdays_MenuItem != null : "fx:id=\"showNextBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert showLastBirthdays_MenuItem != null : "fx:id=\"showLastBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert searchBirthdayMenuItem != null : "fx:id=\"searchBirthdayMenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert newBirthday_MenuItem != null : "fx:id=\"newBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert importBirthdays_MenuItem != null : "fx:id=\"importBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert deleteBirthdays_MenuItem != null : "fx:id=\"deleteBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert help_menu != null : "fx:id=\"help_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert debug != null : "fx:id=\"debug\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert refresh_MenuItem != null : "fx:id=\"refresh_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert openFileExternal_Button != null : "fx:id=\"openFileExternal_Button\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert expandRightSide_Button != null : "fx:id=\"expandRightSide_Button\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert nextBirthday_Label != null : "fx:id=\"nextBirthday_Label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert nextBdaysList != null : "fx:id=\"nextBdaysList\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert openBirthday_MenuItem != null : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert rightSide_TapView != null : "fx:id=\"rightSide_TapView\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert week_tap != null : "fx:id=\"week_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert week_tableView != null : "fx:id=\"week_tableView\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert monday_column1 != null : "fx:id=\"monday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert tuesday_column1 != null : "fx:id=\"tuesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert wednesday_column1 != null : "fx:id=\"wednesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert thursday_column1 != null : "fx:id=\"thursday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert friday_column1 != null : "fx:id=\"friday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert saturday_column1 != null : "fx:id=\"saturday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert sunday_column1 != null : "fx:id=\"sunday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert month_tap != null : "fx:id=\"month_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert monday_column2 != null : "fx:id=\"monday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert tuesday_column2 != null : "fx:id=\"tuesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert wednesday_column2 != null : "fx:id=\"wednesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert thursday_column2 != null : "fx:id=\"thursday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert friday_column2 != null : "fx:id=\"friday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert saturday_column2 != null : "fx:id=\"saturday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert sunday_column2 != null : "fx:id=\"sunday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert openedFile_label != null : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert progressbar != null : "fx:id=\"progressbar\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
        assert date_label != null : "fx:id=\"date_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    }

    /**
     * Bind EventHandlers an JavaFX-Components
     */
    private void bindComponents() {
        // Menu items
        // File
        this.openFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);
        this.openFile_MenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        this.createRecentFilesMenuItems();

        this.closeFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().closeFileHandler);
        this.saveFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().saveToFileHandler);
        this.saveFile_MenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        this.saveAsFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().exportToFileHandler);

        this.exportToCalendar_MenuItem.addEventHandler(ActionEvent.ANY, getMainController().exportToCalendarHandler);
        this.preferences_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openPreferencesHandler);

        this.quit_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().closeAppHandler);

        this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);
        this.newBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.newBirthdayHandler);
        this.newBirthday_MenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        this.importBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.importBirthdaysHandler);

        this.showNextBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.showNextBirthdaysHandler);
        this.showLastBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.showRecentBirthdaysHandler);

        // configure the List
        this.nextBdaysList.setItems(this.getMainController().getSessionInfos().getNextBirthdays());
        this.nextBdaysList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.nextBdaysList.setCellFactory(this.colorCellFactory);
        this.nextBdaysList.addEventHandler(MouseEvent.MOUSE_CLICKED, this.birthdayDoubleClickHandler);
        this.nextBdaysList.addEventHandler(KeyEvent.KEY_PRESSED, this.birthdayDoubleClickHandler);
        this.nextBdaysList.getItems().addListener((ListChangeListener<Person>) change -> {
            this.nextBdaysList.setCellFactory(null);
            this.nextBdaysList.refresh();
            this.nextBdaysList.setCellFactory(this.colorCellFactory);
            this.nextBdaysList.refresh();
        });

        // set Texts
        this.date_label.setText(DATE_FORMATTER.format(LocalDate.now()));
        this.openedFile_label.textProperty().bind(this.getMainController().getSessionInfos().getFileToOpenName());

        this.deleteBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.deletePersonHandler);
        this.deleteBirthdays_MenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));

        this.openFileExternal_Button.addEventHandler(ActionEvent.ANY, this.getMainController().openFileExternal);

        this.rightSide_TapView.visibleProperty().set(false);

        this.week_tableView.setItems(this.getMainController().getSessionInfos().getPersonsInAWeekList());
        this.monday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.MONDAY));
        this.tuesday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.TUESDAY));
        this.wednesday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.WEDNESDAY));
        this.thursday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.THURSDAY));
        this.friday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.FRIDAY));
        this.saturday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.SATURDAY));
        this.sunday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.SUNDAY));

        this.refresh_MenuItem.setOnAction(actionEvent -> this.refreshBirthdayTableView());

        this.searchBirthdayMenuItem.setOnAction(actionEvent -> this.getMainController().goToSearchView());

        this.expandRightSide_Button.setOnAction(actionEvent -> {
            if (this.expandRightSide_Button.getText().matches(">")) {
                this.expandRightSide_Button.setText("<");
                this.rightSide_TapView.visibleProperty().set(true);
                this.getMainController().getStage().setWidth(1200);
            } else {
                this.expandRightSide_Button.setText(">");
                this.rightSide_TapView.visibleProperty().set(false);
                this.getMainController().getStage().setWidth(550);
            }
        });
        getMainController().getStage().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.TAB) {
                if (BirthdaysOverviewController.this.showNextBirthdays) {
                    showNextBirthdays_MenuItem.fire();
                } else {
                    showLastBirthdays_MenuItem.fire();
                }
                BirthdaysOverviewController.this.showNextBirthdays = !BirthdaysOverviewController.this.showNextBirthdays;
                keyEvent.consume();
            }
        });
        getMainController().getSessionInfos().getRecentFileNames().addListener(listener -> {
            createRecentFilesMenuItems();
        });
    }

    private void refreshBirthdayTableView() {
        this.getMainController().getSessionInfos().updateSubLists();
        this.week_tableView.refresh();
        this.nextBdaysList.setCellFactory(null);
        this.nextBdaysList.refresh();
        this.nextBdaysList.setCellFactory(this.colorCellFactory);
        this.nextBdaysList.refresh();
    }

    /**
     *
     */
    private void createRecentFilesMenuItems() {
        // clear all menu items
        this.openRecent_MenuItem.getItems().clear();

        // create Menu Items and adding them
        RecentItems recentFileNames = getMainController().getSessionInfos().getRecentFileNames();
        for (String paths : recentFileNames.getItems()) {
            File file = new File(paths);
            MenuItem tempMenuItem = new MenuItem();
            tempMenuItem.textProperty().set(file.getName());
            tempMenuItem.addEventHandler(ActionEvent.ANY, new RecentFileEventHandler(file));
            this.openRecent_MenuItem.getItems().add(tempMenuItem);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.fxml.Initializable#initialize(java.net.URL,
     * java.util.ResourceBundle)
     */
    @FXML
    public void initialize(final URL location, final ResourceBundle resources) {
        this.getMainController().getStage().setWidth(550);

        this.assertion();

        // Localisation
        this.updateLocalisation();

        this.bindComponents();
    }

    /*
     * (non-Javadoc)
     *
     * @see application.controller.Controller#updateLocalisation()
     */
    @Override
    public void updateLocalisation() {
        final LangResourceManager resourceManager = new LangResourceManager();

        this.nextBirthday_Label.setText(resourceManager.getLocaleString(LangResourceKeys.str_nextBirthday_Label));

        this.file_menu.setText(resourceManager.getLocaleString(LangResourceKeys.file_menu));
        this.openFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
        this.openRecent_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
        this.closeFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.closeFile_MenuItem));
        this.saveFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveFile_MenuItem));
        this.saveAsFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveAsFile_MenuItem));
        this.preferences_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.preferences_MenuItem));
        this.quit_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.quit_MenuItem));

        this.edit_menu.setText(resourceManager.getLocaleString(LangResourceKeys.edit_menu));
        this.showNextBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.showNextBirthdays_MenuItem));
        this.showLastBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.showLastBirthdays_MenuItem));
        this.newBirthday_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.newBirthday_MenuItem));
        this.importBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.importBirthdays_MenuItem));
        this.deleteBirthdays_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.deleteBirthdays_MenuItem));

        this.help_menu.setText(resourceManager.getLocaleString(LangResourceKeys.help_menu));

        // List menu
        this.openBirthday_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openBirthday_MenuItem));

        this.week_tap.setText(resourceManager.getLocaleString(LangResourceKeys.week_tap));
        this.monday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.monday_column1));
        this.tuesday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.tuesday_column1));
        this.wednesday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.wednesday_column1));
        this.thursday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.thursday_column1));
        this.friday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.friday_column1));
        this.saturday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.saturday_column1));
        this.sunday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.sunday_column1));

        this.month_tap.setText(resourceManager.getLocaleString(LangResourceKeys.month_tap));
        this.monday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.monday_column2));
        this.tuesday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.tuesday_column2));
        this.wednesday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.wednesday_column2));
        this.thursday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.thursday_column2));
        this.friday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.friday_column2));
        this.saturday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.saturday_column2));
        this.sunday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.sunday_column2));
        this.exportToCalendar_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.exportMenuItem));
    }

    @Override
    public void placeFocus() {
        nextBdaysList.requestFocus();
        if (this.expandRightSide_Button.getText().matches(">")) {
            this.rightSide_TapView.visibleProperty().set(false);
            this.getMainController().getStage().setWidth(550);
        } else {
            this.rightSide_TapView.visibleProperty().set(true);
            this.getMainController().getStage().setWidth(1200);
        }
    }

    /**
     * @return the progressbar
     */
    public ProgressBar getProgressbar() {
        return progressbar;
    }

    /**
     * @param progressbar the progressbar to set
     */
    public void setProgressbar(ProgressBar progressbar) {
        this.progressbar = progressbar;
    }

    private void logAndAlertParsingError(Person.PersonCouldNotBeParsedException personCouldNotBeParsedException) {
        LOG.warn(personCouldNotBeParsedException);
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR: Parsing failed");
        alert.setContentText(personCouldNotBeParsedException.getMessage());
        alert.showAndWait();
    }

    private class RecentFileEventHandler implements EventHandler {
        File fileToOpen;

        public RecentFileEventHandler(File file) {
            this.fileToOpen = file;
        }

        @Override
        public void handle(Event event) {
            try {
                BirthdaysOverviewController.this.getMainController().openFile(fileToOpen);
            } catch (IOException ioException) {
                LOG.catching(Level.DEBUG, ioException);
            }
        }
    }
}