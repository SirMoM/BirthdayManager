package application.util;

import application.model.Person;

import java.time.LocalDate;
import java.util.Comparator;

public class BirthdayComparator implements Comparator<Person> {
    private final boolean compareToToday;
    private final LocalDate today = LocalDate.now();

    /**
     * @param compareToToday compare the birthday to today
     */
    public BirthdayComparator(final boolean compareToToday) {
        this.compareToToday = compareToToday;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final Person person1, final Person person2) {
        if (this.compareToToday) {
            if (person2.getBirthday().getDayOfYear() < this.today.getDayOfYear()) {
                return -1;
            } else if (person2.getBirthday().getDayOfYear() == this.today.getDayOfYear()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (person2.getBirthday().getDayOfYear() < person1.getBirthday().getDayOfYear()) {
                return 1;
            } else if (person2.getBirthday().getDayOfYear() == person1.getBirthday().getDayOfYear()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
