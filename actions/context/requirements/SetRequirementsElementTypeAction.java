package magicUWE.actions.context.requirements;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.KeyStroke;

import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;
import magicUWE.stereotypes.requirements.types.*;

/**
 * Copies stereotypes and tags from properties to classes and the other way around
 * (Activated on properties)
 * 
 * @author PST LMU
 */
public class SetRequirementsElementTypeAction extends DefaultDiagramAction
{
	private static final long serialVersionUID = 4116603470727848955L;
	private static final Logger logger = Logger.getLogger(SetRequirementsElementTypeAction.class);

    public SetRequirementsElementTypeAction(String name, KeyStroke keyStroke, String group)
    {
        super(name, name, keyStroke, group);
    }

    /**
     * copy uwe property tags to class
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        List<PresentationElement> selectedElements = Application.getInstance().getProject().getActiveDiagram().getSelected();

        for (PresentationElement presentationElement : selectedElements)
        {
            Element element = presentationElement.getElement();

            if ((element instanceof Action)||(element instanceof Pin))
            {
                RequirementsTypeDialog dialog = new RequirementsTypeDialog(element);
                if (dialog.getSelection()!=null)
                {
                    setTag(element,dialog.getSelection());
                    MessageWriter.log("Type changed", logger);
                }
            }
        }

    }

    public void setTag(Element element, String sValue)
    {
        if (sValue.equals(RequirementsTypeDialog.sREMOVE))
        {
            SessionManager.getInstance().createSession("Deleting type");
            if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString())))
            {
                if (StereotypesHelper.getStereotypePropertyValue(element,
                        UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), UWEDisplayActionType.sTagName).size()==1)
                {
                     Slot slot=StereotypesHelper.getSlot(element,StereotypesHelper.getPropertyByName(
                             StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                             UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),
                             UWEDisplayActionType.sTagName), false, false);
                    if (slot != null)
                    {
                        for (int i = slot.getValue().size() - 1; i >= 0; i--)
                        {
                                slot.getValue().remove(i);
                        }
                    }
                }
            }
            else if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString())))
            {
                if (StereotypesHelper.getStereotypePropertyValue(element,
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), UWENavigationActionType.sTagName).size()==1)
                {
                     Slot slot=StereotypesHelper.getSlot(element,StereotypesHelper.getPropertyByName(
                             StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                             UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),
                             UWENavigationActionType.sTagName), false, false);
                    if (slot != null)
                    {
                        for (int i = slot.getValue().size() - 1; i >= 0; i--)
                        {
                                slot.getValue().remove(i);
                        }
                    }
                }
            }
            else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString())))
            {
                if (StereotypesHelper.getStereotypePropertyValue(element,
                        UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(), UWEDisplayPinType.sTagName).size()==1)
                {
                     Slot slot=StereotypesHelper.getSlot(element,StereotypesHelper.getPropertyByName(
                             StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                             UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString()),
                             UWEDisplayPinType.sTagName), false, false);
                    if (slot != null)
                    {
                        for (int i = slot.getValue().size() - 1; i >= 0; i--)
                        {
                                slot.getValue().remove(i);
                        }
                    }
                }
            }
            else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString())))
            {
                if (StereotypesHelper.getStereotypePropertyValue(element,
                        UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(), UWEInteractionPinType.sTagName).size()==1)
                {
                     Slot slot=StereotypesHelper.getSlot(element,StereotypesHelper.getPropertyByName(
                             StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                             UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString()),
                             UWEInteractionPinType.sTagName), false, false);
                    if (slot != null)
                    {
                        for (int i = slot.getValue().size() - 1; i >= 0; i--)
                        {
                                slot.getValue().remove(i);
                        }
                    }
                }
            }
            SessionManager.getInstance().closeSession();
        }
        else
        {
            SessionManager.getInstance().createSession("Setting type");
            if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString())))
            {
                StereotypesHelper.setStereotypePropertyValue(element,
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()),
                        UWEDisplayActionType.sTagName, sValue);
            }
            else if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString())))
            {
                StereotypesHelper.setStereotypePropertyValue(element,
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()),
                        UWENavigationActionType.sTagName, sValue);
            }
            else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString())))
            {
                StereotypesHelper.setStereotypePropertyValue(element,
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString()),
                        UWEDisplayPinType.sTagName, sValue);
            }
            else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString())))
            {
                StereotypesHelper.setStereotypePropertyValue(element,
                        StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                        UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString()),
                        UWEInteractionPinType.sTagName, sValue);
            }
            SessionManager.getInstance().closeSession();
        }
    }
}
