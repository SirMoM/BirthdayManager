/**
 * 
 */
package application.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import application.util.ConfigFields;
import application.util.ConfigHandler;
import application.util.localisation.LangResourceManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SessionInfos{
	private File fileToOpen;
	private StringProperty fileToOpenName;
	private ConfigHandler configHandler;
	private ObservableList<Person> allPersons = FXCollections.observableArrayList();
	private ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
	private ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();

	private ObservableMap<Object, Object> birthdaysThisWeek = FXCollections.observableHashMap();
	private ObservableMap<Integer, Person> birthdaysThisMonat = FXCollections.observableHashMap();
	private Locale appLocale;
	final private LangResourceManager langResources;

	/**
	 * @param configHandler
	 * @throws IOException
	 */
	public SessionInfos(){
		super();
		this.fileToOpenName = new SimpleStringProperty();
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

	/**
	 * @return the birthdaysThisMonat
	 */
	public ObservableMap<Integer, Person> getBirthdaysThisMonat(){
		return this.birthdaysThisMonat;
	}

	/**
	 * @return the birthdaysThisWeek
	 */
	public ObservableMap<Object, Object> getBirthdaysThisWeek(){
		return this.birthdaysThisWeek;
	}

	public ConfigHandler getConfigHandler(){
		return this.configHandler;
	}

	public File getFileToOpen(){
		return this.fileToOpen;
	}

	/**
	 * @return the fileToOpenName
	 */
	public StringProperty getFileToOpenName(){
		return this.fileToOpenName;
	}

	/**
	 * @return the langResources
	 */
	public LangResourceManager getLangResourceManager(){
		return this.langResources;
	}

	/**
	 * @return the langResources
	 */
	public LangResourceManager getLangResources(){
		return this.langResources;
	}

	/**
	 * @return the nextBirthdays
	 */
	public ObservableList<Person> getNextBirthdays(){
		return this.nextBirthdays;
	}

	/**
	 * @return the recentBirthdays
	 */
	public ObservableList<Person> getRecentBirthdays(){
		return this.recentBirthdays;
	}

	public void resetSubLists(){
		this.allPersons.clear();
		this.nextBirthdays.clear();
		this.recentBirthdays.clear();
	}

	/**
	 * @param allPersons the allPersons to set
	 */
	public void setAllPersons(List<Person> allPersons){
		this.allPersons.setAll(allPersons);
	}

	/**
	 * @param appLocale the appLocale to set
	 */
	public void setAppLocale(Locale appLocale){
		this.appLocale = appLocale;
	}

	public void setFileToOpen(File fileToOpen){
		this.fileToOpen = fileToOpen;
		this.getFileToOpenName().set(fileToOpen.getName());
	}
}
