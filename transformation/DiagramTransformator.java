package magicUWE.transformation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

/**
 * Helper-methods for the UWE transformation feature. Transformation means
 * converting elements from one diagram into another. Please call Methods from
 * {@link TransformationType}, if you want to improve the transformation. In
 * this class only the general things should be done, that are equal at every
 * transformation. (the other way is to overwrite the general
 * TransformationType-methods)
 * 
 * 
 * @author PST LMU
 */
class DiagramTransformator {
	private static final Logger logger = Logger.getLogger(DiagramTransformator.class);
	private TransformationType transformationType;
	private final Package destPackage;

	// map of original shape class element and it transformed new element
	private HashMap<PresentationElement, PresentationElement> transformedClassElements =
			new HashMap<PresentationElement, PresentationElement>();

	// map of relation (path) and its one end element (client)
	private HashMap<PathElement, PresentationElement> path2origClient = new HashMap<PathElement, PresentationElement>();

	// map of relation and its other end element (supplier)
	private HashMap<PresentationElement, PresentationElement> path2origSuplier =
			new HashMap<PresentationElement, PresentationElement>();
	private final Diagram destDiagram;

	/**
	 * DiagramTransformator
	 * 
	 * @param fromDiagram
	 *            source
	 * @param destDiagram
	 *            target
	 * @param destPackage
	 * @param transformationType
	 *            TransformationType
	 */
	public DiagramTransformator(DiagramPresentationElement fromDiagram, Diagram destDiagram, Package destPackage,
			TransformationType transformationType) {
		if (fromDiagram == null || destDiagram == null || transformationType == null) {
			throw new IllegalArgumentException("No Diagram for Transformation.");
		}
		this.destPackage = destPackage;
		this.transformationType = transformationType;
		this.destDiagram = destDiagram;
	}

        public Package getPackage()
        {
            return(destPackage);
        }

	/**
	 * // * transform class element. The transformation logic of the according
	 * {@link TransformationType} is used.
	 * 
	 * @param origClass
	 * @return new, transformed class element
	 */
	Class transformClass(PresentationElement origClass) {
		// CREATE new class instance
		Class newClass = Application.getInstance().getProject().getElementsFactory().createClassInstance();

		// set some PROPERTIES of the new class
		newClass.setOwner(destPackage);
		newClass.setName(origClass.getName());

		// add transformed stereotype(s)
		transformationType.addConvertedStereotypeToClass(origClass, newClass);

		try {
			ShapeElement shape = setClassLayout(origClass, newClass);

			// UPDATE the HashMaps
			// pairs of old class elements and the new view
			transformedClassElements.put(origClass, shape);

			// set path maps if there is a association from/to this class
			// we need all path elements from one class to another
			List<PathElement> paths = ((ShapeElement) origClass).getConnectedPathElements();
			for (PathElement path : paths) {
				path2origSuplier.put(path, path.getSupplier());
				path2origClient.put(path, path.getClient());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageWriter.showError("Something went wrong while transforming the classes", logger);
		}
		return newClass;
	}

	/**
	 * set class layout, means create a new PresentationElement with the same
	 * borders than the old. Add it to {@link DiagramTransformator#destDiagram}
	 * 
	 * @param origClass
	 * @param givenClass != null
	 * @return shape
	 */
	ShapeElement setClassLayout(PresentationElement origClass, Class givenClass) {
		ShapeElement shape = null;
		try {
			// set some LAYOUT properties of the given class
			shape =
					PresentationElementsManager.getInstance().createShapeElement(givenClass,
							Application.getInstance().getProject().getDiagram(destDiagram));
			// set the proper display location in the new diagram
			Rectangle bounds = origClass.getBounds();
			PresentationElementsManager.getInstance().reshapeShapeElement(shape, bounds);
		} catch (Exception e) {
			e.printStackTrace();
			MessageWriter.log("ERROR: Something went wrong while copying PresentationElements of the classes", logger);
		}
		return shape;
	}

	/**
	 * transform association. The transformation logic of the according
	 * {@link TransformationType} is used.
	 * 
	 * @param origAssoc
	 * @return thisAssociationWasTransformed
	 */
	boolean transformAssociation(PresentationElement origAssoc) {
		boolean thisAssociationWasTransformed = false;
		PathElement pathEl = (PathElement) origAssoc;
		// get the new client
		PresentationElement origClient = path2origClient.get(pathEl);
		PresentationElement newClient = transformedClassElements.get(origClient);
		// get the new supplier
		PresentationElement origSuplier = path2origSuplier.get(pathEl);
		PresentationElement newSuplier = transformedClassElements.get(origSuplier);

		if (origClient != null && newClient != null && origSuplier != null && newSuplier != null) {
			// CREATE new association instance
			Association newAss =
					Application.getInstance().getProject().getElementsFactory().createAssociationInstance();

			// set some PROPERTIES of the new association
			newAss.setOwner(destPackage);
			newAss.setName(origAssoc.getName());

			try {
				// set transformed stereotype(s)
				transformationType.addConvertedStereotypeToAssociation(origAssoc, newAss);

				// set the association client
				ModelHelper.setClientElement(newAss, newClient.getElement());
				// set the association supplier
				ModelHelper.setSupplierElement(newAss, newSuplier.getElement());

				// retrieve the association properties on both its ends
				List<Property> newMemberEnds = newAss.getMemberEnd();
				Association originalAssociation = (Association) origAssoc.getElement();
				List<Property> origMemberEnds = new ArrayList<Property>();
				if (originalAssociation.hasMemberEnd()) {
					origMemberEnds = originalAssociation.getMemberEnd();
				}

				// set the aggregation type etc of the new association-ends
				if (origMemberEnds != null && origMemberEnds.size() == 2 && newMemberEnds != null
						&& newMemberEnds.size() == 2) {
					for (int i = 0; i < origMemberEnds.size(); i++) {
						Property origProperty = origMemberEnds.get(i);
						Property newProperty = newMemberEnds.get(i);

						newProperty.setAggregation(origMemberEnds.get(i).getAggregation());
						// and set navigable like the original
						ModelHelper.setNavigable(newProperty, origProperty.isNavigable());
						// set client multiplicity
						ModelHelper.setMultiplicity(ModelHelper.getMultiplicity(origProperty), newProperty);
						newProperty.setName(origProperty.getName());
					}
				}

				// LAYOUT
				// create new path element for the new association with its
				// client and supplier
				PathElement newAssPath =
						PresentationElementsManager.getInstance().createPathElement(newAss, newClient, newSuplier);

				newAssPath.setBounds(pathEl.getBounds());
				PresentationElementsManager.getInstance().resetLabelPositions(newAssPath);
				thisAssociationWasTransformed = true;
			} catch (Exception e) {
				e.printStackTrace();
				MessageWriter.showError("Something went wrong while transforming the associations", logger);
			}
		}
		return thisAssociationWasTransformed;
	}
}
