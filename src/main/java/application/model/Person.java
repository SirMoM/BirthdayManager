package application.model;

import static java.time.LocalDate.parse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class Person {
	protected final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private StringProperty surname;

	private StringProperty name;

	private StringProperty misc;

	private final ObjectProperty<LocalDate> birthday;

	/**
	 * Create an empty person
	 */
	public Person() {
		this.surname = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		this.misc = new SimpleStringProperty();
		this.birthday = new SimpleObjectProperty<LocalDate>();
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
	public Person(final String surname, final String name, final String misc, final LocalDate birthday) {
		super();
		if (surname == null || surname.isEmpty()) {
			this.surname = new SimpleStringProperty();
		} else {
			this.surname = new SimpleStringProperty(surname);
		}
		if (name == null || name.isEmpty()) {
			this.name = new SimpleStringProperty();
		} else {
			this.name = new SimpleStringProperty(name);
		}
		if (misc == null || misc.isEmpty()) {
			this.misc = new SimpleStringProperty();
		} else {
			this.misc = new SimpleStringProperty(misc);
		}
		this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
	}

	/**
	 * @return the birthday of the person
	 */
	public LocalDate getBirthday() {
		return this.birthday.getValue();
	}

	/**
	 * @return the middle name / everything of the person
	 */
	public String getMisc() {
		try {
			return this.misc.getValue();
		} catch (final NullPointerException NullPointerException) {
			return null;
		}
	}

	/**
	 * @return the name of the person
	 */
	public String getName() {
		try {
			return this.name.getValue();
		} catch (final NullPointerException NullPointerException) {
			return null;
		}
	}

	/**
	 * @return the surname of the person
	 */
	public String getSurname() {
		try {
			return this.surname.getValue();
		} catch (final NullPointerException NullPointerException) {
			return null;
		}
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(final LocalDate birthday) {
		this.birthday.set(birthday);
	}

	/**
	 * @param misc the middle name to set
	 */
	public void setMisc(final String misc) {
		this.misc.set(misc);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name.set(name);
	}

	/**
	 * @param surname the surname to set
	 */
	public void setSurname(final String surname) {
		this.surname.set(surname);
	}




	/**
	 * Parsing lines in the format: birthday;name;sonstiges
	 * <ul>
	 * <li>the name is expected like this: name, surname, misc
	 * <li>the birthday is expected like this: dd.MM.yyyy
	 * </ul>
	 *
	 * <p><b>The name and surname are mandatory</b></p>
	 *
	 * @param txtLine a read line from the file
	 * @param line which line of the file is parsed right now
	 * @return a new Person resulting from the given String
	 */
	public static Person parseFromCSVLine(final String txtLine, final int line) {
		String name = null;
		String surname = null;
		String misc = null;
		final LocalDate birthday;
		String whatCouldNotBeParsed = null;
		try {
			final String[] splitLine = txtLine.split(",");
			if (splitLine.length < 1) {
				throw new IndexOutOfBoundsException("Could not split line");
			}
			whatCouldNotBeParsed = "birthday";
			birthday = parse(splitLine[0], Person.DATE_FORMATTER);

			whatCouldNotBeParsed = "full name";
			final String[] nameAsArray = splitLine[1].split(";");

			whatCouldNotBeParsed = "name";
			name = nameAsArray[0];
			whatCouldNotBeParsed = "surname";
			surname = nameAsArray[1];

			if (nameAsArray.length > 2) {
				whatCouldNotBeParsed = "misc";
				misc = nameAsArray[2];
			} else {
				misc = null;
			}

			whatCouldNotBeParsed = null;
			return new Person(surname, name, misc, birthday);
		} catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("ERROR: Parsing failed");
			alert.setHeaderText("Line " + line + " could not be parsed: ");
			alert.setContentText("This attribute could not be parsed: " + whatCouldNotBeParsed + "\n" + txtLine);
			alert.showAndWait();
			return null;
		}

	}

	/**
	 * @param txtLine a read line from the Text-file
	 * @return a new Person resulting from the given String
	 */
	public static Person parseFromTXTLine(final String txtLine) {
		String name = null;
		String surname = null;
		String misc = null;
		LocalDate birthday = null;

		try {
			final String[] splitLine = txtLine.split("=");
			if (splitLine.length < 1) {
				throw new IndexOutOfBoundsException("Could not split line");
			}
			final String[] nameSplit = splitLine[0].split(" ");
			try {
				birthday = parse(splitLine[1], Person.DATE_FORMATTER);
			} catch (final DateTimeParseException e) {
				final Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Could not parse this birthday");
				alert.setHeaderText("Birthday which could not be parsed: ");
				alert.setContentText(splitLine[1]);
				alert.showAndWait();
				return null;
			}

			if (nameSplit.length == 3) {
				name = nameSplit[0];
				misc = nameSplit[1];
				surname = nameSplit[2];
			} else if (nameSplit.length == 2) {
				name = nameSplit[0];
				surname = nameSplit[1];
			} else {
				misc = splitLine[0];
			}
			return new Person(surname, name, misc, birthday);
		} catch (final IndexOutOfBoundsException e) {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Could not parse this line");
			alert.setHeaderText("Line which could not be parsed: ");
			alert.setContentText(txtLine);
			alert.showAndWait();
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (this.name.get() != null) {
			builder.append(this.name.get());
		}
		if (this.misc.get() != null) {
			builder.append(" ");
			builder.append(this.misc.get());
		}
		if (this.getSurname() != null) {
			builder.append(" ");
			builder.append(this.surname.get());
		}
		if (this.birthday.get() != null) {
			builder.append("\n");
			builder.append(DATE_FORMATTER.format(this.birthday.get()));
		}
		return builder.toString();
	}

	public String namesToString() {
		final StringBuilder builder = new StringBuilder();
		if (this.getName() != null) {
			builder.append(this.name.get());
		}
		if (this.getMisc() != null) {
			builder.append(" ");
			builder.append(this.misc.get());
		}
		if (this.getSurname() != null) {
			builder.append(" ");
			builder.append(this.surname.get());
		}
		return builder.toString();
	}

	/**
	 * String representation of the person with the formate: <br>
	 * <code>name misc surname</code>=<code>dd.mm.yyyy</code>
	 *
	 * @return The String representation of the person for a TXT-file.
	 */
	public String toTXTString() {
		final StringBuilder builder = new StringBuilder();
		if (this.getName() != null && !this.getName().isEmpty()) {
			builder.append(this.getName());
			builder.append(" ");
		}
		if (this.getMisc() != null && !this.getMisc().isEmpty()) {
			builder.append(this.getMisc());
			builder.append(" ");
		}
		if (this.getSurname() != null && !this.getSurname().isEmpty()) {
			builder.append(this.getSurname());
		}
		if (this.getBirthday() != null) {
			builder.append("=");
			builder.append(DATE_FORMATTER.format(this.birthday.get()));
		}

		return builder.toString();
	}

	/**
	 * @return a CSV-String representation of the person.
	 */
	public String toCSVString() {
		final StringBuilder builder = new StringBuilder();
		if (this.getBirthday() != null) {
			builder.append(Person.DATE_FORMATTER.format(this.getBirthday()));
		}
		builder.append(",");

		if (this.getName() != null) {
			builder.append(this.getName());
		}
		if (this.getSurname() != null) {
			builder.append(";");
			builder.append(this.getSurname());
		}
		if (this.getMisc() != null) {
			builder.append(";");
			builder.append(this.getMisc());
		}

		return builder.toString();
	}

	/**
	 * @return a ExtendedString of the person for Debugging
	 */
	public String toExtendedString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Person: ");
		if (this.getName() != null) {
			builder.append("getName()=");
			builder.append(this.getName());
			builder.append(", ");
		}
		if (this.getMisc() != null) {
			builder.append("getMisc()=");
			builder.append(this.getMisc());
			builder.append(", ");
		}
		if (this.getSurname() != null) {
			builder.append("getSurname()=");
			builder.append(this.getSurname());
			builder.append(", ");
		}
		if (this.getBirthday() != null) {
			builder.append("getBirthday()=");
			builder.append(this.getBirthday());
		}

		return builder.toString();
	}
}
