package application.controller;

import application.model.Person;
import application.model.PersonManager;
import application.model.PersonsInAMonthWeek;
import application.model.SessionInfos;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BirthdaysOverviewControllerTest {

    private static final class RecordingSessionInfos extends SessionInfos {
        private boolean updateSubListsCalled;

        private RecordingSessionInfos() {
            super(null);
        }

        @Override
        public void updateSubLists() {
            updateSubListsCalled = true;
        }
    }

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

    @Test
    void deleteSelectedPerson_RemovesTheSelectedInstanceWithoutUsingAnIndex() {
        PersonManager personManager = PersonManager.getInstance();
        personManager.getPersons().clear();

        Person personToKeep = new Person("Keep", "Alice", null, LocalDate.of(1990, 1, 1));
        Person personToDelete = new Person("Delete", "Bob", null, LocalDate.of(1991, 2, 2));
        personManager.getPersons().add(personToKeep);
        personManager.getPersons().add(personToDelete);

        RecordingSessionInfos sessionInfos = new RecordingSessionInfos();
        personManager.setSessionInfos(sessionInfos);

        ObservableList<Person> selectedItems = FXCollections.observableArrayList(personToDelete);

        BirthdaysOverviewController.deleteSelectedPerson(selectedItems);

        assertThat(personManager.getPersons()).containsExactly(personToKeep);
        assertThat(sessionInfos.updateSubListsCalled).isTrue();
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
