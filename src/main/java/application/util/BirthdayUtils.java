package application.util;

import java.time.LocalDate;
import java.time.MonthDay;

public final class BirthdayUtils {

  private BirthdayUtils() {}

  public static LocalDate getBirthdayInYear(final LocalDate birthday, final int year) {
    return MonthDay.from(birthday).atYear(year);
  }
}
