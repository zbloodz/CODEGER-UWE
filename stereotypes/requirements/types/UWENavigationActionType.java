/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements.types;

/**
 *
 * @author PST LMU
 */
public enum UWENavigationActionType
{
    ANCHOR("anchor"),
    TAB("tab"),
    BUTTON("button"),
    AUTOMATIC("automatic");

    public static final String sTagName="type";
    
    private final String name;

    private UWENavigationActionType(String name)
    {
        this.name=name;
    }

    public String getName()
    {
        return(name);
    }
}
