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
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

/**
 * This class configures the diagram context MagicUWE menu item. Displayed
 * according to the constructor variables onlyShowInThisDiagramType and
 * onlyShowOnElementsContainingThisStereotype.
 * 
 * 
 * @author PST LMU
 */
public class DiagramContextConfiguratorSelect extends ClassDiagramContextAMConfigurator {

	protected static final Logger logger = Logger.getLogger(DiagramContextConfiguratorSelect.class);
	protected final ActionsCategory category;
	protected final UWEDiagramType onlyShowInThisDiagramType;
	protected final String onlyShowOnElementsContainingThisStereotype;
        protected final boolean bShowOnAssociations;
        protected final boolean bShowOnClasses;
        protected final boolean bShowOnDiagram;

	/**
	 * Creates configurator which adds the given top-level context menu. Only
	 * added over association(s) or over one class.
	 * 
	 * @param category
	 *            action to be added to manager.
	 * @param onlyShowInThisDiagramType
	 *            (can be null)
	 * @param onlyShowOnElementsContainingThisStereotype
	 *            (can be null)
	 */
	public DiagramContextConfiguratorSelect(ActionsCategory category, UWEDiagramType onlyShowInThisDiagramType,
			String onlyShowOnElementsContainingThisStereotype,boolean bShowOnAssociations,boolean bShowOnClasses,boolean bShowOnDiagram) {
		this.category = category;
		this.onlyShowInThisDiagramType = onlyShowInThisDiagramType;
		this.onlyShowOnElementsContainingThisStereotype = onlyShowOnElementsContainingThisStereotype;
		logger.debug("DiagramContextConfigurator entered");

                this.bShowOnAssociations=bShowOnAssociations;
                this.bShowOnClasses=bShowOnClasses;
                this.bShowOnDiagram=bShowOnDiagram;
	}

	/**
	 * top-level context menu ONLY. <br/>
	 * oddity of MagicDraw: many times called when a Class or a Property element
	 * is selected
	 */
	@Override
	public void configure(ActionsManager mngr, DiagramPresentationElement diagram, PresentationElement[] selected,
			PresentationElement requestor) {
		// another oddity of MagicDraw: requestor is null when more than one
		// element is selected

		mngr.removeCategory(category);

		// if you want to display this menu over more than one class object
		// CHANGE IT HERE! (more than one association is allowed)
		if (bShowOnAssociations && selected.length > 0 && selected[0] instanceof AssociationView && isToBeShown(diagram.getDiagram(),
				selected[0]))
                {
			mngr.addCategory(category);
                }
                else if (bShowOnClasses && requestor != null && selected.length == 1
						&& (requestor.getElement() instanceof Class || requestor.getElement() instanceof Property) && isToBeShown(
						diagram.getDiagram(), requestor))
                {
			// logger.debug("Add category \"" + category.getName() + "\" to the
			// context menu");

			mngr.addCategory(category);
		}
                else if (bShowOnDiagram && requestor == null &&
                        onlyShowOnElementsContainingThisStereotype==null &&
                        (UWEDiagramType.getDiagramType(diagram.getDiagram()) == onlyShowInThisDiagramType))
                {
                    mngr.addCategory(category);
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
				Stereotype stereotype =
						StereotypesHelper.getStereotype(Application.getInstance().getProject(), s.getName());
				Map<Stereotype, List<Property>> map =
						StereotypesHelper.getPropertiesIncludingParents(stereotype);
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
