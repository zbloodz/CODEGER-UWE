/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.transformation.requirements;

import java.util.Iterator;

import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import magicUWE.shared.MessageWriter;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

/**
 * Needs refactoring
 * @author PST LMU
 */
public class ElementCollector
{   
    public static final int iNO_ELEMENT=-1;

    public static class ReturnElement
    {
        public Class cClass;
        public PresentationElement cShape;
        public int iChildren;
        public ArrayList<Class> additionalClasses;
        public ArrayList<PresentationElement> additionalShapes;

        public ReturnElement(Class cClass,PresentationElement cShape,int iChildren,
                ArrayList<Class> additionalClasses,ArrayList<PresentationElement> additionalShapes)
        {
            this.cClass=cClass;
            this.cShape=cShape;
            this.iChildren=iChildren;
            this.additionalClasses=additionalClasses;
            this.additionalShapes=additionalShapes;
        }
    };
    
    private static boolean shouldElementBeAdded(String sStereotype, java.lang.Class<?> tElementType,
			boolean bSubclasses, ArrayList<Element> tempElements, int i, String sName) {
		// FIXME .contains simple name is just used, because Model and ModelImpl (and similar Impl classes)
		return (((!bSubclasses)&&(tempElements.get(i).getClass().getSimpleName().contains(tElementType.getSimpleName()))) ||
	            ((bSubclasses)&&(tElementType.isInstance(tempElements.get(i))))) &&
				((sName==null) || (((NamedElement)(tempElements.get(i))).getName().equals(sName))) &&
	            ((sStereotype==null) || (StereotypesHelper.hasStereotype(tempElements.get(i), sStereotype)));
	}

	/** Finds first match to a given name
     * 
     * @param arrayNamedElements list of named elements
     * @param sName required name
     * @param bCaseSensitive case sensitive comparison?
     * @param bClearSpaces remove all white spaces from names?
     * @return index of the first match
     */
    public static <N extends NamedElement> int getNamedElementFromArrayList(ArrayList<N> arrayNamedElements,
            String sName,
            boolean bCaseSensitive, 
            boolean bClearSpaces)
    {
        for (int i=0;i<arrayNamedElements.size();i++)
        {
            if ((!bCaseSensitive)&&((sName.toLowerCase().equals(arrayNamedElements.get(i).getName().toLowerCase()))||
                ((bClearSpaces)&&(sName.toLowerCase().replaceAll(" ","").equals(arrayNamedElements.get(i).getName().toLowerCase().replaceAll(" ",""))))))
            {
                return(i);
            }
            else if ((bCaseSensitive)&&((sName.equals(arrayNamedElements.get(i).getName()))||
                ((bClearSpaces)&&(sName.replaceAll(" ","").equals(arrayNamedElements.get(i).getName().replaceAll(" ",""))))))
            {
                return(i);
            }
        }

        return(-1);
    }
    
    /**Finds first match to a given name
     * 
     * @param arrayNamedElements list of named elements
     * @param sName required name
     * @param bCaseSensitive case sensitive comparison?
     * @param bClearSpaces remove all white spaces from names?
     * @param bPart can the given name be only a part of the found one?
     * @param sAdditional ignored additional text at the end of the name
     * @return index of the first match
     */
    public static <N extends NamedElement> int getNamedElementFromArrayList(ArrayList<N> arrayNamedElements,
            String sName,
            boolean bCaseSensitive, 
            boolean bClearSpaces,
            boolean bPart,
            String sAdditional)
    {
        for (int i=0;i<arrayNamedElements.size();i++)
        {
            if ((bPart)&&((!bCaseSensitive)&&((sName.toLowerCase().contains(arrayNamedElements.get(i).getName().toLowerCase()))||
                ((bClearSpaces)&&(sName.toLowerCase().replaceAll(" ","").contains(arrayNamedElements.get(i).getName().toLowerCase().replaceAll(" ","")))))))
            {
                return(i);
            }
            else if ((bPart)&&((!bCaseSensitive)&&((sName.toLowerCase().contains(arrayNamedElements.get(i).getName().toLowerCase()+sAdditional))||
                ((bClearSpaces)&&(sName.toLowerCase().replaceAll(" ","").contains((arrayNamedElements.get(i).getName().toLowerCase()+sAdditional).replaceAll(" ","")))))))
            {
                return(i);
            }
            else if ((!bCaseSensitive)&&((sName.toLowerCase().equals(arrayNamedElements.get(i).getName().toLowerCase()))||
                ((bClearSpaces)&&(sName.toLowerCase().replaceAll(" ","").equals(arrayNamedElements.get(i).getName().toLowerCase().replaceAll(" ",""))))))
            {
                return(i);
            }
            else if ((bCaseSensitive)&&((sName.equals(arrayNamedElements.get(i).getName()))||
                ((bClearSpaces)&&(sName.replaceAll(" ","").equals(arrayNamedElements.get(i).getName().replaceAll(" ",""))))))
            {
                return(i);
            }
        }

        return(-1);
    }

