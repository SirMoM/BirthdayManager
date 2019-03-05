/**
 *
 */
package application.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
		System.out.println(personsToParse.size());
		for(int i = 0; i < personsToParse.size(); i++){
			System.out.println(personsToParse.get(i));

			final LocalDate birthday = personsToParse.get(i).getBirthday();
			final LocalDate thisYearsBirthday = birthday.withYear(2019);

			switch (thisYearsBirthday.getDayOfWeek()) {
				case MONDAY:
					if(mondayPerson == null){
						mondayPerson = personsToParse.remove(i);
					}
					break;
				case TUESDAY:
					if(tuesdayPerson == null){
						tuesdayPerson = personsToParse.remove(i);
					}
					break;
				case WEDNESDAY:
					if(wednesdayPerson == null){
						wednesdayPerson = personsToParse.remove(i);
					}
					break;
				case THURSDAY:
					if(thursdayPerson == null){
						thursdayPerson = personsToParse.remove(i);
					}
					break;
				case FRIDAY:
					if(fridayPerson == null){
						fridayPerson = personsToParse.remove(i);
					}
					break;
				case SATURDAY:
					if(saturdayPerson == null){
						saturdayPerson = personsToParse.remove(i);
					}
					break;
				case SUNDAY:
					if(sundayPerson == null){
						sundayPerson = personsToParse.remove(i);
					}
					break;
			}
			personsInAWeekList.add(new PersonsInAWeek(mondayPerson, tuesdayPerson, wednesdayPerson, thursdayPerson, fridayPerson, saturdayPerson, sundayPerson));
		}
		if(!personsToParse.isEmpty()){
			final List<PersonsInAWeek> parseAList = parseAList(personsToParse);
			personsInAWeekList.addAll(parseAList);
		}

		return personsInAWeekList;
	}

	final private Person mondayPerson;

	final private Person tuesdayPerson;
	final private Person wednesdayPerson;
	final private Person thursdayPerson;
	final private Person fridayPerson;
	final private Person saturdayPerson;
	final private Person sundayPerson;

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
		this.mondayPerson = mondayPerson;
		this.tuesdayPerson = tuesdayPerson;
		this.wednesdayPerson = wednesdayPerson;
		this.thursdayPerson = thursdayPerson;
		this.fridayPerson = fridayPerson;
		this.saturdayPerson = saturdayPerson;
		this.sundayPerson = sundayPerson;
	}

	/**
	 * @return the fridayPerson
	 */
	public Person getFridayPerson(){
		return this.fridayPerson;
	}

	/**
	 * @return the mondayPerson
	 */
	public Person getMondayPerson(){
		return this.mondayPerson;
	}

	/**
	 * @return the saturdayPerson
	 */
	public Person getSaturdayPerson(){
		return this.saturdayPerson;
	}

	/**
	 * @return the sundayPerson
	 */
	public Person getSundayPerson(){
		return this.sundayPerson;
	}

	/**
	 * @return the thursdayPerson
	 */
	public Person getThursdayPerson(){
		return this.thursdayPerson;
	}

	/**
	 * @return the tuesdayPerson
	 */
	public Person getTuesdayPerson(){
		return this.tuesdayPerson;
	}

	/**
	 * @return the wednesdayPerson
	 */
	public Person getWednesdayPerson(){
		return this.wednesdayPerson;
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
			builder.append(this.mondayPerson);
			builder.append(", ");
		}
		if(this.tuesdayPerson != null){
			builder.append("tuesdayPerson=");
			builder.append(this.tuesdayPerson);
			builder.append(", ");
		}
		if(this.wednesdayPerson != null){
			builder.append("wednesdayPerson=");
			builder.append(this.wednesdayPerson);
			builder.append(", ");
		}
		if(this.thursdayPerson != null){
			builder.append("thursdayPerson=");
			builder.append(this.thursdayPerson);
			builder.append(", ");
		}
		if(this.fridayPerson != null){
			builder.append("fridayPerson=");
			builder.append(this.fridayPerson);
			builder.append(", ");
		}
		if(this.saturdayPerson != null){
			builder.append("saturdayPerson=");
			builder.append(this.saturdayPerson);
			builder.append(", ");
		}
		if(this.sundayPerson != null){
			builder.append("sundayPerson=");
			builder.append(this.sundayPerson);
		}
		return builder.toString();
	}

}
