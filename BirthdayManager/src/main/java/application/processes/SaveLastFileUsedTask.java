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

	private MainController mainController;

	/**
	 * @param mainController
	 */
	public SaveLastFileUsedTask(MainController mainController){
		super();
		this.mainController = mainController;
	}

	@Override
	protected Boolean call() throws Exception{
		this.mainController.getSessionInfos().getConfigHandler().getProperties().setProperty("last_opend", this.mainController.getSessionInfos().getFileToOpen().getAbsolutePath());
		this.mainController.getSessionInfos().getConfigHandler().storeProperties("saveRecentfile");
		return true;
	}

}
