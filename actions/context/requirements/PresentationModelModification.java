/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.util.ArrayList;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.transformation.requirements.ReqToPreTransformations;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

/**
 *
 * @author PST LMU
 */
public class PresentationModelModification extends DefaultDiagramAction
{
	private static final long serialVersionUID = 8940680695279886295L;
	public static final String sACTION_ADD_PROPERTIES="Generate Inner Elements";
    public static final String sACTION_ADD_PROPERTIES_TOPLEVEL="Generate Toplevel Inner Elements";

    /** Constructs a diagram context action
     *
     * @param name action name
     * @param keyStroke shortkey
     * @param group action group
     */
    public PresentationModelModification(String name, KeyStroke keyStroke, String group)
    {
            super(name,name,keyStroke,group);
    }

    /** Is called when an event occured. Adds child presentation elements to presentation class.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (this.getName().equals(sACTION_ADD_PROPERTIES_TOPLEVEL))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class,Property.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                if ((acTempElements.get(e).getElement() instanceof Class)||
                    (acTempElements.get(e).getElement() instanceof Property))
                {
                    ReqToPreTransformations.clearElement(acTempElements.get(e).getElement());
                    int iChildren=ReqToPreTransformations.addPresentationChildren(acTempElements.get(e).getElement(),true, null);
                    MessageWriter.log("Added "+iChildren+" child elements to presentation class \""+
                        ((NamedElement)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
                }
            }
        }
        else if (this.getName().equals(sACTION_ADD_PROPERTIES))
        {
            ArrayList<PresentationElement> acTempElements=ElementCollector.getSelectedPresentationElements(Class.class,Property.class);
            for (int e=0;e<acTempElements.size();e++)
            {
                if ((acTempElements.get(e).getElement() instanceof Class)||
                    (acTempElements.get(e).getElement() instanceof Property))
                {
                    ReqToPreTransformations.clearElement(acTempElements.get(e).getElement());
                    int iChildren=ReqToPreTransformations.addPresentationChildren(acTempElements.get(e).getElement(),false, null);
                    MessageWriter.log("Added "+iChildren+" child elements to presentation class \""+
                        ((NamedElement)(acTempElements.get(e).getElement())).getName()+"\"",
                        Logger.getLogger(ContentModelModification.class));
                }
            }
        }
    }
}
