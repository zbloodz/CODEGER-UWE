package magicUWE.stereotypes;

import static magicUWE.shared.UWEDiagramType.BASIC_RIGHTS;

import javax.swing.KeyStroke;

import magicUWE.shared.UWEDiagramType;

/**
 * UWE Stereotypes of Basic Right dependencies
 * 
 * @author PST LMU
 */
public enum UWEStereotypeBasicRightsDependencies implements UWEStereotypeWithKey {
	CREATE("create", BASIC_RIGHTS),
	DELETE("delete", BASIC_RIGHTS),
	UPDATE_ALL("updateAll", BASIC_RIGHTS),
	READ_ALL("readAll", BASIC_RIGHTS),
	EXECUTE_ALL("executeAll", BASIC_RIGHTS),
	UPDATE("update", BASIC_RIGHTS),
	READ("read", BASIC_RIGHTS),
	EXECUTE("execute", BASIC_RIGHTS);

	private final String displayName;
	private final String name;
	private final UWEDiagramType associatedDiagramType;
	private KeyStroke key = null;

	private UWEStereotypeBasicRightsDependencies(String name, UWEDiagramType associatedDiagramType) {
		this.name = name;
		this.displayName = name;
		this.associatedDiagramType = associatedDiagramType;
	}

	private UWEStereotypeBasicRightsDependencies(String name, UWEDiagramType associatedDiagramType, String displayName) {
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
