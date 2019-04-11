package magicUWE.actions.context.sessionTransmissionCheck;

import java.awt.Component;

import magicUWE.core.PluginManager;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.ProjectWindow;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.WindowsManager;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;

/**
 * MagicDraw Tab
 */
public class Tab implements WindowComponentContent {
	private final ViolationsGUI gui;

	public Tab(ViolationsGUI gui) {
		ProjectWindowsManager windowsManager = Application.getInstance().getMainFrame().getProjectWindowsManager();

		// MAYBE refactor if needed again
		WindowComponentInfo info = new WindowComponentInfo("UWE {transmissionType} check",
				"UWE {transmissionType} check", PluginManager.getIcon("icons/CheckSessionTransmissionType.png"),
				WindowsManager.SIDE_SOUTH, WindowsManager.STATE_DOCKED, true);
		this.gui = gui;
		windowsManager.addWindow(new ProjectWindow(info, this));
	}

	@Override
	public Component getWindowComponent() {
		return gui;
	}

	@Override
	public Component getDefaultFocusComponent() {
		return gui;
	}
}
