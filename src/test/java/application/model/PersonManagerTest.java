package application.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class PersonManagerTest {

  private static final class RecordingSessionInfos extends SessionInfos {
    private boolean updateSubListsCalled;

    private RecordingSessionInfos() {
      super(null);
    }

    @Override
    public void updateSubLists() {
      updateSubListsCalled = true;
    }
  }

  @Test
  void updatePerson_UpdatesTheExistingInstanceAndRefreshesSublists() {
    PersonManager personManager = PersonManager.getInstance();
    personManager.getPersons().clear();

    Person originalPerson = new Person("Doe", "Jane", "A.", LocalDate.of(1990, 1, 1));
    Person updatedPerson = new Person("Smith", "Janet", "B.", LocalDate.of(1991, 2, 2));
    personManager.getPersons().add(originalPerson);

    RecordingSessionInfos sessionInfos = new RecordingSessionInfos();
    personManager.setSessionInfos(sessionInfos);

    personManager.updatePerson(originalPerson, updatedPerson);

    assertThat(personManager.getPersons()).containsExactly(originalPerson);
    assertThat(personManager.getPersons().get(0)).isSameAs(originalPerson);
    assertThat(originalPerson.getSurname()).isEqualTo("Smith");
    assertThat(originalPerson.getName()).isEqualTo("Janet");
    assertThat(originalPerson.getMisc()).isEqualTo("B.");
    assertThat(originalPerson.getBirthday()).isEqualTo(LocalDate.of(1991, 2, 2));
    assertThat(sessionInfos.updateSubListsCalled).isTrue();
  }

  @Test
  void mergePersons_RemovesExactDuplicatesWhileKeepingDistinctBirthdays() {
    PersonManager personManager = PersonManager.getInstance();
    personManager.getPersons().clear();

    Person existingPerson = new Person("Walter", "Stefan", null, LocalDate.of(1964, 4, 16));
    Person importedDuplicate = new Person("Walter", "Stefan", null, LocalDate.of(1964, 4, 16));
    Person importedUnique = new Person("Meyer", "Markus", null, LocalDate.of(1962, 4, 18));

    personManager.getPersons().add(existingPerson);

    personManager.mergePersons(List.of(importedDuplicate, importedUnique));

    assertThat(personManager.getPersons()).containsExactly(existingPerson, importedUnique);
  }
}
