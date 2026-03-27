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
            return Integer.compare(getCurrentYearDayOfYear(person2), this.today.getDayOfYear());
        } else {
            return Integer.compare(
                    getCurrentYearDayOfYear(person1),
                    getCurrentYearDayOfYear(person2));
        }
    }

    private int getCurrentYearDayOfYear(final Person person) {
        return BirthdayUtils.getBirthdayInYear(person.getBirthday(), this.today.getYear())
                .getDayOfYear();
    }
}
