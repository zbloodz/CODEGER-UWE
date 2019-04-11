package magicUWE.configurators.context;

import magicUWE.shared.UWEDiagramType;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.AssociationView;

/**
 * A context menu which is only shown over selected associations
 * 
 * @author PST LMU
 */
public class DiagramContextConfiguratorForAssociations extends DiagramContextConfigurator {

	private String addToThisMenu;

	public DiagramContextConfiguratorForAssociations(ActionsCategory category,
			UWEDiagramType onlyShowInThisDiagramType, String onlyShowOnElementsContainingThisStereotype,
			String addToThisMenu) {
		super(category, onlyShowInThisDiagramType, onlyShowOnElementsContainingThisStereotype);
		this.addToThisMenu = addToThisMenu;
	}

	/**
	 * configurator. <br/>oddity of MagicDraw: many times called when a Class
	 * element is selected
	 */
	@Override
	public void configure(ActionsManager mngr, DiagramPresentationElement diagram, PresentationElement[] selected,
			PresentationElement requestor) {
		// only show menu on associations
		if (selected.length > 0 && selected[0] instanceof AssociationView && isToBeShown(diagram.getDiagram(), selected[0])) {
			addToGivenMenu(mngr, addToThisMenu);
		} else {
			removeFromGivenMenu(mngr, addToThisMenu);
		}
	}
}
