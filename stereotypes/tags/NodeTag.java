/**
 * 
 */
package magicUWE.stereotypes.tags;

/**
 * 
 * @author PST LMU
 * 
 */
public interface NodeTag {

	/**
	 * @return String representation of the stereotype associated with this
	 *         tagged value. MUST be equal for all tags in one enum!
	 */
	public String getAssociatedStereotypeName();

	/**
	 * @return true <=> value of tag has the type of a boolean
	 */
	public boolean isBoolean();

	/**
	 * set Option for RIA Features, only important for tags of the Presentation
	 * Model
	 * 
	 * @param option
	 */
	public void setRIAOption(String option);

	/**
	 * @return riaOption
	 */
	public String getRIAOption();

	@Override
	public String toString();

}
