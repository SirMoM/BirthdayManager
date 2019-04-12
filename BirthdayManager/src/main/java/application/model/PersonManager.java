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

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManager{
	final static Logger LOG = LogManager.getLogger(PersonManager.class);
	private static PersonManager personManagerSingelton = null;

	/**
	 * Static method to create instance of PersonManager class
	 *
	 * @return the only instance {@link PersonManager}
	 */
	public static PersonManager getInstance(){
		if(personManagerSingelton == null){
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
	private PersonManager(){
		this.setPersonDB(new ArrayList<Person>());
	}

	public void addNewPerson(final Person newPerson){
		this.personDB.add(newPerson);
		if(this.writeThru){
			this.saveToFile();
		}
	}

	/**
	 * Checks if the Save-File exists. If not create the File.
	 */
	private void checkSaveFile(){
		if(!this.saveFile.exists()){
			try{
				this.saveFile.createNewFile();
			} catch (final IOException ioException){
				LOG.catching(ioException);
			}
		}
	}

	/**
	 * @param person
	 */
	public void deletePerson(final Person person){
		this.personDB.remove(person);
		if(this.writeThru){
			this.saveToFile();
		}
	}

	public Person get(final int indexPerson){
		return this.personDB.get(indexPerson);
	}

	/**
	 * @return the personDB == the {@link ArrayList} which contains the Persons
	 */
	public List<Person> getPersonDB(){
		return this.personDB;
	}

	/**
	 * @return the saveFile
	 */
	public File getSaveFile(){
		return this.saveFile;
	}

//	/**
//	 * Populates the {@link ArrayList} which contains the Persons ==
//	 * {@link #personDB}
//	 *
//	 * @throws FileNotFoundException if the File is not found
//	 * @throws IOException           if a new File could not be read
//	 */
//	private void populateList() throws IOException, FileNotFoundException{
//		final LoadPersonsTask loadPersonsTask = new LoadPersonsTask(this.saveFile, this.saveFile.getAbsolutePath().endsWith(".csv"));
//		loadPersonsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>(){
//
//			@Override
//			public void handle(final WorkerStateEvent event){
//				PersonManager.this.personDB.clear();
//				PersonManager.this.personDB.addAll(loadPersonsTask.getValue());
//			}
//		});
//		new Thread(loadPersonsTask).start();
//
//	}

	/**
	 * Saves the {@link ArrayList} {@link #personDB} to the {@link #saveFile}
	 *
	 * @return a boolean which indicates if the save was completed
	 */
	public boolean save(){
		return this.saveToFile();
	}

	/**
	 * Saves the {@link ArrayList} {@link #personDB} to the selectedFile
	 *
	 * @param selectedFile a new saveFile
	 * @return a boolean which indicates if the save was completed
	 */
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

	/**
	 * Private saves the {@link ArrayList} {@link #personDB} to the
	 * {@link #saveFile}
	 *
	 * @return a boolean which indicates if the save was completed
	 */
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
		if(saveFile == null){
			LOG.info("Warn saveFile is null");
		} else if(!saveFile.exists()){
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

	/**
	 * @param indexPerson   the index of the person which will be updated
	 * @param updatedPerson the person which was updated
	 */
	public void updatePerson(final int indexPerson, final Person updatedPerson){
		this.personDB.get(indexPerson).setBirthday(updatedPerson.getBirthday());
		this.personDB.get(indexPerson).setName(updatedPerson.getName());
		this.personDB.get(indexPerson).setSurname(updatedPerson.getSurname());
		this.personDB.get(indexPerson).setMisc(updatedPerson.getMisc());
		if(this.writeThru){
			this.saveToFile();
		}
	}
}
