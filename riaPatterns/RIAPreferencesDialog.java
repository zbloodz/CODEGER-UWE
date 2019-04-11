package magicUWE.riaPatterns;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import magicUWE.actions.context.DiagramContextBooleanTagAction;
import magicUWE.core.PluginManager;
import magicUWE.settings.GlobalConstants;
import magicUWE.settings.PropertyStorer;
import magicUWE.shared.MessageWriter;
import magicUWE.stereotypes.tags.NodeTag;

import org.apache.log4j.Logger;

/**
 * Dialog for the setting of the RIA Patterns Options Opened from the main menu
 * entry "RIA Patterns"
 * 
 * 
 */
public class RIAPreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RIAPreferencesDialog.class);

	// GUI
	private static boolean useSystemLookAndFeel = false;
	private static final String okButtonName = "OK";
	private static final String cancelButtonName = "Cancel";

	public static final String applyToAllButtonName = "apply to all";

	private JButton okButton;
	private JButton cancelButton;

	private JButton applyToAllButton;
	private JComboBox applyToAllCB;

	// table with RIATags and options
	private RIATable table;

	/**
	 * Constructor of RIAPreferencesDialog, opens the dialog
	 */
	public RIAPreferencesDialog() {

		if (useSystemLookAndFeel) { // set to false, otherwise some problems
			// with other java applications running parallel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Couldn't use system look and feel.");
			}
		}
		this.setModal(true);
		this.setTitle("RIA Patterns Options");
		Image img = new ImageIcon(PluginManager.class.getResource("icons/UWERiaPatterns.png")).getImage();
		this.setIconImage(img);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// label for headline
		JPanel labelPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Modelling RIA Features:");
		labelPanel.add(label, BorderLayout.WEST);

		// panel for apply to all button and combobox
		JPanel applyToAllPanel = new JPanel(new FlowLayout());

		applyToAllCB = new JComboBox(ComboBoxTableModel.getComboBoxStates());
		applyToAllCB.setRenderer(new ComboBoxRenderer());
		applyToAllButton = new JButton(applyToAllButtonName);
		applyToAllButton.addActionListener(this);
		applyToAllButton.setActionCommand(applyToAllButtonName);

		applyToAllPanel.add(applyToAllCB);
		applyToAllPanel.add(applyToAllButton);

		// RIA table
		table = new RIATable();
		table.setBorder(BorderFactory.createLineBorder(Color.gray));
		JPanel tablePanel = new JPanel();
		tablePanel.add(table);

		// panel for ok and cancel buttons
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

		// new content pane
		JPanel newContentPane = new JPanel();
		newContentPane.setOpaque(true);
		newContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.Y_AXIS));
		this.setContentPane(newContentPane);

		// add components to content pane
		newContentPane.add(labelPanel);
		newContentPane.add(tablePanel);
		newContentPane.add(applyToAllPanel);
		newContentPane.add(buttonPanel);

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
			setRIAOptions();
			this.dispose();

		} else if (event.getActionCommand().equals(cancelButtonName)) {
			// close the package chooser dialog
			this.dispose();
		} else if (event.getActionCommand().equals(applyToAllButtonName)) {
			applyToAll(applyToAllCB.getSelectedItem());
		}

		else {
			MessageWriter.showError("Unknown operation", logger);
		}

	}

	/**
	 * Sets the RIA Options of the RIA Tags to the options chosen in the dialog
	 */
	private void setRIAOptions() {
		ComboBoxTableModel model = (ComboBoxTableModel) table.getModel();
		Object[][] data = model.getTableData();
		for (int i = 0; i < data.length; i++) {
			String tagName = String.valueOf(data[i][0]);
			String option = String.valueOf(data[i][1]);
			RIATagsHelper.setRIAOptionFor(tagName, option);
			PropertyStorer.storeProperty("ria" + tagName, option);
		}
		for (DiagramContextBooleanTagAction action : RIATagsHelper.riaTagActions) {
			action.setIcon();
		}

	}

	/**
	 * Sets all values of the ComboBoxes in the table to value
	 * 
	 * @param value
	 */
	private void applyToAll(Object value) {
		ComboBoxTableModel model = (ComboBoxTableModel) table.getModel();
		Object[][] data = model.getTableData();
		for (int i = 0; i < data.length; i++) {
			model.setValueAt(value, i, 1);
		}

	}

}

