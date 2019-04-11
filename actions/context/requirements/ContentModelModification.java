/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.util.ArrayList;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.transformation.requirements.ReqToConTransformations;
import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
        
import org.apache.log4j.Logger;

/**
 *
 * @author PST LMU
 */
public class ContentModelModification extends DefaultDiagramAction
{
	private static final long serialVersionUID = 498064232265236661L;
	public static final String sACTION_ADD_ATTRIBUTES="Create Attributes";
    public static final String sACTION_ADD_ASSOCIATIONS="Create Associations";
    public static final String sACTION_ADD_ATTRIBUTES_AND_ASSOCIATIONS="Create Attributes and Associations";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public ContentModelModification(String name, KeyStroke keyStroke, String group)
    {
            super(name,name,keyStroke,group);
    }

    /** Is called when an event occured. Adds attributes or associations to a content class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (this.getName().equals(sACTION_ADD_ATTRIBUTES))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                int iAttributes=ReqToConTransformations.replaceAttributesToContentClass(((Class)(acTempElements.get(e).getElement())),true);
                
                try
                {
                    SessionManager.getInstance().createSession("Reshape Content Class");
                    PresentationElementsManager.getInstance().reshapeShapeElement((ShapeElement)(acTempElements.get(e)),
                        new Rectangle(acTempElements.get(e).getBounds().x,acTempElements.get(e).getBounds().y,0,0));
                    SessionManager.getInstance().closeSession();
                }
                catch (Exception E)
                {
                    
                }
                
                MessageWriter.log("Added "+iAttributes+" attributes to content class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
            }

        }
        else if (this.getName().equals(sACTION_ADD_ASSOCIATIONS))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                ReqToConTransformations.clearAssociations((Class)(acTempElements.get(e).getElement()));
                int iAssociations=ReqToConTransformations.addAssociationsToContentClass(((Class)(acTempElements.get(e).getElement())),acTempElements.get(e));
                
                MessageWriter.log("Added "+iAssociations+" associations to content class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
            }
        }
        else if (this.getName().equals(sACTION_ADD_ATTRIBUTES_AND_ASSOCIATIONS))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                ReqToConTransformations.clearAssociations((Class)(acTempElements.get(e).getElement()));
                int iAttributes=ReqToConTransformations.replaceAttributesToContentClass(((Class)(acTempElements.get(e).getElement())),true);
                int iAssociations=ReqToConTransformations.addAssociationsToContentClass(((Class)(acTempElements.get(e).getElement())),acTempElements.get(e));
                
                try
                {
                    SessionManager.getInstance().createSession("Reshape content Class");
                    PresentationElementsManager.getInstance().reshapeShapeElement((ShapeElement)(acTempElements.get(e)),
                        new Rectangle(acTempElements.get(e).getBounds().x,acTempElements.get(e).getBounds().y,0,0));
                    SessionManager.getInstance().closeSession();
                }
                catch (Exception E)
                {
                    
                }
                
                MessageWriter.log("Added "+iAttributes+" attributes and "+iAssociations+" associations to Content class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
            }
        }
    }
}
