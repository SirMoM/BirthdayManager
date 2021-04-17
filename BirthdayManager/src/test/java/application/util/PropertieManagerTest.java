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
		PropertyManager.getInstance().fillWithStandartProperties();

	}

	/**
	 * Test method for
	 * {@link application.util.PropertyManager#getProperty(java.lang.String)}.
	 */
	@Test
	public final void testGetProperty() {
		assertThat(PropertyManager.getProperty(PropertyFields.AUTOSAVE), is("true"));
		assertThat(PropertyManager.getProperty(PropertyFields.WRITE_THRU), is("true"));
		assertThat(PropertyManager.getProperty(PropertyFields.OPEN_FILE_ON_START), is("false"));
		assertThat(PropertyManager.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT), is("15"));
		assertThat(PropertyManager.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR), is("#ff000066"));
		assertThat(PropertyManager.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR), is("#ffcc6666"));
		assertThat(PropertyManager.getProperty(PropertyFields.EXPORT_WITH_ALARM), is("true"));

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

		PropertyManager.getInstance().loadProperties();

		assertThat(PropertyManager.getProperty("test"), is("test"));

	}
}
