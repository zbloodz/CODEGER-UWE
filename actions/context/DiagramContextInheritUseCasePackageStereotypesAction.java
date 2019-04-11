package magicUWE.actions.context;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;

/**
 * Copies stereotypes and tags from a package to use cases
 * 
 * @author PST LMU
 */
public class DiagramContextInheritUseCasePackageStereotypesAction extends UWEtagsCopier {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DiagramContextInheritUseCasePackageStereotypesAction.class);

	public DiagramContextInheritUseCasePackageStereotypesAction(String name, KeyStroke keyStroke, String group) {
		super(name, name, keyStroke, group);
	}

	/**
	 * copy uwe stereotypes to use cases
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("copy stereotypes to use cases");
		Project project = Application.getInstance().getProject();
		List<PresentationElement> selectedElements = project.getActiveDiagram().getSelected();
		if (selectedElements.size() != 1) {
			MessageWriter.showMessage("Only exactly one package can be selected!", logger);
		} else {
			if (MessageWriter.showQuestionOkCancel(
					"Do you want to copy the UWE stereotypes (and tags) of this package\n"
							+ "to all inner use cases that are not stereotyped yet?", logger)) {
				for (PresentationElement presEl : selectedElements) {
					Element element = presEl.getElement();

					if (element instanceof Package) {
						copyUWESeterotypesToUseCases((Package) element);
					} else {
						logger.debug("inherit stereotypes: some of the selected elements are no packages, nothing done for these ones");
					}
				}
				MessageWriter.log("inherit stereotypes: Done.", logger);
			}
		}
	}

	private void copyUWESeterotypesToUseCases(Package srcElem) {
		SessionManager.getInstance().createSession("copy stereotypes and tags");
		Project project = Application.getInstance().getProject();

		// iterate over stereotypes of source
		List<Stereotype> stereotypeList = StereotypesHelper.getStereotypes(srcElem);
		for (Stereotype ster : stereotypeList) 
                {
                    UWEStereotypeRequirementsUseCases[] listUseCases=UWEStereotypeRequirementsUseCases.values();
                    for (int i=0;i<listUseCases.length;i++)
                    {
			if (listUseCases[i].toString().equals(ster.getName())) 
                        {
				// it is an UWE Stereotype, copy it
				Stereotype stereotype = StereotypesHelper.getStereotype(project, ster.getName());

				// iterate over destination elements
				for (Element destElem : srcElem.getOwnedElement()) {
					if (!StereotypesHelper.hasStereotype(destElem, stereotype)) {
						StereotypesHelper.addStereotype(destElem, stereotype);
						MessageWriter.log(
								"Stereotype '" + ster.getName() + "' was added to " + destElem.getHumanName(), logger);
						copyUWEtagsOfStereotype(srcElem, destElem, project, stereotype);
					}
				}
			}
                    }
		}
		SessionManager.getInstance().closeSession();
	}
}
