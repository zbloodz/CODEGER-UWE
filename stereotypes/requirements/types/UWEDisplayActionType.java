/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements.types;

/**
 *
 * @author PST LMU
 */
public enum UWEDisplayActionType
{
    GROUP("group"),
    FORM("form"),
    ITERATION("iteration"),
    ALTERNATIVES("alternatives");
    
    public static final String sTagName="type";

    private final String name;

    private UWEDisplayActionType(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return(name);
    }
}
