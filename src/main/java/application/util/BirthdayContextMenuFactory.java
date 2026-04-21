package application.util;

import application.model.Person;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public final class BirthdayContextMenuFactory {

  private BirthdayContextMenuFactory() {}

  public static ContextMenu createContextMenu(
      final List<Person> persons, final String openLabel, final Consumer<Person> openPersonAction) {
    if (persons == null || persons.isEmpty()) {
      return null;
    }

    final ContextMenu contextMenu = new ContextMenu();
    if (persons.size() == 1) {
      final MenuItem menuItem = createPersonMenuItem(openLabel, persons.get(0), openPersonAction);
      contextMenu.getItems().add(menuItem);
      return contextMenu;
    }

    final Menu menu = new Menu(openLabel);
    for (Person person : persons) {
      menu.getItems().add(createPersonMenuItem(person.namesToString(), person, openPersonAction));
    }
    contextMenu.getItems().add(menu);
    return contextMenu;
  }

  private static MenuItem createPersonMenuItem(
      final String text, final Person person, final Consumer<Person> openPersonAction) {
    final MenuItem menuItem = new MenuItem(text);
    menuItem.setOnAction(event -> openPersonAction.accept(person));
    return menuItem;
  }
}
