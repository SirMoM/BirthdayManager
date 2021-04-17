/**
 *
 */
package application.model;

import java.io.File;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.util.PropertieFields;
import application.util.PropertieManager;
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
	final static Logger LOG = LogManager.getLogger();

	private File fileToOpen;
	final private PropertieManager propertieManager;
	final private LangResourceManager langResources;
	private Locale appLocale;
	private final StringProperty fileToOpenName = new SimpleStringProperty();
	private StringProperty recentFileName = new SimpleStringProperty();

	private final ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
	private final ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();

	private final ObservableMap<Object, Object> birthdaysThisWeek = FXCollections.observableHashMap();
	private final ObservableMap<Integer, Person> birthdaysThisMonat = FXCollections.observableHashMap();

//	private SessionInfos(){
//		try{
//			// load locale or set Germany as default
//			final String savedLocaleProperty = this.propertieManager.getPropertie(PropertieFields.SAVED_LOCALE).replace(" ", "");
//			if(!savedLocaleProperty.isEmpty()){
//				final String[] savedLocalePropertySplit = savedLocaleProperty.split("_");
//				this.appLocale = new Locale(savedLocalePropertySplit[0], savedLocalePropertySplit[1]);
//			} else{
//				LOG.error("could not load saved_locale_property");
//			}
//		} catch (final IOException ioException){
//			LOG.catching(ioException);
//		} finally{
//			if(this.appLocale == null){
//				this.appLocale = Locale.GERMANY;
//			}
//			LOG.debug("Started in: " + this.appLocale);
//
//		}
//		this.langResources = new LangResourceManager(this.appLocale);
//		this.fileToOpenName = new SimpleStringProperty();
//		try{
//			this.getRecentFileName().set(new File(this.propertieManager.getPropertie(PropertieFields.LAST_OPEND)).getName());
//		} catch (final NullPointerException nullPointerException){
//			LOG.catching(Level.INFO, nullPointerException);
//			LOG.info("Don't worry just could not load recent File");
//		}
//	}

	/**
	 * Loads the saved properties or gets the default values
	 */
	public SessionInfos(){
		this.propertieManager = new PropertieManager();
		final String localePropertieString = this.propertieManager.getPropertie(PropertieFields.SAVED_LOCALE);
		LOG.debug("Loaded locale propertie " + localePropertieString);
		this.langResources = new LangResourceManager(new Locale(localePropertieString));

		try{
			this.getRecentFileName().set(new File(this.propertieManager.getPropertie(PropertieFields.LAST_OPEND)).getName());
		} catch (final NullPointerException nullPointerException){
			LOG.catching(Level.INFO, nullPointerException);
			LOG.info("Don't worry just could not load recent File");
		}
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

	/**
	 * @return fileToOpen
	 */
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

	public PropertieManager getPropertiesHandler(){
		return this.propertieManager;
	}

	/**
	 * @return the recentBirthdays
	 */
	public ObservableList<Person> getRecentBirthdays(){
		return this.recentBirthdays;
	}

	/**
	 * @return the recentFileName
	 */
	public StringProperty getRecentFileName(){
		return this.recentFileName;
	}

	/**
	 * resets the nextBirthdays and recentBirthdays
	 */
	public void resetSubLists(){
		this.nextBirthdays.clear();
		this.recentBirthdays.clear();
	}

	/**
	 * @param appLocale the appLocale to set
	 */
	public void setAppLocale(final Locale appLocale){
		this.appLocale = appLocale;
	}

	/**
	 * @param fileToOpen the File to open aka the save file
	 */
	public void setFileToOpen(final File fileToOpen){
		this.fileToOpen = fileToOpen;
		if(fileToOpen != null){
			this.getFileToOpenName().set(fileToOpen.getName());
		} else{
			this.getFileToOpenName().set(null);
		}
	}

	/**
	 * @param recentFileName the recentFileName to set
	 */
	public void setRecentFileName(final StringProperty recentFileName){
		this.recentFileName = recentFileName;
	}
}
