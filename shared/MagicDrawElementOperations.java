/**
 * 
 */
package magicUWE.shared;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdstructuredactivities.StructuredActivityNode;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.InputPin;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.OutputPin;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.PseudostateKind;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;

/**
 * Some useful operations on UML Elements
 * 
 * @author PST LMU
 */
public abstract class MagicDrawElementOperations {
	private static final Logger logger = Logger.getLogger(MagicDrawElementOperations.class);

	/**
	 * @param element
	 * @param stereotypeString
	 * @return true <=> element has a stereotype named stereotypeString
	 */
	public static boolean hasStereotype(Element element, String stereotypeString) {
		List<Stereotype> stereotypes = StereotypesHelper.getStereotypes(element);
		for (Stereotype stereotype : stereotypes) {
			if (stereotype.getName().equals(stereotypeString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new class in MagicDraw
	 * 
	 * @param ster
	 * @return class
	 */
	public static Class createClass(String ster) {
		logger.debug("create class");
		Class clazz = Application.getInstance().getProject().getElementsFactory().createClassInstance();
		addStereotypeToElement(clazz, ster);
		return clazz;
	}

	public static Association createAssociation(String ster) {
		logger.debug("create class");
		Association clazz = Application.getInstance().getProject().getElementsFactory().createAssociationInstance();
		addStereotypeToElement(clazz, ster);
		return clazz;
	}
	
	/**
	 * Creates a new Action in MagicDraw
	 * 
	 * @param ster
	 * @return class
	 */
	public static CallBehaviorAction createAction(String ster) {
		logger.debug("create action");
		CallBehaviorAction action = Application.getInstance().getProject().getElementsFactory().createCallBehaviorActionInstance();
		addStereotypeToElement(action, ster);
		return action;
	}
	
	/**
	 * Creates a new Action in MagicDraw
	 * 
	 * @param ster
	 * @return class
	 */
	public static StructuredActivityNode createStructuredAction(String ster) {
		logger.debug("create action");
		StructuredActivityNode action = Application.getInstance().getProject().getElementsFactory().createStructuredActivityNodeInstance();
		addStereotypeToElement(action, ster);
		return action;
	}

	/**
	 * Creates a new InputPin in MagicDraw
	 *
	 * @param ster
	 * @return class
	 */
	public static InputPin createInputPin(String ster) {
		logger.debug("create input pin");
		InputPin pin = Application.getInstance().getProject().getElementsFactory().createInputPinInstance();
		addStereotypeToElement(pin, ster);
		return pin;
	}

        /**
	 * Creates a new OutputPin in MagicDraw
	 *
	 * @param ster
	 * @return class
	 */
	public static OutputPin createOutputPin(String ster) {
		logger.debug("create input pin");
		OutputPin pin = Application.getInstance().getProject().getElementsFactory().createOutputPinInstance();
		addStereotypeToElement(pin, ster);
		return pin;
	}

        /**
	 * Creates a new UseCase in MagicDraw
	 *
	 * @param ster
	 * @return class
	 */
	public static UseCase createUseCase(String ster) {
		logger.debug("create use case");
		UseCase use = Application.getInstance().getProject().getElementsFactory().createUseCaseInstance();
		addStereotypeToElement(use, ster);
		return use;
	}

        /**
	 * Creates a new Package in MagicDraw
	 *
	 * @param ster
	 * @return class
	 */
	public static  Package createPackage(String ster) {
		logger.debug("create use case");
		Package pack = Application.getInstance().getProject().getElementsFactory().createPackageInstance();
		addStereotypeToElement(pack, ster);
		return pack;
	}
	
	/**
	 * Creates a new State in MagicDraw
	 * 
	 * @param ster
	 * @return class
	 */
	public static State createState(String ster) {
		logger.debug("create state");
		State state = Application.getInstance().getProject().getElementsFactory().createStateInstance();
		addStereotypeToElement(state, ster);
		return state;
	}
	
	/**
	 * Creates a new Pseudo State in MagicDraw
	 * 
	 * @param ster
	 * @param pseudostateKindEnum
	 * @return class
	 */
	public static Pseudostate createPseudoState(String ster, PseudostateKind pseudostateKindEnum) {
		logger.debug("create pseudo state");
		Pseudostate state = Application.getInstance().getProject().getElementsFactory().createPseudostateInstance();
		state.setKind(pseudostateKindEnum);
		addStereotypeToElement(state, ster);
		return state;
	}

	/**
	 * add stereotype to the given element if possible
	 * 
	 * @param elem
	 * @param ster
	 * @return true <=> stereotype added
	 */
	public static boolean addStereotypeToElement(Element elem, String ster) {
		if (elem != null && elem.isEditable() && ster != null && !ster.equals("")) {
			logger.debug("set sterotype \"" + ster + "\" to element");
			Stereotype stereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(), ster);
			if (stereotype != null) {
				StereotypesHelper.addStereotype(elem, stereotype);
				return true;
			}
			MessageWriter.log("Can't load stereotype \"" + ster + "\" for element \"" + elem.getHumanName() + "\"! Please add the UWE profile (Option/Modules)!",
					logger);
		}
		return false;
	}

	/**
	 * remove stereotype from the given element if possible
	 * 
	 * @param elem
	 * @param ster
	 * @return true <=> stereotype removed successfully
	 */
	public static boolean tryToRemoveStereotypeFromElement(Element elem, String ster) {
		if (elem != null && elem.isEditable() && ster != null && !ster.equals("")) {
			if (MagicDrawElementOperations.hasStereotype(elem, ster)) {
				logger.debug("remove sterotype \"" + ster + "\" from element");
				Stereotype stereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(), ster);
				if (stereotype != null) {
					StereotypesHelper.removeStereotype(elem, stereotype);
					return true;
				}
				MessageWriter.log("Can't remove stereotype \"" + ster + "\" from element \"" + elem.getHumanName()
						+ "\"!", logger);
			}
		}
		return false;
	}
	
	/**
	 * Collect for each Element a List of PresentationElements in a given
	 * PresentationElement, like e.g. a diagram. That means if e.g. a class is
	 * shown twice in a diagram, the hashMap contains an entry with the key
	 * "class" and the value "PresentationElement1, PresentationElement2".
	 * 
	 * @param presentationElement
	 * @param elements
	 */
	public static void collectElementsAndPresentationElements(PresentationElement presentationElement,
			HashMap<Element, List<PresentationElement>> elements) {
		List<PresentationElement> ownedPresentationElements = presentationElement.getPresentationElements();
		for (PresentationElement pe : ownedPresentationElements) {
			Element element = pe.getElement();
			if (element != null) {
				List<PresentationElement> oldList = elements.get(element);
				if (oldList != null) {
					oldList.add(pe);
					elements.put(element, oldList);
				} else {
					LinkedList<PresentationElement> newList = new LinkedList<PresentationElement>();
					newList.add(pe);
					elements.put(element, newList);
				}
			}
			collectElementsAndPresentationElements(pe, elements);
		}
	}
}
