package magicUWE.shared;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import magicUWE.stereotypes.UWEStereotypeWithKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.impl.ElementsFactory;

/**
 * UWE Diagram Types with names and a variety of functions
 * 
 * @author PST LMU
 */
public enum UWEDiagramType {

	CONTENT("Content", DiagramTypeConstants.UML_CLASS_DIAGRAM, "contentModel"),
	BASIC_RIGHTS("Basic Rights", DiagramTypeConstants.UML_CLASS_DIAGRAM, "basicRightsModel"),
	NAVIGATION_STATES("Navigation States", DiagramTypeConstants.UML_STATECHART_DIAGRAM, "navigationStatesModel"),
	NAVIGATION("Navigation", DiagramTypeConstants.UML_CLASS_DIAGRAM, "navigationModel"),
	PRESENTATION("Presentation", DiagramTypeConstants.UML_CLASS_DIAGRAM, "presentationModel"),
	PROCESS_STRUCTURE("Process Structure", "Process", DiagramTypeConstants.UML_CLASS_DIAGRAM, "processModel"),
	PROCESS_FLOW("Process Flow", "Process", DiagramTypeConstants.UML_ACTIVITY_DIAGRAM, "processModel"),
	USE_CASE("Use Case", "Requirements", DiagramTypeConstants.UML_USECASE_DIAGRAM, "requirementsModel"),
	USER_MODEL("User Model", "UserModel", DiagramTypeConstants.UML_CLASS_DIAGRAM, "userModel"),
    USE_CASE_FLOW("Use Case Activity", "Requirements", DiagramTypeConstants.UML_ACTIVITY_DIAGRAM, "requirementsModel");
	
	private static final Logger logger = Logger.getLogger(UWEDiagramType.class);

	public final String name;
	public final String umlDiagramType;
	public final String originalModelName;
	public final String modelStereotype;

	private UWEDiagramType(String name, String umlDiagramType, String modelStereotype) {
		this.name = name;
		this.originalModelName = name;
		this.umlDiagramType = umlDiagramType;
		this.modelStereotype = modelStereotype;
	}

	private UWEDiagramType(String name, String originalModelName, String umlDiagramType, String modelStereotype) {
		this.name = name;
		this.originalModelName = originalModelName;
		this.umlDiagramType = umlDiagramType;
		this.modelStereotype = modelStereotype;
	}

	/**
	 * long Name for Diagrams
	 * 
	 * @return toString + " Diagram"
	 */
	public String getNameWithDiagramTail() {
		return this.toString() + " Diagram";
	}

	/**
	 * short Name for UWE Diagram Types
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Create model and add it to
	 * Application.getInstance().getProject().getModel()
	 * 
	 * @return created model or null
	 */
	private Model createModel() {
		Project project = Application.getInstance().getProject();
		Model rootModel = project.getModel();
		ElementsFactory factory = project.getElementsFactory();
		if (rootModel != null && factory != null) {

			SessionManager.getInstance()
					.createSession(
							"Create model " + this.originalModelName + " and add the stereotype '"
									+ this.modelStereotype + "'");
			Model model = factory.createModelInstance();
			model.setName(this.originalModelName);
			model.setOwner(rootModel);
			MagicDrawElementOperations.addStereotypeToElement(model, this.modelStereotype);
			SessionManager.getInstance().closeSession();

			return model;
		}
		MessageWriter.showError("Can't create model \"" + this.originalModelName + "\".", logger);
		return null;
	}

	/**
	 * Creates new Diagram, adds it to the project and opens it. If there exists
	 * a diagram with this name, add a number
	 * 
	 * @param destinationPackage
	 * @param diagramName
	 * @return diagram
	 */
	public synchronized Diagram createAndAddDiagram(Package destinationPackage, String diagramName) {

		// a diagram should have a name
		if (diagramName == null || diagramName.trim().equals("")) {
			diagramName = this.getNameWithDiagramTail();
		}

		// If there would be duplicates, add a number
		diagramName = diagramName + getDiagramNameExtension(destinationPackage, diagramName, "");

		Project project = Application.getInstance().getProject();
		// Create new diagram and open it
		SessionManager.getInstance().createSession("Create and open " + diagramName);
		Diagram diag = null;
		try {
			// diagram is created and added to specified package
			diag = ModelElementsManager.getInstance().createDiagram(this.umlDiagramType, destinationPackage);

			// open diagram (automatically makes it to the active one)
			project.getDiagram(diag).open();
		} catch (ReadOnlyElementException e) {
			logger.debug("Error: destination Package was read only");
			e.printStackTrace();
		} finally {
			// closing the session
			SessionManager.getInstance().closeSession();
		}

		// set the name at the first time does not work with ProcessFlow diagram
		SessionManager.getInstance().createSession("Set name to: " + diagramName);
		if (diag != null) {
			diag.setName(diagramName);
			logger.debug("project name: " + project.getName() + " :: " + diag.getHumanName());
			// this "if" is because of the undo-case when MagicDraw has detected
			// a model inconsistency
			if (diag.getOwner() != null) {
				MessageWriter.log("UWE Diagram " + diag.getHumanName() + " was created, stored in "
						+ diag.getOwner().getHumanName() + " (" + diag.getName() + ") and opened.", logger);
			}
		}
		SessionManager.getInstance().closeSession();
		return diag;
	}

