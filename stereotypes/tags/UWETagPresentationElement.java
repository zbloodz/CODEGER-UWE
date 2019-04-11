package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;


/**
 * PresentationElement Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagPresentationElement implements NodeTag {
	DYNAMIC_DISPLAY("dynamicDisplay", true);

	
	public final static String associatedStereotype = "presentationElement";

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagPresentationElement(String name, boolean isBoolean) {
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
