package magicUWE.configurators.context;

import magicUWE.actions.context.DiagramContextBooleanTagAction;
import magicUWE.shared.UWEDiagramType;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * Extending DiagramContextConfigurator with the functionality of setting the
 * actual state of the menu-checkbox of every DiagramContextClassAction in the
 * category (e.g. for IS_HOME). Properties and classes are supportet. For
 * properties, the stereotypes of the associated class are used.
 * 
 * Only shown on States, Pseudostates, Classes or Properties!
 * 
 * @author PST LMU
 */
public class DiagramContextConfiguratorWithMenuStatesForTags extends DiagramContextConfigurator {

	private String addToThisMenu;

	public DiagramContextConfiguratorWithMenuStatesForTags(ActionsCategory category,
			UWEDiagramType onlyShowInThisDiagramType, String onlyShowOnElementsContainingThisStereotype,
			String addToThisMenu) {
		super(category, onlyShowInThisDiagramType, onlyShowOnElementsContainingThisStereotype);
		this.addToThisMenu = addToThisMenu;
	}

	/**
	 * configurator. <br/>
	 * oddity of MagicDraw: many times called when a Class or a Property element
	 * is selected
	 */
	@Override
	public void configure(ActionsManager mngr, DiagramPresentationElement diagram, PresentationElement[] selected,
			PresentationElement requestor) {
		logger.debug("configure() called from DiagramContextConfiguratorWithMenuStatesForTags");
		// only over classes and properties
		if (requestor != null && selected.length > 0
				&& (requestor.getElement() instanceof Pin || requestor.getElement() instanceof Action || requestor.getElement() instanceof UseCase || requestor.getElement() instanceof Package ||
                                requestor.getElement() instanceof Class || requestor.getElement() instanceof Property || requestor.getElement() instanceof State || requestor.getElement() instanceof Pseudostate)
				&& isToBeShown(diagram.getDiagram(), requestor)) {

			// set the state of the checkboxes according to the tag values
			for (Object cat : category.getActions()) {
				if (cat instanceof DiagramContextBooleanTagAction) {
					((DiagramContextBooleanTagAction) cat).setStateAccordingToValueOfTag(requestor.getElement());
				} else {
					// should not happen (because of menu navigation)
					((DiagramContextBooleanTagAction) cat).setState(false);

				}
			}
			// add category to the given context menu
			addToGivenMenu(mngr, addToThisMenu);
		} else {
			removeFromGivenMenu(mngr, addToThisMenu);
		}
	}

}
