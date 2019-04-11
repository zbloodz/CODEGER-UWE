/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements.types;

/**
 *
 * @author PST LMU
 */
public enum UWEInteractionPinType
{
    TEXT("text"),
    IMAGE("image"),
    SELECTION("selection"),
    FILE("file"),
    CUSTOM("custom");

    public static final String sTagName="type";
    
    private final String name;

    private UWEInteractionPinType(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return(name);
    }
}
