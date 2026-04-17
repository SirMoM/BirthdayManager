package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SearchViewControllerTest {

  private final SearchViewController searchViewController = new SearchViewController(null);

  @Test
  void contains_MatchesPartialTextInNameFields() {
    Person person = new Person("Neumann", "Christian", "", LocalDate.of(1959, 5, 20));

    assertThat(searchViewController.contains(person, "Chris")).isTrue();
    assertThat(searchViewController.contains(person, "umann")).isTrue();
  }
}
