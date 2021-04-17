/**
 *
 */
package application.model;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.controller.BirthdaysOverviewController;
import application.controller.MainController;
import application.processes.CheckMissedBirthdays;
import application.processes.UpdateBirthdaysThisWeekTask;
import application.processes.UpdateNextBirthdaysTask;
import application.processes.UpdateRecentBirthdaysTask;
import application.util.PropertyFields;
import application.util.PropertyManager;
import application.util.localisation.LangResourceKeys;
import application.util.localisation.LangResourceManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

/**
 * This holds a lot of information for the app.
 * 
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SessionInfos {
	final static Logger LOG = LogManager.getLogger();
	final MainController mainController;

	private Locale appLocale;
	private File saveFile;
	private final StringProperty fileToOpenName = new SimpleStringProperty();
	private StringProperty recentFileName = new SimpleStringProperty();

	private ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
	private final ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();

	private final List<Person> birthdaysThisWeek = new ArrayList<Person>();
	private final ObservableList<PersonsInAWeek> personsInAWeekList = FXCollections.observableArrayList();
	private final ObservableList<Person> birthdaysThisMonat = FXCollections.observableArrayList();

	/**
	 * Loads the saved properties or gets the default values.
	 * @param mainController The {@link MainController} of the application.
	 */
	public SessionInfos(MainController mainController) {
		LOG.info("SessionInfos created");
		this.mainController = mainController;
		final String localePropertieString = PropertyManager.getProperty(PropertyFields.SAVED_LOCALE);
		LOG.debug("Loaded locale propertie " + localePropertieString);

		try {
			this.getRecentFileName().set(new File(PropertyManager.getProperty(PropertyFields.LAST_OPEND)).getName());
		} catch (final NullPointerException nullPointerException) {
			LOG.catching(Level.TRACE, nullPointerException);
			LOG.info("Don't worry just could not load recent File");
		}
	}

	/**
	 * @return the appLocale
	 */
	public Locale getAppLocale() {
		return this.appLocale;
	}

	/**
	 * @return the birthdaysThisMonat
	 */
	public ObservableList<Person> getBirthdaysThisMonat() {
		return this.birthdaysThisMonat;
	}

	/**
	 * @return the birthdaysThisWeek
	 */
	private List<Person> getBirthdaysThisWeek() {
		return this.birthdaysThisWeek;
	}

	/**
	 * @return the fileToOpenName
	 */
	public StringProperty getFileToOpenName() {
		return this.fileToOpenName;
	}

	/**
	 * @return the nextBirthdays
	 */
	public ObservableList<Person> getNextBirthdays() {
		return this.nextBirthdays;
	}

	/**
	 * @return the personsInAWeekList
	 */
	public ObservableList<PersonsInAWeek> getPersonsInAWeekList() {
		return this.personsInAWeekList;
	}

	/**
	 * @return the recentBirthdays
	 */
	public ObservableList<Person> getRecentBirthdays() {
		return this.recentBirthdays;
	}

	/**
	 * @return the recentFileName
	 */
	public StringProperty getRecentFileName() {
		return this.recentFileName;
	}

	/**
	 * resets the nextBirthdays and recentBirthdays
	 */
	public void resetSubLists() {
		this.nextBirthdays.clear();
		this.recentBirthdays.clear();
		this.personsInAWeekList.clear();
	}

	/**
	 * @param appLocale the appLocale to set
	 */
	public void setAppLocale(final Locale appLocale) {
		this.appLocale = appLocale;
	}

	/**
	 * @param nextBirthdays the nextBirthdays to set
	 */
	public void setNextBirthdays(final ObservableList<Person> nextBirthdays) {
		this.nextBirthdays = nextBirthdays;
	}

	/**
	 * @param recentFileName the recentFileName to set
	 */
	public void setRecentFileName(final StringProperty recentFileName) {
		this.recentFileName = recentFileName;
	}

	/**
	 * Resets and updates all sublists
	 * <p>
	 *
	 * <b>Sublists</b>
	 * <ul>
	 * <li>{@link #nextBirthdays}
	 * <li>{@link #recentBirthdays}
	 * <li>{@link #birthdaysThisWeek}
	 * </ul>
	 * <b>Used Tasks</b>
	 * <ul>
	 * <li>{@link UpdateNextBirthdaysTask}
	 * <li>{@link UpdateRecentBirthdaysTask}
	 * <li>{@link UpdateBirthdaysThisWeekTask}
	 * </ul>
	 */
	public void updateSubLists() {
		this.resetSubLists();
		final ProgressBar progressBar;
		if (this.mainController.getActiveController() instanceof BirthdaysOverviewController) {
			progressBar = ((BirthdaysOverviewController) this.mainController.getActiveController()).getProgressbar();
		} else {
			progressBar = null;
		}
		if (progressBar != null) {
			// Unbind progress property
			progressBar.progressProperty().unbind();
		}

		final UpdateNextBirthdaysTask updateNextBirthdaysTask = new UpdateNextBirthdaysTask();
		updateNextBirthdaysTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
				new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(final WorkerStateEvent workerStateEvent) {
						LOG.debug(workerStateEvent.getSource().getClass().getName() + " ENDED ");
						SessionInfos.this.getNextBirthdays().setAll(updateNextBirthdaysTask.getValue());
						if (progressBar != null) {
							// Unbind progress property
							progressBar.progressProperty().unbind();
							progressBar.setProgress(0);
						}
					}
				});

		if (progressBar != null) {
			// Bind progress property
			progressBar.progressProperty().bind(updateNextBirthdaysTask.progressProperty());
		}
		Thread t = new Thread(updateNextBirthdaysTask);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();

			}
		});
		t.start();

		final UpdateBirthdaysThisWeekTask updateBirthdaysThisWeekTask = new UpdateBirthdaysThisWeekTask();
		updateBirthdaysThisWeekTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
				new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(final WorkerStateEvent workerStateEvent) {
						SessionInfos.this.personsInAWeekList.setAll(updateBirthdaysThisWeekTask.getValue());
						LOG.debug(workerStateEvent.getSource().getClass().getName() + " ENDED ");
					}
				});
		new Thread(updateBirthdaysThisWeekTask).start();

		final UpdateRecentBirthdaysTask updateRecentBirthdaysTask = new UpdateRecentBirthdaysTask();
		updateRecentBirthdaysTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
				new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(final WorkerStateEvent workerStateEvent) {
						SessionInfos.this.recentBirthdays.setAll(updateRecentBirthdaysTask.getValue());
						LOG.debug(workerStateEvent.getSource().getClass().getName() + " ENDED ");
					}
				});
		new Thread(updateRecentBirthdaysTask).start();

		CheckMissedBirthdays missedBirthdays = new CheckMissedBirthdays();
		missedBirthdays.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(final WorkerStateEvent workerStateEvent) {
				List<Person> value = missedBirthdays.getValue();
				LangResourceManager lRM = new LangResourceManager();
				if (value == null) {
					LOG.debug("No missed Birthdays!");
				} else {
					final Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle(lRM.getLocaleString(LangResourceKeys.missedBirthdays));
					alert.setHeaderText(lRM.getLocaleString(LangResourceKeys.missedBirthdays));

					StringBuilder stringBuilder = new StringBuilder();

					for (Person person : value) {
						int age = (LocalDate.now().getYear() - person.getBirthday().getYear());
//					stringBuilder.append(lRM.getLocaleString(LangResourceKeys.age) + ": " + age+ "\t");
						long days = ChronoUnit.DAYS.between(person.getBirthday().withYear(LocalDate.now().getYear()),
								LocalDate.now());
						stringBuilder.append(person.namesToString() + " ");
						String missedBirthdaysMessage = String
								.format(lRM.getLocaleString(LangResourceKeys.missedBirthdaysMsg), days, age);
						stringBuilder.append(missedBirthdaysMessage);
						stringBuilder.append("\n");
					}

					final TextArea textArea = new TextArea(stringBuilder.toString());
					textArea.setEditable(false);
					textArea.setWrapText(false);

					alert.getDialogPane().setContent(textArea);
					alert.showAndWait();
					LOG.debug(workerStateEvent.getSource().getClass().getName() + " ENDED ");
				}
			}
		});

		new Thread(missedBirthdays).start();

	}

	public File getSaveFile() {
		return saveFile;
	}

	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

}
