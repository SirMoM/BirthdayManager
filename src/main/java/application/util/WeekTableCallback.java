package application.util;

import application.model.Person;
import application.model.PersonsInAWeek;
import java.time.DayOfWeek;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class WeekTableCallback
    implements Callback<
        TableColumn.CellDataFeatures<PersonsInAWeek, String>, ObservableValue<String>> {

  private final DayOfWeek forWhichDay;

  public WeekTableCallback(final DayOfWeek forWhichDay) {
    this.forWhichDay = forWhichDay;
  }

  @Override
  public ObservableValue<String> call(
      final CellDataFeatures<PersonsInAWeek, String> personsInAWeekDate) {
    final PersonsInAWeek personsInAWeek = personsInAWeekDate.getValue();
    switch (this.forWhichDay) {
      case MONDAY:
        return buildString(personsInAWeek.getMondayPerson());
      case TUESDAY:
        return buildString(personsInAWeek.getTuesdayPerson());
      case WEDNESDAY:
        return buildString(personsInAWeek.getWednesdayPerson());
      case THURSDAY:
        return buildString(personsInAWeek.getThursdayPerson());
      case FRIDAY:
        return buildString(personsInAWeek.getFridayPerson());
      case SATURDAY:
        return buildString(personsInAWeek.getSaturdayPerson());
      case SUNDAY:
        return buildString(personsInAWeek.getSundayPerson());

      default:
        return null;
    }
  }

  private ObservableValue<String> buildString(Person person) {
    final StringBuilder builder = new StringBuilder();
    if (person == null) {
      return null;
    }

    if (person.getSurname() != null) {
      builder.append(person.getSurname());
      builder.append(", \n");
    }
    if (person.getMisc() != null) {
      builder.append(person.getMisc());
      builder.append(", \n");
    }
    if (person.getName() != null) {
      builder.append(person.getName());
    }
    return new SimpleStringProperty(builder.toString());
  }
}
