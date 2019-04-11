package magicUWE.configurators.context;

import magicUWE.shared.MagicDrawAndUWEOperations;
import magicUWE.shared.UWEDiagramType;

import org.apache.log4j.Logger;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.ConfiguratorWithPriority;
import com.nomagic.magicdraw.ui.browser.Tree;

/**
 * Containment Tree configurator (package browser context menu, e.g. for the
 * newDiagram Menu and for the transformation entries)
 * 
 * @author PST LMU
 */
public class BrowserContextConfigurator implements BrowserContextAMConfigurator {
	private static final Logger logger = Logger.getLogger(BrowserContextConfigurator.class);
	private NMAction action;
	private String actionsID;
	private UWEDiagramType onlyShowOverThisDiagramType;

	/**
	 * BrowserContextConfigurator
	 * 
	 * @param action
	 * @param actionsID
	 *            Show given action (menu) within another
	 * @param onlyShowOverThisDiagramType
	 */
	public BrowserContextConfigurator(NMAction action, String actionsID, UWEDiagramType onlyShowOverThisDiagramType) {
		this.action = action;
		this.actionsID = actionsID;
		this.onlyShowOverThisDiagramType = onlyShowOverThisDiagramType;
	}

	@Override
	public void configure(ActionsManager mngr, Tree tree) {
		// don't show Menu, if the selected element is read only or not an
		// element
		if (canBeUsed(tree)) {
			if (actionsID == null) {
				mngr.addCategory((ActionsCategory) action);
			} else {
				// Show menu within another (see actionsID)
				NMAction magicDrawAction = mngr.getActionFor(actionsID);
				if (magicDrawAction instanceof ActionsCategory) {
					((ActionsCategory) magicDrawAction).addAction(action);
				} else {
					logger.debug("Something went wrong with the menu, can't find MagicDraw's \"" + actionsID
							+ "\" menu");
					return;
				}
			}
		}
	}

	/**
	 * Action can be used only if there are selected model element in the
	 * browser.
	 * 
	 * @param tree
	 * @return true <=>
	 *         {@link MagicDrawAndUWEOperations#getSelectedElementIfNotReadOnly(Tree)}
	 *         != null or if onlyShowOverThisDiagramType is set, than look for a
	 *         usable diagram
	 */
	private boolean canBeUsed(Tree tree) {
		boolean usableElements = false;
		if (onlyShowOverThisDiagramType == null) {
			usableElements = MagicDrawAndUWEOperations.getSelectedElementIfNotReadOnly(tree) != null;
		} else {
			// search for the wanted diagram type (if nothing appropriate found,
			// return false)
			usableElements =
					MagicDrawAndUWEOperations.getAllDiagramsInTreeSelectionOfTheGivenType(tree,
							onlyShowOverThisDiagramType).size() > 0;
		}
		return usableElements;
	}

	@Override
	public int getPriority() {
		return ConfiguratorWithPriority.MEDIUM_PRIORITY;
	}
}
