package magicUWE.configurators;

import java.util.Collection;
import java.util.Iterator;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.DiagramInnerToolbarConfiguration;
import com.nomagic.magicdraw.ui.actions.BaseDiagramToolbarConfigurator;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramStateAction;

/**
 * This class configures the diagram toolbar actions.
 * 
 * 
 * @author PST LMU
 */
public class ToolbarConfigurator extends BaseDiagramToolbarConfigurator {

	//private static final Logger logger = Logger.getLogger(ToolbarConfigurator.class);

	private Collection<NMAction> actions;

	// this String is especially for the MagicUWE toolbar
	private final String UWE_TOOLBAR;

	/**
	 * Creates configurator which adds given action.
	 * 
	 * @param actions
	 *            action to be added to manager.
	 * @param categoryPartOfName
	 *            String for the tool-bar category
	 */
	public ToolbarConfigurator(Collection<NMAction> actions, String categoryPartOfName) {
		this.actions = actions;
		this.UWE_TOOLBAR = "MagicUWE - " + categoryPartOfName;
	}

	/**
	 * Configuring the class toolbar
	 * 
	 * @param mngr
	 *            ActionsManager
	 */
	@Override
	public void configure(ActionsManager mngr) {
		// check if there is already this UWE toolbar menu existing - if not create it
		DiagramInnerToolbarConfiguration category = (DiagramInnerToolbarConfiguration) mngr.getCategory(UWE_TOOLBAR);
		if (category == null) {
			category = new DiagramInnerToolbarConfiguration(UWE_TOOLBAR, null, UWE_TOOLBAR, true);
			mngr.addCategory(category);
		}

		ActionsCategory actionsCategory = new ActionsCategory("", "");
		category.addAction(actionsCategory, 0);

		// add the actions
		Iterator<NMAction> it = this.actions.iterator();
		while (it.hasNext()) {
			DefaultDiagramStateAction action = (DefaultDiagramStateAction) it.next();
			actionsCategory.addAction(action);
			// logger.debug("action added: " + action);
		}
	}

	@Override
	public int getPriority() {
		return MEDIUM_PRIORITY;
	}
}
