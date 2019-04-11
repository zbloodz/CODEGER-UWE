/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ReqToNavTransformations;
import magicUWE.transformation.requirements.ReqToProTransformations;
import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;

/**
 *
 * @author PST LMU
 */
public class NavigationModelInsertion extends DefaultDiagramAction
{
	private static final long serialVersionUID = 6920599870439500504L;
	public static final String sACTION_NAVIGATION="Insert Navigation Class";
    public static final String sACTION_PROCESS="Insert Process Class";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public NavigationModelInsertion(String name, KeyStroke keyStroke, String group)
    {
        super(name,name,keyStroke,group);

    }

    /** Is called when an event occured. Creates a navigation or process class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        TransformationsListener.getListener().setStarted(true);
        if (this.getName().equals(sACTION_NAVIGATION))
        {
            NavigationClassInsertionDialog dialog=new NavigationClassInsertionDialog();
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cReturn=ReqToNavTransformations.createNavigationClass(dialog.getClassName().replaceAll(" ",""),
                        TransformationsListener.getListener().getPosition(),
                        dialog.getPackage());
                
                ElementCollector.ReturnElement cMenu=null;
                if (dialog.getSettings()!=NavigationClassInsertionDialog.iCREATE_CLASS_ONLY)
                {
                    cMenu=ReqToNavTransformations.addNavigationClassAssociations(cReturn.cClass, cReturn.cShape, 
                            dialog.getSettings()==NavigationClassInsertionDialog.iCREATE_LINKS_AND_MENU);
                }
                
                if (cMenu==null)
                {
                    MessageWriter.log("Navigation class \""+cReturn.cClass.getName()+"\" created",
                        Logger.getLogger(NavigationModelInsertion.class));
                }
                else if (cMenu.cClass==null)
                {
                    MessageWriter.log("Navigation class \""+cReturn.cClass.getName()+"\" including a menu and "+cMenu.iChildren+" associations created",
                        Logger.getLogger(ContentModelInsertion.class));
                }
                else
                {
                    MessageWriter.log("Navigation class \""+cReturn.cClass.getName()+"\" including "+cMenu.iChildren+" associations created",
                        Logger.getLogger(ContentModelInsertion.class));
                }
            }
        }
        else if (this.getName().equals(sACTION_PROCESS))
        {
            ProcessClassInsertionDialog dialog=new ProcessClassInsertionDialog(true);
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cReturn=ReqToProTransformations.createProcessClass(dialog.getClassName().replaceAll(" ",""),
                        TransformationsListener.getListener().getPosition(),
                        dialog.getPackage());
                
                ElementCollector.ReturnElement cAdd=null;
                if (dialog.getSelection())
                {
                    cAdd=ReqToNavTransformations.addProcessClassAssociations(cReturn.cClass, cReturn.cShape);
                }
                MessageWriter.log("Process class \""+cReturn.cClass.getName()+"\" including "+cAdd.iChildren+" associations created",
                    Logger.getLogger(ContentModelInsertion.class));
            }

        }
        TransformationsListener.getListener().setStarted(false);
    }

}
