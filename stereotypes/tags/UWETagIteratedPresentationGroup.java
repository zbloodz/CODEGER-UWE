package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;
import magicUWE.stereotypes.UWEStereotypeClassPres;

/**
 * iteratedPresentationGroup Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagIteratedPresentationGroup implements NodeTag {
	 
	ITEM_VAR_NAME("itemVarName", false);

	public final static String associatedStereotype = UWEStereotypeClassPres.ITERATED_PRESENTATION_GROUP.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;


	private UWETagIteratedPresentationGroup(String name, boolean isBoolean) {
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
