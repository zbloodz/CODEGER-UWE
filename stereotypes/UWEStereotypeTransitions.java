/**
 * 
 */
package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import magicUWE.shared.UWEDiagramType;

/**
 * UWE Stereotypes of transitions
 * 
 * @author PST LMU
 */
public enum UWEStereotypeTransitions implements UWEStereotypeWithKey {
	INTEGRATED_MENU("integratedMenu", UWEDiagramType.NAVIGATION_STATES),
	SEARCH("search", UWEDiagramType.NAVIGATION_STATES),
	FROM_COLLECTION("allItems", UWEDiagramType.NAVIGATION_STATES);

	private final String displayName;
	private final String name;
	private final UWEDiagramType associatedDiagramType;
	private KeyStroke key = null;

	private UWEStereotypeTransitions(String name, UWEDiagramType associatedDiagramType) {
		this.name = name;
		this.displayName = name;
		this.associatedDiagramType = associatedDiagramType;
	}

	private UWEStereotypeTransitions(String name, UWEDiagramType associatedDiagramType, String displayName) {
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
