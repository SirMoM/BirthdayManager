package application.controller;

import application.model.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SearchViewControllerTest {

    private final SearchViewController searchViewController = new SearchViewController(null);

    @Test
    void contains_MatchesPartialTextInNameFields() {
        Person person = new Person("Neumann", "Christian", "", LocalDate.of(1959, 5, 20));

        assertThat(searchViewController.contains(person, "Chris")).isTrue();
        assertThat(searchViewController.contains(person, "umann")).isTrue();
    }
}
