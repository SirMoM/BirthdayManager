package application.util;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BirthdayComparatorTest {

  Person person1 = new Person("Max", "Mustermann", "Test", LocalDate.now());
  Person person2 = new Person("Max", "Mustermann", "Test", LocalDate.now().plusDays(1));
  Person person3 = new Person("Max", "Mustermann", "Test", LocalDate.now().minusDays(1));

  @Test
  void compareToToday() {
    BirthdayComparator classToTest = new BirthdayComparator(true);
    assertThat(classToTest.compare(null, person1)).isEqualTo(0);
    assertThat(classToTest.compare(null, person2)).isEqualTo(1);
    assertThat(classToTest.compare(null, person3)).isEqualTo(-1);
  }

  @Test
  void compare() {
    BirthdayComparator classToTest = new BirthdayComparator(false);
    assertThat(classToTest.compare(person1, person1)).isEqualTo(0);
    assertThat(classToTest.compare(person1, person2)).isEqualTo(-1);
    assertThat(classToTest.compare(person1, person3)).isEqualTo(1);
  }
}
