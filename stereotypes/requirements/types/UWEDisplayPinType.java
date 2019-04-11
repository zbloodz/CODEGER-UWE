/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements.types;

/**
 *
 * @author PST LMU
 */
public enum UWEDisplayPinType
{
    TEXT("text"),
    IMAGE("image"),
    MEDIA("media");

    public static final String sTagName="type";
    
    private final String name;

    private UWEDisplayPinType(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return(name);
    }
}
