/**
 *
 */
package application.processes;

import application.model.Person;
import application.util.BirthdayComparator;
import application.util.PropertyFields;
import application.util.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateRecentBirthdaysTask extends PersonTasks<List<Person>> {

    private static final Logger LOG = LogManager.getLogger(UpdateRecentBirthdaysTask.class.getName());
    private final int NEXT_BIRTHDAYS_COUNT;

    /** */
    public UpdateRecentBirthdaysTask() {
        String showBirthdaysCountProperty = PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT);
        NEXT_BIRTHDAYS_COUNT = Integer.parseInt(showBirthdaysCountProperty);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected List<Person> call() throws Exception {
        LOG.debug("Started {}", this.getClass().getName());

        /** All upcoming persons / birthdays */
        final List<Person> upcoming = new ArrayList<>();

        /** All passed persons / birthdays */
        final List<Person> after = new ArrayList<>();

        /** Final recent persons / birthdays */
        final List<Person> recentBirthdays = new ArrayList<>();

        List<Person> personDB = getPersons();

        // Fill upcoming and after based on today
        for (int i = 0; i < personDB.size(); i++) {
            final Person person = personDB.get(i);
            int dayOfYear = person.getBirthday().withYear(LocalDate.now().getYear()).getDayOfYear();
            if (dayOfYear >= LocalDate.now().getDayOfYear()) {
                upcoming.add(person);
            } else {
                after.add(person);
            }
        }

        upcoming.sort(new BirthdayComparator(false));
        after.sort(new BirthdayComparator(false));
        int i = 0;
        for (; i < NEXT_BIRTHDAYS_COUNT; i++) {
            try {
                recentBirthdays.add(after.get((after.size() - 1) - i));
            } catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
                LOG.debug("Probably not enough Persons to gather the birthdays to fill the recent List", indexOutOfBoundsException);
                break;
            }
        }
        int j = 0;
        for (; j < NEXT_BIRTHDAYS_COUNT - i; j++) {
            try {
                recentBirthdays.add(upcoming.get((upcoming.size() - 1) - j));
            } catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
                LOG.debug("Probably not enough Persons to gather the 10 birthdays for recent", indexOutOfBoundsException);
                break;
            }
        }
        return recentBirthdays;
    }
}
