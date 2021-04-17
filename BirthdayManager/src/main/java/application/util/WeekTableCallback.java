/**
 *
 */
package application.util;

import java.time.DayOfWeek;

import application.model.PersonsInAWeek;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * @author i13az81
 *
 */
public class WeekTableCallback implements Callback<TableColumn.CellDataFeatures<PersonsInAWeek, String>, ObservableValue<String>>{

	final private DayOfWeek forWhichDay;

	public WeekTableCallback(final DayOfWeek forWhichDay){
		this.forWhichDay = forWhichDay;
	}

	@Override
	public ObservableValue<String> call(final CellDataFeatures<PersonsInAWeek, String> param){
		final PersonsInAWeek personsInAWeek = param.getValue();
		switch (this.forWhichDay) {
			case MONDAY:
				return new SimpleStringProperty(personsInAWeek.getMondayPerson().toString());

			default:
				return null;
		}

	}

	public DayOfWeek getForWhichDay(){
		return this.forWhichDay;
	}

}
