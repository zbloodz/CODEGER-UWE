package magicUWE.stereotypes.tags;

/**
 * navigationalNode Tags
 * 
 * @author PST LMU
 */
public enum UWETagNavigationalNode implements NodeTag {
	IS_MODAL("isModal", true),
	IS_HOME("isHome", true);

	public final static String associatedStereotype = "navigationalNode";

	private final String name;
	private final boolean isBoolean;

	private UWETagNavigationalNode(String name, boolean isBoolean) {
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
