package application.util;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import application.model.PersonsInAMonthWeek;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import org.junit.jupiter.api.Test;

class MonthTableCallbackTest {

  @Test
  void call_FormatsDateAndBirthdaysForTheConfiguredDay() {
    Person firstBirthday = new Person("Entry", "First", null, LocalDate.of(1990, 3, 2));
    Person secondBirthday = new Person("Entry", "Second", null, LocalDate.of(1995, 3, 2));
    PersonsInAMonthWeek monthWeek =
        new PersonsInAMonthWeek(
            LocalDate.of(2026, 3, 2),
            List.of(firstBirthday, secondBirthday),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of());

    MonthTableCallback callback = new MonthTableCallback(DayOfWeek.MONDAY);
    ObservableValue<String> value =
        callback.call(new TableColumn.CellDataFeatures<>(null, null, monthWeek));

    assertThat(value.getValue()).isEqualTo("First Entry\nSecond Entry");
  }

  @Test
  void call_FormatsEmptyDaysAsDateOnly() {
    PersonsInAMonthWeek monthWeek =
        new PersonsInAMonthWeek(
            LocalDate.of(2026, 3, 2),
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of(),
            null,
            List.of());

    MonthTableCallback callback = new MonthTableCallback(DayOfWeek.MONDAY);
    ObservableValue<String> value =
        callback.call(new TableColumn.CellDataFeatures<>(null, null, monthWeek));

    assertThat(value.getValue()).isEmpty();
  }
}
