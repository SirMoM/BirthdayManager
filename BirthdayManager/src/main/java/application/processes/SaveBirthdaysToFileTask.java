/**
 *
 */
package application.processes;

import application.model.PersonManager;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class saveBirthdaysToFileTask extends Task<Boolean>{

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Boolean call() throws Exception{
		return PersonManager.getInstance().save();
	}

}
