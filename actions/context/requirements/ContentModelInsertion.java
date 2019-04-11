/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ReqToConTransformations;
import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.shared.MessageWriter;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
        
import org.apache.log4j.Logger;

/**
 *
 * @author PST LMU
 */
public class ContentModelInsertion extends DefaultDiagramAction
{
	private static final long serialVersionUID = -3613269033225116818L;
	public static final String sACTION_TITLE="Insert Class";
    
    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public ContentModelInsertion(String name, KeyStroke keyStroke, String group)
    {
        super(name,name,keyStroke,group);
        
    }

    /** Is called when an event occured. Creates a content class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        TransformationsListener.getListener().setStarted(true);
        if (this.getName().equals(sACTION_TITLE))
        {
            ContentClassInsertionDialog dialog=new ContentClassInsertionDialog();
            if (dialog.getClassName()!=null)
            {
                ElementCollector.ReturnElement cCreatedElement=ReqToConTransformations.createContentClass(dialog.getClassName(),
                        TransformationsListener.getListener().getPosition(),((Package)(dialog.getPackage())));

                int iAttributes=0;
                int iAssociations=0;
                
                if (dialog.getAttributesCheck())
                {
                    iAttributes=ReqToConTransformations.addAttributesToContentClass(cCreatedElement.cClass, dialog.getAssociationsCheck());
                }

                if (dialog.getAssociationsCheck())
                {
                    iAssociations=ReqToConTransformations.addAssociationsToContentClass(cCreatedElement.cClass,cCreatedElement.cShape);
                }
                
                MessageWriter.log("Content class \""+cCreatedElement.cClass.getName()+"\" including "+
                        iAttributes+" attributes and "+iAssociations+" associations created",
                        Logger.getLogger(ContentModelInsertion.class));
            }
        }
        TransformationsListener.getListener().setStarted(false);
    }

}
