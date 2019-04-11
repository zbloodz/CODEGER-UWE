package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;


/**
 * ValuedElement Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagValuedElement implements NodeTag {
	VALUE_EXPRESSION("valueExpression", false);

	public final static String associatedStereotype = "valuedElement";

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagValuedElement(String name, boolean isBoolean) {
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
