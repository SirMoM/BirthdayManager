package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
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
			} else {
				this.loadProperties();
				this.fillWithStandartProperties();
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
		if (this.properties.getProperty(PropertyFields.SAVED_LOCALE) == null) {
			this.properties.setProperty(PropertyFields.SAVED_LOCALE, "en_GB");
			LOG.info("Added " + PropertyFields.SAVED_LOCALE + " propery");
		}
		if (this.properties.getProperty(PropertyFields.AUTOSAVE) == null) {
			this.properties.setProperty(PropertyFields.AUTOSAVE, "true");
			LOG.info("Added " + PropertyFields.AUTOSAVE + " propery");
		}
		if (this.properties.getProperty(PropertyFields.WRITE_THRU) == null) {
			this.properties.setProperty(PropertyFields.WRITE_THRU, "true");
			LOG.info("Added " + PropertyFields.WRITE_THRU + " propery");
		}
		if (this.properties.getProperty(PropertyFields.OPEN_FILE_ON_START) == null) {
			this.properties.setProperty(PropertyFields.OPEN_FILE_ON_START, "false");
			LOG.info("Added " + PropertyFields.OPEN_FILE_ON_START + " propery");
		}
		if (this.properties.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT) == null) {
			this.properties.setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT, "15");
			LOG.info("Added " + PropertyFields.SHOW_BIRTHDAYS_COUNT + " propery");
		}
		if (this.properties.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR) == null) {
			this.properties.setProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR, "#ff000066");
			LOG.info("Added " + PropertyFields.FIRST_HIGHLIGHT_COLOR + " propery");
		}
		if (this.properties.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR) == null) {
			this.properties.setProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR, "#ffcc6666");
			LOG.info("Added " + PropertyFields.SECOND_HIGHLIGHT_COLOR + " propery");
		}
		if (this.properties.getProperty(PropertyFields.EXPORT_WITH_ALARM) == null) {
			this.properties.setProperty(PropertyFields.EXPORT_WITH_ALARM, "true");
			LOG.info("Added " + PropertyFields.EXPORT_WITH_ALARM + " propery");
		}
		if (this.properties.getProperty(PropertyFields.LAST_VISIT) == null) {
			this.properties.setProperty(PropertyFields.LAST_VISIT, LocalDate.now().toString());
			LOG.info("Added " + PropertyFields.LAST_VISIT + " propery");
		}
		try {
			this.storeProperties("Default properties stored");
		} catch (final IOException ioException) {
			this.LOG.catching(Level.WARN, ioException);
		}
	}

	/**
	 * @param key For the property
	 * @return a property as String
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

	public Properties getProperties() {
		return this.properties;
	}

	public void loadProperties() throws FileNotFoundException, IOException {
		this.properties.load(new FileInputStream(this.propertiesFile));
	}

	public void storeProperties(final String comments) throws FileNotFoundException, IOException {
		this.properties.store(new FileWriter(this.propertiesFile), comments);
	}
}
