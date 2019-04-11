package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;


/**
 * InputElement Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagInputElement implements NodeTag {
	SUBMIT_CHANGE("submitChange", true);

	public final static String associatedStereotype = "inputElement";

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagInputElement(String name, boolean isBoolean) {
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
