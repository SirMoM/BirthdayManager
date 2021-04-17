/**
 *
 */
package application.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManager {
	final static Logger LOG = LogManager.getLogger(PersonManager.class);
	private static PersonManager personManagerSingelton = null;
	public SimpleBooleanProperty changesProperty = new SimpleBooleanProperty(false);

	/**
	 * Static method to create instance of PersonManager class
	 *
	 * @return the only instance {@link PersonManager}
	 */
	public static PersonManager getInstance() {
		if (personManagerSingelton == null) {
			personManagerSingelton = new PersonManager();
		}
		return personManagerSingelton;
	}

	public boolean writeThru = false;
	private List<Person> personDB;

	private File saveFile;

	/**
	 * Private constructor restricted to this class itself.
	 */
	private PersonManager() {
		this.setPersonDB(new ArrayList<Person>());
	}

	public void addNewPerson(final Person newPerson) {
		this.personDB.add(newPerson);
		changesProperty.set(true);
//		if (this.writeThru) {TODO DELETE THIS
//			boolean succsesfull = this.saveToFile();
//		}

	}

	/**
	 * Checks if the Save-File exists. If not create the File. TODO DELETE THIS
	 */
	private void checkSaveFile() {
		if (!this.saveFile.exists()) {
			try {
				this.saveFile.createNewFile();
			} catch (final IOException ioException) {
				LOG.catching(ioException);
			}
		}
	}

	/**
	 * @param person
	 */
	public void deletePerson(final Person person) {
		this.personDB.remove(person);
		this.changesProperty.set(true);
//		if (this.writeThru) {TODO DELETE THIS
//			this.saveToFile();
//		}
	}

	public Person getPersonFromIndex(final int indexPerson) {
		return this.personDB.get(indexPerson);
	}

	/**
	 * @return the personDB == the {@link ArrayList} which contains the Persons
	 */
	public List<Person> getPersons() {
		return this.personDB;
	}

	/**
	 * TODO DELETE THIS
	 * 
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return this.saveFile;
	}

	/**
	 * Saves the {@link ArrayList} {@link #personDB} to the {@link #saveFile} TODO
	 * DELETE THIS
	 *
	 * @return a boolean which indicates if the save was completed
	 */
	public boolean save() {
		return this.saveToFile();
	}

	/**
	 * Saves the {@link ArrayList} {@link #personDB} to the selectedFile TODO DELETE
	 * THIS
	 *
	 * @param selectedFile a new saveFile
	 * @return a boolean which indicates if the save was completed
	 */
	public boolean save(final File selectedFile) {
		try {
			final FileWriter fileWriter = new FileWriter(selectedFile);
			if (this.saveFile.getAbsolutePath().endsWith(".csv")) {
				LOG.debug("Saving to CSV");
				for (final Person person : this.personDB) {
					fileWriter.write(person.toCSVString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}
			} else {
				LOG.debug("Saving to TXT");
				for (final Person person : this.personDB) {
					fileWriter.write(person.toTXTString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}

			}
			fileWriter.close();
		} catch (final IOException ioException) {
			LOG.catching(ioException);
			return false;
		}
		return true;
	}

	/**
	 * Private saves the {@link ArrayList} {@link #personDB} to the TODO DELETE THIS
	 * {@link #saveFile}
	 *
	 * @return a boolean which indicates if the save was completed
	 */
	private boolean saveToFile() {
		if (saveFile == null) {
			return false;
		}

		this.saveFile.delete();
		LOG.info("Deleted File", this.saveFile);
		this.checkSaveFile();

		try {
			final FileWriter fileWriter = new FileWriter(this.saveFile);
			if (this.saveFile.getAbsolutePath().endsWith(".csv")) {
				LOG.debug("Saving to CSV");
				for (final Person person : this.personDB) {
					fileWriter.write(person.toCSVString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}
			} else {
				LOG.debug("Saving to TXT");
				for (final Person person : this.personDB) {
					fileWriter.write(person.toTXTString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}

			}
			fileWriter.close();
		} catch (final IOException ioException) {
			LOG.catching(ioException);
			return false;
		}
		return true;
	}

	/**
	 * @param personDB the personDB to set
	 */
	public void setPersonDB(final List<Person> personDB) {
		this.personDB = personDB;
	}

	/**
	 * @param saveFile the saveFile to set TODO DELETE THIS
	 */
	public void setSaveFile(final File saveFile) {
		this.saveFile = saveFile;
		if (saveFile == null) {
			LOG.info("Warn saveFile is null");
		} else if (!saveFile.exists()) {
			try {
				saveFile.createNewFile();
			} catch (final IOException ioException) {
				LOG.catching(ioException);
			}
		}
	}

	/**
	 * @param saveFile the saveFile to set TODO DELETE THIS
	 */
	public void setSaveFilePlain(final File saveFile) {
		this.saveFile = saveFile;
	}

	/**
	 * @param indexPerson   the index of the person which will be updated
	 * @param updatedPerson the person which was updated
	 */
	public void updatePerson(final int indexPerson, final Person updatedPerson) {
		this.personDB.get(indexPerson).setBirthday(updatedPerson.getBirthday());
		this.personDB.get(indexPerson).setName(updatedPerson.getName());
		this.personDB.get(indexPerson).setSurname(updatedPerson.getSurname());
		this.personDB.get(indexPerson).setMisc(updatedPerson.getMisc());
		this.changesProperty.set(true);
//		if (this.writeThru) {
//			this.saveToFile();
//		}
	}
}
