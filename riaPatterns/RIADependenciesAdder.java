package magicUWE.riaPatterns;

import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.tags.NodeTag;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.ui.DialogConstants;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.Collaboration;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.CollaborationUse;
import com.nomagic.uml2.impl.ElementsFactory;

/**
 * Class for adding dependencies for RIA Patterns.
 * 
 * 
 */
public class RIADependenciesAdder {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RIADependenciesAdder.class);

	private Project project;
	private Element element;
	private NodeTag tag;

	/**
	 * @param project
	 *            active project
	 * @param element
	 *            element to add dependencies to
	 * @param tag
	 *            RIA Pattern
	 */
	public RIADependenciesAdder(Project project, Element element, NodeTag tag) {
		this.project = project;
		this.element = element;
		this.tag = tag;
	}

	/**
	 * Adds Collaboration Use for given source Collaboration to element (if
	 * element is instance of Property, it's added to class of element)
	 * 
	 * @param sourceColl
	 * @return Collaboration Use
	 */
	private CollaborationUse addCollaborationUse(Collaboration sourceColl) {
		SessionManager.getInstance().createSession("add collaboration use");
		ElementsFactory elementsFactory = Application.getInstance().getProject().getElementsFactory();
		// create Collaboration Use instance
		CollaborationUse collUse = elementsFactory.createCollaborationUseInstance();

		collUse.setType(sourceColl);
		collUse.setName(sourceColl.getName());

		if (element instanceof Property) {
			collUse.setOwner(((Property) element).getType());
		} else {
			collUse.setOwner(element);
		}

		SessionManager.getInstance().closeSession();
		MessageWriter.log(collUse.getHumanName() + " is added", logger);

		return collUse;
	}

	/**
	 * Adds parts to given Collaboration Use with types chosen in
	 * {@link RIADependenciesPartsDialog}. Adds Dependencies between parts of
	 * collUse and parts of source Collaboration from UWE Profile
	 * 
	 * @param tableData
	 *            data of table from {@link RIADependenciesPartsDialog}
	 * @param collUse
	 *            Collaboration Use that was added by method
	 *            addCollaborationUse()
	 */
	private void addParts(Object[][] tableData, CollaborationUse collUse) {
		SessionManager.getInstance().createSession("add parts for collaboration use");
		ElementsFactory elementsFactory = Application.getInstance().getProject().getElementsFactory();

		for (int i = 0; i < tableData.length; i++) {
			Property part = elementsFactory.createPropertyInstance();
			if (element instanceof Property) {
				part.setOwner(((Property) element).getType());
			} else {
				part.setOwner(element);
			}
			part.setName(((Property) tableData[i][0]).getName());
			
			if (tableData[i][1] != null) {
				//StereotypesHelper.addStereotypes(part, StereotypesHelper.getStereotypes(((Class) tableData[i][1])));
				part.setType((Class) tableData[i][1]);
			}
			// create Dependency
			Dependency dep = elementsFactory.createDependencyInstance();
			dep.set_collaborationUseOfRoleBinding(collUse);
			ModelHelper.setClientElement(dep, part);
			ModelHelper.setSupplierElement(dep, ((Property) tableData[i][0]));
		}

		SessionManager.getInstance().closeSession();
		MessageWriter.log("Parts for " + tag.toString() + " are added", logger);
	}

	/**
	 * Adds dependencies, i.e. opens {@link RIADependenciesPartsDialog} for
	 * choosing types of parts and adds Collaboration Use with parts
	 * 
	 * @return true if dialog was canceled
	 */
	public boolean addDependencies() {
		boolean dialogCanceled = false;
		// open Dialog, add Collaboration Use, add Parts
		Collaboration coll = RIAPatternsOperations.getSourceCollaboration(project, tag);
		if (coll != null) {
			RIADependenciesPartsDialog dialog = new RIADependenciesPartsDialog(coll);
			if (dialog.getResult() == DialogConstants.OK) {
				CollaborationUse collUse = addCollaborationUse(coll);
				PartsTableModel model = (PartsTableModel) dialog.getTable().getModel();
				addParts(model.getTableData(coll), collUse);

			} else {
				dialogCanceled = true;
			}

		} else {
			MessageWriter.showMessage(
					"Dependencies can not be added because there is no Collaboration for tag " + tag.toString()
							+ " in the UWE Profile.", logger);

		}
		return dialogCanceled;
	}

}
