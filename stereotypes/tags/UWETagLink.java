package magicUWE.stereotypes.tags;


/**
 * Link Tags
 * 
 * @author PST LMU
 */
public enum UWETagLink implements NodeTag {
	IS_AUTOMATIC("isAutomatic", true), 
	GUARD("guard", false), 
	SELECTION_EXPRESSION("selectionExpression", false);

	public final static String associatedStereotype = "link";

	private final String name;
	private final boolean isBoolean;

	private UWETagLink(String name, boolean isBoolean) {
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
