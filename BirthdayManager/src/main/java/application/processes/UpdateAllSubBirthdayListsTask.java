/**
 *
 */
package application.processes;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.util.BirthdayComparator;
import application.util.ConfigFields;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateAllSubBirthdayListsTask extends Task<Boolean>{

	final int NEXT_BIRTHDAYS_COUNT;
	private final SessionInfos sessionInfos;

	/**
	 * @param birthdaysOverviewController
	 */
	public UpdateAllSubBirthdayListsTask(final SessionInfos sessionInfos){
		super();
		this.sessionInfos = sessionInfos;
		this.NEXT_BIRTHDAYS_COUNT = Integer.parseInt(sessionInfos.getConfigHandler().getProperties().getProperty(ConfigFields.NEXT_BIRTHDAYS_COUNT));
	}

	@Override
	protected Boolean call() throws Exception{
		final List<Person> allPersons = PersonManager.getInstance().getPersonDB();

		final BirthdayComparator birthdayComparator = new BirthdayComparator(true);
		final List<Person> temp = new ArrayList<>(allPersons);

		temp.sort(birthdayComparator);
		this.updateNextBirthdays(temp);
		this.updateRecentBirthdays(temp);

		this.updateBirthdaysThisWeek(temp);
		this.updateBirthdaysThisMonth(temp);

		return true;
	}

	private void updateBirthdaysThisMonth(final List<Person> temp){
		// TODO do it
	}

	private void updateBirthdaysThisWeek(final List<Person> temp){
		final int week = LocalDate.now().get(IsoFields.WEEK_BASED_YEAR);

		for(final Person person : temp){
			if(week == person.getBirthday().get(IsoFields.WEEK_BASED_YEAR)){
//				this.sessionInfos.getBirthdaysThisWeek().put(person.getBirthday().get().getDayOfWeek(), person);
			}

		}

	}

	/**
	 * @param temp
	 */
	private void updateNextBirthdays(final List<Person> temp){
		this.sessionInfos.getNextBirthdays().clear();
		final int birthdaysSize = temp.size() - 1;
		int i = 0;
		for(; i < this.NEXT_BIRTHDAYS_COUNT; i++){
			final Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().getDayOfYear() < LocalDate.now().getDayOfYear()){
				break;
			} else{
				this.sessionInfos.getNextBirthdays().add(tempPerson);
			}
		}
		Person tempPerson = null;
		for(; i < this.NEXT_BIRTHDAYS_COUNT; i++){
			tempPerson = temp.get(birthdaysSize - i);
			this.sessionInfos.getNextBirthdays().add(tempPerson);
		}
	}

	/**
	 * @param temp
	 */
	private void updateRecentBirthdays(final List<Person> temp){
		// TODO Jahres übergreifend XD
		this.sessionInfos.getRecentBirthdays().clear();
		int i = 10;
		for(; i == 0; i++){
			final Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().getDayOfYear() > LocalDate.now().getDayOfYear()){
				break;
			}
		}
		final int x = temp.size() - (this.NEXT_BIRTHDAYS_COUNT - i);
		for(; i < this.NEXT_BIRTHDAYS_COUNT; i++){
			final Person tempPerson = temp.get(i);
			this.sessionInfos.getRecentBirthdays().add(tempPerson);
		}

	}
}
