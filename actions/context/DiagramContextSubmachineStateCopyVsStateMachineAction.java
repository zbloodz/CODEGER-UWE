package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeStatesNav;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;

/**
 * Copies stereotypes and tags from properties to classes and the other way
 * around (Activated on properties)
 * 
 * @author PST LMU
 */
public class DiagramContextSubmachineStateCopyVsStateMachineAction extends UWEtagsCopier{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextSubmachineStateCopyVsStateMachineAction.class);

	public DiagramContextSubmachineStateCopyVsStateMachineAction(String name, KeyStroke keyStroke, String group) {
		super(name, name, keyStroke, group);
	}

	/**
	 * copy uwe state machine stereotypes and tags to submachine states or the
	 * other way around
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("copy Stereotypes and Tags state machine <-> submachine state");
		Project project = Application.getInstance().getProject();
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();

		for (PresentationElement presEl : selectedElements) {
			Element element = presEl.getElement();

			if (element instanceof State) {
				copyUWETagsOfPropertyToClass((State) element);
			} else {
				logger.debug("copying stereotypes and tags: some of the selected elements are no states, nothing done for these ones");
			}
		}
		MessageWriter.log("copying tags: Done.", logger);
	}

	private void copyUWETagsOfPropertyToClass(State element) {
		Object[] options = { "Submachine State -> State Machine", "State Machine -> Submachine State" };
		int n = JOptionPane
				.showOptionDialog(null,
						"<html>Copy UWE stereotypes and tagged values of them<br/>in the following direction:<br/>(tagged values will be overwritten!)</html>",
						"Copy Stereotypes and Tags", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);

		if (n != JOptionPane.CLOSED_OPTION) {
			if (n == JOptionPane.YES_OPTION) {
				copyPresentationStereotypesAndTags(element, element.getSubmachine());
			} else {
				copyPresentationStereotypesAndTags(element.getSubmachine(), element);
			}
		}
		MessageWriter.log("Done.", logger);
	}

	private void copyPresentationStereotypesAndTags(Element srcElem, Element destElem) {
		// MAYBE refactor and merge those copy functionalities (see instance +
		// element tags)
		SessionManager.getInstance().createSession("copy stereotype and tags");
		Project project = Application.getInstance().getProject();

		// List with UWE presentation stereotypes as String
		List<String> arrayList = new LinkedList<String>();
		for (UWEStereotypeStatesNav uweSter : UWEStereotypeStatesNav.values()) {
			arrayList.add(uweSter.toString().toLowerCase());
		}

		copyUWETags(srcElem, destElem, project, arrayList);
		SessionManager.getInstance().closeSession();
	}
}
