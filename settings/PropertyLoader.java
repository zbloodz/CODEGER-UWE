package magicUWE.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import magicUWE.riaPatterns.RIATagsHelper;
import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeAssoc;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeClassPres;
import magicUWE.stereotypes.UWEStereotypeOfElWithSecondKey;
import magicUWE.stereotypes.UWEStereotypeProcessFlow;
import magicUWE.stereotypes.UWEStereotypeStatesNav;
import magicUWE.stereotypes.UWEStereotypeTransitions;
import magicUWE.stereotypes.UWEStereotypeWithKey;
import magicUWE.stereotypes.tags.NodeTag;

import org.apache.log4j.Logger;

/**
 * Class for loading default properties from the MagicUWE.properties file
 * 
 * @author PST LMU
 */
public abstract class PropertyLoader {
	// properties variables key (set in properties file)
	private static final String PROPKey_UWE_PROFILE_NAME = "uweProfileName";
	private static final String PROPKey_UWE_PROFILE_NAME_IN_THE_PROJECT_DIR = "uweProfileNameInTheProjectDirectory";
	private static final String PROPKey_USE_NEW_SUBMENU = "useNewSubmenu";

	private static final Logger logger = Logger.getLogger(PropertyLoader.class);

	protected static Properties properties;

	/**
	 * Creates and loads all default MagicUWE properties. In case of an error,
	 * the user is informed with a MsgBox and default values will be used.
	 */
	public static void initConstants() {
		properties = new Properties();
		// check if MagicUWE.properties already exists in plugin-folder
		File propFile = new File(GlobalConstants.UWE_PROPERTIES_FILE);
		try {
			properties.load(new FileInputStream(propFile));
		} catch (IOException e) {
			logger.error(e.getMessage());
			MessageWriter.showError("Error reading the " + GlobalConstants.UWE_PROPERTIES_FILE + " file.\n"
					+ "Please configure it, (perhaps reinstall the MagicUWE Plugin)!\n"
					+ "You will now work with default values.", logger);
			return; // default-values are already set in the various Classes
		}
		// Load name of the UWE profile and save it into GlobalConstants
		GlobalConstants.UWE_PROFILE_NAME_IN_THE_PROJECT_DIR =
				properties.getProperty(PROPKey_UWE_PROFILE_NAME_IN_THE_PROJECT_DIR,
						GlobalConstants.UWE_PROFILE_NAME_IN_THE_PROJECT_DIR);
		GlobalConstants.UWE_PROFILE_NAME =
				properties.getProperty(PROPKey_UWE_PROFILE_NAME, GlobalConstants.UWE_PROFILE_NAME);

		// Load UWE NewDiagram-Submenu setting
		GlobalConstants.USE_NEW_SUBMENU =
				Boolean.valueOf(properties.getProperty(PROPKey_USE_NEW_SUBMENU,
						String.valueOf(GlobalConstants.USE_NEW_SUBMENU)));
		
		propertiesToUWEStereotypeWithKeyDescendants(properties);
		propertiesForPropertyOrAndClassMenuToUWEStereotypeClassPres(properties);
		propertiesForRiaOptions(properties);

		logger.debug("All properties were read and saved successfully in the GlobalConstants");

	}

	/**
	 * Load if the presentation element should be shown in the properties or/and
	 * class toolbar-menu
	 * 
	 * @param prop
	 */
	private static void propertiesForPropertyOrAndClassMenuToUWEStereotypeClassPres(Properties prop) {
		// Load Class Values
		for (UWEStereotypeClassPres ster : UWEStereotypeClassPres.values()) {
			ster.setShouldBeUsedAsClass(Boolean.valueOf(prop.getProperty(ster.getDisplayName().replaceAll(" ", ""),
					"true")));
			ster.setShouldBeUsedAsProperty(Boolean.valueOf(prop.getProperty(ster.getDisplayName(1).replaceAll(" ", ""),
					"true")));
		}
	}

	/**
	 * Load property values (Keys are the "key" + toString-Names) and save into
	 * descendants from {@link UWEStereotypeWithKey}
	 * 
	 * @param prop
	 */
	private static void propertiesToUWEStereotypeWithKeyDescendants(Properties prop) {
		final String keyPref = "key";
		for (UWEStereotypeWithKey ster : UWEStereotypeClassNav.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeAssoc.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeClassPres.values()) {
			readStereotypeProperty(prop, keyPref + "class", ster);
		}
		for (UWEStereotypeOfElWithSecondKey ster : UWEStereotypeClassPres.values()) {
			readStereotypeProperty(prop, keyPref + "property", ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeClassGeneral.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeProcessFlow.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeStatesNav.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
		for (UWEStereotypeWithKey ster : UWEStereotypeTransitions.values()) {
			readStereotypeProperty(prop, keyPref, ster);
		}
	}

	private static void propertiesForRiaOptions(Properties prop) {
		final String prefix = "ria";
		LinkedList<NodeTag> riaTags = RIATagsHelper.getRIATags();
		for (NodeTag tag : riaTags) {
			if (tag.isBoolean()) {
				tag.setRIAOption(prop.getProperty((prefix + tag.toString()).replaceAll(" ", ""),
						GlobalConstants.RIA_OPTION_ASK_EVERYTIME));
			}
		}

	}

	private static void readStereotypeProperty(Properties prop, final String keyPref, UWEStereotypeWithKey ster) {
		ster.setKeyStroke(getStereotypeProperty(prop, keyPref, ster));
	}

	private static void readStereotypeProperty(Properties prop, final String keyPref,
			UWEStereotypeOfElWithSecondKey ster) {
		ster.setSecondKeyStroke(getStereotypeProperty(prop, keyPref, ster));
	}

	private static Integer getStereotypeProperty(Properties prop, final String keyPref, UWEStereotypeWithKey ster) {
		// no Shortcuts for Stereotypes without names
		if (!ster.toString().equals("")) {
			// use display name without spaces, it should be unique
			String sterKeyString = keyPref + ster.getDisplayName();
			sterKeyString = sterKeyString.replaceAll(" ", "");
			String value = prop.getProperty(sterKeyString);
			if (value != null) {
				if (!value.equals("null")) {
					// get Bytecode for letter
					byte[] bytesRepresentingKey = value.getBytes();
					if (bytesRepresentingKey.length >= 1) {
						return Integer.valueOf(bytesRepresentingKey[0]);
					}
					logger.error("Can't read \"" + sterKeyString + "\" from MagicUWE properties-File");
				}
			} else {
				logger.error("Can't find \"" + sterKeyString + "\" in MagicUWE properties-File");
			}
		}
		return null;
	}

}
