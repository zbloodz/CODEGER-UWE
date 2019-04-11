package magicUWE.actions.context;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeAssoc;
import magicUWE.stereotypes.UWEStereotypeClassNav;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.AssociationView;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.AggregationKindEnum;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.impl.ElementsFactory;

/**
 * This class is for automatic insertion of class elements into an existing UWE
 * navigation diagram.
 * 
 * @author PST LMU
 * 
 */

public class NavDiagramContextAssocAction extends DefaultDiagramAction {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(NavDiagramContextAssocAction.class);
	private UWEStereotypeClassNav insertAClassOfThisType;

	/**
	 * Constructs a diagram context action
	 * 
	 * @param name
	 * @param keyStroke
	 * @param group
	 * @param insertAClassOfThisType
	 */
	public NavDiagramContextAssocAction(String name, KeyStroke keyStroke, String group,
			UWEStereotypeClassNav insertAClassOfThisType) {
		super(name, name, keyStroke, group);
		this.insertAClassOfThisType = insertAClassOfThisType;
	}

	/**
	 * Inserting a new Menu or Index or Query into the diagram. Can only be
	 * successful if all selected associations have the same class at one end.
	 */
	@Override
	@SuppressWarnings( {"null" })
	public void actionPerformed(ActionEvent event) {
		final String SAME_CLASS_ERROR = "The selected associations must all have the same class at one end";

		// get selected PresentationElements
		List<PresentationElement> selectedElements = this.getSelected();

		// warn the user if it's not the right diagram type
		// normally no dialog (because of the kind of menu navigation)
		boolean bothDirectionsAvailable = true;
		if (UWEDiagramType.getDiagramType(Application.getInstance().getProject().getActiveDiagram().getDiagram()) == UWEDiagramType.NAVIGATION
				|| MessageWriter
						.showQuestionOkCancel(
								"This is not a Navigation Diagram, do you really want to insert the selected Navigation element?",
								logger)) {
			if (selectedElements.size() >= 1) {
				/***************************************************************
				 * calculate how the new class can be inserted. Build HashMap
				 * with association to integer (false <=> head class in Client).
				 * all associations must be connected with the same class at one
				 * end they have to be accessible towards the different targets
				 **************************************************************/

				// be careful when changing this code, it's a bit tricky :)
				HashMap<AssociationView, Boolean> assocs = new HashMap<AssociationView, Boolean>();
				AssociationView firstAssoc = null;

				// false <=> the first assocEnd is the one which
				// contains always the same class
				boolean firstAssocSwitched = false;
				for (PresentationElement ass : selectedElements) {
					// ignore all non-associations
					if (ass instanceof AssociationView) {

						List<Property> ends = ((Association) ass.getElement()).getMemberEnd();
						// all associations must have two ends
						if (ends.size() != 2) {
							MessageWriter.showError("Some strange association is selected (without 2 ends),"
									+ "\nplease only select normal associations", logger);
							return;
						}

						if (assocs.isEmpty()) {
							// first assoc, do not look at the order of the ends
							// Therefore ignore the "may be null"-warnings in
							// the else-case
							assocs.put((AssociationView) ass, false);
							firstAssoc = (AssociationView) ass;
						} else {
							// same head-class at the saved assoc end
							PathElement origPath = (PathElement) ass;

							if (!firstAssocSwitched) { // compare client
								// Test if there are two ways of insertion
								if (firstAssoc.getClient() == origPath.getClient()
										&& firstAssoc.getSupplier() == origPath.getSupplier()) {
									assocs.put((AssociationView) ass, false);
								} else if ((firstAssoc.getClient() == origPath.getSupplier() && firstAssoc
										.getSupplier() == origPath.getClient())) {
									assocs.put((AssociationView) ass, true);
								} else {
									// Test the possible directions
									if (firstAssoc.getClient() == origPath.getClient()) {
										assocs.put((AssociationView) ass, false);
									} else if (firstAssoc.getClient() == origPath.getSupplier()) {
										assocs.put((AssociationView) ass, true);
									}
									// no success so far: If both directions are
									// still available, try to take the other
									// class as head-class
									else if (bothDirectionsAvailable
											&& firstAssoc.getSupplier() == origPath.getClient()) {
										firstAssocSwitched = true;
										// change previous values
										changeAllBooleansInHashMap(assocs);

										assocs.put((AssociationView) ass, false);
									} else if (bothDirectionsAvailable
											&& firstAssoc.getSupplier() == origPath.getSupplier()) {
										firstAssocSwitched = true;
										// change previous values
										changeAllBooleansInHashMap(assocs);

										assocs.put((AssociationView) ass, true);
									} else {
										MessageWriter.showError(SAME_CLASS_ERROR, logger);
										return;
									}
									// there is only one direction possible
									bothDirectionsAvailable = false;
								}
							} else {
								// first element is switched, compare supplier
								if (firstAssoc.getSupplier() == origPath.getClient()) {
									assocs.put((AssociationView) ass, false);
								} else if (firstAssoc.getSupplier() == origPath.getSupplier()) {
									assocs.put((AssociationView) ass, true);
								} else {
									MessageWriter.showError(SAME_CLASS_ERROR, logger);
									return;
								}
							}
						}
					}
				}

				/***************************************************************
				 * LOGIC for the different class types (determinable before the
				 * direction is known)
				 **************************************************************/
				boolean compositionAtTheHeadClassEnd = false;

				// Composition at the association end near the head class
				// (we must later ask for the direction even when only one
				// assoc was selected)
				if (insertAClassOfThisType == UWEStereotypeClassNav.MENU) {
					compositionAtTheHeadClassEnd = true;
				}

				if (assocs.size() == 1) {
					for (AssociationView origPath : assocs.keySet()) {
						if (((Association) origPath.getElement()).hasMemberEnd()) {
							List<Property> memberEnds = ((Association) origPath.getElement()).getMemberEnd();
							if (memberEnds.size() == 2) {

								// Multiplicity copying part 1
								// Logic for inserting an INDEX class warn the
								// user when insertAClassOfThisType is index,
								// that one multiplicity should contain *
								if (insertAClassOfThisType == UWEStereotypeClassNav.INDEX) {
									if (ModelHelper.getMultiplicity(memberEnds.get(0)).contains("*")
											&& ModelHelper.getMultiplicity(memberEnds.get(1)).contains("*")) {
										// let the user decide the wanted
										// direction
									} else if (ModelHelper.getMultiplicity(memberEnds.get(0)).contains("*")) {
										// direction is clear now
										bothDirectionsAvailable = false;
									} else if (ModelHelper.getMultiplicity(memberEnds.get(1)).contains("*")) {
										assocs.put(origPath, true);
										// direction is clear now
										bothDirectionsAvailable = false;
									} else {
										if (MessageWriter.showQuestion(
												"One end of the association should have a multiplicity including \"*\"."
														+ "\nDo you want to cancel this operation?", logger)) {
											return;
										}
									}
								}

							}
						}
					}
				} else {
					if ((insertAClassOfThisType == UWEStereotypeClassNav.INDEX
							|| insertAClassOfThisType == UWEStereotypeClassNav.QUERY || insertAClassOfThisType == UWEStereotypeClassNav.GUIDEDTOUR)
							&& MessageWriter.showQuestion(
									"Selecting more than one association is not useful for a new "
											+ insertAClassOfThisType.toString() + " class.\n"
											+ "Do you want to cancel this operation?", logger)) {
						return;
					}
				}

				/***************************************************************
				 * decide which direction is used, if both are possible for
				 * inserting the new element
				 **************************************************************/
				if (bothDirectionsAvailable) {
					if (MessageWriter.showQuestion("Do you want to navigate from \""
							+ firstAssoc.getClient().getHumanName() + "\" to the inserted class?"
							+ "\n(No navigates from \"" + firstAssoc.getSupplier().getHumanName()
							+ "\" to the inserted class)", logger)) {
						// this is the default configuration, change nothing
					} else {
						changeAllBooleansInHashMap(assocs);
					}
				}

				/***************************************************************
				 * LOGIC for the different class types (determinable after the
				 * direction is known)
				 **************************************************************/

				String nameForInsertedElement = null;

				// only one association is selected
				if (assocs.size() == 1) {
					for (AssociationView origPath : assocs.keySet()) {
						if (((Association) origPath.getElement()).hasMemberEnd()) {
							List<Property> memberEnds = ((Association) origPath.getElement()).getMemberEnd();
							if (memberEnds.size() == 2) {

								// Logic for the names of the inserted class
								if (assocs.get(origPath)) {
									nameForInsertedElement = memberEnds.get(1).getName();
									memberEnds.get(1).setName("");
								} else {
									nameForInsertedElement = memberEnds.get(0).getName();
									memberEnds.get(0).setName("");
								}
								if (!nameForInsertedElement.equals("") && nameForInsertedElement.length() > 0) {
									// use an upper case letter at the first
									// position of the name
									nameForInsertedElement =
											nameForInsertedElement.substring(0, 1).toUpperCase()
													+ nameForInsertedElement.substring(1).toLowerCase();
								}

								// add a * if no one is there
								if (insertAClassOfThisType == UWEStereotypeClassNav.INDEX) {
									if (assocs.get(origPath)
											&& ModelHelper.getMultiplicity(memberEnds.get(1)).equals("")) {
										ModelHelper.setMultiplicity("*", memberEnds.get(1));
									} else if (!assocs.get(origPath)
											&& ModelHelper.getMultiplicity(memberEnds.get(0)).equals("")) {
										ModelHelper.setMultiplicity("*", memberEnds.get(0));
									}
								}

							}
						}
					}
				}

				/***************************************************************
				 * do the changes
				 **************************************************************/
				SessionManager.getInstance().createSession("Inserting new element between the selected associations");
				ShapeElement newInsertedShape = null;
				for (AssociationView origPath : assocs.keySet()) {

					Association origAssociation = (Association) origPath.getElement();

					// change to normal association
					// (but don't change the direction of the association)
					List<Property> properties = origAssociation.getMemberEnd();
					for (Property property : properties) {
						property.setAggregation(AggregationKindEnum.NONE);
					}

					if (newInsertedShape == null) {
						// Insert Class Element
						newInsertedShape =
								insertElement(origPath.getSupplier(), origPath.getClient(), nameForInsertedElement);
						// association creation and adaption
						if (assocs.get(origPath)) {
							createAndAddaptAssociation(origAssociation, origPath.getSupplier(), newInsertedShape,
									compositionAtTheHeadClassEnd);
						} else {
							createAndAddaptAssociation(origAssociation, origPath.getClient(), newInsertedShape,
									compositionAtTheHeadClassEnd);
						}
					}
					// reassign the association end from head class to
					// inserted class
					reassignAssociation(origAssociation, origPath, newInsertedShape, assocs.get(origPath));
				}
				SessionManager.getInstance().closeSession();
			} else {
				// should not happen (because of the kind of menu navigation)
				MessageWriter.showError("Please select associations!", logger);
			}
		}
	}

