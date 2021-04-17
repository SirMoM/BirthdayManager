/**
 *
 */
package application.processes;

import application.model.Person;
import application.model.PersonsInAWeek;
import application.util.BirthdayComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateBirthdaysThisWeekTask extends PersonTasks<List<PersonsInAWeek>> {


    private static final Logger LOG = LogManager.getLogger(UpdateBirthdaysThisWeekTask.class.getName());
    private final int week;
    private int timeInWaiting;

    /** */
    public UpdateBirthdaysThisWeekTask() {
        this.week = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        LOG.info("Gathering the Birthdays for this week: {}", this.week);
    }

    /**
     * Fills the BirthdaysThisWeek
     *
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected List<PersonsInAWeek> call() throws Exception {
        LOG.debug("Started {}", this.getClass().getName());

        List<Person> personDB = getPersons();

        final List<Person> birthdaysThisWeek = new ArrayList<>();

        personDB.sort(new BirthdayComparator(true));

        for (final Person person : personDB) {
            final LocalDate birthday = person.getBirthday().withYear(LocalDate.now().getYear());
            if (birthday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == this.week) {
                birthdaysThisWeek.add(person);
            }
        }

        LOG.debug("Gathered all Persons this week({})! Count: {}", this.week, birthdaysThisWeek.size());
        return PersonsInAWeek.parseAList(birthdaysThisWeek);
    }
}
