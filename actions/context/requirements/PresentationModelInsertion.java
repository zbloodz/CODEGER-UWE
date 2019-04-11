/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ReqToPreTransformations;
import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
/**
 *
 * @author PST LMU
 */
public class PresentationModelInsertion extends DefaultDiagramAction
{
	private static final long serialVersionUID = -2994962385716526662L;
	public static final String sACTION_GROUP="Insert Presentation Group";
    public static final String sACTION_FORM="Insert Input Form";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public PresentationModelInsertion(String name, KeyStroke keyStroke, String group)
    {
        super(name,name,keyStroke,group);

    }

    /** Is called when an event occured. Creates a presentation class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        TransformationsListener.getListener().setStarted(true);
        if (this.getName().equals(sACTION_GROUP))
        {
            PresentationClassInsertionDialog dialog=new PresentationClassInsertionDialog(true);
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cCreatedElement=ReqToPreTransformations.createPresentationClass(dialog.getClassName(),
                        TransformationsListener.getListener().getPosition(),((Package)(dialog.getPackage())),true);

                int iChildren=0;
                if (dialog.getSettings()==PresentationClassInsertionDialog.iCREATE_TOPLEVEL)
                {
                    iChildren=ReqToPreTransformations.addPresentationChildren(cCreatedElement.cClass, true, null);
                }
                else if (dialog.getSettings()==PresentationClassInsertionDialog.iCREATE_ALL)
                {
                    iChildren=ReqToPreTransformations.addPresentationChildren(cCreatedElement.cClass, false, null);
                }
                MessageWriter.log("Presentation group \""+cCreatedElement.cClass.getName()+"\" including "+iChildren+" child elements created",
                    Logger.getLogger(ContentModelInsertion.class));
            }
        }
        else if (this.getName().equals(sACTION_FORM))
        {
            PresentationClassInsertionDialog dialog=new PresentationClassInsertionDialog(false);
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cCreatedElement=ReqToPreTransformations.createPresentationClass(dialog.getClassName(),
                        TransformationsListener.getListener().getPosition(),((Package)(dialog.getPackage())),false);

                int iChildren=0;
                if (dialog.getSettings()==PresentationClassInsertionDialog.iCREATE_TOPLEVEL)
                {
                    iChildren=ReqToPreTransformations.addPresentationChildren(cCreatedElement.cClass, true, null);
                }
                else if (dialog.getSettings()==PresentationClassInsertionDialog.iCREATE_ALL)
                {
                    iChildren=ReqToPreTransformations.addPresentationChildren(cCreatedElement.cClass, false, null);
                }
                MessageWriter.log("Input form \""+cCreatedElement.cClass.getName()+"\" including "+iChildren+" child elements created",
                    Logger.getLogger(ContentModelInsertion.class));
            }

        }
        TransformationsListener.getListener().setStarted(false);
    }

}
