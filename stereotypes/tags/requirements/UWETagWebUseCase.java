/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.tags.requirements;

import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;
import magicUWE.stereotypes.tags.NodeTag;

/**
 * TextInput Tags
 *
 * @author PST LMU
 */
public enum UWETagWebUseCase implements NodeTag {
	IS_LANDMARK("isLandmark", true),
	GUARD("guard", false),
	TRANSFER_TYPE("transferType", false);

	public final static String associatedStereotype = UWEStereotypeRequirementsUseCases.WEBUSECASE_USECASE.toString();

	private final String name;
	private final boolean isBoolean;
	private String riaOption = "no icon";

	private UWETagWebUseCase(String name, boolean isBoolean) {
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