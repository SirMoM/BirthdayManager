package application.util;

import application.model.Person;
import application.model.PersonsInAMonthWeek;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class MonthTableCallback
    implements Callback<
        TableColumn.CellDataFeatures<PersonsInAMonthWeek, String>, ObservableValue<String>> {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd");
  private final DayOfWeek forWhichDay;

  public MonthTableCallback(final DayOfWeek forWhichDay) {
    this.forWhichDay = forWhichDay;
  }

  @Override
  public ObservableValue<String> call(
      final TableColumn.CellDataFeatures<PersonsInAMonthWeek, String> monthDate) {
    final PersonsInAMonthWeek monthWeek = monthDate.getValue();
    final LocalDate date = monthWeek.getDateFor(this.forWhichDay);
    if (date == null) {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    final List<Person> persons = monthWeek.getPersonsFor(this.forWhichDay);
    for (Person person : persons) {
      if (builder.length() > 0) {
        builder.append("\n");
      }
      builder.append(person.namesToString());
    }

    return new SimpleStringProperty(builder.toString());
  }
}
