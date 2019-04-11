package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeStatesNav;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Region;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;

/**
 * set default stereotype (navigationalNode) on all states nested in a state,
 * which is typed by a kind of <<navigationalNode>> stereotype
 * 
 * @author PST LMU
 */
public class DiagramContextSetDefaultNavStateStereotype extends DefaultDiagramAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextSetDefaultNavStateStereotype.class);

	public DiagramContextSetDefaultNavStateStereotype(String name, KeyStroke keyStroke, String group) {
		super(name, name, keyStroke, group);
	}

	/**
	 * set default stereotype
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		Project project = Application.getInstance().getProject();
		logger.debug("set default navigation state tags");
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();

		if (MessageWriter.showQuestionOkCancel("Do you want to set the default stereotype «navigationalNode»\n"
				+ "on substates that are not already stereotyped by\n"
				+ "a kind of «navigationalNode» stereotype?\n \n"
				+ "     (This functionality can only be applied on \n"
				+ "     «navigationalNode» and «session» states.)", logger)) {
			SessionManager.getInstance().createSession("set default navigation state stereotype to substates");
			for (PresentationElement presEl : selectedElements) {
				Element element = presEl.getElement();

				if (element instanceof State && isKindOfNavigationalNodeOrSession(element)) {
					setNavigationalNodeToUnstereotypedSubstates(element, project);
				} else {
					MessageWriter
							.log("set default stereotypes: some of the selected elements are no states or are not stereotyped by a «navigationalNode» sterotype. Nothing done for these ones",
									logger);
				}
			}
			SessionManager.getInstance().closeSession();
			MessageWriter.log("set default stereotypes: Done.", logger);
		}
	}

	/**
	 * set default stereotype (navigationalNode) on all states nested in a state
	 * (and its regions), which is typed by a kind of <<navigationalNode>>
	 * stereotype Recursive for state subelements, even if the parent had been
	 * stereotyped before!
	 * 
	 * @param superElement
	 * @param project
	 */
	private void setNavigationalNodeToUnstereotypedSubstates(Element superElement, Project project) {
		Stereotype sterNavNode = StereotypesHelper.getStereotype(project,
				UWEStereotypeStatesNav.NAVIGATIONAL_NODE.toString());
		if (superElement instanceof Region || isKindOfNavigationalNodeOrSession(superElement)) {
			for (Element innerElement : superElement.getOwnedElement()) {
				if (innerElement instanceof State) {
					State subState = (State) innerElement;
					if (!isKindOfAnyNavigationalNode(subState)) {
						StereotypesHelper.addStereotype(subState, sterNavNode);
						MessageWriter.log(
								"Stereotype '" + sterNavNode.getName() + "' was added to " + subState.getHumanName(),
								logger);
					}
					setNavigationalNodeToUnstereotypedSubstates(subState, project);
				} else if (innerElement instanceof Region) {
					setNavigationalNodeToUnstereotypedSubstates(innerElement, project);
				}
			}
		}
	}

	/**
	 * 
	 * @param state
	 * @return true <=> state is stereotyped by a stereotype of the
	 *         UWEStereotypeStatesNav enum.
	 */
	private boolean isKindOfAnyNavigationalNode(State state) {
		for (UWEStereotypeStatesNav ster : UWEStereotypeStatesNav.values()) {
			if (StereotypesHelper.hasStereotype(state, ster.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param element
	 * @return true <=> element is dennoted by the stereotype navigationalNode
	 *         or session.
	 */
	private boolean isKindOfNavigationalNodeOrSession(Element element) {
		if (StereotypesHelper.hasStereotype(element, UWEStereotypeStatesNav.NAVIGATIONAL_NODE.toString())
				|| StereotypesHelper.hasStereotype(element, UWEStereotypeStatesNav.SESSION.toString())) {
			return true;
		}
		return false;
	}
}
