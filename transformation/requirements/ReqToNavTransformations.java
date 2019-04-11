/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.transformation.requirements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeAssoc;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;
import magicUWE.stereotypes.requirements.types.UWENavigationActionType;
import magicUWE.stereotypes.tags.UWETagNavigationNode;
import magicUWE.stereotypes.tags.requirements.UWETagNavigationAction;
import magicUWE.stereotypes.tags.requirements.UWETagWebUseCase;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.AggregationKindEnum;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceValue;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdusecases.Extend;
import com.nomagic.uml2.ext.magicdraw.mdusecases.Include;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;

/**
 *
 * @author PST LMU
 */
public class ReqToNavTransformations
{
    /** Stores all data needed to create a association
     * 
     */
    public static class AssociationCandidate
    {
        public String sStereotype;
        public Class cFrom;
        public Class cTo;
        public boolean bAsynchronous;
        public boolean bAutomatic;
        public String sGuard;
        public PresentationElement elShape;
        
        AssociationCandidate(String sStereotype,
                Class cFrom,
                Class cTo,
                String sGuard,
                boolean bAsynchronous,
                boolean bAutomatic,
                PresentationElement elShape)
        {
            this.sStereotype=sStereotype;
            this.cFrom=cFrom;
            this.cTo=cTo;
            this.sGuard=sGuard;
            this.bAsynchronous=bAsynchronous;
            this.bAutomatic=bAutomatic;
            this.elShape=elShape;
        }
    };

    // Additional distance between navigation classes or menus in a diagram when computing collisions
    public static final int iMENU_DISTANCE=30;
    public static final int iMIN_DISTANCE_X=50;
    public static final int iMIN_DISTANCE_Y=25;
    
    // Name for the home navigation class
    public static final String sHOME_NAME="Home";

    /** Computes the candidates for navigation classes
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of candidates
     */
    public static ArrayList<NamedElement> getNavigationClassCandidates(boolean bRemoveDuplicates)
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

    /** Computes the candidates for process classes
     * 
     * @param bRemoveDuplicates remove candidates with same name?
     * @return list of candidates
     */
    public static ArrayList<NamedElement> getProcessClassCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
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

