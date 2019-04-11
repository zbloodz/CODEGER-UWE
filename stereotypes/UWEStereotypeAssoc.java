/**
 * 
 */
package magicUWE.stereotypes;

import static magicUWE.shared.UWEDiagramType.CONTENT;
import static magicUWE.shared.UWEDiagramType.NAVIGATION;
import static magicUWE.shared.UWEDiagramType.PRESENTATION;

import javax.swing.KeyStroke;

import magicUWE.shared.UWEDiagramType;

/**
 * UWE Stereotypes of associations
 * 
 * @author PST LMU
 */
public enum UWEStereotypeAssoc implements UWEStereotypeWithKey {
	NAVIGATION_LINK("navigationLink", NAVIGATION),
	NAVIGATION_LINK_DIRECTED("navigationLink", NAVIGATION, "directed navigationLink"),
	PRESENTATION_ASSOCIATION("", PRESENTATION),
	CONTENT_ASSOCIATION("", CONTENT),
	PROCESS_LINK("processLink", NAVIGATION),
	PROCESS_LINK_DIRECTED("processLink", NAVIGATION, "directed processLink");

	private final String displayName;
	private final String name;
	private final UWEDiagramType associatedDiagramType;
	private KeyStroke key = null;

	private UWEStereotypeAssoc(String name, UWEDiagramType associatedDiagramType) {
		this.name = name;
		this.displayName = name;
		this.associatedDiagramType = associatedDiagramType;
	}

	private UWEStereotypeAssoc(String name, UWEDiagramType associatedDiagramType, String displayName) {
		this.name = name;
		this.displayName = displayName;
		this.associatedDiagramType = associatedDiagramType;
	}

	/**
	 * Name of Stereotype
	 */
	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDisplayName(int setOfNames) {
		// only one possibility to show these stereotypes e.g in the toolbar
		return getDisplayName();
	}

	@Override
	public void setKeyStroke(Integer key) {
		if (key == null) {
			this.key = null;
		} else {
			this.key = KeyStroke.getKeyStroke(key, keyModifier);
		}
	}

	@Override
	public KeyStroke getKeyStroke() {
		return key;
	}

	@Override
	public UWEDiagramType[] getAssociatedDiagramTypes() {
		UWEDiagramType[] dgType = { this.associatedDiagramType };
		return dgType;
	}
}
