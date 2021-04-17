package application.processes;

import application.model.Person;
import application.model.PersonManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateBirthdaysThisWeekTaskTest {
    UpdateBirthdaysThisWeekTask classToTest;
    List<Person> persons = new ArrayList<>();

    @Test
    void call() throws Exception {
        for (int i = -7; i < 8; i++) {
            Person tempPerson = new Person("Max", "Mustermann", String.valueOf(i), LocalDate.now().minusDays(i));
            persons.add(tempPerson);
        }

        PersonManager.getInstance().setPersonDB(persons);
        classToTest = new UpdateBirthdaysThisWeekTask();
        assertThat(classToTest.call().get(0));
    }
}