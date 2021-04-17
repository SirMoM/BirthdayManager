/**
 *
 */
package application.processes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import application.util.BirthdayComparator;
import application.util.PropertyFields;
import application.util.PropertyManager;
import javafx.concurrent.Task;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateRecentBirthdaysTask extends Task<List<Person>> {

	private final int NEXT_BIRTHDAYS_COUNT;
	private List<Person> personDB;
	private Logger LOG;

	/**
	 *
	 */
	public UpdateRecentBirthdaysTask() {
		this.LOG = LogManager.getLogger(this.getClass().getName());
		NEXT_BIRTHDAYS_COUNT = Integer.parseInt(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT));
		if (PersonManager.getInstance().getPersonDB() != null && !PersonManager.getInstance().getPersonDB().isEmpty()) {
			personDB = PersonManager.getInstance().getPersonDB();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<Person> call() throws Exception {
		LOG.debug("Started " + this.getClass().getName());
		while (personDB == null || PersonManager.getInstance().getPersonDB().isEmpty()) {
			personDB = PersonManager.getInstance().getPersonDB();
			LOG.warn("Waiting for personenDB to be filled!");
			Thread.sleep(500);
		}

		final List<Person> upcomming = new ArrayList<Person>();
		final List<Person> after = new ArrayList<Person>();
		final List<Person> recentBirthdays = new ArrayList<Person>();

		for (int i = 0; i < personDB.size(); i++) {
			final Person person = personDB.get(i);
			if (person.getBirthday().getDayOfYear() >= LocalDate.now().getDayOfYear()) {
				upcomming.add(person);
			} else {
				after.add(person);
			}
		}

		upcomming.sort(new BirthdayComparator(false));
		after.sort(new BirthdayComparator(false));
		int i = 0;
		for (; i < NEXT_BIRTHDAYS_COUNT; i++) {
			try {
				recentBirthdays.add(after.get((after.size() - 1) - i));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for recent",
						indexOutOfBoundsException);
				break;
			}
		}
		int j = 0;
		for (; j < NEXT_BIRTHDAYS_COUNT - i; j++) {
			try {
				recentBirthdays.add(after.get((upcomming.size() - 1) - j));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for recent",
						indexOutOfBoundsException);
				break;
			}
		}
		return recentBirthdays;
	}
}
