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
import application.util.PropertieFields;
import application.util.PropertieManager;
import javafx.concurrent.Task;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateNextBirthdaysTask extends Task<List<Person>>{

	private final Logger LOG;

	/**
	 *
	 */
	public UpdateNextBirthdaysTask(){
		this.LOG = LogManager.getLogger(this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<Person> call() throws Exception{
		return this.updateNextBirthdays();
	}

	/**
	 * Fills the NextBirthdays
	 *
	 * @param temp the {@link List} of persons where the next Birthdays are
	 *             extracted
	 * @return
	 */
	private List<Person> updateNextBirthdays(){
		final int NEXT_BIRTHDAYS_COUNT = Integer.parseInt(PropertieManager.getPropertie(PropertieFields.SHOW_BIRTHDAYS_COUNT));
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		final List<Person> upcomming = new ArrayList<Person>();
		final List<Person> after = new ArrayList<Person>();
		final List<Person> nextBirthdays = new ArrayList<Person>();

		for(final Person person : temp){
			if(person.getBirthday().getDayOfYear() >= LocalDate.now().getDayOfYear()){
				upcomming.add(person);
			} else{
				after.add(person);
			}

		}

		upcomming.sort(new BirthdayComparator(false));
		for(final Person person : upcomming){
			System.out.println(person);
		}

		after.sort(new BirthdayComparator(false));
		for(final Person person : upcomming){
			System.out.println(person);
		}

		int i = 0;
		for(; i < NEXT_BIRTHDAYS_COUNT; i++){
			try{
				nextBirthdays.add(upcomming.get(i));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				this.LOG.debug("Probably not enought Persons to geather the 10 birthdays for next", indexOutOfBoundsException);
				break;
			}
		}

		for(int j = 0; j < NEXT_BIRTHDAYS_COUNT - i; j++){
			try{
				nextBirthdays.add(after.get(j));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				this.LOG.debug("Probably not enought Persons to geather the 10 birthdays for next", indexOutOfBoundsException);
				break;
			}
		}
		return nextBirthdays;
	}

}
