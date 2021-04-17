/**
 * 
 */
package application.model;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import application.util.ConfigFields;
import application.util.ConfigHandler;
import application.util.localisation.LangResourceManager;
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
	private Locale appLocale;
	final private LangResourceManager langResources;

	/**
	 * @param configHandler
	 * @throws IOException
	 */
	public SessionInfos(){
		super();
		try{
			this.configHandler = new ConfigHandler();

			String savedLocaleProperty = this.configHandler.getProperties().getProperty(ConfigFields.SAVED_LOCALE).replace(" ", "");
			if(!savedLocaleProperty.isEmpty()){
				String[] savedLocalePropertySplit = savedLocaleProperty.split("_");
				this.appLocale = new Locale(savedLocalePropertySplit[0], savedLocalePropertySplit[1]);
			} else{
				System.err.println("could not load saved_locale_property");
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			if(this.appLocale == null){
				this.appLocale = Locale.GERMANY;
			}
			System.out.println("Started in: " + this.appLocale);
			this.langResources = new LangResourceManager(this.appLocale);
		}

	}

	/**
	 * @return the allPersons
	 */
	public ObservableList<Person> getAllPersons(){
		return this.allPersons;
	}

	/**
	 * @return the appLocale
	 */
	public Locale getAppLocale(){
		return this.appLocale;
	}

	public ConfigHandler getConfigHandler(){
		return this.configHandler;
	}

	public File getFileToOpen(){
		return this.fileToOpen;
	}

	/**
	 * @return the langResources
	 */
	public LangResourceManager getLangResourceManager(){
		return this.langResources;
	}

	/**
	 * @param appLocale the appLocale to set
	 */
	public void setAppLocale(Locale appLocale){
		this.appLocale = appLocale;
	}

	public void setFileToOpen(File fileToOpen){
		this.fileToOpen = fileToOpen;
	}

}
