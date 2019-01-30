package application.model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TEST{

	public static void main(final String[] args) throws IOException{
		final Person person = new Person("Noah", "Ruben", "Michael", LocalDate.of(1999, 3, 25));
		final Person person1 = new Person("Noah", "Ruben", "1", LocalDate.of(1999, 3, 25));
		final File output = new File("output.csv");

		final PersonManager personManager = PersonManager.getInstance();

		final ArrayList<Person> personDB = new ArrayList<Person>();
		personDB.add(person);

		personManager.setSaveFile(output);
		personManager.setPersonDB(personDB);

		personManager.save();
	}

}
