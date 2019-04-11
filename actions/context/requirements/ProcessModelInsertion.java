/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ReqToProTransformations;
import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
        
import org.apache.log4j.Logger;

/**
 *
 * @author PST LMU
 */
public class ProcessModelInsertion extends DefaultDiagramAction
{
	private static final long serialVersionUID = -6491861321664871913L;
	public static final String sACTION_TITLE="Insert Process Class";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public ProcessModelInsertion(String name, KeyStroke keyStroke, String group)
    {
        super(name,name,keyStroke,group);

    }

     /** Is called when an event occured. Creates a process class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        TransformationsListener.getListener().setStarted(true);
        if (this.getName().equals(sACTION_TITLE))
        {
            ProcessClassInsertionDialog dialog=new ProcessClassInsertionDialog(false);
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cReturn=ReqToProTransformations.createProcessClass(dialog.getClassName().replaceAll(" ",""),
                        TransformationsListener.getListener().getPosition(),
                        dialog.getPackage());
                
                boolean createdWorkflow=false;
                if (dialog.getSelection())
                {
                    createdWorkflow=ReqToProTransformations.addWorkflow(cReturn.cClass);
                }
                
                if (createdWorkflow)
                {
                    MessageWriter.log("Content class \""+cReturn.cClass.getName()+"\" including a workflow created",
                        Logger.getLogger(ContentModelInsertion.class));
                }
                else
                {
                    MessageWriter.log("Content class \""+cReturn.cClass.getName()+"\" created",
                        Logger.getLogger(ContentModelInsertion.class));
                }
            }

        }
        TransformationsListener.getListener().setStarted(false);
    }

}
