package magicUWE.stereotypes.tags;

import magicUWE.stereotypes.UWEStereotypeClassNav;

/**
 * Query Tags
 * 
 * @author PST LMU
 */
public enum UWETagQuery implements NodeTag {
	EXPRESSION("expression", false);

	public final static String associatedStereotype = UWEStereotypeClassNav.QUERY.toString();

	private final String name;
	private final boolean isBoolean;

	private UWETagQuery(String name, boolean isBoolean) {
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
