/**
 *
 */
package application.model;

import static java.time.LocalDate.parse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class Person{
	protected final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private StringProperty surname;
	private StringProperty name;
	private StringProperty misc;

	private ObjectProperty<LocalDate> birthday;

	/**
	 * Create an empty person
	 */
	public Person(){
	}

	/**
	 * @param lineFromFile a read line from the rext file
	 */
	// TODO DO the parsing elsewhere
	public Person(final String lineFromFile){
		super();
		this.surname = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		this.misc = new SimpleStringProperty();
		this.birthday = new SimpleObjectProperty<LocalDate>();
		final String[] splitLine = lineFromFile.split("=");
		final String[] nameSplit = splitLine[0].split(" ");
		if(nameSplit.length == 3){
			this.setName(nameSplit[0]);
			this.setMisc(nameSplit[1]);
			this.setSurname(nameSplit[2]);
		} else if(nameSplit.length == 2){
			this.setName(nameSplit[0]);
			this.setSurname(nameSplit[1]);
		} else{
			this.setMisc(splitLine[0]);
		}
		this.setBirthday(parse(splitLine[1], DATE_FORMATTER));

	}

	/**
	 * Creates a new person with all attributes
	 *
	 * @param surname  The surname of the person
	 * @param name     The name of the person
	 * @param misc     The middle name of the person of or everything if it could
	 *                 not get parsed properly
	 * @param birthday the Birthday of the person
	 */
	public Person(final String surname, final String name, final String misc, final LocalDate birthday){
		super();
		if(surname == null || surname.isEmpty()){
			this.surname = new SimpleStringProperty();
		} else{
			this.surname = new SimpleStringProperty(surname);
		}
		if(name == null || name.isEmpty()){
			this.name = new SimpleStringProperty();
		} else{
			this.name = new SimpleStringProperty(name);
		}
		if(misc == null || misc.isEmpty()){
			this.misc = new SimpleStringProperty();
		} else{
			this.misc = new SimpleStringProperty(misc);
		}
		this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
	}

	/**
	 * @return the birthday of the person
	 */
	public LocalDate getBirthday(){
		return this.birthday.getValue();
	}

	/**
	 * @return the middle name / everything of the person
	 */
	public String getMisc(){
		return this.misc.get();
	}

	/**
	 * @return the name of the person
	 */
	public String getName(){
		return this.name.get();
	}

	/**
	 * @return the surname of the person
	 */
	public String getSurname(){
		return this.surname.get();
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(final LocalDate birthday){
		this.birthday.set(birthday);
	}

	/**
	 * @param misc the middle name to set
	 */
	public void setMisc(final String misc){
		this.misc.set(misc);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name){
		this.name.set(name);
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(final String surname){
		this.surname.set(surname);
	}

	/**
	 * @return a CSV-String representation of the person.
	 */
	public String toCSVString(){
		final StringBuilder builder = new StringBuilder();
		if(this.getName() != null){
			builder.append(this.getName());
			builder.append(";");
		}
		if(this.getMisc() != null){
			builder.append(this.getMisc());
			builder.append(";");
		}
		if(this.getSurname() != null){
			builder.append(this.getSurname());
			builder.append(";");
		}
		if(this.getBirthday() != null){
			builder.append(this.getBirthday());
		}

		return builder.toString();
	}

	/**
	 * @return a ExtendedString of the person for Debugging
	 */
	public String toExtendedString(){
		final StringBuilder builder = new StringBuilder();
		builder.append("Person: ");
		if(this.getName() != null){
			builder.append("getName()=");
			builder.append(this.getName());
			builder.append(", ");
		}
		if(this.getMisc() != null){
			builder.append("getMisc()=");
			builder.append(this.getMisc());
			builder.append(", ");
		}
		if(this.getSurname() != null){
			builder.append("getSurname()=");
			builder.append(this.getSurname());
			builder.append(", ");
		}
		if(this.getBirthday() != null){
			builder.append("getBirthday()=");
			builder.append(this.getBirthday());
		}

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		final StringBuilder builder = new StringBuilder();
		if(this.getSurname() != null){
			builder.append(this.surname.get());
			builder.append(", ");
		}
		if(this.misc.get() != null){
			builder.append(" ");
			builder.append(this.misc.get());
			builder.append(", ");
		}
		if(this.name.get() != null){
			builder.append(this.name.get());
		}
		if(this.birthday.get() != null){
			builder.append("\n");
			builder.append(DATE_FORMATTER.format(this.birthday.get()));
		}
		return builder.toString();
	}

	/**
	 * String representation of the person.
	 * <p>
	 * with the formate
	 * <p>
	 * <code>name misc surname</code>=<code>dd.mm.yyyy</code>
	 */
	public String toTXTString(){
		final StringBuilder builder = new StringBuilder();
		if(this.getName() != null){
			builder.append(this.getName());
			builder.append(" ");
		}
		if(this.getMisc() != null){
			builder.append(this.getMisc());
			builder.append(" ");
		}
		if(this.getSurname() != null){
			builder.append(this.getSurname());
		}
		if(this.getBirthday() != null){
			builder.append("=");
			builder.append(DATE_FORMATTER.format(this.birthday.get()));
		}

		return builder.toString();
	}

}
