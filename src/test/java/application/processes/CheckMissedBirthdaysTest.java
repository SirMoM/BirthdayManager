package application.processes;

import application.model.Person;
import application.model.PersonManager;
import application.util.PropertyFields;
import application.util.PropertyManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CheckMissedBirthdaysTest {

    CheckMissedBirthdays classToTest;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    List<Person> persons = new ArrayList<>();

    @Test
    void call() throws Exception {
        for (int i = 0; i < 8; i++) {
            Person tempPerson = new Person("Max", "Mustermann", String.valueOf(i), LocalDate.now().minusDays(i));
            persons.add(tempPerson);
        }
        PropertyManager.getInstance().getProperties().setProperty(PropertyFields.LAST_VISIT, LocalDate.now().minusDays(5).format(dateTimeFormatter));
        PersonManager.getInstance().setPersonDB(persons);

        classToTest = new CheckMissedBirthdays();

        List<Person> missed = classToTest.call();
        assertThat(missed).size().isEqualTo(4);
    }
}
