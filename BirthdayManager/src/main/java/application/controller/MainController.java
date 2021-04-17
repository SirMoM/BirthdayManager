/**
 * 
 */
package application.controller;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.SessionInfos;
import application.processes.LoadBirthdaysFromFileTask;
import application.processes.SaveLastFileUsedTask;
import application.util.ConfigFields;
import application.util.localisation.LangResourceKeys;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Noah Ruben
 *
 */
public class MainController{

	private final Logger LOG = LogManager.getLogger(this.getClass().getName());
	private Stage stage;
	private final SessionInfos sessionInfos;

	@FXML
	private MenuItem changeLanguage_MenuItem;

	final EventHandler<ActionEvent> openFromFileChooserHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(ActionEvent event){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(MainController.this.getSessionInfos().getLangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			fileChooser.setInitialDirectory(new File(MainController.this.getSessionInfos().getConfigHandler().getPropertie(ConfigFields.LAST_OPEND).toString()).getParentFile());

			// TODO Extension Filters with internationalisation
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			File selectedFile = fileChooser.showOpenDialog(MainController.this.getStage().getScene().getWindow());
			MainController.this.getSessionInfos().setFileToOpen(selectedFile);

			SaveLastFileUsedTask saveLastFileUsedTask = new SaveLastFileUsedTask(MainController.this);
			saveLastFileUsedTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){

				@Override
				public void handle(WorkerStateEvent t){
					Boolean result = saveLastFileUsedTask.getValue();
					if(result){
						MainController.this.LOG.debug("Saved recent succsesfully");
					} else{
						MainController.this.LOG.debug("Saveing recent faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();

			LoadBirthdaysFromFileTask loadBirthdaysFromFileTask = new LoadBirthdaysFromFileTask(MainController.this);
			loadBirthdaysFromFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(WorkerStateEvent t){
					List<Person> result = loadBirthdaysFromFileTask.getValue();
					MainController.this.getSessionInfos().getAllPersons().addAll(result);
					if(result.isEmpty()){
						MainController.this.LOG.debug("Failed loading birthdays");
					} else{
						MainController.this.LOG.debug("Loaded birthdays from file");
					}
				}
			});
			new Thread(loadBirthdaysFromFileTask).start();
		}
	};

	public MainController(Stage stage){
		this.stage = stage;
		this.sessionInfos = new SessionInfos();
	}

	public SessionInfos getSessionInfos(){
		return this.sessionInfos;
	}

	public Stage getStage(){
		return this.stage;
	}

	public void goToBirthdaysOverview(){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", new BirthdaysOverviewController(this));

		} catch (Exception ex){
			System.out.println("BirthdaysOverview: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void goToBirthdaysOverview(BirthdaysOverviewController birthdaysOverviewController){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", birthdaysOverviewController);

		} catch (Exception ex){
			System.out.println("BirthdaysOverview: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void goToEditBirthdayView(Person person){
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", new EditBirthdayViewController(this, person));

		} catch (Exception ex){
			System.out.println("EditBirthdayView: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void gotoNextScene(String fxmlPath, Initializable controller){
		try{
			this.replaceSceneContent(fxmlPath, controller);
		} catch (Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public void setStage(Stage stage){
		this.stage = stage;
	}

	public void start(){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", new BirthdaysOverviewController(this));

		} catch (Exception ex){
			System.out.println("BirthdaysOverview: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void replaceSceneContent(String fxmlPath, Initializable controller) throws Exception{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(fxmlPath));
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add("test.css");
		this.stage.setScene(scene);

		// Show the GUI
		this.stage.show();
	}

}
