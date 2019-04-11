package magicUWE.actions.menubar;

import java.awt.event.ActionEvent;

import magicUWE.shared.MessageWriter;


import com.nomagic.magicdraw.actions.MDAction;

/**
 * This class stores the current version number of MagicUWE and shows it when
 * executed.
 * 
 * 
 * @author PST LMU
 */
public class AboutUweAction extends MDAction {

	private static final long serialVersionUID = 1L;

	private static final String uweVersion = "Version 1.5.0";

	/**
	 * AboutUweAction
	 * @param name
	 * @param mnemonic
	 * @param group
	 */
	public AboutUweAction(String name, int mnemonic, String group) {
		super(name, name, mnemonic, group);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		//showing information
		MessageWriter.showMessage(
        		"<html>" +
        		"<p style='font-size:14;' align='center'>" +  " UWE Plugin MagicUWE (" + uweVersion + ")"  + "</p>" +
        		"<p style='font-size:5;'> </p>" +
        		"<p style='font-size:11;'>LMU Munich, Germany - Institute for Informatics - PST – Web Engineering Group</p>" +
        		"<p style='font-size:10;'><a href=\"http://uwe.pst.ifi.lmu.de/\">http://uwe.pst.ifi.lmu.de/</a></p>" +
        		"<p style='font-size:5;'> </p>" +
        		"<p style='font-size:11;'>Prof. Dr. Alexander Knapp, Dr. Nora Koch, Dr. Marianne Busch</p>" +
        		"<p style='font-size:11;'>Sergej Kozuruba, Sonja Harrer, Petar Blagoev</p>" +
        		"<p style='font-size:10;'>uwe@pst.ifi.lmu.de</p>" +
        		" </html>", null);
	}
}
