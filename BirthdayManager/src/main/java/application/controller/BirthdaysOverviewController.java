/**
 * 
 */
package application.controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import application.model.Person;
import application.processes.LoadBirthdaysFromFileTask;
import application.processes.SaveLastFileUsedTask;
import application.util.ConfigFields;
import application.util.ConfigHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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

	MenuItem recentFilesMenuItem;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // fx:id="openRecent_MenuItem"
	private Menu openRecent_MenuItem; // Value injected by FXMLLoader

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="openFileMenuItem"
	private MenuItem openFileMenuItem; // Value injected by FXMLLoader

	@FXML // fx:id="nextBirthdayLabel"
	private Label nextBirthdayLabel; // Value injected by FXMLLoader

	@FXML // fx:id="nextBdaysList"
	private ListView<Person> nextBdaysList; // Value injected by FXMLLoader

	@FXML // fx:id="openBirthday_MenuItem"
	private MenuItem openBirthday_MenuItem; // Value injected by FXMLLoader

	@FXML // fx:id="openedFile_label"
	private Label openedFile_label; // Value injected by FXMLLoader

	@FXML // fx:id="x3"
	private Font x3; // Value injected by FXMLLoader

	@FXML // fx:id="x4"
	private Color x4; // Value injected by FXMLLoader

	@FXML // fx:id="date_label"
	private Label date_label; // Value injected by FXMLLoader

	final EventHandler<ActionEvent> openFromFileChooserHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(ActionEvent event){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
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
						System.out.println("Saved succsesfully");
						BirthdaysOverviewController.this.recentFilesMenuItem.setText(selectedFile.getName());
					} else{
						System.out.println("Saveing  faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();

			LoadBirthdaysFromFileTask loadBirthdaysFromFileTask = new LoadBirthdaysFromFileTask(BirthdaysOverviewController.this.getMainController());
			loadBirthdaysFromFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					List<Person> result = loadBirthdaysFromFileTask.getValue();
					BirthdaysOverviewController.this.getMainController().getSessionInfos().getAllPersons().addAll(result);
					BirthdaysOverviewController.this.openedFile_label.setText(selectedFile.getName());
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
						System.out.println("Saved succsesfully");
						BirthdaysOverviewController.this.recentFilesMenuItem.setText(birthdayFile.getName());
					} else{
						System.out.println("Saveing  faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();

			LoadBirthdaysFromFileTask loadBirthdaysFromFileTask = new LoadBirthdaysFromFileTask(BirthdaysOverviewController.this.getMainController());
			loadBirthdaysFromFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					List<Person> result = loadBirthdaysFromFileTask.getValue();
					BirthdaysOverviewController.this.getMainController().getSessionInfos().getAllPersons().addAll(result);
					BirthdaysOverviewController.this.openedFile_label.setText(birthdayFile.getName());
				}
			});
			new Thread(loadBirthdaysFromFileTask).start();
		}
	};

	public BirthdaysOverviewController(MainController mainController){
		super(mainController);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources){
		assert this.openFileMenuItem != null : "fx:id=\"openFileMenuItem\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.nextBirthdayLabel != null : "fx:id=\"nextBirthdayLabel\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'Kalender.fxml'.";
		assert this.x4 != null : "fx:id=\"x4\" was not injected: check your FXML file 'Kalender.fxml'.";

		this.openFileMenuItem.addEventHandler(ActionEvent.ANY, this.openFromFileChooserHandler);
		this.nextBdaysList.setItems(this.getMainController().getSessionInfos().getAllPersons());
		this.date_label.setText(DATE_FORMATTER.format(LocalDate.now()));
		String property = null;
		try{
			property = new ConfigHandler().getProperties().getProperty(ConfigFields.LAST_OPEND);
			this.recentFilesMenuItem = new MenuItem(new File(property).getName());
			this.recentFilesMenuItem.addEventHandler(ActionEvent.ANY, this.openFromRecentHandler);
			this.openRecent_MenuItem.getItems().add(this.recentFilesMenuItem);

		} catch (Exception e){
			e.printStackTrace();
		}
		;

	}
}