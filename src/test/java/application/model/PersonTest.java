package application.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class PersonTest {

    Person actualPerson = new Person();

    @BeforeEach
    void setUp() {
        actualPerson.setName("Mustermann");
        actualPerson.setBirthday(LocalDate.of(2000,1,1));
        actualPerson.setSurname("Maximilian");
        actualPerson.setMisc("Maxi");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseFromCSVLine_SuccessTest() throws Person.PersonCouldNotBeParsedException {
        // Full Person
        Person person = Person.parseFromCSVLine("01.01.2000,Mustermann;Maximilian;Maxi", 1);
        assertThat(actualPerson).usingRecursiveComparison().isEqualTo(person);

        // Missing misc
        person = Person.parseFromCSVLine("01.01.2000,Mustermann;Maximilian", 1);
        assertThat(actualPerson).usingRecursiveComparison().ignoringFields("misc").isEqualTo(person);
    }

    @Test
    void parseFromCSVLine_FailureTest() {
        // Wrong name
        assertThatThrownBy(() -> {
            Person.parseFromCSVLine("01.01.2000,Maxi", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: full name.\nLine was: 01.01.2000,Maxi");
        // Invalid Date
        assertThatThrownBy(() -> {
            Person.parseFromCSVLine("01.01.20000,Max; Mustermann", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: birthday");
        // Invalid line
        assertThatThrownBy(() -> {
            Person.parseFromCSVLine("01.01.2000Max; Mustermann", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: the whole line");
    }

    @Test
    void parseFromTXTLine_SuccessTest() throws Person.PersonCouldNotBeParsedException {
        // Full Person
        Person person = Person.parseFromTXTLine("Maximilian Maxi Mustermann=01.01.2000", 1);
        assertThat(actualPerson).usingRecursiveComparison().isEqualTo(person);

        // Missing misc
        person = Person.parseFromTXTLine("Maximilian Mustermann=01.01.2000", 1);
        assertThat(actualPerson).usingRecursiveComparison().ignoringFields("misc").isEqualTo(person);
    }

    @Test
    void parseFromTXTLine_FailureTest() throws Person.PersonCouldNotBeParsedException {
        // Wrong name
        assertThatThrownBy(() -> {
            Person.parseFromTXTLine("Maximilian=01.01.2000", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: full name.\nLine was: Maximilian=01.01.2000");
        // Invalid Date
        assertThatThrownBy(() -> {
            Person.parseFromTXTLine("Maximilian Maxi Mustermann=01.01.20000", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: birthday");
        // Invalid line
        assertThatThrownBy(() -> {
            Person.parseFromTXTLine("01.01.2000Max Mustermann", 1);
        }).isInstanceOf(Person.PersonCouldNotBeParsedException.class).hasMessageContaining("Could not parse Person from line: 1\n Could not parse field: the whole line");

    }

    @Test
    void testToString() {
        assertThat(actualPerson).hasToString("Mustermann Maxi Maximilian\n01.01.2000");
    }

    @Test
    void namesToString() {
        assertThat(actualPerson.namesToString()).isEqualTo("Mustermann Maxi Maximilian");
    }

    @Test
    void toTXTString() {
        assertThat(actualPerson.toTXTString()).isEqualTo("Mustermann Maxi Maximilian=01.01.2000");
    }

    @Test
    void toCSVString() {
        assertThat(actualPerson.toCSVString()).isEqualTo("01.01.2000,Mustermann;Maximilian;Maxi");
    }

    @Test
    void toExtendedString() {
        assertThat(actualPerson.toExtendedString()).isEqualTo("Person: getName()=Mustermann, getMisc()=Maxi, getSurname()=Maximilian, getBirthday()=2000-01-01");
    }
}