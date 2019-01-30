/**
 *
 */
package application.processes;

import java.io.File;

import application.model.PersonManager;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SaveBirthdaysToFileTask extends Task<Boolean>{
	File selectedFile;

	public SaveBirthdaysToFileTask(){
		this.selectedFile = null;
	}

	public SaveBirthdaysToFileTask(final File selectedFile){
		this.selectedFile = selectedFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Boolean call() throws Exception{
		if(this.selectedFile == null){
			return PersonManager.getInstance().save();
		} else{
			return PersonManager.getInstance().save(this.selectedFile);
		}
	}

}
