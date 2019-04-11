package magicUWE.actions.context.sessionTransmissionCheck;

import java.util.HashMap;
import java.util.List;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Rule Violation of the session {transmissionType} check
 * 
 * @author PST LMU
 */
class RuleViolationTransmissionType {
	/**
	 * Violations for each opened project.
	 */
	private static HashMap<Project, List<RuleViolationTransmissionType>> violationsPerProject = new HashMap<Project, List<RuleViolationTransmissionType>>();

	public final Element violationElement;
	public final String tagName;
	public final String tagValue;
	public final String parentValueWas;

	public RuleViolationTransmissionType(Element violationElement, String tagName, String tagValue,
			String parentValueWas) {
		this.violationElement = violationElement;
		this.tagName = tagName;
		this.tagValue = tagValue;
		this.parentValueWas = parentValueWas;
	}

	/**
	 * @param project
	 * @return violations for the active project
	 */
	public static List<RuleViolationTransmissionType> getViolations(Project project) {
		return violationsPerProject.get(project);
	}

	public static void cleanViolations(Project project) {
		violationsPerProject.put(project, null);
	}

	public static void setViolations(Project project, List<RuleViolationTransmissionType> violations) {
		violationsPerProject.put(project, violations);
	}

	public static RuleViolationTransmissionType getViolationForElement(Project project, Element element) {
		List<RuleViolationTransmissionType> violations = getViolations(project);
		if (element == null)
			return null;
		if (violations == null || violations.isEmpty())
			return null;
		for (RuleViolationTransmissionType violation : violations)
			if (violation.violationElement == element)
				return violation;

		return null;
	}

	@Override
	public String toString() {
		return this.violationElement.getHumanName() + " {" + this.tagName + "=" + this.tagValue + "} instead of '"
				+ this.parentValueWas + "'";
	}
}
