package magicUWE.actions.toolbar;

import java.util.List;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeWithKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DrawPathDiagramAction;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

/**
 * Create and draw an association into a diagram.
 * 
 * 
 * @author PST LMU
 */
public class DrawAssociationAction extends DrawPathDiagramAction {

	private static final Logger logger = Logger.getLogger(DrawAssociationAction.class);
	private static final long serialVersionUID = 1L;
	private final UWEStereotypeWithKey assocType;
	private final Boolean isDirected;
	private Boolean showWarningAgain = true;

	/**
	 * DrawAssociationAction
	 * 
	 * @param ster
	 * @param isDirected
	 */
	public DrawAssociationAction(UWEStereotypeWithKey ster, Boolean isDirected) {
		super(ster.getDisplayName(), ster.getDisplayName(), ster.getKeyStroke());
		this.assocType = ster;
		this.isDirected = isDirected;
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
		logger.debug("prepare/create Association element");
		Association association =
				Application.getInstance().getProject().getElementsFactory().createAssociationInstance();

		// add stereotype
		if (this.assocType.toString() != null) {
			MagicDrawElementOperations.addStereotypeToElement(association, this.assocType.toString());
		}

		List<Property> assProperties = association.getMemberEnd();
		if (assProperties.size() > 0) {
			// only set one assoc end navigable when you wan a directed assoc
			if (isDirected) {
				ModelHelper.setNavigable(assProperties.get(0), true);
			} else {
				for (Property property : assProperties) {
					ModelHelper.setNavigable(property, true);
				}
			}
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
