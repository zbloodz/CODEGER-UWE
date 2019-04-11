/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.tags.requirements;

import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.tags.NodeTag;

/**
 * TextInput Tags
 *
 * @author PST LMU
 */
public enum UWETagDisplayAction implements NodeTag {
	COLLAPSE("collapse", true),
	LIGHTBOX("lightbox", true),
	LIVE_REPORT("liveReport", true),
	RICH_EDITOR("richEditor", true),
	FILTER("filter", true),
	GALLERY("gallery", true);

	public final static String associatedStereotype = UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = "no icon";

	private UWETagDisplayAction(String name, boolean isBoolean) {
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