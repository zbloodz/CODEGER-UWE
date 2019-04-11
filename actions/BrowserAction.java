package magicUWE.actions;

import java.awt.event.ActionEvent;

import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;

/**
 * NOT USED AT THE MOMENT (all browser menu = containment tree menu entries are re-used from other
 * menus)
 * 
 * This class is a browser action. It is executed when a user makes right click
 * on the browser tree window and then clicks on the browser action.
 * 
 * @author PST LMU
 */
public class BrowserAction extends DefaultBrowserAction {

	private static final long serialVersionUID = 1L;

	/**
	 * BrowserAction
	 * 
	 * @param name
	 * @param actionsGroup
	 */
	public BrowserAction(String name, String actionsGroup) {
		super(name, name, null, actionsGroup);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		MessageWriter.showMessage("Not implemented yet", null);
	}
}
