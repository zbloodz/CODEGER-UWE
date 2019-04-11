package magicUWE.shared;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Some useful and frequently used functions for MagicDraw and MagicUWE
 * 
 * @author PST LMU
 */
public abstract class MagicDrawAndUWEOperations {
	// private static final Logger logger = Logger.getLogger(MagicDrawElementOperations.class);

	/**
	 * get the selected element in the tree, if it's not read-only
	 * 
	 * @param tree
	 * @return selected element (if not read only) in given tree model element
	 *         or null if no one is selected.
	 */
	public static Element getSelectedElementIfNotReadOnly(Tree tree) {
		if (tree.getSelectedNodes() == null) {
			return null;
		}
		// iterate selected nodes.
		Iterator<Node> it = Arrays.asList(tree.getSelectedNodes()).iterator();
		while (it.hasNext()) {
			Node node = it.next();
			// checks type of the node
			if (node.getUserObject() instanceof Element && ((Element) node.getUserObject()).isEditable()) {
				return (Element) node.getUserObject();
			}
		}
		// if there is no selected model element.
		return null;
	}

	/**
	 * getAllDiagramsInTreeSelectionOfTheGivenType
	 * 
	 * @param tree
	 * @param diagramType (null means every diagram)
	 * @return all diagrams of the selected elements in the tree, that have the
	 *         right diagram type
	 */
	public static List<Diagram> getAllDiagramsInTreeSelectionOfTheGivenType(Tree tree, UWEDiagramType diagramType) {
		List<Diagram> diagrams = new LinkedList<Diagram>();
		if (tree.getSelectedNodes() == null) {
			return diagrams;
		}
		// iterate selected nodes
		Iterator<Node> it = Arrays.asList(tree.getSelectedNodes()).iterator();
		while (it.hasNext()) {
			Node node = it.next();
			// checks type of the node, if diagramType is set
			if (node.getUserObject() instanceof Diagram
					&& (diagramType == null || UWEDiagramType.getDiagramType((Diagram) node.getUserObject()) == diagramType)) {
				diagrams.add((Diagram) node.getUserObject());
			}
		}
		return diagrams;
	}
}
