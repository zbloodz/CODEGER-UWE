package magicUWE.configurators.context;

import magicUWE.shared.UWEDiagramType;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

/**
 * Shows or hides sub-contexmenu items, e.g. for the tags with string values
 * 
 * Only shown on States, Pseudostates, Classes or Properties or Packages!
 * 
 * @author PST LMU
 * 
 */
public class DiagramContextConfiguratorSubmenu extends DiagramContextConfigurator {

	private String addToThisMenu;
	private final boolean displayOnClass;
	private final boolean displayOnProperty;
	private boolean displayOnSubMachineStateNotState;
        private final boolean displayOnAction;
        private final boolean displayOnPin;
        private final boolean displayOnUseCase;
        private final boolean displayOnPackage;

	public DiagramContextConfiguratorSubmenu(ActionsCategory category, UWEDiagramType onlyShowInThisDiagramType,
			String onlyShowOnElementsContainingThisStereotype, String addToThisMenu, boolean displayOnProperty,
			boolean displayOnClass, boolean displayOnSubMachineStateNotState) {
		super(category, onlyShowInThisDiagramType, onlyShowOnElementsContainingThisStereotype);
		this.addToThisMenu = addToThisMenu;
		this.displayOnProperty = displayOnProperty;
		this.displayOnClass = displayOnClass;
		this.displayOnSubMachineStateNotState = displayOnSubMachineStateNotState;
                this.displayOnAction=false;
                this.displayOnPin=false;
                this.displayOnUseCase=false;
                this.displayOnPackage=false;
	}

	public DiagramContextConfiguratorSubmenu(ActionsCategory category, UWEDiagramType onlyShowInThisDiagramType,
			String onlyShowOnElementsContainingThisStereotype, String addToThisMenu, boolean displayOnProperty,
			boolean displayOnClass, boolean displayOnSubMachineStateNotState,
                        boolean displayOnPackage, boolean displayOnUseCase, boolean displayOnPin, boolean displayOnAction) {
		super(category, onlyShowInThisDiagramType, onlyShowOnElementsContainingThisStereotype);
		this.addToThisMenu = addToThisMenu;
		this.displayOnProperty = displayOnProperty;
		this.displayOnClass = displayOnClass;
		this.displayOnSubMachineStateNotState = displayOnSubMachineStateNotState;
                this.displayOnAction=displayOnAction;
                this.displayOnPin=displayOnPin;
                this.displayOnUseCase=displayOnUseCase;
                this.displayOnPackage=displayOnPackage;
	}

	/**
	 * configurator. <br/>
	 * oddity of MagicDraw: many times called when a Class or a Property element
	 * is selected
	 */
	@Override
	public void configure(ActionsManager mngr, DiagramPresentationElement diagram, PresentationElement[] selected,
			PresentationElement requestor) {
		// only over classes and properties
		if (requestor != null
				&& selected.length > 0
				&& ((displayOnClass && requestor.getElement() instanceof Class)
						|| (displayOnProperty && requestor.getElement() instanceof Property)
						|| (displayOnSubMachineStateNotState && requestor.getElement() instanceof State && ((State) requestor
								.getElement()).getSubmachine() != null)
						|| (!displayOnSubMachineStateNotState && (requestor.getElement() instanceof State || requestor
								.getElement() instanceof Pseudostate)) 
						|| (displayOnPackage && requestor.getElement() instanceof Package)
						|| (displayOnUseCase && requestor.getElement() instanceof UseCase)
						|| (displayOnAction && requestor.getElement() instanceof Action)
						|| (displayOnPin && requestor.getElement() instanceof Pin))
				&& isToBeShown(diagram.getDiagram(), requestor)) {

			// add category to the given context menu
			addToGivenMenu(mngr, addToThisMenu);
		} else {
			removeFromGivenMenu(mngr, addToThisMenu);
		}
	}
}