package magicUWE.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import magicUWE.shared.MagicDrawAndUWEOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.transformation.TransformationType;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * This class starts the several transformations.
 * 
 * 
 * @author PST LMU
 */
public class TransformatorAction extends MDAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TransformatorAction.class);

	private final TransformationType transformationType;
	private final boolean openSelectedDiagramFromContainmentTree;

	/**
	 * The constructor.
	 * 
	 * @param transformationType
	 * @param actionGroup
	 * @param namePrefix
	 * @param openSelectedDiagramFromContainmentTree
	 */
	public TransformatorAction(TransformationType transformationType, String actionGroup, String namePrefix,
			boolean openSelectedDiagramFromContainmentTree) {
		super(namePrefix + transformationType.toString(), namePrefix + transformationType.toString(), null, actionGroup);
		this.transformationType = transformationType;
		this.openSelectedDiagramFromContainmentTree = openSelectedDiagramFromContainmentTree;
	}

	/**
	 * Executed when a Transformation-menu-item is activated. Can be called from
	 * Browser-Context-menu = containmentTree or from the main menu
	 * 
	 * @param event
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("transformationType from diagram type: " + transformationType.toString());
		Project project = Application.getInstance().getProject();

		// maybe we must open the diagram first (selected in the containment
		// tree)
		if (openSelectedDiagramFromContainmentTree) {
			List<Diagram> selectedDiagrams =
					MagicDrawAndUWEOperations.getAllDiagramsInTreeSelectionOfTheGivenType(Application.getInstance()
							.getMainFrame().getBrowser().getContainmentTree(), transformationType
							.getDiagramSourceTypeForTransformation());

			if (selectedDiagrams.size() == 0) {
				MessageWriter.showError("Sorry, can't find a diagram in the given selection to convert", logger);
				return;
			}

			// every suitable selected diagram will be transformed
			for (Diagram diagram : selectedDiagrams) {
				project.getDiagram(diagram).open();
				maybeStartTransformation(event, project);
			}
		} else {
			// called from menu
			maybeStartTransformation(event, project);
		}
	}

	/**
	 * maybe choose model and start transformation, if everything is all right
	 * 
	 * @param event
	 * @param project
	 */
	private void maybeStartTransformation(ActionEvent event, Project project) {
		DiagramPresentationElement sourceDiagram = project.getActiveDiagram();

		if (sourceDiagram == null) {
			MessageWriter.showError("No" + event.getActionCommand() + " Transformation, because no Diagram is active",
					logger);
		} else {

			// Transformation-menu
			UWEDiagramType sourceDiagramType = UWEDiagramType.getDiagramType(sourceDiagram.getDiagram());

			// It's an UWE diagram.. is it the right one
			if ((sourceDiagramType != null && transformationType
					.isRightSourceDiagramTypeForTransformation(sourceDiagramType))
					|| !MessageWriter.showQuestion(
							"The active diagram doesn't have the source type of the selected transformation (\""
									+ transformationType.toString()
									+ "\")!\n \nDo you want to attend this order and don't run the transformation?",
							logger)) {
				// .. open the package browser dialog (modal)
				UWEDiagramType dgType = transformationType.getDestinationDiagramTypeForTransformation();
				if(dgType == null){
					transformationType.launchTransformation(sourceDiagram, null);
				}
				else{
					Package targetPackage =
							dgType.getModelOrCreateIt(project, "Do you want to store the result\n"
									+ "from the transformation of the active diagram\n" + "in a "
									+ dgType.originalModelName + " model?\n \n(\"No\" will ask you to select a package)",
									true);
					if (targetPackage != null) {
						transformationType.launchTransformation(sourceDiagram, targetPackage);
						// MessageWriter.log("Transformation '" +
						// transformationType.toString() + "' into the container "
						// + targetPackage.getName() + " completed.", logger);
					} else {
						MessageWriter.log("Transformation aborted manually.", logger);
					}
				}
				
			}
		}
	}
}
