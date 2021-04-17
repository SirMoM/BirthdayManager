/**
 *
 */
package application.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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

    /** @param person The person to delete */
    public void deletePerson(final Person person) {
        this.personDB.remove(person);
        LOG.debug("Deleted person {}", person.toExtendedString());
        updateSessionInfosIfPossible();
    }

    public Person getPersonFromIndex(final int indexPerson) {
        return this.personDB.get(indexPerson);
    }

    /** @return the personDB == the {@link ArrayList} which contains the Persons */
    public List<Person> getPersons() {
        return this.personDB;
    }

    /** @param personDB the personDB to set */
    public void setPersonDB(final List<Person> personDB) {
        this.personDB = personDB;
        updateSessionInfosIfPossible();
    }

    /**
     * @param indexPerson the index of the person which will be updated
     * @param updatedPerson the person which was updated
     */
    public void updatePerson(final int indexPerson, final Person updatedPerson) {
        LOG.debug("Update person from {} new person {}", this.personDB.get(indexPerson).toExtendedString(), updatedPerson.toExtendedString());
        this.personDB.get(indexPerson).setBirthday(updatedPerson.getBirthday());
        this.personDB.get(indexPerson).setName(updatedPerson.getName());
        this.personDB.get(indexPerson).setSurname(updatedPerson.getSurname());
        this.personDB.get(indexPerson).setMisc(updatedPerson.getMisc());
        updateSessionInfosIfPossible();
    }
}
