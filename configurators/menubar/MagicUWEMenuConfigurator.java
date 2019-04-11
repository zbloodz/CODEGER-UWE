package magicUWE.configurators.menubar;

import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;

/**
 * MagicUWE main menu configuration. Creates the MagicUWE main menu item.
 * 
 * 
 * @author PST LMU
 */
public class MagicUWEMenuConfigurator implements AMConfigurator {

	private static final Logger logger = Logger.getLogger(MagicDrawMenuConfigurator.class);

	/**
	 * place of the former menu item where to place the MagicUWE menu item
	 */
	private static final int MENU_POSITION = 8;

	/**
	 * Action will be added to manager.
	 */
	private final NMAction action;

	/**
	 * Creates configurator.
	 * 
	 * @param action
	 *            action to be added to main menu.
	 */
	public MagicUWEMenuConfigurator(NMAction action) {
		this.action = action;
	}

	/**
	 * Configurates the main menu actions.
	 * 
	 * @param mngr
	 *            ActionsManager
	 */
	@Override
	public void configure(ActionsManager mngr) {
		// this String is especially for the MagicUWE main menu
		final String UWE_MAIN_MENU = "CODEGER-UWE";
		// searching for UWE action category
		ActionsCategory category = (ActionsCategory) mngr.getActionFor(UWE_MAIN_MENU);
		if (category == null) {
			// creating new category
			category = new MDActionsCategory(UWE_MAIN_MENU, UWE_MAIN_MENU, KeyEvent.VK_U);
			category.setNested(true);
			mngr.addCategory(MENU_POSITION, category);
			logger.debug("UWE main menu entry is created");
		}
		category.addAction(this.action);
	}

	// set priority
	@Override
	public int getPriority() {
		return MEDIUM_PRIORITY;
	}

}
