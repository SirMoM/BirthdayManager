/**
 *
 */
package application.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junitx.framework.FileAssert;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManagerTest{

	private File fileToTest;

	final private Person person = new Person("Noah", "Ruben", "Michael", LocalDate.of(1999, 3, 25));

	private List<Person> personDB;

	@After
	public void afterEachTest(){
		this.personDB.clear();
	}

	@Before
	public void beforeEachTest(){
		this.personDB = new ArrayList<Person>();
		this.personDB.add(this.person);

		for(int i = 0; i < 10; i++){
			this.personDB.add(new Person("Test", "test", String.valueOf(i), LocalDate.now()));
		}
		PersonManager.getInstance().setPersonDB(this.personDB);
	}

	/**
	 * Test method for
	 * {@link application.model.PersonManager#deletePerson(application.model.Person)}.
	 */
	@Test
	public void testDeletePerson(){
		PersonManager.getInstance().deletePerson(this.person);
		this.personDB.remove(this.person);
		assertThat(this.personDB, is(PersonManager.getInstance().getPersons()));
	}

	/**
	 * Test method for {@link application.model.PersonManager#save()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSaveCSV() throws IOException{
		// arrange
		final File tempFile = File.createTempFile("output", ".csv");
//		tempFile.deleteOnExit();
		this.fileToTest = File.createTempFile("output", ".csv");
		this.fileToTest.deleteOnExit();

		final FileWriter fileWriter = new FileWriter(tempFile);
		fileWriter.write(this.person.toCSVString());
		fileWriter.flush();
		fileWriter.close();

		final ArrayList<Person> tempList = new ArrayList<Person>();
		tempList.add(this.person);
		PersonManager.getInstance().setSaveFile(this.fileToTest);
		PersonManager.getInstance().setPersonDB(tempList);

//		 act TODO FIX with thread
		
//		PersonManager.getInstance().save();

		// assert
		FileAssert.assertEquals(tempFile, this.fileToTest);
	}

	/**
	 * Test method for {@link application.model.PersonManager#save()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSaveTXT() throws IOException{
		// arrange
		final File tempFile = File.createTempFile("output", ".csv");
		tempFile.deleteOnExit();
		this.fileToTest = File.createTempFile("output", ".csv");
		this.fileToTest.deleteOnExit();

		final FileWriter fileWriter = new FileWriter(tempFile);
		fileWriter.write(this.person.toCSVString());
		fileWriter.flush();
		fileWriter.close();

		final ArrayList<Person> tempList = new ArrayList<Person>();
		tempList.add(this.person);
		PersonManager.getInstance().setSaveFile(this.fileToTest);
		PersonManager.getInstance().setPersonDB(tempList);

//		// act TODO FIX with 
//		PersonManager.getInstance().save();

		// assert
		FileAssert.assertEquals(tempFile, this.fileToTest);
	}

	/**
	 * Test method for
	 * {@link application.model.PersonManager#updatePerson(application.model.Person, application.model.Person)}.
	 */
	@Test
	public void testUpdatePerson(){
		PersonManager.getInstance().getPersons().get(0).setName("TEST");
		this.personDB.get(0).setName("TEST");
		assertThat(this.personDB, is(PersonManager.getInstance().getPersons()));
	}
}
