package magicUWE.actions;

import java.awt.event.ActionEvent;

import magicUWE.shared.MagicDrawAndUWEOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * Used to create UWE diagrams from the main menu and for the context-menu in
 * the containment tree.
 * 
 * 
 * @author PST LMU
 */
public class NewDiagramMenuAction extends MDAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(NewDiagramMenuAction.class);
	private final Boolean isBrowserContextMenu;

	/**
	 * NewDiagramMenuAction
	 * 
	 * @param name
	 * @param actionsGroup
	 * @param isBrowserContextMenu
	 */
	public NewDiagramMenuAction(String name, String actionsGroup, boolean isBrowserContextMenu) {
		super(name, name, null, actionsGroup);
		this.isBrowserContextMenu = isBrowserContextMenu;
	}

	/**
	 * This method is called from MDAction when an action is performed. Also the
	 * UWE diagrams are created in this method.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		Project project = Application.getInstance().getProjectsManager().getActiveProject();
		// set actions on depends what was clicked
		if (project == null) {
			MessageWriter.showError("You have to open or create a new project first!", logger);
		} else {
			// create the wanted diagram
			if (getName().contains(UWEDiagramType.CONTENT.toString())) {
				createDiagram(UWEDiagramType.CONTENT, project);
			} else if (getName().contains(UWEDiagramType.BASIC_RIGHTS.toString())) {
				createDiagram(UWEDiagramType.BASIC_RIGHTS, project);
			} else if (getName().contains(UWEDiagramType.NAVIGATION_STATES.toString())) {
				createDiagram(UWEDiagramType.NAVIGATION_STATES, project);
			} else if (getName().contains(UWEDiagramType.NAVIGATION.toString())) {
				createDiagram(UWEDiagramType.NAVIGATION, project);
			} else if (getName().contains(UWEDiagramType.PRESENTATION.toString())) {
				createDiagram(UWEDiagramType.PRESENTATION, project);
			} else if (getName().contains(UWEDiagramType.PROCESS_STRUCTURE.toString())) {
				createDiagram(UWEDiagramType.PROCESS_STRUCTURE, project);
			} else if (getName().contains(UWEDiagramType.PROCESS_FLOW.toString())) {
				createDiagram(UWEDiagramType.PROCESS_FLOW, project);
			} else if (getName().contains(UWEDiagramType.USE_CASE_FLOW.toString())) {
				createDiagram(UWEDiagramType.USE_CASE_FLOW, project);
			} else if (getName().contains(UWEDiagramType.USE_CASE.toString())) {
				createDiagram(UWEDiagramType.USE_CASE, project);
			} else if (getName().contains(UWEDiagramType.USER_MODEL.toString())) {
				createDiagram(UWEDiagramType.USER_MODEL, project);
			}
		}
	}

	/**
	 * Create the wanted Diagram. If called from ContainmentTree, look at the
	 * selected element and eventually give an advice to the user, that this is
	 * not the right location for that diagram type.
	 * 
	 * @param dgType
	 * @param project
	 */
	private void createDiagram(UWEDiagramType dgType, Project project) {
		final String question = "Do you want the new diagram to be stored in a new model called \""
				+ dgType.originalModelName + "\"\nincluding the stereotype \"" + dgType.modelStereotype
				+ "\"? (recommended)" + "\n(\"No\" will ask you to select a package)";
		if (!isBrowserContextMenu) {
			// create diagram from MagicUWE Menu
			Package pack = dgType.getModelOrCreateIt(project, question, true);
			if (pack != null) {
				dgType.createAndAddDiagram(pack);
			} else {
				MessageWriter.log("Diagram creation aborted manually.", logger);
			}
		} else {
			// look for the element, selected in the containment tree
			Element selectedEl = MagicDrawAndUWEOperations.getSelectedElementIfNotReadOnly(Application.getInstance()
					.getMainFrame().getBrowser().getContainmentTree());
			// is it the right place for the wanted diagram of type dgType
			Model model = UWEDiagramType.getContainerOfElement(selectedEl);
			if (model != null) {
				Package pack = UWEDiagramType.getContainerOfElement(selectedEl, true);
				if (dgType.isRightModelForThisDiagramType(model)) {
					// use parental container package
					dgType.createAndAddDiagram(pack);
				} else {
					if (MessageWriter.showQuestion("'" + model.getName()
							+ "' has not the right UWE model stereotype for this diagram type!\n "
							+ "Do you rather want to create the diagram in a model called \""
							+ dgType.originalModelName + "\"\nincluding the stereotype \"" + dgType.modelStereotype
							+ "\"?\n", logger)) {
						model = dgType.getModelOrCreateIt(project);
						dgType.createAndAddDiagram(model);
					} else {
						// use parental container package
						dgType.createAndAddDiagram(pack);
					}
				}
			} else {
				// don't care about the selected model any longer
				model = dgType.getModelOrCreateIt(project, question);
				MessageWriter.showMessage("The diagram will be stored in the model " + model.getName()
						+ " \n(it has the stereotype \"" + dgType.modelStereotype + "\")", logger);
				dgType.createAndAddDiagram(model);
			}
		}
	}

	@Override
	protected void setGroup(String arg0) {
		super.setGroup(arg0);
	}
}
