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
 * The requirement actions stereotypes from UWE Profile
 *
 * @author PST LMU
 */
public enum UWEStereotypeRequirementsActions implements UWEStereotypeOfElement
{
    DISPLAY_ACTION("displayAction",false),
    NAVIGATION_ACTION("navigationAction",false),
    DISPLAY_ACTION_STRUCTURED("displayAction",true);

    private final static String sCALLACTION="";
    private final static String sSTRUCTURED=" (Structured)";
    
    private final String displayName;
    private final String name;
    private KeyStroke key;

    private UWEStereotypeRequirementsActions(String name)
    {
        this.name = name;
        this.displayName = name;
    }

    private UWEStereotypeRequirementsActions(String name,boolean bStructured)
    {
        this.name = name;
        if (bStructured)
        {
            this.displayName = name+sSTRUCTURED;
        }
        else
        {
            this.displayName = name+sCALLACTION;
        }
    }


    private UWEStereotypeRequirementsActions(String name, Integer key)
    {
        this.name = name;
        this.displayName = name;
        setKeyStroke(key);
    }

    private UWEStereotypeRequirementsActions(String name, String displayName, Integer key)
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
        if (displayName.endsWith(sSTRUCTURED))
        {
            return MagicDrawElementOperations.createStructuredAction(this.toString());
        }
        else
        {
            return MagicDrawElementOperations.createAction(this.toString());
        }
    }
    
    @Override
    public UWEDiagramType[] getAssociatedDiagramTypes()
    {
        UWEDiagramType[] dgType = { UWEDiagramType.USE_CASE_FLOW };
        return dgType;
    }
}
