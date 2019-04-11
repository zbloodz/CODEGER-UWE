/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.transformation.requirements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.actions.SelectConstraintAction;
import com.nomagic.magicdraw.uml.symbols.shapes.NoteView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.CallBehaviorAction;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.InputPin;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.ActivityEdge;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.activities.mdintermediateactivities.CentralBufferNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdstructuredactivities.StructuredActivityNode;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralBoolean;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralInteger;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralUnlimitedNatural;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.MultiplicityElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.OpaqueExpression;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdusecases.Actor;
import com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase;
import com.nomagic.uml2.impl.magicdraw.classes.mdkernel.OpaqueExpressionClassImpl;

//import javassist.compiler.Javac;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;

/**
 *
 * @author PST LMU
 */
public class ReqToConTransformations
{
    // Additional distance between the content classes in a diagram when computing collisions
    public static final int iMIN_DISTANCE_X=50;
    public static final int iMIN_DISTANCE_Y=25;
    
    /** Removes all attributes from a content class
     *
     * @param cClass class to be cleared
     */
    public static void clearAttributes(Class cClass)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Clear Attributes");
        }

        ArrayList<Property> listProperties=new ArrayList<Property>();
        Iterator<Property> itProp=cClass.getAttribute().iterator();
        while (itProp.hasNext())
        {
            Property prop=itProp.next();

            if (prop.getAssociation()==null)
            {
                listProperties.add(prop);
            }
        }

        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }

    /** Removes all associations from a content class
     *
     * @param cClass class to be cleared
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
        Iterator<Property> itProp=cClass.getAttribute().iterator();
        while (itProp.hasNext())
        {
            Property prop=itProp.next();

            if (prop.getAssociation()!=null)
            {
                listProperties.add(prop);
            }
        }

        for (int i=0;i<listProperties.size();i++)
        {
            listProperties.get(i).dispose();
        }

        ArrayList<NamedElement> listTempAssociations=ElementCollector.getNamedElements(null, null, Association.class, false, false);

        for (int a=0;a<listTempAssociations.size();a++)
        {
            for (int e=0;e<((Association)(listTempAssociations.get(a))).getMemberEnd().size();e++)
            {
                if (((Association)(listTempAssociations.get(a))).getMemberEnd().get(e).getType()==cClass)
                {
                    listTempAssociations.get(a).dispose();
                }
            }
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
    }

    /** Creates a new content class 
     * 
     * @param sName name of the class
     * @param position the position of the class in the active diagram
     * @param cPackage the parent package of the class
     * @return the class and shape element
     */
    public static ElementCollector.ReturnElement createContentClass(String sName,
            Point position,
            Package cPackage)
    {
        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Content Class");
        }
        ElementCollector.ReturnElement elReturn=null;
        Project cProject = Application.getInstance().getProject();
        DiagramPresentationElement cDiagram = cProject.getActiveDiagram();
        ArrayList<NamedElement> listTemp=new ArrayList<NamedElement>();
        listTemp.add(cPackage);
        ArrayList<NamedElement> listOld=ElementCollector.getNamedElements(null, sName, Class.class, true, listTemp, true);

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

            ShapeElement elShape=null;
            try
            {
                elShape=PresentationElementsManager.getInstance().createShapeElement(cReturn,cDiagram);

                PresentationElementsManager.getInstance().reshapeShapeElement(elShape,new Rectangle(position.x,position.y,0,0));
            }
            catch (Exception e)
            {
                Application.getInstance().getGUILog().showError("Exception in ReqToConTransformations.createContentClass(): "+e.toString());
            }

            if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
            {
                SessionManager.getInstance().closeSession();
            }

            com.nomagic.magicdraw.properties.Property cProp=elShape.getProperty("SUPPRESS_CLASS_OPERATIONS").clone();
            cProp.setValue(true);
            elShape.changeProperty(cProp);
            cProp=elShape.getProperty("STEREOTYPES_DISPLAY_MODE").clone();
            cProp.setValue("STEREOTYPE_DISPLAY_MODE_ICON");
            elShape.changeProperty(cProp);
            elReturn=new ElementCollector.ReturnElement(cReturn,elShape,0,null,null);
        }

        if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
        {
            SessionManager.getInstance().closeSession();
        }
        return(elReturn);
    }

    /** collects all attribute candidates for a class name
     * 
     * @param sClassName name of the content class
     * @return list of candidates for content class attributes
     */
    public static ArrayList<TypedElement> collectAttributeCandidates(String sClassName)
    {
        ArrayList<TypedElement> listReturn=new ArrayList<TypedElement>();
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listReqObjects=ElementCollector.getNamedElements(null, sClassName, CentralBufferNode.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqClasses=ElementCollector.getNamedElements(null, sClassName, Class.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqPins=ElementCollector.getNamedElements(null, null, Pin.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqFlows=ElementCollector.getNamedElements(null, null, ActivityEdge.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqActions=ElementCollector.getNamedElements(null, null, Action.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqStructuredActions=ElementCollector.getNamedElements(null, null, StructuredActivityNode.class, true, listReqModels, false);
        listReqActions.addAll(listReqStructuredActions);

        for (int c=0;c<listReqClasses.size();c++)
        {
            Iterator<Property> itProps=((Class)(listReqClasses.get(c))).getAttribute().iterator();
            while (itProps.hasNext())
            {
                Property prop=itProps.next();
                if (prop.getAssociation()==null)
                {
                    listReturn.add(prop);
                }
            }
        }
        
        ArrayList<NamedElement> listReqFlows2=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqObjects.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqObjects.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqObjects.get(o)))
                {
                    if (!listReqFlows2.contains(listReqFlows.get(f)))
                    {
                        listReqFlows2.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions2=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows2.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows2.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows2.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions2.contains(listReqActions.get(a)))
                    {
                        listReqActions2.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqFlows3=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqActions2.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqActions2.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqActions2.get(o)))
                {
                    if (!listReqFlows3.contains(listReqFlows.get(f)))
                    {
                        listReqFlows3.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions3=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows3.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows3.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows3.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions3.contains(listReqActions.get(a)))
                    {
                        listReqActions3.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        listReqActions=listReqActions2;
        listReqFlows=listReqFlows2;
        listReqFlows.addAll(listReqFlows3);

        for (int p=listReqPins.size()-1;p>=0;p--)
        {
            for (int a=0;a<listReqActions.size();a++)
            {
                if ((listReqActions.get(a).getOwnedElement().contains(listReqPins.get(p)))&&
                    (!listReturn.contains(((TypedElement)(listReqPins.get(p))))))
                {
                    listReturn.add(((TypedElement)(listReqPins.get(p))));
                    break;
                }
            }

            for (int f=0;f<listReqFlows.size();f++)
            {
                if (((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqPins.get(p))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqPins.get(p)))&&
                    (!listReturn.contains(((TypedElement)(listReqPins.get(p))))))
                {
                    listReturn.add(((TypedElement)(listReqPins.get(p))));
                    break;
                }
            }
        }
        return(listReturn);
    }

    /** collects all constraint candidates for a class name
     * 
     * @param sClassName name of the content class
     * @return list of candidates for content class constraint
     */
    public static ArrayList<Constraint> collectConstraintCandidates(String sClassName)
    {
        ArrayList<Constraint> listReturn=new ArrayList<Constraint>();
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listReqObjects=ElementCollector.getNamedElements(null, sClassName, CentralBufferNode.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqClasses=ElementCollector.getNamedElements(null, sClassName, Class.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqPins=ElementCollector.getNamedElements(null, null, Pin.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqCons=ElementCollector.getNamedElements(null, null, Constraint.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqFlows=ElementCollector.getNamedElements(null, null, ActivityEdge.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqActions=ElementCollector.getNamedElements(null, null, Action.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqStructuredActions=ElementCollector.getNamedElements(null, null, StructuredActivityNode.class, true, listReqModels, false);
        listReqActions.addAll(listReqStructuredActions);

        for (int c=0;c<listReqClasses.size();c++)
        {
            Iterator<Property> itProps=((Class)(listReqClasses.get(c))).getAttribute().iterator();
            while (itProps.hasNext())
            {
                Property prop=itProps.next();
                if (prop.getAssociation()==null)
                {
//                    listReturn.add(prop);
                	if(!prop.get_constraintOfConstrainedElement().isEmpty())
                    {
                    	for(Object u : prop.get_constraintOfConstrainedElement().toArray())
                    	{
                    		for(int i=0;i<listReqCons.size();i++)
                    		{
                    			if(listReqCons.get(i)==u)
                    			{
                    				listReturn.add(((Constraint)(listReqCons.get(i))));
                    			}
                    		}
                    	}
                    }
                }
            }
        }
        
        
        ArrayList<NamedElement> listReqFlows2=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqObjects.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqObjects.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqObjects.get(o)))
                {
                    if (!listReqFlows2.contains(listReqFlows.get(f)))
                    {
                        listReqFlows2.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions2=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows2.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows2.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows2.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions2.contains(listReqActions.get(a)))
                    {
                        listReqActions2.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqFlows3=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqActions2.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqActions2.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqActions2.get(o)))
                {
                    if (!listReqFlows3.contains(listReqFlows.get(f)))
                    {
                        listReqFlows3.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions3=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows3.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows3.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows3.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions3.contains(listReqActions.get(a)))
                    {
                        listReqActions3.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        listReqActions=listReqActions2;
        listReqFlows=listReqFlows2;
        listReqFlows.addAll(listReqFlows3);

        for (int p=listReqPins.size()-1;p>=0;p--)
        {
            for (int a=0;a<listReqActions.size();a++)
            {
                if ((listReqActions.get(a).getOwnedElement().contains(listReqPins.get(p)))&&
                    (!listReturn.contains(((TypedElement)(listReqPins.get(p))))))
                {
//                    listReturn.add(((TypedElement)(listReqPins.get(p))));
                	
                	if(!listReqPins.get(p).get_constraintOfConstrainedElement().isEmpty())
                    {
                    	for(Object u : listReqPins.get(p).get_constraintOfConstrainedElement().toArray())
                    	{
                    		for(int i=0;i<listReqCons.size();i++)
                    		{
                    			if(listReqCons.get(i)==u)
                    			{
                    				listReturn.add(((Constraint)(listReqCons.get(i))));
                    			}
                    		}
                    	}
                    break;
                    }
                }
            }

            for (int f=0;f<listReqFlows.size();f++)
            {
                if (((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqPins.get(p))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqPins.get(p)))&&
                    (!listReturn.contains(((TypedElement)(listReqPins.get(p))))))
                {
//                    listReturn.add(((TypedElement)(listReqPins.get(p))));
                    if(!listReqPins.get(p).get_constraintOfConstrainedElement().isEmpty())
                    {
                    	for(Object u : listReqPins.get(p).get_constraintOfConstrainedElement().toArray())
                    	{
                    		for(int i=0;i<listReqCons.size();i++)
                    		{
                    			if(listReqCons.get(i)==u)
                    			{
                    				listReturn.add(((Constraint)(listReqCons.get(i))));
                    			}
                    		}
                    	}
                    break;
                    }
                }
            }
        }
        return(listReturn);
    }
    
    
    /** collects all associations candidates for a class name
     * 
     * @param sClassName name of the content class
     * @return list of candidates for content class associations
     */
    public static ArrayList<TypedElement> collectAssociations(String sClassName)
    {
        ArrayList<TypedElement> listTemp=new ArrayList<TypedElement>();
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listReqObjects=ElementCollector.getNamedElements(null, sClassName, CentralBufferNode.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqClasses=ElementCollector.getNamedElements(null, sClassName, Class.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqPins=ElementCollector.getNamedElements(null, null, Pin.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqFlows=ElementCollector.getNamedElements(null, null, ActivityEdge.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqActions=ElementCollector.getNamedElements(null, null, Action.class, true, listReqModels, false);
        ArrayList<NamedElement> listReqStructuredActions=ElementCollector.getNamedElements(null, null, StructuredActivityNode.class, true, listReqModels, false);
        listReqActions.addAll(listReqStructuredActions);

        for (int c=0;c<listReqClasses.size();c++)
        {
            Iterator<Property> itProps=((Class)(listReqClasses.get(c))).getAttribute().iterator();
            while (itProps.hasNext())
            {
                Property prop=itProps.next();
                if (prop.getAssociation()!=null)
                {
                    listTemp.add(prop);
                }
            }
        }

        ArrayList<NamedElement> listReqFlows2=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqObjects.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqObjects.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqObjects.get(o)))
                {
                    if (!listReqFlows2.contains(listReqFlows.get(f)))
                    {
                        listReqFlows2.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions2=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows2.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows2.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows2.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions2.contains(listReqActions.get(a)))
                    {
                        listReqActions2.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqFlows3=new ArrayList<NamedElement>();
        for (int f=listReqFlows.size()-1;f>=0;f--)
        {
            for (int o=0;o<listReqActions2.size();o++)
            {
                if ((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqActions2.get(o))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqActions2.get(o)))
                {
                    if (!listReqFlows3.contains(listReqFlows.get(f)))
                    {
                        listReqFlows3.add(listReqFlows.get(f));
                    }
                    break;
                }
            }
        }
        
        ArrayList<NamedElement> listReqActions3=new ArrayList<NamedElement>();
        for (int a=listReqActions.size()-1;a>=0;a--)
        {
            for (int f=0;f<listReqFlows3.size();f++)
            {
                if ((((ActivityEdge)(listReqFlows3.get(f))).getSource()==listReqActions.get(a))||
                    (((ActivityEdge)(listReqFlows3.get(f))).getTarget()==listReqActions.get(a)))
                {
                    if (!listReqActions3.contains(listReqActions.get(a)))
                    {
                        listReqActions3.add(listReqActions.get(a));
                    }
                    break;
                }
            }
        }
        listReqActions=listReqActions2;
        listReqFlows=listReqFlows2;
        listReqFlows.addAll(listReqFlows3);
        
        for (int p=listReqPins.size()-1;p>=0;p--)
        {
            for (int a=0;a<listReqActions.size();a++)
            {
                if ((listReqActions.get(a).getOwnedElement().contains(listReqPins.get(p)))&&
                    (!listTemp.contains(((TypedElement)(listReqPins.get(p))))))
                {
                    listTemp.add(((TypedElement)(listReqPins.get(p))));
                    break;
                }
            }

            for (int f=0;f<listReqFlows.size();f++)
            {
                if (((((ActivityEdge)(listReqFlows.get(f))).getSource()==listReqPins.get(p))||
                    (((ActivityEdge)(listReqFlows.get(f))).getTarget()==listReqPins.get(p)))&&
                    (!listTemp.contains(((TypedElement)(listReqPins.get(p))))))
                {
                    listTemp.add(((TypedElement)(listReqPins.get(p))));
                    break;
                }
            }
        }

        ArrayList<NamedElement> listModels=ElementCollector.getNamedElements(UWEDiagramType.CONTENT.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listClasses=ElementCollector.getNamedElements(null, null, Class.class, false, listModels, false);
        ArrayList<TypedElement> listReturn=new ArrayList<TypedElement>();
        for (int c=0;c<listClasses.size();c++)
        {
            for (int n=listTemp.size()-1;n>=0;n--)
            {
                if ((listTemp.get(n) instanceof Pin) &&
                    ((listTemp.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase())) ||
                    (listTemp.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase()+"s")) ||
                    (listTemp.get(n).getName().toLowerCase().contains(listClasses.get(c).getName().toLowerCase()))))
                {
                    listReturn.add(listTemp.get(n));
                }
            }
        }

        listModels=ElementCollector.getNamedElements(UWEDiagramType.USER_MODEL.modelStereotype, null, Model.class, false, true);
        listClasses=ElementCollector.getNamedElements(null, null, Class.class, false, listModels, false);
        for (int c=0;c<listClasses.size();c++)
        {
            for (int n=listTemp.size()-1;n>=0;n--)
            {
                if ((listTemp.get(n) instanceof Pin) &&
                    ((listTemp.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase())) ||
                    (listTemp.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase()+"s")) ||
                    (listTemp.get(n).getName().toLowerCase().contains(listClasses.get(c).getName().toLowerCase()))))
                {
                    listReturn.add(listTemp.get(n));
                }
            }
        }

        for (int c=0;c<listClasses.size();c++)
        {
            for (int n=listTemp.size()-1;n>=0;n--)
            {
                if (!(listTemp.get(n) instanceof Pin))
                {
                    listReturn.add(listTemp.get(n));
                }
            }
        }
        
        return(listReturn);
    }

    /** collects all operation candidates for a class name
     * 
     * @param sClassName name of the content class
     * @return list of candidates for content class operation
     */
    public static ArrayList<NamedElement> collectOperationCandidates(String sClassName)
    {
        ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
//        ArrayList<NamedElement> listReqProcess=ElementCollector.getNamedElements(null, null, UseCase.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqProcess=ElementCollector.getNamedElements(null, null, UseCase.class, false, listReqModels, false);
        
        
        for(int i=0;i<listReqProcess.size();i++)
        {
        	ArrayList<NamedElement> listAssociation=ElementCollector.getNamedElements(null, null, Association.class, false, listReqModels, false);
        	for (int j=0;j<listAssociation.size();j++)
        	{
        		Association propAssociation=((Association)(listAssociation.get(j)));
        		Object u = propAssociation.getRelatedElement().toArray()[0];
        		if (listReqProcess.get(i)==u)
        		{
            		listReturn.add((NamedElement)(listReqProcess.get(i)));
            	}
        	}
        }
        
        return(listReturn);
    }
    
    /** collects all operation condition candidates for a class name
     * 
     * @param sOperationName name of the operation class
     * @return list of candidates for content class operation
     */
    public static ArrayList<NamedElement> collectConditionCandidates(String sOperationName)
    {
    	ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
    	ArrayList<NamedElement> listCandinate=new ArrayList<NamedElement>();
    	ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
//    	ArrayList<NamedElement> listReqModels=new ArrayList<NamedElement>();
    	   	
//    	for (DiagramPresentationElement diagram : Application.getInstance().getProject().getDiagrams(DiagramTypeConstants.UML_ACTIVITY_DIAGRAM))
//    	{
//    		if(diagram.getName().equals(sOperationName))
//    		{
//    			listReturn.add((NamedElement) diagram.getElement().getOwner());
//    		}
//    	}
    	ArrayList<NamedElement> listBehaviour=ElementCollector.getNamedElements(null, null, CallBehaviorAction.class, true, listReqModels, false);
    	
    	for(int i=0;i<listBehaviour.size();i++)
    	{
    		if(listBehaviour.get(i).getOwner() instanceof Activity)
    		{
    			if(((Activity) listBehaviour.get(i).getOwner()).getName().equals(sOperationName))
    			{
    				listCandinate.add(listBehaviour.get(i));
    			}
    		}
    	}
    	
    	for(int i=0;i<listCandinate.size();i++)
    	{
    		for(Element element : listCandinate.get(i).getOwnedElement())
    		{
    			if(element instanceof Constraint)
    			{
    				OpaqueExpression specTemp = (OpaqueExpression)(((Constraint) element).getSpecification());
    				
    				if((specTemp.getBody().get(0).equals("validated")) & (!listReturn.contains((NamedElement) listCandinate.get(i))))
    				{
//    					javax.swing.JOptionPane.showMessageDialog(null, element.getHumanName());
    					listReturn.add((NamedElement) listCandinate.get(i));
    					break;
    				}
    			}
    		}
    	}
    	
   	
    	return(listReturn);
    }
    
    /** Removes elements with same names
     * 
     * @param listElements list of elements
     * @return list of elements without name duplicates
     */
    public static ArrayList<TypedElement> removeHomonyms(ArrayList<TypedElement> listElements)
    {
        ArrayList<String> asNames=new ArrayList<String>();

        for (int i=listElements.size()-1;i>=0;i--)
        {
            if (!asNames.contains(listElements.get(i).getName()))
            {
                asNames.add(listElements.get(i).getName());
            }
            else
            {
                listElements.remove(i);
            }
        }

        return(listElements);
    }

    /** Removes elements from a list that can be used to create associations
     * 
     * @param listElements list of typed elements 
     * @return list of elements without association candidates
     */
    public static ArrayList<TypedElement> skipAssociationCandidates(ArrayList<TypedElement> listElements)
    {
        ArrayList<NamedElement> listModels=ElementCollector.getNamedElements(UWEDiagramType.CONTENT.modelStereotype, null, Model.class, false, true);
        if (!listModels.isEmpty())
        {    
            ArrayList<NamedElement> listClasses=ElementCollector.getNamedElements(null, null, Class.class, false, listModels, false);
            for (int c=0;c<listClasses.size();c++)
            {
                for (int n=listElements.size()-1;n>=0;n--)
                {
                    if ((listElements.get(n) instanceof Pin) &&
                        ((listElements.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase())) ||
                        (listElements.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase()+"s")) ||
                        (listElements.get(n).getName().toLowerCase().contains(listClasses.get(c).getName().toLowerCase()))))
                    {
                        listElements.remove(n);
                    }
                }
            }
        }

        listModels=ElementCollector.getNamedElements(UWEDiagramType.USER_MODEL.modelStereotype, null, Model.class, false, true);
        if (!listModels.isEmpty())
        {    
            ArrayList<NamedElement> listClasses=ElementCollector.getNamedElements(null, null, Class.class, false, listModels, false);
            for (int c=0;c<listClasses.size();c++)
            {
                for (int n=listElements.size()-1;n>=0;n--)
                {
                    if ((listElements.get(n) instanceof Pin) &&
                        ((listElements.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase())) ||
                        (listElements.get(n).getName().toLowerCase().equals(listClasses.get(c).getName().toLowerCase()+"s")) ||
                        (listElements.get(n).getName().toLowerCase().contains(listClasses.get(c).getName().toLowerCase()))))
                    {
                        listElements.remove(n);
                    }
                }
            }
        }

        return(listElements);
    }

    /** Creates a list of attributes to a given content class
     * 
     * @param listElements list of attribute candidates
     * @param cMain the content class
     */
    public static void createAttributes(ArrayList<TypedElement> listElements,
            Class cMain)
    {
        for (int i=0;i<listElements.size();i++)
        {
            Property propTemp=Application.getInstance().getProject().getElementsFactory().createPropertyInstance();

            if (Property.class.isInstance(listElements.get(i)))
            {
                if (((Property)(listElements.get(i))).getDefaultValue()!=null)
                {
                    if (((Property)(listElements.get(i))).getDefaultValue() instanceof LiteralInteger)
                    {
                        LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                        litInt.setValue(((LiteralInteger)((Property)(listElements.get(i))).getDefaultValue()).getValue());
                        propTemp.setDefaultValue(litInt);
                    }
                    else if (((Property)(listElements.get(i))).getDefaultValue() instanceof LiteralUnlimitedNatural)
                    {
                        LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                        litUN.setValue(((LiteralUnlimitedNatural)((Property)(listElements.get(i))).getDefaultValue()).getValue());
                        propTemp.setDefaultValue(litUN);
                    }
                    else if (((Property)(listElements.get(i))).getDefaultValue() instanceof LiteralString)
                    {
                        LiteralString litString=Application.getInstance().getProject().getElementsFactory().createLiteralStringInstance();
                        litString.setValue(((LiteralString)((Property)(listElements.get(i))).getDefaultValue()).getValue());
                        propTemp.setDefaultValue(litString);
                    }
                    else if (((Property)(listElements.get(i))).getDefaultValue() instanceof LiteralBoolean)
                    {
                        LiteralBoolean litBool=Application.getInstance().getProject().getElementsFactory().createLiteralBooleanInstance();
                        litBool.setValue(((LiteralBoolean)((Property)(listElements.get(i))).getDefaultValue()).isValue());
                        propTemp.setDefaultValue(litBool);
                    }
                }
            }

            propTemp.setName(listElements.get(i).getName());
            propTemp.setType(listElements.get(i).getType());
            if (((MultiplicityElement)(listElements.get(i))).getUpperValue()!=null)
            {
                if (((MultiplicityElement)(listElements.get(i))).getUpperValue() instanceof LiteralInteger)
                {
                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                    litInt.setValue(((LiteralInteger)((MultiplicityElement)(listElements.get(i))).getUpperValue()).getValue());
                    propTemp.setUpperValue(litInt);
                }
                else if (((MultiplicityElement)(listElements.get(i))).getUpperValue() instanceof LiteralUnlimitedNatural)
                {
                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                    litUN.setValue(((LiteralUnlimitedNatural)((MultiplicityElement)(listElements.get(i))).getUpperValue()).getValue());
                    propTemp.setUpperValue(litUN);
                }
            }
            if (((MultiplicityElement)(listElements.get(i))).getLowerValue()!=null)
            {
                if (((MultiplicityElement)(listElements.get(i))).getLowerValue() instanceof LiteralInteger)
                {
                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                    litInt.setValue(((LiteralInteger)((MultiplicityElement)(listElements.get(i))).getLowerValue()).getValue());
                    propTemp.setLowerValue(litInt);
                }
                else if (((MultiplicityElement)(listElements.get(i))).getLowerValue() instanceof LiteralUnlimitedNatural)
                {
                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                    litUN.setValue(((LiteralUnlimitedNatural)((MultiplicityElement)(listElements.get(i))).getLowerValue()).getValue());
                    propTemp.setLowerValue(litUN);
                }
            }
            propTemp.setOwner(cMain);
        }
    }

    /** Creates a list of constraint to an attributes from a given content class
     * 
     * @param listConstraint list of constraint candidates
     * @param aName name of the attributes
     * @param oName name of the operation
     * @param cMain the content class
     */
    public static void createConstraint(ArrayList<Constraint> listConstraint, String aName, String oName,
            Class cMain)
    {
        for (int i=0;i<listConstraint.size();i++)
        {
            Constraint consTemp=Application.getInstance().getProject().getElementsFactory().createConstraintInstance();
            OpaqueExpression oETemp=Application.getInstance().getProject().getElementsFactory().createOpaqueExpressionInstance();
            

            
            OpaqueExpression specTemp = (OpaqueExpression)(listConstraint.get(i).getSpecification());
            for(int j=0;j<specTemp.getBody().size();j++)
            {
            	oETemp.getBody().add("this."+aName+"."+specTemp.getBody().get(j));
            }
            
            oETemp.getLanguage().add("OCL2.0");
//            javax.swing.JOptionPane.showMessageDialog(null, specTemp.getLanguage());
            
            
            consTemp.setName(listConstraint.get(i).getName());
            consTemp.setSpecification((ValueSpecification)(oETemp));
            if(cMain.getOwnedAttribute().size() > 0)
            {
            	for(int j=0;j<cMain.getOwnedAttribute().size();j++)
            	{
            		if(cMain.getOwnedAttribute().get(j).getName() == aName)
            		{
            			consTemp.getConstrainedElement().add(cMain.getOwnedAttribute().get(j));
            		}
            	}
            }
            consTemp.setOwner(cMain);
            
           
        }
    }
    
    /** Creates a list of operation to a given content class
     * 
     * @param listData list of operation candidates
     * @param cMain the content class
     */
    public static void createOperation(ArrayList<NamedElement> listData,
            Class cMain)
    {
        for (int i=0;i<listData.size();i++)
        {
        	Operation operTemp = Application.getInstance().getProject().getElementsFactory().createOperationInstance();
        	
        	operTemp.setName(listData.get(i).getName());
        	ArrayList<NamedElement> listData2 = collectConditionCandidates(listData.get(i).getName());
        	createCondition(listData2, listData.get(i).getName());
        	
        	operTemp.setOwner(cMain);
        }
    }
    
    /** Creates a list of condition of an operation
     * 
     * @param listData list of condition candidates
     * @param oName name of the operation
     */
    public static void createCondition(ArrayList<NamedElement> listData,
            String oName)
    {
        DiagramPresentationElement diaTemp = null;
        ArrayList<String> listCondition = new ArrayList<String>();

        for (DiagramPresentationElement diagram : Application.getInstance().getProject().getDiagrams(UWEDiagramType.CONTENT.umlDiagramType))
        {
        	if(diagram.getName().equals("Content Diagram"))
        	{
        		diaTemp = diagram;
        	}
        }
    	if((diaTemp != null) & (!listData.isEmpty()))
    	{
    		for (int i=0;i<listData.size();i++)
            {
    			String tempString = "";
    			ArrayList<NamedElement> listAtribute = new ArrayList<NamedElement>();
    			for(Element element : listData.get(i).getOwnedElement())
    			{
    				if(element instanceof InputPin)
    				{
    					listAtribute.add((NamedElement)element);
    				}
    			}
    			if(!listAtribute.isEmpty())
    			{
    				tempString = tempString + "Atribute " + listAtribute.get(0).getName();
    				for(int j=1;j<listAtribute.size();j++)
    				{
    					tempString = tempString + " And " + listAtribute.get(j).getName();
    				}
    			}
    			listCondition.add("Condition " + listData.get(i).getName() + "\n"+tempString);
            }
    		SessionManager.getInstance().createSession("Create Note");
    		try {
				NoteView tempNote = (NoteView) PresentationElementsManager.getInstance().createNote(diaTemp);
				String tempString = "Operation " + oName + "\n";
				if(!listCondition.isEmpty())
				{
					for (int i = 0; i < listCondition.size(); i++) {
						tempString = tempString + listCondition.get(i);
					}
				}
				PresentationElementsManager.getInstance().setText(tempNote, tempString);
			} catch (ReadOnlyElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		SessionManager.getInstance().closeSession();
        }
    }
    
    
    /** Computes all possible attributes to a given content class and adds new ones
     * 
     * @param cMain the content class
     * @param bSkipAssociations skip the attribute candidates that are also candidates for a association?
     * @return created attributes count
     */
    public static int addAttributesToContentClass(Class cMain,
            boolean bSkipAssociations)
    {
        ArrayList<TypedElement> listData=collectAttributeCandidates(cMain.getName());
        ArrayList<Constraint> listConstraint=collectConstraintCandidates(cMain.getName());

        if (!listData.isEmpty())
        {
            listData=removeHomonyms(listData);

            Iterator<Property> itProps=cMain.getAttribute().iterator();
            while (itProps.hasNext())
            {
                int iPos=ElementCollector.getNamedElementFromArrayList(listData,itProps.next().getName(),true,false);
                if (iPos!=ElementCollector.iNO_ELEMENT)
                {
                    listData.remove(iPos);
                }
            }
            
            if (bSkipAssociations)
            {
                listData=skipAssociationCandidates(listData);
            }

            createAttributes(listData,cMain);
        }
        for(int i=0;i<listData.size();i++)
        {
        	if(!listData.get(i).get_constraintOfConstrainedElement().isEmpty())
        	{
        		ArrayList<Constraint> listEleConstraint = new ArrayList<Constraint>();
        		for(Object u : listData.get(i).get_constraintOfConstrainedElement().toArray())
            	{
            		for(int j=0;j<listConstraint.size();j++)
            		{
            			if(listConstraint.get(j)==u)
            			{
            				listEleConstraint.add(listConstraint.get(j));
            			}
            		}
            	}
        		
//        		javax.swing.JOptionPane.showMessageDialog(null, listData.get(i).getOwner().getOwner().getHumanName());
        		createConstraint(listEleConstraint, listData.get(i).getName(), listData.get(i).getOwner().getOwner().getHumanName(), cMain);
        	}
        	
        	
        }
        
        return(listData.size());
    }

    /** Computes all possible attributes to a given content class and replaces old ones
     * 
     * @param cMain the content class
     * @param bSkipAssociations skip the attribute candidates that are also candidates for a association?
     * @return created attributes count
     */
    public static int replaceAttributesToContentClass(Class cMain,
            boolean bSkipAssociations)
    {
        ArrayList<TypedElement> listData=collectAttributeCandidates(cMain.getName());

        createAttributes(listData,cMain);
        if (!listData.isEmpty())
        {
            listData=removeHomonyms(listData);

            Iterator<Property> itProps=cMain.getAttribute().iterator();
            ArrayList<Property> listProps=new ArrayList<Property>();
            while (itProps.hasNext())
            {
                listProps.add(itProps.next());
            }
            
            for (int i=listProps.size()-1;i>=0;i--)
            {
                int iPos=ElementCollector.getNamedElementFromArrayList(listData,listProps.get(i).getName(),true,false);
                if (iPos!=ElementCollector.iNO_ELEMENT)
                {
                    listProps.get(i).dispose();
                }
            }

            if (bSkipAssociations)
            {
                listData=skipAssociationCandidates(listData);
            }
            
            createAttributes(listData,cMain);
        }
        return(listData.size());
    }

    /** Creates a list of associations to a given content class
     * 
     * @param listElements list of attribute candidates
     * @param cMain the content class
     * @param prShape a presentation element of the content class
     */
    public static void createAssociations(ArrayList<TypedElement> listElements, 
            Class cMain, 
            PresentationElement prShape)
    {
        ArrayList<Association> listAssociations=new ArrayList<Association>();

        boolean bCreated=false;
        if (!SessionManager.getInstance().isSessionCreated())
        {
            bCreated=true;
            SessionManager.getInstance().createSession("Create Associations");
        }
        
        if (!listElements.isEmpty())
        {
            ArrayList<NamedElement> listModels=ElementCollector.getNamedElements(UWEDiagramType.CONTENT.modelStereotype, null, Model.class, false, true);
            ArrayList<NamedElement> listClasses=ElementCollector.getNamedElements(null, null, Class.class, true, listModels, false);
            listModels=ElementCollector.getNamedElements(UWEDiagramType.USER_MODEL.modelStereotype, null, Model.class, false, true);
            ArrayList<NamedElement> listTempClasses=ElementCollector.getNamedElements(null, null, Class.class, true, listModels, false);

            for (int i=0;i<listTempClasses.size();i++)
            {
                listClasses.add(listTempClasses.get(i));
            }

            for (int i=0;i<listElements.size();i++)
            {
                if (listElements.get(i) instanceof Pin)
                {
                    int iClass=ElementCollector.getNamedElementFromArrayList(listClasses,listElements.get(i).getName(),false,false,true,"s");
                    if (iClass!=ElementCollector.iNO_ELEMENT)
                    {
                        Association cAssociation=Application.getInstance().getProject().getElementsFactory().createAssociationInstance();

                        cAssociation.setOwner(cMain.getOwner());
                        Application.getInstance().getProject().addElementByID(cAssociation,cAssociation.getID());

                        cAssociation.getOwnedEnd().get(1).setType(cMain);
                        cAssociation.getOwnedEnd().get(1).setOwner(listClasses.get(iClass));

                        if (((MultiplicityElement)(listElements.get(i))).getUpperValue()!=null)
                        {
                            if (((MultiplicityElement)(listElements.get(i))).getUpperValue() instanceof LiteralInteger)
                            {
                                LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                litInt.setValue(((LiteralInteger)(((MultiplicityElement)(listElements.get(i))).getUpperValue())).getValue());
                                cAssociation.getOwnedEnd().get(0).setUpperValue(litInt);
                            }
                            else if (((MultiplicityElement)(listElements.get(i))).getUpperValue() instanceof LiteralUnlimitedNatural)
                            {
                                LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                litUN.setValue(((LiteralUnlimitedNatural)(((MultiplicityElement)(listElements.get(i))).getUpperValue())).getValue());
                                cAssociation.getOwnedEnd().get(0).setUpperValue(litUN);
                            }
                        }
                        if (((MultiplicityElement)(listElements.get(i))).getLowerValue()!=null)
                        {
                            if (((MultiplicityElement)(listElements.get(i))).getLowerValue() instanceof LiteralInteger)
                            {
                                LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                litInt.setValue(((LiteralInteger)(((MultiplicityElement)(listElements.get(i))).getLowerValue())).getValue());
                                cAssociation.getOwnedEnd().get(0).setLowerValue(litInt);
                            }
                            else if (((MultiplicityElement)(listElements.get(i))).getLowerValue() instanceof LiteralUnlimitedNatural)
                            {
                                LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                litUN.setValue(((LiteralUnlimitedNatural)(((MultiplicityElement)(listElements.get(i))).getLowerValue())).getValue());
                                cAssociation.getOwnedEnd().get(0).setLowerValue(litUN);
                            }
                        }

                        if (listElements.get(i).getName().contains(listClasses.get(iClass).getName()+"s"))
                        {
                            cAssociation.getOwnedEnd().get(0).setName(listElements.get(i).getName().replaceAll(listClasses.get(iClass).getName()+"s",""));
                        }
                        else if (listElements.get(i).getName().toLowerCase().contains(listClasses.get(iClass).getName().toLowerCase()+"s"))
                        {
                            cAssociation.getOwnedEnd().get(0).setName(listElements.get(i).getName().toLowerCase().replaceAll(listClasses.get(iClass).getName().toLowerCase()+"s",""));
                        }
                        else if(listElements.get(i).getName().contains(listClasses.get(iClass).getName()))
                        {
                            cAssociation.getOwnedEnd().get(0).setName(listElements.get(i).getName().replaceAll(listClasses.get(iClass).getName(),""));
                        }
                        else if (listElements.get(i).getName().toLowerCase().contains(listClasses.get(iClass).getName().toLowerCase()))
                        {
                            cAssociation.getOwnedEnd().get(0).setName(listElements.get(i).getName().toLowerCase().replaceAll(listClasses.get(iClass).getName().toLowerCase(),""));
                        }
                        cAssociation.getOwnedEnd().get(0).setType((Class)(listClasses.get(iClass)));
                        cAssociation.getOwnedEnd().get(0).setOwner(cMain);
                        listAssociations.add(cAssociation);
                    }
                }
                else if (listElements.get(i) instanceof Property)
                {
                    Property propTemp=((Property)(listElements.get(i)));
                    Property propEnd=null;
                    for (int c=0;c<propTemp.getAssociation().getMemberEnd().size();c++)
                    {
                        if (propTemp.getAssociation().getMemberEnd().get(c)!=propTemp)
                        {
                            propEnd=propTemp.getAssociation().getMemberEnd().get(c);
                            break;
                        }
                    }
                    
                    if (propEnd.getType()!=null)
                    {
                        int iClass=ElementCollector.getNamedElementFromArrayList(listClasses,propEnd.getType().getName(),false,false);
                        if (iClass!=ElementCollector.iNO_ELEMENT)
                        {
                            if (cMain==listClasses.get(iClass))
                            {
                                for (int ii=i;ii<listElements.size();ii++)
                                {
                                    if (listElements.get(ii)==propEnd)
                                    {
                                        listElements.remove(ii);
                                        break;
                                    }
                                }
                            }

                            Association cAssociation=Application.getInstance().getProject().getElementsFactory().createAssociationInstance();

                            cAssociation.setName(propTemp.getAssociation().getName());
                            cAssociation.setOwner(cMain.getOwner());
                            Application.getInstance().getProject().addElementByID(cAssociation,cAssociation.getID());
                            
                            if (propTemp.getUpperValue()!=null)
                            {
                                if (propTemp.getUpperValue() instanceof LiteralInteger)
                                {
                                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                    litInt.setValue(((LiteralInteger)(propTemp.getUpperValue())).getValue());
                                    cAssociation.getOwnedEnd().get(1).setUpperValue(litInt);
                                }
                                else if (propTemp.getUpperValue() instanceof LiteralUnlimitedNatural)
                                {
                                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                    litUN.setValue(((LiteralUnlimitedNatural)(propTemp.getUpperValue())).getValue());
                                    cAssociation.getOwnedEnd().get(1).setUpperValue(litUN);
                                }
                            }
                            if (propTemp.getLowerValue()!=null)
                            {
                                if (propTemp.getLowerValue() instanceof LiteralInteger)
                                {
                                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                    litInt.setValue(((LiteralInteger)(propTemp.getLowerValue())).getValue());
                                    cAssociation.getOwnedEnd().get(1).setLowerValue(litInt);
                                }
                                else if (propTemp.getLowerValue() instanceof LiteralUnlimitedNatural)
                                {
                                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                    litUN.setValue(((LiteralUnlimitedNatural)(propTemp.getLowerValue())).getValue());
                                    cAssociation.getOwnedEnd().get(1).setLowerValue(litUN);
                                }
                            }
                            cAssociation.getOwnedEnd().get(1).setName(propTemp.getName());
                            cAssociation.getOwnedEnd().get(1).setType(cMain);
                            cAssociation.getOwnedEnd().get(1).setOwner(listClasses.get(iClass));

                            if (propEnd.getUpperValue()!=null)
                            {
                                if (propEnd.getUpperValue() instanceof LiteralInteger)
                                {
                                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                    litInt.setValue(((LiteralInteger)(propEnd.getUpperValue())).getValue());
                                    cAssociation.getOwnedEnd().get(0).setUpperValue(litInt);
                                }
                                else if (propEnd.getUpperValue() instanceof LiteralUnlimitedNatural)
                                {
                                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                    litUN.setValue(((LiteralUnlimitedNatural)(propEnd.getUpperValue())).getValue());
                                    cAssociation.getOwnedEnd().get(0).setUpperValue(litUN);
                                }
                            }
                            if (propEnd.getLowerValue()!=null)
                            {
                                if (propEnd.getLowerValue() instanceof LiteralInteger)
                                {
                                    LiteralInteger litInt=Application.getInstance().getProject().getElementsFactory().createLiteralIntegerInstance();
                                    litInt.setValue(((LiteralInteger)(propEnd.getLowerValue())).getValue());
                                    cAssociation.getOwnedEnd().get(0).setLowerValue(litInt);
                                }
                                else if (propEnd.getLowerValue() instanceof LiteralUnlimitedNatural)
                                {
                                    LiteralUnlimitedNatural litUN=Application.getInstance().getProject().getElementsFactory().createLiteralUnlimitedNaturalInstance();
                                    litUN.setValue(((LiteralUnlimitedNatural)(propEnd.getLowerValue())).getValue());
                                    cAssociation.getOwnedEnd().get(0).setLowerValue(litUN);
                                }
                            }
                            cAssociation.getOwnedEnd().get(0).setName(propEnd.getName());
                            cAssociation.getOwnedEnd().get(0).setType((Class)(listClasses.get(iClass)));
                            cAssociation.getOwnedEnd().get(0).setOwner(cMain);
                            listAssociations.add(cAssociation);
                        }
                    }
                }
            }
        }

        for (int i=0;i<listAssociations.size();i++)
        {
            Iterator<Property> itProp=listAssociations.get(i).getMemberEnd().iterator();
            ArrayList<PresentationElement> listPresentations=new ArrayList<PresentationElement>();
            ArrayList<Diagram> listDiagrams=new ArrayList<Diagram>();
            listDiagrams.add(Application.getInstance().getProject().getActiveDiagram().getDiagram());

            while (itProp.hasNext())
            {
                Property propTemp=itProp.next();
                if (propTemp.getType()!=cMain)
                {
                    listPresentations=ElementCollector.getDiagramPresentationElements(null, listDiagrams, propTemp.getType());
                }
            }

            if (!listPresentations.isEmpty())
            {

                if (listPresentations.size()==1)
                {
                    if (prShape==null)
                    {
                        PresentationElement elPresentation=null;
                        for (int c=0;c<Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().size();c++)
                        {
                            if (Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c).getElement()==
                                    cMain)
                            {
                                elPresentation=Application.getInstance().getProject().getActiveDiagram().getDiagramPresentationElement().getPresentationElements().get(c);
                            }
                        }

                        if (elPresentation!=null)
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),elPresentation,listPresentations.get(0));
                            }
                            catch (Exception e)
                            {
                                try
                                {
                                    PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentations.get(0),elPresentation);
                                }
                                catch (Exception e2)
                                {
                                    Application.getInstance().getGUILog().showError("Exception in ReqToConTransformationRules.createAssociations(): "+e2.toString());
                                    if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                    {
                                        SessionManager.getInstance().closeSession();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),prShape,listPresentations.get(0));
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().createPathElement(listAssociations.get(i),listPresentations.get(0),prShape);
                            }
                            catch (Exception e2)
                            {
                                Application.getInstance().getGUILog().showError("Exception in ReqToConTransformationRules.createAssociations(): "+e2.toString());
                                if ((bCreated)&&(SessionManager.getInstance().isSessionCreated()))
                                {
                                    SessionManager.getInstance().closeSession();
                                }
                                return;
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
    }

    /** Computes all possible associations to a given content class and adds new ones
     * 
     * @param cMain the content class
     * @param prShape a presentation element of the content class
     * @return added associations count
     */
    public static int addAssociationsToContentClass(Class cMain, 
            PresentationElement prShape)
    {
        ArrayList<TypedElement> listData=collectAssociations(cMain.getName());

        if (!listData.isEmpty())
        {
            listData=removeHomonyms(listData);

            Iterator<Property> itProps=cMain.getAttribute().iterator();
            while (itProps.hasNext())
            {
                int iPos=ElementCollector.getNamedElementFromArrayList(listData,itProps.next().getName(),true,false);
                if (iPos!=ElementCollector.iNO_ELEMENT)
                {
                    listData.remove(iPos);
                }
            }

            createAssociations(listData,cMain,prShape);
        }
        return(listData.size());
    }

    /** Computes all possible associations to a given content class and replaces old ones
     * 
     * @param cMain the content class
     * @param prShape a presentation element of the content class
     * @return added associations count
     */
    public static int replaceAssociationsToContentClass(Class cMain, 
            PresentationElement prShape)
    {
        ArrayList<TypedElement> listData=collectAssociations(cMain.getName());

        if (!listData.isEmpty())
        {
            listData=removeHomonyms(listData);

            Iterator<Property> itProps=cMain.getAttribute().iterator();
            while (itProps.hasNext())
            {
                Property prop=itProps.next();
                int iPos=ElementCollector.getNamedElementFromArrayList(listData,prop.getName(),true,false);
                if (iPos!=ElementCollector.iNO_ELEMENT)
                {
                    prop.dispose();
                }
            }

            createAssociations(listData,cMain,prShape);
        }
        return(listData.size());
    }

    /** Computes all possible operation to a given content class and adds new ones
     * 
     * @param cMain the content class
     * @param bSkipAssociations skip the attribute candidates that are also candidates for a association?
     * @return created operation count
     */
    public static int addOperationToContentClass(Class cMain,
            boolean bSkipAssociations)
    {
    	ArrayList<NamedElement> listData=collectOperationCandidates(cMain.getName());
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> listAssociation=ElementCollector.getNamedElements(null, null, Association.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqUser=ElementCollector.getNamedElements(null, null, Actor.class, false, listReqModels, false);
        Actor actorTemp = null;

        if (!listReqUser.isEmpty())
        {
//            listData=removeHomonyms(listData);

//            Iterator<Operation> itProps=cMain.getOwnedOperation().iterator();
//            while (itProps.hasNext())
//            {
//                int iPos=ElementCollector.getNamedElementFromArrayList(listData,itProps.next().getName(),true,false);
//                if (iPos!=ElementCollector.iNO_ELEMENT)
//                {
//                    listData.remove(iPos);
//                }
//            }
            
//            if (bSkipAssociations)
//            {
//                listData=skipAssociationCandidates(listData);
//            }
        	
            for(int i=0;i<listReqUser.size();i++)
            {
            	if(Objects.equals(listReqUser.get(i).getName(), cMain.getName()))
            	{
            		actorTemp = (Actor)(listReqUser.get(i));
            		
            		break;
            	}
            }
            if(actorTemp==null)
            {
            	listData.removeAll(listData);
            }
        	for(int i=0;i<listAssociation.size();i++)
            {
            	Association propAssociation=((Association)(listAssociation.get(i)));
            	if(propAssociation.getRelatedElement().toArray()[1]!=actorTemp)
            	{
            		listAssociation.remove(listAssociation.get(i));
            	}
            }
            for(int i=0;i<listData.size();i++)
            {
            	int tempResult = 0;
            	for (int j=0;j<listAssociation.size();j++)
                {
                	Association propAssociation=((Association)(listAssociation.get(j)));
                	if(propAssociation.getRelatedElement().toArray()[0]==listData.get(i))
                	{
                		tempResult = 1;
                		break;
                	}
                }
            	if(tempResult == 0)
            	{
            		listData.remove(listData.get(i));
            	}
            }
            createOperation(listData,cMain);
        }
        
        return(listData.size());
    }

    
    /** Computes a list of content class candidates.
     * 
     * @param bRemoveDuplicates also remove same named elements?
     * @return list of candidates
     */
    public static ArrayList<NamedElement> getContentClassCandidates(boolean bRemoveDuplicates)
    {
        ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
        if ((listReqModels==null)||(listReqModels.isEmpty()))
        {
        	MessageWriter.log("No use case model found.", null);
            return(new ArrayList<NamedElement>());
        }
        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(null, null, CentralBufferNode.class, false, listReqModels, false);
        ArrayList<NamedElement> listReqUser=ElementCollector.getNamedElements(null, null, Actor.class, false, listReqModels, false);
        for (int i=0;i<listReqUser.size();i++)
    	{
        	listReturn.add(listReqUser.get(i));
    	}
        
        if (bRemoveDuplicates)
        {
            for (int i=listReturn.size()-1;i>=0;i--)
            {
                if (ElementCollector.getNamedElementFromArrayList(listReturn, listReturn.get(i).getName(),false,false)<i)
                {
                    listReturn.remove(i);
                }
            }
        }
        return(listReturn);
    }
    
//    /** Computes a list of user class candidates.
//     * 
//     * @param bRemoveDuplicates also remove same named elements?
//     * @return list of candidates
//     */
//    public static ArrayList<NamedElement> getUserClassCandidates(boolean bRemoveDuplicates)
//    {
//    	ArrayList<NamedElement> listReqModels=ElementCollector.getNamedElements(UWEDiagramType.USE_CASE.modelStereotype, null, Model.class, false, true);
//    	
//        ArrayList<NamedElement> listReturn=ElementCollector.getNamedElements(null, null, Actor.class, false, listReqModels, false);
////        for (int j=0;j<listReturn.size();j++)
////        {
////        	javax.swing.JOptionPane.showMessageDialog(null, listReturn.get(j).getName());
////        }
//        
//    	return(listReturn);
//    }
}