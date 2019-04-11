package magicUWE.actions.toolbar;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeWithKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DrawPathDiagramAction;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Transition;

/**
 * Create and draw an transition into a state chart.
 * 
 * @author PST LMU
 */
public class DrawTransitionAction extends DrawPathDiagramAction {

	private static final Logger logger = Logger.getLogger(DrawElementAction.class);
	private static final long serialVersionUID = 1L;
	private final UWEStereotypeWithKey assocType;
	private Boolean showWarningAgain = true;

	/**
	 * Draw transission action
	 * 
	 * @param ster
	 */
	public DrawTransitionAction(UWEStereotypeWithKey ster) {
		super(ster.getDisplayName(), ster.getDisplayName(), ster.getKeyStroke());
		this.assocType = ster;
	}

	@Override
	protected Element createElement() {
		// inform the user about the wrong diagram type once
		UWEDiagramType dgType =
				UWEDiagramType.getDiagramType(Application.getInstance().getProject().getActiveDiagram().getDiagram());
		if (showWarningAgain != null && showWarningAgain == true
				&& (dgType == null || !dgType.mayContainStereotype(this.assocType))) {
			showWarningAgain = DrawElementAction.showWarningBecauseOfWrongDiagramTypeEverySecondTime(assocType, logger);
		}
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		logger.debug("prepare/create Transition element");
		Transition association =
				Application.getInstance().getProject().getElementsFactory().createTransitionInstance();

		// add stereotype
		if (this.assocType.toString() != null) {
			MagicDrawElementOperations.addStereotypeToElement(association, this.assocType.toString());
		}
		return association;
	}

	/**
	 * Wraps the first call of createElement(). Used to display the message
	 * maximally once per toolbar action (uses null value of showWarningAgain =>
	 * must be changed back to true)
	 */
	@Override
	protected PathElement createPathElement() {
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		PathElement pathElement = super.createPathElement();
		if (showWarningAgain) {
			showWarningAgain = null;
		}
		return pathElement;
	}
}
