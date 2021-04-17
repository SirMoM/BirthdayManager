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

	private PropertieManager classToTest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.classToTest = PropertieManager.getInstance();
	}

	/**
	 * Test method for
	 * {@link application.util.PropertieManager#getPropertie(java.lang.String)}.
	 */
	@Test
	public final void testGetPropertie() {
		assertThat(PropertieManager.getPropertie(PropertieFields.SHOW_BIRTHDAYS_COUNT), is("10"));
		assertThat(PropertieManager.getPropertie(PropertieFields.WRITE_THRU), is("false"));
		assertThat(PropertieManager.getPropertie(PropertieFields.AUTOSAVE), is("false"));
	}

	/**
	 * Test method for {@link application.util.PropertieManager#loadProperties()}.
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public final void testStoreLoadProperties() throws FileNotFoundException, IOException {
		final PropertieManager tempPropertieManager = PropertieManager.getInstance();
		tempPropertieManager.getProperties().setProperty("test", "test");
		tempPropertieManager.storeProperties("test");

		this.classToTest.loadProperties();

		assertThat(PropertieManager.getPropertie("test"), is("test"));

	}
}
