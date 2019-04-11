package magicUWE.stereotypes.tags;

/**
 * navigationNode Tags
 * 
 * @author PST LMU
 */
public enum UWETagNavigationNode implements NodeTag {
	IS_LANDMARK("isLandmark", true),
	IS_HOME("isHome", true),
	GUARD("guard", false),
	DATA_EXPRESSION("dataExpression", false);

	public final static String associatedStereotype = "navigationNode";

	private final String name;
	private final boolean isBoolean;

	private UWETagNavigationNode(String name, boolean isBoolean) {
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
