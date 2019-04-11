/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements;

import javax.swing.KeyStroke;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeOfElement;

/**
 * The requirement pins stereotypes from UWE Profile
 *
 * @author PST LMU
 */
public enum UWEStereotypeRequirementsPins implements UWEStereotypeOfElement
{
    DISPLAY_PIN_INPUT("displayPin",true),
    INTERACTION_PIN_INPUT("interactionPin",true),
    DISPLAY_PIN_OUTPUT("displayPin",false),
    INTERACTION_PIN_OUTPUT("interactionPin",false);

    private final static String sINPUT=" (Input)";
    private final static String sOUTPUT=" (Output)";

    private final String displayName;
    private final String name;
    private KeyStroke key;

    private UWEStereotypeRequirementsPins(String name)
    {
        this.name = name;
        this.displayName = name;
    }

    private UWEStereotypeRequirementsPins(String name,boolean bInput)
    {
        this.name = name;
        if (bInput)
        {
            this.displayName = name+sINPUT;
        }
        else
        {
            this.displayName = name+sOUTPUT;
        }
    }

    private UWEStereotypeRequirementsPins(String name, Integer key)
    {
        this.name = name;
        this.displayName = name;
        setKeyStroke(key);
    }

    private UWEStereotypeRequirementsPins(String name, String displayName, Integer key)
    {
        this.name = name;
        this.displayName = displayName;
        setKeyStroke(key);
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * displayName shown in the toolbar. If not set in constructor, its equal to
     * toString().
     */
    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getDisplayName(int setOfNames)
    {
        // only one possibility to show these stereotypes e.g in the toolbar
        return getDisplayName();
    }

    @Override
    public void setKeyStroke(Integer key)
    {
        if (key == null)
        {
            this.key = null;
        }
        else
        {
            this.key = KeyStroke.getKeyStroke(key, keyModifier);
        }
    }

    @Override
    public KeyStroke getKeyStroke()
    {
        return key;
    }

    @Override
    public Element createElementWithStereotype()
    {
        if (displayName.endsWith(sINPUT))
        {
            return MagicDrawElementOperations.createInputPin(this.toString());
        }
        else
        {
            return MagicDrawElementOperations.createOutputPin(this.toString());
        }
    }

    @Override
    public UWEDiagramType[] getAssociatedDiagramTypes()
    {
        UWEDiagramType[] dgType = { UWEDiagramType.USE_CASE_FLOW };
        return dgType;
    }
}
