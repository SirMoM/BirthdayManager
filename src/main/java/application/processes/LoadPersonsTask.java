package application.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.model.Person;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person.PersonCouldNotBeParsedException;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LoadPersonsTask extends Task<LoadPersonsTask.Result> {
	private final BufferedReader reader;
	private final Logger LOG;
	private int prozess = 0;
	private final boolean csvFile;
	private final long MAX_Prozess;

	/**
	 * Base constructor
	 *
	 * @param file File with all the persons.
	 * @param csvFile if it is a CSV-file.
	 * @throws IOException If the file can't be read.
	 */
	public LoadPersonsTask(final File file, final boolean csvFile) throws IOException {
		super();
		this.LOG = LogManager.getLogger();
		this.reader = new BufferedReader(new FileReader(file));
		BufferedReader tempReader = new BufferedReader(new FileReader(file));

		long count = 0;
		while (tempReader.readLine() != null) {
			count++;
		}
		tempReader.close();

		LOG.info("Anzahl: " + count);
		this.MAX_Prozess = count;
		this.csvFile = csvFile;
	}

	@Override
	protected Result call() throws Exception {
		LOG.debug("Started " + this.getClass().getName());
		final ArrayList<Person> persons = new ArrayList<Person>((int) MAX_Prozess);
		final ArrayList<PersonCouldNotBeParsedException> exceptions = new ArrayList<PersonCouldNotBeParsedException>();
		if (this.reader == null) {
			this.LOG.fatal("There is no reader to get the CSV Data from!");
		}
		String line = this.reader.readLine();
			while (line != null) {
				if (!this.csvFile) {
						try {
							persons.add(Person.parseFromTXTLine(line, prozess));
							this.prozess++;
						}catch (PersonCouldNotBeParsedException personCouldNotBeParsedException){
							exceptions.add(personCouldNotBeParsedException);
							System.out.println(personCouldNotBeParsedException);
						}
				} else if (this.csvFile) {
					try {
						persons.add(Person.parseFromCSVLine(line, prozess));
						this.prozess++;
					}catch (PersonCouldNotBeParsedException personCouldNotBeParsedException){
						exceptions.add(personCouldNotBeParsedException);
					}
				}
				this.updateProgress(this.prozess, this.MAX_Prozess);
				line = this.reader.readLine();
			}
		reader.close();
		return new Result(persons, exceptions);

	}

	public static class Result{
		ArrayList<Person> persons;
		ArrayList<PersonCouldNotBeParsedException> errors;

		public Result(ArrayList<Person> persons, ArrayList<PersonCouldNotBeParsedException> errors){
			this.persons = persons;
			this.errors = errors;
		}

		public ArrayList<Person> getPersons() {
			return persons;
		}

		public ArrayList<PersonCouldNotBeParsedException> getErrors() {
			return errors;
		}
	}
}