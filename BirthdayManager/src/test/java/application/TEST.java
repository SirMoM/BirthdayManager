package application;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import application.model.Person;
import application.model.PersonManager;

public class TEST{

	public static void main(final String[] args) throws IOException{
		final ArrayList<Person> personDB = new ArrayList<Person>();
		for(int i = 0; i < 10; i++){
			personDB.add(new Person("Test", "test", String.valueOf(i), LocalDate.now()));
		}
//		final File fileToTest = File.createTempFile("output", ".csv");
//		fileToTest.deleteOnExit();
		final File fileToTest = new File("test");
		PersonManager.getInstance().setSaveFile(fileToTest);
		PersonManager.getInstance().setPersonDB(personDB);
		PersonManager.getInstance().save();

	}

}
