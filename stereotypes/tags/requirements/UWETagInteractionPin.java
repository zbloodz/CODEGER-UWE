/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.tags.requirements;

import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;
import magicUWE.stereotypes.tags.NodeTag;

/**
 * TextInput Tags
 *
 * @author PST LMU
 */
public enum UWETagInteractionPin implements NodeTag {
	SUBMIT_CHANGE("submitChange", true),
	LIVE_VALIDATION("liveValidation", true),
	AUTO_COMPLETION("autoCompletion", true),
	AUTO_SUGGESTION("autoSuggestion", true),
	MULTIPLE_SELECTION("multipleSelection", true);

	public final static String associatedStereotype = UWEStereotypeRequirementsPins.INTERACTION_PIN_OUTPUT.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = "no icon";

	private UWETagInteractionPin(String name, boolean isBoolean) {
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