	/**
	 * if there exists a diagram with this name, add a number. This function
	 * returns the number (recursive)
	 * 
	 * @param destinationPackage
	 * @param diagramName
	 * @param numberAddedToDgName
	 *            Should be "" first
	 * @return String of the Number which should be added to the diagram name
	 */
	private String getDiagramNameExtension(Package destinationPackage, String diagramName, String numberAddedToDgName) {
		for (Element act : destinationPackage.getOwnedElement()) {
			if (act instanceof Activity && ((Activity) act).getName().equals(diagramName + numberAddedToDgName)) {
				// recursion
				numberAddedToDgName =
						getDiagramNameExtension(destinationPackage, diagramName,
								increaseStringNumber(numberAddedToDgName));
				return numberAddedToDgName;
			}
		}
		for (Diagram ownedDg : destinationPackage.getOwnedDiagram()) {
			if (ownedDg.getName().equals(diagramName + numberAddedToDgName)) {
				// recursion (terminates if none of the diagrams in this package
				// has an equal name)
				numberAddedToDgName =
						getDiagramNameExtension(destinationPackage, diagramName,
								increaseStringNumber(numberAddedToDgName));
				break;
			}
		}
		return numberAddedToDgName;
	}

	/**
	 * increaseStringNumber for getDiagramNameExtension
	 * 
	 * @param numberAddedToDgName
	 * @return Number
	 */
	private String increaseStringNumber(String numberAddedToDgName) {
		if (numberAddedToDgName.equals("")) {
			numberAddedToDgName = "1";
		} else {
			numberAddedToDgName = String.valueOf(Long.valueOf(numberAddedToDgName) + 1);
		}
		return numberAddedToDgName;
	}

	/**
	 * Call {@link UWEDiagramType#createAndAddDiagram(Package, String)} with
	 * String {@link UWEDiagramType#getNameWithDiagramTail()}
	 * 
	 * @param destinationPackage
	 * @return Diagram
	 */
	public Diagram createAndAddDiagram(Package destinationPackage) {
		return this.createAndAddDiagram(destinationPackage, this.getNameWithDiagramTail());
	}

	/**
	 * get the proper UWE Model for this dgType and create it if it doesn't
	 * exist.
	 * 
	 * @param project
	 * @param question
	 *            true => ask if new diagram should be stored within an
	 *            appropriate new model.
	 * @param eventuallyShowTreeAndAllowPackages
	 *            show package selector GUI, if there is no appropriate package
	 * @return Model
	 */
	public synchronized Package getModelOrCreateIt(Project project, String question,
			boolean eventuallyShowTreeAndAllowPackages) {
		Package model = null;
		// Look if there is an equal named model, if not, create one
		Collection<NamedElement> existingModels = project.getModel().getOwnedMember();
		for (NamedElement existingModel : existingModels) {
			if (existingModel instanceof Model && existingModel.isEditable()
					&& MagicDrawElementOperations.hasStereotype(existingModel, this.modelStereotype)) {
				logger.debug(existingModel.getName() + " model exists.");
				model = (Model) existingModel;
				break;
			}
		}
		if (model == null) {
			// No model with equal name exists => create it!
			// or ask if it should be created
			if (question == null) {
				model = this.createModel();
			} else if (MessageWriter.showQuestion(question, logger)) {
				model = createModel();
			} else {
				if (eventuallyShowTreeAndAllowPackages) {
					// select the target package with a GUI
					model = (new GUIPackageSelector()).targetPackage;
				} else {
					model = project.getModel();
				}
			}
		}
		return model;
	}

	public synchronized Model getModelOrCreateIt(Project project, String question) {
		return (Model) getModelOrCreateIt(project, question, false);
	}

