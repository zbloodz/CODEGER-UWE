package magicUWE.configurators.context;

import java.util.List;
import java.util.Map;

import magicUWE.shared.UWEDiagramType;

import org.apache.log4j.Logger;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.ClassDiagramContextAMConfigurator;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.AssociationView;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

/**
 * This class configures the diagram context MagicUWE menu item. Displayed
 * according to the constructor variables onlyShowInThisDiagramType and
 * onlyShowOnElementsContainingThisStereotype.
 * 
 * Only shown on States, Pseudostates, Classes or Properties or Packages!
 * 
 * 
 * @author PST LMU
 */
public class DiagramContextConfigurator extends ClassDiagramContextAMConfigurator {

	protected static final Logger logger = Logger.getLogger(DiagramContextConfigurator.class);
	protected final ActionsCategory category;
	private final UWEDiagramType onlyShowInThisDiagramType;
	private final String onlyShowOnElementsContainingThisStereotype;

	/**
	 * Creates configurator which adds the given top-level context menu. Only
	 * added over association(s) or over one class/state.
	 * 
	 * @param category
	 *            action to be added to manager.
	 * @param onlyShowInThisDiagramType
	 *            (can be null)
	 * @param onlyShowOnElementsContainingThisStereotype
	 *            (can be null)
	 */
	public DiagramContextConfigurator(ActionsCategory category, UWEDiagramType onlyShowInThisDiagramType,
			String onlyShowOnElementsContainingThisStereotype) {
		this.category = category;
		this.onlyShowInThisDiagramType = onlyShowInThisDiagramType;
		this.onlyShowOnElementsContainingThisStereotype = onlyShowOnElementsContainingThisStereotype;
		logger.debug("DiagramContextConfigurator entered");
	}

	/**
	 * top-level context menu ONLY. <br/>
	 * oddity of MagicDraw: many times called when a Class/State or a Property
	 * element is selected
	 */
	@Override
	public void configure(ActionsManager mngr, DiagramPresentationElement diagram, PresentationElement[] selected,
			PresentationElement requestor) {
		// another oddity of MagicDraw: requestor is null when more than one
		// element is selected

		// if you want to display this menu over more than one object
		// CHANGE IT HERE! (more than one association is allowed)
		if ((selected.length > 0 && selected[0] instanceof AssociationView && isToBeShown(diagram.getDiagram(),
				selected[0]))
				|| (requestor != null
						&& selected.length == 1
						&& (requestor.getElement() instanceof Class || requestor.getElement() instanceof Action
                                                                || requestor.getElement() instanceof Pin
                                                                || requestor.getElement() instanceof UseCase
								|| requestor.getElement() instanceof Property
								|| requestor.getElement() instanceof State
								|| requestor.getElement() instanceof Pseudostate 
                                                                || requestor.getElement() instanceof Package) && isToBeShown(
						diagram.getDiagram(), requestor))) {
			mngr.addCategory(category);
			
		} else {
			// we don't want to have an old menu here
			mngr.removeCategory(category);
		}
	}

	/**
	 * add the category to a given parent-menu
	 * 
	 * @param mngr
	 * @param addToThisMenu
	 */
	protected void addToGivenMenu(ActionsManager mngr, String addToThisMenu) {
		ActionsCategory parentCategory = mngr.getCategory(addToThisMenu);
		if (parentCategory != null) {
			// add category to the given context menu
			parentCategory.addAction(category);
			// logger.debug("Added category \"" + category.getName() + "\" to
			// \"" + addToThisMenu + "\"");
		} else {
			logger.debug("can't get parent context menu \"" + addToThisMenu + "\" for " + category.getName());
		}
	}

	/**
	 * remove action from given menu
	 * 
	 * @param mngr
	 * @param addToThisMenu
	 */
	protected void removeFromGivenMenu(ActionsManager mngr, String addToThisMenu) {
		ActionsCategory parentCategory = mngr.getCategory(addToThisMenu);
		if (parentCategory != null) {
			parentCategory.removeAction(category);
		}
	}

	/**
	 * Only show the menu if its the context menu of the wanted diagram and an
	 * element including the given stereotype
	 * 
	 * @param diagram
	 * @param requestor
	 *            (null => false)
	 * @return true <=> show diagram
	 */
	protected boolean isToBeShown(Diagram diagram, PresentationElement requestor) {
		if (requestor != null) {
			if (onlyShowInThisDiagramType != null) {
				if (UWEDiagramType.getDiagramType(diagram) == onlyShowInThisDiagramType) {
					return isToBeShownStereotype(requestor);
				}
				return false;
			}
			return isToBeShownStereotype(requestor);
		}
		return false;
	}

	/**
	 * @param requestor
	 * @return false <=> onlyShowOnElementsContainingThisStereotype is given but
	 *         element does not contain this stereotype
	 */
	private boolean isToBeShownStereotype(PresentationElement requestor) {
		if (onlyShowOnElementsContainingThisStereotype != null) {
			List<Stereotype> stereotypes = StereotypesHelper.getStereotypes(requestor.getElement());
			for (Stereotype s : stereotypes) {
				Stereotype stereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(),
						s.getName());
				Map<Stereotype, List<Property>> map = StereotypesHelper.getPropertiesIncludingParents(stereotype);
				for (Stereotype m : map.keySet()) {
					if (m.getName().equals(onlyShowOnElementsContainingThisStereotype)) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public int getPriority() {
		return MEDIUM_PRIORITY;
	}
}
