package magicUWE.stereotypes.tags;

/**
 * externalLink Tags
 * 
 * @author PST LMU
 */
public enum UWETagSystemAction implements NodeTag {
	CONFIRMED("confirmed", true);

	public final static String associatedStereotype = "systemAction";

	private final String name;
	private final boolean isBoolean;

	private UWETagSystemAction(String name, boolean isBoolean) {
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
