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
	 *
	 */
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
	 * @param surname
	 * @param name
	 * @param birthday
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
	 * @return the birthday
	 */
	public LocalDate getBirthday(){
		return this.birthday.getValue();
	}

	/**
	 * @return the misc
	 */
	public String getMisc(){
		return this.misc.get();
	}

	/**
	 * @return the name
	 */
	public String getName(){
		return this.name.get();
	}

	/**
	 * @return the surname
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
	 * @param misc the misc to set
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
