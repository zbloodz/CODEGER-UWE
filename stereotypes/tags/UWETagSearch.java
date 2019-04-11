package magicUWE.stereotypes.tags;

/**
 * Search Tags
 * 
 * @author PST LMU
 */
public enum UWETagSearch implements NodeTag {
	EXPRESSION("expression", false);

	public final static String associatedStereotype = "search";

	private final String name;
	private final boolean isBoolean;

	private UWETagSearch(String name, boolean isBoolean) {
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
