/**
 *
 */
package application.model;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.util.BirthdayComparator;
import application.util.PropertieFields;
import application.util.PropertieManager;
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
	private Locale appLocale;
	private final StringProperty fileToOpenName = new SimpleStringProperty();
	private StringProperty recentFileName = new SimpleStringProperty();

	private ObservableList<Person> nextBirthdays = FXCollections.observableArrayList();
	private final ObservableList<Person> recentBirthdays = FXCollections.observableArrayList();

	private final ObservableMap<DayOfWeek, Person> birthdaysThisWeek = FXCollections.observableHashMap();
	private final ObservableList<Person> birthdaysThisMonat = FXCollections.observableArrayList();

	/**
	 * Loads the saved properties or gets the default values
	 */
	public SessionInfos(){
		this.propertieManager = new PropertieManager();
		final String localePropertieString = this.propertieManager.getPropertie(PropertieFields.SAVED_LOCALE);
		LOG.debug("Loaded locale propertie " + localePropertieString);

		try{
			this.getRecentFileName().set(new File(this.propertieManager.getPropertie(PropertieFields.LAST_OPEND)).getName());
		} catch (final NullPointerException nullPointerException){
			LOG.catching(Level.TRACE, nullPointerException);
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
	public ObservableList<Person> getBirthdaysThisMonat(){
		return this.birthdaysThisMonat;
	}

	/**
	 * @return the birthdaysThisWeek
	 */
	public ObservableMap<DayOfWeek, Person> getBirthdaysThisWeek(){
		return this.birthdaysThisWeek;
	}

	/**
	 * @return the fileToOpenName
	 */
	public StringProperty getFileToOpenName(){
		return this.fileToOpenName;
	}

	/**
	 * @return the nextBirthdays
	 */
	public ObservableList<Person> getNextBirthdays(){
		return this.nextBirthdays;
	}

	/**
	 * @return the propertieManager
	 */
	public PropertieManager getPropertieManager(){
		return this.propertieManager;
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

		for(final Person person : temp){
			if(person.getBirthday().get(IsoFields.WEEK_BASED_YEAR) == week){
				this.birthdaysThisWeek.put(person.getBirthday().getDayOfWeek(), person);
			}

		}

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
		final List<Person> upcomming = new ArrayList<Person>();
		final List<Person> after = new ArrayList<Person>();

		for(final Person person : temp){
			if(person.getBirthday().getDayOfYear() >= LocalDate.now().getDayOfYear()){
				upcomming.add(person);
			} else{
				after.add(person);
			}

		}

		upcomming.sort(new BirthdayComparator(false));
		after.sort(new BirthdayComparator(false));
		int i = 0;
		for(; i < NEXT_BIRTHDAYS_COUNT; i++){
			try{
				this.getNextBirthdays().add(upcomming.get(i));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for next", indexOutOfBoundsException);
				break;
			}
		}
		int j = 0;
		for(; j < NEXT_BIRTHDAYS_COUNT - i; j++){
			try{
				this.getNextBirthdays().add(after.get(j));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for next", indexOutOfBoundsException);
				break;
			}
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
		final List<Person> upcomming = new ArrayList<Person>();
		final List<Person> after = new ArrayList<Person>();

		for(final Person person : temp){
//			if (person.getBirthday().getDayOfYear() >= LocalDate.now().getDayOfYear() ) {
			if(person.getBirthday().getDayOfYear() >= LocalDate.now().getDayOfYear()){
				upcomming.add(person);
			} else{
				after.add(person);
			}

		}

		upcomming.sort(new BirthdayComparator(false));
		after.sort(new BirthdayComparator(false));
		int i = 0;
		for(; i < NEXT_BIRTHDAYS_COUNT; i++){
			try{
				this.getRecentBirthdays().add(after.get((after.size() - 1) - i));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for recent", indexOutOfBoundsException);
				break;
			}
		}
		int j = 0;
		for(; j < NEXT_BIRTHDAYS_COUNT - i; j++){
			try{
				this.getRecentBirthdays().add(after.get((upcomming.size() - 1) - j));
			} catch (final IndexOutOfBoundsException indexOutOfBoundsException){
				LOG.debug("Probably not enought Persons to geather the 10 birthdays for recent", indexOutOfBoundsException);
				break;
			}
		}
	}

	/**
	 * Resets and updates all sublists
	 * <p>
	 *
	 * <b>Sublists</b>
	 * <ul>
	 * <li>{@link #updateNextBirthdays}
	 * <li>{@link #recentBirthdays}
	 * <li>{@link #birthdaysThisWeek}
	 * <li>{@link #birthdaysThisMonat}
	 * </ul>
	 */
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
