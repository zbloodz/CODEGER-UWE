package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;

/**
 * The Process Flow stereotypes from UWE Profile
 * 
 * @author PST LMU
 */
public enum UWEStereotypeProcessFlow implements UWEStereotypeOfElement {
	USER_ACTION("userAction") {
		@Override
		public UWEDiagramType[] getAssociatedDiagramTypes() {
			UWEDiagramType[] dgType = { UWEDiagramType.PROCESS_FLOW };
			return dgType;
		}
	},
	SYSTEM_ACTION("systemAction") {
		@Override
		public UWEDiagramType[] getAssociatedDiagramTypes() {
			UWEDiagramType[] dgType = { UWEDiagramType.PROCESS_FLOW };
			return dgType;
		}
	};

	private final String displayName;
	private final String name;
	private KeyStroke key;

	private UWEStereotypeProcessFlow(String name) {
		this.name = name;
		this.displayName = name;
	}

	private UWEStereotypeProcessFlow(String name, Integer key) {
		this.name = name;
		this.displayName = name;
		setKeyStroke(key);
	}

	private UWEStereotypeProcessFlow(String name, String displayName, Integer key) {
		this.name = name;
		this.displayName = displayName;
		setKeyStroke(key);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * displayName shown in the toolbar. If not set in constructor, its equal to
	 * toString().
	 */
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
	public Element createElementWithStereotype() {
		return MagicDrawElementOperations.createAction(this.toString());
	}
}