	/**
	 * change all booleans of the given hash map values
	 * 
	 * @param assocs
	 */
	private void changeAllBooleansInHashMap(HashMap<AssociationView, Boolean> assocs) {
		for (AssociationView pe : assocs.keySet()) {
			// change all directions
			assocs.put(pe, (assocs.get(pe) ? false : true));
		}
	}

	/**
	 * create association and connect it with the given PresentationElements
	 * 
	 * @param origAssociation
	 * @param origSupplier
	 * @param recentlyInsertedShape
	 * @param compositionAtTheHeadClassEnd
	 */
	private void createAndAddaptAssociation(Association origAssociation, PresentationElement origSupplier,
			PresentationElement recentlyInsertedShape, boolean compositionAtTheHeadClassEnd) {
		// create the new association
		Association newAsssociation =
				Application.getInstance().getProject().getElementsFactory().createAssociationInstance();
		newAsssociation.setOwner(origAssociation.getOwner());

		ModelHelper.setClientElement(newAsssociation, recentlyInsertedShape.getElement());
		ModelHelper.setSupplierElement(newAsssociation, origSupplier.getElement());

		// use no stereotypes if we have a composition
		if (!compositionAtTheHeadClassEnd) {
			// copy all stereotypes from an old association to the new one
			StereotypesHelper.addStereotypes(newAsssociation, StereotypesHelper.getStereotypes(origAssociation));

			// add the adequate stereotypes
			changeAssociationStereotypeAccordingToTheUWERules(newAsssociation);
		}

		try {
			// create new path element for the new association with its
			// client and supplier and set it navigable at both ends
			PresentationElementsManager.getInstance().createPathElement(newAsssociation, recentlyInsertedShape,
					origSupplier);
			List<Property> assocProperties = newAsssociation.getMemberEnd();
			logger.debug("assocProperties size: " + assocProperties.size());
			if (assocProperties.size() > 0) {
				for (Property property : assocProperties) {
					ModelHelper.setNavigable(property, true);
				}
			}
			if (assocProperties.size() == 2) {
				// set composition
				if (compositionAtTheHeadClassEnd) {
					assocProperties.get(1).setAggregation(AggregationKindEnum.COMPOSITE);
				}
			}
		} catch (ReadOnlyElementException e) {
			MessageWriter.showError("Association is read only", logger);
			e.printStackTrace();
		}
	}

