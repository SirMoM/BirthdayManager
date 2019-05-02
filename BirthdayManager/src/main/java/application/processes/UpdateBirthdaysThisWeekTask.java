/**
 *
 */
package application.processes;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAWeek;
import application.util.BirthdayComparator;
import javafx.concurrent.Task;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateBirthdaysThisWeekTask extends Task<List<PersonsInAWeek>> {

	private final Logger LOG;
	private List<Person> personDB = null;
	private final int week;

	/**
	 *
	 */
	public UpdateBirthdaysThisWeekTask() {
		this.LOG = LogManager.getLogger(this.getClass().getName());
		week = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		this.LOG.info("Gathering the Birthdays for this week: " + week);

		if (PersonManager.getInstance().getPersons() != null && !PersonManager.getInstance().getPersons().isEmpty()) {
			personDB = PersonManager.getInstance().getPersons();
		}
	}

	/**
	 * Fills the BirthdaysThisWeek
	 *
	 * @param temp the {@link List} of persons where the Birthdays this week are
	 *             extracted
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<PersonsInAWeek> call() throws Exception {
		LOG.debug("Started " + this.getClass().getName());
		while (personDB == null || PersonManager.getInstance().getPersons().isEmpty()) {
			personDB = PersonManager.getInstance().getPersons();
			LOG.warn("Waiting for personenDB to be filled!");
			Thread.sleep(500);
		}

		final List<Person> birthdaysThisWeek = new ArrayList<Person>();

		personDB.sort(new BirthdayComparator(true));

		for (final Person person : personDB) {
			final LocalDate birthday = person.getBirthday().withYear(2019);
			if (birthday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week) {
				birthdaysThisWeek.add(person);
			}
		}

		LOG.debug("Gathered all Persons this week( " + week + " )! Count: " + birthdaysThisWeek.size());
		final List<PersonsInAWeek> personsInAWeekList = PersonsInAWeek.parseAList(birthdaysThisWeek);
		return personsInAWeekList;
	}
}
