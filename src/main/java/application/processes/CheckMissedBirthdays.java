package application.processes;

import application.model.Person;
import application.model.PersonManager;
import application.util.PropertyFields;
import application.util.PropertyManager;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Ruben
 * @since 2019-11-02
 */
public class CheckMissedBirthdays extends Task<List<Person>> {

	
	private List<Person> personDB;
	private Logger LOG;
	private int timeInWaiting;

	public CheckMissedBirthdays() {
		this.LOG = LogManager.getLogger(this.getClass().getName());
		if (PersonManager.getInstance().getPersons() != null && !PersonManager.getInstance().getPersons().isEmpty()) {
			this.personDB = PersonManager.getInstance().getPersons();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected List<Person> call() throws Exception {
		Thread.sleep(500);
		
		// Checks if Data is there else aborts this Task.
		while (personDB == null || PersonManager.getInstance().getPersons().isEmpty()) {
			personDB = PersonManager.getInstance().getPersons();
			LOG.info("Waiting for personenDB to be filled!");

			Thread.sleep(500);
			this.timeInWaiting += 500;
			if (this.timeInWaiting > 10000) {
				this.cancel();
				LOG.debug("Thread canceled because it took too long to wait for the list of people!");
			}
		}
		
		// Get and manage last visit to compare with today.
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate lastVisit = LocalDate.parse(PropertyManager.getProperty(PropertyFields.LAST_VISIT), dateTimeFormatter);
		PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_VISIT, LocalDate.now().format(dateTimeFormatter));
		PropertyManager.getInstance().storeProperties("Change last visit");
		
		
		long daysScinceLastVisit = ChronoUnit.DAYS.between(lastVisit, LocalDate.now());
		if (daysScinceLastVisit >= 2) {
			return getBrithdaysSince(lastVisit, LocalDate.now());
		}
		failed();
		return new ArrayList<>();
	}

	private List<Person> getBrithdaysSince(LocalDate lastVisit, LocalDate now) {
		List<Person> skippedBirthdays = new ArrayList<>();
		for (Person person : personDB) {
			if (person.getBirthday().withYear(now.getYear()).isAfter(lastVisit) && person.getBirthday().withYear(now.getYear()).isBefore(now)) {
				LOG.debug("{0} missed!", person.toExtendedString());
				skippedBirthdays.add(person);
			}
		}
		return skippedBirthdays;
	}

}
