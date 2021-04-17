/**
 *
 */
package application.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PersonsInAWeek{

	static public List<PersonsInAWeek> parseAList(final List<Person> list){
		final List<Person> personsToParse = list;
		Person mondayPerson = null;
		Person tuesdayPerson = null;
		Person wednesdayPerson = null;
		Person thursdayPerson = null;
		Person fridayPerson = null;
		Person saturdayPerson = null;
		Person sundayPerson = null;

		final ArrayList<PersonsInAWeek> personsInAWeekList = new ArrayList<PersonsInAWeek>();
		System.out.println("personsToParse.size() " + personsToParse.size());
		for(int i = 0; i < personsToParse.size(); i++){
			final LocalDate birthday = personsToParse.get(i).getBirthday();
			final LocalDate thisYearsBirthday = birthday.withYear(2019);

			switch (thisYearsBirthday.getDayOfWeek()) {
				case MONDAY:
					if(mondayPerson == null){
						mondayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case TUESDAY:
					if(tuesdayPerson == null){
						tuesdayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case WEDNESDAY:
					if(wednesdayPerson == null){
						wednesdayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case THURSDAY:
					if(thursdayPerson == null){
						thursdayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case FRIDAY:
					if(fridayPerson == null){
						fridayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case SATURDAY:
					if(saturdayPerson == null){
						saturdayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
				case SUNDAY:
					if(sundayPerson == null){
						sundayPerson = personsToParse.remove(i);
						i = -1;
					}
					break;
			}
		}
		personsInAWeekList.add(new PersonsInAWeek(mondayPerson, tuesdayPerson, wednesdayPerson, thursdayPerson, fridayPerson, saturdayPerson, sundayPerson));
		if(!personsToParse.isEmpty()){
			final List<PersonsInAWeek> parseAList = parseAList(personsToParse);
			personsInAWeekList.addAll(parseAList);
		}

		return personsInAWeekList;
	}


	final private ObjectProperty<Person> mondayPerson;
	final private ObjectProperty<Person> tuesdayPerson;
	final private ObjectProperty<Person> wednesdayPerson;
	final private ObjectProperty<Person> thursdayPerson;
	final private ObjectProperty<Person> fridayPerson;
	final private ObjectProperty<Person> saturdayPerson;
	final private ObjectProperty<Person> sundayPerson;

	/**
	 * @param mondayPerson
	 * @param tuesdayPerson
	 * @param wednesdayPerson
	 * @param thursdayPerson
	 * @param fridayPerson
	 * @param saturdayPerson
	 * @param sundayPerson
	 */
	public PersonsInAWeek(final Person mondayPerson, final Person tuesdayPerson, final Person wednesdayPerson, final Person thursdayPerson, final Person fridayPerson, final Person saturdayPerson, final Person sundayPerson){
		this.mondayPerson = new SimpleObjectProperty<Person>(mondayPerson);
		this.tuesdayPerson = new SimpleObjectProperty<Person>(tuesdayPerson);
		this.wednesdayPerson = new SimpleObjectProperty<Person>(wednesdayPerson);
		this.thursdayPerson = new SimpleObjectProperty<Person>(thursdayPerson);
		this.fridayPerson = new SimpleObjectProperty<Person>(fridayPerson);
		this.saturdayPerson = new SimpleObjectProperty<Person>(saturdayPerson);
		this.sundayPerson = new SimpleObjectProperty<Person>(sundayPerson);
	}

	/**
	 * @return the fridayPerson
	 */
	public Person getFridayPerson(){
		return this.fridayPerson.getValue();
	}

	/**
	 * @return the mondayPerson
	 */
	public Person getMondayPerson(){
		return this.mondayPerson.getValue();
	}

	/**
	 * @return the saturdayPerson
	 */
	public Person getSaturdayPerson(){
		return this.saturdayPerson.getValue();
	}

	/**
	 * @return the sundayPerson
	 */
	public Person getSundayPerson(){
		return this.sundayPerson.getValue();
	}

	/**
	 * @return the thursdayPerson
	 */
	public Person getThursdayPerson(){
		return this.thursdayPerson.getValue();
	}

	/**
	 * @return the tuesdayPerson
	 */
	public Person getTuesdayPerson(){
		return this.tuesdayPerson.getValue();
	}

	/**
	 * @return the wednesdayPerson
	 */
	public Person getWednesdayPerson(){
		return this.wednesdayPerson.getValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		final StringBuilder builder = new StringBuilder();
		if(this.mondayPerson != null){
			builder.append("mondayPerson=");
			builder.append(getMondayPerson());
			builder.append(", ");
		}
		if(this.tuesdayPerson != null){
			builder.append("tuesdayPerson=");
			builder.append(getTuesdayPerson());
			builder.append(", ");
		}
		if(this.wednesdayPerson != null){
			builder.append("wednesdayPerson=");
			builder.append(getWednesdayPerson());
			builder.append(", ");
		}
		if(this.thursdayPerson != null){
			builder.append("thursdayPerson=");
			builder.append(getThursdayPerson());
			builder.append(", ");
		}
		if(this.fridayPerson != null){
			builder.append("fridayPerson=");
			builder.append(getFridayPerson());
			builder.append(", ");
		}
		if(this.saturdayPerson != null){
			builder.append("saturdayPerson=");
			builder.append(getSaturdayPerson());
			builder.append(", ");
		}
		if(this.sundayPerson != null){
			builder.append("sundayPerson=");
			builder.append(getSundayPerson());
		}
		return builder.toString();
	}

}