    /** Collects elements from the project with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param listParents a list of element the collected should be a child of
     * @param bDirectOnly only collect direct children?
     * @return list of elements matching the parameters
     */
    public static <E extends Element> ArrayList<Element> getElements(String sStereotype,
            java.lang.Class<?> tElementType,
            boolean bSubclasses,
            ArrayList<E> listParents,
            boolean bDirectOnly)
    {
        ArrayList<Element> listReturn=new ArrayList<Element>();
        ArrayList<Element> tempElements=new ArrayList<Element>();

        if (!Element.class.isAssignableFrom(tElementType))
        {
            return(listReturn);
        }

        if (listParents==null)
        {
            listParents=new ArrayList<E>();
        }

        if (listParents.isEmpty())
        {
            Iterator<Element> cIterator=Application.getInstance().getMainFrame().getBrowser().getContainmentTree().getRootElement().getOwnedElement().iterator();

            while (cIterator.hasNext())
            {
                Element cAdd=cIterator.next();
                if (!tempElements.contains(cAdd))
                {
                    tempElements.add(cAdd);
                }
            }
        }
        else
        {
            for (int i=0;i<listParents.size();i++)
            {
                Iterator<Element> cIterator=listParents.get(i).getOwnedElement().iterator();

                while (cIterator.hasNext())
                {
                    Element cAdd=cIterator.next();
                    if (!tempElements.contains(cAdd))
                    {
                        tempElements.add(cAdd);
                    }
                }
            }
        }

        for (int i=0;i<tempElements.size();i++)
        {
            if (!bDirectOnly)
            {
                Iterator<Element> cIterator=tempElements.get(i).getOwnedElement().iterator();

                while (cIterator.hasNext())
                {
                    Element cAdd=cIterator.next();
                    if (!tempElements.contains(cAdd))
                    {
                        tempElements.add(cAdd);
                    }
                }
            }

            if (shouldElementBeAdded(sStereotype, tElementType, bSubclasses, tempElements, i, null))
            {
                listReturn.add((tempElements.get(i)));
            }
        }

        return(listReturn);
    }
    
    /** Collects elements from a list of diagrams with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param cDiagrams parent diagram
     * @return list of elements matching the parameters
     */
    public static ArrayList<Element> getDiagramElements(String sStereotype,
             java.lang.Class<?> tElementType,
            boolean bSubclasses,
            Diagram cDiagrams)
    {
        ArrayList<Diagram> tempDiagrams=new ArrayList<Diagram>();
        tempDiagrams.add(cDiagrams);
        return(getDiagramElements(sStereotype,tElementType,bSubclasses,tempDiagrams));
    }

    /** Collects elements from a list of diagrams with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param listDiagrams list of diagrams
     * @return list of elements matching the parameters
     */
    public static ArrayList<Element> getDiagramElements(String sStereotype,
             java.lang.Class<?> tElementType,
            boolean bSubclasses,
            ArrayList<Diagram> listDiagrams)
    {
        ArrayList<Element> listReturn=new ArrayList<Element>();
        ArrayList<Element> tempElements=new ArrayList<Element>();

        if (!Element.class.isAssignableFrom(tElementType))
        {
            return(listReturn);
        }

        if (listDiagrams==null)
        {
            listDiagrams=new ArrayList<Diagram>();
        }

        if (listDiagrams.isEmpty())
        {
            listDiagrams.add(Application.getInstance().getProject().getActiveDiagram().getDiagram());
        }

        for (int i=0;i<listDiagrams.size();i++)
        {
            Iterator<PresentationElement> cIterator=Application.getInstance().getProject().
                    getDiagram(listDiagrams.get(i)).getPresentationElements().iterator();

            while (cIterator.hasNext())
            {
                Element cAdd=cIterator.next().getElement();
                if (!tempElements.contains(cAdd))
                {
                    tempElements.add(cAdd);
                }
            }
        }

        for (int i=0;i<tempElements.size();i++)
        {
            if (shouldElementBeAdded(sStereotype, tElementType, bSubclasses, tempElements, i, null))
            {
                listReturn.add(tempElements.get(i));
            }
        }

        return(listReturn);
    }
	
