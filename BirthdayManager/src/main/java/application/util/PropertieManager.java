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

/**
 * @author Admin
 * @see <a href="https://github.com/SirMoM/BirthdayManager">Github</a>
 */
public class PropertieManager{

	private final Logger LOG = LogManager.getLogger(this.getClass().getName());

	private final Properties properties;
	private final File propertiesFile;
	private final String PROPERTIE_FILE_NAME = "BirthdayManager.properties";

	/**
	 * Basis ConfigHandler uses the a app.cfg file generates it if necessary
	 *
	 */
	public PropertieManager(){
		this.properties = new Properties();
//		this.propertiesFile = new File(System.getProperty("java.io.tmpdir") + this.PROPERTIE_FILE_NAME);
		this.propertiesFile = new File(this.PROPERTIE_FILE_NAME);
//		this.LOG.info("Propertie File " + this.propertiesFile.getAbsolutePath());

		try{
			if(this.propertiesFile.createNewFile()){
				this.LOG.info("Created new properties File");
				this.fillWithStandartProperties();
				this.LOG.info("Loaded default properties");
			} else{
				this.loadProperties();
//				this.LOG.info("Loaded properties");
			}
		} catch (final FileNotFoundException fileNotFoundException){
			this.LOG.catching(Level.FATAL, fileNotFoundException);
			// TODO Dialog FATAL
		} catch (final IOException ioException){
			this.LOG.catching(Level.FATAL, ioException);
			// TODO Dialog FATAL
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
	private void fillWithStandartProperties(){
		this.properties.setProperty(PropertieFields.SHOW_BIRTHDAYS_COUNT, "10");
		this.properties.setProperty(PropertieFields.SAVED_LOCALE, "en_GB");
		this.properties.setProperty(PropertieFields.AUTOSAVE, "true");
		this.properties.setProperty(PropertieFields.WRITE_THRU, "false");
		this.properties.setProperty(PropertieFields.OPEN_FILE_ON_START, "false");
		this.properties.setProperty(PropertieFields.HIGHLIGHT_TODAY_COLOR, "mediumseagreen");
		try{
			this.storeProperties("Default properties stored");
		} catch (final IOException ioException){
			this.LOG.catching(Level.WARN, ioException);
		}
	}

	/**
	 * @return the a propertie
	 */
	public String getPropertie(final String key){
		return this.properties.get(key).toString();
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties(){
		return this.properties;
	}

	/**
	 * @see Properties
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadProperties() throws FileNotFoundException, IOException{
		this.properties.load(new FileInputStream(this.propertiesFile));
	}

	/**
	 * @see Properties
	 *
	 * @param comments
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void storeProperties(final String comments) throws FileNotFoundException, IOException{
		this.properties.store(new FileWriter(this.propertiesFile), comments);
	}
}
