package application.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.LocalDate.parse;

/**
 * @author Noah Ruben
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class Person {
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ObjectProperty<LocalDate> birthday;
    private final StringProperty surname;
    private final StringProperty name;
    private final StringProperty misc;

    /**
     * Create an empty person
     */
    public Person() {
        this.surname = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.misc = new SimpleStringProperty();
        this.birthday = new SimpleObjectProperty<>();
    }

    /**
     * Creates a new person with all attributes
     *
     * @param surname  The surname of the person
     * @param name     The name of the person
     * @param misc     The middle name of the person of or everything if it could not get parsed properly
     * @param birthday the Birthday of the person
     */
    public Person(
            final String surname,
            final String name,
            final String misc,
            final LocalDate birthday) {
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
        this.birthday = new SimpleObjectProperty<>(birthday);
    }

    /**
     * Parsing lines in the format: birthday;name;sonstiges
     *
     * <ul>
     *   <li>the name is expected like this: name, surname, misc
     *   <li>the birthday is expected like this: dd.MM.yyyy
     * </ul>
     *
     * <p><b>The name and surname are mandatory</b>
     *
     * @param line       a read line from the file
     * @param lineNumber which line of the file is parsed right now
     * @return a new Person resulting from the given String
     */
    public static Person parseFromCSVLine(final String line, final int lineNumber)
            throws PersonCouldNotBeParsedException {
        String name = null;
        String surname = null;
        String misc = null;
        LocalDate birthday;
        String whatCouldNotBeParsed = "";

        try {
            final String[] splitLine = line.split(",");
            if (splitLine.length == 1) {
                throw new PersonCouldNotBeParsedException(lineNumber, "the whole line", line);
            }
            whatCouldNotBeParsed = "birthday";
            birthday = parse(splitLine[0], Person.DATE_FORMATTER);

            whatCouldNotBeParsed = "full name";
            final String[] nameAsArray = splitLine[1].split(";");

            name = nameAsArray[0];
            surname = nameAsArray[1];

            if (nameAsArray.length > 2) {
                whatCouldNotBeParsed = "misc";
                misc = nameAsArray[2];
            } else {
                misc = null;
            }
            return new Person(surname, name, misc, birthday);
        } catch (IndexOutOfBoundsException | DateTimeParseException exception) {
            throw new PersonCouldNotBeParsedException(lineNumber, whatCouldNotBeParsed, line);
        }
    }

    /**
     * @param line a read line from the Text-file
     * @return a new Person resulting from the given String
     */
    public static Person parseFromTXTLine(final String line, final int lineNumber)
            throws PersonCouldNotBeParsedException {
        String name = null;
        String surname = null;
        String misc = null;
        LocalDate birthday;
        String whatCouldNotBeParsed = "";

        try {
            final String[] splitLine = line.split("=");
            if (splitLine.length == 1) {
                throw new PersonCouldNotBeParsedException(lineNumber, "the whole line", line);
            }
            whatCouldNotBeParsed = "birthday";
            birthday = parse(splitLine[1], Person.DATE_FORMATTER);

            whatCouldNotBeParsed = "full name";
            final String[] nameAsArray = splitLine[0].split(" ");

            surname = nameAsArray[0];

            if (nameAsArray.length > 2) {
                whatCouldNotBeParsed = "misc";
                misc = nameAsArray[1];
                whatCouldNotBeParsed = "full name";
                name = nameAsArray[2];
            } else {
                misc = null;
                whatCouldNotBeParsed = "full name";
                name = nameAsArray[1];
            }
            return new Person(surname, name, misc, birthday);
        } catch (IndexOutOfBoundsException | DateTimeParseException exception) {
            throw new PersonCouldNotBeParsedException(lineNumber, whatCouldNotBeParsed, line);
        }
    }

    /**
     * @return the birthday of the person
     */
    public LocalDate getBirthday() {
        return this.birthday.getValue();
    }

    /**
     * @param birthday the birthday to set
     */
    public void setBirthday(final LocalDate birthday) {
        this.birthday.set(birthday);
    }

    /**
     * @return the middle name / everything of the person
     */
    public String getMisc() {
        try {
            return this.misc.getValue();
        } catch (final NullPointerException nullPointerException) {
            return null;
        }
    }

    /**
     * @param misc the middle name to set
     */
    public void setMisc(final String misc) {
        this.misc.set(misc);
    }

    /**
     * @return the name of the person
     */
    public String getName() {
        try {
            return this.name.getValue();
        } catch (final NullPointerException nullPointerException) {
            return null;
        }
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name.set(name);
    }

    /**
     * @return the surname of the person
     */
    public String getSurname() {
        try {
            return this.surname.getValue();
        } catch (final NullPointerException nullPointerException) {
            return null;
        }
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(final String surname) {
        this.surname.set(surname);
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

    public static class PersonCouldNotBeParsedException extends Exception {
        private final int lineNumber;
        private final String whatCouldNotBeParsed;
        private final String line;

        public PersonCouldNotBeParsedException(
                int lineNumber, String whatCouldNotBeParsed, String fullLine) {
            this.lineNumber = lineNumber;
            this.line = fullLine;
            this.whatCouldNotBeParsed = whatCouldNotBeParsed;
        }

        @Override
        public String getMessage() {
            return "Could not parse Person from line: "
                    + lineNumber
                    + "\n Could not parse field: "
                    + whatCouldNotBeParsed
                    + ".\nLine was: "
                    + line;
        }
    }
}
