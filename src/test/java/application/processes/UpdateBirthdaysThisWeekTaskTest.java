package application.processes;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAWeek;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UpdateBirthdaysThisWeekTaskTest {

  @Test
  void call_ReturnsOnlyBirthdaysFromTheCurrentWeek() throws Exception {
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    Person mondayBirthday = personForDate("Monday", monday);
    Person wednesdayBirthday = personForDate("Wednesday", monday.plusDays(2));
    Person sundayBirthday = personForDate("Sunday", monday.plusDays(6));
    Person nextWeekBirthday = personForDate("NextWeek", monday.plusDays(7));

    PersonManager.getInstance()
        .setPersonDB(
            new ArrayList<>(
                List.of(nextWeekBirthday, sundayBirthday, mondayBirthday, wednesdayBirthday)));

    List<PersonsInAWeek> result = new UpdateBirthdaysThisWeekTask().call();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getMondayPerson()).isEqualTo(mondayBirthday);
    assertThat(result.get(0).getTuesdayPerson()).isNull();
    assertThat(result.get(0).getWednesdayPerson()).isEqualTo(wednesdayBirthday);
    assertThat(result.get(0).getThursdayPerson()).isNull();
    assertThat(result.get(0).getFridayPerson()).isNull();
    assertThat(result.get(0).getSaturdayPerson()).isNull();
    assertThat(result.get(0).getSundayPerson()).isEqualTo(sundayBirthday);
  }

  @Test
  void call_CreatesAnotherWeekRowWhenTwoBirthdaysShareTheSameWeekday() throws Exception {
    LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    Person firstMondayBirthday = personForDate("FirstMonday", monday);
    Person secondMondayBirthday = personForDate("SecondMonday", monday);
    Person tuesdayBirthday = personForDate("Tuesday", monday.plusDays(1));

    PersonManager.getInstance()
        .setPersonDB(
            new ArrayList<>(List.of(secondMondayBirthday, tuesdayBirthday, firstMondayBirthday)));

    List<PersonsInAWeek> result = new UpdateBirthdaysThisWeekTask().call();

    assertThat(result).hasSize(2);
    assertThat(result.stream().map(PersonsInAWeek::getMondayPerson).toList())
        .containsExactlyInAnyOrder(firstMondayBirthday, secondMondayBirthday);
    assertThat(
            result.stream()
                .map(PersonsInAWeek::getTuesdayPerson)
                .filter(person -> person != null)
                .toList())
        .containsExactly(tuesdayBirthday);
  }

  private Person personForDate(String name, LocalDate date) {
    return new Person(
        name, "Mustermann", null, LocalDate.of(1990, date.getMonth(), date.getDayOfMonth()));
  }
}
