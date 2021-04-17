package application.util;

import application.model.PersonManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PropertyManager {
    private static final String PROPERTIES_FILE_NAME = "BirthdayManager.properties";
    private static final Logger LOG = LogManager.getLogger(PropertyManager.class.getName());
    private static PropertyManager propertyManagerSingleton = null;
    private final Properties properties;
    private final File propertiesFile;

    /**
     * Basis ConfigHandler uses the a app.cfg file generates it if necessary Private constructor
     * restricted to this class itself.
     */
    private PropertyManager() {
        this.properties = new Properties();
        this.propertiesFile = new File(PROPERTIES_FILE_NAME);

        try {
            if (this.propertiesFile.createNewFile()) {
                LOG.info("Created new properties File");
                this.fillWithStandardProperties();
            } else {
                this.loadProperties();
                this.fillWithStandardProperties();
                LOG.info("Loaded properties");
            }
        } catch (final IOException ioException) {
            LOG.catching(Level.FATAL, ioException);
        }
    }

    /**
     * Static method to create instance of PersonManager class
     *
     * @return the only instance {@link PersonManager}
     */
    public static PropertyManager getInstance() {
        if (propertyManagerSingleton == null) {
            propertyManagerSingleton = new PropertyManager();
        }
        return propertyManagerSingleton;
    }

    /**
     * @param key For the property
     * @return a property as String
     */
    public static String getProperty(final String key) {
        try {
            return getInstance().properties.get(key).toString();
        } catch (NullPointerException nullPointerException) {
            LogManager.getLogger(PropertyManager.class.getName()).warn("Could not get property {}", key);
        }
        return null;
    }

    /**
     * Fills the properties with default values
     *
     * <ul>
     *   <li>SHOW_BIRTHDAYS_COUNT == 10
     *   <li>SAVED_LOCALE == en_EN
     *   <li>AUTOSAVE == false
     *   <li>WRITE_THRU == false
     * </ul>
     */
    protected void fillWithStandardProperties() {
        final String message = "Added {} property";
        if (this.properties.getProperty(PropertyFields.SAVED_LOCALE) == null) {
            this.properties.setProperty(PropertyFields.SAVED_LOCALE, "en_GB");
            LOG.info(message, PropertyFields.SAVED_LOCALE);
        }
        if (this.properties.getProperty(PropertyFields.AUTOSAVE) == null) {
            this.properties.setProperty(PropertyFields.AUTOSAVE, "true");
            LOG.info(message, PropertyFields.AUTOSAVE);
        }
        if (this.properties.getProperty(PropertyFields.WRITE_THRU) == null) {
            this.properties.setProperty(PropertyFields.WRITE_THRU, "true");
            LOG.info(message, PropertyFields.WRITE_THRU);
        }
        if (this.properties.getProperty(PropertyFields.OPEN_FILE_ON_START) == null) {
            this.properties.setProperty(PropertyFields.OPEN_FILE_ON_START, "false");
            LOG.info(message, PropertyFields.OPEN_FILE_ON_START);
        }
        if (this.properties.getProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT) == null) {
            this.properties.setProperty(PropertyFields.SHOW_BIRTHDAYS_COUNT, "15");
            LOG.info(message, PropertyFields.SHOW_BIRTHDAYS_COUNT);
        }
        if (this.properties.getProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR) == null) {
            this.properties.setProperty(PropertyFields.FIRST_HIGHLIGHT_COLOR, "#ff000066");
            LOG.info(message, PropertyFields.FIRST_HIGHLIGHT_COLOR);
        }
        if (this.properties.getProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR) == null) {
            this.properties.setProperty(PropertyFields.SECOND_HIGHLIGHT_COLOR, "#ffcc6666");
            LOG.info(message, PropertyFields.SECOND_HIGHLIGHT_COLOR);
        }
        if (this.properties.getProperty(PropertyFields.EXPORT_WITH_ALARM) == null) {
            this.properties.setProperty(PropertyFields.EXPORT_WITH_ALARM, "true");
            LOG.info(message, PropertyFields.EXPORT_WITH_ALARM);
        }
        if (this.properties.getProperty(PropertyFields.LAST_VISIT) == null) {
            this.properties.setProperty(PropertyFields.LAST_VISIT, LocalDate.now().toString());
            LOG.info(message, PropertyFields.LAST_VISIT);
        }
        if (this.properties.getProperty(PropertyFields.DARK_MODE) == null) {
            this.properties.setProperty(PropertyFields.DARK_MODE, "true");
            LOG.info(message, PropertyFields.DARK_MODE);
        }
        if (this.properties.getProperty(PropertyFields.NEW_VERSION_REMINDER) == null) {
            this.properties.setProperty(PropertyFields.NEW_VERSION_REMINDER, "false");
            LOG.info(message, PropertyFields.NEW_VERSION_REMINDER);
        }
        try {
            this.storeProperties("Default properties stored");
        } catch (final IOException ioException) {
            LOG.catching(Level.WARN, ioException);
        }
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void loadProperties() throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(this.propertiesFile)) {
            this.properties.load(fileInputStream);
        }
    }

    public void storeProperties(final String comments) throws IOException {
        try (FileOutputStream fileInputStream = new FileOutputStream(this.propertiesFile)) {
            this.properties.store(fileInputStream, comments);
        }
    }
}
