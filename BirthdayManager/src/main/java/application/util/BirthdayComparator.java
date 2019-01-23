package application.util;

import java.time.LocalDate;
import java.util.Comparator;

import application.model.Person;

public class BirthdayComparator implements Comparator<Person>{
	private final boolean compareToToday;
	private final LocalDate today = LocalDate.now();

	/**
	 *
	 */
	public BirthdayComparator(final boolean compareToToday){
		this.compareToToday = compareToToday;
	}

	@Override
	public int compare(final Person person1, final Person person2){
		if(this.compareToToday){
			if(person2.getBirthday().getDayOfYear() < this.today.getDayOfYear()){
				return -1;
			} else if(person2.getBirthday().getDayOfYear() == this.today.getDayOfYear()){
				return 0;
			} else{
				return 1;
			}
		} else{
			if(person2.getBirthday().getDayOfYear() < person1.getBirthday().getDayOfYear()){
				return -1;
			} else if(person2.getBirthday().getDayOfYear() == person1.getBirthday().getDayOfYear()){
				return 0;
			} else{
				return 1;
			}
		}
	}

}
