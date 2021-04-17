/**
 *
 */
package application.model;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.util.BirthdayComparator;
import application.util.PropertieFields;
import application.util.PropertieManager;
import application.util.localisation.LangResourceManager;
import javafx.application.Platform;
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

	final private PropertieManager propertieManager;
	final private LangResourceManager langResources;
	private Locale appLocale;
	private final StringProperty fileToOpenName = new SimpleStringProperty();
	private StringProperty recentFileName = new SimpleStringProperty();

	private ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
	private final ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();

	private final ObservableMap<Object, Object> birthdaysThisWeek = FXCollections.observableHashMap();
	private final ObservableMap<Integer, Person> birthdaysThisMonat = FXCollections.observableHashMap();

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
	 * @param nextBirthdays the nextBirthdays to set
	 */
	public void setNextBirthdays(final ObservableList<Person> nextBirthdays){
		this.nextBirthdays = nextBirthdays;
	}

	/**
	 * @param recentFileName the recentFileName to set
	 */
	public void setRecentFileName(final StringProperty recentFileName){
		this.recentFileName = recentFileName;
	}

	/**
	 * Fills the BirthdaysThisMonth
	 *
	 * @param temp the {@link List} of persons where the Birthdays this month are
	 *             extracted
	 */
	private void updateBirthdaysThisMonth(){
		// TODO updateBirthdaysThisMonth
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		temp.sort(new BirthdayComparator(true));
	}

	/**
	 * Fills the BirthdaysThisWeek
	 *
	 * @param temp the {@link List} of persons where the Birthdays this week are
	 *             extracted
	 */
	private void updateBirthdaysThisWeek(){
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		temp.sort(new BirthdayComparator(true));

		final int week = LocalDate.now().get(IsoFields.WEEK_BASED_YEAR);

	}

	/**
	 * Fills the NextBirthdays
	 *
	 * @param temp the {@link List} of persons where the next Birthdays are
	 *             extracted
	 */
	private void updateNextBirthdays(){
		final int NEXT_BIRTHDAYS_COUNT = Integer.parseInt(this.getPropertiesHandler().getPropertie(PropertieFields.SHOW_BIRTHDAYS_COUNT));
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		temp.sort(new BirthdayComparator(true));
		final int birthdaysSize = temp.size() - 1;
		int i = 0;
		for(; i < NEXT_BIRTHDAYS_COUNT; i++){
			final Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().getDayOfYear() < LocalDate.now().getDayOfYear()){
				break;
			} else{
				// Avoid throwing IllegalStateException by running from a non-JavaFX thread.
				Platform.runLater(() -> {
					this.getNextBirthdays().add(tempPerson);
				});
			}
		}
		for(; i < NEXT_BIRTHDAYS_COUNT; i++){
			final Person tempPerson = temp.get(birthdaysSize - i);
			// Avoid throwing IllegalStateException by running from a non-JavaFX thread.
			Platform.runLater(() -> {
				this.getNextBirthdays().add(tempPerson);
			});
		}

	}

	/**
	 * Fills the RecentBirthdays
	 *
	 * @param temp the {@link List} of persons where the Birthdays are listed
	 */
	private void updateRecentBirthdays(){
		final int NEXT_BIRTHDAYS_COUNT = Integer.parseInt(this.getPropertiesHandler().getPropertie(PropertieFields.SHOW_BIRTHDAYS_COUNT));
		final List<Person> temp = PersonManager.getInstance().getPersonDB();
		temp.sort(new BirthdayComparator(true));
		int i = 0;

		for(; i > NEXT_BIRTHDAYS_COUNT; i++){
			final Person tempPerson = temp.get(i);
			if(tempPerson.getBirthday().getDayOfYear() < LocalDate.now().getDayOfYear()){
				this.getRecentBirthdays().add(tempPerson);
			} else{
				LOG.warn(tempPerson.toExtendedString() + "not added to recent! ");
			}
		}
	}

	public void updateSubLists(){
		this.resetSubLists();
		try{
			this.updateNextBirthdays();
			this.updateRecentBirthdays();
			this.updateBirthdaysThisWeek();
			this.updateBirthdaysThisMonth();
		} catch (final IndexOutOfBoundsException outOfBoundsException){
			LOG.catching(Level.INFO, outOfBoundsException);
			LOG.info("Probably not enought Persons to geather the next 10 birthdays");
		}
	}

}
