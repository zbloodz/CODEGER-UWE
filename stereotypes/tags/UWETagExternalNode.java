package magicUWE.stereotypes.tags;

import magicUWE.stereotypes.UWEStereotypeClassNav;

/**
 * ExternalNode Tags
 * 
 * @author PST LMU
 */
public enum UWETagExternalNode implements NodeTag {
	LOCATION_EXPRESSION("locationExpression", false);

	public final static String associatedStereotype = UWEStereotypeClassNav.EXTERNAL_NODE.toString();

	private final String name;
	private final boolean isBoolean;

	private UWETagExternalNode(String name, boolean isBoolean) {
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
