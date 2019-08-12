package application.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class PersonTest {

	private Person classToTest;

	@Before
	public void setUp() throws Exception {
		classToTest = new Person("Ruben", "Noah", "Michael", LocalDate.of(1999, 3, 25));
	}

	@Test
	public final void testParseFromCSVLineFullTrue() {
		assertThat(classToTest, samePropertyValuesAs(Person.parseFromCSVLine("25.03.1999,Noah;Ruben;Michael", 0)));
	}

	@Test
	public final void testParseFromCSVLineFullFalse() {
		assertThat(classToTest, not(samePropertyValuesAs(Person.parseFromCSVLine("25.03.1999,Noah;Ruben", 0))));
	}

	@Test
	public final void testParseFromCSVLinePartTrue() {
		classToTest.setMisc(null);
		assertThat(classToTest, samePropertyValuesAs(Person.parseFromCSVLine("25.03.1999,Noah;Ruben", 0)));
	}

	@Test
	public final void testParseFromCSVLinePartFalse() {
		classToTest.setMisc(null);
		assertThat(classToTest, not(samePropertyValuesAs(Person.parseFromCSVLine("25.03.1999,Noah;Ruben;Michael", 0))));
	}

	@Test
	public final void testParseFromTXTLineFullTrue() {
		assertThat(classToTest, samePropertyValuesAs(Person.parseFromTXTLine("Noah Michael Ruben=25.03.1999")));
	}

	@Test
	public final void testParseFromTXTLineFullFalse() {
		assertThat(classToTest, not(samePropertyValuesAs(Person.parseFromTXTLine("Noah Ruben=25.03.1999"))));
	}

	@Test
	public final void testParseFromTXTLinePartTrue() {
		classToTest.setMisc(null);
		assertThat(classToTest, samePropertyValuesAs(Person.parseFromTXTLine("Noah Ruben=25.03.1999")));
	}

	@Test
	public final void testParseFromTXTLinePartFalse() {
		classToTest.setMisc(null);
		assertThat(classToTest, not(samePropertyValuesAs(Person.parseFromTXTLine("Noah Michael Ruben=25.03.1999"))));
	}

	@Test
	public final void testToCSVStringFull() {
		assertThat(classToTest.toCSVString(), is("25.03.1999,Noah;Ruben;Michael"));
	}

	@Test
	public final void testToCSVStringPart() {
		classToTest.setMisc(null);
		assertThat(classToTest.toCSVString(), is("25.03.1999,Noah;Ruben"));
	}

	@Test
	public final void testToStringFull() {
		assertThat(classToTest.toString(), is("Noah Michael Ruben\n25.03.1999"));
	}

	@Test
	public final void testToStringPart() {
		classToTest.setMisc(null);
		assertThat(classToTest.toString(), is("Noah Ruben\n25.03.1999"));
	}

	@Test
	public final void testNamesToString() {
		assertThat(classToTest.namesToString(), is("Noah Michael Ruben"));
	}

	@Test
	public final void testToTXTStringFull() {
		assertThat(classToTest.toTXTString(), is("Noah Michael Ruben=25.03.1999"));
	}

	@Test
	public final void testToTXTStringPart() {
		classToTest.setMisc(null);
		assertThat(classToTest.toTXTString(), is("Noah Ruben=25.03.1999"));
	}

}
