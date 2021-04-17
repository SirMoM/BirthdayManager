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

public class PropertieManager{

	private final Logger LOG = LogManager.getLogger(this.getClass().getName());

	private final Properties properties;
	private final File propertiesFile;
	private final String PROPERTIE_FILE_NAME = "BirthdayManager.properties";

	/**
	 * Basis ConfigHandler uses the a app.cfg file generates it if necessary
	 *
	 * @throws IOException
	 */
	public PropertieManager() throws IOException{
		this.properties = new Properties();
		this.propertiesFile = new File(System.getProperty("java.io.tmpdir") + this.PROPERTIE_FILE_NAME);
		this.LOG.info("Propertie File " + this.propertiesFile.getAbsolutePath());

		if(this.propertiesFile.createNewFile()){
			this.LOG.info("Created new properties File");
			this.fillWithStandartProperties();
			this.LOG.info("Loaded default properties");
		} else{
			this.loadProperties();
			this.LOG.info("Loaded properties");
		}
	}

	private void fillWithStandartProperties(){
		this.properties.setProperty(PropertieFields.SHOW_BIRTHDAYS_COUNT, "10");
		this.properties.setProperty(PropertieFields.SAVED_LOCALE, "en_EN");
		this.properties.setProperty(PropertieFields.AUTOSAVE, "false");
		this.properties.setProperty(PropertieFields.WRITE_THRU, "false");
		try{
			this.storeProperties("Default properties stored");
		} catch (final IOException ioException){
			this.LOG.catching(Level.WARN, ioException);
		}
	}

	/**
	 * @return the properties
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

	public void loadProperties() throws FileNotFoundException, IOException{
		this.properties.load(new FileInputStream(this.propertiesFile));
	}

	public void storeProperties(final String comments) throws FileNotFoundException, IOException{
		this.properties.store(new FileWriter(this.propertiesFile), comments);
	}
}