	/** Collects presentation elements from a list of diagrams with the given parameters
     * 
     * @param sName name of the element
     * @param cDiagrams parent diagram
     * @param elType element presented by collected presentation elements
     * @return list of presentation elements
     */
    public static ArrayList<PresentationElement> getDiagramPresentationElements(String sName,
            Diagram cDiagrams,
            Element elType)
    {
        ArrayList<Diagram> listDiagrams=new ArrayList<Diagram>();
        listDiagrams.add(cDiagrams);
        return(getDiagramPresentationElements(sName,listDiagrams,elType));
    }

    /** Collects presentation elements from a list of diagrams with the given parameters
     * 
     * @param sName name of the element
     * @param listDiagrams list of diagrams
     * @param elType element presented by collected presentation elements
     * @return list of presentation elements
     */
    public static ArrayList<PresentationElement> getDiagramPresentationElements(String sName,
            ArrayList<Diagram> listDiagrams,
            Element elType)
    {
        ArrayList<PresentationElement> listReturn=new ArrayList<PresentationElement>();
        ArrayList<PresentationElement> tempElements=new ArrayList<PresentationElement>();

        if (listDiagrams==null)
        {
            listDiagrams=new ArrayList<Diagram>();
        }

        if (listDiagrams.isEmpty())
        {
            listDiagrams.add(Application.getInstance().getProject().getActiveDiagram().getDiagram());
        }

        for (int i=0;i<listDiagrams.size();i++)
        {
            Iterator<PresentationElement> cIterator=Application.getInstance().getProject().
                    getDiagram(listDiagrams.get(i)).getPresentationElements().iterator();

            while (cIterator.hasNext())
            {
                PresentationElement cAdd=cIterator.next();
                if (!tempElements.contains(cAdd))
                {
                    tempElements.add(cAdd);
                }
            }
        }

        for (int i=0;i<tempElements.size();i++)
        {
            if (((sName==null) || (((NamedElement)(tempElements.get(i))).getName().equals(sName)))&&
                ((elType==null) || (tempElements.get(i).getElement()==elType)))
            {
                listReturn.add(tempElements.get(i));
            }
        }

        return(listReturn);
    }

    /** Collects named elements from the project with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param sName name of the elements (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param bDirectOnly only collect direct children?
     * @return list of elements matching the parameters
     */
    public static <E extends Element> ArrayList<NamedElement> getNamedElements(String sStereotype,
            String sName,
            java.lang.Class<?> tElementType,
            boolean bSubclasses,
            boolean bDirectOnly)
    {
        ArrayList<E> listParents=new ArrayList<E>();
        return(getNamedElements(sStereotype,sName,tElementType,bSubclasses,listParents,bDirectOnly));
    }

    /** Collects named elements from the project with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param sName name of the elements (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param cParent parent of the collected elements
     * @param bDirectOnly only collect direct children?
     * @return list of elements matching the parameters
     */
    public static <E extends Element> ArrayList<NamedElement> getNamedElements(String sStereotype,
            String sName,
            java.lang.Class<?> tElementType,
            boolean bSubclasses,
            E cParent,
            boolean bDirectOnly)
    {
        ArrayList<E> listParents=new ArrayList<E>();
        listParents.add(cParent);
        return(getNamedElements(sStereotype,sName,tElementType,bSubclasses,listParents,bDirectOnly));
    }

