package application.processes;

import application.model.Person;
import application.model.PersonManager;
import application.util.PropertyFields;
import application.util.PropertyManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateNextBirthdaysTaskTest {
    UpdateNextBirthdaysTask classToTest;
    List<Person> persons = new ArrayList<>();

    @Test
    void call() throws Exception {
        // Setup
        for (int i = -2; i < 2; i++) {
            Person tempPerson = new Person("Max", "Mustermann", String.valueOf(i), LocalDate.now().plusDays(i));
            persons.add(tempPerson);
        }
        PersonManager.getInstance().setPersonDB(persons);
        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT, "2");
        classToTest = new UpdateNextBirthdaysTask();

        // Test
        List<Person> personList = classToTest.call();
        assertThat(personList.get(0)).isEqualTo(persons.get(2));
        assertThat(personList.get(1)).isEqualTo(persons.get(3));
        assertThat(personList).size().isEqualTo(2);
    }
}