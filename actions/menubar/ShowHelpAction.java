package magicUWE.actions.menubar;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;


import com.nomagic.magicdraw.actions.MDAction;

/**
 * Shows the UWE Web-page
 * 
 * @author PST LMU
 */
public class ShowHelpAction extends MDAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ShowHelpAction.class);

	/**
	 * ShowHelpAction
	 * @param name
	 * @param mnemonic
	 * @param group
	 */
	public ShowHelpAction(String name, int mnemonic, String group) {
		super(name, name, mnemonic, group);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String url = "http://uwe.pst.ifi.lmu.de/toolMagicUWE.html";
		if (MessageWriter.showQuestion("Do you want to go to the MagicUWE-Website?\n" + url, logger)) {
			try {
				Desktop.getDesktop().browse(new URL(url).toURI());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (UnsupportedOperationException ex) {
				MessageWriter.showError("Your system doesn't support the automatic opening of a browser.\n \n"
						+ "Please use the following URL:\n" + url, logger);
			}
		}
	}
}
