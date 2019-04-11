/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.transformation.requirements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeProcessFlow;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;
import magicUWE.stereotypes.tags.UWETagNavigationNode;
import magicUWE.stereotypes.tags.UWETagSystemAction;
import magicUWE.stereotypes.tags.UWETagUserAction;
import magicUWE.stereotypes.tags.requirements.UWETagWebUseCase;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.InputPin;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.OutputPin;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityEdge;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityFinalNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ControlFlow;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.InitialNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ObjectFlow;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.ActivityNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.CentralBufferNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.DecisionNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdstructuredactivities.StructuredActivityNode;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

/**
 *
 * @author PST LMU
 */
public class ReqToProTransformations 
{
    // Additional distance between the process classes in a diagram when computing collisions
    public static final int iMIN_DISTANCE_X=20;
    public static final int iMIN_DISTANCE_Y=20;
    
    /** Removes all workflows form a process class
     * 
     * @param cClass process class
     */
    public static void clearWorkflows(Class cClass)
    {
        ArrayList<NamedElement> listActivities=ElementCollector.getNamedElements(null, null, Activity.class, true, cClass, false);
        
        for (int i=listActivities.size()-1;i>=0;i--)
        {
            listActivities.get(i).dispose();
        }
    }

    /** Creates a process class
     * 
     * @param sName name of the class
     * @param position position of the class
     * @param elParent parent element 
     * @return class and presentation element
     */
    public static ElementCollector.ReturnElement createProcessClass(String sName,
            Point position,
            NamedElement elParent)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Process Class");
        }
        Project cProject = Application.getInstance().getProject();
        DiagramPresentationElement cDiagram = cProject.getActiveDiagram();
        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(null, sName.replaceAll(" ",""), Class.class, true, elParent, true);

        Class cReturn=null;
        ShapeElement elShape=null;
        Stereotype cStereotype=null;
        if (cDiagram!=null)
        {
            if (listOld.isEmpty())
            {
                cReturn = Application.getInstance().getProject().getElementsFactory().createClassInstance();

                if (elParent==null)
                {
                    cReturn.setOwner(cDiagram.getDiagram().getOwner());
                }
                else
                {
                    cReturn.setOwner(elParent);
                }
                cReturn.setName(sName.replaceAll(" ",""));
                cStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeClassGeneral.PROCESS_CLASS.toString());
            }
            else
            {
                cReturn=((Class)(listOld.get(0)));
            }

            try
            {
                elShape=PresentationElementsManager.getInstance().createShapeElement(cReturn,cDiagram);
                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToProTransformationRules.createProcessClass(): "+e.toString());
            }

            if (cStereotype!=null)
            {
                StereotypesHelper.addStereotype(cReturn,cStereotype);
                
                ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, true, true);
                ArrayList<NamedElement> listCases=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString(), sName,
                        UseCase.class, true, listReqModels, false);
                if (listCases.size()>0)
                {
                    Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                            UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString()),UWETagWebUseCase.IS_LANDMARK.toString());

                    Slot slotOld=StereotypesHelper.getSlot(listCases.get(0),propOld,false,false);
                    if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                    {
                        Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeClassGeneral.PROCESS_CLASS.toString()),UWETagNavigationNode.IS_LANDMARK.toString());
                        Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                        LiteralBoolean litBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                        litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                        slotNew.getValue().add(litBoolean);
                    }

                    
                    propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                            UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString()),UWETagWebUseCase.GUARD.toString());

                    slotOld=StereotypesHelper.getSlot(listCases.get(0),propOld,false,false);
                    if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                    {
                        Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeClassGeneral.PROCESS_CLASS.toString()),UWETagNavigationNode.GUARD.toString());
                        Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                        LiteralString litString=cProject.getElementsFactory().createLiteralStringInstance();
                        litString.setValue(((LiteralString)(slotOld.getValue().get(0))).getValue());
                        slotNew.getValue().add(litString);
                    }
                }
                
                ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString(), null,
                        Package.class, true, listReqModels, false);
                for (int p=0;p<listPackages.size();p++)
                {
                    listCases=ElementCollector.getNamedElements(null, sName, UseCase.class, true, listPackages.get(p), false);
                    
                    
                    if (listCases.size()>0)
                    {
                        Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString()),UWETagWebUseCase.IS_LANDMARK.toString());

                        Slot slotOld=StereotypesHelper.getSlot(listPackages.get(p),propOld,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                    UWEStereotypeClassGeneral.PROCESS_CLASS.toString()),UWETagNavigationNode.IS_LANDMARK.toString());
                            Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                            LiteralBoolean litBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }


                        propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString()),UWETagWebUseCase.GUARD.toString());

                        slotOld=StereotypesHelper.getSlot(listPackages.get(p),propOld,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                    UWEStereotypeClassGeneral.PROCESS_CLASS.toString()),UWETagNavigationNode.GUARD.toString());
                            Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                            LiteralString litString=cProject.getElementsFactory().createLiteralStringInstance();
                            litString.setValue(((LiteralString)(slotOld.getValue().get(0))).getValue());
                            slotNew.getValue().add(litString);
                        }
                    }
                }
                
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }
            try
            {
                com.nomagic.magicdraw.properties.Property cProp=elShape.getProperty("SUPPRESS_CLASS_ATTRIBUTES").clone();
                cProp.setValue(true);
                elShape.changeProperty(cProp);
                cProp=elShape.getProperty("SUPPRESS_CLASS_OPERATIONS").clone();
                cProp.setValue(true);
                elShape.changeProperty(cProp);
                cProp=elShape.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
                cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
                elShape.changeProperty(cProp);

                if (!SessionManager.getInstance().isSessionCreated())
                {
                    bCreated=true;
                    SessionManager.getInstance().createSession("Create Process Class");
                }
                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToProTransformationRules.createProcessClass(): "+e.toString());
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }

        return(new ElementCollector.ReturnElement(cReturn, elShape,0,null,null));
    }
    
    /** Adds a workflow to a process class
     *
     * @param cMain the process class
     */
    public static boolean addWorkflow(Class cMain)
    {
        ArrayList<Action> nodesRemove=new ArrayList<Action>();
        ArrayList<ArrayList<ShapeElement>> shapeActionPins=new ArrayList<ArrayList<ShapeElement>>();
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> temp=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString(), 
                null, UseCase.class, false, listReqModels, false);
        ArrayList<NamedElement> temp2=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString(), 
                null, Package.class, false, listReqModels, false);
        ArrayList<NamedElement> temp3=ElementCollector.getNamedElements(null, null, UseCase.class, false, temp2, false);
        temp.addAll(temp3);

        ArrayList<NamedElement> listConModels=ElementCollector.getNamedElements(UWEDiagramType.CONTENT.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listClasses=ElementCollector.getNamedElements(null, null, Class.class, false, listConModels, false);
        ArrayList<NamedElement> listUserModels=ElementCollector.getNamedElements(UWEDiagramType.USER_MODEL.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listClasses2=ElementCollector.getNamedElements(null, null, Class.class, false, listUserModels, false);
        listClasses.addAll(listClasses2);
        
        ArrayList<NamedElement> listUseCases=new ArrayList<NamedElement>();
        if (ElementCollector.getNamedElementFromArrayList(temp, cMain.getName(), false, true)!=ElementCollector.iNO_ELEMENT)
        {
            listUseCases.add(temp.get(ElementCollector.getNamedElementFromArrayList(temp, cMain.getName(), false, true)));
        }
        else 
        {
            return(false);
        }
        ArrayList<NamedElement> listActivities=ElementCollector.getNamedElements(null, null, Activity.class, true, listUseCases, false);
        
        ArrayList<ShapeElement> listShapes=new ArrayList<ShapeElement>();
        ArrayList<PresentationElement> listMasters=new ArrayList<PresentationElement>();

        for (int i=0;i<listActivities.size();i++)
        {
            boolean bCreated=false;
            if (!SessionManager.getInstance().isSessionCreated())
            {
                bCreated=true;
                SessionManager.getInstance().createSession("Create Workflow");
            }

            HashMap<ActivityNode,ActivityNode> acNodes=new HashMap<ActivityNode,ActivityNode>();
            HashMap<ActivityEdge,ActivityEdge> acEdges=new HashMap<ActivityEdge,ActivityEdge>();

            Activity cActivity=Application.getInstance().getProject().
                                        getElementsFactory().createActivityInstance();

            cActivity.setName(listActivities.get(i).getName().replaceAll(" ",""));
            cActivity.setAbstract(((Activity)(listActivities.get(i))).isAbstract());
            cActivity.setActive(((Activity)(listActivities.get(i))).isActive());
            cActivity.setAppliedStereotypeInstance(listActivities.get(i).getAppliedStereotypeInstance());
            cActivity.setClassifierBehavior(((Activity)(listActivities.get(i))).getClassifierBehavior());
            cActivity.setNameExpression(listActivities.get(i).getNameExpression());
            cActivity.setNamespace(listActivities.get(i).getNamespace());
            cActivity.setOwnedTemplateSignature(((Activity)(listActivities.get(i))).getOwnedTemplateSignature());
            cActivity.setOwningPackage(cMain.getOwningPackage());
            cActivity.setOwningTemplateParameter(((Activity)(listActivities.get(i))).getOwningTemplateParameter());
            cActivity.setPackage(((Activity)(listActivities.get(i))).getPackage());
            cActivity.setReadOnly(((Activity)(listActivities.get(i))).isReadOnly());
            cActivity.setReentrant(((Activity)(listActivities.get(i))).isReentrant());
            cActivity.setRepresentation(((Activity)(listActivities.get(i))).getRepresentation());
            cActivity.setSingleExecution(((Activity)(listActivities.get(i))).isSingleExecution());
            cActivity.setTemplateParameter(((Activity)(listActivities.get(i))).getTemplateParameter());
            cActivity.setSpecification(((Activity)(listActivities.get(i))).getSpecification());
            cActivity.setUMLClass(((Activity)(listActivities.get(i))).getUMLClass());
            cActivity.setVisibility(listActivities.get(i).getVisibility());
            cActivity.setOwner(cMain);

            Iterator<ActivityNode> itNodes=((Activity)(listActivities.get(i))).getNode().iterator();
            ArrayList<ActivityNode> listOldNodes=new ArrayList<ActivityNode>();
            ArrayList<ActivityNode> listOldNodeParents=new ArrayList<ActivityNode>();
            while (itNodes.hasNext())
            {
                listOldNodes.add(itNodes.next());
                listOldNodeParents.add(null);
            }
            Iterator<ActivityEdge> itEdges=((Activity)(listActivities.get(i))).getEdge().iterator();
            ArrayList<ActivityEdge> listEdges=new ArrayList<ActivityEdge>();
            ArrayList<ActivityNode> listEdgeParents=new ArrayList<ActivityNode>();
            while (itEdges.hasNext())
            {
                listEdges.add(itEdges.next());
                listEdgeParents.add(null);
            }
            
            for (int q=0;q<listOldNodes.size();q++)
            {
                ActivityNode cTemp=listOldNodes.get(q);

                if (cTemp instanceof CallBehaviorAction)
                {
                    CallBehaviorAction cOld=((CallBehaviorAction)(cTemp));
                    CallBehaviorAction cNew=Application.getInstance().getProject().
                            getElementsFactory().createCallBehaviorActionInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setBehavior(cOld.getBehavior());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setOnPort(cOld.getOnPort());
                    cNew.setSynchronous(cOld.isSynchronous());
                    cNew.setVisibility(cOld.getVisibility());

                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }

                    if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString());
                        nodesRemove.add(cNew);
                    }
                    else if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeProcessFlow.SYSTEM_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeProcessFlow.SYSTEM_ACTION.toString());
                        
                        Property property = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeProcessFlow.SYSTEM_ACTION.toString()),
                        UWETagSystemAction.CONFIRMED.toString());

                        Slot slotOld=StereotypesHelper.getSlot(cOld,property,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Slot slotNew=StereotypesHelper.getSlot(cNew,property,true,false);
                            LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }
                    }
                    else if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeProcessFlow.USER_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeProcessFlow.USER_ACTION.toString());
                        
                        Property property = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeProcessFlow.USER_ACTION.toString()),
                        UWETagUserAction.VALIDATED.toString());

                        Slot slotOld=StereotypesHelper.getSlot(cOld,property,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Slot slotNew=StereotypesHelper.getSlot(cNew,property,true,false);
                            LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }
                    }
                    else if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString());
                        nodesRemove.add(cNew);
                    }

                    Iterator<InputPin> itInputPins=cOld.getInput().iterator();
                    while (itInputPins.hasNext())
                    {
                        InputPin inOldPin=itInputPins.next();
                        InputPin inNewPin=Application.getInstance().getProject().
                            getElementsFactory().createInputPinInstance();
                        acNodes.put(inOldPin,inNewPin);

                        inNewPin.setActivity(inOldPin.getActivity());
                        inNewPin.setControl(inOldPin.isControl());
                        inNewPin.setControlType(inOldPin.isControlType());
                        inNewPin.setInStructuredNode(inOldPin.getInStructuredNode());
                        inNewPin.setLowerValue(inOldPin.getLowerValue());
                        inNewPin.setName(inOldPin.getName());
                        inNewPin.setNameExpression(inOldPin.getNameExpression());
                        inNewPin.setOrdering(inOldPin.getOrdering());
                        inNewPin.setParameter(inOldPin.getParameter());
                        inNewPin.setSelection(inOldPin.getSelection());
                        inNewPin.setType(inOldPin.getType());
                        inNewPin.setUnique(inOldPin.isUnique());
                        inNewPin.setUpperBound(inOldPin.getUpperBound());
                        inNewPin.setUpperValue(inOldPin.getUpperValue());
                        inNewPin.setVisibility(inOldPin.getVisibility());

                        inNewPin.setOwner(cNew);
                    }

                    Iterator<OutputPin> itOutputPins=cOld.getOutput().iterator();
                    while (itOutputPins.hasNext())
                    {
                        OutputPin outOldPin=itOutputPins.next();
                        OutputPin outNewPin=Application.getInstance().getProject().
                            getElementsFactory().createOutputPinInstance();
                        acNodes.put(outOldPin,outNewPin);

                        outNewPin.setActivity(outOldPin.getActivity());
                        outNewPin.setControl(outOldPin.isControl());
                        outNewPin.setControlType(outOldPin.isControlType());
                        outNewPin.setInStructuredNode(outOldPin.getInStructuredNode());
                        outNewPin.setLowerValue(outOldPin.getLowerValue());
                        outNewPin.setName(outOldPin.getName());
                        outNewPin.setNameExpression(outOldPin.getNameExpression());
                        outNewPin.setOrdering(outOldPin.getOrdering());
                        outNewPin.setParameter(outOldPin.getParameter());
                        outNewPin.setSelection(outOldPin.getSelection());
                        outNewPin.setType(outOldPin.getType());
                        outNewPin.setUnique(outOldPin.isUnique());
                        outNewPin.setUpperBound(outOldPin.getUpperBound());
                        outNewPin.setUpperValue(outOldPin.getUpperValue());
                        outNewPin.setVisibility(outOldPin.getVisibility());

                        outNewPin.setOwner(cNew);
                    }
                }
                else if (cTemp instanceof StructuredActivityNode)
                {
                    StructuredActivityNode cOld=((StructuredActivityNode)(cTemp));
                    StructuredActivityNode cNew=Application.getInstance().getProject().
                            getElementsFactory().createStructuredActivityNodeInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setVisibility(cOld.getVisibility());

                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }

                    if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeProcessFlow.SYSTEM_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeProcessFlow.SYSTEM_ACTION.toString());
                        
                        Property property = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeProcessFlow.SYSTEM_ACTION.toString()),
                        UWETagSystemAction.CONFIRMED.toString());

                        Slot slotOld=StereotypesHelper.getSlot(cOld,property,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Slot slotNew=StereotypesHelper.getSlot(cNew,property,true,false);
                            LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }
                    }
                    else if (StereotypesHelper.hasStereotype(cOld,UWEStereotypeProcessFlow.USER_ACTION.toString()))
                    {
                        StereotypesHelper.addStereotypeByString(cNew,UWEStereotypeProcessFlow.USER_ACTION.toString());
                        
                        Property property = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeProcessFlow.USER_ACTION.toString()),
                        UWETagUserAction.VALIDATED.toString());

                        Slot slotOld=StereotypesHelper.getSlot(cOld,property,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Slot slotNew=StereotypesHelper.getSlot(cNew,property,true,false);
                            LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }
                    }

                    Iterator<InputPin> itInputPins=cOld.getInput().iterator();
                    while (itInputPins.hasNext())
                    {
                        InputPin inOldPin=itInputPins.next();
                        InputPin inNewPin=Application.getInstance().getProject().
                            getElementsFactory().createInputPinInstance();
                        acNodes.put(inOldPin,inNewPin);

                        inNewPin.setActivity(inOldPin.getActivity());
                        inNewPin.setControl(inOldPin.isControl());
                        inNewPin.setControlType(inOldPin.isControlType());
                        inNewPin.setInStructuredNode(inOldPin.getInStructuredNode());
                        inNewPin.setLowerValue(inOldPin.getLowerValue());
                        inNewPin.setName(inOldPin.getName());
                        inNewPin.setNameExpression(inOldPin.getNameExpression());
                        inNewPin.setOrdering(inOldPin.getOrdering());
                        inNewPin.setParameter(inOldPin.getParameter());
                        inNewPin.setSelection(inOldPin.getSelection());
                        inNewPin.setType(inOldPin.getType());
                        inNewPin.setUnique(inOldPin.isUnique());
                        inNewPin.setUpperBound(inOldPin.getUpperBound());
                        inNewPin.setUpperValue(inOldPin.getUpperValue());
                        inNewPin.setVisibility(inOldPin.getVisibility());

                        inNewPin.setOwner(cNew);
                    }

                    Iterator<OutputPin> itOutputPins=cOld.getOutput().iterator();
                    while (itOutputPins.hasNext())
                    {
                        OutputPin outOldPin=itOutputPins.next();
                        OutputPin outNewPin=Application.getInstance().getProject().
                            getElementsFactory().createOutputPinInstance();
                        acNodes.put(outOldPin,outNewPin);

                        outNewPin.setActivity(outOldPin.getActivity());
                        outNewPin.setControl(outOldPin.isControl());
                        outNewPin.setControlType(outOldPin.isControlType());
                        outNewPin.setInStructuredNode(outOldPin.getInStructuredNode());
                        outNewPin.setLowerValue(outOldPin.getLowerValue());
                        outNewPin.setName(outOldPin.getName());
                        outNewPin.setNameExpression(outOldPin.getNameExpression());
                        outNewPin.setOrdering(outOldPin.getOrdering());
                        outNewPin.setParameter(outOldPin.getParameter());
                        outNewPin.setSelection(outOldPin.getSelection());
                        outNewPin.setType(outOldPin.getType());
                        outNewPin.setUnique(outOldPin.isUnique());
                        outNewPin.setUpperBound(outOldPin.getUpperBound());
                        outNewPin.setUpperValue(outOldPin.getUpperValue());
                        outNewPin.setVisibility(outOldPin.getVisibility());

                        outNewPin.setOwner(cNew);
                    }
                    
                    Iterator<ActivityNode> itInnerNodes=cOld.getNode().iterator();
                    while (itInnerNodes.hasNext())
                    {
                        listOldNodes.add(itInnerNodes.next());
                        listOldNodeParents.add(cNew);
                    }
                    
                    Iterator<ActivityEdge> itInnerEdges=cOld.getEdge().iterator();
                    while (itInnerEdges.hasNext())
                    {
                        listEdges.add(itInnerEdges.next());
                        listEdgeParents.add(cNew);
                    }
                    
                    if (cNew.getOwnedElement().isEmpty())
                    {
                        nodesRemove.add(cNew);
                    }
                }
                else if (cTemp instanceof InitialNode)
                {
                    InitialNode cOld=((InitialNode)(cTemp));
                    InitialNode cNew=Application.getInstance().getProject().
                            getElementsFactory().createInitialNodeInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setVisibility(cOld.getVisibility());

                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }
                }
                else if (cTemp instanceof ActivityFinalNode)
                {
                    ActivityFinalNode cOld=((ActivityFinalNode)(cTemp));
                    ActivityFinalNode cNew=Application.getInstance().getProject().
                            getElementsFactory().createActivityFinalNodeInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setVisibility(cOld.getVisibility());

                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }
                }
                else if (cTemp instanceof CentralBufferNode)
                {
                    CentralBufferNode cOld=((CentralBufferNode)(cTemp));
                    CentralBufferNode cNew=Application.getInstance().getProject().
                            getElementsFactory().createCentralBufferNodeInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setVisibility(cOld.getVisibility());
                    cNew.setControlType(cOld.isControlType());
                    cNew.setOrdering(cOld.getOrdering());
                    cNew.setSelection(cOld.getSelection());
                    cNew.setType(cOld.getType());
                    cNew.setUpperBound(cOld.getUpperBound());
        
                    if ((cOld.getType()!=null)&&
                        (ElementCollector.getNamedElementFromArrayList(listClasses,cOld.getType().getName(),false,false)!=ElementCollector.iNO_ELEMENT))
                    {
                        cNew.setType(((Class)(listClasses.get(ElementCollector.getNamedElementFromArrayList(listClasses,cOld.getType().getName(),false,false)))));
                    }
                    else if ((cOld.getType()==null)&&
                        (ElementCollector.getNamedElementFromArrayList(listClasses,cOld.getName(),false,false)!=ElementCollector.iNO_ELEMENT))
                    {
                        cNew.setType(((Class)(listClasses.get(ElementCollector.getNamedElementFromArrayList(listClasses,cOld.getName(),false,false)))));
                    }
                    
                    cNew.setName(cOld.getName());
                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }
                }
                else if (cTemp instanceof DecisionNode)
                {
                    DecisionNode cOld=((DecisionNode)(cTemp));
                    DecisionNode cNew=Application.getInstance().getProject().
                            getElementsFactory().createDecisionNodeInstance();
                    acNodes.put(cTemp,cNew);

                    cNew.setActivity(cOld.getActivity());
                    cNew.setInStructuredNode(cOld.getInStructuredNode());
                    cNew.setName(cOld.getName());
                    cNew.setNameExpression(cOld.getNameExpression());
                    cNew.setVisibility(cOld.getVisibility());

                    if (listOldNodeParents.get(q)==null)
                    {
                        cNew.setOwner(cActivity);
                    }
                    else
                    {
                        cNew.setOwner(listOldNodeParents.get(q));
                    }
                }
            }
            
            for (int e=0;e<listEdges.size();e++)
            {
                if (listEdges.get(e) instanceof ObjectFlow)
                {
                    ObjectFlow flowOld=((ObjectFlow)(listEdges.get(e)));
                     
                    if ((flowOld.getTarget() instanceof Action)&&
                        ((StereotypesHelper.hasStereotype(flowOld.getTarget(),UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))||
                        (StereotypesHelper.hasStereotype(flowOld.getTarget(),UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))))
                    {
                        Iterator<ActivityEdge> itTemp=((Activity)(listActivities.get(i))).getEdge().iterator();
                        while (itTemp.hasNext())
                        {
                            ActivityEdge tempEdge=itTemp.next();
                            if (tempEdge instanceof ControlFlow)
                            {
                                ControlFlow flowOld2=((ControlFlow)(tempEdge));
                                if (flowOld2.getSource()==flowOld.getTarget())
                                {
                                    ObjectFlow flowNew=Application.getInstance().getProject().getElementsFactory().createObjectFlowInstance();
                                    acEdges.put(listEdges.get(e),flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flowOld.getGuard());
                                    flowNew.setInStructuredNode(flowOld.getInStructuredNode());
                                    flowNew.setInterrupts(flowOld.getInterrupts());
                                    flowNew.setMulticast(flowOld.isMulticast());
                                    flowNew.setMultireceive(flowOld.isMultireceive());
                                    flowNew.setName(flowOld.getName());
                                    flowNew.setNameExpression(flowOld.getNameExpression());
                                    flowNew.setSelection(flowOld.getSelection());
                                    flowNew.setSource(acNodes.get(flowOld.getSource()));
                                    flowNew.setTarget(acNodes.get(flowOld2.getTarget()));
                                    flowNew.setTransformation(flowOld.getTransformation());
                                    flowNew.setVisibility(flowOld.getVisibility());
                                    flowNew.setWeight(flowOld.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                            else if (tempEdge instanceof ObjectFlow)
                            {
                                ObjectFlow flowOld2=((ObjectFlow)(tempEdge));
                                if (flowOld2.getSource()==flowOld.getTarget())
                                {
                                    ObjectFlow flowNew=Application.getInstance().getProject().getElementsFactory().createObjectFlowInstance();
                                    acEdges.put(listEdges.get(e),flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flowOld.getGuard());
                                    flowNew.setInStructuredNode(flowOld.getInStructuredNode());
                                    flowNew.setInterrupts(flowOld.getInterrupts());
                                    flowNew.setMulticast(flowOld2.isMulticast());
                                    flowNew.setMultireceive(flowOld2.isMultireceive());
                                    flowNew.setName(flowOld.getName());
                                    flowNew.setNameExpression(flowOld.getNameExpression());
                                    flowNew.setSelection(flowOld2.getSelection());
                                    flowNew.setSource(acNodes.get(flowOld.getSource()));
                                    flowNew.setTarget(acNodes.get(flowOld2.getTarget()));
                                    flowNew.setTransformation(flowOld2.getTransformation());
                                    flowNew.setVisibility(flowOld.getVisibility());
                                    flowNew.setWeight(flowOld.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                        }
                    }
                    else if ((flowOld.getTarget() instanceof Pin)&&
                        ((StereotypesHelper.hasStereotype(flowOld.getTarget().getOwner(),UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))||
                        (StereotypesHelper.hasStereotype(flowOld.getTarget().getOwner(),UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))))
                    {
                        for (int e2=0;e2<listEdges.size();e2++)
                        {
                            if (e2==e)
                            {
                                continue;
                            }
                            
                            if (listEdges.get(e2) instanceof ControlFlow)
                            {
                                ControlFlow flowOld2=((ControlFlow)(listEdges.get(e2)));
                                if (flowOld2.getSource()==flowOld.getTarget().getOwner())
                                {
                                    Pin cPin=null;
                                    if (flowOld.getTarget() instanceof InputPin)
                                    {
                                        InputPin inOldPin=((InputPin)(flowOld.getTarget()));
                                        InputPin inNewPin=Application.getInstance().getProject().
                                            getElementsFactory().createInputPinInstance();
                                        acNodes.put(inOldPin,inNewPin);

                                        inNewPin.setActivity(inOldPin.getActivity());
                                        inNewPin.setControl(inOldPin.isControl());
                                        inNewPin.setControlType(inOldPin.isControlType());
                                        inNewPin.setInStructuredNode(inOldPin.getInStructuredNode());
                                        inNewPin.setLowerValue(inOldPin.getLowerValue());
                                        inNewPin.setName(inOldPin.getName());
                                        inNewPin.setNameExpression(inOldPin.getNameExpression());
                                        inNewPin.setOrdering(inOldPin.getOrdering());
                                        inNewPin.setParameter(inOldPin.getParameter());
                                        inNewPin.setSelection(inOldPin.getSelection());
                                        inNewPin.setType(inOldPin.getType());
                                        inNewPin.setUnique(inOldPin.isUnique());
                                        inNewPin.setUpperBound(inOldPin.getUpperBound());
                                        inNewPin.setUpperValue(inOldPin.getUpperValue());
                                        inNewPin.setVisibility(inOldPin.getVisibility());

                                        inNewPin.setOwner(acNodes.get(flowOld2.getTarget()));
                                        cPin=inNewPin;
                                    }
                                    else if (flowOld.getTarget() instanceof OutputPin)
                                    {
                                        OutputPin outOldPin=((OutputPin)(flowOld.getTarget()));
                                        OutputPin outNewPin=Application.getInstance().getProject().
                                            getElementsFactory().createOutputPinInstance();
                                        acNodes.put(outOldPin,outNewPin);

                                        outNewPin.setActivity(outOldPin.getActivity());
                                        outNewPin.setControl(outOldPin.isControl());
                                        outNewPin.setControlType(outOldPin.isControlType());
                                        outNewPin.setInStructuredNode(outOldPin.getInStructuredNode());
                                        outNewPin.setLowerValue(outOldPin.getLowerValue());
                                        outNewPin.setName(outOldPin.getName());
                                        outNewPin.setNameExpression(outOldPin.getNameExpression());
                                        outNewPin.setOrdering(outOldPin.getOrdering());
                                        outNewPin.setParameter(outOldPin.getParameter());
                                        outNewPin.setSelection(outOldPin.getSelection());
                                        outNewPin.setType(outOldPin.getType());
                                        outNewPin.setUnique(outOldPin.isUnique());
                                        outNewPin.setUpperBound(outOldPin.getUpperBound());
                                        outNewPin.setUpperValue(outOldPin.getUpperValue());
                                        outNewPin.setVisibility(outOldPin.getVisibility());

                                        outNewPin.setOwner(acNodes.get(flowOld2.getTarget()));
                                        cPin=outNewPin;
                                    }
                                    
                                    ObjectFlow flowNew=Application.getInstance().getProject().getElementsFactory().createObjectFlowInstance();
                                    acEdges.put(listEdges.get(e),flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flowOld.getGuard());
                                    flowNew.setInStructuredNode(flowOld.getInStructuredNode());
                                    flowNew.setInterrupts(flowOld.getInterrupts());
                                    flowNew.setMulticast(flowOld.isMulticast());
                                    flowNew.setMultireceive(flowOld.isMultireceive());
                                    flowNew.setName(flowOld.getName());
                                    flowNew.setNameExpression(flowOld.getNameExpression());
                                    flowNew.setSelection(flowOld.getSelection());
                                    flowNew.setSource(acNodes.get(flowOld.getSource()));
                                    flowNew.setTarget(cPin);
                                    flowNew.setTransformation(flowOld.getTransformation());
                                    flowNew.setVisibility(flowOld.getVisibility());
                                    flowNew.setWeight(flowOld.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                            else if (listEdges.get(e2) instanceof ObjectFlow)
                            {
                                ObjectFlow flowOld2=((ObjectFlow)(listEdges.get(e2)));
                                if (flowOld2.getSource()==flowOld.getTarget().getOwner())
                                {
                                    Pin cPin=null;
                                    if (flowOld.getTarget() instanceof InputPin)
                                    {
                                        InputPin inOldPin=((InputPin)(flowOld.getTarget()));
                                        InputPin inNewPin=Application.getInstance().getProject().
                                            getElementsFactory().createInputPinInstance();
                                        acNodes.put(inOldPin,inNewPin);

                                        inNewPin.setActivity(inOldPin.getActivity());
                                        inNewPin.setControl(inOldPin.isControl());
                                        inNewPin.setControlType(inOldPin.isControlType());
                                        inNewPin.setInStructuredNode(inOldPin.getInStructuredNode());
                                        inNewPin.setLowerValue(inOldPin.getLowerValue());
                                        inNewPin.setName(inOldPin.getName());
                                        inNewPin.setNameExpression(inOldPin.getNameExpression());
                                        inNewPin.setOrdering(inOldPin.getOrdering());
                                        inNewPin.setParameter(inOldPin.getParameter());
                                        inNewPin.setSelection(inOldPin.getSelection());
                                        inNewPin.setType(inOldPin.getType());
                                        inNewPin.setUnique(inOldPin.isUnique());
                                        inNewPin.setUpperBound(inOldPin.getUpperBound());
                                        inNewPin.setUpperValue(inOldPin.getUpperValue());
                                        inNewPin.setVisibility(inOldPin.getVisibility());

                                        inNewPin.setOwner(acNodes.get(flowOld2.getTarget()));
                                        cPin=inNewPin;
                                    }
                                    else if (flowOld.getTarget() instanceof OutputPin)
                                    {
                                        OutputPin outOldPin=((OutputPin)(flowOld.getTarget()));
                                        OutputPin outNewPin=Application.getInstance().getProject().
                                            getElementsFactory().createOutputPinInstance();
                                        acNodes.put(outOldPin,outNewPin);

                                        outNewPin.setActivity(outOldPin.getActivity());
                                        outNewPin.setControl(outOldPin.isControl());
                                        outNewPin.setControlType(outOldPin.isControlType());
                                        outNewPin.setInStructuredNode(outOldPin.getInStructuredNode());
                                        outNewPin.setLowerValue(outOldPin.getLowerValue());
                                        outNewPin.setName(outOldPin.getName());
                                        outNewPin.setNameExpression(outOldPin.getNameExpression());
                                        outNewPin.setOrdering(outOldPin.getOrdering());
                                        outNewPin.setParameter(outOldPin.getParameter());
                                        outNewPin.setSelection(outOldPin.getSelection());
                                        outNewPin.setType(outOldPin.getType());
                                        outNewPin.setUnique(outOldPin.isUnique());
                                        outNewPin.setUpperBound(outOldPin.getUpperBound());
                                        outNewPin.setUpperValue(outOldPin.getUpperValue());
                                        outNewPin.setVisibility(outOldPin.getVisibility());

                                        outNewPin.setOwner(acNodes.get(flowOld2.getTarget()));
                                        cPin=outNewPin;
                                    }
                                    
                                    ObjectFlow flowNew=Application.getInstance().getProject().getElementsFactory().createObjectFlowInstance();
                                    acEdges.put(listEdges.get(e),flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flowOld.getGuard());
                                    flowNew.setInStructuredNode(flowOld.getInStructuredNode());
                                    flowNew.setInterrupts(flowOld.getInterrupts());
                                    flowNew.setMulticast(flowOld2.isMulticast());
                                    flowNew.setMultireceive(flowOld2.isMultireceive());
                                    flowNew.setName(flowOld.getName());
                                    flowNew.setNameExpression(flowOld.getNameExpression());
                                    flowNew.setSelection(flowOld2.getSelection());
                                    flowNew.setSource(acNodes.get(flowOld.getSource()));
                                    flowNew.setTarget(cPin);
                                    flowNew.setTransformation(flowOld2.getTransformation());
                                    flowNew.setVisibility(flowOld.getVisibility());
                                    flowNew.setWeight(flowOld.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                        }
                    }
                    else
                    {
                        ObjectFlow flowNew=Application.getInstance().getProject().
                                            getElementsFactory().createObjectFlowInstance();
                        acEdges.put(listEdges.get(e),flowNew);

                        flowNew.setActivity(cActivity);
                        flowNew.setGuard(flowOld.getGuard());
                        flowNew.setInStructuredNode(flowOld.getInStructuredNode());
                        flowNew.setInterrupts(flowOld.getInterrupts());
                        flowNew.setMulticast(flowOld.isMulticast());
                        flowNew.setMultireceive(flowOld.isMultireceive());
                        flowNew.setName(flowOld.getName());
                        flowNew.setNameExpression(flowOld.getNameExpression());
                        flowNew.setSelection(flowOld.getSelection());
                        flowNew.setSource(acNodes.get(flowOld.getSource()));
                        flowNew.setTarget(acNodes.get(flowOld.getTarget()));
                        flowNew.setTransformation(flowOld.getTransformation());
                        flowNew.setVisibility(flowOld.getVisibility());
                        flowNew.setWeight(flowOld.getWeight());
                        flowNew.setOwner(cActivity);
                    }

                }
                else if (listEdges.get(e) instanceof ControlFlow)
                {
                    int iStop=10;
                    ControlFlow flow=((ControlFlow)(listEdges.get(e)));
                    ControlFlow flowStart=((ControlFlow)(listEdges.get(e)));
                    
                    if ((flow.getTarget() instanceof Action)&&
                        ((StereotypesHelper.hasStereotype(flow.getTarget(),UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))||
                        (StereotypesHelper.hasStereotype(flow.getTarget(),UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))))
                    {
                        ActivityNode source=flow.getSource();
                        for (int e2=0;e2<listEdges.size();e2++)
                        {
                            if (e2==e)
                            {
                                continue;
                            }
                            
                            if ((listEdges.get(e2) instanceof ControlFlow)&&
                                (listEdges.get(e2).getTarget() instanceof Action)&&
                                ((StereotypesHelper.hasStereotype(listEdges.get(e2).getTarget(),UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))||
                                (StereotypesHelper.hasStereotype(listEdges.get(e2).getTarget(),UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))))
                            {
                                if (listEdges.get(e2).getSource()==flow.getTarget())
                                {
                                    iStop--;
                                    if (iStop>0)
                                    {
                                        flow=((ControlFlow)(listEdges.get(e2)));
                                        e2=0;
                                    }
                                }
                            }
                            else if (listEdges.get(e2) instanceof ControlFlow)
                            {
                                ControlFlow flowOld=((ControlFlow)(listEdges.get(e2)));
                                if (flowOld.getSource()==flow.getTarget())
                                {
                                    ControlFlow flowNew=Application.getInstance().getProject().getElementsFactory().createControlFlowInstance();
                                    acEdges.put(flowStart,flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flow.getGuard());
                                    flowNew.setInStructuredNode(flow.getInStructuredNode());
                                    flowNew.setInterrupts(flow.getInterrupts());
                                    flowNew.setName(flow.getName());
                                    flowNew.setNameExpression(flow.getNameExpression());
                                    flowNew.setSource(acNodes.get(source));
                                    flowNew.setTarget(acNodes.get(flowOld.getTarget()));
                                    flowNew.setVisibility(flow.getVisibility());
                                    flowNew.setWeight(flow.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                            else if (listEdges.get(e2) instanceof ObjectFlow)
                            {
                                ObjectFlow flowOld2=((ObjectFlow)(listEdges.get(e2)));
                                if (flowOld2.getSource()==flow.getTarget())
                                {
                                    ObjectFlow flowNew=Application.getInstance().getProject().getElementsFactory().createObjectFlowInstance();
                                    acEdges.put(listEdges.get(e),flowNew);

                                    flowNew.setActivity(cActivity);
                                    flowNew.setGuard(flow.getGuard());
                                    flowNew.setInStructuredNode(flow.getInStructuredNode());
                                    flowNew.setInterrupts(flow.getInterrupts());
                                    flowNew.setMulticast(flowOld2.isMulticast());
                                    flowNew.setMultireceive(flowOld2.isMultireceive());
                                    flowNew.setName(flow.getName());
                                    flowNew.setNameExpression(flow.getNameExpression());
                                    flowNew.setSelection(flowOld2.getSelection());
                                    flowNew.setSource(acNodes.get(flow.getSource()));
                                    flowNew.setTarget(acNodes.get(flowOld2.getTarget()));
                                    flowNew.setTransformation(flowOld2.getTransformation());
                                    flowNew.setVisibility(flow.getVisibility());
                                    flowNew.setWeight(flow.getWeight());
                                    flowNew.setOwner(cActivity);
                                }
                            }
                        }
                    }
                    else
                    {
                        ControlFlow flowNew=Application.getInstance().getProject().
                                        getElementsFactory().createControlFlowInstance();
                        acEdges.put(listEdges.get(e),flowNew);

                        flowNew.setActivity(cActivity);
                        flowNew.setGuard(flow.getGuard());
                        flowNew.setInStructuredNode(flow.getInStructuredNode());
                        flowNew.setInterrupts(flow.getInterrupts());
                        flowNew.setName(flow.getName());
                        flowNew.setNameExpression(flow.getNameExpression());
                        flowNew.setSource(acNodes.get(flow.getSource()));
                        flowNew.setTarget(acNodes.get(flow.getTarget()));
                        flowNew.setVisibility(flow.getVisibility());
                        flowNew.setWeight(flow.getWeight());
                        flowNew.setOwner(cActivity);
                    }
                }
            }
            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }
            
            if (!SessionManager.getInstance().isSessionCreated())
            {
                bCreated=true;
                SessionManager.getInstance().createSession("Create Workflow");
            }

            Iterator<Diagram> itDiagrams=((Activity)(listActivities.get(i))).getOwnedDiagram().iterator();
            while (itDiagrams.hasNext())
            {
                HashMap<Element,PresentationElement> acElementPresentation=new HashMap<Element,PresentationElement>();
                Diagram diagramOld=((Diagram)(itDiagrams.next()));

                DiagramPresentationElement prOld=Application.getInstance().getProject().getDiagram(diagramOld);
                prOld.open();
                try
                {
                    Diagram diagramNew=ModelElementsManager.getInstance().
                        createDiagram(DiagramTypeConstants.UML_ACTIVITY_DIAGRAM,cActivity);

                    Iterator<PresentationElement> itPresentations=prOld.getPresentationElements().iterator();
                    ArrayList<PresentationElement> tempPresentations=new ArrayList<PresentationElement>();
                    ArrayList<PresentationElement> tempParents=new ArrayList<PresentationElement>();
                    while (itPresentations.hasNext())
                    {
                        tempPresentations.add(itPresentations.next());
                        tempParents.add(null);
                    }
                    
                    for (int p=0;p<tempPresentations.size();p++)
                    {
                        ShapeElement elShape=null;
                        
                        if (tempPresentations.get(p).getElement() instanceof ActivityNode)
                        {
                            int iAddParent=0;
                            if (tempPresentations.get(p).getElement() instanceof StructuredActivityNode)
                            {
                                Iterator<PresentationElement> itPresentationsIn=tempPresentations.get(p).getPresentationElements().iterator();
                                while (itPresentationsIn.hasNext())
                                {
                                    PresentationElement tempPresentation=itPresentationsIn.next();
                                    
                                    boolean bAdd=true;
                                    for (int a=0;a<tempPresentations.size();a++)
                                    {
                                        if (tempPresentation.getElement()==tempPresentations.get(a).getElement())
                                        {
                                            bAdd=false;
                                        }
                                    }
                                    
                                    if (bAdd)
                                    {
                                        tempPresentations.add(tempPresentation);
                                        iAddParent++;
                                    }
                                }
                            }
                               
                            if (acNodes.get(((ActivityNode)(tempPresentations.get(p).getElement())))!=null)
                            {
                                try
                                {
                                    if (tempParents.get(p)==null)
                                    {

                                        elShape=PresentationElementsManager.getInstance().
                                            createShapeElement(acNodes.get(((ActivityNode)(tempPresentations.get(p).getElement()))),
                                            Application.getInstance().getProject().getDiagram(diagramNew));
                                    }
                                    else
                                    {
                                        elShape=PresentationElementsManager.getInstance().
                                            createShapeElement(acNodes.get(((ActivityNode)(tempPresentations.get(p).getElement()))),
                                            tempParents.get(p));
                                    }

                                    if (elShape!=null)
                                    {
                                        for (int t=0;t<iAddParent;t++)
                                        {
                                            tempParents.add(elShape);
                                        }
                                        Rectangle rectBounds = tempPresentations.get(p).getBounds();
                                        PresentationElementsManager.getInstance().reshapeShapeElement(elShape,rectBounds);
                                        acElementPresentation.put(acNodes.get(((ActivityNode)(tempPresentations.get(p).getElement()))),elShape);
                                        listShapes.add(elShape);
                                        listMasters.add(tempPresentations.get(p));
                                    }
                                }
                                catch (Exception e)
                                {
                                    Application.getInstance().getGUILog().showError("Exception in ReqToProTransformationRules.addWorkflow(): "+e.toString());
                                }
                            }
                        }
                    }
                    
                    itPresentations=prOld.getPresentationElements().iterator();
                    while (itPresentations.hasNext())
                    {
                        tempPresentations.add(itPresentations.next());
                    }

                    for (int p=0;p<tempPresentations.size();p++)
                    {
                        ShapeElement elShape=null;

                        if (tempPresentations.get(p).getElement() instanceof ActivityNode)
                        {
                            if (tempPresentations.get(p).getElement() instanceof StructuredActivityNode)
                            {
                                Iterator<PresentationElement> itPresentationsIn=tempPresentations.get(p).getPresentationElements().iterator();
                                while (itPresentationsIn.hasNext())
                                {
                                    tempPresentations.add(itPresentationsIn.next());
                                }
                            }

                            Iterator<PresentationElement> itPresentations2=tempPresentations.get(p).getPresentationElements().iterator();

                            ArrayList<ShapeElement> shapePins=new ArrayList<ShapeElement>();
                            while (itPresentations2.hasNext())
                            {
                                PresentationElement tempPresentation=itPresentations2.next();

                                if (tempPresentation.getElement() instanceof Pin)
                                {
                                    try
                                    {
                                        for (int s=0;s<listShapes.size();s++)
                                        {
                                            if ((acNodes.get(((Pin)(tempPresentation.getElement())))!=null)&&
                                                (listShapes.get(s).getElement()==acNodes.get(((Pin)(tempPresentation.getElement()))).getOwner()))
                                            {
                                                elShape=listShapes.get(s);
                                                break;
                                            }
                                        }

                                        if (elShape!=null)
                                        {
                                            ShapeElement elShape2=PresentationElementsManager.getInstance().
                                                createShapeElement(acNodes.get(((Pin)(tempPresentation.getElement()))),elShape);

                                            elShape2.setBounds(tempPresentation.getBounds());
                                            shapePins.add(elShape2);
                                            elShape.addPresentationElement(elShape2);
                                            acElementPresentation.put(acNodes.get(((Pin)(tempPresentation.getElement()))),elShape2);
                                            listShapes.add(elShape2);

                                            listMasters.add(tempPresentation);
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Application.getInstance().getGUILog().showError("Exception in ReqToProTransformation.addWorkflow(): "+e.toString());
                                    }
                                }
                            }
                            shapeActionPins.add(shapePins);
                        }
                    }
 
                    itPresentations=prOld.getPresentationElements().iterator();
                    while (itPresentations.hasNext())
                    {
                        try
                        {
                            PresentationElement tempPresentation=itPresentations.next();
                            if ((tempPresentation.getElement() instanceof ActivityEdge)&&
                                (acEdges.get(((ActivityEdge)(tempPresentation.getElement())))!=null))
                            {
                                PathElement pathElement=PresentationElementsManager.getInstance().
                                        createPathElement(acEdges.get(((ActivityEdge)(tempPresentation.getElement()))),
                                        acElementPresentation.get(acEdges.get(((ActivityEdge)(tempPresentation.getElement()))).getSource()),
                                        acElementPresentation.get(acEdges.get(((ActivityEdge)(tempPresentation.getElement()))).getTarget()));

                                if (pathElement!=null)
                                { 
                                    Application.getInstance().getProject().getDiagram(diagramNew).addPresentationElement(pathElement);
                                }
                            }

                            Iterator<PresentationElement> itPresentation2=tempPresentation.getPresentationElements().iterator();
                            while (itPresentation2.hasNext())
                            {
                                PresentationElement cTemp2=itPresentation2.next();

                                if ((cTemp2.getElement() instanceof ActivityEdge)&&
                                    (acEdges.get(((ActivityEdge)(cTemp2.getElement())))!=null))
                                {
                                    PathElement cPath=PresentationElementsManager.getInstance().
                                        createPathElement(acEdges.get(((ActivityEdge)(cTemp2.getElement()))),
                                        acElementPresentation.get(acEdges.get(((ActivityEdge)(cTemp2.getElement()))).getSource()),
                                        acElementPresentation.get(acEdges.get(((ActivityEdge)(cTemp2.getElement()))).getTarget()));


                                    Application.getInstance().getProject().getDiagram(diagramNew).addPresentationElement(cPath);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Application.getInstance().getGUILog().showError("Exception in ReqToProTransformation.addWorkflow(): "+e.toString());
                        }
                    }
                    prOld.close();
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError("Exception in ReqToProTransformation.addWorkflow(): "+e.toString());
                    return(false);
                }
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }

        }
        
        for (int a=0;a<shapeActionPins.size();a++)
        {
            int iTries=10;
            boolean bRetry=false;
            for (int p=0;p<shapeActionPins.get(a).size();p++)
            {
                for (int p2=0;p2<p;p2++)
                {
                    if (shapeActionPins.get(a).get(p2).getBounds().intersects(shapeActionPins.get(a).get(p).getBounds()))
                    {
                        Rectangle rectTemp=shapeActionPins.get(a).get(p2).getBounds();
                        rectTemp.x+=shapeActionPins.get(a).get(p).getBounds().width*2;
                        rectTemp.y+=shapeActionPins.get(a).get(p).getBounds().height;
                        shapeActionPins.get(a).get(p2).setBounds(rectTemp);
                        
                        bRetry=true;
                    }
                }
                if ((bRetry)&&(iTries>0))
                {
                    p=-1;
                    iTries--;
                    bRetry=false;
                }
            }
        }

        for (int i=0;i<listShapes.size();i++)
        {

            com.nomagic.magicdraw.properties.Property cProp=listShapes.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            listShapes.get(i).changeProperty(cProp);
            
            if ((listShapes.get(i).getProperty("SHOW_NAME")!=null)&&
                ((listMasters.get(i).getProperty("SHOW_NAME")==null)||
                (listMasters.get(i).getProperty("SHOW_NAME").getValue().equals(false))))
            {
                cProp=listShapes.get(i).getProperty("SHOW_NAME").clone();
                cProp.setValue(false);
                listShapes.get(i).changeProperty(cProp);
            }
        }
        
        for (int i=0;i<nodesRemove.size();i++)
        {
            nodesRemove.get(i).dispose();
        }
        return(true);
    }
}