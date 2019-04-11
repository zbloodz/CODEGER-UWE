package magicUWE.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import magicUWE.actions.context.sessionTransmissionCheck.RuleViolationDiagramPainter;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;

/**
 * The class is the main event change listener of MagicUWE.
 * 
 * @author PST LMU
 */
public class ProjectEventChangeListener extends ProjectEventListenerAdapter implements PropertyChangeListener {

	// private static final Logger logger =
	// Logger.getLogger(ProjectEventChangeListener.class);

	/**
	 * adds the RuleViolationDiagramPainter
	 * @param project
	 */
	@Override
	public void projectOpened(Project project) {
		// add listener for getting events about opened diagram
		project.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Project.DIAGRAM_OPENED)) {
					// register custom painter for
					// painting colored rectangles around
					// some shapes in the diagram
					Application.getInstance().getProject().getActiveDiagram().getDiagramSurface()
							.addPainter(new RuleViolationDiagramPainter());
				}
			}
		});
	}

	/**
	 * NOTHING DONE HERE This method listens on every property (action) changed
	 * at the main GUI window of MagicDraw.
	 * 
	 * @param event
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// logger.debug("propertyChange: " + event.getPropertyName());
		// Project curProj = Application.getInstance().getProject();
		//
		// // ##### DIAGRAM_OPENED #####
		// if (event.getPropertyName().equals(Project.DIAGRAM_OPENED)) {
		// MessageWriter.log(">> " + curProj.getActiveDiagram().getName() +
		// " was opend.", logger);
		// }
		//
		// // ##### SELECTION_CHANGED #####
		// if (event.getPropertyName().equals(Project.SELECTION_CHANGED)) {
		//
		// }
		//
		// // #### COMMAND_EXECUTED #####
		// if (event.getPropertyName().equals(Project.COMMAND_EXECUTED)) {
		//
		// }
	}
}
