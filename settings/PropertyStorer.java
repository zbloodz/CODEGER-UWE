package magicUWE.settings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Class for storing default properties in the MagicUWE.properties file
 * 
 * 
 */
public abstract class PropertyStorer {

	private static final Logger logger = Logger.getLogger(PropertyLoader.class);

	private static Properties prop = PropertyLoader.properties;

	private static FileOutputStream output;

	/**
	 * Stores a property with given key and value
	 * 
	 * @param key
	 * @param value
	 */
	public static void storeProperty(String key, String value) {
		try {
			output = new FileOutputStream(GlobalConstants.UWE_PROPERTIES_FILE);
		} catch (FileNotFoundException e1) {
			logger.debug("File " + GlobalConstants.UWE_PROPERTIES_FILE + " not found");
		}
		prop.setProperty(key, value);
		try {
			prop.store(output, null);
		} catch (Exception e) {
			logger.debug("The properties could not be stored in " + GlobalConstants.UWE_PROPERTIES_FILE);
		}
	}

}
