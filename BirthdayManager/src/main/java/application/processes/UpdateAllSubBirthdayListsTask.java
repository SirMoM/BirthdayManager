/**
 * 
 */
package application.processes;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

import application.model.Person;
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
	private SessionInfos sessionInfos;

	/**
	 * @param birthdaysOverviewController
	 */
	public UpdateAllSubBirthdayListsTask(SessionInfos sessionInfos){
		super();
		this.sessionInfos = sessionInfos;
		this.NEXT_BIRTHDAYS_COUNT = Integer.parseInt(sessionInfos.getConfigHandler().getProperties().getProperty(ConfigFields.NEXT_BIRTHDAYS_COUNT));
	}

	private void updateBirthdaysThisMonth(List<Person> temp){
		// TODO Auto-generated method stub

	}

	private void updateBirthdaysThisWeek(List<Person> temp){
		int week = LocalDate.now().get(IsoFields.WEEK_BASED_YEAR);

		for(Person person : temp){
			if(week == person.getBirthday().get().get(IsoFields.WEEK_BASED_YEAR)){
				System.out.println(person);
//				this.sessionInfos.getBirthdaysThisWeek().put(person.getBirthday().get().getDayOfWeek(), person);
			}

		}

	}

	/**
	 * @param temp
	 */
	private void updateNextBirthdays(final List<Person> temp){
		this.sessionInfos.getNextBirthdays().clear();
		int birthdaysSize = temp.size() - 1;
		int i = 0;
		for(; i < this.NEXT_BIRTHDAYS_COUNT; i++){
			Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().get().getDayOfYear() < LocalDate.now().getDayOfYear()){
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
			Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().get().getDayOfYear() > LocalDate.now().getDayOfYear()){
				break;
			}
		}
		int x = temp.size() - (this.NEXT_BIRTHDAYS_COUNT - i);
		for(; i < this.NEXT_BIRTHDAYS_COUNT; i++){
			Person tempPerson = temp.get(i);
			this.sessionInfos.getRecentBirthdays().add(tempPerson);
		}

	}

	@Override
	protected Boolean call() throws Exception{
		System.out.println("UpdateAllSubBirthdayListsTask.call()");

		List<Person> allPersons = this.sessionInfos.getAllPersons();

		BirthdayComparator birthdayComparator = new BirthdayComparator(true);
		List<Person> temp = new ArrayList<>(allPersons);

		temp.sort(birthdayComparator);
		this.updateNextBirthdays(temp);
		System.out.println("updateNextBirthdays");
		this.updateRecentBirthdays(temp);
		System.out.println("updateRecentBirthdays");

		this.updateBirthdaysThisWeek(temp);
		this.updateBirthdaysThisMonth(temp);

		return true;
	}
}