    /** Removes all associations from a navigation class
     *
     * @param cClass the navigation class
     */
    public static void clearAssociations(Class cClass)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Clear Associations");
        }

        ArrayList<Property> listProperties=new ArrayList<Property>();
        Iterator<Property> itProps=cClass.getAttribute().iterator();
        while (itProps.hasNext())
        {
            Property prop=itProps.next();

            if (prop.getAssociation()!=null)
            {
                listProperties.add(prop);
            }
        }

        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }

        ArrayList<NamedElement> tempAssociations=ElementCollector.getNamedElements(null, null, Association.class, false, false);

        for (int a=0;a<tempAssociations.size();a++)
        {
            for (int e=0;e<((Association)(tempAssociations.get(a))).getMemberEnd().size();e++)
            {
                if (((Association)(tempAssociations.get(a))).getMemberEnd().get(e).getType()==cClass)
                {
                    tempAssociations.get(a).dispose();
                }
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }

    /** Removes all associations and menus from a navigation class
     *
     * @param cClass the navigation class
     */
    public static void clearAssociationsAndMenu(Class cClass)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Clear Associations and Menus");
        }

        ArrayList<Property> listProperties=new ArrayList<Property>();
        ArrayList<Class> listMenus=new ArrayList<Class>();
        Iterator<Property> itProps=cClass.getAttribute().iterator();
        
        while (itProps.hasNext())
        {
            Property propTemp=itProps.next();

            if (propTemp.getAssociation()!=null)
            {
                listProperties.add(propTemp);
                if (propTemp.getAggregation()==AggregationKindEnum.COMPOSITE)
                {
                	// Update MagicDraw18: replaced propTemp.getAssociation().getEndType() with ends, because it was a List before 
                	// assume propTemp.getAssociation().getEndType() has 2 elements
                	List<Type> ends = new ArrayList<Type>(propTemp.getAssociation().getEndType());
                    if ((ends.get(0)==cClass)&&
                        (StereotypesHelper.hasStereotype(ends.get(1),UWEStereotypeClassNav.MENU.toString()))&&
                        (ends.get(1) instanceof Class))
                    {
                        if (ElementCollector.getNamedElementFromArrayList(listMenus, ((Class)(ends.get(1))).getName(), true,false)==
                                ElementCollector.iNO_ELEMENT)
                        {
                            listMenus.add(((Class)(ends.get(1))));
                        }
                    }
                    else if((ends.get(1) == cClass)&&
                        (StereotypesHelper.hasStereotype(ends.get(0),UWEStereotypeClassNav.MENU.toString()))&&
                        (ends.get(0) instanceof Class))
                    {
                        if (ElementCollector.getNamedElementFromArrayList(listMenus, ((Class)(ends.get(0))).getName(), true,false)==
                                ElementCollector.iNO_ELEMENT)
                        {
                            listMenus.add(((Class)(ends.get(0))));
                        }
                    }
                }
            }
        }
 
        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }

        ArrayList<NamedElement> tempAssociations=ElementCollector.getNamedElements(null, null, Association.class, false, false);

        for (int a=0;a<tempAssociations.size();a++)
        {
            for (int e=0;e<((Association)(tempAssociations.get(a))).getMemberEnd().size();e++)
            {
                if (((Association)(tempAssociations.get(a))).getMemberEnd().get(e).getType()==cClass)
                {
                    tempAssociations.get(a).dispose();
                }
            }
        }

        for (int i=0;i<listMenus.size();i++)
        {
            clearAssociations(listMenus.get(i));
            listMenus.get(i).dispose();
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }

    /**
     * 
     * @param sName
     * @param position
     * @param elParent
     * @return 
     */
    public static ElementCollector.ReturnElement createNavigationClass(String sName,
            Point position,
            NamedElement elParent)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Navigation Class");
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
                cStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeClassNav.NAVIGATION_CLASS.toString());
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
                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createNavigationClass(): "+e.toString());
            }

            if (cStereotype!=null)
            {
                StereotypesHelper.addStereotype(cReturn,cStereotype);
                
                ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements("requirementsModel", null, Model.class, true, true);
                ArrayList<NamedElement> listCases=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString(), sName,
                        UseCase.class, true, listReqModels, false);
                if (listCases.size()>0)
                {
                    Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                            UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString()),UWETagWebUseCase.IS_LANDMARK.toString());

                    Slot slotOld=StereotypesHelper.getSlot(listCases.get(0),propOld,false,false);
                    if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                    {
                        Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),UWETagNavigationNode.IS_LANDMARK.toString());
                        Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                        LiteralBoolean litBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                        litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                        slotNew.getValue().add(litBoolean);
                    }

                    
                    propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                            UWEStereotypeRequirementsUseCases.BROWSING_USECASE.toString()),UWETagWebUseCase.GUARD.toString());

                    slotOld=StereotypesHelper.getSlot(listCases.get(0),propOld,false,false);
                    if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                    {
                        Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),UWETagNavigationNode.GUARD.toString());
                        Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                        LiteralString litString=cProject.getElementsFactory().createLiteralStringInstance();
                        litString.setValue(((LiteralString)(slotOld.getValue().get(0))).getValue());
                        slotNew.getValue().add(litString);
                    }
                }
                
                ArrayList<NamedElement> listPackages=ElementCollector.getNamedElements(UWEStereotypeRequirementsUseCases.BROWSING_PACKAGE.toString(), null,
                        Package.class, true, listReqModels, false);
                for (int p=0;p<listPackages.size();p++)
                {
                    listCases=ElementCollector.getNamedElements(null, sName, UseCase.class, true, listPackages.get(p), false);
                    
                    
                    if (listCases.size()>0)
                    {
                        Property propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeRequirementsUseCases.BROWSING_PACKAGE.toString()),UWETagWebUseCase.IS_LANDMARK.toString());

                        Slot slotOld=StereotypesHelper.getSlot(listPackages.get(p),propOld,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                    UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),UWETagNavigationNode.IS_LANDMARK.toString());
                            Slot slotNew=StereotypesHelper.getSlot(cReturn,propNew,true,false);
                            LiteralBoolean litBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                            litBoolean.setValue(((LiteralBoolean)(slotOld.getValue().get(0))).isValue());
                            slotNew.getValue().add(litBoolean);
                        }


                        propOld = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                UWEStereotypeRequirementsUseCases.BROWSING_PACKAGE.toString()),UWETagWebUseCase.GUARD.toString());

                        slotOld=StereotypesHelper.getSlot(listPackages.get(p),propOld,false,false);
                        if ((slotOld!=null)&&(slotOld.getValue().size()>0))
                        {
                            Property propNew = StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(cProject,
                                    UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),UWETagNavigationNode.GUARD.toString());
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
                    SessionManager.getInstance().createSession("Create Navigation Class");
                }
                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createNavigationClass(): "+e.toString());
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return(new ElementCollector.ReturnElement(cReturn, elShape,0,null,null));
    }
    
    /** Adds a association 
     * 
     * @param sStereotype name of the stereotype
     * @param cFrom source class
     * @param cTo target class
     * @param sGuard guardcondition
     * @param bAsynchronous is this association asynchronous?
     * @param bAutomatic is this association automatic?
     * @return newly created association
     */
    public static Association addAssociation(String sStereotype,
            Class cFrom,
            Class cTo,
            String sGuard,
            boolean bAsynchronous,
            boolean bAutomatic)
    {
        ArrayList<NamedElement> listParents=new ArrayList<NamedElement>();
        listParents.add(cFrom.getPackage());
        listParents.add(cTo.getPackage());
        ArrayList<NamedElement> listAssociations=ElementCollector.getNamedElements(sStereotype, null, Association.class, false, listParents, false);

        for (int i=listAssociations.size()-1;i>=0;i--)
        {
            if (((((Association)(listAssociations.get(i))).getMemberEnd().get(0).getType()==cFrom)&&
                (((Association)(listAssociations.get(i))).getMemberEnd().get(1).getType()==cTo))||
                ((((Association)(listAssociations.get(i))).getMemberEnd().get(1).getType()==cFrom)&&
                (((Association)(listAssociations.get(i))).getMemberEnd().get(0).getType()==cTo)))
            {
                continue;
            }
            listAssociations.remove(i);
        }

        Association cAssociation=null;
        if (listAssociations.isEmpty())
        {
            cAssociation=MagicDrawElementOperations.createAssociation(sStereotype);

            cAssociation.setOwner(cFrom.getOwner());
            Application.getInstance().getProject().addElementByID(cAssociation,cAssociation.getID());

            cAssociation.getOwnedEnd().get(1).setType(cFrom);
            cAssociation.getOwnedEnd().get(0).setType(cTo);
            cAssociation.getOwnedEnd().get(0).setOwner(cFrom);

        }
        else
        {
            cAssociation=(Association)(listAssociations.get(0));

            if (cAssociation.getMemberEnd().get(0).getType()==cTo)
            {
                cAssociation.getMemberEnd().get(0).setOwner(cFrom);
            }
            else if (cAssociation.getMemberEnd().get(1).getType()==cTo)
            {
                cAssociation.getMemberEnd().get(1).setOwner(cFrom);
            }
            cAssociation=null;
        }
        
        if (cAssociation!=null)
        {

            Property propLink=null;
            Slot cSlot=null;
            if (bAutomatic)
            {
                propLink=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeAssoc.NAVIGATION_LINK.toString()),"isAutomatic");
                cSlot=StereotypesHelper.getSlot(cAssociation,propLink,true,false);
                LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                litBoolean.setValue(bAutomatic);
                cSlot.getValue().add(litBoolean);
            }
            
            if ((!sStereotype.equals(UWEStereotypeAssoc.NAVIGATION_LINK.toString()))&&(bAsynchronous))
            {
                propLink=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeAssoc.PROCESS_LINK.toString()),UWETagNavigationAction.ASYNCHRONOUS.toString());
                cSlot=StereotypesHelper.getSlot(cAssociation,propLink,true,false);
                LiteralBoolean litBoolean=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                litBoolean.setValue(bAsynchronous);
                cSlot.getValue().add(litBoolean);
            }

            if (sGuard!=null)
            {
                propLink=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeAssoc.NAVIGATION_LINK.toString()),UWETagNavigationAction.GUARD.toString());
                cSlot=StereotypesHelper.getSlot(cAssociation,propLink,true,false);
                LiteralString litString=Application.getInstance().getProject().getElementsFactory().createLiteralStringInstance();
                litString.setValue(sGuard);
                cSlot.getValue().add(litString);
            }
        }
        return(cAssociation);
    }

    /** Adds a menu to a navigation class
     * 
     * @param cMain navigation class
     * @param prMain presentation element of the navigation class
     * @param position position for the menu
     * @return menu class and its presentation element
     */
    public static ElementCollector.ReturnElement addMenu(Class cMain, 
            PresentationElement prMain,
            Point position)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Menu to Navigation Class");
        }

        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(UWEStereotypeClassNav.MENU.toString(), cMain.getName()+"Menu", Class.class, true, cMain.getPackage(), false);
        Class cMenu=null;
        if (listOld.isEmpty())
        {
            cMenu = Application.getInstance().getProject().getElementsFactory().createClassInstance();
            StereotypesHelper.addStereotype(cMenu,
                StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeClassNav.MENU.toString()));
            cMenu.setName(cMain.getName()+"Menu");

            cMenu.setOwner(cMain.getOwner());
        }
        else
        {
            cMenu=((Class)(listOld.get(0)));
        }

        Association cAssociation=Application.getInstance().getProject().getElementsFactory().createAssociationInstance();

        cAssociation.setOwner(cMain.getOwner());
        Application.getInstance().getProject().addElementByID(cAssociation,cAssociation.getID());

        cAssociation.getOwnedEnd().get(1).setType(cMain);
        cAssociation.getOwnedEnd().get(1).setOwner(cMenu);
        cAssociation.getOwnedEnd().get(0).setType(cMenu);
        cAssociation.getOwnedEnd().get(0).setAggregation(AggregationKindEnum.COMPOSITE);
        cAssociation.getOwnedEnd().get(0).setOwner(cMain);

        ShapeElement elShape=null;
        try
        {
            DiagramPresentationElement prDiagram = Application.getInstance().getProject().getActiveDiagram();
            elShape=PresentationElementsManager.getInstance().createShapeElement(cMenu,prDiagram);

            PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));

            PresentationElementsManager.getInstance().createPathElement(cAssociation,prMain,elShape);

        }
        catch (Exception e)
        {
            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createMenu(): "+e.toString());
            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
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
                SessionManager.getInstance().createSession("Create Navigation Class");
            }
            PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
        }
        catch (Exception e)
        {
            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createMenu(): "+e.toString());
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }

        return (new ElementCollector.ReturnElement(cMenu,elShape,0,null,null));
    }
    
    /** Adds a external node to a navigation class
     * 
     * @param cMain navigation class
     * @param prMain presentation element of the navigation class
     * @param position position for the menu
     * @return menu class and its presentation element
     */
    public static ElementCollector.ReturnElement addExternal(Class cMain,
            String sName, 
            Point position)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add External Node");
        }

        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(UWEStereotypeClassNav.EXTERNAL_NODE.toString(), sName, Class.class, true, cMain.getPackage(), false);

        Class cExternal=null;

        if (listOld.isEmpty())
        {
            cExternal = Application.getInstance().getProject().getElementsFactory().createClassInstance();
            StereotypesHelper.addStereotype(cExternal,
                StereotypesHelper.getStereotype(Application.getInstance().getProject(),UWEStereotypeClassNav.EXTERNAL_NODE.toString()));
            cExternal.setName(sName);

            cExternal.setOwner(cMain.getOwner());
        }
        else
        {
            cExternal=((Class)(listOld.get(0)));
        }

        ShapeElement elShape=null;
        try
        {
            DiagramPresentationElement cDiagram = Application.getInstance().getProject().getActiveDiagram();
            
            ArrayList<PresentationElement> listPresentations=ElementCollector.getDiagramPresentationElements(null, cDiagram.getDiagram(), cExternal);
            
            if (listPresentations.isEmpty())
            {
                elShape=PresentationElementsManager.getInstance().createShapeElement(cExternal,cDiagram);

                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            else if (listPresentations.get(0) instanceof ShapeElement)
            {
                elShape=((ShapeElement)(listPresentations.get(0)));
            }
        }
        catch (Exception e)
        {
            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addExternal(): "+e.toString());
            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
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
                SessionManager.getInstance().createSession("Add External Node");
            }
            PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
        }
        catch (Exception e)
        {
            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addExternal(): "+e.toString());
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return (new ElementCollector.ReturnElement(cExternal,elShape,0,null,null));
    }

    /** Collects candidates for associations of a navigation class
     * 
     * @param cMain naviagiton class
     * @param prMain presentation element of the navigation class
     * @param bProcess is it a process class?
     * @return list of association candidates
     */
    public static ArrayList<AssociationCandidate> collectAssociations(Class cMain,
            PresentationElement prMain, 
            boolean bProcess)
    {
        ArrayList<AssociationCandidate> listAssociations=new ArrayList<AssociationCandidate>();
        ArrayList<NamedElement> tempCases=new ArrayList<NamedElement>();
        if (bProcess)
        {
            tempCases=getProcessClassCandidates(false);
        }
        else
        {
            tempCases=getNavigationClassCandidates(false);
        }
        ArrayList<NamedElement> listNavModels=ElementCollector.getNamedElements(UWEDiagramType.NAVIGATION.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listNavClasses=ElementCollector.getNamedElements(UWEStereotypeClassNav.NAVIGATION_CLASS.toString(),
                null, Class.class, true, listNavModels, false);
        ArrayList<NamedElement> listProModels=ElementCollector.getNamedElements(UWEDiagramType.PROCESS_STRUCTURE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listProClasses=ElementCollector.getNamedElements(UWEStereotypeClassGeneral.PROCESS_CLASS.toString(),
                null, Class.class, true, listProModels, false);
        ArrayList<NamedElement> listProClasses2=ElementCollector.getNamedElements(UWEStereotypeClassGeneral.PROCESS_CLASS.toString(),
                null, Class.class, true, listNavModels, false);
        listProClasses.addAll(listProClasses2);
        ArrayList<NamedElement> listMenus=new ArrayList<NamedElement>();
        for (int i=0;i<listNavClasses.size();i++)
        {
            listMenus.add(null);
        }
        
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listNavigationActions=ElementCollector.getNamedElements(UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), null,
                Action.class, true, listReqModels, false);

        for (int i=0;i<listNavigationActions.size();i++)
        {
            if ((listNavigationActions.get(i) instanceof CallBehaviorAction)&&
                ((((CallBehaviorAction)(listNavigationActions.get(i))).getActivity().getName().replaceAll(" ","").equals(cMain.getName()))||
                (((((CallBehaviorAction)(listNavigationActions.get(i))).getActivity().getOwner()) instanceof NamedElement)&&
                (((NamedElement)(((CallBehaviorAction)(listNavigationActions.get(i))).getActivity().getOwner())).getName().replaceAll(" ","").equals(cMain.getName())))))
            {
                Property prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWETagNavigationAction.ASYNCHRONOUS.toString());
                Slot slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                boolean bAsynchronous=false;
                if ((slot!=null)&&(slot.getValue().size()>0))
                {
                    bAsynchronous=((LiteralBoolean)(slot.getValue().get(0))).isValue();
                }
                
                prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWETagNavigationAction.GUARD.toString());
                slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                String sGuard=null;
                if ((slot!=null)&&(slot.getValue().size()>0))
                {
                    sGuard=((LiteralString)(slot.getValue().get(0))).getValue();
                }
                
                prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWENavigationActionType.sTagName);
                slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                boolean bAutomatic=false;
                if ((slot!=null)&&(!slot.getValue().isEmpty())&&((slot.getValue().get(0) instanceof InstanceValue)&&
                    (((InstanceValue)(slot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                    (((EnumerationLiteral)(((InstanceValue)(slot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.AUTOMATIC.getName()))))
                {
                    bAutomatic=true;
                }
                
                boolean bCreate=true;
                for (int c=0;c<listProClasses.size();c++)
                {
                    if ((((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()!=null)&&
                        (((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior().getName().replaceAll(" ","").equals(listProClasses.get(c).getName())))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                ((Class)(listProClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                    else if ((((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()==null)&&
                        (((CallBehaviorAction)(listNavigationActions.get(i))).getName().replaceAll(" ","").equals(listProClasses.get(c).getName())))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                ((Class)(listProClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                }
                
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if ((((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()!=null)&&
                        (((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior().getName().replaceAll(" ","").equals(listNavClasses.get(c).getName())))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                ((Class)(listNavClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                    else if ((((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()==null)&&
                        (((CallBehaviorAction)(listNavigationActions.get(i))).getName().replaceAll(" ","").equals(listNavClasses.get(c).getName())))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                ((Class)(listNavClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                }

                if ((bCreate)&&(((CallBehaviorAction)(listNavigationActions.get(i))).getBehavior()==null))
                {
                    ElementCollector.ReturnElement elExternal=addExternal(cMain,listNavigationActions.get(i).getName(),
                            new Point(prMain.getBounds().x,prMain.getBounds().y+50));
                    listAssociations.add(new AssociationCandidate(StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassGeneral.PROCESS_CLASS.toString())?
                            UWEStereotypeAssoc.PROCESS_LINK.toString():UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                            elExternal.cClass,sGuard,bAsynchronous,bAutomatic,elExternal.cShape));
                }
            }
            else if ((((Action)(listNavigationActions.get(i))).getActivity().getName().replaceAll(" ","").equals(cMain.getName()))||
                (((((Action)(listNavigationActions.get(i))).getActivity().getOwner()) instanceof NamedElement)&&
                (((NamedElement)(((Action)(listNavigationActions.get(i))).getActivity().getOwner())).getName().replaceAll(" ","").equals(cMain.getName()))))
            {
                Property prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWETagNavigationAction.ASYNCHRONOUS.toString());
                Slot slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                boolean bAsynchronous=false;
                if ((slot!=null)&&(slot.getValue().size()>0))
                {
                    bAsynchronous=((LiteralBoolean)(slot.getValue().get(0))).isValue();
                }
                
                prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWETagNavigationAction.GUARD.toString());
                slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                String sGuard=null;
                if ((slot!=null)&&(slot.getValue().size()>0))
                {
                    sGuard=((LiteralString)(slot.getValue().get(0))).getValue();
                }
                
                prop=StereotypesHelper.getPropertyByName(StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),UWENavigationActionType.sTagName);
                slot=StereotypesHelper.getSlot(listNavigationActions.get(i),prop,false,false);
                boolean bAutomatic=false;
                if ((slot!=null)&&(!slot.getValue().isEmpty())&&((slot.getValue().get(0) instanceof InstanceValue)&&
                    (((InstanceValue)(slot.getValue().get(0))).getInstance() instanceof EnumerationLiteral)&&
                    (((EnumerationLiteral)(((InstanceValue)(slot.getValue().get(0))).getInstance())).getName().equals(UWENavigationActionType.AUTOMATIC.getName()))))
                {
                    bAutomatic=true;
                }
                
                boolean bCreate=true;
                for (int c=0;c<listProClasses.size();c++)
                {
                    if (((Action)(listNavigationActions.get(i))).getName().replaceAll(" ","").equals(listProClasses.get(c).getName()))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                ((Class)(listProClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                }
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if (((Action)(listNavigationActions.get(i))).getName().replaceAll(" ","").equals(listNavClasses.get(c).getName()))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                ((Class)(listNavClasses.get(c))),sGuard,bAsynchronous,bAutomatic,null));
                        bCreate=false;
                    }
                }

                if (bCreate)
                {
                    ElementCollector.ReturnElement elExternal=addExternal(cMain,listNavigationActions.get(i).getName(),
                            new Point(prMain.getBounds().x,prMain.getBounds().y+50));
                    
                    listAssociations.add(new AssociationCandidate(StereotypesHelper.hasStereotype(cMain,UWEStereotypeClassGeneral.PROCESS_CLASS.toString())?
                            UWEStereotypeAssoc.PROCESS_LINK.toString():UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                            elExternal.cClass,sGuard,bAsynchronous,bAutomatic,elExternal.cShape));
                }
            }
        }

        ArrayList<NamedElement> listComposites=ElementCollector.getNamedElements(null,null, Association.class, true, listNavModels, false);
        for (int i=0;i<listComposites.size();i++)
        {
            if (((Association)(listComposites.get(i))).getMemberEnd().get(0).getAggregation()==AggregationKindEnum.COMPOSITE)
            {
                for (int n=0;n<listNavClasses.size();n++)
                {
                    if ((listNavClasses.get(n)==((Association)(listComposites.get(i))).getMemberEnd().get(1).getType())&&
                       (StereotypesHelper.hasStereotype(((Association)(listComposites.get(i))).getMemberEnd().get(0).getType(),
                            UWEStereotypeClassNav.MENU.toString())))
                    {
                        listMenus.set(n,((Association)(listComposites.get(i))).getMemberEnd().get(0).getType());
                        break;
                    }
                }
            }
            else if (((Association)(listComposites.get(i))).getMemberEnd().get(1).getAggregation()==AggregationKindEnum.COMPOSITE)
            {
                for (int n=0;n<listNavClasses.size();n++)
                {
                    if ((listNavClasses.get(n)==((Association)(listComposites.get(i))).getMemberEnd().get(0).getType())&&
                       (StereotypesHelper.hasStereotype(((Association)(listComposites.get(i))).getMemberEnd().get(1).getType(),
                            UWEStereotypeClassNav.MENU.toString())))
                    {
                        listMenus.set(n,((Association)(listComposites.get(i))).getMemberEnd().get(1).getType());
                        break;
                    }
                }
            }
        }

        ArrayList<NamedElement> listUseCases=new ArrayList<NamedElement>();
        int iFound=ElementCollector.iNO_ELEMENT;
        do
        {
            iFound=ElementCollector.getNamedElementFromArrayList(tempCases, cMain.getName(), false, true);
            if (iFound!=ElementCollector.iNO_ELEMENT)
            {
                listUseCases.add(tempCases.get(iFound));
                tempCases.remove(iFound);
            }
        } while (iFound!=ElementCollector.iNO_ELEMENT);

        for (int i=0;i<listUseCases.size();i++)
        {
            Iterator<Extend> itExtends=((UseCase)(listUseCases.get(i))).getExtend().iterator();
            while (itExtends.hasNext())
            {
                Extend tempEx=itExtends.next();
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if (listNavClasses.get(c).getName().equals(tempEx.getExtendedCase().getName().replaceAll(" ","")))
                    {
                        if (listMenus.get(c)==null)
                        {
                            if (bProcess)
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),
                                        ((Class)(listNavClasses.get(c))),cMain,null,false,false,null));
                            }
                            else
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),
                                        ((Class)(listNavClasses.get(c))),cMain,null,false,false,null));
                            }
                        }
                        else
                        {
                            if (bProcess)
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),
                                        ((Class)(listMenus.get(c))),cMain,null,false,false,null));
                            }
                            else
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),
                                        ((Class)(listMenus.get(c))),cMain,null,false,false,null));
                            }
                        }
                    }
                }
                for (int c=0;c<listProClasses.size();c++)
                {
                    if (listProClasses.get(c).getName().equals(tempEx.getExtendedCase().getName().replaceAll(" ","")))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),
                                ((Class)(listProClasses.get(c))),cMain,null,false,false,null));
                    }
                }
            }

            Iterator<Include> itIncludes=((UseCase)(listUseCases.get(i))).getInclude().iterator();
            while (itIncludes.hasNext())
            {
                Include tempIn=itIncludes.next();
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if (listNavClasses.get(c).getName().equals(tempIn.getAddition().getName().replaceAll(" ","")))
                    {
                        if (listMenus.get(c)==null)
                        {
                            if (bProcess)
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                        ((Class)(listNavClasses.get(c))),null,false,false,null));
                            }
                            else
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                        ((Class)(listNavClasses.get(c))),null,false,false,null));
                            }
                        }
                        else
                        {
                            if (bProcess)
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                        ((Class)(listMenus.get(c))),null,false,false,null));
                            }
                            else
                            {
                                listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                        ((Class)(listMenus.get(c))),null,false,false,null));
                            }
                        }
                    }
                }
                for (int c=0;c<listProClasses.size();c++)
                {
                    if (listProClasses.get(c).getName().equals(tempIn.getAddition().getName().replaceAll(" ","")))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                ((Class)(listProClasses.get(c))),null,false,false,null));
                    }
                }
            }

            itExtends=((UseCase)(listUseCases.get(i))).get_extendOfExtendedCase().iterator();
            while (itExtends.hasNext())
            {
                Extend tempEx=itExtends.next();
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if (listNavClasses.get(c).getName().equals(tempEx.getExtension().getName().replaceAll(" ","")))
                    {
                        if (bProcess)
                        {
                            listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                    ((Class)(listNavClasses.get(c))),null,false,false,null));
                        }
                        else
                        {
                            listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cMain,
                                    ((Class)(listNavClasses.get(c))),null,false,false,null));
                        }
                    }
                }
                for (int c=0;c<listProClasses.size();c++)
                {
                    if (listProClasses.get(c).getName().equals(tempEx.getExtension().getName().replaceAll(" ","")))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cMain,
                                ((Class)(listProClasses.get(c))),null,false,false,null));
                    }
                }
            }

            itIncludes=((UseCase)(listUseCases.get(i))).get_includeOfAddition().iterator();
            while (itIncludes.hasNext())
            {
                Include tempIn=itIncludes.next();
                for (int c=0;c<listNavClasses.size();c++)
                {
                    if (listNavClasses.get(c).getName().equals(tempIn.getIncludingCase().getName().replaceAll(" ","")))
                    {
                        if (bProcess)
                        {
                            listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),
                                    ((Class)(listNavClasses.get(c))),cMain,null,false,false,null));
                        }
                        else
                        {
                            listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),
                                    ((Class)(listNavClasses.get(c))),cMain,null,false,false,null));
                        }
                    }
                }
                for (int c=0;c<listProClasses.size();c++)
                {
                    if (listProClasses.get(c).getName().equals(tempIn.getIncludingCase().getName().replaceAll(" ","")))
                    {
                        listAssociations.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),
                                ((Class)(listProClasses.get(c))),cMain,null,false,false,null));
                    }
                }
            }
        }
        return(listAssociations);
    }

    /** Adds associations to a navigation class
     * 
     * @param cMain navigation class
     * @param prShape presetnation element of the navigation class
     * @param bMenu create also a menu?
     * @return class and presentation element 
     */
    public static ElementCollector.ReturnElement addNavigationClassAssociations(Class cMain, 
            PresentationElement prShape, 
            boolean bMenu)
    {
        ArrayList<AssociationCandidate> listCandidates=collectAssociations(cMain,prShape,false);
        ArrayList<Association> listAssociations=new ArrayList<Association>();
        ArrayList<Class> listExternal=new ArrayList<Class>();
        ArrayList<PresentationElement> listExternalPresentations=new ArrayList<PresentationElement>();
        
        int iFrom=0;
        for (int i=0;i<listCandidates.size();i++)
        {
            if (listCandidates.get(i).cFrom==cMain)
            {
                iFrom++;
            }
        }

        ElementCollector.ReturnElement retMenu=null;
        if ((bMenu)&&(iFrom>1))
        {
            retMenu=addMenu(cMain,prShape,new Point(prShape.getBounds().x,prShape.getBounds().y+prShape.getBounds().height+iMENU_DISTANCE));

            for (int i=0;i<listCandidates.size();i++)
            {
                if ((listCandidates.get(i).cTo==cMain)&&(listCandidates.get(i).cFrom==cMain))
                {
                    listAssociations.add(addAssociation(listCandidates.get(i).sStereotype,retMenu.cClass,retMenu.cClass,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
                }
                else if(listCandidates.get(i).cFrom == cMain)
                {
                    listAssociations.add(addAssociation(listCandidates.get(i).sStereotype,retMenu.cClass,listCandidates.get(i).cTo,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
                }
                else if (listCandidates.get(i).cTo==cMain)
                {
                    listAssociations.add(addAssociation(listCandidates.get(i).sStereotype,listCandidates.get(i).cFrom,retMenu.cClass,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
                }
                
                if (StereotypesHelper.hasStereotype(listCandidates.get(i).cTo,UWEStereotypeClassNav.EXTERNAL_NODE.toString()))
                {
                    if (!listExternal.contains(listCandidates.get(i).cTo))
                    {
                        listExternal.add(listCandidates.get(i).cTo);
                        listExternalPresentations.add(listCandidates.get(i).elShape);
                    }
                }
            }
        }
        else if (iFrom==1)
        {
            for (int i=0;i<listCandidates.size();i++)
            {
                if (StereotypesHelper.hasStereotype(listCandidates.get(i).cTo,UWEStereotypeClassNav.EXTERNAL_NODE.toString()))
                {
                    if (!listExternal.contains(listCandidates.get(i).cTo))
                    {
                        listExternal.add(listCandidates.get(i).cTo);
                        listExternalPresentations.add(listCandidates.get(i).elShape);
                    }
                }
                
                listAssociations.add(addAssociation(listCandidates.get(i).sStereotype,listCandidates.get(i).cFrom,listCandidates.get(i).cTo,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
            }
        }

        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Associations to Navigation Class");
        }
        ArrayList<PathElement> listPaths=new ArrayList<PathElement>();
        for (int i=0;i<listAssociations.size();i++)
        {
            if (listAssociations.get(i)==null)
            {
                continue;
            }
            Iterator<Property> itProps=listAssociations.get(i).getMemberEnd().iterator();
            ArrayList<PresentationElement> listPresentationElements=new ArrayList<PresentationElement>();

            while (itProps.hasNext())
            {
                Property cTemp=itProps.next();

                if ((cTemp.getType()!=cMain)&&((retMenu==null)||(cTemp.getType()!=retMenu.cClass)))
                {
                    listPresentationElements=ElementCollector.getDiagramPresentationElements(null, Application.getInstance().getProject().getActiveDiagram().getDiagram(), cTemp.getType());
                }
            }

            if ((bMenu)&&(listPresentationElements.size()==1)&&(retMenu!=null))
            {
                if (retMenu.cShape==null)
                {
                    PresentationElement elPresentation=null;
                    for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                    {
                        if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().
                            getPresentationElements().get(c).getElement()==retMenu.cClass)
                        {
                            elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            break;
                        }
                    }

                    if (elPresentation!=null)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),elPresentation,listPresentationElements.get(0)));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),elPresentation));
                            }
                            catch (Exception e2)
                            {
                                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addNavigationClassAssociations(): "+e2.toString());

                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                            }
                        }
                    }
                }
                else 
                {
                    try
                    {
                        listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),retMenu.cShape,listPresentationElements.get(0)));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),retMenu.cShape));
                        }
                        catch (Exception e2)
                        {
                            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addNavigationClassAssociations(): "+e2.toString());

                            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                            {
                                SessionManager.getInstance().closeSession();
                            }
                        }
                    }
                }
            }
            else if (((!bMenu)||(retMenu==null))&&(listPresentationElements.size()==1))
            {
                if (prShape==null)
                {
                    PresentationElement elPresentation=null;
                    for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                    {
                        if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().
                            getPresentationElements().get(c).getElement()==cMain)

                        {
                            elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            break;
                        }
                    }

                    if (elPresentation!=null)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),elPresentation,listPresentationElements.get(0)));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),elPresentation));
                            }
                            catch (Exception e2)
                            {
                                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addNavigationClassAssociations(): "+e2.toString());

                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                            }
                        }
                    }
                }
                else
                {
                    try
                    {
                        listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),prShape,listPresentationElements.get(0)));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),prShape));
                        }
                        catch (Exception e2)
                        {
                            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addNavigationClassAssociations(): "+e2.toString());

                            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                            {
                                SessionManager.getInstance().closeSession();
                            }
                        }
                    }
                }
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        for (int i=0;i<listPaths.size();i++)
        {
            com.nomagic.magicdraw.properties.Property cProp=listPaths.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            listPaths.get(i).changeProperty(cProp);
        }
        if (retMenu!=null)
        {
            retMenu.iChildren=listPaths.size()+1;
            retMenu.additionalClasses=listExternal;
            retMenu.additionalShapes=listExternalPresentations;
        }
        else
        {
            retMenu=new ElementCollector.ReturnElement(null, null, listPaths.size()+1,listExternal,listExternalPresentations);
        }
        return(retMenu);
    }

    /** Adds associations to a process class
     * 
     * @param cMain process class
     * @param prShape presetnation element of the process class
     * @return created associations counts
     */
    public static ElementCollector.ReturnElement addProcessClassAssociations(Class cMain, 
            PresentationElement prShape)
    {
        ArrayList<AssociationCandidate> listCandidates=collectAssociations(cMain,prShape,true);
        ArrayList<Association> listAssociations=new ArrayList<Association>();

        ArrayList<Class> listExternal=new ArrayList<Class>();
        ArrayList<PresentationElement> listExternalPresentations=new ArrayList<PresentationElement>();
        
        for (int i=0;i<listCandidates.size();i++)
        {
            if (StereotypesHelper.hasStereotype(listCandidates.get(i).cTo,UWEStereotypeClassNav.EXTERNAL_NODE.toString()))
            {
                if (!listExternal.contains(listCandidates.get(i).cTo))
                {
                    listExternal.add(listCandidates.get(i).cTo);
                    listExternalPresentations.add(listCandidates.get(i).elShape);
                }
            }
            
            listAssociations.add(addAssociation(listCandidates.get(i).sStereotype,listCandidates.get(i).cFrom,listCandidates.get(i).cTo,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
        }

        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Associations to Process Class");
        }
        ArrayList<PathElement> listPaths=new ArrayList<PathElement>();
        for (int i=0;i<listAssociations.size();i++)
        {
            if (listAssociations.get(i)==null)
            {
                continue;
            }
            Iterator<Property> cIterator=listAssociations.get(i).getMemberEnd().iterator();
            ArrayList<PresentationElement> listPresentationElements=new ArrayList<PresentationElement>();

            while (cIterator.hasNext())
            {
                Property cTemp=cIterator.next();

                if (cTemp.getType()!=cMain)
                {
                    listPresentationElements=ElementCollector.getDiagramPresentationElements(null, Application.getInstance().getProject().getActiveDiagram().getDiagram(), cTemp.getType());
                }
            }

            if (listPresentationElements.size()==1)
            {
                if (prShape==null)
                {
                    PresentationElement elPresentation=null;
                    for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                    {
                        if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().
                            getPresentationElements().get(c).getElement()==cMain)

                        {
                            elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            break;
                        }
                    }

                    if (elPresentation!=null)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),elPresentation,listPresentationElements.get(0)));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),elPresentation));
                            }
                            catch (Exception e2)
                            {

                                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addProcessClassAssociations(): "+e2.toString());

                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                            }
                        }
                    }
                }
                else
                {
                    try
                    {
                        listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),prShape,listPresentationElements.get(0)));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentationElements.get(0),prShape));
                        }
                        catch (Exception e2)
                        {

                            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addProcessClassAssociations(): "+e2.toString());

                            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                            {
                                SessionManager.getInstance().closeSession();
                            }
                        }
                    }
                }
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        for (int i=0;i<listPaths.size();i++)
        {
            com.nomagic.magicdraw.properties.Property cProp=listPaths.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            listPaths.get(i).changeProperty(cProp);
        }
        
        return(new ElementCollector.ReturnElement(null, null, listPaths.size(),listExternal,listExternalPresentations));
    }

    /** Create home navigation class
     * 
     * @param position position of this class
     * @param elParent parent elment of this class
     * @return class and presentation element
     */
    public static ElementCollector.ReturnElement createHome(Point position,
            NamedElement elParent)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Home Class");
        }
        Project cProject = Application.getInstance().getProject();
        DiagramPresentationElement cDiagram = cProject.getActiveDiagram();
        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(null, "Home", Class.class, true, elParent, false);

        Stereotype cStereotype=null;
        Class cReturn=null;
        ShapeElement elShape=null;
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
                cReturn.setName(sHOME_NAME);
                cStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    UWEStereotypeClassNav.NAVIGATION_CLASS.toString());
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
                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createHome(): "+e.toString());
            }

            if (cStereotype!=null)
            {
                StereotypesHelper.addStereotype(cReturn,cStereotype);

                Property propTag = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(cProject,UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),
                        UWETagNavigationNode.IS_HOME.toString());
                Slot slot=StereotypesHelper.getSlot(cReturn,propTag,true,false);
                LiteralBoolean cBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                cBoolean.setValue(true);
                slot.getValue().add(cBoolean);

                propTag = StereotypesHelper.getPropertyByName(
                        StereotypesHelper.getStereotype(cProject,UWEStereotypeClassNav.NAVIGATION_CLASS.toString()),
                        UWETagNavigationNode.IS_LANDMARK.toString());
                slot=StereotypesHelper.getSlot(cReturn,propTag,true,false);
                cBoolean=cProject.getElementsFactory().createLiteralBooleanInstance();
                cBoolean.setValue(true);
                slot.getValue().add(cBoolean);
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
                    SessionManager.getInstance().createSession("Create Home Class");
                }
                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.createHome(): "+e.toString());
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return(new ElementCollector.ReturnElement(cReturn, elShape,0,null,null));
    }

    /** Adds associations to the home navigation class
     * 
     * @param cHome home navigation class
     * @param elShape presentation element of home
     * @param bMenu add a menu?
     * @param listNavClasses list of navigation classes and their presentation elements
     * @param listProClasses list of process classes and their presentation elements
     * @return created associations count
     */
    public static ElementCollector.ReturnElement addHomeAssociations(Class cHome, 
            PresentationElement elShape, 
            boolean bMenu,
            ArrayList<ElementCollector.ReturnElement> listNavClasses, 
            ArrayList<ElementCollector.ReturnElement> listProClasses)
    {
        ArrayList<NamedElement> listAssociations=ElementCollector.getNamedElements(null, null, Association.class, true, 
                ElementCollector.getNamedElements(UWEDiagramType.NAVIGATION.modelStereotype, null, Model.class, false, true), false);
        ArrayList<NamedElement> tempAssociations=ElementCollector.getNamedElements(null, null, Association.class, true, 
                ElementCollector.getNamedElements(UWEDiagramType.PROCESS_STRUCTURE.modelStereotype, null, Model.class, false, true), false);
        listAssociations.addAll(tempAssociations);

        for (int r=listNavClasses.size()-1;r>=0;r--)
        {
            for (int a=0;a<listAssociations.size();a++)
            {
                if ((((Association)(listAssociations.get(a))).getMemberEnd().get(0).getType()==listNavClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(1).getOwner()!=listNavClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(0).getOwner()!=listNavClasses.get(r).cClass))
                {
                    listNavClasses.remove(r);
                    break;
                }
                else if ((((Association)(listAssociations.get(a))).getMemberEnd().get(1).getType()==listNavClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(1).getOwner()!=listNavClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(0).getOwner()!=listNavClasses.get(r).cClass))
                {
                    listNavClasses.remove(r);
                    break;
                }
            }
        }

        for (int r=listProClasses.size()-1;r>=0;r--)
        {
            for (int a=0;a<listAssociations.size();a++)
            {
                if ((((Association)(listAssociations.get(a))).getMemberEnd().get(0).getType()==listProClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(1).getOwner()!=listProClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(0).getOwner()!=listProClasses.get(r).cClass))
                {
                    listProClasses.remove(r);
                    break;
                }
                else if ((((Association)(listAssociations.get(a))).getMemberEnd().get(1).getType()==listProClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(1).getOwner()!=listProClasses.get(r).cClass)&&
                    (((Association)(listAssociations.get(a))).getMemberEnd().get(0).getOwner()!=listProClasses.get(r).cClass))
                {
                    listProClasses.remove(r);
                    break;
                }
            }
        }

        ArrayList<AssociationCandidate> listCandidates=new ArrayList<AssociationCandidate>();
        ElementCollector.ReturnElement retMenu=null;
        if ((bMenu)&&(listProClasses.size()+listNavClasses.size()>1))
        {
            retMenu=addMenu(cHome,elShape,new Point(elShape.getBounds().x,elShape.getBounds().y+elShape.getBounds().height+iMENU_DISTANCE));

            for (int i=0;i<listProClasses.size();i++)
            {
                listCandidates.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),retMenu.cClass,
                        listProClasses.get(i).cClass,null,false,false,null));
            }
            for (int i=0;i<listNavClasses.size();i++)
            {
                listCandidates.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),retMenu.cClass,
                        listNavClasses.get(i).cClass,null,false,false,null));
            }
        }
        else
        {
            for (int i=0;i<listProClasses.size();i++)
            {
                listCandidates.add(new AssociationCandidate(UWEStereotypeAssoc.PROCESS_LINK.toString(),cHome,
                        listProClasses.get(i).cClass,null,false,false,null));
            }
            for (int i=0;i<listNavClasses.size();i++)
            {
                listCandidates.add(new AssociationCandidate(UWEStereotypeAssoc.NAVIGATION_LINK.toString(),cHome,
                        listNavClasses.get(i).cClass,null,false,false,null));
            }
        }
        ArrayList<Association> acAssociations=new ArrayList<Association>();
        for (int i=0;i<listCandidates.size();i++)
        {
            acAssociations.add(addAssociation(listCandidates.get(i).sStereotype,listCandidates.get(i).cFrom,listCandidates.get(i).cTo,
                            listCandidates.get(i).sGuard,listCandidates.get(i).bAsynchronous,listCandidates.get(i).bAutomatic));
        }

        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Add Associations to Home Class");
        }
        ArrayList<PathElement> listPaths=new ArrayList<PathElement>();
        for (int i=0;i<acAssociations.size();i++)
        {
            if (acAssociations.get(i)==null)
            {
                continue;
            }
            Iterator<Property> cIterator=acAssociations.get(i).getMemberEnd().iterator();
            ArrayList<PresentationElement> listPresentationElements=new ArrayList<PresentationElement>();

            while (cIterator.hasNext())
            {
                Property cTemp=cIterator.next();

                if ((cTemp.getType()!=cHome)&&((retMenu==null)||(cTemp.getType()!=retMenu.cClass)))
                {
                    listPresentationElements=ElementCollector.getDiagramPresentationElements(null, Application.getInstance().getProject().getActiveDiagram().getDiagram(), cTemp.getType());
                }

            }

            if (retMenu != null && (bMenu) && (listPresentationElements.size()==1))
            {
                if (retMenu.cShape==null)
                {
                    PresentationElement elPresentation=null;
                    for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                    {
                        if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().
                            getPresentationElements().get(c).getElement()==retMenu.cClass)

                        {
                            elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            break;
                        }
                    }

                    if (elPresentation!=null)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),elPresentation,listPresentationElements.get(0)));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),listPresentationElements.get(0),elPresentation));
                            }
                            catch (Exception e2)
                            {

                                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addHomeAssociations(): "+e2.toString());

                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                            }
                        }
                    }
                }
                else
                {
                    try
                    {
                        listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),retMenu.cShape,listPresentationElements.get(0)));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),listPresentationElements.get(0),retMenu.cShape));
                        }
                        catch (Exception e2)
                        {

                            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addHomeAssociations(): "+e2.toString());

                            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                            {
                                SessionManager.getInstance().closeSession();
                            }
                        }
                    }
                }
            }
            else if ((!bMenu)&&(listPresentationElements.size()==1))
            {
                if (elShape==null)
                {
                    PresentationElement elPresentation=null;
                    for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                    {
                        if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().
                            getPresentationElements().get(c).getElement()==cHome)

                        {
                            elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            break;
                        }
                    }

                    if (elPresentation!=null)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),elPresentation,listPresentationElements.get(0)));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),listPresentationElements.get(0),elPresentation));
                            }
                            catch (Exception e2)
                            {

                                Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addHomeAssociations(): "+e2.toString());

                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                            }
                        }
                    }
                }
                else
                {
                    try
                    {
                        listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),elShape,listPresentationElements.get(0)));
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            listPaths.add(PresentationElementsManager.getInstance().createPathElement(acAssociations.get(i),listPresentationElements.get(0),elShape));
                        }
                        catch (Exception e2)
                        {

                            Application.getInstance().getGUILog().showError("Exception in ReqToNavTransformationRules.addHomeAssociations(): "+e2.toString());

                            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                            {
                                SessionManager.getInstance().closeSession();
                            }
                        }
                    }
                }
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        for (int i=0;i<listPaths.size();i++)
        {
            com.nomagic.magicdraw.properties.Property cProp=listPaths.get(i).getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            listPaths.get(i).changeProperty(cProp);
        }
        
        if (retMenu!=null)
        {
            retMenu.iChildren=listPaths.size()+1;
        }
        else
        {
            retMenu=new ElementCollector.ReturnElement(null, null, listPaths.size(),null,null);
        }
        return(retMenu);
    }
}
