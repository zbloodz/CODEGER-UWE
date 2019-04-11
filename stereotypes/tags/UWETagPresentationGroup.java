package magicUWE.stereotypes.tags;

import magicUWE.settings.GlobalConstants;
import magicUWE.stereotypes.UWEStereotypeClassPres;

/**
 * PresentationGroup Tags
 * 
 * @author PST LMU
 * 
 */
public enum UWETagPresentationGroup implements NodeTag {
	COLLAPSE("collapse", true),
	LIGHTBOX("lightbox", true),
	LIVE_REPORT("liveReport", true),
	RICH_EDITOR("richEditor", true),
	FILTER("filter", true),
	GALLERY("gallery", true);

	public final static String associatedStereotype = UWEStereotypeClassPres.PRESENTATION_GROUP.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = GlobalConstants.RIA_OPTION_ASK_EVERYTIME;

	private UWETagPresentationGroup(String name, boolean isBoolean) {
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
