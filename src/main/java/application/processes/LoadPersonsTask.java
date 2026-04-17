package application.processes;

import application.model.Person;
import application.model.Person.PersonCouldNotBeParsedException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class LoadPersonsTask extends Task<LoadPersonsTask.Result> {
  private static final Logger LOG = LogManager.getLogger(LoadPersonsTask.class.getName());
  private final BufferedReader reader;
  private final boolean csvFile;

  /**
   * Base constructor
   *
   * @param file File with all the persons.
   * @param csvFile if it is a CSV-file.
   * @throws IOException If the file can't be read.
   */
  public LoadPersonsTask(final File file, final boolean csvFile) throws IOException {
    super();
    this.reader = openReader(file);
    this.csvFile = csvFile;
  }

  private static BufferedReader openReader(final File file) throws IOException {
    BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    bufferedReader.mark(1);
    int firstChar = bufferedReader.read();
    if (firstChar != '\uFEFF' && firstChar != -1) {
      bufferedReader.reset();
    }
    return bufferedReader;
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
