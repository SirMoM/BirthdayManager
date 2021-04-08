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
public class UpdateNextBirthdaysTask extends PersonTasks<List<Person>> {

    /** Logger for this process */
    private static final Logger LOG = LogManager.getLogger(UpdateNextBirthdaysTask.class.getName());
    /** How many birthdays will be shown. */
    private final int NEXT_BIRTHDAYS_COUNT;

    /** Default Constructor */
    public UpdateNextBirthdaysTask() {
        super();
        String showBirthdaysCountProperty = PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT);
        NEXT_BIRTHDAYS_COUNT = Integer.parseInt(showBirthdaysCountProperty);
    }

    /**
     * Fills the NextBirthdays List
     *
     * @return A {@link List} of persons
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected List<Person> call() throws Exception {
        LOG.debug("Started {} at {}", this.getClass().getName(), System.currentTimeMillis());

        List<Person> personDB = getPersons();

        /** All upcoming persons / birthdays */
        final List<Person> upcoming = new ArrayList<>();

        /** All passed persons / birthdays */
        final List<Person> after = new ArrayList<>();
        /** Final next persons / birthdays */
        final List<Person> nextBirthdays = new ArrayList<>();

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
                nextBirthdays.add(upcoming.get(i));
            } catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
                LOG.debug("Probably not enough Persons to gather the 10 birthdays for next", indexOutOfBoundsException);
                break;
            }
        }

        for (int j = 0; j < NEXT_BIRTHDAYS_COUNT - i; j++) {
            try {
                nextBirthdays.add(after.get(j));
                this.updateProgress(j, NEXT_BIRTHDAYS_COUNT);
            } catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
                LOG.debug("Probably not enought Persons to gather the 10 birthdays for next", indexOutOfBoundsException);
                break;
            }
        }
        return nextBirthdays;
    }
}
