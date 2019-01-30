/**
 *
 */
package application.controller;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.SaveBirthdaysToFileTask;
import application.processes.SaveLastFileUsedTask;
import application.processes.UpdateAllSubBirthdayListsTask;
import application.util.PropertieFields;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

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

	final EventHandler<ActionEvent> closeFileHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			MainController.this.sessionInfos.setFileToOpen(null);
			MainController.this.sessionInfos.resetSubLists();
			PersonManager.getInstance().setSaveFile(null);
		}
	};

	final EventHandler<ActionEvent> saveToFileHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			new Thread(new SaveBirthdaysToFileTask()).start();
		}
	};

	final EventHandler<ActionEvent> exportToFileHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(MainController.this.getSessionInfos().getLangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			fileChooser.setInitialDirectory(new File(MainController.this.getSessionInfos().getConfigHandler().getPropertie(PropertieFields.LAST_OPEND).toString()).getParentFile());
			// TODO Extension Filters with internationalisation
			fileChooser.setInitialFileName("Birthdays");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			final File saveFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());

			new Thread(new SaveBirthdaysToFileTask(saveFile)).start();
		}
	};

	final EventHandler<ActionEvent> openFromFileChooserHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(MainController.this.getSessionInfos().getLangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			try{
				fileChooser.setInitialDirectory(new File(MainController.this.getSessionInfos().getConfigHandler().getPropertie(PropertieFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException){
				fileChooser.setInitialDirectory(new File("C:\\Users\\Admin"));
			}

			// TODO Extension Filters with internationalisation
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			final File selectedFile = fileChooser.showOpenDialog(MainController.this.getStage().getScene().getWindow());
			MainController.this.getSessionInfos().setFileToOpen(selectedFile);
			PersonManager.getInstance().setSaveFile(selectedFile);

			final SaveLastFileUsedTask saveLastFileUsedTask = new SaveLastFileUsedTask(MainController.this);
			saveLastFileUsedTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){

				@Override
				public void handle(final WorkerStateEvent t){
					final Boolean result = saveLastFileUsedTask.getValue();
					if(result){
						MainController.this.LOG.info("Saved recent succsesfully");
					} else{
						MainController.this.LOG.error("Saveing recent faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();
			new Thread(new UpdateAllSubBirthdayListsTask(MainController.this.sessionInfos)).start();
		}
	};

	final EventHandler<ActionEvent> openFromRecentHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			final String lastUsedFilePath = MainController.this.getSessionInfos().getConfigHandler().getPropertie(PropertieFields.LAST_OPEND).toString();
			MainController.this.LOG.debug(lastUsedFilePath);
			final File birthdayFile = new File(lastUsedFilePath);

			MainController.this.getSessionInfos().setFileToOpen(birthdayFile);
			PersonManager.getInstance().setSaveFile(birthdayFile);

			final SaveLastFileUsedTask saveLastFileUsedTask = new SaveLastFileUsedTask(MainController.this);
			saveLastFileUsedTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
				@Override
				public void handle(final WorkerStateEvent t){
					final Boolean result = saveLastFileUsedTask.getValue();
					if(result){
						MainController.this.LOG.info("Saved recent succsesfully");
					} else{
						MainController.this.LOG.error("Saveing recent faild");
					}
				}
			});
			new Thread(saveLastFileUsedTask).start();
			new Thread(new UpdateAllSubBirthdayListsTask(MainController.this.sessionInfos)).start();
		}
	};

	public MainController(final Stage stage){
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

		} catch (final Exception ex){
			this.LOG.error(ex.getMessage());
			this.LOG.error(ex.getStackTrace().toString());
		}
	}

	public void goToBirthdaysOverview(final BirthdaysOverviewController birthdaysOverviewController){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", birthdaysOverviewController);

		} catch (final Exception ex){
			this.LOG.error(ex.getMessage());
			this.LOG.error(ex.getStackTrace().toString());
		}
	}

	public void goToEditBirthdayView(final Person person){
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", new EditBirthdayViewController(this, person));

		} catch (final Exception ex){
			this.LOG.error(ex.getMessage());
			this.LOG.error(ex.getStackTrace().toString());
		}
	}

	public void gotoNextScene(final String fxmlPath, final Initializable controller){
		try{
			this.replaceSceneContent(fxmlPath, controller);
		} catch (final Exception ex){
			this.LOG.error(ex.getMessage());
			this.LOG.error(ex.getStackTrace().toString());
		}
	}

	private void replaceSceneContent(final String fxmlPath, final Initializable controller) throws Exception{
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(fxmlPath));
		loader.setController(controller);
		final Parent root = loader.load();
		final Scene scene = new Scene(root);
		scene.getStylesheets().add("test.css");
		this.stage.setScene(scene);

		// Show the GUI
		this.stage.show();
	}

	public void setStage(final Stage stage){
		this.stage = stage;
	}

	public void start(){
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", new BirthdaysOverviewController(this));

		} catch (final Exception ex){
			this.LOG.error(ex.getMessage());
			this.LOG.error(ex.getStackTrace().toString());
		}
	}

}
