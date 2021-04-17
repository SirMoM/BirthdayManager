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
public class UpdateNextBirthdaysTask extends Task<List<Person>> {

	private final Logger LOG;
	private List<Person> personDB;
	private final int NEXT_BIRTHDAYS_COUNT;
	private int timeInWaiting;

	/**
	 *
	 */
	public UpdateNextBirthdaysTask() {
		this.LOG = LogManager.getLogger(this.getClass().getName());
		NEXT_BIRTHDAYS_COUNT = Integer.parseInt(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT));
		if (PersonManager.getInstance().getPersons() != null && !PersonManager.getInstance().getPersons().isEmpty()) {
			personDB = PersonManager.getInstance().getPersons();
		}
		this.updateProgress(0, NEXT_BIRTHDAYS_COUNT);
	}

	/**
	 * Fills the NextBirthdays List
	 *
	 * @return A {@link List} of persons
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<Person> call() throws Exception {
		LOG.debug("Started " + this.getClass().getName() + " " + System.currentTimeMillis());
		while (personDB == null || PersonManager.getInstance().getPersons().isEmpty()) {
			personDB = PersonManager.getInstance().getPersons();
			LOG.info("Waiting for personenDB to be filled!");
			Thread.sleep(500);
			this.timeInWaiting += 500;
			if (this.timeInWaiting > 10000) {
				this.cancel();
				LOG.debug("Thread canceled because it took too long to wait for the list of people!");
			}
		}
		final List<Person> upcomming = new ArrayList<Person>();
		final List<Person> after = new ArrayList<Person>();
		final List<Person> nextBirthdays = new ArrayList<Person>();

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
				nextBirthdays.add(upcomming.get(i));
				this.updateProgress(i, NEXT_BIRTHDAYS_COUNT);
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
				this.LOG.debug("Probably not enought Persons to gather the 10 birthdays for next",
						indexOutOfBoundsException);
				break;
			}
		}

		for (int j = 0; j < NEXT_BIRTHDAYS_COUNT - i; j++) {
			try {
				nextBirthdays.add(after.get(j));
				this.updateProgress(j, NEXT_BIRTHDAYS_COUNT);
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
				this.LOG.debug("Probably not enought Persons to gather the 10 birthdays for next",
						indexOutOfBoundsException);
				break;
			}
		}
		return nextBirthdays;
	}

}
