/**
 *
 */
package application.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.CheckMissedBirthdays;
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
import javafx.event.Event;
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

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class MainController{
	private final Logger LOG;
	private final Stage stage;
	private final SessionInfos sessionInfos;
	private Controller activeController = null;

	@FXML
	private MenuItem changeLanguage_MenuItem;

	@SuppressWarnings("rawtypes")
	final EventHandler closeAppHandler = new EventHandler<Event>(){
		@Override
		public void handle(final Event event){
			final boolean autosave = Boolean.parseBoolean(PropertyManager.getProperty(PropertyFields.AUTOSAVE));

			if(PersonManager.getInstance().getPersons().isEmpty()){
				Platform.exit();
				System.exit(0);
			}

			if(autosave && MainController.this.getSessionInfos().getSaveFile() != null){
				SaveBirthdaysToFileTask saveBirthdaysToFileTask = new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile());
				saveBirthdaysToFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent event) {
						Platform.exit();
						System.exit(0);
					}
				});
				new Thread(saveBirthdaysToFileTask).start();
			} else{
				MainController.this.LOG.debug(MainController.this.getSessionInfos().getSaveFile());
				final LangResourceManager lRM = new LangResourceManager();
				final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle(lRM.getLocaleString(LangResourceKeys.save_before_exit));
				alert.setContentText(lRM.getLocaleString(LangResourceKeys.save_before_exit_question));

				final ButtonType okButton = new ButtonType(lRM.getLocaleString(LangResourceKeys.yes), ButtonBar.ButtonData.YES);
				final ButtonType noButton = new ButtonType(lRM.getLocaleString(LangResourceKeys.no), ButtonBar.ButtonData.NO);
				alert.getButtonTypes().setAll(okButton, noButton);

				alert.showAndWait().ifPresent(type -> {
					if(type.getButtonData() == ButtonType.YES.getButtonData()){
						if(MainController.this.getSessionInfos().getSaveFile() == null){
							final FileChooser fileChooser = new FileChooser();
							fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
							fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), "*.csv"));
							try{
								fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString()).getParentFile());
							} catch (final NullPointerException nullPointerException){
								fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
							}

							// if the chooser is "x'ed" the file is null
							final File selectedFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());
							if(selectedFile == null){
								final Alert error = new Alert(Alert.AlertType.ERROR);
								error.showAndWait();
								return;
							} else{
								MainController.this.getSessionInfos().setSaveFile(selectedFile);
								SaveBirthdaysToFileTask saveBirthdaysToFileTask = new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile());
								saveBirthdaysToFileTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {

									@Override
									public void handle(WorkerStateEvent event) {
										Platform.exit();
										System.exit(0);
									}
								});
								new Thread(saveBirthdaysToFileTask).start();
							}

						}
					}
				});
			}
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
			if(MainController.this.getSessionInfos().getSaveFile() == null){
				final FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
				fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), "*.csv"));
				try{
					fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString()).getParentFile());
				} catch (final NullPointerException nullPointerException){
					fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				}

				// if the chooser is "x'ed" the file is null
				final File selectedFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());
				MainController.this.getSessionInfos().setSaveFile(selectedFile);

			}
			if(MainController.this.getSessionInfos().getSaveFile() == null){
				final Alert error = new Alert(Alert.AlertType.ERROR);
				error.showAndWait();
				return;
			} else{
				new Thread(new SaveBirthdaysToFileTask(MainController.this.getSessionInfos().getSaveFile())).start();
			}
		}
	};
	final EventHandler<ActionEvent> exportToFileHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			try{
				fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString()).getParentFile());
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
	final EventHandler<ActionEvent> exportToCalendarHandler = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));

			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			fileChooser.setInitialFileName("birthdays.ics");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Calendars", "*.ics"));

			final File saveFile = fileChooser.showSaveDialog(MainController.this.getStage().getScene().getWindow());
			if(saveFile == null){
				return;
			}
			ExportToCalenderTask exportToCalenderTask = null;
			try{
				exportToCalenderTask = new ExportToCalenderTask(saveFile);
				exportToCalenderTask.setOnSucceeded(x -> {
					MainController.this.LOG.debug("EXPORTED");
					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Exported");
					alert.setHeaderText("All bithdays have been exported to " + saveFile.getAbsolutePath() + "!");
					alert.showAndWait();
				});
				if(MainController.this.getActiveController() instanceof BirthdaysOverviewController){
					((BirthdaysOverviewController) MainController.this.getActiveController()).getProgressbar().progressProperty().bind(exportToCalenderTask.workDoneProperty());

				}
			} catch (final IOException ioException){
				MainController.this.LOG.catching(Level.ERROR, ioException);
			}
			if(exportToCalenderTask != null){
				new Thread(exportToCalenderTask).start();
			}
		}
	};
	final EventHandler<ActionEvent> openFromFileChooserHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(new LangResourceManager().getLocaleString(LangResourceKeys.fileChooserCaption));
			fileChooser.getExtensionFilters().add(new ExtensionFilter(new LangResourceManager().getLocaleString(LangResourceKeys.csv_file), "*.csv"));

			try{
				fileChooser.setInitialDirectory(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString()).getParentFile());
			} catch (final NullPointerException nullPointerException){
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}

			// if the chooser is "x'ed" the file is null
			final File selectedFile = fileChooser.showOpenDialog(MainController.this.getStage().getScene().getWindow());
			if(selectedFile == null){
				return;
			}
			try{ // TODO REDO THIS OPEN SHIT to not throw something
				MainController.this.openFile(selectedFile);
			} catch (final IOException ioException){
				MainController.this.LOG.catching(ioException);
			}
		}
	};
	final EventHandler<ActionEvent> openFromRecentHandler = new EventHandler<ActionEvent>(){
		@Override
		public void handle(final ActionEvent event){
			final String lastUsedFilePath = PropertyManager.getProperty(PropertyFields.LAST_OPEND).toString();
			final File birthdayFile = new File(lastUsedFilePath);

			try{
				MainController.this.openFile(birthdayFile);
			} catch (final IOException ioException){
				MainController.this.LOG.catching(ioException);
			}
		}
	};
	public EventHandler<ActionEvent> openFileExternal = new EventHandler<ActionEvent>(){

		@Override
		public void handle(final ActionEvent event){
			// first check if Desktop is supported by Platform or not
			if(!Desktop.isDesktopSupported()){
				MainController.this.LOG.debug("Desktop is not supported");
				return;
			}

			final File file = PersonManager.getInstance().getSaveFile();

			final Desktop desktop = Desktop.getDesktop();
			if(desktop.isSupported(Desktop.Action.EDIT) && file != null){
				try{
					desktop.edit(file);
				} catch (final IOException ioException){
					ioException.printStackTrace();
				}
			}
		}

	};

	/**
	 * @param stage the mainstage for the application
	 */
	public MainController(final Stage stage){
		this.stage = stage;
		this.sessionInfos = new SessionInfos(this);
		this.LOG = LogManager.getLogger(this.getClass().getName());
		this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/img/icons8-birthday-50.png")));

//		stage.setOnCloseRequest(x -> {
//			Platform.exit();
//		});
		stage.setOnCloseRequest(this.closeAppHandler);
	}

	public Controller getActiveController(){
		return this.activeController;
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
		this.setActiveController(new BirthdaysOverviewController(this));
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());

		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
		this.getSessionInfos().updateSubLists();
	}

	/**
	 * Swiches scenes to the BirthdayOverview. Generates a new Controller.
	 *
	 * @see EditBirthdayViewController
	 */
	public void goToEditBirthdayView(){
		this.setActiveController(new NewBirthdayViewController(this));
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.getActiveController());
		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

	/**
	 * Switches scenes to the BirthdayOverview. Generates a new Controller.
	 *
	 * @param indexPerson The index of the person to edit.
	 *
	 * @see EditBirthdayViewController
	 */
	public void goToEditBirthdayView(final int indexPerson){
		this.setActiveController(new EditBirthdayViewController(this, indexPerson));
		try{
			this.replaceSceneContent("/application/view/EditBirthdayView.fxml", this.getActiveController());

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
	 * @param selectedFile used to fill the birthday list
	 * @throws IOException
	 */
	private void openFile(final File selectedFile) throws IOException{
		MainController.this.LOG.debug("Opend file:" + selectedFile.getAbsolutePath());
		this.getSessionInfos().setSaveFile(selectedFile);
		PersonManager.getInstance().setSaveFile(selectedFile);

		final LoadPersonsTask loadPersonsTask = new LoadPersonsTask(selectedFile, selectedFile.getAbsolutePath().endsWith(".csv"));
		loadPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(final WorkerStateEvent event){
				PersonManager.getInstance().getPersons().clear();
				PersonManager.getInstance().getPersons().addAll(loadPersonsTask.getValue());
				MainController.this.LOG.debug("Loaded birthdays from File");
				MainController.this.sessionInfos.updateSubLists();
			}
		});
		new Thread(loadPersonsTask).start();

		MainController.this.sessionInfos.getRecentFileName().set(selectedFile.getName());
		MainController.this.sessionInfos.getFileToOpenName().set(selectedFile.getName());

		PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_OPEND, PersonManager.getInstance().getSaveFile().getAbsolutePath());
		try

		{
			PropertyManager.getInstance().storeProperties("Saved recent file.");
		} catch (final IOException ioException){
			this.LOG.catching(Level.FATAL, ioException);
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
		// TODO DO i really want this
		// new JMetro(JMetro.Style.LIGHT).applyTheme(root);
		final Scene scene = new Scene(root);
		scene.getStylesheets().add("test.css");
		this.stage.setScene(scene);

		// Show the GUI
		this.stage.show();
	}

	private void setActiveController(final Controller activeController){
		this.activeController = activeController;
	}

	public void settingsChanged(){
		this.getActiveController().updateLocalisation();
	}

	/**
	 * Starts the application with the BirthdaysOverview and possibly loaded file
	 */
	public void start(){
		this.setActiveController(new BirthdaysOverviewController(this));
		try{
			this.replaceSceneContent("/application/view/BirthdaysOverview.fxml", this.getActiveController());
			if(new Boolean(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START))){
				String file = PropertyManager.getProperty(PropertyFields.FILE_ON_START);
				if(!PropertyManager.getProperty(PropertyFields.FILE_ON_START).endsWith(".csv")){
					PropertyManager.getInstance().getProperties().setProperty(PropertyFields.FILE_ON_START, "");
					file = null;
				}
				if(file != null && !file.isEmpty()){
					this.openFile(new File(PropertyManager.getProperty(PropertyFields.FILE_ON_START)));
				} else{
					this.LOG.warn("Should have opend a file upon start but no file to open was found!");
				}
			}
			
		} catch (final Exception exception){
			this.LOG.catching(Level.ERROR, exception);
		}
	}

}
