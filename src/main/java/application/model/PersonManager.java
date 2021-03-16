/**
 *
 */
package application.model;

import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonManager {
    static final Logger LOG = LogManager.getLogger(PersonManager.class);
    private static PersonManager personManagerSingelton = null;

    public final SimpleBooleanProperty changesProperty = new SimpleBooleanProperty(false);

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
        if (personManagerSingelton == null) {
            personManagerSingelton = new PersonManager();
        }
        return personManagerSingelton;
    }

    public void addNewPerson(final Person newPerson) {
        this.personDB.add(newPerson);
        changesProperty.set(true);
    }

    /** @param person The person to delete */
    public void deletePerson(final Person person) {
        this.personDB.remove(person);
        this.changesProperty.set(true);
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
    }

    /**
     * @param indexPerson the index of the person which will be updated
     * @param updatedPerson the person which was updated
     */
    public void updatePerson(final int indexPerson, final Person updatedPerson) {
        this.personDB.get(indexPerson).setBirthday(updatedPerson.getBirthday());
        this.personDB.get(indexPerson).setName(updatedPerson.getName());
        this.personDB.get(indexPerson).setSurname(updatedPerson.getSurname());
        this.personDB.get(indexPerson).setMisc(updatedPerson.getMisc());
        this.changesProperty.set(true);
    }
}
