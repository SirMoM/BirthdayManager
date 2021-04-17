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
	public Person(String lineFromFile){
		super();
		this.surname = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		this.misc = new SimpleStringProperty();
		this.birthday = new SimpleObjectProperty<LocalDate>();
		String[] splitLine = lineFromFile.split("=");
		String[] nameSplit = splitLine[0].split(" ");
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
	public Person(String surname, String name, String misc, LocalDate birthday){
		super();
		this.surname = new SimpleStringProperty(surname);
		this.name = new SimpleStringProperty(name);
		this.misc = new SimpleStringProperty(misc);
		this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
	}

	/**
	 * @return the birthday
	 */
	public ObjectProperty<LocalDate> getBirthday(){
		return this.birthday;
	}

	/**
	 * @return the misc
	 */
	public StringProperty getMisc(){
		return this.misc;
	}

	/**
	 * @return the name
	 */
	public StringProperty getName(){
		return this.name;
	}

	/**
	 * @return the surname
	 */
	public StringProperty getSurname(){
		return this.surname;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(LocalDate birthday){
		this.birthday.set(birthday);
	}

	/**
	 * @param misc the misc to set
	 */
	public void setMisc(String misc){
		this.misc.set(misc);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name){
		this.name.set(name);
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname){
		this.surname.set(surname);
	}

	public String toExtendedString(){
		StringBuilder builder = new StringBuilder();
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
		StringBuilder builder = new StringBuilder();
		if(this.getSurname().get() != null){
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

}
