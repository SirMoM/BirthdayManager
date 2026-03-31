package application.processes;

import application.model.Person;
import application.model.PersonsInAMonthWeek;
import application.util.BirthdayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UpdateBirthdaysThisMonthTask extends PersonTasks<List<PersonsInAMonthWeek>> {

    private static final Logger LOG = LogManager.getLogger(UpdateBirthdaysThisMonthTask.class.getName());
    private final YearMonth targetMonth;

    public UpdateBirthdaysThisMonthTask() {
        this(YearMonth.from(LocalDate.now()));
    }

    UpdateBirthdaysThisMonthTask(final YearMonth targetMonth) {
        this.targetMonth = targetMonth;
        LOG.info("Gathering the Birthdays for this month: {}", this.targetMonth);
    }

    @Override
    protected List<PersonsInAMonthWeek> call() throws Exception {
        LOG.debug("Started {}", this.getClass().getName());

        List<Person> personDB = new ArrayList<>(getPersons());
        personDB.sort(Comparator.comparing(person -> BirthdayUtils.getBirthdayInYear(person.getBirthday(), this.targetMonth.getYear())));

        final List<Person> birthdaysThisMonth = new ArrayList<>();
        for (Person person : personDB) {
            LocalDate birthday = BirthdayUtils.getBirthdayInYear(person.getBirthday(), this.targetMonth.getYear());
            if (YearMonth.from(birthday).equals(this.targetMonth)) {
                birthdaysThisMonth.add(person);
            }
        }

        LOG.debug("Gathered all Persons this month({})! Count: {}", this.targetMonth, birthdaysThisMonth.size());
        return PersonsInAMonthWeek.parseMonth(birthdaysThisMonth, this.targetMonth);
    }
}
