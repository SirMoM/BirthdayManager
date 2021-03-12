package application.processes;

import application.model.Person;
import application.model.Person.PersonCouldNotBeParsedException;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LoadPersonsTask extends Task<LoadPersonsTask.Result> {
    private final BufferedReader reader;
    private final Logger LOG;
    private final boolean csvFile;

    /**
     * Base constructor
     *
     * @param file    File with all the persons.
     * @param csvFile if it is a CSV-file.
     * @throws IOException If the file can't be read.
     */
    public LoadPersonsTask(final File file, final boolean csvFile) throws IOException {
        super();
        this.LOG = LogManager.getLogger();

        this.reader = new BufferedReader(new FileReader(file));
        this.csvFile = csvFile;
    }

    @Override
    protected Result call() throws Exception {
        LOG.debug("Started {}", this.getClass().getName());
        final List<Person> persons = new ArrayList<>();
        final List<PersonCouldNotBeParsedException> exceptions = new ArrayList<>();

        int lineNumber = 0;
        String line = this.reader.readLine();

        while (line != null) {
            if (!this.csvFile) {
                try {
                    persons.add(Person.parseFromTXTLine(line, lineNumber));
                    lineNumber++;
                } catch (PersonCouldNotBeParsedException personCouldNotBeParsedException) {
                    exceptions.add(personCouldNotBeParsedException);
                    LOG.debug(personCouldNotBeParsedException);
                }
            } else {
                try {
                    persons.add(Person.parseFromCSVLine(line, lineNumber));
                    lineNumber++;
                } catch (PersonCouldNotBeParsedException personCouldNotBeParsedException) {
                    exceptions.add(personCouldNotBeParsedException);
                }
            }
            line = this.reader.readLine();
        }
        reader.close();
        return new Result(persons, exceptions);

    }

    public static class Result {
        List<Person> persons;
        List<PersonCouldNotBeParsedException> errors;

        public Result(List<Person> persons, List<PersonCouldNotBeParsedException> errors) {
            this.persons = persons;
            this.errors = errors;
        }

        public List<Person> getPersons() {
            return persons;
        }

        public List<PersonCouldNotBeParsedException> getErrors() {
            return errors;
        }
    }
}