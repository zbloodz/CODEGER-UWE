package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.PseudostateKindEnum;

/**
 * The Navigation States stereotypes from UWE Profile
 * 
 * @author PST LMU
 */
public enum UWEStereotypeStatesNav implements UWEStereotypeOfElement {
	NAVIGATIONAL_NODE("navigationalNode", false),
	SESSION("session", false),
	COLLECTION("collection", false) ,
	TARGET("target", false),
	EXTERNAL_NODE("externalNode", true);

	private final String displayName;
	private final String name;
	private KeyStroke key;
	private boolean isTerminalNode;

	private UWEStereotypeStatesNav(String name) {
		this.name = name;
		this.displayName = name;
	}
	
	private UWEStereotypeStatesNav(String name, boolean isTerminalNode) {
		this.name = name;
		this.displayName = name;
		this.isTerminalNode = isTerminalNode;
	}

	private UWEStereotypeStatesNav(String name, Integer key) {
		this.name = name;
		this.displayName = name;
		setKeyStroke(key);
	}

	private UWEStereotypeStatesNav(String name, String displayName, Integer key) {
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
		// MAYBE .. refactored
		if (this.isTerminalNode){
			return MagicDrawElementOperations.createPseudoState(this.toString(), PseudostateKindEnum.TERMINATE);
		}
		return MagicDrawElementOperations.createState(this.toString());
	}
	
	@Override
	public UWEDiagramType[] getAssociatedDiagramTypes() {
		UWEDiagramType[] dgType = { UWEDiagramType.NAVIGATION_STATES };
		return dgType;
	}
}