/**
 * Table for displaying the RIA Tags and their RIA Options in the
 * {@link RIAPreferencesDialog}
 * 
 * 
 * 
 */
class RIATable extends JTable {

	private static final long serialVersionUID = 1L;

	public RIATable() {
		// Create the JTable
		super(new ComboBoxTableModel());
		this.setCellSelectionEnabled(false);
		// this.setBorder(BorderFactory.createLineBorder(Color.gray));

		// Create the combo box editor
		JComboBox comboBox = new JComboBox(ComboBoxTableModel.getComboBoxStates());
		comboBox.setEditable(false);
		// set combo box renderer for showing icon and text
		comboBox.setRenderer(new ComboBoxRenderer());
		DefaultCellEditor editor = new DefaultCellEditor(comboBox);

		// Assign the editor to the second column
		TableColumn comboBoxColumn = this.getColumnModel().getColumn(1);
		comboBoxColumn.setCellEditor(editor);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Choose RIA Option");
		comboBoxColumn.setCellRenderer(renderer);

		// set the renderer for the second column to show icons and text
		comboBoxColumn.setCellRenderer(new SecondColumnIconRenderer());

		// Set column widths
		this.getColumnModel().getColumn(0).setPreferredWidth(155);
		comboBoxColumn.setPreferredWidth(230);

		// Set row height
		this.setRowHeight(23);

		this.setPreferredScrollableViewportSize(this.getPreferredSize());
		this.setFillsViewportHeight(true);
		this.setIntercellSpacing(new Dimension(0, 5));
		this.getTableHeader().setResizingAllowed(false);
		this.getTableHeader().setReorderingAllowed(false);

	}

}

/**
 * TableModel that holds the data of the {@link RIATable}
 * 
 * 
 * 
 */
class ComboBoxTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	// column names
	private String[] columnNames = { "tagged value", "RIA Option" };

	// ComboBox states
	private static String[] comboBoxStates = { GlobalConstants.RIA_OPTION_TAG_ONLY,
			GlobalConstants.RIA_OPTION_DEPENDENCIES, GlobalConstants.RIA_OPTION_BEHAVIOUR,
			GlobalConstants.RIA_OPTION_ASK_EVERYTIME };

	// table data
	private Object[][] tableData;

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public int getRowCount() {
		getTableData();
		return tableData.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return tableData[row][col];
	}

	@Override
	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col >= 1;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		tableData[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	/**
	 * Getter for the ComboBox states
	 * 
	 * @return comboBoxStates
	 */
	public static String[] getComboBoxStates() {
		return comboBoxStates;
	}

	/**
	 * Getter for the table data
	 * 
	 * @return tableData
	 */
	public Object[][] getTableData() {
		if (tableData == null) {
			LinkedList<NodeTag> riaTags = RIATagsHelper.getSortedRIATags();
			tableData = new String[riaTags.size()][2];
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = riaTags.get(i).toString();
				tableData[i][1] = riaTags.get(i).getRIAOption();
			}
		}

		return tableData;
	}
}

/**
 * Renderer for ComboBox with RIA Options
 * 
 * 
 * 
 */
class ComboBoxRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	public ComboBoxRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(Color.white);
		} else {
			setBackground(Color.lightGray);
			setForeground(Color.BLACK);
		}

		// Set the icon and text
		ImageIcon icon = RIATagsHelper.getRIAIcon(value.toString());

		if (icon != null) {
			setIcon(icon);
		}

		setText(value.toString());

		return this;
	}

}

/**
 * Renderer for second column. Shows RIA Option with icon.
 * 
 * 
 */
class SecondColumnIconRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object,
	 *      boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		ImageIcon icon = RIATagsHelper.getRIAIcon(value.toString());
		setText(value.toString());
		setIcon(icon);
		return this;
	}

}