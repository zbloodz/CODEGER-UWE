package magicUWE.settings;

/**
 * Defines the globals constants for MagicUWE.<br>
 * (also look at Strings in enums!)
 * 
 * 
 * @author PST LMU
 */
public abstract class GlobalConstants {

	// especially for the MagicUWE navigation diagram context menu
	public final static String UWE_NAVIGATION_DG_CONTEXT = "MagicUWE Navigation Features";
	public final static String UWE_NAV_STATES_DG_CONTEXT = "MagicUWE NavStates Features";
	public final static String UWE_USECASE_DG_CONTEXT = "MagicUWE Requirements Features";
	public final static String UWE_PRESENTATION_DG_CONTEXT = "MagicUWE Presentation Features";
	public static final String UWE_PROPERTIES_FILE = "plugins/com.generation.CodegerUWE/MagicUWE.properties";
	public final static String UWE_USECASEFLOW_DG_CONTEXT = "MagicUWE UseCase Activity Features";
	public final static String UWE_PROCESS_FLOW_DG_CONTEXT = "MagicUWE Process Flow Features";
	public final static String UWE_NAVIGATION_DG_TRANSFORMATIONS = "MagicUWE Navigation Transformations";
	public final static String UWE_CONTENT_DG_TRANSFORMATIONS = "MagicUWE Content Transformations";
	public final static String UWE_USER_MODEL_DG_TRANSFORMATIONS = "MagicUWE User Transformations";
	public final static String UWE_PRESENTATION_DG_TRANSFORMATIONS = "MagicUWE Presentation Transformations";
	public final static String UWE_PROCESS_DG_TRANSFORMATIONS = "MagicUWE Process Transformations";
	public static boolean USE_NEW_SUBMENU = true;

	/**
	 * Properties from MagicUWE.properties-file (standard set here, maybe
	 * overwritten from Properties.java)
	 */
	static String UWE_PROFILE_NAME = "UWE Profile.mdzip";
	static String UWE_PROFILE_NAME_IN_THE_PROJECT_DIR = "UWE Profile.mdzip";

	/**
	 * Properties for RIA Options
	 */
	public static String RIA_OPTION_TAG_ONLY = "by tag only";
	public static String RIA_OPTION_DEPENDENCIES = "adding dependencies between parts";
	public static String RIA_OPTION_BEHAVIOUR = "adding behaviour";
	public static String RIA_OPTION_ASK_EVERYTIME = "ask everytime";

	/**
	 * Getter for property-profile-name in the profile directory of MagicDraw
	 * 
	 * @return property
	 */
	public static String getUWE_PROPERTY_PROFILE_NAME() {
		return UWE_PROFILE_NAME;
	}

	/**
	 * Getter for property-profile-name in the same directory as the project
	 * file
	 * 
	 * @return property
	 */
	public static String getUWE_PROPERTY_PROFILE_NAME_IN_THE_PROJECT_DIR() {
		return UWE_PROFILE_NAME_IN_THE_PROJECT_DIR;
	}
}
