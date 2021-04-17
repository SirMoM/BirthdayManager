/**
 * 
 */
package application.controller;

import java.io.File;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.model.Person;
import application.processes.LoadBirthdaysFromFileTask;
import application.processes.SaveLastFileUsedTask;
import application.processes.UpdateAllSubBirthdayListsTask;
import application.util.ConfigFields;
import application.util.ConfigHandler;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class BirthdaysOverviewController extends Controller{
	protected final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private MenuItem recentFiles_MenuItem;

	@FXML
	private TableView<Person> week_tableView;

	@FXML
	private MenuItem changeLanguage_MenuItem;

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
	private MenuItem exporteFile_MenuItem;

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
	private Label nextBirthday_Label;

	@FXML
	private ListView<Person> nextBdaysList;

	@FXML
	private MenuItem openBirthday_MenuItem;

	@FXML
	private Tab week_tap;

	@FXML
	private TableColumn<DayOfWeek, Person> monday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> tuesday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> wednesday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> thursday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> friday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> saturday_column1;

	@FXML
	private TableColumn<DayOfWeek, Person> sunday_column1;

	@FXML
	private Tab month_tap;

	@FXML
	private TableColumn<?, ?> monday_column2;

	@FXML
	private TableColumn<?, ?> tuesday_column2;

	@FXML
	private TableColumn<?, ?> wednesday_column2;

	@FXML
	private TableColumn<?, ?> thursday_column2;

	@FXML
	private TableColumn<?, ?> friday_column2;

	@FXML
	private TableColumn<?, ?> saturday_column2;

	@FXML
	private TableColumn<?, ?> sunday_column2;

	@FXML
	private Label openedFile_label;

	@FXML
	private Font x3;

	@FXML
	private Color x4;

	@FXML
	private Label date_label;

	final EventHandler<ActionEvent> openBirthday = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent arg0){
			ObservableList<Person> selectedItems = BirthdaysOverviewController.this.nextBdaysList.getSelectionModel().getSelectedItems();
			if(selectedItems.isEmpty()){
				return;
			} else{
				BirthdaysOverviewController.this.getMainController().goToEditBirthdayView(selectedItems.get(0));
			}
		}
	};

	final EventHandler<ActionEvent> openFromFileChooserHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(ActionEvent event){
			FileChooser fileChooser = new FileChooser();
			// TODO international
			fileChooser.setTitle("Open Resource File");
			// TODO mit dem letzten Dir
			fileChooser.setInitialDirectory(new File("./src/main/java/application/model/data/Geburtstage.txt").getParentFile());
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			File selectedFile = fileChooser.showOpenDialog(BirthdaysOverviewController.this.getMainController().getStage().getScene().getWindow());
			BirthdaysOverviewController.this.getMainController().getSessionInfos().setFileToOpen(selectedFile);
			SaveLastFileUsedTask saveLastFileUsedTask = new SaveLastFileUsedTask(BirthdaysOverviewController.this.getMainController());
			saveLastFileUsedTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					Boolean result = saveLastFileUsedTask.getValue();
					if(result){
						System.out.println("Saved recent succsesfully");
						BirthdaysOverviewController.this.recentFiles_MenuItem.setText(selectedFile.getName());
					} else{
						System.out.println("Saveing recent faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();

			LoadBirthdaysFromFileTask loadBirthdaysFromFileTask = new LoadBirthdaysFromFileTask(BirthdaysOverviewController.this.getMainController());
			loadBirthdaysFromFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					List<Person> result = loadBirthdaysFromFileTask.getValue();
					BirthdaysOverviewController.this.getMainController().getSessionInfos().setAllPersons(result);
					BirthdaysOverviewController.this.openedFile_label.setText(selectedFile.getName());
					if(result.isEmpty()){
						System.out.println("failed");
					} else{
						System.out.println("loaded birthdays from file");
						UpdateAllSubBirthdayListsTask updateAllSubBirthdayLists = new UpdateAllSubBirthdayListsTask(BirthdaysOverviewController.this.getMainController().getSessionInfos());
						new Thread(updateAllSubBirthdayLists).start();
					}
				}
			});
			new Thread(loadBirthdaysFromFileTask).start();
		}
	};
	final EventHandler<ActionEvent> openFromRecentHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(ActionEvent event){
			String lastUsedFilePath = BirthdaysOverviewController.this.getMainController().getSessionInfos().getConfigHandler().getProperties().getProperty(ConfigFields.LAST_OPEND);
			System.out.println(lastUsedFilePath);
			File birthdayFile = new File(lastUsedFilePath);
			BirthdaysOverviewController.this.getMainController().getSessionInfos().setFileToOpen(birthdayFile);

			SaveLastFileUsedTask saveLastFileUsedTask = new SaveLastFileUsedTask(BirthdaysOverviewController.this.getMainController());
			saveLastFileUsedTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					Boolean result = saveLastFileUsedTask.getValue();
					if(result){
						System.out.println("Saved recent succsesfully");
						BirthdaysOverviewController.this.recentFiles_MenuItem.setText(birthdayFile.getName());
					} else{
						System.out.println("Saveing recent faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();

			LoadBirthdaysFromFileTask loadBirthdaysFromFileTask = new LoadBirthdaysFromFileTask(BirthdaysOverviewController.this.getMainController());
			loadBirthdaysFromFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					List<Person> result = loadBirthdaysFromFileTask.getValue();
					BirthdaysOverviewController.this.getMainController().getSessionInfos().setAllPersons(result);
					BirthdaysOverviewController.this.openedFile_label.setText(birthdayFile.getName());
					if(result.isEmpty()){
						System.out.println("failed");
					} else{
						System.out.println("loaded birthdays from file");
						UpdateAllSubBirthdayListsTask updateAllSubBirthdayLists = new UpdateAllSubBirthdayListsTask(BirthdaysOverviewController.this.getMainController().getSessionInfos());
						new Thread(updateAllSubBirthdayLists).start();
					}
				}
			});
			new Thread(loadBirthdaysFromFileTask).start();
		}
	};

	final EventHandler<ActionEvent> updateListsHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(ActionEvent event){
			UpdateAllSubBirthdayListsTask updateAllSubBirthdayLists = new UpdateAllSubBirthdayListsTask(BirthdaysOverviewController.this.getMainController().getSessionInfos());
			updateAllSubBirthdayLists.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					Boolean result = updateAllSubBirthdayLists.getValue();
					if(result){
						System.out.println("Saved succsesfully");
					} else{
						System.out.println("Saveing faild");
					}
				}
			});
			new Thread(updateAllSubBirthdayLists).start();
		}
	};

	final EventHandler<ActionEvent> showRecentBirthdaysHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
			BirthdaysOverviewController.this.nextBdaysList.setItems(BirthdaysOverviewController.this.getMainController().getSessionInfos().getRecentBirthdays());
			BirthdaysOverviewController.this.nextBdaysList.refresh();
			BirthdaysOverviewController.this.nextBirthday_Label.setText(BirthdaysOverviewController.this.getMainController().getSessionInfos().getLangResourceManager().getLocaleString(LangResourceKeys.str_recentBirthday_Label));
		}
	};

	final EventHandler<ActionEvent> showNextBirthdaysHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(ActionEvent event){
			BirthdaysOverviewController.this.nextBdaysList.setItems(BirthdaysOverviewController.this.getMainController().getSessionInfos().getNextBirthdays());
			BirthdaysOverviewController.this.nextBdaysList.refresh();
			BirthdaysOverviewController.this.nextBirthday_Label.setText(BirthdaysOverviewController.this.getMainController().getSessionInfos().getLangResourceManager().getLocaleString(LangResourceKeys.str_nextBirthday_Label));
		}
	};

	private EventHandler<ActionEvent> changeLanguageHandler = new EventHandler<ActionEvent>(){
		// TODO this is shit
		@Override
		public void handle(ActionEvent event){
			BirthdaysOverviewController.this.getMainController().getSessionInfos().setAppLocale(Locale.getDefault());
			BirthdaysOverviewController.this.getMainController().getSessionInfos().getLangResourceManager().changeLocale(new Locale("de", "DE"));
			BirthdaysOverviewController.this.updateLocalisation();
		}
	};

	public BirthdaysOverviewController(MainController mainController){
		super(mainController);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources){
		assert this.file_menu != null : "fx:id=\"file_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.openFile_MenuItem != null : "fx:id=\"openFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.openRecent_MenuItem != null : "fx:id=\"openRecent_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.closeFile_MenuItem != null : "fx:id=\"closeFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.saveFile_MenuItem != null : "fx:id=\"saveFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.saveAsFile_MenuItem != null : "fx:id=\"saveAsFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.exporteFile_MenuItem != null : "fx:id=\"exporteFile_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.preferences_MenuItem != null : "fx:id=\"preferences_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.quit_MenuItem != null : "fx:id=\"quit_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.edit_menu != null : "fx:id=\"edit_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.showNextBirthdays_MenuItem != null : "fx:id=\"showNextBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.showLastBirthdays_MenuItem != null : "fx:id=\"showLastBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.newBirthday_MenuItem != null : "fx:id=\"newBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.importBirthdays_MenuItem != null : "fx:id=\"importBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.deleteBirthdays_MenuItem != null : "fx:id=\"deleteBirthdays_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.help_menu != null : "fx:id=\"help_menu\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.debug != null : "fx:id=\"debug\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.refresh_MenuItem != null : "fx:id=\"refresh_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.nextBirthday_Label != null : "fx:id=\"nextBirthdayLabel\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.nextBdaysList != null : "fx:id=\"nextBdaysList\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.openBirthday_MenuItem != null : "fx:id=\"openBirthday_MenuItem\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.week_tap != null : "fx:id=\"week_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.monday_column1 != null : "fx:id=\"monday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.tuesday_column1 != null : "fx:id=\"tuesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.wednesday_column1 != null : "fx:id=\"wednesday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.thursday_column1 != null : "fx:id=\"thursday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.friday_column1 != null : "fx:id=\"friday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.saturday_column1 != null : "fx:id=\"saturday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.sunday_column1 != null : "fx:id=\"sunday_column1\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.month_tap != null : "fx:id=\"month_tap\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.monday_column2 != null : "fx:id=\"monday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.tuesday_column2 != null : "fx:id=\"tuesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.wednesday_column2 != null : "fx:id=\"wednesday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.thursday_column2 != null : "fx:id=\"thursday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.friday_column2 != null : "fx:id=\"friday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.saturday_column2 != null : "fx:id=\"saturday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.sunday_column2 != null : "fx:id=\"sunday_column2\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.openedFile_label != null : "fx:id=\"openedFile_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";
		assert this.date_label != null : "fx:id=\"date_label\" was not injected: check your FXML file 'BirthdaysOverview.fxml'.";

		// Localisation
		this.updateLocalisation();

		// EventHandlers

		this.openBirthday_MenuItem.addEventHandler(ActionEvent.ANY, this.openBirthday);
		this.debug.addEventHandler(ActionEvent.ACTION, this.updateListsHandler);
		this.refresh_MenuItem.addEventHandler(ActionEvent.ANY, this.updateListsHandler);

		this.showNextBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.showNextBirthdaysHandler);
		this.showLastBirthdays_MenuItem.addEventHandler(ActionEvent.ANY, this.showRecentBirthdaysHandler);

		this.nextBdaysList.setItems(this.getMainController().getSessionInfos().getNextBirthdays());

		this.date_label.setText(DATE_FORMATTER.format(LocalDate.now()));
		this.nextBdaysList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		this.openFile_MenuItem.addEventHandler(ActionEvent.ANY, this.getMainController().openFromFileChooserHandler);
		this.changeLanguage_MenuItem.addEventHandler(ActionEvent.ANY, this.changeLanguageHandler);
		String property = null;
		try{
			property = new ConfigHandler().getProperties().getProperty(ConfigFields.LAST_OPEND);
			this.recentFiles_MenuItem = new MenuItem(new File(property).getName());
			this.recentFiles_MenuItem.addEventHandler(ActionEvent.ANY, this.openFromRecentHandler);
			this.openRecent_MenuItem.getItems().add(this.recentFiles_MenuItem);

		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private void updateLocalisation(){
		LangResourceManager resourceManager = this.getMainController().getSessionInfos().getLangResourceManager();

		this.nextBirthday_Label.setText(resourceManager.getLocaleString(LangResourceKeys.str_nextBirthday_Label));

		this.file_menu.setText(resourceManager.getLocaleString(LangResourceKeys.file_menu));
		this.openFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openFile_MenuItem));
		this.openRecent_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.openRecent_MenuItem));
		this.closeFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.closeFile_MenuItem));
		this.saveFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveFile_MenuItem));
		this.saveAsFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.saveAsFile_MenuItem));
		this.exporteFile_MenuItem.setText(resourceManager.getLocaleString(LangResourceKeys.exporteFile_MenuItem));
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
	}
}