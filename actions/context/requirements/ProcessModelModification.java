/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.transformation.requirements.ReqToProTransformations;
import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
        
import org.apache.log4j.Logger;
/**
 *
 * @author PST LMU
 */
public class ProcessModelModification extends DefaultDiagramAction
{
	private static final long serialVersionUID = 1025949682514302762L;
	public static final String sACTION_ADD_WORKFLOW="Create Workflow";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public ProcessModelModification(String name, KeyStroke keyStroke, String group)
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
        if (this.getName().equals(sACTION_ADD_WORKFLOW))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                if (ReqToProTransformations.addWorkflow(((Class)(acTempElements.get(e).getElement()))))
                {
                    MessageWriter.log("Added a workflow to process class \""+
                        ((Class)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
                }
            } 
        }
    }
}
