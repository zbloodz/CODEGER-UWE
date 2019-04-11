package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;


/**
 * OutputElement Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagOutputElement implements NodeTag {
	PERIODIC_REFRESH("periodicRefresh", true),
	DRAG_DROP("dragDrop", true);

	
	public final static String associatedStereotype = "outputElement";

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagOutputElement(String name, boolean isBoolean) {
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
		return riaOption;
	}

	@Override
	public void setRIAOption(String option) {
		this.riaOption = option;
	}
}
