/**
 *
 */
package application.controller;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.SaveBirthdaysToFileTask;
import application.processes.SaveLastFileUsedTask;
import application.util.PropertieFields;
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
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class MainController{
	private final Logger LOG;
	private final Stage stage;
	private final SessionInfos sessionInfos;
	private Controller activeController = null;

	@FXML
	private MenuItem changeLanguage_MenuItem;

	final EventHandler<ActionEvent> closeAppHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			Platform.exit();
		}

	};

	final EventHandler<ActionEvent> closeFileHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			MainController.this.sessionInfos.resetSubLists();
			PersonManager.getInstance().setSaveFile(null);
		}
	};
	final EventHandler<ActionEvent> openPreferencesHander = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			MainController.this.openPreferences();
			MainController.this.LOG.trace("Open Preferences");
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
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			try{
				fileChooser.setInitialDirectory(new File(MainController.this.getSessionInfos().getPropertiesHandler().getPropertie(PropertieFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException){
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
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
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			try{
				fileChooser.setInitialDirectory(new File(MainController.this.getSessionInfos().getPropertiesHandler().getPropertie(PropertieFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException){
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}

			// TODO Extension Filters with internationalisation
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"), new ExtensionFilter("CSV Files", "*.csv"), new ExtensionFilter("All Files", "*.*"));

			final File selectedFile = fileChooser.showOpenDialog(MainController.this.getStage().getScene().getWindow());
			MainController.this.LOG.debug("Opend file:" + selectedFile.getAbsolutePath());
			PersonManager.getInstance().setSaveFile(selectedFile);
			MainController.this.sessionInfos.getRecentFileName().set(selectedFile.getName());

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
			MainController.this.sessionInfos.updateSubLists();
		}
	};
	final EventHandler<ActionEvent> openFromRecentHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			final String lastUsedFilePath = MainController.this.getSessionInfos().getPropertiesHandler().getPropertie(PropertieFields.LAST_OPEND).toString();
			MainController.this.LOG.debug("Saved as recend used: " + lastUsedFilePath);
			final File birthdayFile = new File(lastUsedFilePath);

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
			MainController.this.getSessionInfos().updateSubLists();
		}
	};

	/**
	 * @param stage the mainstage for the application
	 */
	public MainController(final Stage stage){
		this.stage = stage;
		this.sessionInfos = new SessionInfos();
		this.LOG = LogManager.getLogger(this.getClass().getName());
	}

	/**
	 * @return the session infos for the spezific App-Instance
	 */
	public SessionInfos getSessionInfos(){
		return this.sessionInfos;
	}

	/**
	 * @return the main stage of this app
	 */
	public Stage getStage(){
		return this.stage;
	}

	/**
	 * Swiches scenes to the BirthdayOverview. Generates a new Controller.
	 *
	 * @see BirthdaysOverviewController
	 */
	public void goToBirthdaysOverview(){
		this.activeController = new BirthdaysOverviewController(this);
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.activeController);

		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

	/**
	 * Swiches scenes to the BirthdayOverview. Generates a new Controller.
	 *
	 * @see EditBirthdayViewController
	 */
	public void goToEditBirthdayView(){
		this.activeController = new NewBirthdayViewController(this);
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.activeController);
		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

	/**
	 * Switches scenes to the BirthdayOverview. Generates a new Controller.
	 *
	 * @param person The person to edit.
	 *
	 * @see EditBirthdayViewController
	 */
	public void goToEditBirthdayView(final Person person){
		this.activeController = new EditBirthdayViewController(this, person);
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.activeController);

		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

	/**
	 * @param fxmlPath   the path of the FXML-File representing the view
	 * @param controller the associated Controller
	 */
	public void gotoNextScene(final String fxmlPath, final Initializable controller){
		try{
			this.replaceSceneContent(fxmlPath, controller);
		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

	/**
	 * Opens the preferences Window.
	 * <ul>
	 * <li>Creates a new {@link Stage}</li>
	 * <li>Create a new {@link Scene}</li>
	 * <li>Create a new {@link PreferencesViewController}</li>
	 * </ul>
	 *
	 */
	public void openPreferences(){
		try{
			final FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(this.getClass().getResource("/application/view/PreferencesView.fxml"));
			fxmlLoader.setController(new PreferencesViewController(this));
			final Scene scene = new Scene(fxmlLoader.load());
			final Stage stage = new Stage();
			stage.setTitle("Preferences");
			stage.setScene(scene);
			stage.show();
		} catch (final IOException ioException){
			this.LOG.log(Level.ERROR, "Failed to create new Window.", ioException);
		}
	}

	/**
	 * @param fxmlPath   the path of the FXML-File representing the view
	 * @param controller the associated Controller
	 * @throws IOException if the FXML-File could not be loaded
	 */
	private void replaceSceneContent(final String fxmlPath, final Initializable controller) throws IOException{
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

	public void settingsChanged(){
		this.activeController.updateLocalisation();
	}

	/**
	 * Starts the application with the BirthdaysOverview and possibly loaded file
	 */
	public void start(){
		this.activeController = new BirthdaysOverviewController(this);
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.activeController);
			if(new Boolean(this.sessionInfos.getPropertiesHandler().getPropertie(PropertieFields.OPEN_FILE_ON_START))){
				PersonManager.getInstance().setSaveFile(new File(this.sessionInfos.getPropertiesHandler().getPropertie(PropertieFields.FILE_ON_START)));
				this.sessionInfos.updateSubLists();
				this.LOG.debug("OPEN with File");
			}
		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

}
