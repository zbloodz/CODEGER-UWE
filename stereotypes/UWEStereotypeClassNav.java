package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * enum containing the UWE Stereotypes for the Navigation Model. Be careful,
 * {@link UWEStereotypeClassGeneral#PROCESS_CLASS} is one, too!
 * 
 * @author PST LMU
 */
public enum UWEStereotypeClassNav implements UWEStereotypeOfElement {
	NAVIGATION_CLASS("navigationClass"),
	GUIDEDTOUR("guidedTour"),
	INDEX("index"),
	QUERY("query"),
	MENU("menu"),
	EXTERNAL_NODE("externalNode");

	private final String displayName;
	private final String name;
	private KeyStroke key = null;

	private UWEStereotypeClassNav(String name) {
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
	public UWEDiagramType[] getAssociatedDiagramTypes() {
		UWEDiagramType[] dgType = { UWEDiagramType.NAVIGATION };
		return dgType;
	}
	
	@Override
	public Element createElementWithStereotype() {
		return MagicDrawElementOperations.createClass(this.toString());
	}
}