    /**Collects named elements from the project with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param sName name of the elements (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param listParents list of parents of the collected classes
     * @param bDirectOnly only collect direct children?
     * @return list of elements matching the parameters
     */
    public static <E extends Element> ArrayList<NamedElement> getNamedElements(String sStereotype,
            String sName,
            java.lang.Class<?> tElementType,
            boolean bSubclasses,
            ArrayList<E> listParents,
            boolean bDirectOnly)
    {
        ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
        ArrayList<Element> tempElements=new ArrayList<Element>();

        if (!NamedElement.class.isAssignableFrom(tElementType))
        {
        	// MessageWriter.log("ERROR: NamedElement is not assignable from element " + tElementType.getName(), null);
            return(listReturn);
        }

        if (listParents.isEmpty())
        {
            Iterator<Element> cIterator=Application.getInstance().getMainFrame().getBrowser().getContainmentTree().
                    getRootElement().getOwnedElement().iterator();

            while (cIterator.hasNext())
            {
                Element cAdd=cIterator.next();
                if (!tempElements.contains(cAdd))
                {
                    tempElements.add(cAdd);
                }
            }
        }
        else
        {
            for (int i=0;i<listParents.size();i++)
            {
                Iterator<Element> cIterator=listParents.get(i).getOwnedElement().iterator();

                while (cIterator.hasNext())
                {
                    Element cAdd=cIterator.next();
                    if (!tempElements.contains(cAdd))
                    {
                        tempElements.add(cAdd);
                    }
                }
            }
        }

        
        // MessageWriter.log("Number of tmp elements: " + tempElements.size(), null);
        for (int i=0;i<tempElements.size();i++)
        {
            if (!bDirectOnly)
            {
                Iterator<Element> cIterator=tempElements.get(i).getOwnedElement().iterator();
                
                while (cIterator.hasNext())
                {
                    Element cAdd=cIterator.next();
                    if (!tempElements.contains(cAdd))
                    {
                        tempElements.add(cAdd);
                    }
                }
            }

            if (shouldElementBeAdded(sStereotype, tElementType, bSubclasses, tempElements, i, sName))
            {
                listReturn.add(((NamedElement)(tempElements.get(i))));
            }
        }

        return(listReturn);
    }

    /** Collects named elements from a list of diagrams with the given parameters
     * 
     * @param sStereotype name of the stereotype (null to ignore)
     * @param sName name of the elements (null to ignore)
     * @param tElementType ElementType type of the element (null to ignore)
     * @param bSubclasses collect also the subclasses?
     * @param listDiagrams list of diagrams
     * @return list of elements matching the parameters
     */
    public static ArrayList<NamedElement> getDiagramNamedElements(String sStereotype,
            String sName,
            java.lang.Class<?> tElementType,
            boolean bSubclasses,
            ArrayList<Diagram> listDiagrams)
    {
        ArrayList<NamedElement> listReturn=new ArrayList<NamedElement>();
        ArrayList<Element> tempElements=new ArrayList<Element>();

        if (!NamedElement.class.isAssignableFrom(tElementType))
        {
            return(listReturn);
        }

        if (listDiagrams==null)
        {
            listDiagrams=new ArrayList<Diagram>();
        }

        if (listDiagrams.isEmpty())
        {
            listDiagrams.add(Application.getInstance().getProject().getActiveDiagram().getDiagram());
        }

        for (int i=0;i<listDiagrams.size();i++)
        {
            Iterator<PresentationElement> cIterator=Application.getInstance().getProject().
                    getDiagram(listDiagrams.get(i)).getPresentationElements().iterator();

            while (cIterator.hasNext())
            {
                Element cAdd=cIterator.next().getElement();
                if (!tempElements.contains(cAdd))
                {
                    tempElements.add(cAdd);
                }
            }
        }

        for (int i=0;i<tempElements.size();i++)
        {
            if (shouldElementBeAdded(sStereotype, tElementType, bSubclasses, tempElements, i, sName))
            {
                listReturn.add(((NamedElement)(tempElements.get(i))));
            }
        }

        return(listReturn);
    }

    /** Collects selected presentation elements of a type
     * 
     * @param tClass elementtype
     * @return list of elements
     */
    public static ArrayList<PresentationElement> getSelectedPresentationElements(java.lang.Class<?> tClass)
    {
        ArrayList<PresentationElement> listReturn=new ArrayList<PresentationElement>();

        List<PresentationElement> listSelectedElements=Application.getInstance().getProject().getActiveDiagram().getSelected();

        for (int i=0;i<listSelectedElements.size();i++)
        {
            if (tClass.isInstance(listSelectedElements.get(i).getElement()))
            {
                listReturn.add(listSelectedElements.get(i));
            }
        }

        return(listReturn);
    }

    /** Collects selected presentation elements of one of the two types
     * 
     * @param tClass1 first elementtype
     * @param tClass2 second elementtype
     * @return list of elements
     */
    public static ArrayList<PresentationElement> getSelectedPresentationElements(java.lang.Class<?> tClass1,
            java.lang.Class<?> tClass2)
    {
        ArrayList<PresentationElement> listReturn=new ArrayList<PresentationElement>();

        List<PresentationElement> listSelectedElements=Application.getInstance().getProject().getActiveDiagram().getSelected();

        for (int i=0;i<listSelectedElements.size();i++)
        {
            if ((tClass1.isInstance(listSelectedElements.get(i).getElement()))||(tClass2.isInstance(listSelectedElements.get(i).getElement())))
            {
                listReturn.add(listSelectedElements.get(i));
            }
        }

        return(listReturn);
    }

