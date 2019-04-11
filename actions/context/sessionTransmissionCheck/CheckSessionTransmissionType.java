package magicUWE.actions.context.sessionTransmissionCheck;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeStatesNav;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Region;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.StateMachine;

/**
 * checkss recursively, if the {transmissionType} tagged value is changed on
 * substates or substatemachines.
 * 
 * @author PST LMU
 */
public class CheckSessionTransmissionType extends DefaultDiagramAction {

	private static final String TRANSMISSION_TYPE = "transmissionType";
	private static HashMap<Project, ViolationsGUI> tabs = new HashMap<Project, ViolationsGUI>();
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CheckSessionTransmissionType.class);
	private static final String sessionSter = UWEStereotypeStatesNav.SESSION.toString();

	public CheckSessionTransmissionType(String name, KeyStroke keyStroke, String group) {
		super(name, name, keyStroke, group);
	}

	/**
	 * do the transmissionType check
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		Project project = Application.getInstance().getProject();
		logger.debug("Check session's transmissionType on substates");
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();
		String transmissionType = null;
		if (selectedElements.size() == 1) {
			Element element = selectedElements.get(0).getElement();
			if (element instanceof State && StereotypesHelper.hasStereotype(element, sessionSter)) {
				transmissionType = getTransmissionTypeValue(project, element);
				if (transmissionType != null) {
					// check transmission type
					RuleViolationTransmissionType.setViolations(
							project,
							checkTransmissionType(element, project, transmissionType,
									new LinkedList<RuleViolationTransmissionType>()));

				} else {
					MessageWriter.showMessage("This check can only be executet on a <<session>> state\n"
							+ "where the tag {transmissionType} has a nonempty value.", logger);
					return;
				}
			} else {
				MessageWriter.showMessage("This check can only be executet on a <<session>> state!", logger);
				return;
			}
		} else {
			MessageWriter.showMessage("This check can only be executet on exactly one <<session>> state!", logger);
			return;
		}

		if (RuleViolationTransmissionType.getViolations(project) == null
				|| RuleViolationTransmissionType.getViolations(project).isEmpty()) {
			MessageWriter.showMessage(
					"No changes of the {transmissionType} tagged value were found recursively in the substates\n"
							+ "and substatemachines of the selected state.", logger);
		} else {
			// GUI tab
			ViolationsGUI gui = tabs.get(project);
			if (gui != null) {
				new Tab(gui);
			} else {
				gui = new ViolationsGUI(project, RuleViolationTransmissionType.getViolations(project));
				tabs.put(project, gui);
				new Tab(gui);
			}
			gui.update();
			// // simple output
			// for (RuleViolationTransmissionType ruleViolation :
			// RuleViolationTransmissionType.getViolations(project)) {
			// MessageWriter.log(ruleViolation.toString(), logger);
			// }
		}
	}

	private String getTransmissionTypeValue(Project project, Element element) {
		Stereotype stereotype = StereotypesHelper.getStereotype(project, sessionSter);
		List<String> tagValueList =
				StereotypesHelper.getStereotypePropertyValueAsString(element, stereotype, TRANSMISSION_TYPE);
		if (tagValueList != null && tagValueList.size() >= 1) {
			return tagValueList.get(0);
		}
		return null;
	}

	/**
	 * check if the {transmissionType} tagged value is redefined in substates.
	 * The output is stored as RuleViolationTransmissionType elements in the
	 * violationList.
	 * 
	 * @param superElement
	 * @param project
	 * @param transmissionTypeSuperValue
	 * @param violationList
	 * @return violationList
	 */
	private List<RuleViolationTransmissionType> checkTransmissionType(Element superElement, Project project,
			final String transmissionTypeSuperValue, List<RuleViolationTransmissionType> violationList) {
		if (superElement instanceof Region) {
			for (Element innerElement : superElement.getOwnedElement()) {
				checkTransmissionType(innerElement, project, transmissionTypeSuperValue, violationList);
			}
		} else if (superElement instanceof State) {
			StateMachine subMachine = ((State) superElement).getSubmachine();
			String transmissionTypeValue = getTransmissionTypeValue(project, superElement);
			if (subMachine != null) {
				for (Element subElement : subMachine.getOwnedElement()) {
					checkTransmissionType(subElement, project, transmissionTypeSuperValue, violationList);
				}
			} else if (transmissionTypeValue != null && !transmissionTypeValue.equals(transmissionTypeSuperValue)) {
				// normal state maybe with substates, but they are ignored
				// because we only want the first level of states that change
				// the tag
				violationList.add(new RuleViolationTransmissionType(superElement, TRANSMISSION_TYPE,
						transmissionTypeValue, transmissionTypeSuperValue));
			} else if (((State) superElement).isComposite()) {
				for (Element subElement : superElement.getOwnedElement()) {
					checkTransmissionType(subElement, project, transmissionTypeSuperValue, violationList);
				}
			}
		} else if (superElement instanceof StateMachine) {
			for (Element subElement : superElement.getOwnedElement()) {
				checkTransmissionType(subElement, project, transmissionTypeSuperValue, violationList);
			}
		}
		return violationList;
	}
}