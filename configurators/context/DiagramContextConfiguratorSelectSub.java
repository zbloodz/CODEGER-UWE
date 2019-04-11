package magicUWE.configurators.context;

import java.util.List;

import magicUWE.shared.UWEDiagramType;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.AssociationView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;

/**
 * A context menu which is only shown over selected associations
 * 
 * @author PST LMU
 */
public class DiagramContextConfiguratorSelectSub extends DiagramContextConfiguratorSelect {

	private String addToThisMenu;

	public DiagramContextConfiguratorSelectSub(ActionsCategory category,
			UWEDiagramType onlyShowInThisDiagramType,
			String addToThisMenu,
			String onlyShowOnElementsContainingThisStereotype,
                        boolean bShowOnAssociations,boolean bShowOnClasses,boolean bShowOnDiagram) {
		super(category, onlyShowInThisDiagramType,onlyShowOnElementsContainingThisStereotype,bShowOnAssociations,bShowOnClasses,bShowOnDiagram);
		this.addToThisMenu = addToThisMenu;
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
                removeFromGivenMenu(mngr, addToThisMenu);

		// if you want to display this menu over more than one class object
		// CHANGE IT HERE! (more than one association is allowed)
		if (bShowOnAssociations && selected.length > 0 && selected[0] instanceof AssociationView && isToBeShown(diagram.getDiagram(),
				selected[0]))
                {
			addToGivenMenu(mngr, addToThisMenu);
                }
                else if (bShowOnClasses && requestor != null && selected.length == 1
						&& (requestor.getElement() instanceof Class || requestor.getElement() instanceof Property) && isToBeShown(
						diagram.getDiagram(), requestor))
                {
			// logger.debug("Add category \"" + category.getName() + "\" to the
			// context menu");

			addToGivenMenu(mngr, addToThisMenu);
		}
                else if (bShowOnDiagram && requestor == null &&
                        onlyShowOnElementsContainingThisStereotype==null &&
                        (UWEDiagramType.getDiagramType(diagram.getDiagram()) == onlyShowInThisDiagramType))
                {
			addToGivenMenu(mngr, addToThisMenu);
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
	private boolean isToBeShownStereotype(PresentationElement requestor)
        {
            if (onlyShowOnElementsContainingThisStereotype != null) {
                List<Stereotype> stereotypes = StereotypesHelper.getStereotypes(requestor.getElement());
                for (Stereotype s : stereotypes) {
                    if (s.getName().equals(onlyShowOnElementsContainingThisStereotype))
                    {
                        return true;
                    }
                }
                return false;
            }
            return true;
	}
}
