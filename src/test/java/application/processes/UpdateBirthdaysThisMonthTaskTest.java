package application.processes;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAMonthWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateBirthdaysThisMonthTaskTest {

  @Test
  void call_IncludesOnlyBirthdaysOfTheRequestedMonth() throws Exception {
    Person firstBirthday = new Person("Entry", "First", null, LocalDate.of(1990, 3, 2));
    Person secondBirthday = new Person("Entry", "Second", null, LocalDate.of(1995, 3, 2));
    Person endOfMonthBirthday = new Person("Entry", "Last", null, LocalDate.of(1988, 3, 31));
    Person nextMonthBirthday = new Person("Entry", "Ignored", null, LocalDate.of(1981, 4, 1));

    PersonManager.getInstance()
        .setPersonDB(List.of(firstBirthday, secondBirthday, endOfMonthBirthday, nextMonthBirthday));

    UpdateBirthdaysThisMonthTask classToTest =
        new UpdateBirthdaysThisMonthTask(YearMonth.of(2026, 3));

    List<PersonsInAMonthWeek> monthWeeks = classToTest.call();

    assertThat(monthWeeks).hasSize(6);
    assertThat(monthWeeks.get(1).getMondayPersons()).containsExactly(firstBirthday, secondBirthday);
    assertThat(monthWeeks.get(5).getTuesdayPersons()).containsExactly(endOfMonthBirthday);
    assertThat(flatten(monthWeeks)).doesNotContain(nextMonthBirthday);
  }

  private List<Person> flatten(List<PersonsInAMonthWeek> monthWeeks) {
    List<Person> persons = new ArrayList<>();
    for (PersonsInAMonthWeek monthWeek : monthWeeks) {
      persons.addAll(monthWeek.getMondayPersons());
      persons.addAll(monthWeek.getTuesdayPersons());
      persons.addAll(monthWeek.getWednesdayPersons());
      persons.addAll(monthWeek.getThursdayPersons());
      persons.addAll(monthWeek.getFridayPersons());
      persons.addAll(monthWeek.getSaturdayPersons());
      persons.addAll(monthWeek.getSundayPersons());
    }
    return persons;
  }
}
