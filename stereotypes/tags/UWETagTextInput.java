package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;
import magicUWE.stereotypes.UWEStereotypeClassPres;

/**
 * TextInput Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagTextInput implements NodeTag {
	AUTO_COMPLETION("autoCompletion", true),
	AUTO_SUGGESTION("autoSuggestion", true),
	LIVE_VALIDATION("liveValidation", true);

	public final static String associatedStereotype = UWEStereotypeClassPres.TEXT_INPUT.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagTextInput(String name, boolean isBoolean) {
		this.name = name;
		this.isBoolean = isBoolean;
	}

	@Override
	public String getAssociatedStereotypeName() {
		return associatedStereotype;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean isBoolean() {
		return isBoolean;
	}

	@Override
	public String getRIAOption() {
		return riaOption;
	}

	@Override
	public void setRIAOption(String option) {
		this.riaOption = option;
	}
}
