package application.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.Person;
import javafx.concurrent.Task;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LoadPersonsTask extends Task<List<Person>>{
	private final BufferedReader reader;
	private final Logger LOG;
	private final boolean HEADER = false;
	private int prozess = 0;
	private final long MAX_Prozess;
	private final boolean csvFile;

	/**
	 * Base constructor
	 *
	 * @throws IOException
	 */
	public LoadPersonsTask(final File file, final boolean csvFile) throws IOException{
		super();
		this.LOG = LogManager.getLogger();
		this.reader = new BufferedReader(new FileReader(file));
		this.MAX_Prozess = Files.lines(file.toPath()).count();
		this.csvFile = csvFile;

	}

	@Override
	protected List<Person> call() throws Exception{
		final ArrayList<Person> persons = new ArrayList<Person>(10);
		if(this.reader == null){
			this.LOG.fatal("There is no reader to get the CSV Data from!");
		}
		String line = this.reader.readLine();
		while (line != null){
			if(!this.csvFile){
				persons.add(Person.parseFromTXTLine(line));
				this.prozess++;
			} else if(this.csvFile){
				persons.add(Person.parseFromCSVLine(line, this.prozess));
				this.prozess++;
			}
			this.updateValue(persons);
			this.updateProgress(this.prozess, this.MAX_Prozess);
			line = this.reader.readLine();
		}
		return persons;
	}
}