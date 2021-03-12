/**
 *
 */
package application.processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import application.model.PersonManager;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SaveBirthdaysToFileTask extends Task<Boolean> {
	File saveFile;
	private Logger LOG;

	/**
	 * This is used to save the birthdays as.
	 *
	 * @param file the File
	 */
	public SaveBirthdaysToFileTask(final File file) {
		this.LOG = LogManager.getLogger(this.getClass().getName());
		this.LOG.info("Save to " + file.getAbsolutePath());
		this.saveFile = file;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected Boolean call() {
		LOG.debug("Started " + this.getClass().getName());

		List<Person> persons = PersonManager.getInstance().getPersons();

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(saveFile);

			if (this.saveFile.getAbsolutePath().endsWith(".csv")) {
				LOG.debug("Saving to CSV");

				for (Person person: persons) {
					fileWriter.write(person.toCSVString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}
			} else {
				LOG.debug("Saving to TXT");
				for (int i = 0; i < persons.size(); i++) {
					fileWriter.write(persons.get(i).toTXTString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}

			}
			fileWriter.close();
			return true;
		} catch (FileNotFoundException fileNotFoundException) {
			LOG.catching(fileNotFoundException);
			return false;
		} catch (IOException ioException) {
			LOG.catching(ioException);
			return false;
		} finally {
			LOG.debug(this.getClass().getName() + " ENDED");
		}

//		if (this.saveFile == null) {
//
//			LOG.debug(this.getClass().getName() + " ENDED");
//			return PersonManager.getInstance().save();
//		} else {
//			LOG.debug(this.getClass().getName() + " ENDED");
//			return PersonManager.getInstance().save(this.saveFile);
//		}
	}

}
