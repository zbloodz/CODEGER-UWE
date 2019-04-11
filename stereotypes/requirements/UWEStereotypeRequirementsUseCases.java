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
 * The requirement use cases stereotypes from UWE Profile
 *
 * @author PST LMU
 */
public enum UWEStereotypeRequirementsUseCases implements UWEStereotypeOfElement
{
    WEBUSECASE_USECASE("webUseCase",true),
    BROWSING_USECASE("browsing",true),
    PROCESSING_USECASE("processing",true),
    WEBUSECASE_PACKAGE("webUseCase",false),
    BROWSING_PACKAGE("browsing",false),
    PROCESSING_PACKAGE("processing",false);

    private final static String sUSECASE=" (Use Case)";
    private final static String sPACKAGE=" (Package)";

    private final String displayName;
    private final String name;
    private KeyStroke key;

    private UWEStereotypeRequirementsUseCases(String name)
    {
        this.name = name;
        this.displayName = name;
    }

    private UWEStereotypeRequirementsUseCases(String name,boolean bUseCase)
    {
        this.name = name;
        if (bUseCase)
        {
            this.displayName = name+sUSECASE;
        }
        else
        {
            this.displayName = name+sPACKAGE;
        }
    }

    private UWEStereotypeRequirementsUseCases(String name, Integer key)
    {
        this.name = name;
        this.displayName = name;
        setKeyStroke(key);
    }

    private UWEStereotypeRequirementsUseCases(String name, String displayName, Integer key)
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
        if (displayName.endsWith(sPACKAGE))
        {
            return MagicDrawElementOperations.createPackage(this.toString());
        }
        else
        {
            return MagicDrawElementOperations.createUseCase(this.toString());
        }
    }

    @Override
    public UWEDiagramType[] getAssociatedDiagramTypes()
    {
        UWEDiagramType[] dgType = { UWEDiagramType.USE_CASE };
        return dgType;
    }
}
