package application.util;

import java.time.LocalDate;
import java.util.Comparator;

import application.model.Person;

public class BirthdayComparator implements Comparator<Person>{
	private boolean compareToToday;
	private final LocalDate today = LocalDate.now();

	/**
	 * 
	 */
	public BirthdayComparator(boolean compareToToday){
		this.compareToToday = compareToToday;
	}

	@Override
	public int compare(Person person1, Person person2){
		if(this.compareToToday){
			if(person2.getBirthday().get().getDayOfYear() < this.today.getDayOfYear()){
				return -1;
			} else if(person2.getBirthday().get().getDayOfYear() == this.today.getDayOfYear()){
				return 0;
			} else{
				return 1;
			}
		} else{
			if(person2.getBirthday().get().getDayOfYear() < person1.getBirthday().get().getDayOfYear()){
				return -1;
			} else if(person2.getBirthday().get().getDayOfYear() == person1.getBirthday().get().getDayOfYear()){
				return 0;
			} else{
				return 1;
			}
		}
	}

}
