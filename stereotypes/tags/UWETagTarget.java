package magicUWE.stereotypes.tags;

/**
 * target Tags
 * 
 * @author PST LMU
 */
public enum UWETagTarget implements NodeTag {
	GO_BACK("goBack", true);

	public final static String associatedStereotype = "target";

	private final String name;
	private final boolean isBoolean;

	private UWETagTarget(String name, boolean isBoolean) {
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
