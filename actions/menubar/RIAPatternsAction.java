package magicUWE.actions.menubar;

import java.awt.event.ActionEvent;

import magicUWE.riaPatterns.RIAPreferencesDialog;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.actions.MDAction;

/**
 * Class for the MagicUWE menu entry "RIA Patterns" for the RIA Patterns Options Dialog
 * 
 * 
 * 
 */
public class RIAPatternsAction extends MDAction {

	private static final Logger logger = Logger.getLogger(RIAPatternsAction.class);

	private static final long serialVersionUID = 1L;

	public RIAPatternsAction(String name, int mnemonic, String group) {
		super(name, name, mnemonic, group);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("Opening RIAPreferencesDialog");
		new RIAPreferencesDialog();

	}

}
