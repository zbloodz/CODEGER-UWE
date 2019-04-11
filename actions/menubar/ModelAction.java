package magicUWE.actions.menubar;

import java.awt.event.ActionEvent;

import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;

/**
 * This class listens to the Model-Button in MagicUWE Main menu
 * 
 * @author PST LMU
 */
public class ModelAction extends MDAction {

	private static final Logger logger = Logger.getLogger(ModelAction.class);

	private static final long serialVersionUID = 1L;

	/**
	 * ModelAction
	 * 
	 * @param name
	 * @param mnemonic
	 * @param group
	 */
	public ModelAction(String name, int mnemonic, String group) {
		super(name, name, mnemonic, group);
	}

	/**
	 * Loading and adding default models into the root of the opened
	 * project. If a name of a model already exists, ignore that specific
	 * model.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Project project = Application.getInstance().getProjectsManager().getActiveProject();
		if (project != null) { // should not happen
			logger.debug("creating default models for UWE project: " + project);

			for (UWEDiagramType dgType : UWEDiagramType.values()) {
				dgType.getModelOrCreateIt(project);
			}
			MessageWriter.log("Typical UWE models were created successfully.", logger);
		} else {
			MessageWriter.log("Can't create UWE default models, no open project!", logger);
		}
	}
}
