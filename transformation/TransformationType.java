/**
 * 
 */
package magicUWE.transformation;

import java.util.HashMap;
import java.util.List;
import java.awt.Point;
import java.util.ArrayList;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeAssoc;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeClassPres;
import magicUWE.actions.context.requirements.TransformationsListener;
import magicUWE.transformation.requirements.*;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.AssociationView;
import com.nomagic.magicdraw.uml.symbols.shapes.ClassView;
import com.nomagic.magicdraw.uml.symbols.shapes.NoteView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ControlFlow;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.DecisionNode;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;

/**
 * Diagram transformation types and some basic actions.
 * 
 * @author PST LMU
 */
public enum TransformationType
{
        REQUIREMENTS_2_CONTENT("Requirements -> Content", UWEDiagramType.USE_CASE, UWEDiagramType.CONTENT) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Application.getInstance().getGUILog().log("MagicUWE: Transformation \""+this.toString()+" \": Creating Diagram...");
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);

                        TransformationsListener.getListener().resetListeners();
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram)
                {
                    UWEDiagramType.CONTENT.getModelOrCreateIt(Application.getInstance().getProject(), null, false);
                    
                    ArrayList<NamedElement> listCandidates=ReqToConTransformations.getContentClassCandidates(true);
                    ArrayList<ElementCollector.ReturnElement> listElements=new ArrayList<ElementCollector.ReturnElement>();
                    for (int i=0;i<listCandidates.size();i++)
                    {
                        Point posTemp=new Point(50+50*i,50);
                        listElements.add(ReqToConTransformations.createContentClass(listCandidates.get(i).getName(), posTemp, diagramTransformator.getPackage()));
                        this.numberOfTransformedClasses++;
                    }

                    for (int i=0;i<listElements.size();i++)
                    {
                        numberOfTransformedAttributes+=ReqToConTransformations.addAttributesToContentClass(listElements.get(i).cClass,true);
                        numberOfTransformedOperation+=ReqToConTransformations.addOperationToContentClass(listElements.get(i).cClass,true);
                        numberOfTransformedAssociations+=ReqToConTransformations.addAssociationsToContentClass(listElements.get(i).cClass,listElements.get(i).cShape);
                    }

                    ElementCollector.removeCollisions(listElements,ReqToConTransformations.iMIN_DISTANCE_X,ReqToConTransformations.iMIN_DISTANCE_Y);
                    numberOfTransformedClasses=listElements.size();
		}

		@Override
		public void launchAssociationTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Associations
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName)
                {
                    MessageWriter.log("Transformation of \""
                        + this.toString()
                        + " done: "
                        + (this.numberOfTransformedClasses == 0 ? "No elements were" : this.numberOfTransformedClasses
                            + (this.numberOfTransformedClasses > 1 ? " classes" : " class")
                            + ", "
                            + this.numberOfTransformedAttributes
                            + (this.numberOfTransformedAttributes > 1 ? " attributes " : " attribute ")
                            + " and "
                             + this.numberOfTransformedOperation
                            + (this.numberOfTransformedOperation > 1 ? " operations " : " operation ")
                            + " and "
                            + this.numberOfTransformedAssociations
                            + (this.numberOfTransformedAssociations > 1 ? " associations " : " association ")
                            + ((this.numberOfTransformedAttributes
                                + this.numberOfTransformedAssociations
                                + this.numberOfTransformedClasses) > 1 ? "were" : "was")) + " transformed.", logger);
		}
	},
        REQUIREMENTS_2_NAVIGATION("Requirements -> Navigation", UWEDiagramType.USE_CASE, UWEDiagramType.NAVIGATION) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage)
                {
			// create a new diagram
                        Application.getInstance().getGUILog().log("MagicUWE: Transformation \""+this.toString()+" \": Creating Diagram...");
			Diagram destDiagram=this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);
			this.diagramTransformator=new DiagramTransformator(sourceDiagram,destDiagram,destPackage,this);

                        TransformationsListener.getListener().resetListeners();
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram)
                {
                    UWEDiagramType.NAVIGATION.getModelOrCreateIt(Application.getInstance().getProject(), null, false);
                    UWEDiagramType.PROCESS_STRUCTURE.getModelOrCreateIt(Application.getInstance().getProject(), null, false);
                    
                    // Transform all Classes
                    ArrayList<NamedElement> listNavCandidates=ReqToNavTransformations.getNavigationClassCandidates(true);
                    ArrayList<NamedElement> listProCandidates=ReqToNavTransformations.getProcessClassCandidates(true);

                    ArrayList<ElementCollector.ReturnElement> listNavElements=new ArrayList<ElementCollector.ReturnElement>();
                    ArrayList<ElementCollector.ReturnElement> listProElements=new ArrayList<ElementCollector.ReturnElement>();
                    ArrayList<ElementCollector.ReturnElement> listElements=new ArrayList<ElementCollector.ReturnElement>();

                    Package packNavigation=null;
                    if (StereotypesHelper.hasStereotype(diagramTransformator.getPackage(),UWEDiagramType.NAVIGATION.modelStereotype))
                    {
                        packNavigation=diagramTransformator.getPackage();
                    }
                    else
                    {
                        ArrayList<NamedElement> modelsNavigation=ElementCollector.getNamedElements(UWEDiagramType.NAVIGATION.modelStereotype, null, Model.class, false, true);
                        if (!modelsNavigation.isEmpty())
                        {
                            packNavigation=((Model)(modelsNavigation.get(0)));
                        }
                    }

                    Package packProcess=null;
                    if (StereotypesHelper.hasStereotype(diagramTransformator.getPackage(),UWEDiagramType.PROCESS_STRUCTURE.modelStereotype))
                    {
                        packProcess=diagramTransformator.getPackage();
                    }
                    else
                    {
                        ArrayList<NamedElement> modelsProcess=ElementCollector.getNamedElements(UWEDiagramType.PROCESS_STRUCTURE.modelStereotype, null, Model.class, false, true);
                        if (!modelsProcess.isEmpty())
                        {
                            packProcess=((Model)(modelsProcess.get(0)));
                        }
                    }

                    ArrayList<NamedElement> modelsRequirements=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, true, true);
                    ArrayList<NamedElement> listUseCases=ElementCollector.getNamedElements(null, null, UseCase.class, true, modelsRequirements, false);
                    int iI=0;
                    for (int i=0;i<listNavCandidates.size();i++)
                    {

                        Point tempPoint=new Point(50+50*iI/5,50+50*iI%5);
                        int iElement=ElementCollector.getNamedElementFromArrayList(listUseCases, listNavCandidates.get(i).getName(), true, true);
                        if ((iElement!=ElementCollector.iNO_ELEMENT)&&(elementsInDiagram.get(listUseCases.get(iElement))!=null)&&
                            (elementsInDiagram.get(listUseCases.get(iElement)).size()>0))
                        {
                            tempPoint=new Point(elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().x,
                                elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().y);
                        }
                        listNavElements.add(ReqToNavTransformations.
                                createNavigationClass(listNavCandidates.get(i).getName(), tempPoint, packNavigation));
                        iI++;
                    }
                    for (int i=0;i<listProCandidates.size();i++)
                    {
                        Point tempPoint=new Point(50+50*iI/5,50+50*iI%5);
                        int iElement=ElementCollector.getNamedElementFromArrayList(listUseCases, listProCandidates.get(i).getName(), true, true);
                        if ((iElement!=ElementCollector.iNO_ELEMENT)&&(elementsInDiagram.get(listUseCases.get(iElement))!=null)&&
                            (elementsInDiagram.get(listUseCases.get(iElement)).size()>0))
                        {
                            tempPoint=new Point(elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().x,
                                elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().y);
                        }
                        listProElements.add(ReqToProTransformations.
                                createProcessClass(listProCandidates.get(i).getName(), tempPoint, packProcess));
                        iI++;
                    }
                    listElements.addAll(listNavElements);
                    listElements.addAll(listProElements);

                    for (int i=0;i<listNavElements.size();i++)
                    {
                        ElementCollector.ReturnElement retMenu=ReqToNavTransformations.addNavigationClassAssociations(listNavElements.get(i).cClass, listNavElements.get(i).cShape, true);
                        if (retMenu.cClass!=null)
                        {
                            listElements.add(retMenu);
                        }
                        if ((retMenu.additionalClasses!=null)&&(retMenu.additionalShapes!=null))
                        {
                            for (int a=0;a<retMenu.additionalClasses.size();a++)
                            {
                                if (retMenu.additionalShapes.size()>a)
                                {
                                    listElements.add(new ElementCollector.ReturnElement(retMenu.additionalClasses.get(a),
                                        retMenu.additionalShapes.get(a), 0, null, null));
                                }
                            }
                        }
                        numberOfTransformedAssociations+=retMenu.iChildren;
                    }
                    for (int i=0;i<listProElements.size();i++)
                    {
                        ElementCollector.ReturnElement retAdds=ReqToNavTransformations.addProcessClassAssociations(listProElements.get(i).cClass, listProElements.get(i).cShape);
                        
                        if ((retAdds.additionalClasses!=null)&&(retAdds.additionalShapes!=null))
                        {
                            for (int a=0;a<retAdds.additionalClasses.size();a++)
                            {
                                if (retAdds.additionalShapes.size()>a)
                                {
                                    listElements.add(new ElementCollector.ReturnElement(retAdds.additionalClasses.get(a),
                                        retAdds.additionalShapes.get(a), 0, null, null));
                                }
                            }
                        }
                        numberOfTransformedAssociations+=retAdds.iChildren;
                    }

                    Point tempPoint=new Point(20,40);
                    ElementCollector.ReturnElement cHome=ReqToNavTransformations.createHome(tempPoint, diagramTransformator.getPackage());
                    listElements.add(cHome);
                    ElementCollector.ReturnElement retMenu=ReqToNavTransformations.addHomeAssociations(cHome.cClass, cHome.cShape, true, listNavElements, listProElements);
                    if (retMenu.cClass!=null)
                    {
                        listElements.add(retMenu);
                    }
                    numberOfTransformedAssociations+=retMenu.iChildren;

                    for (int i=listElements.size()-1;i>=0;i--)
                    {
                        for (int o=0;o<i;o++)
                        {
                            if (listElements.get(i).cClass==listElements.get(o).cClass)
                            {
                                listElements.remove(i);
                                break;
                            }
                        }
                    }
                    
                    ElementCollector.removeCollisions(listElements,ReqToNavTransformations.iMIN_DISTANCE_X,ReqToNavTransformations.iMIN_DISTANCE_Y);

                    numberOfTransformedClasses=listElements.size();
		}

		@Override
		public void launchAssociationTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram)
                {
			// Transform all Associations
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName)
                {
                    MessageWriter.log("Transformation of \""
                        + this.toString()
                        + " done: "
                        + (this.numberOfTransformedClasses == 0 ? "No elements were" : this.numberOfTransformedClasses
                            + (this.numberOfTransformedClasses > 1 ? " classes" : " class")
                            + " and "
                            + this.numberOfTransformedAssociations
                            + (this.numberOfTransformedAssociations > 1 ? " associations " : " association ")
                            + ((this.numberOfTransformedAssociations
                                + this.numberOfTransformedAssociations
                                + this.numberOfTransformedClasses) > 1 ? "were" : "was")) + " transformed.", logger);
		}
        },
        REQUIREMENTS_2_PROCESS("Requirements -> Process", UWEDiagramType.USE_CASE, UWEDiagramType.PROCESS_STRUCTURE) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Application.getInstance().getGUILog().log("MagicUWE: Transformation \""+this.toString()+" \": Creating Diagram...");
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);

                        TransformationsListener.getListener().resetListeners();
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram)
                {
                    UWEDiagramType.PROCESS_STRUCTURE.getModelOrCreateIt(Application.getInstance().getProject(), null, false);
                    ArrayList<NamedElement> acProCandidates=ReqToNavTransformations.getProcessClassCandidates(true);
                    
                    ArrayList<ElementCollector.ReturnElement> listProElements=new ArrayList<ElementCollector.ReturnElement>();
                    ArrayList<ElementCollector.ReturnElement> listElements=new ArrayList<ElementCollector.ReturnElement>();

                    Package packProcess=null;
                    if (StereotypesHelper.hasStereotype(diagramTransformator.getPackage(),UWEDiagramType.PROCESS_STRUCTURE.modelStereotype))
                    {
                        packProcess=diagramTransformator.getPackage();
                    }
                    else
                    {
                        ArrayList<NamedElement> modelsProcess=ElementCollector.getNamedElements(UWEDiagramType.PROCESS_STRUCTURE.modelStereotype, null, Model.class, false, true);
                        if (!modelsProcess.isEmpty())
                        {
                            packProcess=((Model)(modelsProcess.get(0)));
                        }
                    }

                    ArrayList<NamedElement> modelsRequirements=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, true, true);
                    ArrayList<NamedElement> listUseCases=ElementCollector.getNamedElements(null, null, UseCase.class, true, modelsRequirements, false);
                    int iI=0;
                    for (int i=0;i<acProCandidates.size();i++)
                    {
                        Point posTemp=new Point(50+50*iI/5,50+50*iI%5);
                        int iElement=ElementCollector.getNamedElementFromArrayList(listUseCases, acProCandidates.get(i).getName(), true, true);
                        if ((iElement!=ElementCollector.iNO_ELEMENT)&&(elementsInDiagram.get(listUseCases.get(iElement))!=null)&&
                            (elementsInDiagram.get(listUseCases.get(iElement)).size()>0))
                        {
                            posTemp=new Point(elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().x,
                                elementsInDiagram.get(listUseCases.get(iElement)).get(0).getBounds().y);
                        }
                        listProElements.add(ReqToProTransformations.
                                createProcessClass(acProCandidates.get(i).getName(), posTemp, packProcess));
                        this.numberOfTransformedClasses++;
                        iI++;
                    }
                    listElements.addAll(listProElements);

                    for (int i=0;i<listProElements.size();i++)
                    {
                        if (ReqToProTransformations.addWorkflow(listProElements.get(i).cClass))
                        {
                            this.numberOfTransformedAssociations++;
                        }
                    }

                    
                    ElementCollector.removeCollisions(listElements,ReqToProTransformations.iMIN_DISTANCE_X,ReqToProTransformations.iMIN_DISTANCE_Y);
		}

		@Override
		public void launchAssociationTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Associations
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName)
                {
                    MessageWriter.log("Transformation of \""
                        + this.toString()
                        + " done: "
                        + (this.numberOfTransformedClasses == 0 ? "No elements were" : this.numberOfTransformedClasses
                            + (this.numberOfTransformedClasses > 1 ? " classes" : " class")
                            + " and "
                            + this.numberOfTransformedAssociations
                            + (this.numberOfTransformedAssociations > 1 ? " workflows " : " workflow ")
                            + ((this.numberOfTransformedAssociations
                                + this.numberOfTransformedClasses) > 1 ? "were" : "was")) + " transformed.", logger);
		}
	},
        
        REQUIREMENTS_2_PRESENTATION("Requirements -> Presentation", UWEDiagramType.USE_CASE, UWEDiagramType.PRESENTATION) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Application.getInstance().getGUILog().log("MagicUWE: Transformation \""+this.toString()+" \": Creating Diagram...");
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);

                        TransformationsListener.getListener().resetListeners();
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram)
                {
                    UWEDiagramType.PRESENTATION.getModelOrCreateIt(Application.getInstance().getProject(), null, false);
                    ArrayList<NamedElement> modelsRequirements=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
                    ArrayList<NamedElement> listUseCases=ElementCollector.getNamedElements(null, null, UseCase.class, false, modelsRequirements, false);
                    
                    ArrayList<ElementCollector.ReturnElement> listElements=new ArrayList<ElementCollector.ReturnElement>();
                    for (int i=0;i<listUseCases.size();i++)
                    {
                        Point posTemp=new Point(50+50*i,50);
                        if ((StereotypesHelper.hasStereotype(listUseCases.get(i),UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString()))||
                            (StereotypesHelper.hasStereotype(listUseCases.get(i).getOwner(),UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString())))
                        {
                            listElements.add(ReqToPreTransformations.createPresentationClass(listUseCases.get(i).getName(),posTemp,diagramTransformator.getPackage(),true));
                            this.numberOfTransformedClasses++;
                        }
                        else if ((StereotypesHelper.hasStereotype(listUseCases.get(i),UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString()))||
                            (StereotypesHelper.hasStereotype(listUseCases.get(i).getOwner(),UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString())))
                        {
                            listElements.add(ReqToPreTransformations.createPresentationClass(listUseCases.get(i).getName(),posTemp,diagramTransformator.getPackage(),false));
                            this.numberOfTransformedClasses++;
                        }
                    }
                    
                    for (int i=0;i<listElements.size();i++)
                    {
                        this.numberOfTransformedAssociations+=ReqToPreTransformations.addPresentationChildren(listElements.get(i).cClass, false, null);
                    }
                    
                    for (int i=listElements.size()-1;i>=0;i--)
                    {
                        if (listElements.get(i).cClass.getOwnedAttribute().isEmpty())
                        {
                            listElements.get(i).cClass.dispose();
                            listElements.remove(i);
                        }
                    }
                    
                    listElements.add(ReqToPreTransformations.createHome(new Point(50,50), diagramTransformator.getPackage(), listElements));
                    ElementCollector.removeCollisions(listElements,ReqToPreTransformations.iMIN_DISTANCE_X,ReqToPreTransformations.iMIN_DISTANCE_Y);
                    
                    numberOfTransformedAssociations+=(2*numberOfTransformedClasses-1);
		}

		@Override
		public void launchAssociationTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Associations
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName)
                {
                    MessageWriter.log("Transformation of \""
                        + this.toString()
                        + " done: "
                        + (this.numberOfTransformedClasses == 0 ? "No elements were" : this.numberOfTransformedClasses
                            + (this.numberOfTransformedClasses > 1 ? " classes" : " class")
                            + " and "
                            + this.numberOfTransformedAssociations
                            + (this.numberOfTransformedAssociations > 1 ? " properties " : " property ")
                            + ((this.numberOfTransformedAssociations
                                + this.numberOfTransformedClasses) > 1 ? "were" : "was")) + " transformed.", logger);
		}
	},
	CONTENT_2_NAVIGATION("Content -> Navigation", UWEDiagramType.CONTENT, UWEDiagramType.NAVIGATION) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Classes
			SessionManager.getInstance().createSession("Transform all classes");
			for (Element element : elementsInDiagram.keySet()) {

				List<PresentationElement> peList = elementsInDiagram.get(element);
				Class transformedClass = null;
				int j = 0;
				for (int i = 0; i < peList.size(); i++) {

					PresentationElement pe = peList.get(i);
					if (pe instanceof ClassView) {
						// create a new class or only add a new shape
						if (j == 0) {
							transformedClass = diagramTransformator.transformClass(pe);
						} else {
							diagramTransformator.setClassLayout(pe, transformedClass);
						}
						this.numberOfTransformedClasses++;
						j++;
					}
				}
			}
			SessionManager.getInstance().closeSession();
		}

		@Override
		public void launchAssociationTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Associations
			SessionManager.getInstance().createSession("Transform all associations");
			for (Element element : elementsInDiagram.keySet()) {
				for (PresentationElement pe : elementsInDiagram.get(element)) {
					if (pe instanceof AssociationView) {
						if (diagramTransformator.transformAssociation(pe)) {
							this.numberOfTransformedAssociations++;
						}
					}
				}
			}
			SessionManager.getInstance().closeSession();
		}

		@Override
		public void addConvertedStereotypeToClass(PresentationElement origClass, Class newClass) {
			MagicDrawElementOperations.addStereotypeToElement(newClass, UWEStereotypeClassNav.NAVIGATION_CLASS
					.toString());
		}

		@Override
		public void addConvertedStereotypeToAssociation(PresentationElement origAssociation, Association newAssociation) {
			MagicDrawElementOperations.addStereotypeToElement(newAssociation, UWEStereotypeAssoc.NAVIGATION_LINK
					.toString());
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			MessageWriter.log("Transformation of \""
					+ sourceDiagramName
					+ "\" "
					+ this.toString()
					+ " done: "
					+ (this.numberOfTransformedClasses == 0 ? "No elements were" : this.numberOfTransformedClasses
							+ (this.numberOfTransformedClasses > 1 ? " classes" : " class")
							+ " and "
							+ this.numberOfTransformedAssociations
							+ (this.numberOfTransformedAssociations > 1 ? " associations " : " association ")
							+ ((this.numberOfTransformedAssociations + this.numberOfTransformedClasses) > 1 ? "were"
									: "was")) + " transformed.", logger);
		}
	},
	NAVIGATION_2_PRESENTATION("Navigation -> Presentation", UWEDiagramType.NAVIGATION, UWEDiagramType.PRESENTATION) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			SessionManager.getInstance().createSession("Transform classes containing one navigation-stereotype");
			for (Element element : elementsInDiagram.keySet()) {

				List<PresentationElement> peList = elementsInDiagram.get(element);
				Class transformedClass = null;
				int j = 0;
				for (int i = 0; i < peList.size(); i++) {

					PresentationElement pe = peList.get(i);
					if (pe instanceof ClassView && transformThisClass(pe)) {
						// create a new class or only add a new shape
						if (j == 0) {
							transformedClass = diagramTransformator.transformClass(pe);
						} else {
							diagramTransformator.setClassLayout(pe, transformedClass);
						}
						this.numberOfTransformedClasses++;
						j++;
					}
				}
			}
			SessionManager.getInstance().closeSession();
		}

		/**
		 * decide if we want to transform this class
		 * 
		 * @param element
		 * @return true <=> transform the given class
		 */
		private boolean transformThisClass(PresentationElement element) {
			// Transform all sorts of navigation-classes (one of those
			// stereotypes is enough)
			for (UWEStereotypeClassNav navigationClassElement : UWEStereotypeClassNav.values()) {
				if (MagicDrawElementOperations.hasStereotype(element.getElement(), navigationClassElement.toString())
						|| MagicDrawElementOperations.hasStereotype(element.getElement(),
								UWEStereotypeAssoc.NAVIGATION_LINK.toString())) {
					return true;
				}
			}
			// Also allow PROCESS_CLASS
			if (MagicDrawElementOperations.hasStereotype(element.getElement(), UWEStereotypeClassGeneral.PROCESS_CLASS
					.toString())) {
				return true;
			}
			return false;
		}

		@Override
		public void addConvertedStereotypeToClass(PresentationElement origClass, Class newClass) {
			MagicDrawElementOperations.addStereotypeToElement(newClass, UWEStereotypeClassPres.PRESENTATION_GROUP
					.toString());
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			MessageWriter.log("Transformation of \""
					+ sourceDiagramName
					+ "\" "
					+ this.toString()
					+ " done: "
					+ (this.numberOfTransformedClasses == 0 ? "No class was" : this.numberOfTransformedClasses
							+ (this.numberOfTransformedClasses > 1 ? " classes were" : " class was"))
					+ " transformed. (Only elements with a kind of " + "navigation stereotype were transformed!)",
					logger);
		}
	},

	NAVIGATION_2_PROCESS_STRUCTURE("Navigation -> Process Structure", UWEDiagramType.NAVIGATION, UWEDiagramType.PROCESS_STRUCTURE) {
		@Override
		protected void doTheFirstPartOfTheTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
			// create a new diagram
			Diagram destDiagram = this.getDestinationDiagramTypeForTransformation().createAndAddDiagram(destPackage);

			this.diagramTransformator = new DiagramTransformator(sourceDiagram, destDiagram, destPackage, this);
		}

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform only PROCESS_CLASS, and don't copy the class elements.
			// (but the shapes)
			SessionManager.getInstance().createSession("Transform Process Classes");
			for (Element element : elementsInDiagram.keySet()) {
				for (PresentationElement pe : elementsInDiagram.get(element)) {
					// iterate over all PresentationElements in the Diagram
					if (pe instanceof ClassView
							&& MagicDrawElementOperations.hasStereotype(element,
									UWEStereotypeClassGeneral.PROCESS_CLASS.toString())) {
						diagramTransformator.setClassLayout(pe, (Class) element);
						this.numberOfTransformedClasses++;
					}
				}
			}
			SessionManager.getInstance().closeSession();
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			MessageWriter.log("Transformation of \""
					+ sourceDiagramName
					+ "\" "
					+ this.toString()
					+ " done: "
					+ (this.numberOfTransformedClasses == 0 ? "No class was" : this.numberOfTransformedClasses
							+ (this.numberOfTransformedClasses > 1 ? " classes were" : " class was"))
					+ " transformed. (Only elements with " + UWEStereotypeClassGeneral.PROCESS_CLASS.toString()
					+ "-stereotype were transformed!)", logger);
		}
	},

	NAVIGATION_2_PROCESS_FLOWS("Navigation -> Process Flows", UWEDiagramType.NAVIGATION, UWEDiagramType.PROCESS_FLOW) {		
		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// we need a transformation launcher without sessions and without
			// destinationDiagram and without diagramTransformator
			Package pack = null;
			for (Element element : elementsInDiagram.keySet()) {
				// iterate only over the elements, NOT over the
				// PresentationElements
				// Transform only PROCESS_CLASS elements
				if (element instanceof Class
						&& MagicDrawElementOperations.hasStereotype(element, UWEStereotypeClassGeneral.PROCESS_CLASS
								.toString())) {
					UWEDiagramType dgType = UWEDiagramType.PROCESS_FLOW;
					if (pack == null) {
						pack =
								dgType.getModelOrCreateIt(Application.getInstance().getProjectsManager()
										.getActiveProject(),
										"Do you want the new diagram(s) to be stored in a new model called \""
												+ dgType.originalModelName + "\"\nincluding the stereotype \""
												+ dgType.modelStereotype
												+ "\"? (recommended) \n\n(\"No\" will ask you to select a package)",
										true);
					}
					if (pack != null) {
						// create diagram with the name of the class element
						// but only if a name exists
						if (!((Class) element).getName().trim().equals("")) {
							this.numberOfTransformedClasses++;
							dgType.createAndAddDiagram(pack, ((Class) element).getName() + " Workflow");
						}
						// MessageWriter.log("Transformation '" +
						// this.toString() + "' into the container "
						// + pack.getName() + " completed.", logger);
					}
				}
			}
			DiagramPresentationElement diaTemp = null;
			DiagramPresentationElement diaTemp2 = null;

            for (DiagramPresentationElement diagram : Application.getInstance().getProject().getDiagrams(UWEDiagramType.CONTENT.umlDiagramType))
            {
            	if(diagram.getName().equals("Content Diagram"))
            	{
            		diaTemp = diagram;
            	}
            }
            if(diaTemp != null)
        	{
        		SessionManager.getInstance().createSession("Create Process Flows Element");
        		List<PresentationElement> elements = diaTemp.getPresentationElements();
        	    for (int i = elements.size() - 1; i >= 0; --i)
        	    {
        	        PresentationElement elementPresent = elements.get(i);
        	        if (elementPresent instanceof NoteView)
        	        {
        	        	String[] arr = elements.get(i).getName().split(" |\n|:|\\(|\\)");    
        	        	
        	        	if(arr[0].equals("context"))
        	        	{
        	        		String wfTemp = arr[3];
        	        		for (int j=4;j<arr.length;j++)
        	        		{
        	        			if(arr[j].equals("pre")|(arr[j].equals("post")))
        	        			{
        	        				String conditionTemp = "";
        	        				int k;
        	        				if(arr[j+1].equals("not"))
        	        				{
        	        					k = j+2;
        	        				}else
        	        				{
        	        					k = j+1;
        	        				}
        	        				while((!arr[k].equals("pre"))&(!arr[k].equals("post")))
        	        				{
        	        					conditionTemp = conditionTemp + arr[k];
        	        					k++;
        	        					if(k==arr.length)
        	        						break;
        	        				}
        	        				for (DiagramPresentationElement diagram : Application.getInstance().getProject().getDiagrams(UWEDiagramType.PROCESS_FLOW.umlDiagramType))
        	        	            {
        	        	            	if(diagram.getName().equals(wfTemp.substring(0, 1).toUpperCase()+wfTemp.substring(1)+" Workflow"))
        	        	            	{
        	        	            		diaTemp2 = diagram;
        	        	            	}
        	        	            }
        	        				if(diaTemp2!=null)
        	        				{
        	        					DecisionNode nodeTemp=Application.getInstance().getProject().getElementsFactory().createDecisionNodeInstance();
		        	            		CallBehaviorAction nodeTrue=Application.getInstance().getProject().getElementsFactory().createCallBehaviorActionInstance();
		        	            		CallBehaviorAction nodeFalse=Application.getInstance().getProject().getElementsFactory().createCallBehaviorActionInstance();
		        	            		ControlFlow flow1=Application.getInstance().getProject().getElementsFactory().createControlFlowInstance();
		        	            		ControlFlow flow2=Application.getInstance().getProject().getElementsFactory().createControlFlowInstance();
		        	            		PresentationElement nodeTempPresent = null;
		        	            		PresentationElement nodeTruePresent = null;
		        	            		PresentationElement nodeFalsePresent = null;
		        	            		nodeTemp.setName(conditionTemp);
		        	            		nodeTemp.setOwner(diaTemp2.getElement().getOwner());
		        	            		try {
		        	            			nodeTempPresent = PresentationElementsManager.getInstance().createShapeElement(nodeTemp, diaTemp2);
										} catch (ReadOnlyElementException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		        	            		nodeTrue.setName("True");
		        	            		nodeTrue.setOwner(diaTemp2.getElement().getOwner());
		        	            		try {
		        	            			nodeTruePresent = PresentationElementsManager.getInstance().createShapeElement(nodeTrue, diaTemp2);
										} catch (ReadOnlyElementException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		        	            		nodeFalse.setName("False");
		        	            		nodeFalse.setOwner(diaTemp2.getElement().getOwner());
		        	            		try {
		        	            			nodeFalsePresent = PresentationElementsManager.getInstance().createShapeElement(nodeFalse, diaTemp2);
										} catch (ReadOnlyElementException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		        	            		flow1.setSource(nodeTemp);
		        	            		flow1.setTarget(nodeTrue);
		        	            		flow1.setOwner(diaTemp2.getElement().getOwner());
//		        	            		try {
//											PresentationElementsManager.getInstance().createPathElement(flow1, nodeTruePresent, nodeTempPresent);
//										} catch (ReadOnlyElementException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
		        	            		flow2.setSource(nodeTemp);
		        	            		flow2.setTarget(nodeFalse);
		        	            		flow2.setOwner(diaTemp2.getElement().getOwner());
//		        	            		try {
//											PresentationElementsManager.getInstance().createPathElement(flow2, nodeFalsePresent, nodeTempPresent);
//										} catch (ReadOnlyElementException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
        	        				}
        	        			}
        	        		}
        	        	}
//        	        	javax.swing.JOptionPane.showMessageDialog(null, arr[0]);
//        	        	javax.swing.JOptionPane.showMessageDialog(null, elements.get(i).getName());
        	        }
        	    }
        		SessionManager.getInstance().closeSession();
        	}
			
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			MessageWriter.log("Transformation of \"" + sourceDiagramName + "\" " + this.toString() + " done."
					+ " (Only classes with " + UWEStereotypeClassGeneral.PROCESS_CLASS.toString()
					+ " stereotype and proper names were transformed into " + this.numberOfTransformedClasses
					+ " process flow " + (this.numberOfTransformedClasses > 1 ? "diagrams" : "diagram") + "!)", logger);
		}
		
	},
	CONTENT_2_JAVACODE("Content -> JavaCode", UWEDiagramType.CONTENT, null){
		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Classes
             ArrayList<NamedElement> modelsContents=ElementCollector.getNamedElements(UWEDiagramType.CONTENT.modelStereotype, null, Model.class, false, true);
             ArrayList<NamedElement> listClass=ElementCollector.getNamedElements(null, null, Class.class, false, modelsContents, false);
                        
             ArrayList<TypedElement> listReturn=new ArrayList<TypedElement>();
             for (int c=0;c<listClass.size();c++){
            	Cont2JavaTransformation.Content2EntityTransformation((Class)listClass.get(c));        
             }          	
		}
		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			// TODO Auto-generated method stub
			MessageWriter.log("Transfomation of \"" + this.toString() + " done! ", logger);
		}

	},
	NAVIGATION_2_JAVACODE("Navigation -> JavaCode", UWEDiagramType.NAVIGATION, null) {

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			ArrayList<NamedElement> modelsContents = ElementCollector
					.getNamedElements(UWEDiagramType.NAVIGATION.modelStereotype, null, Model.class, false, true);
			ArrayList<NamedElement> listClass = ElementCollector.getNamedElements(null, null, Class.class, false,
					modelsContents, false);			
			Nav2JavaTransformation.Navigation2ControllerTransformation(listClass.get(0));					
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			// TODO Auto-generated method stub
			MessageWriter.log("Transfomation of \"" + this.toString() + " done! ", logger);
		}

	},
	PROCESS_2_JAVACODE("Process -> JavaCode", UWEDiagramType.PROCESS_STRUCTURE, null) {

		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {
			// Transform all Classes
			ArrayList<NamedElement> modelsContents = ElementCollector
					.getNamedElements(UWEDiagramType.PROCESS_STRUCTURE.modelStereotype, null, Model.class, false, true);
			ArrayList<NamedElement> listClass = ElementCollector.getNamedElements(null, null, Class.class, false,
					modelsContents, false);
			Proc2JavaTransformation.Process2ControllerTransformation(listClass);
		}

		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			// TODO Auto-generated method stub
			MessageWriter.log("Transfomation of \"" + this.toString() + " done! ", logger);
		}

	},
	PRESENTATION_2_JAVACODE("Presentation -> JavaCode", UWEDiagramType.PRESENTATION, null){
		@Override
		public void launchClassTransformation(HashMap<Element, List<PresentationElement>> elementsInDiagram) {			
			ArrayList<NamedElement> modelsNav = ElementCollector
					.getNamedElements(UWEDiagramType.NAVIGATION.modelStereotype, null, Model.class, false, true);
			ArrayList<NamedElement> listNav = ElementCollector.getNamedElements(null, null, Class.class, false,
					modelsNav, false);

			ArrayList<NamedElement> relativeIndexClass = new ArrayList<NamedElement>();
			for (NamedElement c : listNav) {
				if (StereotypesHelper.hasStereotype(c, UWEStereotypeClassNav.INDEX.toString())){
					ArrayList<NamedElement> lstElements = Nav2JavaTransformation.helper_getAssElement(c);
					relativeIndexClass.addAll(lstElements);
				}		
			}
			Pre2JavaTransformation.helper_setupIndexClass(relativeIndexClass);	
			ArrayList<NamedElement> modelsPresentations = ElementCollector
					.getNamedElements(UWEDiagramType.PRESENTATION.modelStereotype, null, Model.class, false, true);
			ArrayList<NamedElement> listClass = ElementCollector.getNamedElements(null, null, Class.class, false,
					modelsPresentations, false);
			Pre2JavaTransformation.Presentation2JSPTransformation(listClass);
			

		}
		@Override
		protected void showDoneMessage(String sourceDiagramName) {
			// TODO Auto-generated method stub
			MessageWriter.log("Transfomation of \"" + this.toString() + " done! ", logger);
		}
		
	};

	protected int numberOfTransformedOperation;

	protected static final Logger logger = Logger.getLogger(TransformationType.class);

	private final UWEDiagramType sourceDiagramType;
	private final UWEDiagramType destinationDiagramType;
	private final String nameOfTransformation;
	protected DiagramTransformator diagramTransformator;
	protected long numberOfTransformedAssociations = 0;
	protected long numberOfTransformedAttributes = 0;
	protected long numberOfTransformedClasses = 0;

	private TransformationType(String nameOfTransformation, UWEDiagramType sourceDiagramType,
			UWEDiagramType destinationDiagramType) {
		this.nameOfTransformation = nameOfTransformation;
		this.sourceDiagramType = sourceDiagramType;
		this.destinationDiagramType = destinationDiagramType;
	}
	
	@Override
	public String toString() {
		return this.nameOfTransformation;
	}

	/**
	 * executes the transformation. The methods this function calls may be
	 * overwritten (callTransformClass and/or callTransformAssociation etc).
	 * 
	 * @param sourceDiagram
	 * @param destPackage
	 */
	public final void launchTransformation(DiagramPresentationElement sourceDiagram, Package destPackage) {
		this.numberOfTransformedClasses = 0;
		this.numberOfTransformedAssociations = 0;
		logger.debug("starting transformation: " + this.toString());
		if(destPackage != null)
			this.doTheFirstPartOfTheTransformation(sourceDiagram, destPackage);

		// get all elements of the diagram, and its PresentationElements
		HashMap<Element, List<PresentationElement>> elementsInDiagram =
				new HashMap<Element, List<PresentationElement>>();
		MagicDrawElementOperations.collectElementsAndPresentationElements(sourceDiagram, elementsInDiagram);

		// Transform CLASSES
		this.launchClassTransformation(elementsInDiagram);

		// Transform ASSOCIATIONS
		this.launchAssociationTransformation(elementsInDiagram);

		this.showDoneMessage(sourceDiagram.getName());
	}

	protected void doTheFirstPartOfTheTransformation(
			@SuppressWarnings("unused") DiagramPresentationElement sourceDiagram,
			@SuppressWarnings("unused") Package destPackage) {
		// maybe overwritten
	}

	/**
	 * displays the Done-Message after the transformation.
	 * 
	 * @param sourceDiagramName
	 */
	protected abstract void showDoneMessage(String sourceDiagramName);

	/**
	 * checks if dgType is the right source diagram type for transformation
	 * 
	 * @param dgType
	 * @return boolean
	 */
	public boolean isRightSourceDiagramTypeForTransformation(UWEDiagramType dgType) {
		return (this.sourceDiagramType == dgType);
	}

	/**
	 * get diagram source type for this transformation
	 * 
	 * @return UWEDiagramType
	 */
	public UWEDiagramType getDiagramSourceTypeForTransformation() {
		return this.sourceDiagramType;
	}

	/**
	 * get diagram destination type for this transformation
	 * 
	 * @return UWEDiagramType
	 */
	public UWEDiagramType getDestinationDiagramTypeForTransformation() {
		return this.destinationDiagramType;
	}

	/***************************************************************************
	 * the following methods are only a temporary solution, they should be
	 * improved
	 **************************************************************************/

	/**
	 * @param elementsInDiagram
	 */
	public void launchClassTransformation(
			@SuppressWarnings("unused") HashMap<Element, List<PresentationElement>> elementsInDiagram) {
		// may be overwritten
	}

	/**
	 * @param elementsInDiagram
	 */
	public void launchAssociationTransformation(
			@SuppressWarnings("unused") HashMap<Element, List<PresentationElement>> elementsInDiagram) {
		// may be overwritten
	}

	/**
	 * Set converted Stereotype(s) to the new class.
	 * 
	 * @param origClass
	 * @param newClass
	 */
	public void addConvertedStereotypeToClass(@SuppressWarnings("unused") PresentationElement origClass,
			@SuppressWarnings("unused") Class newClass) {
		// may be overwritten
	}

	/**
	 * Set converted Stereotype(s) to the new association.
	 * 
	 * @param origAssociation
	 * @param newAssociation
	 */
	public void addConvertedStereotypeToAssociation(@SuppressWarnings("unused") PresentationElement origAssociation,
			@SuppressWarnings("unused") Association newAssociation) {
		// may be overwritten
	}
}
