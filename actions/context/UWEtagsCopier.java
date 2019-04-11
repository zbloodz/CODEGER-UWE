/**
 * 
 */
package magicUWE.actions.context;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jmi.reflect.JmiException;
import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ElementValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

/**
 * @author PST LMU
 *
 */
public abstract class UWEtagsCopier extends DefaultDiagramAction {
	private static final Logger logger = Logger.getLogger(UWEtagsCopier.class);

	public UWEtagsCopier(String actionID, String actionName, KeyStroke stroke, String group) {
		super(actionID, actionName, stroke, group);
	}

	private static final long serialVersionUID = 1L;
	
	 /** Copy UWE tags from srcElem to destElem in project
	 * @param srcElem
	 * @param destElem
	 * @param project
	 * @param listOfUWEStereotpes
	 */
	protected void copyUWETags(Element srcElem, Element destElem, Project project, List<String> listOfUWEStereotpes){
		// iterate over stereotypes of source
		List<Stereotype> stereotypeList = StereotypesHelper.getStereotypes(srcElem);
		for (Stereotype ster : stereotypeList) {

			if (listOfUWEStereotpes.contains(ster.getName().toLowerCase())) {
				// it is an UWE Stereotype, copy it
				Stereotype stereotype = StereotypesHelper.getStereotype(project, ster.getName());
				if (!StereotypesHelper.hasStereotype(destElem, stereotype)) {
					StereotypesHelper.addStereotype(destElem, stereotype);
					MessageWriter.log("Stereotype '" + ster.getName() + "' was added to " + destElem.getHumanName(),
							logger);
				}
				copyUWEtagsOfStereotype(srcElem, destElem, project, stereotype);
			}
		}
	}

	/**
	 * Copies UWE tags of stereotype
	 * @param srcElem
	 * @param destElem
	 * @param project
	 * @param stereotype
	 * @throws JmiException
	 */
	@SuppressWarnings({ "unchecked"})
	protected void copyUWEtagsOfStereotype(Element srcElem, Element destElem, Project project, Stereotype stereotype)
			throws JmiException {
		// Copy UWE Tags
		Map<Stereotype, List<Property>> propertyMap = StereotypesHelper
				.getPropertiesIncludingParents(stereotype);

		for (List<Property> propList : propertyMap.values()) {
			for (Property p : propList) {

				// Copy all existing values for all properties = tagged
				// values of this stereotype
				List<Object> tagValues = StereotypesHelper.getStereotypePropertyValue(srcElem, stereotype, p);
				if (tagValues != null && tagValues.size() > 0) {

					Boolean deletedOnce = false;
					for (Iterator<Object> iterator = tagValues.iterator(); iterator.hasNext();) {
						Object val = iterator.next();

						// create values of the destination element
						Slot slot = StereotypesHelper.getSlot(destElem, p, true, false);
						if (slot != null) {
							// remove old and allow more than one new
							// happens per Stereotype, but in reality
							// more than one UWE stereotype makes as
							// less sense than more than one value per
							// tag
							if (!deletedOnce) {
								for (int i = slot.getValue().size() - 1; i >= 0; i--) {
									slot.getValue().remove(i);
								}
							}
							deletedOnce = true;
							
							// for more options, see API of ElementValue
							if (val instanceof Boolean) {
								LiteralBoolean literalBoolean = project.getElementsFactory()
										.createLiteralBooleanInstance();
								literalBoolean.setValue((Boolean) val);
								slot.getValue().add(literalBoolean);

							} else if (val instanceof String) {
								LiteralString literalStr = project.getElementsFactory()
										.createLiteralStringInstance();
								literalStr.setValue((String) val);
								slot.getValue().add(literalStr);

							} else if (val instanceof Element) {
								ElementValue e = project.getElementsFactory().createElementValueInstance();
								e.setElement((Element) val);
								slot.getValue().add(e);

							} else if (val instanceof InstanceSpecification) {
								InstanceValue ei = project.getElementsFactory().createInstanceValueInstance();
								ei.setInstance((InstanceSpecification) val);
								slot.getValue().add(ei);

							} else {
								continue;
							}
							MessageWriter.log(
									"Tag '" + p.getName() + "' was added to " + destElem.getHumanName()
											+ ". Value: '" + val.toString() + "'", logger);
						}
					}
					deletedOnce = false;
				}
			}
		}
	}
}
