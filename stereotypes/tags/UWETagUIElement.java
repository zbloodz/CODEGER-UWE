package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;

/**
 * uiElement Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagUIElement implements NodeTag {
	DISABLING_CONDITION("disablingCondition", false),
	ENABLING_CONDITION("enablingCondition", false),
	ID("id", false),
	STYLE_CLASS("styleClass", false),
	STYLE_CLASS_EXPRESSION("styleClassExpression", false),
	VISIBILITY_CONDITION("visibilityCondition", false),
	LIVE_SEARCH_CONDITION("liveSearchCondition", false);

	public final static String associatedStereotype = "uiElement";

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagUIElement(String name, boolean isBoolean) {
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
