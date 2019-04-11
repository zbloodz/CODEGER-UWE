package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import magicUWE.riaPatterns.DependenciesReplaceDialog;
import magicUWE.riaPatterns.RIADependenciesAdder;
import magicUWE.riaPatterns.RIAPatternsOperations;
import magicUWE.riaPatterns.RIAPatternsTagDialog;
import magicUWE.riaPatterns.RIATagsHelper;
import magicUWE.settings.GlobalConstants;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.tags.NodeTag;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

import org.apache.log4j.Logger;

import com.nomagic.actions.NMStateAction;
import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.CollaborationUse;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.StateMachine;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * This class is for changing some Tags with boolean values of existing Elements
 * (e.g. the tags IS_LANDMARK, IS_HOME)
 * 
 * Only shown on States, Pseudostates Classes or Properties!
 * 
 * @author PST LMU
 * 
 */
public class DiagramContextBooleanTagAction extends NMStateAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextBooleanTagAction.class);
	private final NodeTag kindOfTag;
	private final UWEDiagramType uweDgType;

	public DiagramContextBooleanTagAction(String name, KeyStroke keyStroke, String group,
			UWEDiagramType uweDgType, NodeTag kindOfTag) {
		super(name, name, keyStroke, group);
		this.kindOfTag = kindOfTag;
		this.uweDgType = uweDgType;
	}

	/**
	 * set e.g. IS_HOME or IS_LANDMARK - tag
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		super.actionPerformed(event);
		logger.debug("set tag " + kindOfTag.toString());
		Project project = Application.getInstance().getProject();
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();

		if (UWEDiagramType.getDiagramType(project.getActiveDiagram().getDiagram()) == uweDgType
				|| !MessageWriter.showQuestion("Wrong diagram type." + "\n(MagicUWE cares about the model stereotypes)"
						+ "\n \nDo you want to accept this recommendation and do nothing?", logger)) {
			for (PresentationElement presEl : selectedElements) {
				Element element = presEl.getElement();
				if (element instanceof Action || element instanceof Pin || element instanceof Class || 
                                        element instanceof UseCase || element instanceof Package ||
                                        element instanceof Property || element instanceof State || element instanceof Pseudostate) {
					if (uweDgType.equals(UWEDiagramType.PRESENTATION)) {
						doRIAOperation(project, element);

					} else {
						// getState() is the new status of the checkbox
						// outdated: try ((Property) element).getType() to set
						// it to
						// the class of the property
						setTagToPropertyAndClass(project, element, kindOfTag, this.getState());
					}

				} else {
					logger.debug("setting tags: some of the selected elements are no classes/properties/states, nothing done for these ones");
				}
			}
		}
	}

	/**
	 * Executes RIA operation according to RIA Option chosen for kindOfTag in
	 * RIA Patterns Dialog of MagicUWE main menu
	 * 
	 * @param project
	 *            active project
	 * @param element
	 *            element to which RIA Pattern will be added
	 */
	private void doRIAOperation(Project project, Element element) {
		if (kindOfTag.getRIAOption().equals(GlobalConstants.RIA_OPTION_TAG_ONLY)) {
			if (this.getState()) {
				setTagToPropertyAndClass(project, element, kindOfTag, this.getState());
			} else {
				deleteTagFromPropertyAndClass(project, element, kindOfTag);
			}

		} else if (kindOfTag.getRIAOption().equals(GlobalConstants.RIA_OPTION_BEHAVIOUR)) {
			if (this.getState()) {
				addBehaviour(project, element);
			} else {
				openTagDialog(project, element);
			}

		} else if (kindOfTag.getRIAOption().equals(GlobalConstants.RIA_OPTION_DEPENDENCIES)) {
			if (this.getState()) {
				addDependencies(project, element);
			} else {
				openTagDialog(project, element);
			}

		} else if (kindOfTag.getRIAOption().equals(GlobalConstants.RIA_OPTION_ASK_EVERYTIME)) {
			openTagDialog(project, element);
		}

	}

	/**
	 * Sets the value of a Tag (named by this enum) to val for the given
	 * element. Previous values are deleted.
	 * 
	 * @param project
	 * @param element
	 * @param tag
	 * @param val
	 */
	private void setTagTo(Project project, Element element, NodeTag tag, boolean val) {

		// set the value if not already set
		Stereotype stereotype = StereotypesHelper.getStereotype(project, tag.getAssociatedStereotypeName());
		SessionManager.getInstance().createSession("Inserting new tag value " + tag.toString());
		Property property = StereotypesHelper.getPropertyByName(stereotype, tag.toString());

		if (stereotype != null && property != null) {
			// get/create the slot
			Slot slot = StereotypesHelper.getSlot(element, property, true, false);
			if (slot != null) {
				// Delete the values (only one value should be set)
				for (int i = slot.getValue().size() - 1; i >= 0; i--) {
					slot.getValue().remove(i);
				}

				// create literal specification
				LiteralBoolean literalBoolean = project.getElementsFactory().createLiteralBooleanInstance();
				literalBoolean.setValue(val);
				// add literal to slot values
				slot.getValue().add(literalBoolean);
				logger.debug("Tag " + tag.toString() + " added to " + element.getHumanName());
			} else {
				MessageWriter.showError("Selected element does not contain a \"" + tag.getAssociatedStereotypeName()
						+ "\" - stereotype (tag / slot)", logger);
			}
		} else {
			logger.debug("Can't get stereotype/property for adding Tags");
		}
		SessionManager.getInstance().closeSession();
	}

	/**
	 * Sets the value of a tag to val for the given element and its class (if
	 * the element is a property)
	 * 
	 * @param project
	 * @param element
	 * @param tag
	 * @param val
	 */
	private void setTagToPropertyAndClass(Project project, Element element, NodeTag tag, boolean val) {
		setTagTo(project, element, tag, val);
		if (element instanceof Property) {
			if (StereotypesHelper.hasStereotype(((Property) element).getType(), tag.getAssociatedStereotypeName())) {
				setTagTo(project, ((Property) element).getType(), tag, val);
			} else {
				StereotypesHelper.addStereotypeByString(((Property) element).getType(),
						tag.getAssociatedStereotypeName());
				setTagTo(project, ((Property) element).getType(), tag, val);
			}

		}

	}
	/**
	 * Delete a tagged value from the given element and - if the element is a property - from its class
	 * @param project
	 * @param element
	 * @param tag
	 */
	private void deleteTagFromPropertyAndClass(Project project, Element element, NodeTag tag){
		deleteTag(project, element, tag);
		if(element instanceof Property){
			deleteTag(project, ((Property) element).getType(), tag);
		}
	}

	/**
	 * Delete a tagged value from the given element.
	 * 
	 * @param project
	 * @param element
	 * @param tag
	 */
	@SuppressWarnings("unchecked")
	private void deleteTag(Project project, Element element, NodeTag tag) {
		Stereotype stereotype = StereotypesHelper.getStereotype(project, tag.getAssociatedStereotypeName());
		SessionManager.getInstance().createSession("Deleting tag " + tag.toString());

		Property property = StereotypesHelper.getPropertyByName(stereotype, tag.toString());
		if (stereotype != null && property != null) {
			// Look if there are values first (needed for pure deletion)
			List<Boolean> tagValues = StereotypesHelper.getStereotypePropertyValue(element, stereotype, tag.toString());
			if (tagValues != null && tagValues.size() > 0) {
				// get/create the slot
				Slot slot = StereotypesHelper.getSlot(element, property, false, false);
				if (slot != null) {
					// Delete the values
					for (int i = slot.getValue().size() - 1; i >= 0; i--) {
						slot.getValue().remove(i);
					}
				} else {
					MessageWriter.showError("Selected element does not contain a \"" + tag.getAssociatedStereotypeName()
							+ "\" - stereotype (tag / slot)", logger);
				}
			}
		} else {
			logger.debug("Can't get stereotype/property for deleting tags");
		}
		SessionManager.getInstance().closeSession();
	}

	/**
	 * Adds behaviour (dependencies and state machine) for kindOfTag to element
	 * (if element is instance of Property, it is added to class of element). If
	 * behaviour already exists, user gets question dialog to replace.
	 * 
	 * @param project
	 * @param element
	 */
	private void addBehaviour(Project project, Element element) {
		StateMachine existingSM = RIAPatternsOperations.isBehaviourExisting(element, kindOfTag);
		CollaborationUse existingColl = RIAPatternsOperations.isCollaborationUseExisting(element, kindOfTag);
		LinkedList<Property> existingParts = RIAPatternsOperations.isPartsExisting(project, element, kindOfTag);
		RIADependenciesAdder depAdder = new RIADependenciesAdder(project, element, kindOfTag);
		if (existingColl != null && existingParts != null && existingSM == null) {

			setTagToPropertyAndClass(project, element, kindOfTag, true);
			addStateMachine(project, element);

		} else if (existingColl != null && existingParts != null && existingSM != null) {
			if (MessageWriter.showQuestion("Adding behaviour: \n\nBehaviour for RIA Pattern '" + kindOfTag.toString()
					+ "' already exists. \nDo you want to replace?", logger)) {

				boolean dialogCanceled = depAdder.addDependencies();
				if (!dialogCanceled) {
					setTagToPropertyAndClass(project, element, kindOfTag, true);

					SessionManager.getInstance().createSession("delete existing behaviour");
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					existingSM.dispose();
					SessionManager.getInstance().closeSession();

					addStateMachine(project, element);
				}
			}
		} else {
			boolean dialogCanceled = depAdder.addDependencies();
			if (!dialogCanceled) {
				setTagToPropertyAndClass(project, element, kindOfTag, true);
				addStateMachine(project, element);
			}
		}
	}

	/**
	 * Copies state machine for kindOfTag from UWE Profile to given element (if
	 * element is instance of Property, it's added to class of element)
	 * 
	 * @param project
	 * @param element
	 */
	private void addStateMachine(Project project, Element element) {

		if (project != null) {

			boolean foundSM = false;
			Collection<DiagramPresentationElement> stateMachines =
					project.getDiagrams(DiagramTypeConstants.UML_STATECHART_DIAGRAM);
			for (Iterator<DiagramPresentationElement> it = stateMachines.iterator(); it.hasNext();) {
				DiagramPresentationElement sourceDiagram = it.next();
				if (sourceDiagram.getName().equalsIgnoreCase(kindOfTag.toString())) {

					foundSM = true;
					logger.debug("found state machine diagram " + sourceDiagram.getHumanName());
					Element stateMachineToCopy = sourceDiagram.getDiagram().getOwner();
					String qualName = sourceDiagram.getDiagram().getQualifiedName();

					if (qualName.startsWith("UWE Profile")) {
						SessionManager.getInstance().createSession("copy state machine");
						logger.debug("copying state machine");
						if (element instanceof Property) {
							CopyPasting.copyPasteElement(stateMachineToCopy, ((Property) element).getType());
						} else {
							CopyPasting.copyPasteElement(stateMachineToCopy, element);
						}

						SessionManager.getInstance().closeSession();
						MessageWriter.log("State Machine " + sourceDiagram.getHumanName()
								+ " for the RIA behaviour is added", logger);
					}
				}
			}
			if (!foundSM) {
				MessageWriter.showMessage("State Machine for RIA Pattern '" + kindOfTag.toString()
						+ "' does not exist in UWE Profile.", logger);
			}
		}
	}

	/**
	 * Adds dependencies with {@link RIADependenciesAdder} for kindOfTag to
	 * given element (if element is instance of Property, it's added to class of
	 * element). If dependencies already exists, user gets question dialog to
	 * replace. If behaviour already exists, user gets
	 * {@link DependenciesReplaceDialog} to choose action.
	 * 
	 * @param project
	 * @param element
	 */
	private void addDependencies(Project project, Element element) {
		StateMachine existingSM = RIAPatternsOperations.isBehaviourExisting(element, kindOfTag);
		CollaborationUse existingColl = RIAPatternsOperations.isCollaborationUseExisting(element, kindOfTag);
		LinkedList<Property> existingParts = RIAPatternsOperations.isPartsExisting(project, element, kindOfTag);
		RIADependenciesAdder depAdder = new RIADependenciesAdder(project, element, kindOfTag);
		if (existingColl != null && existingParts != null && existingSM == null) {
			if (MessageWriter.showQuestion(
					"Adding dependencies: \n\nDependencies for RIA Pattern '" + kindOfTag.toString()
							+ "' already exist. \nDo you want to replace?", logger)) {

				boolean dialogCanceled = depAdder.addDependencies();
				if (!dialogCanceled) {
					setTagToPropertyAndClass(project, element, kindOfTag, true);
					SessionManager.getInstance().createSession("delete existing dependencies");
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					SessionManager.getInstance().closeSession();

				}
			}
		} else if (existingColl != null && existingParts != null && existingSM != null) {
			DependenciesReplaceDialog replaceDialog = new DependenciesReplaceDialog(kindOfTag.toString());
			String selection = replaceDialog.getSelection();
			if (selection.equals(DependenciesReplaceDialog.replaceDepNotSMName)) {

				boolean dialogCanceled = depAdder.addDependencies();
				if (!dialogCanceled) {
					setTagToPropertyAndClass(project, element, kindOfTag, true);
					SessionManager.getInstance().createSession("delete existing dependencies");
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					SessionManager.getInstance().closeSession();
				}
			} else if (selection.equals(DependenciesReplaceDialog.replaceDepRemoveSMName)) {
				boolean dialogCanceled = depAdder.addDependencies();
				if (!dialogCanceled) {
					setTagToPropertyAndClass(project, element, kindOfTag, true);
					SessionManager.getInstance().createSession("delete existing behaviour");
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					existingSM.dispose();
					SessionManager.getInstance().closeSession();
				}
			} else if (selection.equals(DependenciesReplaceDialog.removeSMNotDepName)) {
				setTagToPropertyAndClass(project, element, kindOfTag, true);
				SessionManager.getInstance().createSession("delete existing state machine");
				existingSM.dispose();
				SessionManager.getInstance().closeSession();
			}
		} else {
			boolean dialogCanceled = depAdder.addDependencies();
			if (!dialogCanceled) {
				setTagToPropertyAndClass(project, element, kindOfTag, true);
			}
		}

	}

	/**
	 * Opens a {@link RIAPatternsTagDialog} for the setting of tags and RIA
	 * features and executes the selected RIA option.
	 * 
	 * @param project
	 * @param element
	 */
	@SuppressWarnings("null") // because of dependenciesExisting and behaviourExisting
	private void openTagDialog(Project project, Element element) {
		StateMachine existingSM = RIAPatternsOperations.isBehaviourExisting(element, kindOfTag);
		CollaborationUse existingColl = RIAPatternsOperations.isCollaborationUseExisting(element, kindOfTag);
		LinkedList<Property> existingParts = RIAPatternsOperations.isPartsExisting(project, element, kindOfTag);
		boolean dependenciesExisting = false;
		boolean behaviourExisting = false;
		if (existingColl != null && existingParts != null) {
			if (existingSM == null) {
				dependenciesExisting = true;
			} else {
				behaviourExisting = true;
			}
		}
		RIAPatternsTagDialog dialog =
				new RIAPatternsTagDialog(kindOfTag.toString(), this.getState(), dependenciesExisting, behaviourExisting);
		String selected = dialog.getSelection();
		if (selected.equals(GlobalConstants.RIA_OPTION_TAG_ONLY)) {
			if (!dependenciesExisting && !behaviourExisting) {
				setTagToPropertyAndClass(project, element, kindOfTag, true);
			} else if (dependenciesExisting) {
				if (MessageWriter.showQuestion(
						"Set tag only: \n\nDependencies for RIA pattern '" + kindOfTag.toString()
								+ "' already exist. \nDo you want to delete existing dependencies and set just the tag?",
						logger)) {
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					setTagToPropertyAndClass(project, element, kindOfTag, true);
				}
			} else if (behaviourExisting) {
				if (MessageWriter.showQuestion("Set tag only: \n\nBehaviour for RIA Pattern '" + kindOfTag.toString()
						+ "' already exists. \nDo you want to delete the existing behaviour and set just the tag?", logger)) {
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					existingSM.dispose();
					setTagToPropertyAndClass(project, element, kindOfTag, true);
				}
			}

		} else if (selected.equals(GlobalConstants.RIA_OPTION_BEHAVIOUR)) {
			addBehaviour(project, element);
		} else if (selected.equals(GlobalConstants.RIA_OPTION_DEPENDENCIES)) {
			addDependencies(project, element);
		} else if (selected.equals(RIAPatternsTagDialog.deleteTagButtonName)) {
			if (dependenciesExisting) {
				if (MessageWriter.showQuestion("Delete tag: \n\nDependencies for RIA Pattern '" + kindOfTag.toString()
						+ "' already exist. \nDo you want to delete them?", logger)) {
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					deleteTagFromPropertyAndClass(project, element, kindOfTag);
				}
			} else if (behaviourExisting) {
				if (MessageWriter.showQuestion("Delete tag: \n\nBehaviour for RIA Pattern '" + kindOfTag.toString()
						+ "' already exists. \nDo you want to delete it?", logger)) {
					existingColl.dispose();
					for (Property part : existingParts) {
						part.dispose();
					}
					existingSM.dispose();
					deleteTagFromPropertyAndClass(project, element, kindOfTag);
				}
			} else {
				deleteTagFromPropertyAndClass(project, element, kindOfTag);
			}

		} else {
			logger.debug("Dialog canceled or unknown selection in RIAPatternsTagDialog");
		}
	}

	/**
	 * set state of checkbox from menu according to the actual value of the tag
	 * 
	 * @param element
	 */
	public void setStateAccordingToValueOfTag(Element element) {
		setState(getValueOfTag(Application.getInstance().getProject(), element, kindOfTag));
	}

	/**
	 * get the actual value of the tag
	 * 
	 * @param project
	 * @param tag
	 * @param element
	 * @return false <=> (first value) set to false or not set or not existing
	 */
	@SuppressWarnings("unchecked")
	private boolean getValueOfTag(Project project, Element element, NodeTag tag) {
		Stereotype stereotype = StereotypesHelper.getStereotype(project, tag.getAssociatedStereotypeName());
		if (stereotype != null) {
			// The tagValues are booleans - only look at the first one
			List<Boolean> tagValues = StereotypesHelper.getStereotypePropertyValue(element, stereotype, tag.toString());
			if (tagValues != null && tagValues.size() > 0) {
				return tagValues.get(0);
			}
		}
		return false;
	}

	/**
	 * Sets icon of {@link DiagramContextBooleanTagAction} according to RIA
	 * Option chosen in RIA Patterns Dialog
	 */
	public void setIcon() {
		ImageIcon icon = RIATagsHelper.getRIAIcon(kindOfTag.getRIAOption());
		if (icon != null) {
			this.setSmallIcon(icon);
		}
	}

}
