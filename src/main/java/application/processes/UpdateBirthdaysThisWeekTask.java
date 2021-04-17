/** */
package application.processes;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAWeek;
import application.util.BirthdayComparator;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class UpdateBirthdaysThisWeekTask extends Task<List<PersonsInAWeek>> {

  private final Logger LOG;
  private final int week;
  private List<Person> personDB = null;
  private int timeInWaiting;

  /** */
  public UpdateBirthdaysThisWeekTask() {
    this.LOG = LogManager.getLogger(this.getClass().getName());
    this.week = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    this.LOG.info("Gathering the Birthdays for this week: {}", this.week);

    if (PersonManager.getInstance().getPersons() != null
        && !PersonManager.getInstance().getPersons().isEmpty()) {
      this.personDB = PersonManager.getInstance().getPersons();
    }
  }

  /**
   * Fills the BirthdaysThisWeek
   *
   * @see javafx.concurrent.Task#call()
   */
  @Override
  protected List<PersonsInAWeek> call() throws Exception {
    this.LOG.debug("Started " + this.getClass().getName());
    while (this.personDB == null || PersonManager.getInstance().getPersons().isEmpty()) {
      this.personDB = PersonManager.getInstance().getPersons();
      this.LOG.info("Waiting for personenDB to be filled!");
      Thread.sleep(500);
      this.timeInWaiting += 500;
      if (this.timeInWaiting > 10000) {
        this.cancel();
        LOG.debug("Thread canceled because it took too long to wait for the list of people!");
      }
    }

    final List<Person> birthdaysThisWeek = new ArrayList<>();

    this.personDB.sort(new BirthdayComparator(true));

    for (final Person person : this.personDB) {
      final LocalDate birthday = person.getBirthday().withYear(LocalDate.now().getYear());
      if (birthday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == this.week) {
        birthdaysThisWeek.add(person);
      }
    }

    this.LOG.debug(
        "Gathered all Persons this week({})! Count: {}", this.week, birthdaysThisWeek.size());
    return PersonsInAWeek.parseAList(birthdaysThisWeek);
  }
}
