package application.util;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import application.processes.TaskTest;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Test;

class BirthdayContextMenuFactoryTest extends TaskTest {

  @Test
  void createContextMenu_ReturnsNullWhenNoBirthdayExists() {
    ContextMenu contextMenu =
        BirthdayContextMenuFactory.createContextMenu(List.of(), "Open birthday", person -> {});

    assertThat(contextMenu).isNull();
  }

  @Test
  void createContextMenu_CreatesDirectActionForSingleBirthday() {
    Person person = new Person("Doe", "Jane", null, LocalDate.of(1990, 1, 1));
    AtomicReference<Person> openedPerson = new AtomicReference<>();

    ContextMenu contextMenu =
        BirthdayContextMenuFactory.createContextMenu(
            List.of(person), "Open birthday", openedPerson::set);

    assertThat(contextMenu.getItems()).hasSize(1);

    MenuItem menuItem = contextMenu.getItems().get(0);
    assertThat(menuItem.getText()).isEqualTo("Open birthday");

    menuItem.fire();

    assertThat(openedPerson.get()).isSameAs(person);
  }

  @Test
  void createContextMenu_CreatesChooserForMultipleBirthdays() {
    Person firstPerson = new Person("Doe", "Jane", null, LocalDate.of(1990, 1, 1));
    Person secondPerson = new Person("Doe", "John", null, LocalDate.of(1991, 2, 2));
    AtomicReference<Person> openedPerson = new AtomicReference<>();

    ContextMenu contextMenu =
        BirthdayContextMenuFactory.createContextMenu(
            List.of(firstPerson, secondPerson), "Open birthday", openedPerson::set);

    assertThat(contextMenu.getItems()).singleElement().isInstanceOf(Menu.class);

    Menu menu = (Menu) contextMenu.getItems().get(0);
    assertThat(menu.getText()).isEqualTo("Open birthday");
    assertThat(menu.getItems())
        .extracting(MenuItem::getText)
        .containsExactly(firstPerson.namesToString(), secondPerson.namesToString());

    menu.getItems().get(1).fire();

    assertThat(openedPerson.get()).isSameAs(secondPerson);
  }
}
