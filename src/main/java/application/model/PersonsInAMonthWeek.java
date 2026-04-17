package application.model;

import application.util.BirthdayUtils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonsInAMonthWeek {

  private final LocalDate mondayDate;
  private final List<Person> mondayPersons;
  private final LocalDate tuesdayDate;
  private final List<Person> tuesdayPersons;
  private final LocalDate wednesdayDate;
  private final List<Person> wednesdayPersons;
  private final LocalDate thursdayDate;
  private final List<Person> thursdayPersons;
  private final LocalDate fridayDate;
  private final List<Person> fridayPersons;
  private final LocalDate saturdayDate;
  private final List<Person> saturdayPersons;
  private final LocalDate sundayDate;
  private final List<Person> sundayPersons;

  public PersonsInAMonthWeek(
      final LocalDate mondayDate,
      final List<Person> mondayPersons,
      final LocalDate tuesdayDate,
      final List<Person> tuesdayPersons,
      final LocalDate wednesdayDate,
      final List<Person> wednesdayPersons,
      final LocalDate thursdayDate,
      final List<Person> thursdayPersons,
      final LocalDate fridayDate,
      final List<Person> fridayPersons,
      final LocalDate saturdayDate,
      final List<Person> saturdayPersons,
      final LocalDate sundayDate,
      final List<Person> sundayPersons) {
    this.mondayDate = mondayDate;
    this.mondayPersons = List.copyOf(mondayPersons);
    this.tuesdayDate = tuesdayDate;
    this.tuesdayPersons = List.copyOf(tuesdayPersons);
    this.wednesdayDate = wednesdayDate;
    this.wednesdayPersons = List.copyOf(wednesdayPersons);
    this.thursdayDate = thursdayDate;
    this.thursdayPersons = List.copyOf(thursdayPersons);
    this.fridayDate = fridayDate;
    this.fridayPersons = List.copyOf(fridayPersons);
    this.saturdayDate = saturdayDate;
    this.saturdayPersons = List.copyOf(saturdayPersons);
    this.sundayDate = sundayDate;
    this.sundayPersons = List.copyOf(sundayPersons);
  }

  public static List<PersonsInAMonthWeek> parseMonth(
      final List<Person> persons, final YearMonth targetMonth) {
    final Map<LocalDate, List<Person>> personsByDate = new HashMap<>();
    final int targetYear = targetMonth.getYear();

    for (Person person : persons) {
      LocalDate birthday = BirthdayUtils.getBirthdayInYear(person.getBirthday(), targetYear);
      personsByDate.computeIfAbsent(birthday, ignored -> new ArrayList<>()).add(person);
    }

    final List<PersonsInAMonthWeek> monthWeeks = new ArrayList<>();
    final LocalDate firstDayOfMonth = targetMonth.atDay(1);
    final LocalDate lastDayOfMonth = targetMonth.atEndOfMonth();
    final LocalDate firstWeekStart =
        firstDayOfMonth.minusDays(
            firstDayOfMonth.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    final LocalDate lastWeekEnd =
        lastDayOfMonth.plusDays(
            DayOfWeek.SUNDAY.getValue() - lastDayOfMonth.getDayOfWeek().getValue());

    for (LocalDate weekStart = firstWeekStart;
        !weekStart.isAfter(lastWeekEnd);
        weekStart = weekStart.plusWeeks(1)) {
      monthWeeks.add(
          new PersonsInAMonthWeek(
              dateIfInsideMonth(weekStart, targetMonth),
                  personsOnDate(weekStart, targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(1), targetMonth),
                  personsOnDate(weekStart.plusDays(1), targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(2), targetMonth),
                  personsOnDate(weekStart.plusDays(2), targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(3), targetMonth),
                  personsOnDate(weekStart.plusDays(3), targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(4), targetMonth),
                  personsOnDate(weekStart.plusDays(4), targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(5), targetMonth),
                  personsOnDate(weekStart.plusDays(5), targetMonth, personsByDate),
              dateIfInsideMonth(weekStart.plusDays(6), targetMonth),
                  personsOnDate(weekStart.plusDays(6), targetMonth, personsByDate)));
    }

    return monthWeeks;
  }

  private static LocalDate dateIfInsideMonth(final LocalDate date, final YearMonth targetMonth) {
    if (YearMonth.from(date).equals(targetMonth)) {
      return date;
    }
    return null;
  }

  private static List<Person> personsOnDate(
      final LocalDate date,
      final YearMonth targetMonth,
      final Map<LocalDate, List<Person>> personsByDate) {
    if (!YearMonth.from(date).equals(targetMonth)) {
      return List.of();
    }
    return personsByDate.getOrDefault(date, List.of());
  }

  public LocalDate getDateFor(final DayOfWeek dayOfWeek) {
    switch (dayOfWeek) {
      case MONDAY:
        return getMondayDate();
      case TUESDAY:
        return getTuesdayDate();
      case WEDNESDAY:
        return getWednesdayDate();
      case THURSDAY:
        return getThursdayDate();
      case FRIDAY:
        return getFridayDate();
      case SATURDAY:
        return getSaturdayDate();
      case SUNDAY:
        return getSundayDate();
      default:
        return null;
    }
  }

  public List<Person> getPersonsFor(final DayOfWeek dayOfWeek) {
    switch (dayOfWeek) {
      case MONDAY:
        return getMondayPersons();
      case TUESDAY:
        return getTuesdayPersons();
      case WEDNESDAY:
        return getWednesdayPersons();
      case THURSDAY:
        return getThursdayPersons();
      case FRIDAY:
        return getFridayPersons();
      case SATURDAY:
        return getSaturdayPersons();
      case SUNDAY:
        return getSundayPersons();
      default:
        return List.of();
    }
  }

  public LocalDate getMondayDate() {
    return mondayDate;
  }

  public List<Person> getMondayPersons() {
    return mondayPersons;
  }

  public LocalDate getTuesdayDate() {
    return tuesdayDate;
  }

  public List<Person> getTuesdayPersons() {
    return tuesdayPersons;
  }

  public LocalDate getWednesdayDate() {
    return wednesdayDate;
  }

  public List<Person> getWednesdayPersons() {
    return wednesdayPersons;
  }

  public LocalDate getThursdayDate() {
    return thursdayDate;
  }

  public List<Person> getThursdayPersons() {
    return thursdayPersons;
  }

  public LocalDate getFridayDate() {
    return fridayDate;
  }

  public List<Person> getFridayPersons() {
    return fridayPersons;
  }

  public LocalDate getSaturdayDate() {
    return saturdayDate;
  }

  public List<Person> getSaturdayPersons() {
    return saturdayPersons;
  }

  public LocalDate getSundayDate() {
    return sundayDate;
  }

  public List<Person> getSundayPersons() {
    return sundayPersons;
  }
}
