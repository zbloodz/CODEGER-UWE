package magicUWE.stereotypes.tags;

import magicUWE.stereotypes.UWEStereotypeClassNav;

/**
 * GuidedTour Tags
 * 
 * @author PST LMU
 */
public enum UWETagGuidedTour implements NodeTag {
	SORT_EXPRESSION("sortExpression", false);

	public final static String associatedStereotype = UWEStereotypeClassNav.GUIDEDTOUR.toString();

	private final String name;
	private final boolean isBoolean;

	private UWETagGuidedTour(String name, boolean isBoolean) {
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
		return null;
	}

	@Override
	public void setRIAOption(String option) {
		// do nothing
	}
}
