/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.util.ArrayList;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.transformation.requirements.ReqToNavTransformations;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
        
import org.apache.log4j.Logger;

/**
 *
 * @author PST LMU
 */
public class NavigationModelModification extends DefaultDiagramAction
{
	private static final long serialVersionUID = -5814304999140833704L;
	public static final String sACTION_ADD_LINKS="Create Links";
    public static final String sACTION_ADD_LINKS_AND_MENU="Create Links And Menu";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public NavigationModelModification(String name, KeyStroke keyStroke, String group)
    {
            super(name,name,keyStroke,group);
    }

    /** Is called when an event occured. Adds associations or / and menu to a navigation or process class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {

        if (this.getName().equals(sACTION_ADD_LINKS))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                if (StereotypesHelper.hasStereotype(acTempElements.get(e).getElement(),UWEStereotypeClassNav.NAVIGATION_CLASS.toString()))
                {
                    ReqToNavTransformations.clearAssociationsAndMenu(((Class)(acTempElements.get(e).getElement())));
                    ElementCollector.ReturnElement cMenu=ReqToNavTransformations.addNavigationClassAssociations(((Class)(acTempElements.get(e).getElement())), acTempElements.get(e),false);
                
                    if (cMenu==null)
                    {
                    }
                    else if (cMenu.cClass==null)
                    {
                        MessageWriter.log("Added "+cMenu.iChildren+" associations and a menu to navigation class \""+
                            ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                            Logger.getLogger(ContentModelModification.class));
                    }
                    else
                    {
                        MessageWriter.log("Added "+cMenu.iChildren+" associations to navigation class \""+
                            ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                            Logger.getLogger(ContentModelModification.class));
                    }
                }
                else if (StereotypesHelper.hasStereotype(acTempElements.get(e).getElement(),UWEStereotypeClassGeneral.PROCESS_CLASS.toString()))
                {
                    ReqToNavTransformations.clearAssociations(((Class)(acTempElements.get(e).getElement())));
                    ElementCollector.ReturnElement cAdd=ReqToNavTransformations.addProcessClassAssociations(((Class)(acTempElements.get(e).getElement())), acTempElements.get(e));
                    
                    MessageWriter.log("Added "+cAdd.iChildren+" associations to process class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
                }
            }
        }
        else if (this.getName().equals(sACTION_ADD_LINKS_AND_MENU))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                if (StereotypesHelper.hasStereotype(acTempElements.get(e).getElement(),UWEStereotypeClassNav.NAVIGATION_CLASS.toString()))
                {
                    ReqToNavTransformations.clearAssociationsAndMenu(((Class)(acTempElements.get(e).getElement())));
                    ElementCollector.ReturnElement cMenu=ReqToNavTransformations.addNavigationClassAssociations(((Class)(acTempElements.get(e).getElement())), acTempElements.get(e),true);
                    
                    if (cMenu==null)
                    {
                    }
                    else if (cMenu.cClass==null)
                    {
                        MessageWriter.log("Added "+cMenu.iChildren+" associations and a menu to navigation class \""+
                            ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                            Logger.getLogger(ContentModelModification.class));
                    }
                    else
                    {
                        MessageWriter.log("Added "+cMenu.iChildren+" associations to navigation class \""+
                            ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                            Logger.getLogger(ContentModelModification.class));
                    }
                }
                else if (StereotypesHelper.hasStereotype(acTempElements.get(e).getElement(),UWEStereotypeClassGeneral.PROCESS_CLASS.toString()))
                {
                    ReqToNavTransformations.clearAssociations(((Class)(acTempElements.get(e).getElement())));
                    ElementCollector.ReturnElement cAdd=ReqToNavTransformations.addProcessClassAssociations(((Class)(acTempElements.get(e).getElement())), acTempElements.get(e));
                    
                    MessageWriter.log("Added "+cAdd.iChildren+" associations to process class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
                }
            }
        }
    }
}
