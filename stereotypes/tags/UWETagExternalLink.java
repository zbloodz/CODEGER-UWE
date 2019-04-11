package magicUWE.stereotypes.tags;

/**
 * externalLink Tags
 * 
 * @author PST LMU
 */
public enum UWETagExternalLink implements NodeTag {
	LOCATION_EXPRESSION("locationExpression", false);

	public final static String associatedStereotype = "externalLink";

	private final String name;
	private final boolean isBoolean;

	private UWETagExternalLink(String name, boolean isBoolean) {
		this.name = name;
		this.isBoolean = isBoolean;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getAssociatedStereotypeName() {
		return associatedStereotype;
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
