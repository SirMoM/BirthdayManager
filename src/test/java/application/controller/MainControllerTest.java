package application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import application.model.Person;
import application.model.PersonManager;
import application.model.SessionInfos;
import application.processes.LoadPersonsTask;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ButtonBar;
import org.junit.jupiter.api.Test;

class MainControllerTest {

  private static final class RecordingSessionInfos extends SessionInfos {
    private boolean updateSubListsCalled;
    private List<Person> personsSeenDuringRefresh = List.of();

    private RecordingSessionInfos() {
      super(null);
    }

    @Override
    public void updateSubLists() {
      updateSubListsCalled = true;
      personsSeenDuringRefresh = new ArrayList<>(PersonManager.getInstance().getPersons());
    }
  }

  @Test
  void determineCloseAction_SavesToExistingFileWhenConfirmed() {
    assertThat(MainController.determineCloseAction(true, ButtonBar.ButtonData.YES))
        .isEqualTo(MainController.CloseAction.SAVE_TO_EXISTING_FILE);
  }

  @Test
  void determineCloseAction_AsksForAFileWhenConfirmedWithoutSaveTarget() {
    assertThat(MainController.determineCloseAction(false, ButtonBar.ButtonData.YES))
        .isEqualTo(MainController.CloseAction.ASK_FOR_SAVE_FILE);
  }

  @Test
  void determineCloseAction_ExitsWithoutSavingWhenDeclined() {
    assertThat(MainController.determineCloseAction(true, ButtonBar.ButtonData.NO))
        .isEqualTo(MainController.CloseAction.EXIT_WITHOUT_SAVING);
  }

  @Test
  void shouldPersistUpdateReminder_UsesTheFinalCheckboxState() {
    assertThat(MainController.shouldPersistUpdateReminder(true)).isTrue();
    assertThat(MainController.shouldPersistUpdateReminder(false)).isFalse();
  }

  @Test
  void applyLoadedPersons_RebuildsSublistsAfterReplacingLoadedPersons() {
    PersonManager personManager = PersonManager.getInstance();
    personManager.getPersons().clear();
    personManager.getPersons().add(new Person("Old", "Entry", null, LocalDate.of(1990, 1, 1)));

    RecordingSessionInfos sessionInfos = new RecordingSessionInfos();
    Person loadedPerson = new Person("New", "Entry", null, LocalDate.of(2000, 2, 2));
    LoadPersonsTask.Result result = new LoadPersonsTask.Result(List.of(loadedPerson), List.of());

    MainController.applyLoadedPersons(sessionInfos, result);

    assertThat(personManager.getPersons()).containsExactly(loadedPerson);
    assertThat(sessionInfos.updateSubListsCalled).isTrue();
    assertThat(sessionInfos.personsSeenDuringRefresh).containsExactly(loadedPerson);
  }
}
