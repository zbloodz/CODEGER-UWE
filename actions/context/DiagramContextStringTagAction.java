package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.tags.NodeTag;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Pseudostate;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

/**
 * This class is for changing some Tags with String values of existing Elements
 * 
 * Only shown on States, Pseudostates Classes or Properties!
 * 
 * @author PST LMU
 */
public class DiagramContextStringTagAction extends DefaultDiagramAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextStringTagAction.class);
	private final NodeTag kindOfTag;
	private final UWEDiagramType uweDgType;

	public DiagramContextStringTagAction(String name, KeyStroke keyStroke, String group, UWEDiagramType uweDgType,
			NodeTag kindOfTag) {
		super(name, name, keyStroke, group);
		this.kindOfTag = kindOfTag;
		this.uweDgType = uweDgType;
	}

	/**
	 * set tag
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("set tag " + kindOfTag.toString());
		Project project = Application.getInstance().getProject();
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();

		if (UWEDiagramType.getDiagramType(project.getActiveDiagram().getDiagram()) == uweDgType
				|| !MessageWriter.showQuestion("Wrong diagram type." + "\n(MagicUWE cares about the model stereotypes)"
						+ "\n \nDo you want to accept this recommendation and do nothing?", logger)) {

			for (PresentationElement presEl : selectedElements) {
				Element element = presEl.getElement();

				if (element instanceof Class || element instanceof Property || element instanceof UseCase || element instanceof Package || 
                                        element instanceof Action || element instanceof State || element instanceof Pseudostate) {
					InputBoxWithDeleteButton inputBox =
							new InputBoxWithDeleteButton("Value of " + kindOfTag.toString() + " for the "
									+ element.getHumanName() + ":", getValueOfTag(project, element, kindOfTag));

					if (inputBox.result != null) {
						setTagTo(project, element, kindOfTag, inputBox.result);
					} else if (inputBox.isDeletionNotCancle) {
						deleteTag(project, element, kindOfTag);
					}
				} else {
					logger
							.debug("setting tags: some of the selected elements are no classes/properties/states, nothing done for these ones");
					return;
				}
			}
		}
	}

	/**
	 * Sets the value of a Tag to val for the given element. Previous values are
	 * deleted.
	 * 
	 * @param project
	 * @param element
	 * @param tag
	 * @param val
	 */
	private static void setTagTo(Project project, Element element, NodeTag tag, String val) {
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
				LiteralString literalStr = project.getElementsFactory().createLiteralStringInstance();
				literalStr.setValue(val);
				// add literal to slot values
				slot.getValue().add(literalStr);
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
	 * Delete a tagged value for the given element.
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
	 * get the actual value of the tag
	 * 
	 * @param project
	 * @param tag
	 * @param element
	 * @return first value
	 */
	@SuppressWarnings("unchecked")
	public static String getValueOfTag(Project project, Element element, NodeTag tag) {
		Stereotype stereotype = StereotypesHelper.getStereotype(project, tag.getAssociatedStereotypeName());
		if (stereotype != null) {
			// only look at the first one, because only one should be set
			List<String> tagValues = StereotypesHelper.getStereotypePropertyValue(element, stereotype, tag.toString());
			if (tagValues != null && tagValues.size() > 0) {
				return tagValues.get(0);
			}
		}
		return null;
	}
}
