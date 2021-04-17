package application.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import application.controller.MainController;
import application.model.Person;
import javafx.concurrent.Task;

public class LoadBirthdaysFromFileTask extends Task<List<Person>>{

	final private MainController mainController;
	private List<Person> persons = new ArrayList<Person>();

	/**
	 * @param mainController
	 */
	public LoadBirthdaysFromFileTask(MainController mainController){
		super();
		this.mainController = mainController;
	}

	public List<Person> getPersons(){
		return this.persons;
	}

	@Override
	protected List<Person> call() throws Exception{
		File fileToOpen = this.mainController.getSessionInfos().getFileToOpen();
		String line;
		try{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToOpen));
			while ((line = bufferedReader.readLine()) != null){
				Person person = new Person(line);
				this.getPersons().add(person);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		return this.getPersons();
	}

}
