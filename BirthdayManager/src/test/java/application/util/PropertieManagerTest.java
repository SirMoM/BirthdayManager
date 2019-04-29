/**
 *
 */
package application.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PropertieManagerTest {

	private PropertyManager classToTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.classToTest = PropertyManager.getInstance();
	}

	/**
	 * Test method for
	 * {@link application.util.PropertyManager#getProperty(java.lang.String)}.
	 */
	@Test
	public final void testGetPropertie() {
		assertThat(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT), is("10"));
		assertThat(PropertyManager.getProperty(PropertyFields.WRITE_THRU), is("false"));
		assertThat(PropertyManager.getProperty(PropertyFields.AUTOSAVE), is("false"));
	}

	/**
	 * Test method for {@link application.util.PropertyManager#loadProperties()}.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public final void testStoreLoadProperties() throws FileNotFoundException, IOException {
		final PropertyManager tempPropertieManager = PropertyManager.getInstance();
		tempPropertieManager.getProperties().setProperty("test", "test");
		tempPropertieManager.storeProperties("test");

		this.classToTest.loadProperties();

		assertThat(PropertyManager.getProperty("test"), is("test"));

	}
}
