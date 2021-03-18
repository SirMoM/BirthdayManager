package application.processes;

import application.model.Person;
import application.model.PersonManager;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class SaveBirthdaysToFileTask extends Task<Boolean> {
    private final Logger LOG;
    File saveFile;

    /**
     * This is used to save the birthdays as.
     *
     * @param file the File
     */
    public SaveBirthdaysToFileTask(final File file) {
        this.LOG = LogManager.getLogger(this.getClass().getName());
        this.LOG.info("Save to " + file.getAbsolutePath());
        this.saveFile = file;
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected Boolean call() {
        LOG.debug("Started " + this.getClass().getName());

        List<Person> persons = PersonManager.getInstance().getPersons();

        // Ensure constant safe file
        persons.sort(Comparator.comparingInt(person -> person.getBirthday().getDayOfYear()));

        try (FileWriter fileWriter = new FileWriter(saveFile)) {
            if (this.saveFile.getAbsolutePath().endsWith(".csv")) {
                LOG.debug("Saving to CSV");
                for (Person person : persons) {
                    fileWriter.write(person.toCSVString());
                    fileWriter.write(System.lineSeparator());
                    fileWriter.flush();
                }
            } else {
                LOG.debug("Saving to TXT");
                for (Person person : persons) {
                    fileWriter.write(person.toTXTString());
                    fileWriter.write(System.lineSeparator());
                    fileWriter.flush();
                }
            }
            return true;
        } catch (IOException ioException) {
            LOG.catching(ioException);
            return false;
        } finally {
            LOG.debug(this.getClass().getName() + " ENDED");
        }
    }
}
