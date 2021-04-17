/**
 * 
 */
package application.model;

import java.io.File;
import java.io.IOException;
import application.util.ConfigHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SessionInfos{
	private File fileToOpen;
	private ConfigHandler configHandler;
	private ObservableList<Person> allPersons = FXCollections.observableArrayList();

	/**
	 * @param configHandler
	 * @throws IOException
	 */
	public SessionInfos(){
		super();
		try{
			this.configHandler = new ConfigHandler();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * @return the allPersons
	 */
	public ObservableList<Person> getAllPersons(){
		return this.allPersons;
	}

	public ConfigHandler getConfigHandler(){
		return this.configHandler;
	}

	public File getFileToOpen(){
		return this.fileToOpen;
	}

	public void setFileToOpen(File fileToOpen){
		this.fileToOpen = fileToOpen;
	}

}
