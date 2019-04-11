package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * The Class stereotypes from UWE Profile which doesn't belong to an own
 * category
 * 
 * @author PST LMU
 */
public enum UWEStereotypeClassGeneral implements UWEStereotypeOfElement {
	CONTENT_CLASS("") {
		@Override
		public UWEDiagramType[] getAssociatedDiagramTypes() {
			UWEDiagramType[] dgType = { UWEDiagramType.CONTENT };
			return dgType;

		}
	},
	PROCESS_CLASS("processClass") {
		// be careful, this is used in Navigation and in Process Diagrams!
		@Override
		public UWEDiagramType[] getAssociatedDiagramTypes() {
			UWEDiagramType[] dgType = { UWEDiagramType.PROCESS_STRUCTURE, UWEDiagramType.NAVIGATION };
			return dgType;
		}
	};

	private final String displayName;
	private final String name;
	private KeyStroke key = null;

	private UWEStereotypeClassGeneral(String name) {
		this.name = name;
		this.displayName = name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * displayName shown in the toolbar. If not set in constructor, its
	 * equal to toString().
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
		return MagicDrawElementOperations.createClass(this.toString());
	}
}
