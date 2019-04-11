package magicUWE.configurators.menubar;

import java.util.List;

import org.apache.log4j.Logger;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsID;

/**
 * Configurator class to add an item to a MagicDraw Menu.
 * 
 * 
 * @author PST LMU
 */
public class MagicDrawMenuConfigurator implements AMConfigurator {

	private static final Logger logger = Logger.getLogger(MagicDrawMenuConfigurator.class);

	/**
	 * Action will be added to manager.
	 */
	private NMAction action;

	private final String actionsIDMenu;

	/**
	 * Creates MagicDrawMenuConfigurator
	 * 
	 * @param action
	 *            action to be added to menu.
	 * @param actionsIDMenu
	 *            MagicDraw's Menu
	 */
	public MagicDrawMenuConfigurator(NMAction action, String actionsIDMenu) {
		this.action = action;
		this.actionsIDMenu = actionsIDMenu;
	}

	/**
	 * Configures the actions.
	 * 
	 * @param mngr
	 *            ActionsManager
	 */
	@Override
	public synchronized void configure(ActionsManager mngr) {
		// searching for action category
		NMAction category = mngr.getActionFor(actionsIDMenu);
		if (category != null && category instanceof ActionsCategory) {
			// insert in proper place
			List<NMAction> actionsInCategory = category.getActions();
			int indexOfFound = actionsInCategory.indexOf(ActionsID.CUSTOM_DIAGRAMS_CATEGORY);
			actionsInCategory.add(indexOfFound + 2, this.action);
			category.setActions(actionsInCategory);
			// only add Menu anywhere: ((ActionsCategory) category).addAction(action);
		} else {
			logger.debug("Something went wrong with the menu, can't find MagicDraw's \"" + actionsIDMenu + "\" menu");
			return;
		}
	}

	// set priority
	@Override
	public int getPriority() {
		return MEDIUM_PRIORITY;
	}
}
