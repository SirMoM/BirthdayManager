/**
 *
 */
package application.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

	private File output;
	private File actual;

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
		assertThat(this.personDB, is(PersonManager.getInstance().getPersonDB()));
	}

	/**
	 * Test method for {@link application.model.PersonManager#save()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSaveCSV() throws IOException{
		this.output = new File("output.csv");
		this.output.createNewFile();

		this.actual = new File("output2.csv");
		this.actual.createNewFile();

		// arrange
		final FileWriter fileWriter = new FileWriter(this.actual);
		fileWriter.write("Ruben Michael Noah=25.03.1999");
		fileWriter.flush();
		fileWriter.close();
		PersonManager.getInstance().setSaveFile(this.output);
		PersonManager.getInstance().setPersonDB(this.personDB);

		// act
		PersonManager.getInstance().save();

		// assert
		FileAssert.assertEquals(this.output, this.actual);
	}

	/**
	 * Test method for {@link application.model.PersonManager#save()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testSaveTXT() throws IOException{
		this.output = new File("output.txt");
		this.output.createNewFile();

		this.actual = new File("output2.txt");
		this.actual.createNewFile();

		// arrange
		final FileWriter fileWriter = new FileWriter(this.actual);
		fileWriter.write("Ruben Michael Noah=25.03.1999");
		fileWriter.flush();
		fileWriter.close();
		PersonManager.getInstance().setSaveFile(this.output);
		PersonManager.getInstance().setPersonDB(this.personDB);

		// act
		PersonManager.getInstance().save();

		// assert
		FileAssert.assertEquals(this.output, this.actual);
	}

	/**
	 * Test method for
	 * {@link application.model.PersonManager#updatePerson(application.model.Person, application.model.Person)}.
	 */
	@Test
	public void testUpdatePerson(){
		fail("Not yet implemented"); // TODO
	}
}
