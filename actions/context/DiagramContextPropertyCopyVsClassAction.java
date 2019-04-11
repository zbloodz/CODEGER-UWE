package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.UWEStereotypeClassPres;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

/**
 * Copies stereotypes and tags from properties to classes and the other way around
 * (Activated on properties)
 * 
 * @author PST LMU
 */
public class DiagramContextPropertyCopyVsClassAction extends UWEtagsCopier {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextPropertyCopyVsClassAction.class);

	public DiagramContextPropertyCopyVsClassAction(String name, KeyStroke keyStroke, String group) {
		super(name, name, keyStroke, group);
	}

	/**
	 * copy uwe property tags to class
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("copy tags");
		Project project = Application.getInstance().getProject();
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();

		for (PresentationElement presEl : selectedElements) {
			Element element = presEl.getElement();

			if (element instanceof Property) {
				copyUWETagsOfPropertyToClass((Property) element);
			} else {
				logger
						.debug("copying tags: some of the selected elements are no properties, nothing done for these ones");
			}
		}
		MessageWriter.log("copying tags: Done.", logger);
	}

	private void copyUWETagsOfPropertyToClass(Property element) {
		Object[] options = { "Property -> Class", "Class -> Property" };
		int n =
				JOptionPane
						.showOptionDialog(
								Application.getInstance().getMainFrame(),
								"<html>Copy UWE stereotypes and tagged values of them<br/>in the following direction:<br/>(tagged values will be overwritten!)</html>",
								"Copy Tags", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

		if (n != JOptionPane.CLOSED_OPTION) {
			if (n == JOptionPane.YES_OPTION) {
				copyPresentationStereotypesAndTags(element, element.getType());
			} else {
				copyPresentationStereotypesAndTags(element.getType(), element);
			}
		}
		MessageWriter.log("Done.", logger);
	}

	private void copyPresentationStereotypesAndTags(Element srcElem, Element destElem) {
		SessionManager.getInstance().createSession("copy tags");
		Project project = Application.getInstance().getProject();

		// List with UWE presentation stereotypes as String
		List<String> arrayList = new LinkedList<String>();
		for (UWEStereotypeClassPres uweSter : UWEStereotypeClassPres.values()) {
			arrayList.add(uweSter.toString().toLowerCase());
		}
		copyUWETags(srcElem, destElem, project, arrayList);
		SessionManager.getInstance().closeSession();
	}
}
