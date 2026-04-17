package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EditBirthdayViewControllerTest {

  @Test
  void hasChanges_ReturnsFalseWhenAllEditableFieldsMatch() {
    Person originalPerson = new Person("Doe", "Jane", "A.", LocalDate.of(1990, 1, 1));
    Person updatedPerson = new Person("Doe", "Jane", "A.", LocalDate.of(1990, 1, 1));

    boolean hasChanges = EditBirthdayViewController.hasChanges(originalPerson, updatedPerson);

    assertThat(hasChanges).isFalse();
  }

  @Test
  void hasChanges_ReturnsTrueWhenAnyEditableFieldDiffers() {
    Person originalPerson = new Person("Doe", "Jane", "A.", LocalDate.of(1990, 1, 1));
    Person updatedPerson = new Person("Doe", "Jane", "A.", LocalDate.of(1990, 1, 2));

    boolean hasChanges = EditBirthdayViewController.hasChanges(originalPerson, updatedPerson);

    assertThat(hasChanges).isTrue();
  }
}
