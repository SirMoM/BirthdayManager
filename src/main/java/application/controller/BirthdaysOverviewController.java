package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAMonthWeek;
import application.model.PersonsInAWeek;
import application.model.RecentItems;
import application.processes.LoadPersonsTask;
import application.processes.SaveBirthdaysToFileTask;
import application.util.BirthdayContextMenuFactory;
import application.util.BirthdayUtils;
import application.util.MonthTableCallback;
import application.util.MonthTableCellFactory;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.WeekTableCallback;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
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

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class BirthdaysOverviewController extends Controller {
  protected static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final Logger LOG =
      LogManager.getLogger(BirthdaysOverviewController.class.getName());
  final EventHandler<ActionEvent> newBirthdayHandler =
      event -> BirthdaysOverviewController.this.getMainController().goToEditBirthdayView();
  private final EventHandler<ActionEvent> importBirthdaysHandler =
      event -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(
            new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
        fileChooser
            .getExtensionFilters()
            .add(
                new ExtensionFilter(
                    new LangResourceManager().getLocaleString(LangResourceKeys.txt_file), "*.txt"));
        fileChooser
            .getExtensionFilters()
            .add(
                new ExtensionFilter(
                    new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), "*.csv"));
        fileChooser
            .getExtensionFilters()
            .add(
                new ExtensionFilter(
                    new LangResourceManager().getLocaleString(LangResourceKeys.all_files), "*.*"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // if the chooser is "x'ed" the file is null
        final File selectedFile =
            fileChooser.showOpenDialog(getMainController().getStage().getScene().getWindow());
        if (selectedFile == null) {
          return;
        }
        LOG.debug("Importing file: {}", selectedFile.getAbsolutePath());

        try {
          final LoadPersonsTask loadPersonsTask =
              new LoadPersonsTask(selectedFile, selectedFile.getAbsolutePath().endsWith(".csv"));
          loadPersonsTask.addEventHandler(
              WorkerStateEvent.WORKER_STATE_SUCCEEDED,
              workerStateEvent -> {
                LoadPersonsTask.Result result = loadPersonsTask.getValue();
                PersonManager.getInstance().mergePersons(result.getPersons());

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
  Callback<ListView<Person>, ListCell<Person>> colorCellFactory =
      new Callback<ListView<Person>, ListCell<Person>>() {

        @Override
        public ListCell<Person> call(final ListView<Person> param) {

          return new ListCell<Person>() {
            @Override
            protected void updateItem(final Person item, final boolean empty) {
              String firstColorString =
                  PropertyManager.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR);
              String secondColorString =
                  PropertyManager.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR);
              super.updateItem(item, empty);
              this.styleProperty().bind(new SimpleStringProperty(""));
              if (!empty && item != null) {
                final LocalDate today = LocalDate.now();
                final LocalDate birthdayThisYear =
                    BirthdayUtils.getBirthdayInYear(item.getBirthday(), today.getYear());

                if (birthdayThisYear.isEqual(today)) {
                  this.styleProperty()
                      .bind(
                          Bindings.when(this.selectedProperty())
                              .then(
                                  new SimpleStringProperty(
                                      "-fx-background-color: -fx-selection-bar;"))
                              .otherwise(
                                  new SimpleStringProperty(
                                      "-fx-background-color: " + firstColorString)));
                } else if (today.isBefore(birthdayThisYear)
                    && birthdayThisYear.isBefore(today.plusDays(7))) {

                  this.styleProperty()
                      .bind(
                          Bindings.when(this.selectedProperty())
                              .then(
                                  new SimpleStringProperty(
                                      "-fx-background-color: -fx-selection-bar;"))
                              .otherwise(
                                  new SimpleStringProperty(
                                      "-fx-background-color: " + secondColorString)));
                }
                this.setText(
                    item
                        + "\t \t \t "
                        + (new LangResourceManager().getLocaleString(LangResourceKeys.age))
                        + ":\t"
                        + (LocalDate.now().getYear() - item.getBirthday().getYear()));

              } else {
                this.setText(null);
              }
            }
          };
        }
      };
  private MenuItem recentFiles_MenuItem;
  @FXML private ResourceBundle resources;
  @FXML private URL location;
  @FXML private Menu file_menu;
  @FXML private MenuItem openFile_MenuItem;
  @FXML private Menu openRecent_MenuItem;
  @FXML private MenuItem closeFile_MenuItem;
  @FXML private MenuItem saveFile_MenuItem;
  @FXML private MenuItem saveAsFile_MenuItem;
  @FXML private MenuItem exportToCalendar_MenuItem;
  @FXML private MenuItem preferences_MenuItem;
  @FXML private MenuItem quit_MenuItem;
  @FXML private Menu edit_menu;
  @FXML private MenuItem showNextBirthdays_MenuItem;
  @FXML private MenuItem showLastBirthdays_MenuItem;
  @FXML private MenuItem newBirthday_MenuItem;
  @FXML private MenuItem importBirthdays_MenuItem;
  @FXML private MenuItem deleteBirthdays_MenuItem;
  @FXML private Menu help_menu;
  @FXML private MenuItem debug;
  @FXML private MenuItem refresh_MenuItem;
  @FXML private MenuItem about;
  @FXML private MenuItem openFileExternal_Button;
  @FXML private Button expandRightSide_Button;
  @FXML private Label nextBirthday_Label;
  @FXML private MenuItem searchBirthdayMenuItem;
  @FXML private ListView<Person> nextBdaysList;
  final EventHandler<ActionEvent> openBirthday =
      event -> {
        final ObservableList<Person> selectedItems =
            BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
          BirthdaysOverviewController.this.openBirthday(selectedItems.get(0));
        }
      };
  final EventHandler<ActionEvent> showRecentBirthdaysHandler =
      eventHandler -> {
        BirthdaysOverviewController.this.nextBdaysList.setItems(
            BirthdaysOverviewController.this
                .getMainController()
                .getSessionInfos()
                .getRecentBirthdays());
        BirthdaysOverviewController.this.nextBirthday_Label.setText(
            new LangResourceManager().getLocaleString(LangResourceKeys.str_recentBirthday_Label));
        BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
        BirthdaysOverviewController.this.nextBdaysList.setCellFactory(null);
        BirthdaysOverviewController.this.nextBdaysList.refresh();
      };
  final EventHandler<ActionEvent> showNextBirthdaysHandler =
      event -> {
        BirthdaysOverviewController.this.nextBdaysList.setItems(
            BirthdaysOverviewController.this
                .getMainController()
                .getSessionInfos()
                .getNextBirthdays());
        BirthdaysOverviewController.this.nextBirthday_Label.setText(
            new LangResourceManager().getLocaleString(LangResourceKeys.str_nextBirthday_Label));
        BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
        BirthdaysOverviewController.this.nextBdaysList.setCellFactory(
            BirthdaysOverviewController.this.colorCellFactory);
        BirthdaysOverviewController.this.nextBdaysList.refresh();
      };
  private final EventHandler<ActionEvent> deletePersonHandler =
      event -> {
        final ObservableList<Person> selectedItems =
            BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
          deleteSelectedPerson(selectedItems);
          BirthdaysOverviewController.this.getMainController().getSessionInfos().updateSubLists();

          if (Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.WRITE_THRU))) {
            final SaveBirthdaysToFileTask task =
                new SaveBirthdaysToFileTask(getMainController().getSessionInfos().getSaveFile());
            task.setOnSucceeded(
                event1 -> {
                  if (event1.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                    LOG.debug("Saved changes to file (via write thru)");
                  }
                });

            new Thread(task).start();
          }

          BirthdaysOverviewController.this.nextBdaysList.setStyle(null);
          BirthdaysOverviewController.this.nextBdaysList.setCellFactory(null);
          BirthdaysOverviewController.this.nextBdaysList.refresh();
          BirthdaysOverviewController.this.nextBdaysList.setCellFactory(
              BirthdaysOverviewController.this.colorCellFactory);
          BirthdaysOverviewController.this.nextBdaysList.refresh();
        }
      };
  @FXML private MenuItem openBirthday_MenuItem;
  @FXML private TabPane rightSide_TapView;
  @FXML private Tab week_tap;
  @FXML private TableView<PersonsInAWeek> week_tableView;
  @FXML private TableColumn<PersonsInAWeek, String> monday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> tuesday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> wednesday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> thursday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> friday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> saturday_column1;
  @FXML private TableColumn<PersonsInAWeek, String> sunday_column1;
  @FXML private Tab month_tap;
  @FXML private TableView<PersonsInAMonthWeek> month_tableView;
  @FXML private TableColumn<PersonsInAMonthWeek, String> monday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> tuesday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> wednesday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> thursday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> friday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> saturday_column2;
  @FXML private TableColumn<PersonsInAMonthWeek, String> sunday_column2;
  @FXML private Label openedFile_label;
  @FXML private Font x3;
  @FXML private Color x4;
  @FXML private Label date_label;
  @FXML private ProgressBar progressbar;

  public BirthdaysOverviewController(final MainController mainController) {
    super(mainController);
  }

  /** All assertions for the controller. Checks if all FXML-Components have been loaded properly. */
  private void assertion() {
    assert about != null
        : "fx:id=\"about\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert file_menu != null
        : "fx:id=\"file_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert openFile_MenuItem != null
        : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert openRecent_MenuItem != null
        : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert closeFile_MenuItem != null
        : "fx:id=\"closeFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert saveFile_MenuItem != null
        : "fx:id=\"saveFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert saveAsFile_MenuItem != null
        : "fx:id=\"saveAsFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert exportToCalendar_MenuItem != null
        : "fx:id=\"exportToCalendar_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert preferences_MenuItem != null
        : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert quit_MenuItem != null
        : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert edit_menu != null
        : "fx:id=\"edit_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert showNextBirthdays_MenuItem != null
        : "fx:id=\"showNextBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert showLastBirthdays_MenuItem != null
        : "fx:id=\"showLastBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert searchBirthdayMenuItem != null
        : "fx:id=\"searchBirthdayMenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert newBirthday_MenuItem != null
        : "fx:id=\"newBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert importBirthdays_MenuItem != null
        : "fx:id=\"importBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert deleteBirthdays_MenuItem != null
        : "fx:id=\"deleteBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert help_menu != null
        : "fx:id=\"help_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert debug != null
        : "fx:id=\"debug\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert refresh_MenuItem != null
        : "fx:id=\"refresh_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert openFileExternal_Button != null
        : "fx:id=\"openFileExternal_Button\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert expandRightSide_Button != null
        : "fx:id=\"expandRightSide_Button\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert nextBirthday_Label != null
        : "fx:id=\"nextBirthday_Label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert nextBdaysList != null
        : "fx:id=\"nextBdaysList\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert openBirthday_MenuItem != null
        : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert rightSide_TapView != null
        : "fx:id=\"rightSide_TapView\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert week_tap != null
        : "fx:id=\"week_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert week_tableView != null
        : "fx:id=\"week_tableView\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert monday_column1 != null
        : "fx:id=\"monday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert tuesday_column1 != null
        : "fx:id=\"tuesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert wednesday_column1 != null
        : "fx:id=\"wednesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert thursday_column1 != null
        : "fx:id=\"thursday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert friday_column1 != null
        : "fx:id=\"friday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert saturday_column1 != null
        : "fx:id=\"saturday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert sunday_column1 != null
        : "fx:id=\"sunday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert month_tap != null
        : "fx:id=\"month_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert month_tableView != null
        : "fx:id=\"month_tableView\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert monday_column2 != null
        : "fx:id=\"monday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert tuesday_column2 != null
        : "fx:id=\"tuesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert wednesday_column2 != null
        : "fx:id=\"wednesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert thursday_column2 != null
        : "fx:id=\"thursday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert friday_column2 != null
        : "fx:id=\"friday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert saturday_column2 != null
        : "fx:id=\"saturday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert sunday_column2 != null
        : "fx:id=\"sunday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert openedFile_label != null
        : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert x3 != null
        : "fx:id=\"x3\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert x4 != null
        : "fx:id=\"x4\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert progressbar != null
        : "fx:id=\"progressbar\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
    assert date_label != null
        : "fx:id=\"date_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
  }

  /** Bind EventHandlers an JavaFX-Components */
  private void bindComponents() {
    // Menu items
    // File
    this.openFile_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);
    this.createRecentFilesMenuItems();

    this.closeFile_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().closeFileHandler);
    this.saveFile_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().saveToFileHandler);
    this.saveAsFile_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().exportToFileHandler);

    this.exportToCalendar_MenuItem.addEventHandler(
        ActionEvent.ANY, getMainController().exportToCalendarHandler);
    this.preferences_MenuItem.addEventHandler(
        ActionEvent.ANY, this.getMainController().openPreferencesHandler);

    this.quit_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().closeAppHandler);

    this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);
    this.newBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.newBirthdayHandler);
    this.importBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.importBirthdaysHandler);

    this.showNextBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.showNextBirthdaysHandler);
    this.showLastBirthdays_MenuItem.addEventHandler(
        ActionEvent.ANY, this.showRecentBirthdaysHandler);

    // configure the List
    this.nextBdaysList.setItems(this.getMainController().getSessionInfos().getNextBirthdays());
    this.nextBdaysList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    this.nextBdaysList.setCellFactory(this.colorCellFactory);
    this.nextBdaysList.addEventHandler(
        MouseEvent.MOUSE_CLICKED, getMainController().birthdayDoubleClickHandler);
    this.nextBdaysList.addEventHandler(
        KeyEvent.KEY_PRESSED, getMainController().birthdayDoubleClickHandler);
    this.nextBdaysList
        .getItems()
        .addListener(
            (ListChangeListener<Person>)
                change -> {
                  this.nextBdaysList.setCellFactory(null);
                  this.nextBdaysList.refresh();
                  this.nextBdaysList.setCellFactory(this.colorCellFactory);
                  this.nextBdaysList.refresh();
                });

    // set Texts
    this.date_label.setText(DATE_FORMATTER.format(LocalDate.now()));
    this.openedFile_label
        .textProperty()
        .bind(this.getMainController().getSessionInfos().getFileToOpenName());

    this.deleteBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.deletePersonHandler);
    this.applySharedShortcutAccelerators();

    this.openFileExternal_Button.addEventHandler(
        ActionEvent.ANY, this.getMainController().openFileExternal);

    this.rightSide_TapView.visibleProperty().set(false);

    this.week_tableView.setItems(
        this.getMainController().getSessionInfos().getPersonsInAWeekList());
    this.monday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.MONDAY));
    this.monday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.MONDAY));
    this.tuesday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.TUESDAY));
    this.tuesday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.TUESDAY));
    this.wednesday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.WEDNESDAY));
    this.wednesday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.WEDNESDAY));
    this.thursday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.THURSDAY));
    this.thursday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.THURSDAY));
    this.friday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.FRIDAY));
    this.friday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.FRIDAY));
    this.saturday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.SATURDAY));
    this.saturday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.SATURDAY));
    this.sunday_column1.setCellValueFactory(new WeekTableCallback(DayOfWeek.SUNDAY));
    this.sunday_column1.setCellFactory(this.createWeekCellFactory(DayOfWeek.SUNDAY));

    this.month_tableView.setItems(
        this.getMainController().getSessionInfos().getPersonsInAMonthList());
    this.month_tableView
        .getItems()
        .addListener(
            (ListChangeListener<PersonsInAMonthWeek>) change -> this.selectCurrentMonthWeekLater());
    this.monday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.MONDAY));
    this.monday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.MONDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.tuesday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.TUESDAY));
    this.tuesday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.TUESDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.wednesday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.WEDNESDAY));
    this.wednesday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.WEDNESDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.thursday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.THURSDAY));
    this.thursday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.THURSDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.friday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.FRIDAY));
    this.friday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.FRIDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.saturday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.SATURDAY));
    this.saturday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.SATURDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.sunday_column2.setCellValueFactory(new MonthTableCallback(DayOfWeek.SUNDAY));
    this.sunday_column2.setCellFactory(
        new MonthTableCellFactory(
            DayOfWeek.SUNDAY, this::getOpenBirthdayLabel, this::openBirthday));
    this.month_tap
        .selectedProperty()
        .addListener(
            (observable, wasSelected, isSelected) -> {
              if (isSelected) {
                this.selectCurrentMonthWeekLater();
              }
            });

    this.refresh_MenuItem.setOnAction(actionEvent -> this.refreshBirthdayTableView());

    this.searchBirthdayMenuItem.setOnAction(
        actionEvent -> this.getMainController().goToSearchView());

    this.expandRightSide_Button.setOnAction(
        actionEvent -> {
          if (this.expandRightSide_Button.getText().matches(">")) {
            this.expandRightSide_Button.setText("<");
            this.rightSide_TapView.visibleProperty().set(true);
            this.getMainController().getStage().setWidth(1200);
            if (this.month_tap.isSelected()) {
              this.selectCurrentMonthWeekLater();
            }
          } else {
            this.expandRightSide_Button.setText(">");
            this.rightSide_TapView.visibleProperty().set(false);
            this.getMainController().getStage().setWidth(550);
          }
        });
    getMainController()
        .getStage()
        .addEventFilter(
            KeyEvent.KEY_PRESSED,
            keyEvent -> {
              final boolean isOverviewSceneActive =
                  getMainController().getStage().getScene() == this.nextBdaysList.getScene();
              if (shouldHandleOverviewTabShortcut(keyEvent.getCode(), isOverviewSceneActive)) {
                if (BirthdaysOverviewController.this.showNextBirthdays) {
                  showNextBirthdays_MenuItem.fire();
                } else {
                  showLastBirthdays_MenuItem.fire();
                }
                BirthdaysOverviewController.this.showNextBirthdays =
                    !BirthdaysOverviewController.this.showNextBirthdays;
                keyEvent.consume();
              }
            });
    getMainController()
        .getSessionInfos()
        .getRecentFileNames()
        .addListener(
            listener -> {
              createRecentFilesMenuItems();
            });

    about.addEventHandler(ActionEvent.ANY, actionEvent -> getMainController().goToAboutView());
  }

  private Callback<TableColumn<PersonsInAWeek, String>, TableCell<PersonsInAWeek, String>>
      createWeekCellFactory(final DayOfWeek dayOfWeek) {
    return column ->
        new TableCell<>() {
          @Override
          protected void updateItem(final String item, final boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
              this.setText(null);
              this.setContextMenu(null);
              return;
            }

            final PersonsInAWeek week =
                this.getTableRow() == null ? null : this.getTableRow().getItem();
            final Person person = getPersonForDay(week, dayOfWeek);

            this.setText(item);
            this.setContextMenu(
                BirthdayContextMenuFactory.createContextMenu(
                    person == null ? List.of() : List.of(person),
                    getOpenBirthdayLabel(),
                    BirthdaysOverviewController.this::openBirthday));
          }
        };
  }

  private Person getPersonForDay(final PersonsInAWeek week, final DayOfWeek dayOfWeek) {
    if (week == null) {
      return null;
    }

    return switch (dayOfWeek) {
      case MONDAY -> week.getMondayPerson();
      case TUESDAY -> week.getTuesdayPerson();
      case WEDNESDAY -> week.getWednesdayPerson();
      case THURSDAY -> week.getThursdayPerson();
      case FRIDAY -> week.getFridayPerson();
      case SATURDAY -> week.getSaturdayPerson();
      case SUNDAY -> week.getSundayPerson();
    };
  }

  private String getOpenBirthdayLabel() {
    return new LangResourceManager().getLocaleString(LangResourceKeys.openBirthday_MenuItem);
  }

  private void openBirthday(final Person person) {
    if (person == null) {
      return;
    }
    this.getMainController().goToEditBirthdayView(person);
  }

  private void applySharedShortcutAccelerators() {
    this.applyShortcutAccelerator(this.openFile_MenuItem, ShortcutDefinition.OPEN_FILE_ACTION);
    this.applyShortcutAccelerator(this.saveFile_MenuItem, ShortcutDefinition.SAVE_FILE_ACTION);
    this.applyShortcutAccelerator(
        this.newBirthday_MenuItem, ShortcutDefinition.NEW_BIRTHDAY_ACTION);
    this.applyShortcutAccelerator(
        this.deleteBirthdays_MenuItem, ShortcutDefinition.DELETE_BIRTHDAY_ACTION);
  }

  private void applyShortcutAccelerator(final MenuItem menuItem, final String actionId) {
    ShortcutDefinition.findByActionId(actionId)
        .ifPresent(definition -> menuItem.setAccelerator(definition.getKeyCombination()));
  }

  private void refreshBirthdayTableView() {
    this.getMainController().getSessionInfos().updateSubLists();
    this.week_tableView.refresh();
    this.month_tableView.refresh();
    this.selectCurrentMonthWeekLater();
    this.nextBdaysList.setCellFactory(null);
    this.nextBdaysList.refresh();
    this.nextBdaysList.setCellFactory(this.colorCellFactory);
    this.nextBdaysList.refresh();
  }

  static int findMonthWeekIndexForDate(
      final List<PersonsInAMonthWeek> monthWeeks, final LocalDate targetDate) {
    for (int i = 0; i < monthWeeks.size(); i++) {
      PersonsInAMonthWeek monthWeek = monthWeeks.get(i);
      for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
        if (targetDate.equals(monthWeek.getDateFor(dayOfWeek))) {
          return i;
        }
      }
    }
    return -1;
  }

  static void deleteSelectedPerson(final ObservableList<Person> selectedItems) {
    if (!selectedItems.isEmpty()) {
      PersonManager.getInstance().deletePerson(selectedItems.get(0));
    }
  }

  static boolean shouldHandleOverviewTabShortcut(
      final KeyCode keyCode, final boolean isOverviewSceneActive) {
    return keyCode == KeyCode.TAB && isOverviewSceneActive;
  }

  private void selectCurrentMonthWeekLater() {
    Platform.runLater(this::selectCurrentMonthWeek);
  }

  private void selectCurrentMonthWeek() {
    final int currentWeekIndex =
        findMonthWeekIndexForDate(this.month_tableView.getItems(), LocalDate.now());
    if (currentWeekIndex < 0) {
      this.month_tableView.getSelectionModel().clearSelection();
      return;
    }

    this.month_tableView.getSelectionModel().clearAndSelect(currentWeekIndex);
    this.month_tableView.getFocusModel().focus(currentWeekIndex);
    if (this.month_tap.isSelected()) {
      this.month_tableView.requestFocus();
      this.month_tableView.scrollTo(currentWeekIndex);
    }
  }

  /** */
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

    this.refreshBirthdayTableView();
  }

  /*
   * (non-Javadoc)
   *
   * @see application.controller.Controller#updateLocalisation()
   */
  @Override
  public void updateLocalisation() {
    final LangResourceManager resourceManager = new LangResourceManager();

    this.nextBirthday_Label.setText(
        resourceManager.getLocaleString(LangResourceKeys.str_nextBirthday_Label));

    this.file_menu.setText(resourceManager.getLocaleString(LangResourceKeys.file_menu));
    this.openFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
    this.openRecent_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
    this.closeFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.closeFile_MenuItem));
    this.saveFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.saveFile_MenuItem));
    this.saveAsFile_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.saveAsFile_MenuItem));
    this.preferences_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.preferences_MenuItem));
    this.quit_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.quit_MenuItem));

    this.edit_menu.setText(resourceManager.getLocaleString(LangResourceKeys.edit_menu));
    this.showNextBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.showNextBirthdays_MenuItem));
    this.showLastBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.showLastBirthdays_MenuItem));
    this.searchBirthdayMenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.searchBirthday_MenuItem));
    this.newBirthday_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.newBirthday_MenuItem));
    this.importBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.importBirthdays_MenuItem));
    this.deleteBirthdays_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.deleteBirthdays_MenuItem));

    this.help_menu.setText(resourceManager.getLocaleString(LangResourceKeys.help_menu));
    this.about.setText(resourceManager.getLocaleString(LangResourceKeys.about));
    this.openFileExternal_Button.setText(
        resourceManager.getLocaleString(LangResourceKeys.openFileExternal_MenuItem));
    this.refresh_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.refresh_MenuItem));
    this.debug.setText(resourceManager.getLocaleString(LangResourceKeys.shortcuts));
    // List menu
    this.openBirthday_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.openBirthday_MenuItem));

    this.week_tap.setText(resourceManager.getLocaleString(LangResourceKeys.week_tap));
    this.monday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.monday_column1));
    this.tuesday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.tuesday_column1));
    this.wednesday_column1.setText(
        resourceManager.getLocaleString(LangResourceKeys.wednesday_column1));
    this.thursday_column1.setText(
        resourceManager.getLocaleString(LangResourceKeys.thursday_column1));
    this.friday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.friday_column1));
    this.saturday_column1.setText(
        resourceManager.getLocaleString(LangResourceKeys.saturday_column1));
    this.sunday_column1.setText(resourceManager.getLocaleString(LangResourceKeys.sunday_column1));

    this.month_tap.setText(resourceManager.getLocaleString(LangResourceKeys.month_tap));
    this.monday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.monday_column2));
    this.tuesday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.tuesday_column2));
    this.wednesday_column2.setText(
        resourceManager.getLocaleString(LangResourceKeys.wednesday_column2));
    this.thursday_column2.setText(
        resourceManager.getLocaleString(LangResourceKeys.thursday_column2));
    this.friday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.friday_column2));
    this.saturday_column2.setText(
        resourceManager.getLocaleString(LangResourceKeys.saturday_column2));
    this.sunday_column2.setText(resourceManager.getLocaleString(LangResourceKeys.sunday_column2));
    this.exportToCalendar_MenuItem.setText(
        resourceManager.getLocaleString(LangResourceKeys.exportMenuItem));
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

  private void logAndAlertParsingError(
      Person.PersonCouldNotBeParsedException personCouldNotBeParsedException) {
    LOG.warn(personCouldNotBeParsedException);
    final LangResourceManager resourceManager = new LangResourceManager();
    final Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(resourceManager.getLocaleString(LangResourceKeys.parseError_Title));
    alert.setContentText(
        String.format(
            resourceManager.getLocaleString(LangResourceKeys.parseError_Message),
            personCouldNotBeParsedException.getLineNumber(),
            localizeParseErrorField(resourceManager, personCouldNotBeParsedException),
            personCouldNotBeParsedException.getLine()));
    alert.showAndWait();
  }

  private String localizeParseErrorField(
      final LangResourceManager resourceManager,
      final Person.PersonCouldNotBeParsedException personCouldNotBeParsedException) {
    return switch (personCouldNotBeParsedException.getWhatCouldNotBeParsed()) {
      case "the whole line" ->
          resourceManager.getLocaleString(LangResourceKeys.parseErrorField_wholeLine);
      case "birthday" -> resourceManager.getLocaleString(LangResourceKeys.parseErrorField_birthday);
      case "full name" ->
          resourceManager.getLocaleString(LangResourceKeys.parseErrorField_fullName);
      case "misc" -> resourceManager.getLocaleString(LangResourceKeys.parseErrorField_misc);
      default -> personCouldNotBeParsedException.getWhatCouldNotBeParsed();
    };
  }

  private class RecentFileEventHandler implements EventHandler<ActionEvent> {
    private final File fileToOpen;

    public RecentFileEventHandler(File file) {
      this.fileToOpen = file;
    }

    @Override
    public void handle(ActionEvent event) {
      try {
        BirthdaysOverviewController.this.getMainController().openFile(fileToOpen);
      } catch (IOException ioException) {
        LOG.catching(Level.DEBUG, ioException);
      }
    }
  }
}
