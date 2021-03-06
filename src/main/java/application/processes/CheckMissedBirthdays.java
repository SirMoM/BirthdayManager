package application.processes;

import application.model.Person;
import application.util.PropertyFields;
import application.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Ruben
 * @since 2019-11-02
 */
public class CheckMissedBirthdays extends PersonTasks<List<Person>> {

    private static final Logger LOG = LogManager.getLogger(CheckMissedBirthdays.class.getName());
    private int timeInWaiting;
    private List<Person> personDB;


    /*
     * (non-Javadoc)
     *
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected List<Person> call() throws Exception {
        personDB = getPersons();
        // Get and manage last visit to compare with today.
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lastVisit = LocalDate.parse(PropertyManager.getProperty(PropertyFields.LAST_VISIT), dateTimeFormatter);
        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_VISIT, LocalDate.now().format(dateTimeFormatter));
        PropertyManager.getInstance().storeProperties("Change last visit");

        long daysSinceLastVisit = ChronoUnit.DAYS.between(lastVisit, LocalDate.now());
        if (daysSinceLastVisit >= 2) {
            return getBirthdaysSince(lastVisit, LocalDate.now());
        }
        failed();
        return new ArrayList<>();
    }

    private List<Person> getBirthdaysSince(LocalDate lastVisit, LocalDate now) {
        List<Person> skippedBirthdays = new ArrayList<>();
        for (Person person : personDB) {
            if (person.getBirthday().withYear(now.getYear()).isAfter(lastVisit) && person.getBirthday().withYear(now.getYear()).isBefore(now)) {
                LOG.debug("{} missed!", person.toExtendedString());
                skippedBirthdays.add(person);
            }
        }
        return skippedBirthdays;
    }
}
