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

    private void reset() {
      updateSubListsCalled = false;
      personsSeenDuringRefresh = List.of();
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
  void retainPopupDimension_KeepsOldSizeWhenNewContentWouldShrink() {
    assertThat(MainController.retainPopupDimension(640.0, 420.0)).isEqualTo(640.0);
  }

  @Test
  void retainPopupDimension_AllowsGrowthWhenNewContentNeedsMoreSpace() {
    assertThat(MainController.retainPopupDimension(420.0, 640.0)).isEqualTo(640.0);
  }

  @Test
  void retainPopupDimension_UsesPreferredSizeForFreshPopup() {
    assertThat(MainController.retainPopupDimension(0.0, 420.0)).isEqualTo(420.0);
  }

  @Test
  void applyLoadedPersons_RebuildsSublistsAfterReplacingLoadedPersons() {
    PersonManager personManager = PersonManager.getInstance();
    personManager.getPersons().clear();

    RecordingSessionInfos sessionInfos = new RecordingSessionInfos();
    personManager.setSessionInfos(sessionInfos);
    sessionInfos.reset();

    personManager.getPersons().add(new Person("Old", "Entry", null, LocalDate.of(1990, 1, 1)));
    Person loadedPerson = new Person("New", "Entry", null, LocalDate.of(2000, 2, 2));
    LoadPersonsTask.Result result = new LoadPersonsTask.Result(List.of(loadedPerson), List.of());

    MainController.applyLoadedPersons(result);

    assertThat(personManager.getPersons()).containsExactly(loadedPerson);
    assertThat(sessionInfos.updateSubListsCalled).isTrue();
    assertThat(sessionInfos.personsSeenDuringRefresh).containsExactly(loadedPerson);
  }

  @Test
  void applyLoadedPersons_RemovesExactDuplicatesFromTheLoadedFile() {
    PersonManager personManager = PersonManager.getInstance();
    personManager.getPersons().clear();

    RecordingSessionInfos sessionInfos = new RecordingSessionInfos();
    personManager.setSessionInfos(sessionInfos);
    sessionInfos.reset();

    Person duplicateBirthday = new Person("Walter", "Stefan", null, LocalDate.of(1964, 4, 16));
    Person sameBirthdayAgain = new Person("Walter", "Stefan", null, LocalDate.of(1964, 4, 16));
    Person uniqueBirthday = new Person("Meyer", "Markus", null, LocalDate.of(1962, 4, 18));
    LoadPersonsTask.Result result =
        new LoadPersonsTask.Result(
            List.of(duplicateBirthday, sameBirthdayAgain, uniqueBirthday), List.of());

    MainController.applyLoadedPersons(result);

    assertThat(personManager.getPersons()).containsExactly(duplicateBirthday, uniqueBirthday);
    assertThat(sessionInfos.personsSeenDuringRefresh)
        .containsExactly(duplicateBirthday, uniqueBirthday);
  }
}