	/**
	 * reassign the association end to inserted class and use the right
	 * stereotype.
	 * 
	 * @param origAssociation
	 * @param origPath
	 * @param recentlyInsertedShape
	 * @param setSupplier
	 */
	private void reassignAssociation(Association origAssociation, PathElement origPath,
			PresentationElement recentlyInsertedShape, boolean setSupplier) {
		// modify the existing association
		changeAssociationStereotypeAccordingToTheUWERules(origAssociation);

		// connect the association with the new class
		if (setSupplier) {
			// the next line cannot be undone (MagicDraw bug?) => the
			// association keeps existing, but is not shown in the diagram
			ModelHelper.setSupplierElement(origAssociation, recentlyInsertedShape.getElement());
			origPath.setSupplier(recentlyInsertedShape);
		} else {
			ModelHelper.setClientElement(origAssociation, recentlyInsertedShape.getElement());
			origPath.setClient(recentlyInsertedShape);
		}
	}

	/**
	 * Logic for the association-Stereotypes. Change association stereotypes
	 * according to the UWE rules, that means if the inserted element is a
	 * Query, use processLink stereotypes and eventually remove the
	 * navigationLink stereotypes. If the inserted element is not a Query, use
	 * natigationLinks and remove processLink-stereotypes.
	 * 
	 * @param origAssociation
	 */
	private void changeAssociationStereotypeAccordingToTheUWERules(Association origAssociation) {
		if (insertAClassOfThisType == UWEStereotypeClassNav.QUERY) {
			// QUERY: add processLink
			if (!(MagicDrawElementOperations.hasStereotype(origAssociation, UWEStereotypeAssoc.PROCESS_LINK.toString()))) {
				MagicDrawElementOperations.addStereotypeToElement(origAssociation, UWEStereotypeAssoc.PROCESS_LINK
						.toString());
			}
			// and eventually remove navigationLink
			MagicDrawElementOperations.tryToRemoveStereotypeFromElement(origAssociation,
					UWEStereotypeAssoc.NAVIGATION_LINK.toString());
		}
		// NO QUERY: add navigationLink and remove ProcessLink
		else {
			if (!(MagicDrawElementOperations.hasStereotype(origAssociation, UWEStereotypeAssoc.NAVIGATION_LINK
					.toString()))) {
				MagicDrawElementOperations.addStereotypeToElement(origAssociation, UWEStereotypeAssoc.NAVIGATION_LINK
						.toString());
			}
			// and eventually remove processLink
			MagicDrawElementOperations.tryToRemoveStereotypeFromElement(origAssociation,
					UWEStereotypeAssoc.PROCESS_LINK.toString());
		}
	}

