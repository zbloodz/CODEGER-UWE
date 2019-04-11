package magicUWE.riaPatterns;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import magicUWE.actions.context.DiagramContextBooleanTagAction;
import magicUWE.core.PluginManager;
import magicUWE.settings.GlobalConstants;
import magicUWE.stereotypes.tags.NodeTag;
import magicUWE.stereotypes.tags.UWETagTextInput;
import magicUWE.stereotypes.tags.UWETagInputElement;
import magicUWE.stereotypes.tags.UWETagInteractiveElement;
import magicUWE.stereotypes.tags.UWETagIteratedPresentationGroup;
import magicUWE.stereotypes.tags.UWETagOutputElement;
import magicUWE.stereotypes.tags.UWETagPresentationElement;
import magicUWE.stereotypes.tags.UWETagPresentationGroup;
import magicUWE.stereotypes.tags.UWETagPresentationPage;
import magicUWE.stereotypes.tags.UWETagSelection;
import magicUWE.stereotypes.tags.UWETagUIElement;
import magicUWE.stereotypes.tags.UWETagValuedElement;

/**
 * Class with methods for work with the RIA Tags
 * 
 * 
 */
public abstract class RIATagsHelper {

	private static LinkedList<NodeTag> riaTags;

	public static LinkedList<DiagramContextBooleanTagAction> riaTagActions =
			new LinkedList<DiagramContextBooleanTagAction>();

	/**
	 * Returns a list of the RIA Tags, i.e. all boolean tags used in the
	 * Presentation-Model
	 * 
	 * @return riaTags
	 */
	public static LinkedList<NodeTag> getRIATags() {
		if (riaTags == null) {
			riaTags = new LinkedList<NodeTag>();

			for (UWETagInputElement tag : UWETagInputElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagInteractiveElement tag : UWETagInteractiveElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagIteratedPresentationGroup tag : UWETagIteratedPresentationGroup.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagOutputElement tag : UWETagOutputElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagPresentationElement tag : UWETagPresentationElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagPresentationGroup tag : UWETagPresentationGroup.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagPresentationPage tag : UWETagPresentationPage.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagSelection tag : UWETagSelection.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagTextInput tag : UWETagTextInput.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagUIElement tag : UWETagUIElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
			for (UWETagValuedElement tag : UWETagValuedElement.values()) {
				if (tag.isBoolean()) {
					riaTags.add(tag);
				}
			}
		}
		return riaTags;

	}

	/**
	 * Sorts the RIA Tags
	 * 
	 * @return list of sorted RIA Tags
	 */
	public static LinkedList<NodeTag> getSortedRIATags() {

		LinkedList<NodeTag> sortedTags = getRIATags();

		Collections.sort(sortedTags, new Comparator<NodeTag>() {
			@Override
			public int compare(NodeTag a, NodeTag b) {
				String aName = a.toString();
				String bName = b.toString();
				return aName.compareToIgnoreCase(bName);
			}
		});

		return sortedTags;
	}

	/**
	 * Sets the RIA Option of a tag given by its name to riaOption
	 * 
	 * @param tagName
	 * @param riaOption
	 */
	public static void setRIAOptionFor(String tagName, String riaOption) {
		riaTags = getRIATags();
		for (NodeTag tag : riaTags) {
			if (tag.toString().equals(tagName)) {
				tag.setRIAOption(riaOption);
			}
		}
	}

	/**
	 * Get the name of icon for given RIA Option
	 * 
	 * @param riaOption
	 * @return icon name (without file extension)
	 */
	public static String getIconName(String riaOption) {
		if (riaOption != null) {
			if (riaOption.equals(GlobalConstants.RIA_OPTION_TAG_ONLY)) {
				return "tagonly";
			} else if (riaOption.equals(GlobalConstants.RIA_OPTION_DEPENDENCIES)) {
				return "dependencies";
			} else if (riaOption.equals(GlobalConstants.RIA_OPTION_BEHAVIOUR)) {
				return "behaviour";
			} else if (riaOption.equals(GlobalConstants.RIA_OPTION_ASK_EVERYTIME)) {
				return "ask";
			} else {
				return "no icon";
			}
		}
		return "no icon";
	}

	/**
	 * Get RIA Icon for given RIA Option
	 * 
	 * @param riaOption
	 * @return RIA Icon
	 */
	public static ImageIcon getRIAIcon(String riaOption) {
		String iconName = getIconName(riaOption);
		String path = "icons/ria/" + iconName + ".png";
		URL imgURL = PluginManager.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}
		System.err.println("Couldn't find file: " + path);
		return null;
	}
}
