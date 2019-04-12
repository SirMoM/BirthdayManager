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
public class UpdateBirthdaysThisWeekTask extends Task<List<PersonsInAWeek>>{

	private final Logger LOG;

	/**
	 *
	 */
	public UpdateBirthdaysThisWeekTask(){
		this.LOG = LogManager.getLogger(this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<PersonsInAWeek> call() throws Exception{
		return this.updateBirthdaysThisWeek();
	}

	/**
	 * Fills the BirthdaysThisWeek
	 *
	 * @param temp the {@link List} of persons where the Birthdays this week are
	 *             extracted
	 */
	private List<PersonsInAWeek> updateBirthdaysThisWeek(){
		final List<Person> birthdaysThisWeek = new ArrayList<Person>();
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		final int week = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

		temp.sort(new BirthdayComparator(true));
		this.LOG.info("Gathering the Birthdays for this week: " + week);

		for(final Person person : temp){
			final LocalDate birthday = person.getBirthday().withYear(2019);
			if(birthday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == week){
				birthdaysThisWeek.add(person);
			}
		}

		final List<PersonsInAWeek> personsInAWeekList = PersonsInAWeek.parseAList(birthdaysThisWeek);
		return personsInAWeekList;
	}

}