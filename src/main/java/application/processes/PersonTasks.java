package application.processes;

import application.model.Person;
import application.model.PersonManager;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class PersonTasks<G> extends Task<G> {

    private static final Logger LOG = LogManager.getLogger(PersonTasks.class.getName());
    protected long timeInWaiting = 0;
    protected long timeToWait = 500;
    /**
     * How long the process has be waiting
     */
    private long waitTimeLimit = 30000;

    protected PersonTasks(final long waitTimeLimit) {
        this.waitTimeLimit = waitTimeLimit;
    }

    protected PersonTasks() {
    }

    protected List<Person> getPersons() throws Exception {
        boolean isEmpty = PersonManager.getInstance().getPersons().isEmpty();
        while (isEmpty) {
            try {
                //noinspection BusyWait
                Thread.sleep(timeToWait);
            } catch (InterruptedException interruptedException) {
                LOG.catching(Level.WARN, interruptedException);
                Thread.currentThread().interrupt();
            }

            timeToWait += timeToWait;
            timeInWaiting += timeToWait;
            LOG.info("Waiting for personDB to be filled! Waiting since {} ms", timeInWaiting);
            if (timeInWaiting > waitTimeLimit) {
                LOG.info("Thread canceled because it took too long to wait for the list of people!");
                this.cancel();
            }
            isEmpty = PersonManager.getInstance().getPersons().isEmpty();
        }
        LOG.info("PersonDB filled! Wait time was {} ms", timeInWaiting);
        return PersonManager.getInstance().getPersons();
    }
}
