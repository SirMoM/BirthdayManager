package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.model.PersonManager;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PropertyManager {

	private final Logger LOG = LogManager.getLogger(this.getClass().getName());

	private final Properties properties;
	private final File propertiesFile;
	private final String PROPERTIE_FILE_NAME = "BirthdayManager.properties";

	private static PropertyManager propertieManagerSingelton = null;

	/**
	 * Static method to create instance of PersonManager class
	 *
	 * @return the only instance {@link PersonManager}
	 */
	public static PropertyManager getInstance() {
		if (propertieManagerSingelton == null) {
			propertieManagerSingelton = new PropertyManager();
		}
		return propertieManagerSingelton;
	}

	/**
	 * Basis ConfigHandler uses the a app.cfg file generates it if necessary Private
	 * constructor restricted to this class itself.
	 */
	private PropertyManager() {
		this.properties = new Properties();
//		this.propertiesFile = new File(System.getProperty("java.io.tmpdir") + this.PROPERTIE_FILE_NAME);
		this.propertiesFile = new File(this.PROPERTIE_FILE_NAME);
//		this.LOG.info("Propertie File " + this.propertiesFile.getAbsolutePath());

		try {
			if (this.propertiesFile.createNewFile()) {
				this.LOG.info("Created new properties File");
				this.fillWithStandartProperties();
				this.LOG.info("Loaded default properties");
			} else {
				this.loadProperties();
				this.LOG.info("Loaded properties");
			}
		} catch (final FileNotFoundException fileNotFoundException) {
			this.LOG.catching(Level.FATAL, fileNotFoundException);
		} catch (final IOException ioException) {
			this.LOG.catching(Level.FATAL, ioException);
		}
	}

	/**
	 * Fills the properties with default values
	 * <ul>
	 * <li>SHOW_BIRTHDAYS_COUNT == 10</li>
	 * <li>SAVED_LOCALE == en_EN</li>
	 * <li>AUTOSAVE == false</li>
	 * <li>WRITE_THRU == false</li>
	 * </ul>
	 */
	protected void fillWithStandartProperties() {
		this.properties.setProperty(PropertyFields.SAVED_LOCALE, "en_GB");
		this.properties.setProperty(PropertyFields.AUTOSAVE, "true");
		this.properties.setProperty(PropertyFields.WRITE_THRU, "true");
		this.properties.setProperty(PropertyFields.OPEN_FILE_ON_START, "false");
		this.properties.setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT, "15");
		this.properties.setProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR, "#ff000066");
		this.properties.setProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR, "#ffcc6666");
		this.properties.setProperty(PropertyFields.EXPORT_WITH_ALARM, "true");
		try {
			this.storeProperties("Default properties stored");
		} catch (final IOException ioException) {
			this.LOG.catching(Level.WARN, ioException);
		}
	}

	/**
	 * @return the a propertie
	 */
	public static String getProperty(final String key) {
		String propertie = null;
		try {
			propertie = getInstance().properties.get(key).toString();
		} catch (NullPointerException nullPointerException) {
			;
		}
		return propertie;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * @see Properties
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadProperties() throws FileNotFoundException, IOException {
		this.properties.load(new FileInputStream(this.propertiesFile));
	}

	/**
	 * @see Properties
	 *
	 * @param comments
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void storeProperties(final String comments) throws FileNotFoundException, IOException {
		this.properties.store(new FileWriter(this.propertiesFile), comments);
	}
}
