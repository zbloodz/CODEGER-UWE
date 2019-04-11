package magicUWE.actions.toolbar;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays a MessageBox and add a CheckBox asking if the user wants to see the
 * message again. CheckBox result is stored in the field "showAgain".
 * 
 * @author PST LMU
 */
class MessageWithDisplayAgainOption extends JDialog implements ActionListener {
	public boolean showAgain = true;

	private static final long serialVersionUID = 1L;
	private final JButton okButton = new JButton("OK");
	private final JCheckBox displayAgainCheckbox = new JCheckBox("Show this message again (within this session)");

	public MessageWithDisplayAgainOption(String message) {
		this.setLayout(new BorderLayout());
		this.setTitle("Warning");
		this.getRootPane().setDefaultButton(okButton);
		this.setResizable(false);

		JLabel messageBox = new JLabel(message);
		messageBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.addActionListener(this);

		GridBagConstraints ccc1 = new GridBagConstraints();
		ccc1.fill = GridBagConstraints.BOTH;
		ccc1.insets = new Insets(2, 70, 5, 70);
		ccc1.weighty = 1;
		ccc1.gridx = 0;
		ccc1.gridy = 2;
		ccc1.gridwidth = 3;
		JComponent newContentPane = new JPanel(new GridBagLayout());
		newContentPane.add(okButton, ccc1);

		displayAgainCheckbox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.add(newContentPane, BorderLayout.SOUTH);
		this.add(displayAgainCheckbox, BorderLayout.CENTER);
		this.add(messageBox, BorderLayout.NORTH);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.pack();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - this.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);

		this.setVisible(true);
	}

	/**
	 * OK-Button
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		showAgain = displayAgainCheckbox.isSelected();
		this.dispose();
	}
}
