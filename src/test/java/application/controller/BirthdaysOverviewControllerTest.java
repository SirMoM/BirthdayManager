package application.controller;

import application.model.Person;
import application.model.PersonsInAMonthWeek;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BirthdaysOverviewControllerTest {

    @Test
    void findMonthWeekIndexForDate_ReturnsMatchingWeekRow() {
        LocalDate monday = LocalDate.of(2026, 3, 2);
        List<PersonsInAMonthWeek> monthWeeks = List.of(
                monthWeekStartingAt(monday),
                monthWeekStartingAt(monday.plusWeeks(1)),
                monthWeekStartingAt(monday.plusWeeks(2))
        );

        int index = BirthdaysOverviewController.findMonthWeekIndexForDate(monthWeeks, LocalDate.of(2026, 3, 17));

        assertThat(index).isEqualTo(2);
    }

    @Test
    void findMonthWeekIndexForDate_ReturnsMinusOneWhenDateIsMissing() {
        LocalDate monday = LocalDate.of(2026, 3, 2);
        List<PersonsInAMonthWeek> monthWeeks = List.of(
                monthWeekStartingAt(monday),
                monthWeekStartingAt(monday.plusWeeks(1))
        );

        int index = BirthdaysOverviewController.findMonthWeekIndexForDate(monthWeeks, LocalDate.of(2026, 4, 1));

        assertThat(index).isEqualTo(-1);
    }

    private static PersonsInAMonthWeek monthWeekStartingAt(LocalDate monday) {
        return new PersonsInAMonthWeek(
                monday, List.<Person>of(),
                monday.plusDays(1), List.<Person>of(),
                monday.plusDays(2), List.<Person>of(),
                monday.plusDays(3), List.<Person>of(),
                monday.plusDays(4), List.<Person>of(),
                monday.plusDays(5), List.<Person>of(),
                monday.plusDays(6), List.<Person>of()
        );
    }
}
