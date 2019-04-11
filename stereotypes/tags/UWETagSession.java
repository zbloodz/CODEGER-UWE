package magicUWE.stereotypes.tags;

/**
 * Session Tags
 * 
 * @author PST LMU
 */
public enum UWETagSession implements NodeTag {
	ROLES_EXPRESSION("rolesExpression", false),
	TRANSMISSION_TYPE("transmissionType", false);

	public final static String associatedStereotype = "session";

	private final String name;
	private final boolean isBoolean;

	private UWETagSession(String name, boolean isBoolean) {
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
