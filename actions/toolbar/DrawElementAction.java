package magicUWE.actions.toolbar;

import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeOfElement;
import magicUWE.stereotypes.UWEStereotypeWithKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Create and draw a class into a diagram.
 * 
 * 
 * @author PST LMU
 */
public class DrawElementAction extends DrawShapeDiagramAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DrawElementAction.class);
	private final UWEStereotypeOfElement stereotype;
	private Boolean showWarningAgain = true;

	/*
	 * Note: It's possible to load icons from profile to e.g toolbar items. The
	 * problem is, that if a diagram is opened while MagicDraw starts, the icons
	 * are not loaded fast enough. That is because the test if the profile
	 * should be loaded is executed at "projectOpened()" and the open diagrams
	 * are shown before that event occurs.
	 * 
	 * Contains all elements in the Toolbar. Allows eg ProjectListener to add
	 * Stereotype-Icons:
	 * 
	 * public static LinkedList<DrawClassAction> allDrawClassActions = new
	 * LinkedList<DrawClassAction>();
	 * 
	 * Some useful code:
	 * 
	 * for (DrawClassAction ctaction : DrawClassAction.allDrawClassActions) ...
	 * StereotypesHelper.getStereotype(Application.getInstance().getProject(),
	 * ctaction.getName()); ResizableIcon icon =
	 * StereotypesHelper.getIcon(ster); ctaction.setLargeIcon(icon);
	 */

	/**
	 * DrawElementAction
	 * 
	 * @param ster
	 */
	public DrawElementAction(UWEStereotypeOfElement ster) {
		super(ster.getDisplayName(), ster.getDisplayName(), ster.getKeyStroke());
		this.stereotype = ster;
	}

	/**
	 * Creates model element of the new element.
	 * 
	 * @return created model element
	 */
	@Override
	public Element createElement() {
		UWEDiagramType dgType =
				UWEDiagramType.getDiagramType(Application.getInstance().getProject().getActiveDiagram().getDiagram());
		// inform the user about the wrong diagram type

		if (showWarningAgain != null && showWarningAgain == true
				&& (dgType == null || !dgType.mayContainStereotype(this.stereotype))) {
			showWarningAgain = showWarningBecauseOfWrongDiagramTypeEverySecondTime(stereotype, logger);
		}
		if (showWarningAgain == null){
			showWarningAgain = true;
		}
		// hint: if you want to return null here, you have to open and close a
		// session first
		return stereotype.createElementWithStereotype();
	}

	/**
	 * Wraps the first call of createElement(). Used to display the message
	 * maximally once per toolbar action (uses null value of showWarningAgain =>
	 * must be changed back to true)
	 */
	@Override
	protected PresentationElement createPresentationElement() {
		if (showWarningAgain == null){
			showWarningAgain = true;
		}
		PresentationElement presElement = super.createPresentationElement();
		if (showWarningAgain) {
			showWarningAgain = null;
		}
		return presElement;
	}

	/**
	 * show warning because of wrong UWE diagram type
	 * 
	 * @param ster
	 *            Stereotype of the element that the user want to insert
	 * @param otherLogger
	 * @return true <=> the user wants to see this warning again.
	 */
	public static boolean showWarningBecauseOfWrongDiagramTypeEverySecondTime(UWEStereotypeWithKey ster,
			Logger otherLogger) {
		String message =
				"<html>This is the wrong UWE diagram type for a " + ster.toString()
						+ ".<br/>One of the following diagram types was expected: "
						+ UWEDiagramType.getMessageAboutRightDiagramTypesForAStereotype(ster)
						+ ".<br/><br/>It's recommended to cancel this operation.</html>";
		otherLogger.info(message);
		return (new MessageWithDisplayAgainOption(message)).showAgain;
	}
}