	public synchronized Model getModelOrCreateIt(Project project) {
		return (Model) getModelOrCreateIt(project, null, false);
	}

	/**
	 * get UWEDiagramType of given Diagram (depends on parental model)
	 * 
	 * @param sourceDiagram
	 * @return UWEDiagramType
	 */
	public static UWEDiagramType getDiagramType(Diagram sourceDiagram) {
		Model sourceModel = getContainerOfElement(sourceDiagram);
		if (sourceModel != null) {
			if (MagicDrawElementOperations.hasStereotype(sourceModel, UWEDiagramType.CONTENT.modelStereotype)) {
				return UWEDiagramType.CONTENT;
			} else if (MagicDrawElementOperations.hasStereotype(sourceModel, UWEDiagramType.NAVIGATION.modelStereotype)) {
				return UWEDiagramType.NAVIGATION;
			} else if (MagicDrawElementOperations.hasStereotype(sourceModel,
					UWEDiagramType.PRESENTATION.modelStereotype)) {
				return UWEDiagramType.PRESENTATION;
			} else if (MagicDrawElementOperations.hasStereotype(sourceModel,
					UWEDiagramType.NAVIGATION_STATES.modelStereotype)) {
				return UWEDiagramType.NAVIGATION_STATES;
			} else if (MagicDrawElementOperations.hasStereotype(sourceModel,
					UWEDiagramType.BASIC_RIGHTS.modelStereotype)) {
				return UWEDiagramType.BASIC_RIGHTS;
			} else if (MagicDrawElementOperations.hasStereotype(sourceModel,
					UWEDiagramType.USER_MODEL.modelStereotype)) {
				return UWEDiagramType.USER_MODEL;
			}
		}

		// Get the MagicDraw diagram type
		String dgType = Application.getInstance().getProjectsManager()
				.getActiveProject().getDiagram(sourceDiagram).getDiagramType()
				.getType();
		if (dgType.equals(UWEDiagramType.PROCESS_STRUCTURE.umlDiagramType)
				&& MagicDrawElementOperations.hasStereotype(sourceModel,
						UWEDiagramType.PROCESS_STRUCTURE.modelStereotype)) {
			return UWEDiagramType.PROCESS_STRUCTURE;
		} else if (dgType.equals(PROCESS_FLOW.umlDiagramType)
				&& MagicDrawElementOperations.hasStereotype(sourceModel,
						UWEDiagramType.PROCESS_FLOW.modelStereotype)) {
			return UWEDiagramType.PROCESS_FLOW;
		} else if (dgType.equals(USE_CASE.umlDiagramType)){
			return UWEDiagramType.USE_CASE;
		} else if (dgType.equals(USE_CASE_FLOW.umlDiagramType)){
			return UWEDiagramType.USE_CASE_FLOW;
		}
		return null;
	}

