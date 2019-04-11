package magicUWE.riaPatterns;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import magicUWE.actions.context.DiagramContextBooleanTagAction;
import magicUWE.core.PluginManager;
import magicUWE.riaPatterns.RIATagsHelper;
import magicUWE.settings.GlobalConstants;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

/**
 * Dialog for the setting of tags and choosing RIA Features for a tag Opened by
 * {@link DiagramContextBooleanTagAction}
 * 
 * 
 * 
 */
public class RIAPatternsTagDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RIAPatternsTagDialog.class);

	// Return value
	private ButtonModel selection;

	// GUI
	private static boolean useSystemLookAndFeel = false;
	private static final String okButtonName = "OK";
	private static final String cancelButtonName = "Cancel";
	public static final String deleteTagButtonName = "delete tag";

	private JButton okButton;
	private JButton cancelButton;
	private JRadioButton tagOnly;
	private JRadioButton behaviour;
	private JRadioButton dependencies;
	private JRadioButton deleteTag;
	private ButtonGroup radioGroup;

	/**
	 * Constructor of RIAPatternsTagDialog, opens the Dialog
	 * 
	 * @param name
	 *            title of Dialog
	 * @param valueOfTag
	 *            boolean value to which the tag is set
	 * @param dependenciesExisting
	 * @param behaviourExisting
	 */
	public RIAPatternsTagDialog(String name, boolean valueOfTag, boolean dependenciesExisting, boolean behaviourExisting) {

		if (useSystemLookAndFeel) { // set to false, otherwise some problems
			// with other java applications running parallel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Couldn't use system look and feel.");
			}
		}
		this.setModal(true);
		this.setTitle(name);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel radioPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		radioPanel.setAlignmentX(LEFT_ALIGNMENT);

		tagOnly = new JRadioButton();
		tagOnly.setActionCommand(GlobalConstants.RIA_OPTION_TAG_ONLY);
		JPanel tagOnlyPanel = new JPanel(new FlowLayout());
		JLabel tagOnlyLabel =
				new JLabel(GlobalConstants.RIA_OPTION_TAG_ONLY,
						RIATagsHelper.getRIAIcon(GlobalConstants.RIA_OPTION_TAG_ONLY), SwingConstants.LEFT);
		tagOnlyPanel.add(tagOnly);
		tagOnlyPanel.add(tagOnlyLabel);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		radioPanel.add(tagOnlyPanel, c);

		dependencies = new JRadioButton();
		dependencies.setActionCommand(GlobalConstants.RIA_OPTION_DEPENDENCIES);
		dependencies.setAlignmentX(LEFT_ALIGNMENT);
		JPanel dependenciesPanel = new JPanel(new FlowLayout());
		dependenciesPanel.setAlignmentX(LEFT_ALIGNMENT);
		JLabel dependenciesLabel =
				new JLabel(GlobalConstants.RIA_OPTION_DEPENDENCIES,
						RIATagsHelper.getRIAIcon(GlobalConstants.RIA_OPTION_DEPENDENCIES), SwingConstants.LEFT);
		dependenciesPanel.add(dependencies);
		dependenciesPanel.add(dependenciesLabel);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		radioPanel.add(dependenciesPanel, c);

		behaviour = new JRadioButton();
		behaviour.setActionCommand(GlobalConstants.RIA_OPTION_BEHAVIOUR);
		behaviour.setAlignmentX(LEFT_ALIGNMENT);
		JPanel behaviourPanel = new JPanel(new FlowLayout());
		behaviourPanel.setAlignmentX(LEFT_ALIGNMENT);
		JLabel behaviourLabel =
				new JLabel(GlobalConstants.RIA_OPTION_BEHAVIOUR,
						RIATagsHelper.getRIAIcon(GlobalConstants.RIA_OPTION_BEHAVIOUR), SwingConstants.LEFT);
		behaviourPanel.add(behaviour);
		behaviourPanel.add(behaviourLabel);
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		radioPanel.add(behaviourPanel, c);

		if (dependenciesExisting) {
			dependencies.setSelected(true);
		} else if (behaviourExisting) {
			behaviour.setSelected(true);
		} else {
			tagOnly.setSelected(true);
		}
		radioGroup = new ButtonGroup();
		radioGroup.add(tagOnly);
		radioGroup.add(dependencies);
		radioGroup.add(behaviour);

		deleteTag = new JRadioButton();
		deleteTag.setActionCommand(deleteTagButtonName);
		deleteTag.setAlignmentX(LEFT_ALIGNMENT);
		JPanel deletePanel = new JPanel(new FlowLayout());
		deletePanel.setAlignmentX(LEFT_ALIGNMENT);
		ImageIcon deleteIcon = new ImageIcon(PluginManager.class.getResource("icons/ria/delete.png"));
		JLabel deleteLabel = new JLabel(deleteTagButtonName, deleteIcon, SwingConstants.LEFT);
		deletePanel.add(deleteTag);
		deletePanel.add(deleteLabel);
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		if (!valueOfTag) {
			radioGroup.add(deleteTag);
			radioPanel.add(deletePanel, c);
		}

		JLabel label = new JLabel("Modelling RIA Features:");
		label.setBorder(BorderFactory.createEmptyBorder(10, 7, 5, 0));
		this.getContentPane().add(label, BorderLayout.NORTH);
		this.getContentPane().add(radioPanel, BorderLayout.CENTER);
		this.getContentPane().add(new JLabel("  "), BorderLayout.WEST);
		this.getContentPane().add(new JLabel("  "), BorderLayout.EAST);

		JPanel buttonPanel = new JPanel(new FlowLayout());

		okButton = new JButton(okButtonName);
		okButton.addActionListener(this);
		okButton.setActionCommand(okButtonName);
		cancelButton = new JButton(cancelButtonName);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(cancelButtonName);
		this.getRootPane().setDefaultButton(okButton);

		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setMnemonic(KeyEvent.VK_C);

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		this.pack();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - this.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);

		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(okButtonName)) {
			selection = radioGroup.getSelection();
			if (selection != null) {
				this.dispose();
			}
		} else if (event.getActionCommand().equals(cancelButtonName)) {
			this.dispose();
		} else {
			MessageWriter.showError("Unknown operation", logger);
		}

	}

	/**
	 * 
	 * @return selection of the radio buttons group of the dialog
	 */
	public String getSelection() {
		if (selection != null) {
			return selection.getActionCommand();
		}
		return "unknown selection";
	}

}