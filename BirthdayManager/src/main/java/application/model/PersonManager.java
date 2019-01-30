/**
 *
 */
package application.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManager{
	final static Logger LOG = LogManager.getLogger(PersonManager.class);
	private static PersonManager personManagerSingelton = null;

	// static method to create instance of PersonManager class
	public static PersonManager getInstance(){
		if(personManagerSingelton == null){
			personManagerSingelton = new PersonManager();
		}
		return personManagerSingelton;
	}

	public boolean writeThru = false;
	private List<Person> personDB;

	private File saveFile;

	// private constructor restricted to this class itself
	private PersonManager(){
		this.setPersonDB(new ArrayList<Person>());
	}

	private void checkSaveFile(){
		if(!this.saveFile.exists()){
			try{
				this.saveFile.createNewFile();
			} catch (final IOException ioException){
				LOG.catching(ioException);
			}
		}
	}

	public void deletePerson(final Person person){
		this.personDB.remove(person);
		if(this.writeThru){
			this.saveToFile();
		}
	}

	/**
	 * @return the personDB
	 */
	public List<Person> getPersonDB(){
		return this.personDB;
	}

	private void populateList() throws IOException, FileNotFoundException{
		this.personDB.clear();
		String line;
		final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.saveFile));
		while ((line = bufferedReader.readLine()) != null){
			this.personDB.add(new Person(line));
		}
		bufferedReader.close();
	}

	public boolean save(){
		return this.saveToFile();
	}

	public boolean save(final File selectedFile){
		try{
			final FileWriter fileWriter = new FileWriter(selectedFile);
			if(this.saveFile.getAbsolutePath().endsWith(".csv")){
				LOG.debug("Saving to CSV");
				for(final Person person : this.personDB){
					fileWriter.write(person.toCSVString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}
			} else{
				LOG.debug("Saving to TXT");
				for(final Person person : this.personDB){
					fileWriter.write(person.toTXTString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}

			}
			fileWriter.close();
		} catch (final IOException ioException){
			LOG.catching(ioException);
			return false;
		}
		return true;
	}

	private boolean saveToFile(){
		this.saveFile.delete();
		LOG.info("Deleted File", this.saveFile);
		this.checkSaveFile();

		try{
			final FileWriter fileWriter = new FileWriter(this.saveFile);
			if(this.saveFile.getAbsolutePath().endsWith(".csv")){
				LOG.debug("Saving to CSV");
				for(final Person person : this.personDB){
					fileWriter.write(person.toCSVString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}
			} else{
				LOG.debug("Saving to TXT");
				for(final Person person : this.personDB){
					fileWriter.write(person.toTXTString());
					fileWriter.write(System.lineSeparator());
					fileWriter.flush();
				}

			}
			fileWriter.close();
		} catch (final IOException ioException){
			LOG.catching(ioException);
			return false;
		}
		return true;
	}

	/**
	 * @param personDB the personDB to set
	 */
	public void setPersonDB(final List<Person> personDB){
		this.personDB = personDB;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(final File saveFile){
		this.saveFile = saveFile;
		if(saveFile.exists()){
			try{
				this.populateList();
			} catch (final IOException ioException){
				LOG.catching(ioException);
			}
		} else{
			try{
				saveFile.createNewFile();
			} catch (final IOException ioException){
				LOG.catching(ioException);
			}
		}
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFilePlain(final File saveFile){
		this.saveFile = saveFile;
	}

	public void updatePerson(final Person personToUpdate, final Person updatedPerson){
		this.personDB.set(this.personDB.indexOf(personToUpdate), updatedPerson);
		if(this.writeThru){
			this.saveToFile();
		}
	}
}
