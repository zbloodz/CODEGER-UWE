package magicUWE.actions.context;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Displays an InputBox and add a DeleteButton. The result is stored in the
 * field "result".
 * 
 * @author PST LMU
 */
class InputBoxWithDeleteButton extends JDialog implements ActionListener {
	public String result;
	/**
	 * true <=> Delete (result is null, too) <br/>
	 * false <=> Cancel (result is null, too)
	 */
	public boolean isDeletionNotCancle = false;

	private static final long serialVersionUID = 1L;
	private final JTextField inputField;
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final JButton deleteButton = new JButton("Delete tagged value");

	public InputBoxWithDeleteButton(String message, String defaultValue) {
		this.setLayout(new BorderLayout());
		this.setTitle("Set tag");
		this.getRootPane().setDefaultButton(okButton);

		JLabel messageBox = new JLabel(message);
		inputField = new JTextField(defaultValue);
		messageBox.setBorder(BorderFactory.createEmptyBorder(10, 7, 5, 0));

		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		deleteButton.setMnemonic(KeyEvent.VK_D);

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		deleteButton.addActionListener(this);

		JComponent newContentPane = new JPanel(new FlowLayout());
		newContentPane.add(okButton);
		newContentPane.add(deleteButton);
		newContentPane.add(cancelButton);

		this.add(inputField, BorderLayout.CENTER);
		this.add(messageBox, BorderLayout.NORTH);
		this.add(newContentPane, BorderLayout.SOUTH);
		this.add(new JLabel("  "), BorderLayout.WEST);
		this.add(new JLabel("  "), BorderLayout.EAST);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - this.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
		
		this.pack();

		this.setVisible(true);
	}

	/**
	 * Buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			result = inputField.getText();
		} else if (e.getSource() == deleteButton) {
			result = null;
			isDeletionNotCancle = true;
		} else {
			result = null;
			isDeletionNotCancle = false;
		}
		this.dispose();
	}
}
