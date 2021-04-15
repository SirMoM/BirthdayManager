package application.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonsInAWeekTest {

    List<Person> persons = new ArrayList<>();

    void setup() {
        for (int i = 0; i < 8; i++) {
            Person tempPerson = new Person("Max", "Mustermann", String.valueOf(i), LocalDate.now().plusDays(i));
            persons.add(tempPerson);
        }
    }

    @Test
    void parseAList() {
        setup();

        List<PersonsInAWeek> personsInAWeekList = PersonsInAWeek.parseAList(persons);

        assertThat(personsInAWeekList.size()).isEqualTo(2);
        PersonsInAWeek personsInAWeek = personsInAWeekList.get(0);

        assertThat(personsInAWeek).hasNoNullFieldsOrProperties();

        assertThat(getDayOfWeek(personsInAWeek.getMondayPerson())).isEqualTo(DayOfWeek.MONDAY);
        assertThat(getDayOfWeek(personsInAWeek.getTuesdayPerson())).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(getDayOfWeek(personsInAWeek.getWednesdayPerson())).isEqualTo(DayOfWeek.WEDNESDAY);
        assertThat(getDayOfWeek(personsInAWeek.getThursdayPerson())).isEqualTo(DayOfWeek.THURSDAY);
        assertThat(getDayOfWeek(personsInAWeek.getFridayPerson())).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(getDayOfWeek(personsInAWeek.getSaturdayPerson())).isEqualTo(DayOfWeek.SATURDAY);
        assertThat(getDayOfWeek(personsInAWeek.getSundayPerson())).isEqualTo(DayOfWeek.SUNDAY);
    }

    private DayOfWeek getDayOfWeek(Person person) {
        return person.getBirthday().withYear(LocalDate.now().getYear()).getDayOfWeek();
    }
}
