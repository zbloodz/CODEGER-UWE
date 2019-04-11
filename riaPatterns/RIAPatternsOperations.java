package magicUWE.riaPatterns;

import java.util.Collection;
import java.util.LinkedList;

import magicUWE.stereotypes.tags.NodeTag;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.ElementFinder;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.Collaboration;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.CollaborationUse;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.StateMachine;

/**
 * Class with necessary methods in context with RIA Patterns
 * 
 * 
 */
public abstract class RIAPatternsOperations {

	private static final Logger logger = Logger.getLogger(RIAPatternsOperations.class);

	/**
	 * Finds source Collaboration from UWE Profile for given tag
	 * 
	 * @param project
	 * @param tag
	 * @return source Collaboration
	 */
	public static Collaboration getSourceCollaboration(Project project, NodeTag tag) {
		Collaboration sourceColl = null;
		if (project != null) {
			LinkedList<Collaboration> results = new LinkedList<Collaboration>();
			String tagNameUpperCase = tag.toString().substring(0, 1).toUpperCase() + tag.toString().substring(1);
			ElementFinder.find(project.getModel(), Collaboration.class, tagNameUpperCase, results, true);
			for (Collaboration coll : results) {
				if (coll.getQualifiedName().startsWith("UWE Profile")) {
					logger.debug("found " + coll.getHumanName());
					sourceColl = coll;
				}
			}
		}
		return sourceColl;
	}

	/**
	 * Checks if Collaboration Use for given tag already exists for given
	 * element
	 * 
	 * @param element
	 * @param tag
	 * @return existing Collaboration Use or null
	 */
	public static CollaborationUse isCollaborationUseExisting(Element element, NodeTag tag) {
		String tagNameUpperCase = tag.toString().substring(0, 1).toUpperCase() + tag.toString().substring(1);
		CollaborationUse collaboration = null;
		if (element instanceof Class) {
			collaboration = (CollaborationUse) ElementFinder.find(element, CollaborationUse.class, tagNameUpperCase);
		} else if (element instanceof Property) {
			collaboration =
					(CollaborationUse) ElementFinder.find(((Property) element).getType(), CollaborationUse.class,
							tagNameUpperCase);
		}
		if (collaboration != null) {
			logger.debug("collaboration use for " + tag.toString() + " is already existing");
		}
		return collaboration;
	}

	/**
	 * Checks if State Machine for given tag already exists for given element
	 * 
	 * @param element
	 * @param tag
	 * @return existing State Machine or null
	 */
	public static StateMachine isBehaviourExisting(Element element, NodeTag tag) {
		String tagNameUpperCase = tag.toString().substring(0, 1).toUpperCase() + tag.toString().substring(1);
		StateMachine stateMachine = null;
		if (element instanceof Class) {
			stateMachine = (StateMachine) ElementFinder.find(element, StateMachine.class, tagNameUpperCase);

		} else if (element instanceof Property) {
			stateMachine =
					(StateMachine) ElementFinder.find(((Property) element).getType(), StateMachine.class,
							tagNameUpperCase);
		}
		if (stateMachine != null) {
			logger.debug("state machine for " + tag.toString() + " is already existing");
		}
		return stateMachine;
	}

	/**
	 * Checks if parts for given tag already exist for given element
	 * 
	 * @param project
	 * @param element
	 * @param tag
	 * @return LinkedList<Property> parts with existing parts or null
	 */
	public static LinkedList<Property> isPartsExisting(Project project, Element element, NodeTag tag) {
		LinkedList<Property> parts = null;
		Collaboration sourceColl = getSourceCollaboration(project, tag);
		if (sourceColl != null) {
			for (Property sourcePart : sourceColl.getAttribute()) {
				Property foundPart = null;
				LinkedList<Property> foundResults = new LinkedList<Property>();
				if (element instanceof Class) {
					ElementFinder.find(element, Property.class, sourcePart.getName(), foundResults, false);

				} else if (element instanceof Property) {
					ElementFinder.find(((Property) element).getType(), Property.class, sourcePart.getName(),
							foundResults, false);
				}
				for (Property foundResult : foundResults) {
					Collection<Dependency> depOfPart = foundResult.getClientDependency();
					for (Dependency dep : depOfPart) {
						if (dep.getSupplier().contains(sourcePart)) {
							foundPart = foundResult;
						}
					}
				}
				if (foundPart != null) {
					logger.debug("part " + foundPart.getName() + " for " + tag.toString() + " is already existing");
					if (parts == null) {
						parts = new LinkedList<Property>();
					}
					parts.add(foundPart);
				}
			}
		}
		return parts;
	}

}