	/**
	 * @param model
	 *            Model
	 * @return true <=> stereotype of the model indicates that it may contain
	 *         this diagramType
	 */
	public boolean isRightModelForThisDiagramType(Model model) {
		if (model != null && MagicDrawElementOperations.hasStereotype(model, this.modelStereotype)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the model containing the given element (or the element if it is a
	 * model itself)
	 * 
	 * @param sourceElement
	 * @param alsoUsePackages
	 * @return model
	 */
	public static Package getContainerOfElement(Element sourceElement, boolean alsoUsePackages) {
		Package model;
		if (sourceElement instanceof Model) {
			model = ((Model) sourceElement);
		} else if (alsoUsePackages && sourceElement instanceof Package) {
			model = ((Package) sourceElement);
		} else {
			if (sourceElement != null) {
				model = getContainerOfElement(sourceElement.getOwner(), alsoUsePackages); // recursive
			} else {
				model = null;
			}
		}
		return model;
	}

	public static Model getContainerOfElement(Element sourceElement) {
		return (Model) getContainerOfElement(sourceElement, false);
	}

	/**
	 * @param stereotype
	 * @return true <=> this diagramType may contain the given stereotype
	 */
	public boolean mayContainStereotype(UWEStereotypeWithKey stereotype) {
		for (UWEDiagramType dgType : stereotype.getAssociatedDiagramTypes()) {
			if (this == dgType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param stereotype
	 * @return Message in which the right diagram types for the given stereotype
	 *         are listed
	 */
	public static String getMessageAboutRightDiagramTypesForAStereotype(UWEStereotypeWithKey stereotype) {
		String msg = "";
		final String seperator = " / ";
		// get all associated diagram types of the given stereotype
		for (UWEDiagramType dgType : stereotype.getAssociatedDiagramTypes()) {
			msg += dgType.getNameWithDiagramTail() + seperator;
		}
		if (msg.length() > 0) {
			return msg.substring(0, msg.length() - seperator.length());
		}
		return msg;
	}
}

/**
 * Package selector.
 * 
 * 
 * @author PST LMU
 */
class GUIPackageSelector implements TreeSelectionListener, ActionListener {

	private static final Logger logger = Logger.getLogger(GUIPackageSelector.class);

	// Return value
	public Package targetPackage;

	// GUI
	private static boolean useSystemLookAndFeel = false;
	private static final String selectButtonName = "Select";
	private static final String cancleButtonName = "Cancel";
	private JDialog dialog;
	private JPanel newContentPane;
	private JTree tree;
	private JButton chooseButton;
	private JButton cancleButton;

	/**
	 * GUIPackageSelector
	 */
	public GUIPackageSelector() {
		storeElementsSelector(); // modal
	}

	/**
	 * Executed when a Button of the TreeSelector is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (selectButtonName.equals(event.getActionCommand())) {
			// package chooser dialog: Select-button
			if (targetPackage != null && targetPackage.isEditable()) {
				dialog.dispose();
			} else {
				MessageWriter.showError("You have to choose a normal package!", logger);
				dialog.toFront();
			}
		} else if (cancleButtonName.equals(event.getActionCommand())) {
			// close the package chooser dialog
			dialog.dispose();
		} else {
			MessageWriter.showError("Unknown operation", logger);
		}
	}

	/**
	 * Creates a modal package dialog to select where to save the newly created
	 * elements.
	 */
	private void storeElementsSelector() {
		if (useSystemLookAndFeel) { // set to false, otherwise some problems
			// with other java applications running parallel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Couldn't use system look and feel.");
			}
		}
		dialog = new JDialog();
		dialog.setModal(true);
		dialog.setTitle("Please select a package...");
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// Create and set up the content pane.
		newContentPane = new JPanel(new GridLayout(1, 0));
		newContentPane.setOpaque(true); // content panes must be opaque
		dialog.setContentPane(newContentPane);

		// set the browser tree
		setUpTheTreeSelector();

		// set the position of the dialog
		dialog.setBounds(0, 0, 300, 300);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - dialog.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - dialog.getHeight()) / 2);
		dialog.setLocation(x, y);

		// Display the window.
		dialog.pack();
		dialog.setVisible(true);
		try {
			// need to wait a moment to show the dialog on the top!
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// wake up
		}
		dialog.toFront();
	}

	/**
	 * Sets the tree and all nodes components retrieved from the MD main dialog
	 * tree browser.
	 */
	private void setUpTheTreeSelector() {
		// get the model browser
		Browser browser = Application.getInstance().getMainFrame().getBrowser();
		// get the active browser tree
		Tree activeTree = browser.getActiveTree();
		logger.debug("activeTree: " + activeTree);

		// Create a tree that allows one selection at a time.
		DefaultMutableTreeNode top = activeTree.getRootNode();

		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		logger.debug("tree:  " + tree);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create the button view
		chooseButton = new JButton(selectButtonName);
		dialog.getRootPane().setDefaultButton(chooseButton);
		cancleButton = new JButton(cancleButtonName);
		// add the listener
		chooseButton.addActionListener(this);
		chooseButton.setActionCommand(selectButtonName);
		cancleButton.addActionListener(this);
		cancleButton.setActionCommand(cancleButtonName);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		JLabel jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(66, 39, 179, 38));
		jLabel.setText("Action");
		JPanel panel = new JPanel();
		panel.add(chooseButton);
		panel.add(cancleButton);

		splitPane.setBottomComponent(panel);

		Dimension minimumSize = new Dimension(100, 50);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(250);
		splitPane.setPreferredSize(new Dimension(350, 300));

		// Add the split pane to this panel.
		newContentPane.add(splitPane);
		newContentPane.setMinimumSize(minimumSize);
		dialog.toFront();
	}

	/**
	 * Action fired on every new selection of a node in the tree. Sets
	 * {@link #targetPackage}
	 * 
	 * @param event
	 *            TreeSelectionEvent
	 */
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node != null) {
			// node is selected
			Object nodeType = node.getUserObject();
			logger.debug("node: " + node + " nodeType: " + nodeType);
			// only if package
			if (nodeType instanceof Package) {
				targetPackage = (Package) nodeType;
				logger.debug("targetPackage was set: " + targetPackage);
			} else {
				// reset the node
				targetPackage = null;
			}
		}
	}
}
