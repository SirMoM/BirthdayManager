package application.model;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;

/**
 * This holds a lot of information for the app.
 *
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SessionInfos {
    static final Logger LOG = LogManager.getLogger();

    final MainController mainController;

    private final StringProperty fileToOpenName = new SimpleStringProperty();
    private final List<Person> birthdaysThisWeek = new ArrayList<>();
    private final ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();
    private final ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
    private final ObservableList<PersonsInAWeek> personsInAWeekList = FXCollections.observableArrayList();

    private final RecentItems recentFileNames = new RecentItems(5);
    private Locale appLocale;
    private File saveFile;

    /**
     * Loads the saved properties or gets the default values.
     *
     * @param mainController The {@link MainController} of the application.
     */
    public SessionInfos(MainController mainController) {
        LOG.info("SessionInfos created");

        this.mainController = mainController;
        final String localePropertyString = PropertyManager.getProperty(PropertyFields.SAVED_LOCALE);

        LOG.debug("Loaded locale property {}", localePropertyString);

        try {
            recentFileNames.loadFromProperties(PropertyManager.getProperty(PropertyFields.LAST_OPENED));
        } catch (final NullPointerException nullPointerException) {
            LOG.catching(Level.WARN, nullPointerException);
            LOG.warn("Don't worry just could not load recent File");
        }
    }

    public RecentItems getRecentFileNames() {
        return recentFileNames;
    }

    /** @return the appLocale */
    public Locale getAppLocale() {
        return this.appLocale;
    }

    /** @param appLocale the appLocale to set */
    public void setAppLocale(final Locale appLocale) {
        this.appLocale = appLocale;
    }

    /** @return the birthdaysThisWeek */
    public List<Person> getBirthdaysThisWeek() {
        return this.birthdaysThisWeek;
    }

    /** @return the fileToOpenName */
    public StringProperty getFileToOpenName() {
        return this.fileToOpenName;
    }

    /** @return the nextBirthdays */
    public ObservableList<Person> getNextBirthdays() {
        return this.nextBirthdays;
    }

    /** @return the personsInAWeekList */
    public ObservableList<PersonsInAWeek> getPersonsInAWeekList() {
        return this.personsInAWeekList;
    }

    /** @return the recentBirthdays */
    public ObservableList<Person> getRecentBirthdays() {
        return this.recentBirthdays;
    }

    /** resets the nextBirthdays and recentBirthdays */
    public void resetSubLists() {
        this.nextBirthdays.clear();
        this.recentBirthdays.clear();
        this.personsInAWeekList.clear();
    }

    /**
     * Resets and updates all sublists
     *
     * <p><b>Sublists</b>
     *
     * <ul>
     *   <li>{@link #nextBirthdays}
     *   <li>{@link #recentBirthdays}
     *   <li>{@link #birthdaysThisWeek}
     * </ul>
     *
     * <b>Used Tasks</b>
     *
     * <ul>
     *   <li>{@link UpdateNextBirthdaysTask}
     *   <li>{@link UpdateRecentBirthdaysTask}
     *   <li>{@link UpdateBirthdaysThisWeekTask}
     * </ul>
     */
    public void updateSubLists() {
        final String endMessage = "{} ENDED ";
        this.resetSubLists();

        // Worker for the next Birthdays
        final UpdateNextBirthdaysTask updateNextBirthdaysTask = new UpdateNextBirthdaysTask();
        updateNextBirthdaysTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
            LOG.debug(endMessage, workerStateEvent.getSource().getClass().getName());
            SessionInfos.this.getNextBirthdays().setAll(updateNextBirthdaysTask.getValue());
        });
        new Thread(updateNextBirthdaysTask).start();

        // Worker for the recent Birthdays
        final UpdateRecentBirthdaysTask updateRecentBirthdaysTask = new UpdateRecentBirthdaysTask();
        updateRecentBirthdaysTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
            SessionInfos.this.recentBirthdays.setAll(updateRecentBirthdaysTask.getValue());
            LOG.debug(endMessage, workerStateEvent.getSource().getClass().getName());
        });
        new Thread(updateRecentBirthdaysTask).start();

        // Worker for the Birthdays this Week
        final UpdateBirthdaysThisWeekTask updateBirthdaysThisWeekTask = new UpdateBirthdaysThisWeekTask();
        updateBirthdaysThisWeekTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
            SessionInfos.this.personsInAWeekList.setAll(updateBirthdaysThisWeekTask.getValue());
            LOG.debug(endMessage, workerStateEvent.getSource().getClass().getName());
        });
        new Thread(updateBirthdaysThisWeekTask).start();


        // FIXME: This should not be executed everytime we rebuild the sublists!
        CheckMissedBirthdays missedBirthdays = new CheckMissedBirthdays();
        missedBirthdays.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, workerStateEvent -> {
            List<Person> value = missedBirthdays.getValue();
            LangResourceManager lRM = new LangResourceManager();

            if (value == null || value.isEmpty()) {
                LOG.debug("No missed Birthdays!");
            } else {
                final Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(lRM.getLocaleString(LangResourceKeys.missedBirthdays));
                alert.setHeaderText(lRM.getLocaleString(LangResourceKeys.missedBirthdays));

                StringBuilder stringBuilder = new StringBuilder();

                for (Person person : value) {
                    int age = (LocalDate.now().getYear() - person.getBirthday().getYear());
                    long days = ChronoUnit.DAYS.between(person.getBirthday().withYear(LocalDate.now().getYear()), LocalDate.now());
                    stringBuilder.append(person.namesToString() + " ");

                    try {
                        String missedBirthdaysMessage = String.format(lRM.getLocaleString(LangResourceKeys.missedBirthdaysMsg), days, age);
                        stringBuilder.append(missedBirthdaysMessage);
                    } catch (IllegalFormatException illegalFormatException) {
                        LOG.catching(Level.WARN, illegalFormatException);
                        LOG.warn("Could not generate Missing-Text for {} based on {} and {}", person, days, age);
                    } catch (NullPointerException nullPointerException) {
                        LOG.catching(Level.WARN, nullPointerException);
                        LOG.warn("Could not generate Missing-Text for {} based on {} and {}", person, days, age);
                        stringBuilder.append("\n");
                    }

                    final TextArea textArea = new TextArea(stringBuilder.toString());
                    textArea.setEditable(false);
                    textArea.setWrapText(false);

                    alert.getDialogPane().setContent(textArea);
                    alert.showAndWait();
                    LOG.debug(endMessage, workerStateEvent.getSource().getClass().getName());
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
