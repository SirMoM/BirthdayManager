/**
 *
 */
package application.processes;

import application.controller.MainController;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SaveLastFileUsedTask extends Task<Boolean>{

	private final MainController mainController;

	/**
	 * @param mainController the MainController of this application
	 */
	public SaveLastFileUsedTask(final MainController mainController){
		super();
		this.mainController = mainController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Boolean call() throws Exception{
		this.mainController.getSessionInfos().getPropertiesHandler().getProperties().setProperty("last_opend", this.mainController.getSessionInfos().getFileToOpen().getAbsolutePath());
		this.mainController.getSessionInfos().getPropertiesHandler().storeProperties("saveRecentfile");
		return true;
	}

}
