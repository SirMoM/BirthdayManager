package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigHandler{
	private final Properties properties;

	private final File configFile;

	/**
	 * Basis ConfigHandler uses the a app.cfg file generates it if necessary
	 * 
	 * @throws IOException
	 */
	public ConfigHandler() throws IOException{
		super();
		this.properties = new Properties();
		this.configFile = new File("./app.cfg");
		if(!this.configFile.exists()){
			this.configFile.createNewFile();
		}

	}

	/**
	 * Basis ConfigHandler uses the file and generates it if necessary
	 * 
	 * @param file the config File
	 * @throws IOException
	 */
	public ConfigHandler(File file) throws IOException{
		super();
		this.properties = new Properties();
		this.configFile = file;
		if(!this.configFile.exists()){
			this.configFile.createNewFile();
		}
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties(){
		return this.properties;
	}

	public void loadProperties() throws FileNotFoundException, IOException{
		this.properties.load(new FileInputStream(this.configFile));
	}

	public void storeProperties(String comments) throws FileNotFoundException, IOException{
		this.properties.store(new FileWriter(this.configFile), comments);
	}
}
