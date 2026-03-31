package application.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonsInAMonthWeekTest {

    @Test
    void parseMonth_CreatesFiveCalendarWeeksForApril2026() {
        List<PersonsInAMonthWeek> monthWeeks = PersonsInAMonthWeek.parseMonth(List.of(), YearMonth.of(2026, 4));

        assertThat(monthWeeks).hasSize(5);

        PersonsInAMonthWeek firstWeek = monthWeeks.get(0);
        PersonsInAMonthWeek lastWeek = monthWeeks.get(4);

        assertThat(firstWeek.getMondayDate()).isNull();
        assertThat(firstWeek.getTuesdayDate()).isNull();
        assertThat(firstWeek.getWednesdayDate()).isEqualTo(LocalDate.of(2026, 4, 1));
        assertThat(firstWeek.getWednesdayPersons()).isEmpty();

        assertThat(lastWeek.getMondayDate()).isEqualTo(LocalDate.of(2026, 4, 27));
        assertThat(lastWeek.getThursdayDate()).isEqualTo(LocalDate.of(2026, 4, 30));
        assertThat(lastWeek.getFridayDate()).isNull();
        assertThat(lastWeek.getSundayDate()).isNull();
    }

    @Test
    void parseMonth_CreatesSixCalendarWeeksAndGroupsBirthdaysByDate() {
        Person firstBirthday = new Person("Entry", "First", null, LocalDate.of(1990, 3, 2));
        Person secondBirthday = new Person("Entry", "Second", null, LocalDate.of(1995, 3, 2));
        Person endOfMonthBirthday = new Person("Entry", "Last", null, LocalDate.of(1988, 3, 31));

        List<PersonsInAMonthWeek> monthWeeks = PersonsInAMonthWeek.parseMonth(
                List.of(firstBirthday, secondBirthday, endOfMonthBirthday),
                YearMonth.of(2026, 3));

        assertThat(monthWeeks).hasSize(6);

        PersonsInAMonthWeek firstWeek = monthWeeks.get(0);
        PersonsInAMonthWeek secondWeek = monthWeeks.get(1);
        PersonsInAMonthWeek lastWeek = monthWeeks.get(5);

        assertThat(firstWeek.getMondayDate()).isNull();
        assertThat(firstWeek.getSundayDate()).isEqualTo(LocalDate.of(2026, 3, 1));

        assertThat(secondWeek.getMondayDate()).isEqualTo(LocalDate.of(2026, 3, 2));
        assertThat(secondWeek.getMondayPersons()).containsExactly(firstBirthday, secondBirthday);

        assertThat(lastWeek.getMondayDate()).isEqualTo(LocalDate.of(2026, 3, 30));
        assertThat(lastWeek.getTuesdayDate()).isEqualTo(LocalDate.of(2026, 3, 31));
        assertThat(lastWeek.getTuesdayPersons()).containsExactly(endOfMonthBirthday);
        assertThat(lastWeek.getWednesdayDate()).isNull();
    }
}