    /** collects active diagrams
     *
     * @return list of active diagrams
     */
    public static ArrayList<Diagram> getActiveDiagram()
    {
        ArrayList<Diagram> listReturn=new ArrayList<Diagram>();
        listReturn.add(Application.getInstance().getProject().getActiveDiagram().getDiagram());
        return(listReturn);
    }
    
    /** Moves the given elements until there are no collisions
     * 
     * @param listElements list of elements
     * @param minX additional distance on the x-axis
     * @param minY additional distance on the y-axis
     */
    public static void removeCollisions(ArrayList<ReturnElement> listElements,
            int minX,
            int minY)
    {
        int iRuns=1000;
        for (int o=0;o<listElements.size();o++)
        {
            if (!SessionManager.getInstance().isSessionCreated())
            {
                SessionManager.getInstance().createSession("Move Shapes");
            }
            boolean bChanged=false;
            for (int i=o+1;i<listElements.size();i++)
            {
                Rectangle rectOut=new Rectangle(listElements.get(o).cShape.getBounds().x,
                        listElements.get(o).cShape.getBounds().y,
                    listElements.get(o).cShape.getBounds().width+minX,
                    listElements.get(o).cShape.getBounds().height+minY);
                Rectangle rectIn=new Rectangle(listElements.get(i).cShape.getBounds().x,
                        listElements.get(i).cShape.getBounds().y,
                    listElements.get(i).cShape.getBounds().width+minX,
                    listElements.get(i).cShape.getBounds().height+minY);
                Rectangle rectX=new Rectangle();

                if (rectOut.x>rectIn.x)
                {
                    rectX.x=rectOut.x;
                }
                else
                {
                    rectX.x=rectIn.x;
                }

                if (rectOut.y>rectIn.y)
                {
                    rectX.y=rectOut.y;
                }
                else
                {
                    rectX.y=rectIn.y;
                }

                if (rectOut.x+rectOut.width<rectIn.x+rectIn.width)
                {
                    rectX.width=rectOut.x+rectOut.width-rectX.x;
                }
                else
                {
                    rectX.width=rectIn.x+rectIn.width-rectX.x;
                }

                if (rectOut.y+rectOut.height<rectIn.y+rectIn.height)
                {
                    rectX.height=rectOut.y+rectOut.height-rectX.y;
                }
                else
                {
                    rectX.height=rectIn.y+rectIn.height-rectX.y;
                }

                if ((rectX.width>0)&&(rectX.height>0))
                {
                    if (rectX.width<=rectX.height)
                    {
                        if (rectOut.x<rectIn.x)
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(listElements.get(i).cShape)),
                                    new Rectangle(rectOut.x+rectOut.width,listElements.get(i).cShape.getBounds().y,
                                    listElements.get(i).cShape.getBounds().width,listElements.get(i).cShape.getBounds().height));
                                bChanged=true;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }
                        else
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(listElements.get(o).cShape)),
                                    new Rectangle(rectIn.x+rectIn.width,listElements.get(o).cShape.getBounds().y,
                                    listElements.get(o).cShape.getBounds().width,listElements.get(o).cShape.getBounds().height));
                                bChanged=true;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }
                    }
                    else
                    {
                        if (rectOut.y<rectIn.y)
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(listElements.get(i).cShape)),
                                    new Rectangle(listElements.get(i).cShape.getBounds().x,rectOut.y+rectOut.height,
                                    listElements.get(i).cShape.getBounds().width,listElements.get(i).cShape.getBounds().height));
                                bChanged=true;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }
                        else
                        {
                            try
                            {
                                PresentationElementsManager.getInstance().reshapeShapeElement(((ShapeElement)(listElements.get(o).cShape)),
                                    new Rectangle(listElements.get(o).cShape.getBounds().x,rectIn.y+rectIn.height,
                                    listElements.get(o).cShape.getBounds().width,listElements.get(o).cShape.getBounds().height));
                                bChanged=true;
                            }
                            catch(Exception e)
                            {
                                
                            }
                        }
                    }
                }
            }
            if ((bChanged)&&(iRuns>0))
            {
                o=-1;
                iRuns--;
            }
                    
            if (SessionManager.getInstance().isSessionCreated())
            {
                SessionManager.getInstance().closeSession();
            }
        }
    }
}
