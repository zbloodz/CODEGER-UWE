/**
 * 
 */
package magicUWE.stereotypes;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import magicUWE.shared.UWEDiagramType;

/**
 * Abstract Functions for Stereotype-Enums. Every Stereotype should provide a
 * shortcut for the MagicUWE Toolbar
 * 
 * @author PST LMU
 */
public interface UWEStereotypeWithKey {
	/**
	 * Must be pressed additionally to the KeyStrokes. Set only from
	 * Properties-File.
	 */
	public static int keyModifier = InputEvent.SHIFT_MASK + InputEvent.ALT_MASK;

	/**
	 * @return Name of Stereotype
	 */
	@Override
	public abstract String toString();

	/**
	 * @return the diagram wherein the stereotype can be used
	 */
	public abstract UWEDiagramType[] getAssociatedDiagramTypes();

	/**
	 * @return displayName shown in the toolbar. If not set in
	 *         constructor, it is equal to toString(). - should be unique even
	 *         without spaces (setOfNames is 0 here)
	 */
	public abstract String getDisplayName();

	/**
	 * Return the name from the chosen setOfNames
	 * 
	 * @see UWEStereotypeWithKey#getDisplayName()
	 * @param setOfNames
	 * @return displayName
	 */
	public abstract String getDisplayName(int setOfNames);

	/**
	 * @return keystroke for menu
	 */
	public abstract KeyStroke getKeyStroke();

	/**
	 * Set KeyStroke. Please set only from Properties-File.
	 * 
	 * @param key
	 */
	public abstract void setKeyStroke(Integer key);
}