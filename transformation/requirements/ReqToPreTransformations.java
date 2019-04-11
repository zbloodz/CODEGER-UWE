/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.transformation.requirements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeClassPres;
import magicUWE.stereotypes.UWEStereotypeProcessFlow;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;
import magicUWE.stereotypes.requirements.types.UWEDisplayActionType;
import magicUWE.stereotypes.requirements.types.UWEDisplayPinType;
import magicUWE.stereotypes.requirements.types.UWEInteractionPinType;
import magicUWE.stereotypes.requirements.types.UWENavigationActionType;
import magicUWE.stereotypes.tags.UWETagInputElement;
import magicUWE.stereotypes.tags.UWETagOutputElement;
import magicUWE.stereotypes.tags.UWETagPresentationElement;
import magicUWE.stereotypes.tags.UWETagPresentationGroup;
import magicUWE.stereotypes.tags.UWETagSelection;
import magicUWE.stereotypes.tags.UWETagSystemAction;
import magicUWE.stereotypes.tags.UWETagTextInput;
import magicUWE.stereotypes.tags.UWETagUserAction;
import magicUWE.stereotypes.tags.requirements.UWETagDisplayAction;
import magicUWE.stereotypes.tags.requirements.UWETagDisplayPin;
import magicUWE.stereotypes.tags.requirements.UWETagInteractionPin;
import magicUWE.stereotypes.tags.requirements.UWETagPresentationPin;
import magicUWE.stereotypes.tags.requirements.UWETagRequirementsAction;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ClassView;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityEdge;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.AggregationKindEnum;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

/**
 *
 * @author PST LMU
 */
public class ReqToPreTransformations 
{
    // Additional distance between presentation classes or properties in a diagram when computing collisions
    public static final int iMIN_DISTANCE_X=20;
    public static final int iMIN_DISTANCE_Y=20;
    public static final int iMIN_PROP_DISTANCE_X=5;
    public static final int iMIN_PROP_DISTANCE_Y=5;
    
    // Names of some presentation elements
    public static final String sVALIDATION_TEXT="ErrorMessage";
    public static final String sCONFIRMATION_OK="Confirm";
    public static final String sCONFIRMATION_CANCEL="Cancel";
    
    // Name for the main alternatives group
    public static final String sVIEWS_NAME="Views";
    
    // Name for the home navigation class
    public static final String sHOME_NAME="Home";
    
    
    /** Removes all elements inside a presentation class
     * 
     * @param cElement presentation class
     */
    public static void clearElement(Element cElement)
    {
        Class cClass=null;
        if (cElement instanceof Class)
        {
            cClass=((Class)(cElement));
        }
        else if ((cElement instanceof Property)&&
            (((Property)(cElement)).getType() instanceof Class))    
        {
            cClass=((Class)(((Property)(cElement)).getType()));
        }
        else 
        {
            return;
        }
        
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Clear Elements");
        }

        ArrayList<Property> listProperties=new ArrayList<Property>();
        ArrayList<Class> listClasses=new ArrayList<Class>();
        Iterator<Property> itProps=cClass.getAttribute().iterator();
        while (itProps.hasNext())
        {
            Property prop=itProps.next();

            if (prop.getAssociation()==null)
            {
                listProperties.add(prop);
            }
        }
        
