package application.util;

import application.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BirthdayComparatorTest {

    Person person1 = new Person("Max", "Mustermann", "Test", LocalDate.now());
    Person person2 = new Person("Max", "Mustermann", "Test", LocalDate.now().plusDays(1));
    Person person3 = new Person("Max", "Mustermann", "Test", LocalDate.now().minusDays(1));

    @Test
    void compareToToday() {
        BirthdayComparator classToTest = new BirthdayComparator(true);
        assertThat(classToTest.compare(null, person1)).isZero();
        assertThat(classToTest.compare(null, person2)).isEqualTo(1);
        assertThat(classToTest.compare(null, person3)).isEqualTo(-1);
    }

    @Test
    void compare() {
        BirthdayComparator classToTest = new BirthdayComparator(false);
        assertThat(classToTest.compare(person1, person1)).isZero();
        assertThat(classToTest.compare(person1, person2)).isEqualTo(-1);
        assertThat(classToTest.compare(person1, person3)).isEqualTo(1);
    }

    @Test
    void compare_SortsLeapDayBeforeMarchFirst() {
        BirthdayComparator classToTest = new BirthdayComparator(false);
        Person leapDay = new Person("Leap", "Year", "", LocalDate.of(2000, 2, 29));
        Person marchFirst = new Person("March", "First", "", LocalDate.of(2001, 3, 1));

        assertThat(classToTest.compare(leapDay, marchFirst)).isNegative();
    }
}
