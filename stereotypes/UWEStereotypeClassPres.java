package magicUWE.stereotypes;

import javax.swing.KeyStroke;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * enum containing the UWE Stereotypes for the Presentation Model
 * 
 * @author PST LMU
 */
public enum UWEStereotypeClassPres implements UWEStereotypeOfElWithSecondKey {
	PRESENTATION_GROUP("presentationGroup"),
	PRESENTATION_PAGE("presentationPage"),
	INPUT_FORM("inputForm"),
	IMAGE("image"),
	BUTTON("button"),
	ANCHOR("anchor"),
	TEXT("text"),
	TEXT_INPUT("textInput"),
	SELECTION("selection"),
	CUSTOM_COMPONENT("customComponent"),
	FILE_UPLOAD("fileUpload"),
	MEDIA_OBJECT("mediaObject"),
	ITERATED_PRESENTATION_GROUP("iteratedPresentationGroup"),
	PRESENTATION_ALTERNATIVES("presentationAlternatives"),
	IMAGE_INPUT("imageInput"),
	TAB("tab");

	private final String displayName;
	private final String name;
	private KeyStroke key = null;
	private KeyStroke secondKey;
	private boolean shouldBeUsedAsClass = true;
	private boolean shouldBeUsedAsProperty = true;

	private UWEStereotypeClassPres(String name) {
		this.name = name;
		this.displayName = name;
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

	/**
	 * Nr 0 = default<br/>
	 * Nr 1 = default + Property
	 */
	@Override
	public String getDisplayName(int setOfNames) {
		switch (setOfNames) {
		case 1:
			return displayName + " Property";
		default:
			return getDisplayName();
		}
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

	public boolean getShouldBeUsedAsClass() {
		return shouldBeUsedAsClass;
	}

	public void setShouldBeUsedAsClass(boolean shouldBeUsedAsClass) {
		this.shouldBeUsedAsClass = shouldBeUsedAsClass;
	}

	public boolean getShouldBeUsedAsProperty() {
		return shouldBeUsedAsProperty;
	}

	public void setShouldBeUsedAsProperty(boolean shouldBeUsedAsProperty) {
		this.shouldBeUsedAsProperty = shouldBeUsedAsProperty;
	}

	@Override
	public KeyStroke getSecondKeyStroke() {
		return this.secondKey;
	}

	@Override
	public void setSecondKeyStroke(Integer key) {
		if (key == null) {
			this.secondKey = null;
		} else {
			this.secondKey = KeyStroke.getKeyStroke(key, keyModifier);
		}
	}

	@Override
	public UWEDiagramType[] getAssociatedDiagramTypes() {
		UWEDiagramType[] dgType = { UWEDiagramType.PRESENTATION };
		return dgType;
	}
	
	@Override
	public Element createElementWithStereotype() {
		return MagicDrawElementOperations.createClass(this.toString());
	}
}
