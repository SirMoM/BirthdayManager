/**
 *
 */
package application.processes;

import java.util.List;

import application.model.Person;
import javafx.concurrent.Task;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateRecentBirthdaysTask extends Task<List<Person>>{

	/**
	 *
	 */
	public UpdateRecentBirthdaysTask(){
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<Person> call() throws Exception{
		// TODO identify the recent birthdays
		return null;
	}

}