        Iterator<Element> itElements=cClass.getOwnedElement().iterator();
        while (itElements.hasNext())
        {
            Element el=itElements.next();
            if (el instanceof Class)
            {
                listClasses.add(((Class)(el)));
            }
        }
        
        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }
        for (int i=0;i<listClasses.size();i++)
        {
            listClasses.get(i).dispose();
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }
    
    /** Collects all candidates of  top level presentation groups
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of presentation group candidates
     */
    public static ArrayList<NamedElement> getTopPresentationGroupCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString(), null, UseCase.class, false, listReqModels, false);

        if (bRemoveDuplicates)
        {
            for (int i=listReturn.size()-1;i>=0;i--)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listReturn.get(i).getName(), false,false)<i)
                {
                    listReturn.remove(i);
                }
            }
        }

        ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_PACKAGE.toString(), null, Package.class, false, listReqModels, false);
        if (!listPackages.isEmpty())
        {
            ArrayList<NamedElement> listTemp=ElementCollector.getNamedElements(null, null, UseCase.class, false, listPackages, false);
            for (int i=0;i<listTemp.size();i++)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listTemp.get(i).getName(), false,false)==ElementCollector.iNO_ELEMENT)
                {
                    listReturn.add(listTemp.get(i));
                }
            }
        }
        return(listReturn);
    }
    
    /** Collects all candidates of  top level input forms
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of input form candidates
     */
    public static ArrayList<NamedElement> getTopInputFormCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements("requirementsModel", null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString(), null, UseCase.class, false, listReqModels, false);

        if (bRemoveDuplicates)
        {
            for (int i=listReturn.size()-1;i>=0;i--)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listReturn.get(i).getName(), false,false)<i)
                {
                    listReturn.remove(i);
                }
            }
        }

        ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString(), null, Package.class, false, listReqModels, false);
        if (!listPackages.isEmpty())
        {
            ArrayList<NamedElement> listTemp=ElementCollector.getNamedElements(null, null, UseCase.class, false, listPackages, false);
            for (int i=0;i<listTemp.size();i++)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listTemp.get(i).getName(), false,false)==ElementCollector.iNO_ELEMENT)
                {
                    listReturn.add(listTemp.get(i));
                }
            }
        }
        return(listReturn);
    }
    
    /** Creates a top level presentation group or input form
     * 
     * @param sName name of the class
     * @param position position of the presentation element
     * @param cPackage package for the class
     * @param bGroup is is a presentation group?
     * @return class and presentation element
     */
    public static ElementCollector.ReturnElement createPresentationClass(String sName,
            Point position,
            Package cPackage,
            boolean bGroup)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Presentation Class");
        }
        ElementCollector.ReturnElement elReturn=null;
        Project cProject = Application.getInstance().getProject();
        DiagramPresentationElement cDiagram = cProject.getActiveDiagram();
        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(null, sName, Class.class, true, cPackage, true);

        if (cDiagram!=null)
        {
            Class cReturn=null;

            if (listOld.isEmpty())
            {
                cReturn = Application.getInstance().getProject().getElementsFactory().createClassInstance();

                if (cPackage==null)
                {
                    cReturn.setOwner(cDiagram.getDiagram().getOwner());
                }
                else
                {
                    cReturn.setOwner(cPackage);
                }
                cReturn.setName(sName);
            }
            else
            {
                cReturn=((Class)(listOld.get(0)));
            }
            if (bGroup)
            {
                StereotypesHelper.addStereotypeByString(cReturn,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
            }
            else
            {
                StereotypesHelper.addStereotypeByString(cReturn,UWEStereotypeClassPres.INPUT_FORM.toString());
            }

            ShapeElement elShape=null;
            try
            {
                elShape=PresentationElementsManager.getInstance().createShapeElement(cReturn,cDiagram);

                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ruleNavigationClass "+e.toString());
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }

            com.nomagic.magicdraw.properties.Property cProp=elShape.getProperty("SUPPRESS_CLASS_ATTRIBUTES").clone();
            cProp.setValue(true);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("SUPPRESS_CLASS_OPERATIONS").clone();
            cProp.setValue(true);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            cProp=elShape.getProperty("SUPPRESS_STRUCTURE").clone();
            cProp.setValue(false);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            elShape.changeProperty(cProp);
            return(new ElementCollector.ReturnElement(cReturn,elShape,0,null,null));
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return(elReturn);
    }
    
    /** Removes all elements inside a presentation property
     * 
     * @param cProperty presentation property
     */
    public static void clearElement(Property cProperty)
    {
        if ((cProperty.getType()==null)||
            (!(cProperty.getType() instanceof Class)))
        {
            return;
        }
        Class cClass=((Class)(cProperty.getType()));
        
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Clear Elements");
        }

        ArrayList<Property> listProperties=new ArrayList<Property>();
        ArrayList<Class> listClasses=new ArrayList<Class>();
        Iterator<Property> itProps=cClass.getAttribute().iterator();
        while (itProps.hasNext())
        {
            Property prop=itProps.next();

            if (prop.getAssociation()==null)
            {
                listProperties.add(prop);
            }
        }
        
        Iterator<Element> itElements=cClass.getOwnedElement().iterator();
        while (itElements.hasNext())
        {
            Element el=itElements.next();
            if (el instanceof Class)
            {
                listClasses.add(((Class)(el)));
            }
        }
        
        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }
        for (int i=0;i<listClasses.size();i++)
        {
            listClasses.get(i).dispose();
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }
    
    /** Collects all candidates of presentation groups
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of presentation group candidates
     */
    public static ArrayList<NamedElement> getPresentationGroupCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString(), 
                null, UseCase.class, false, listReqModels, false);

        if (bRemoveDuplicates)
        {
            for (int i=listReturn.size()-1;i>=0;i--)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listReturn.get(i).getName(), false,false)<i)
                {
                    listReturn.remove(i);
                }
            }
        }

        ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_PACKAGE.toString(), 
                null, Package.class, false, listReqModels, false);
        if (!listPackages.isEmpty())
        {
            ArrayList<NamedElement> tempUseCases=ElementCollector.getNamedElements(null, null, UseCase.class, false, listPackages, false);
            for (int i=0;i<tempUseCases.size();i++)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, tempUseCases.get(i).getName(), false,false)==ElementCollector.iNO_ELEMENT)
                {
                    listReturn.add(tempUseCases.get(i));
                }
            }
        }
        
        ArrayList<NamedElement> listActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listReqModels, false);
        for (int i=0;i<listActions.size();i++)
        {
            Property property = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),UWEDisplayActionType.sTagName);

            Slot slot=StereotypesHelper.getSlot(listActions.get(i),property,false,false);
            if ((slot==null)||(slot.getValue().size()==0))
            {
                listReturn.add(listActions.get(i));
            }
            else if ((slot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(slot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(slot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.GROUP.getName())))
            {
                listReturn.add(listActions.get(i));
            }
        }
        return(listReturn);
    }
    
    /** Collects all candidates of input forms
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of input form candidates
     */
    public static ArrayList<NamedElement> getInputFormCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_USECASE.toString(), 
                null, UseCase.class, false, listReqModels, false);

        if (bRemoveDuplicates)
        {
            for (int i=listReturn.size()-1;i>=0;i--)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listReturn.get(i).getName(), false,false)<i)
                {
                    listReturn.remove(i);
                }
            }
        }

        ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.PROCESSING_PACKAGE.toString(), 
                null, Package.class, false, listReqModels, false);
        if (!listPackages.isEmpty())
        {
            ArrayList<NamedElement> tempUseCases=ElementCollector.getNamedElements(null, null, UseCase.class, false, listPackages, false);
            for (int i=0;i<tempUseCases.size();i++)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, tempUseCases.get(i).getName(), false,false)==ElementCollector.iNO_ELEMENT)
                {
                    listReturn.add(tempUseCases.get(i));
                }
            }
        }
        
        ArrayList<NamedElement> listActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listReqModels, false);
        for (int i=0;i<listActions.size();i++)
        {
            Property cProperty = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),UWEDisplayActionType.sTagName);

            Slot cSlot=StereotypesHelper.getSlot(listActions.get(i),cProperty,false,false);
            if ((cSlot==null)||(cSlot.getValue().size()==0))
            {
                listReturn.add(listActions.get(i));
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.FORM.getName())))
            {
                listReturn.add(listActions.get(i));
            }
        }
        return(listReturn);
    }
    
    /** Collects all candidates of iterated presentation groups
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of iterated presentation group candidates
     */
    public static ArrayList<NamedElement> getIteratedPresentationGroupCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
        
        ArrayList<NamedElement> listActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listReqModels, false);
        for (int i=0;i<listActions.size();i++)
        {
            Property property = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),UWEDisplayActionType.sTagName);

            Slot slot=StereotypesHelper.getSlot(listActions.get(i),property,false,false);
            if ((slot==null)||(slot.getValue().isEmpty()))
            {
                listReturn.add(listActions.get(i));
            }
            else if ((slot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(slot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(slot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.ITERATION.getName())))
            {
                listReturn.add(listActions.get(i));
            }
        }
        return(listReturn);
    }
    
    /** Collects all candidates of presentation alternatives
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of iterated presentation alternative candidates
     */
    public static ArrayList<NamedElement> getPresentationAlternativesCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements("requirementsModel", null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
            return(new ArrayList<NamedElement>());
        }
        
        ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
        
        ArrayList<NamedElement> listActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listReqModels, false);
        for (int i=0;i<listActions.size();i++)
        {
            Property property = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),UWEDisplayActionType.sTagName);

            Slot slot=StereotypesHelper.getSlot(listActions.get(i),property,false,false);
            if ((slot==null)||(slot.getValue().isEmpty()))
            {
                listReturn.add(listActions.get(i));
            }
            else if ((slot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(slot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(slot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.ALTERNATIVES.getName())))
            {
                listReturn.add(listActions.get(i));
            }
        }
        return(listReturn);
    }
    
    /** Fills the presentation class with child elements
     * 
     * @param cElement main presentation element
     * @param bFlat only add direct children
     */
    public static int addPresentationChildren(Element cElement,
            boolean bFlat,
            ArrayList<NamedElement> listAdditionalNavigationActions)
    {
        int iReturn=0;
        Class cMain=null;
        if (cElement instanceof Class)
        {
            cMain=((Class)(cElement));
        }
        else if (cElement instanceof Property)
        {
            if ((((Property)(cElement)).getType()==null)||
                (!(((Property)(cElement)).getType() instanceof Class)))
            {
                return(iReturn);
            }
            cMain=((Class)(((Property)(cElement)).getType()));
        }
        else
        {
            return(iReturn);
        }
        
        ArrayList<ElementCollector.ReturnElement> listReturn=new ArrayList<ElementCollector.ReturnElement>();
        
        ArrayList<NamedElement> listGroups=null;
        if (StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassPres.PRESENTATION_GROUP.toString()))
        {
            listGroups=getPresentationGroupCandidates(true);
        }
        else if (StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassPres.INPUT_FORM.toString()))
        {
            listGroups=getInputFormCandidates(true);
        }
        else if (StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassPres.ITERATED_PRESENTATION_GROUP.toString()))
        {
            listGroups=getIteratedPresentationGroupCandidates(true);
        }
        else if (StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString()))
        {
            listGroups=getPresentationAlternativesCandidates(true);
        }
        
        if ((listGroups==null)||(listGroups.isEmpty())||
            ((ElementCollector.getNamedElementFromArrayList(listGroups, cMain.getName(), false, true))==ElementCollector.iNO_ELEMENT))
        {
            return(iReturn);
        }
        
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Presentation Group Children");
        }
        ArrayList<NamedElement> listSource=new ArrayList<NamedElement>();
        listSource.add(listGroups.get(ElementCollector.getNamedElementFromArrayList(listGroups, cMain.getName(), false, true)));

        ArrayList<NamedElement> listDisplayActions=null;
        ArrayList<NamedElement> listNavigationActions=null;
        ArrayList<NamedElement> listDisplayPins=null;
        ArrayList<NamedElement> listInteractionPins=null;
        ArrayList<NamedElement> listSystemActions=null;
        ArrayList<NamedElement> listUserActions=null;
        if (listSource.get(0) instanceof Action)
        {
            listDisplayActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listSource, true);
            listNavigationActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), 
                null, Action.class, true, listSource, true);
            listDisplayPins=ElementCollector.getNamedElements(UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(),
                null, Pin.class, true, listSource, true);
            listInteractionPins=ElementCollector.getNamedElements(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),
                null, Pin.class, true, listSource, true);
            listSystemActions=ElementCollector.getNamedElements(UWEStereotypeProcessFlow.SYSTEM_ACTION.toString(), 
                null, Action.class, true, listSource, true);
            listUserActions=ElementCollector.getNamedElements(UWEStereotypeProcessFlow.USER_ACTION.toString(), 
                null, Action.class, true, listSource, true);
        }
        else if (listSource.get(0) instanceof UseCase)
        {
            ArrayList<NamedElement> listActivities=ElementCollector.getNamedElements(null, null, Activity.class, true, listSource, true);
            listDisplayActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                null, Action.class, true, listActivities, true);
            listNavigationActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), 
                null, Action.class, true, listActivities, true);
            listDisplayPins=new ArrayList<NamedElement>();
            listInteractionPins=new ArrayList<NamedElement>();
            listSystemActions=ElementCollector.getNamedElements(UWEStereotypeProcessFlow.SYSTEM_ACTION.toString(), 
                null, Action.class, true, listActivities, true);
            listUserActions=ElementCollector.getNamedElements(UWEStereotypeProcessFlow.USER_ACTION.toString(), 
                null, Action.class, true, listActivities, true);
        }
        int iMaxTest=listNavigationActions.size();
        if (listAdditionalNavigationActions!=null)
        {
            listNavigationActions.addAll(listAdditionalNavigationActions);
        }
        ArrayList<ArrayList<NamedElement>> listAdditionalChildren=new ArrayList<ArrayList<NamedElement>>();
        for (int i=0;i<listDisplayActions.size();i++)
        {
            listAdditionalChildren.add(new ArrayList<NamedElement>());
        }

        ArrayList<Property> listNewProps=new ArrayList<Property>();
        ArrayList<Class> listNewClasses=new ArrayList<Class>();
        boolean bConfirmed=false;
        boolean bValidated=false;
        for (int i=0;i<listSystemActions.size();i++)
        {
            Property propRequirements=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeProcessFlow.SYSTEM_ACTION.toString()),UWETagSystemAction.CONFIRMED.toString());
                       
            Slot cSlotOld=StereotypesHelper.getSlot(listSystemActions.get(i),propRequirements,false,false);
            if ((cSlotOld!=null)&&(cSlotOld.getValue().size()>0))
            {
                bConfirmed=true;
                break;
            }
        }
        for (int i=0;i<listUserActions.size();i++)
        {
            Property propRequirements=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeProcessFlow.USER_ACTION.toString()),UWETagUserAction.VALIDATED.toString());
                       
            Slot cSlotOld=StereotypesHelper.getSlot(listUserActions.get(i),propRequirements,false,false);
            if ((cSlotOld!=null)&&(cSlotOld.getValue().size()>0))
            {
                bValidated=true;
                break;
            }
        }
        
        for (int i=0;i<listDisplayActions.size();i++)
        {
            Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),UWEDisplayActionType.sTagName);
            Slot cSlot=StereotypesHelper.getSlot(listDisplayActions.get(i),propOld,false,false);
            
            Property propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(listDisplayActions.get(i).getName().replaceAll(" ",""));
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            
            String sStereotype=null;
            boolean bAlternative=false;
            if ((cSlot==null)||(cSlot.getValue().isEmpty()))
            {
                sStereotype=UWEStereotypeClassPres.PRESENTATION_GROUP.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.GROUP.getName())))
            {
                sStereotype=UWEStereotypeClassPres.PRESENTATION_GROUP.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.FORM.getName())))
            {
                sStereotype=UWEStereotypeClassPres.INPUT_FORM.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.INPUT_FORM.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.INPUT_FORM.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.ITERATION.getName())))
            {
                sStereotype=UWEStereotypeClassPres.ITERATED_PRESENTATION_GROUP.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.ITERATED_PRESENTATION_GROUP.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.ITERATED_PRESENTATION_GROUP.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayActionType.ALTERNATIVES.getName())))
            {
                sStereotype=UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString());
                bAlternative=true;
            }
            
            if (!bAlternative)
            {
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.COLLAPSE.toString(),
                    UWETagPresentationGroup.COLLAPSE.toString(),listDisplayActions.get(i),cType,propNew);
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.FILTER.toString(),
                    UWETagPresentationGroup.FILTER.toString(),listDisplayActions.get(i),cType,propNew);
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.GALLERY.toString(),
                    UWETagPresentationGroup.GALLERY.toString(),listDisplayActions.get(i),cType,propNew);
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.LIGHTBOX.toString(),
                    UWETagPresentationGroup.LIGHTBOX.toString(),listDisplayActions.get(i),cType,propNew);
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.LIVE_REPORT.toString(),
                    UWETagPresentationGroup.LIVE_REPORT.toString(),listDisplayActions.get(i),cType,propNew);
                addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagDisplayAction.RICH_EDITOR.toString(),
                    UWETagPresentationGroup.RICH_EDITOR.toString(),listDisplayActions.get(i),cType,propNew);
            }
            addTag(UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(),sStereotype,UWETagRequirementsAction.DYNAMIC_DISPLAY.toString(),
                    UWETagPresentationElement.DYNAMIC_DISPLAY.toString(),listDisplayActions.get(i),cType,propNew);
            
            Iterator<ActivityEdge> itEdges=((Action)(listDisplayActions.get(i))).getOutgoing().iterator();
            while (itEdges.hasNext())
            {
                ActivityEdge tempEdge=itEdges.next();
                
                if (StereotypesHelper.hasStereotype(tempEdge.getTarget(),UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()))
                {
                    listAdditionalChildren.get(i).add(tempEdge.getTarget());
                }
            }
        }
        
        for (int i=0;i<listNavigationActions.size();i++)
        {
            if (iMaxTest>i)
            {
                boolean bSkip=true;
                Iterator<ActivityEdge> itEdges=((Action)(listNavigationActions.get(i))).getIncoming().iterator();
                while (itEdges.hasNext())
                {
                    ActivityEdge tempEdge=itEdges.next();
                    if (!StereotypesHelper.hasStereotype(tempEdge.getSource(),UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()))
                    {
                        bSkip=false;
                    }
                }

                if (bSkip)
                {
                    continue;
                }
            }

            Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWENavigationActionType.sTagName);
            Slot cSlot=StereotypesHelper.getSlot(listNavigationActions.get(i),propOld,false,false);
            if ((cSlot!=null)&&(cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.AUTOMATIC.getName())))
            {
                continue;
            }
            
            Property propNew=Application.getInstance().getProject().getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType); 
            cType.setOwner(cMain);
            if ((listNavigationActions.get(i) instanceof CallBehaviorAction)&&(((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()!=null))
            {
                cType.setName(((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior().getName().replaceAll(" ",""));
            }
            else
            {
                cType.setName(listNavigationActions.get(i).getName().replaceAll(" ",""));
            }
            
            propNew.setOwner(cMain);
            propNew.setType(cType);
            listNewProps.add(propNew);
            
            String sStereotype=null;
            if ((cSlot==null)||(cSlot.getValue().isEmpty()))
            {
                sStereotype=UWEStereotypeClassPres.ANCHOR.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.ANCHOR.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.ANCHOR.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.ANCHOR.getName())))
            {
                sStereotype=UWEStereotypeClassPres.ANCHOR.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.ANCHOR.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.ANCHOR.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.BUTTON.getName())))
            {
                sStereotype=UWEStereotypeClassPres.BUTTON.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.BUTTON.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.BUTTON.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.TAB.getName())))
            {
                sStereotype=UWEStereotypeClassPres.TAB.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TAB.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TAB.toString());
            }

            addTag(UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(),sStereotype,UWETagRequirementsAction.DYNAMIC_DISPLAY.toString(),
                    UWETagPresentationElement.DYNAMIC_DISPLAY.toString(),listNavigationActions.get(i),cType,propNew);
        }
        
        for (int i=0;i<listDisplayPins.size();i++)
        {
            
            Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString()),UWEDisplayPinType.sTagName);
            Slot cSlot=StereotypesHelper.getSlot(listDisplayPins.get(i),propOld,false,false);
            
            Property propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(listDisplayPins.get(i).getName().replaceAll(" ",""));
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            
            String sStereotype=null;
            if ((cSlot==null)||(cSlot.getValue().isEmpty()))
            {
                sStereotype=UWEStereotypeClassPres.TEXT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TEXT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TEXT.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayPinType.TEXT.getName())))
            {
                sStereotype=UWEStereotypeClassPres.TEXT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TEXT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TEXT.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayPinType.IMAGE.getName())))
            {
                sStereotype=UWEStereotypeClassPres.IMAGE.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.IMAGE.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.IMAGE.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEDisplayPinType.MEDIA.getName())))
            {
                sStereotype=UWEStereotypeClassPres.MEDIA_OBJECT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.MEDIA_OBJECT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.MEDIA_OBJECT.toString());
            }
            
            addTag(UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(),sStereotype,UWETagPresentationPin.DYNAMIC_DISPLAY.toString(),
                    UWETagPresentationElement.DYNAMIC_DISPLAY.toString(),listDisplayPins.get(i),cType,propNew);
            addTag(UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(),sStereotype,UWETagDisplayPin.PERIODIC_REFRESH.toString(),
                    UWETagOutputElement.PERIODIC_REFRESH.toString(),listDisplayPins.get(i),cType,propNew);
            addTag(UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(),sStereotype,UWETagDisplayPin.DRAG_AND_DROP.toString(),
                    UWETagOutputElement.DRAG_DROP.toString(),listDisplayPins.get(i),cType,propNew);
        }
        
        for (int i=0;i<listInteractionPins.size();i++)
        {
            
            Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString()),UWEInteractionPinType.sTagName);
            Slot cSlot=StereotypesHelper.getSlot(listInteractionPins.get(i),propOld,false,false);
            
            Property propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(listInteractionPins.get(i).getName().replaceAll(" ",""));
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            
            boolean bTextInput=false;
            boolean bSelection=false;
            String sStereotype=null;
            if ((cSlot==null)||(cSlot.getValue().isEmpty()))
            {
                sStereotype=UWEStereotypeClassPres.TEXT_INPUT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TEXT_INPUT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TEXT_INPUT.toString());
                bTextInput=true;
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEInteractionPinType.TEXT.getName())))
            {
                sStereotype=UWEStereotypeClassPres.TEXT_INPUT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TEXT_INPUT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TEXT_INPUT.toString());
                bTextInput=true;
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEInteractionPinType.SELECTION.getName())))
            {
                sStereotype=UWEStereotypeClassPres.SELECTION.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.SELECTION.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.SELECTION.toString());
                bSelection=true;
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEInteractionPinType.IMAGE.getName())))
            {
                sStereotype=UWEStereotypeClassPres.IMAGE_INPUT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.IMAGE_INPUT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.IMAGE_INPUT.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEInteractionPinType.FILE.getName())))
            {
                sStereotype=UWEStereotypeClassPres.FILE_UPLOAD.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.FILE_UPLOAD.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.FILE_UPLOAD.toString());
            }
            else if ((cSlot.getValue().get(0) instanceof InstanceValue)&&
                (((InstanceValue)(cSlot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                (((EnumerationLiteral)(((InstanceValue)(cSlot.getValue().get(0))).getInstance())).getName().equals(UWEInteractionPinType.CUSTOM.getName())))
            {
                sStereotype=UWEStereotypeClassPres.CUSTOM_COMPONENT.toString();
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.CUSTOM_COMPONENT.toString());
                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.CUSTOM_COMPONENT.toString());
            }
            
            addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagPresentationPin.DYNAMIC_DISPLAY.toString(),
                    UWETagPresentationElement.DYNAMIC_DISPLAY.toString(),listInteractionPins.get(i),cType,propNew);            
            addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagInteractionPin.SUBMIT_CHANGE.toString(),
                    UWETagInputElement.SUBMIT_CHANGE.toString(),listInteractionPins.get(i),cType,propNew);
            
            if (bTextInput)
            {        
                addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagInteractionPin.AUTO_COMPLETION.toString(),
                    UWETagTextInput.AUTO_COMPLETION.toString(),listInteractionPins.get(i),cType,propNew);            
                addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagInteractionPin.AUTO_SUGGESTION.toString(),
                    UWETagTextInput.AUTO_SUGGESTION.toString(),listInteractionPins.get(i),cType,propNew);            
                addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagInteractionPin.LIVE_VALIDATION.toString(),
                    UWETagTextInput.LIVE_VALIDATION.toString(),listInteractionPins.get(i),cType,propNew);
            }
            if (bSelection)
            {        
                addTag(UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(),sStereotype,UWETagInteractionPin.MULTIPLE_SELECTION.toString(),
                    UWETagSelection.MULTIPLE_SELECTION.toString(),listInteractionPins.get(i),cType,propNew);
            }
        }
        
        if (bValidated)
        {
            
            Property propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(sVALIDATION_TEXT);
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            listNewClasses.add(cType);
            StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.TEXT.toString());
            StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.TEXT.toString());
        }
        if (bConfirmed)
        {
            
            Property propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(sCONFIRMATION_OK);
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            listNewClasses.add(cType);
            StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.BUTTON.toString());
            StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.BUTTON.toString());
            
            propNew=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            propNew.setAggregation(AggregationKindEnum.COMPOSITE);
            cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            listNewClasses.add(cType);
            cType.setOwner(cMain);
            cType.setName(sCONFIRMATION_CANCEL);
            propNew.setType(cType);
            propNew.setOwner(cMain);
            listNewProps.add(propNew);
            listNewClasses.add(cType);
            StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.BUTTON.toString());
            StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.BUTTON.toString());
        }
        
        ArrayList<ShapeElement> listShapes=new ArrayList<ShapeElement>();
        PresentationElement elPresentation=null;
        int iGroups=0;
        for (int x=0;x<listNewProps.size();x++)
        {
            ShapeElement elShape=null;
            
            ArrayList<PresentationElement> listPresentations=new ArrayList<PresentationElement>();
            listPresentations.add(Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement());
            for (int c=0;c<listPresentations.size();c++)
            {
                if ((listPresentations.get(c).getElement()==cElement)&&
                    ((listPresentations.get(c) instanceof PartView)||(listPresentations.get(c) instanceof ClassView)))
                {
                    elPresentation=listPresentations.get(c);
                    break;
                }
                else
                {
                    for (int n=0;n<listPresentations.get(c).getPresentationElements().size();n++)
                    {
                        listPresentations.add(listPresentations.get(c).getPresentationElements().get(n));
                    }
                }
            }
            try
            {
                if (elPresentation instanceof ShapeElement)
                {
                    PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(elPresentation)),
                        new Rectangle(((ShapeElement)(elPresentation)).getBounds().x,((ShapeElement)(elPresentation)).getBounds().y,0,0));
                }
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.addPresentationChildren(): "+e.toString());
            }

            if (elPresentation!=null)
            {
                try
                {
                    elShape=PresentationElementsManager.getInstance().createShapeElement(listNewProps.get(x),elPresentation,true);
                    listReturn.add(new ElementCollector.ReturnElement(listNewClasses.get(x), elShape,0,null,null));

                    PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(elPresentation.getBounds().x+10,
                            elPresentation.getBounds().y+elPresentation.getBounds().height+iGroups*30,0,0));
                    listShapes.add(elShape);
                    
                    iGroups++;
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.addPresentationChildren(): "+e.toString());
                }
            }
        }
        
        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }

        for (int i=0;i<listShapes.size();i++)
        {
            com.nomagic.magicdraw.properties.Property cProp=listShapes.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            listShapes.get(i).changeProperty(cProp);
        }

        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Presentation Group Children");
        }
        for (int i=0;i<listShapes.size();i++)
        {
            try
            {
                PresentationElementsManager.getInstance().reshapeShapeElement(listShapes.get(i),
                        new Rectangle(listShapes.get(i).getBounds().x,listShapes.get(i).getBounds().y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.addPresentationChildren(): "+e.toString());
            }
        }
        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        
        if (!bFlat)
        {
            for (int i=0;i<listNewProps.size();i++)
            {
                if (i<listDisplayActions.size())
                {
                    iReturn+=addPresentationChildren(listNewProps.get(i),false,listAdditionalChildren.get(i));
                }
            }
        }
              
        ElementCollector.removeCollisions(listReturn,ReqToPreTransformations.iMIN_PROP_DISTANCE_X,ReqToPreTransformations.iMIN_PROP_DISTANCE_Y);

        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Presentation Group");
        }
        try
        {
            if (elPresentation instanceof ShapeElement)
            {
                PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(elPresentation)),
                    new Rectangle(((ShapeElement)(elPresentation)).getBounds().x,((ShapeElement)(elPresentation)).getBounds().y,0,0));
            }
        }
        catch (Exception e)
        {
            Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.addPresentationChildren(): "+e.toString());
        }
        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        
        /*int iProps=0;
        Iterator<Element> itChildren=cElement.getOwnedElement().iterator();
        while (itChildren.hasNext())
        {
            if (itChildren.next() instanceof Property)
            {
                iProps++;
            }
        }
        
        if (iProps==1)
        {
            itChildren=cElement.getOwnedElement().iterator();
            while (itChildren.hasNext())
            {
                Element elTemp=itChildren.next();
                if (elTemp instanceof Property)
                {
                    if ((StereotypesHelper.hasStereotype(((Property)(elTemp)).getType(),UWEStereotypeClassPres.PRESENTATION_GROUP.toString()))||
                       (StereotypesHelper.hasStereotype(((Property)(elTemp)).getType(),UWEStereotypeClassPres.INPUT_FORM.toString())))
                    {
                        Application.getInstance().getGUILog().log("A "+((NamedElement)(cElement)).getName());
                    }
                }
            }
        }*/
        
        return(iReturn+listNewProps.size());
    }
    
    /** Adds a tagged value to a presentation element according to a value from the requirements model
     * 
     * @param stereotypeReq stereotype name of the requirements element
     * @param stereotypePre stereotype name of the presentation element
     * @param tagReq tag name of the requirements element
     * @param tagPre tag name of the presentation element
     * @param elReq requirements element
     * @param cElement class to be set
     * @param propElement property to be set
     */
    public static void addTag(String stereotypeReq,
            String stereotypePre,
            String tagReq,
            String tagPre,
            NamedElement elReq,
            Class cElement,
            Property propElement)
    {
        Property propRequirements=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),stereotypeReq),tagReq);
        
        Property propPresentation=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),stereotypePre),tagPre);
                        
        Slot slotOld=StereotypesHelper.getSlot(elReq,propRequirements,false,false);
        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
        {
            Slot slotNewType=StereotypesHelper.getSlot(cElement,propPresentation,true,false);
            LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
            slotNewType.getValue().add(litBoolean);
            Slot cSlotNewProp=StereotypesHelper.getSlot(propElement,propPresentation,true,false);
            litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
            cSlotNewProp.getValue().add(litBoolean);
        }
        
    }

    /** Creates a main presentation page
     * 
     * @param position position of the page
     * @param cPackage package of the page
     * @param listElements list of all top level presentation elements
     * @return class and presentation element
     */
    public static ElementCollector.ReturnElement createHome(Point position,
            Package cPackage,
            ArrayList<ElementCollector.ReturnElement> listElements)
    {
        int iReturn=0;
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Home");
        }
	Project cProject = Application.getInstance().getProject();
        DiagramPresentationElement cDiagram = cProject.getActiveDiagram();

        ArrayList<ShapeElement> listShapes=new ArrayList<ShapeElement>();

        ShapeElement elShape=null;
        Class cReturn=null;
        if (cDiagram!=null)
        {
            cReturn = Application.getInstance().getProject().getElementsFactory().createClassInstance();

            if (cPackage==null)
            {
                cReturn.setOwner(cDiagram.getDiagram().getOwner());
            }
            else
            {
                cReturn.setOwner(cPackage);
            }
            cReturn.setName(sHOME_NAME);

            ShapeElement elView=null;
            try
            {
                elShape=PresentationElementsManager.getInstance().createShapeElement(cReturn,cDiagram);

                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
            }

            StereotypesHelper.addStereotype(cReturn,StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeClassPres.PRESENTATION_PAGE.toString()));

            for (int i=0;i<listElements.size();i++)
            {
                Property propNew=Application.getInstance().getProject().
                        getElementsFactory().createPropertyInstance();

                Class cType=Application.getInstance().getProject().getElementsFactory().createClassInstance();
                cType.setOwner(cReturn);
                cType.setName(listElements.get(i).cClass.getName());
                StereotypesHelper.addStereotypeByString(cType,UWEStereotypeClassPres.BUTTON.toString());

                propNew.setAggregation(AggregationKindEnum.COMPOSITE);
                propNew.setType(cType);
                propNew.setOwner(cReturn);

                StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.BUTTON.toString());
            }

            int iGroups=0;
            int iWidth=0;
            for (int x=0;x<cReturn.getOwnedAttribute().size();x++)
            {
                ShapeElement elTemp=null;
                try
                {
                    elTemp=PresentationElementsManager.getInstance().createShapeElement(cReturn.getOwnedAttribute().get(x),elShape,true);

                    PresentationElementsManager.getInstance().reshapeShapeElement(elTemp,
                            new Rectangle(elShape.getBounds().x,elShape.getBounds().y+iGroups*30,0,0));
                    listShapes.add(elTemp);

                    if (elTemp.getBounds().width>iWidth)
                    {
                        iWidth=elTemp.getBounds().width;
                    }
                    iGroups++;
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
                }
            }

            Property cProperty=Application.getInstance().getProject().
                getElementsFactory().createPropertyInstance();

            cProperty.setAggregation(AggregationKindEnum.COMPOSITE);

            Class cViews=Application.getInstance().getProject().getElementsFactory().createClassInstance();
            cViews.setOwner(cReturn);
            cViews.setName(sVIEWS_NAME);
            StereotypesHelper.addStereotypeByString(cViews,UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString());
            cProperty.setType(cViews);
            cProperty.setOwner(cReturn);
            StereotypesHelper.addStereotypeByString(cProperty,UWEStereotypeClassPres.PRESENTATION_ALTERNATIVES.toString());

            for (int i=0;i<listElements.size();i++)
            {
                Property propNew=Application.getInstance().getProject().
                    getElementsFactory().createPropertyInstance();

                propNew.setAggregation(AggregationKindEnum.COMPOSITE);
                propNew.setType(listElements.get(i).cClass);
                propNew.setOwner(cViews);

                if (StereotypesHelper.hasStereotype(listElements.get(i).cClass,UWEStereotypeClassPres.INPUT_FORM.toString()))
                {
                    StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.INPUT_FORM.toString());
                }
                else if (StereotypesHelper.hasStereotype(listElements.get(i).cClass,UWEStereotypeClassPres.PRESENTATION_GROUP.toString()))
                {
                    StereotypesHelper.addStereotypeByString(propNew,UWEStereotypeClassPres.PRESENTATION_GROUP.toString());
                }
            }

            iGroups=0;
            try
            {
                elView=PresentationElementsManager.getInstance().createShapeElement(cProperty,elShape,true);

                PresentationElementsManager.getInstance().reshapeShapeElement(elView,
                        new Rectangle(elShape.getBounds().x+iWidth+20,elShape.getBounds().y+iGroups*30,0,0));
                iGroups++;
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
            }

            for (int x=0;x<cViews.getOwnedAttribute().size();x++)
            {
                ShapeElement elTemp=null;
                try
                {
                    elTemp=PresentationElementsManager.getInstance().createShapeElement(cViews.getOwnedAttribute().get(x),elView,true);

                    PresentationElementsManager.getInstance().reshapeShapeElement(elTemp,
                            new Rectangle(elView.getBounds().x+10,elView.getBounds().y+iGroups*30,0,0));
                    listShapes.add(elTemp);
                    iGroups++;
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
                }
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }

            com.nomagic.magicdraw.properties.Property cProp=elShape.getProperty("SUPPRESS_CLASS_ATTRIBUTES").clone();
            cProp.setValue(true);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("SUPPRESS_CLASS_OPERATIONS").clone();
            cProp.setValue(true);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            elShape.changeProperty(cProp);

            cProp=elView.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            elView.changeProperty(cProp);

            for (int i=0;i<listShapes.size();i++)
            {
                cProp=listShapes.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
                cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
                listShapes.get(i).changeProperty(cProp);
            }

            if (!SessionManager.getInstance().isSessionCreated())
            {
                bCreated=true;
                SessionManager.getInstance().createSession("Create Home");
            }
            
            for (int i=0;i<listShapes.size();i++)
            {
                try
                {
                    PresentationElementsManager.getInstance().reshapeShapeElement(listShapes.get(i),
                            new Rectangle(listShapes.get(i).getBounds().x,listShapes.get(i).getBounds().y,0,0));
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
                }
            }
            try
            {
                PresentationElementsManager.getInstance().reshapeShapeElement(elView,new Rectangle(elView.getBounds().x,elView.getBounds().y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
            }
            try
            {
                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToPreTransformationRules.createHome(): "+e.toString());
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return(new ElementCollector.ReturnElement(cReturn,elShape,iReturn,null,null));
    }
}
