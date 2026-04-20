/** */
package application.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManager {
  private static final Logger LOG = LogManager.getLogger(PersonManager.class.getName());
  private static PersonManager personManagerSingleton = null;
  private SessionInfos sessionInfos;
  private List<Person> personDB;

  /** Private constructor restricted to this class itself. */
  private PersonManager() {
    this.setPersonDB(new ArrayList<>());
  }

  /**
   * Static method to create instance of PersonManager class
   *
   * @return the only instance {@link PersonManager}
   */
  public static PersonManager getInstance() {
    if (personManagerSingleton == null) {
      personManagerSingleton = new PersonManager();
    }
    return personManagerSingleton;
  }

  public void setSessionInfos(final SessionInfos sessionInfos) {
    this.sessionInfos = sessionInfos;
    if (!personDB.isEmpty()) {
      sessionInfos.updateSubLists();
    }
  }

  public void addNewPerson(final Person newPerson) {
    this.personDB.add(newPerson);
    LOG.debug("Added new person {}", newPerson.toExtendedString());
    updateSessionInfosIfPossible();
  }

  private void updateSessionInfosIfPossible() {
    if (sessionInfos != null) sessionInfos.updateSubLists();
    else LOG.debug("Could not update session infos; they are missing");
  }

  /**
   * @param person The person to delete
   */
  public void deletePerson(final Person person) {
    this.personDB.remove(person);
    LOG.debug("Deleted person {}", person.toExtendedString());
    updateSessionInfosIfPossible();
  }

  /**
   * @return the personDB == the {@link ArrayList} which contains the Persons
   */
  public List<Person> getPersons() {
    return this.personDB;
  }

  /**
   * @param personDB the personDB to set
   */
  public void setPersonDB(final List<Person> personDB) {
    this.personDB = distinctPersons(personDB);
    updateSessionInfosIfPossible();
  }

  public void mergePersons(final List<Person> persons) {
    final List<Person> mergedPersons = new ArrayList<>(this.personDB);
    mergedPersons.addAll(persons);
    this.personDB = distinctPersons(mergedPersons);
    updateSessionInfosIfPossible();
  }

  public void updatePerson(final Person personToUpdate, final Person updatedPerson) {
    LOG.debug(
        "Update person from {} new person {}",
        personToUpdate.toExtendedString(),
        updatedPerson.toExtendedString());
    personToUpdate.setBirthday(updatedPerson.getBirthday());
    personToUpdate.setName(updatedPerson.getName());
    personToUpdate.setSurname(updatedPerson.getSurname());
    personToUpdate.setMisc(updatedPerson.getMisc());
    updateSessionInfosIfPossible();
  }

  private static List<Person> distinctPersons(final List<Person> persons) {
    final Map<String, Person> uniquePersons = new LinkedHashMap<>();

    for (Person person : persons) {
      uniquePersons.putIfAbsent(buildIdentity(person), person);
    }

    final int removedDuplicates = persons.size() - uniquePersons.size();
    if (removedDuplicates > 0) {
      LOG.info(
          "Removed {} duplicate birthdays while normalizing the person list.", removedDuplicates);
    }

    return new ArrayList<>(uniquePersons.values());
  }

  private static String buildIdentity(final Person person) {
    return nullToEmpty(person.getSurname())
        + "\u0000"
        + nullToEmpty(person.getName())
        + "\u0000"
        + nullToEmpty(person.getMisc())
        + "\u0000"
        + (person.getBirthday() == null ? "" : person.getBirthday());
  }

  private static String nullToEmpty(final String value) {
    return value == null ? "" : value;
  }
}
