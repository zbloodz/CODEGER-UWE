package magicUWE.riaPatterns;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import magicUWE.core.PluginManager;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.ui.DialogConstants;

/**
 * Dialog for choosing action in case of adding dependencies when behavior
 * already exists.
 * 
 * 
 */
public class DependenciesReplaceDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(DependenciesReplaceDialog.class);

	// Return value
	private ButtonModel selection;

	// GUI
	private static boolean useSystemLookAndFeel = false;
	private static final String okButtonName = "OK";
	private static final String cancelButtonName = "Cancel";
	public static final String replaceDepNotSMName = "Replace existing dependencies, no changes on state machine";
	public static final String replaceDepRemoveSMName = "Replace existing dependencies, remove existing state machine";
	public static final String removeSMNotDepName = "Remove existing state machine, no changes on dependencies";
	private JButton okButton;
	private JButton cancelButton;

	private ButtonGroup radioGroup;

	private int result;

	/**
	 * Opens {@link DependenciesReplaceDialog}
	 * 
	 * @param tagName
	 */
	public DependenciesReplaceDialog(String tagName) {
		if (useSystemLookAndFeel) { // set to false, otherwise some problems
			// with other java applications running parallel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Couldn't use system look and feel.");
			}
		}
		this.setModal(true);
		this.setTitle("Dependencies for " + tagName);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.setAlignmentX(LEFT_ALIGNMENT);

		radioGroup = new ButtonGroup();

		JRadioButton replaceDepNotSM = new JRadioButton(replaceDepNotSMName, true);
		replaceDepNotSM.setActionCommand(replaceDepNotSMName);
		radioPanel.add(replaceDepNotSM);
		radioGroup.add(replaceDepNotSM);

		JRadioButton replaceDepRemoveSM = new JRadioButton(replaceDepRemoveSMName);
		replaceDepRemoveSM.setActionCommand(replaceDepRemoveSMName);
		radioPanel.add(replaceDepRemoveSM);
		radioGroup.add(replaceDepRemoveSM);

		JRadioButton removeSMNotDep = new JRadioButton(removeSMNotDepName);
		removeSMNotDep.setActionCommand(removeSMNotDepName);
		radioPanel.add(removeSMNotDep);
		radioGroup.add(removeSMNotDep);

		JLabel label1 = new JLabel("Behaviour for RIA Pattern '" + tagName + "' already exists.");
		JLabel label2 = new JLabel("What do you want to do?");
		JPanel text = new JPanel(new GridLayout(0, 1));
		text.add(label1);
		text.add(label2);
		text.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(text, BorderLayout.NORTH);
		centerPanel.add(radioPanel, BorderLayout.CENTER);
		borderPanel.add(centerPanel, BorderLayout.CENTER);

		Icon icon = new ImageIcon(PluginManager.class.getResource("icons/ria/question.png"));
		borderPanel.add(new JLabel(icon), BorderLayout.WEST);

		this.getContentPane().add(new JLabel("  "), BorderLayout.NORTH);
		this.getContentPane().add(borderPanel, BorderLayout.CENTER);
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
			this.result = DialogConstants.OK;
			selection = radioGroup.getSelection();
			if (selection != null) {
				this.dispose();
			}
		} else if (event.getActionCommand().equals(cancelButtonName)) {
			this.result = DialogConstants.CANCEL;
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

	/**
	 * @return result DialogConstants.OK or DialogConstants.CANCEL
	 */
	public int getResult() {
		return result;
	}

}
