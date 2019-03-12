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


	private Person mondayPerson;
	private Person tuesdayPerson;
	private Person wednesdayPerson;
	private Person thursdayPerson;
	private Person saturdayPerson;
	private Person sundayPerson;
	private Person fridayPerson;

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

	/**
	 * @return the mondayPerson
	 */
	public Person getMondayPerson() {
		return mondayPerson;
	}

	/**
	 * @return the tuesdayPerson
	 */
	public Person getTuesdayPerson() {
		return tuesdayPerson;
	}

	/**
	 * @return the wednesdayPerson
	 */
	public Person getWednesdayPerson() {
		return wednesdayPerson;
	}

	/**
	 * @return the thursdayPerson
	 */
	public Person getThursdayPerson() {
		return thursdayPerson;
	}

	/**
	 * @return the saturdayPerson
	 */
	public Person getSaturdayPerson() {
		return saturdayPerson;
	}

	/**
	 * @return the sundayPerson
	 */
	public Person getSundayPerson() {
		return sundayPerson;
	}

	/**
	 * @return the fridayPerson
	 */
	public Person getFridayPerson() {
		return fridayPerson;
	}

	/**
	 * @param mondayPerson the mondayPerson to set
	 */
	public void setMondayPerson(Person mondayPerson) {
		this.mondayPerson = mondayPerson;
	}

	/**
	 * @param tuesdayPerson the tuesdayPerson to set
	 */
	public void setTuesdayPerson(Person tuesdayPerson) {
		this.tuesdayPerson = tuesdayPerson;
	}

	/**
	 * @param wednesdayPerson the wednesdayPerson to set
	 */
	public void setWednesdayPerson(Person wednesdayPerson) {
		this.wednesdayPerson = wednesdayPerson;
	}

	/**
	 * @param thursdayPerson the thursdayPerson to set
	 */
	public void setThursdayPerson(Person thursdayPerson) {
		this.thursdayPerson = thursdayPerson;
	}

	/**
	 * @param saturdayPerson the saturdayPerson to set
	 */
	public void setSaturdayPerson(Person saturdayPerson) {
		this.saturdayPerson = saturdayPerson;
	}

	/**
	 * @param sundayPerson the sundayPerson to set
	 */
	public void setSundayPerson(Person sundayPerson) {
		this.sundayPerson = sundayPerson;
	}

	/**
	 * @param fridayPerson the fridayPerson to set
	 */
	public void setFridayPerson(Person fridayPerson) {
		this.fridayPerson = fridayPerson;
	}
}