	/**
	 * This method inserts the new element at the proper place between the given
	 * elements into the diagram.
	 * 
	 * @param origSupplier
	 * @param origClient
	 * @param nameForInsertedElement
	 * @return ShapeElement of new created class
	 */
	private ShapeElement insertElement(PresentationElement origSupplier, PresentationElement origClient,
			String nameForInsertedElement) {
		// create the view of newly created class element
		try {
			ElementsFactory elementsFactory = Application.getInstance().getProject().getElementsFactory();
			Class newClass = elementsFactory.createClassInstance();
			PresentationElementsManager presentationElementsManager = PresentationElementsManager.getInstance();
			newClass.setOwner(origSupplier.getElement().getOwner());

			// set name
			if (nameForInsertedElement != null) {
				newClass.setName(nameForInsertedElement);
			}

			// set stereotype
			MagicDrawElementOperations.addStereotypeToElement(newClass, insertAClassOfThisType.toString());

			ShapeElement newShape =
					presentationElementsManager.createShapeElement(newClass, Application.getInstance().getProject()
							.getActiveDiagram());
			// set the class view
			if (newShape != null) {
				// set the proper display location in the new diagram
				Rectangle bounds = origSupplier.getBounds();
				int x = 0;
				if (origClient.getMiddlePointX() > origSupplier.getMiddlePointX()) {
					x =
							origClient.getMiddlePointX()
									- ((origClient.getMiddlePointX() - origSupplier.getMiddlePointX()) / 2);
				} else {
					x =
							origSupplier.getMiddlePointX()
									- ((origSupplier.getMiddlePointX() - origClient.getMiddlePointX()) / 2);
				}
				bounds.x = x;
				int y = 0;
				if (origClient.getMiddlePointY() > origSupplier.getMiddlePointY()) {
					y =
							origClient.getMiddlePointY()
									- ((origClient.getMiddlePointY() - origSupplier.getMiddlePointY()) / 2);
				} else {
					y =
							origSupplier.getMiddlePointY()
									- ((origSupplier.getMiddlePointY() - origClient.getMiddlePointY()) / 2);
				}
				bounds.y = y;
				logger.debug("x: " + x + " y: " + y);
				presentationElementsManager.reshapeShapeElement(newShape, bounds);

				return newShape;
			}
		} catch (ReadOnlyElementException e) {
			MessageWriter.showError("Element is read only", logger);
			e.printStackTrace();
		}
		return null;
	}
}
