package magicUWE.riaPatterns;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import magicUWE.core.PluginManager;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.ui.DialogConstants;
import com.nomagic.ui.ResizableIcon;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdcollaborations.Collaboration;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

/**
 * Dialog for choosing types of parts of a Collaboration Use that should be
 * added for dependencies
 * 
 * 
 */
public class RIADependenciesPartsDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RIADependenciesPartsDialog.class);

	private static boolean useSystemLookAndFeel = false;

	private JButton okButton;
	private JButton cancelButton;

	private static final String okButtonName = "OK";
	private static final String cancelButtonName = "Cancel";

	private PartsTable table;

	private int result;

	/**
	 * Opens {@link RIADependenciesPartsDialog}
	 * 
	 * @param collaboration
	 *            source Collaboration from UWE Profile
	 */
	public RIADependenciesPartsDialog(Collaboration collaboration) {

		if (useSystemLookAndFeel) { // set to false, otherwise some problems
			// with other java applications running parallel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.error("Couldn't use system look and feel.");
			}
		}
		this.setModal(true);
		this.setTitle("choose classes of the parts for " + collaboration.getName());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// new content pane
		JPanel newContentPane = new JPanel();
		newContentPane.setOpaque(true);
		newContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.Y_AXIS));
		this.setContentPane(newContentPane);

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

		table = new PartsTable(this, collaboration);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		newContentPane.add(scrollPane);

		newContentPane.add(buttonPanel);

		this.pack();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - this.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);

		this.setAlwaysOnTop(true);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(okButtonName)) {
			this.result = DialogConstants.OK;
			this.dispose();
		} else if (event.getActionCommand().equals(cancelButtonName)) {
			this.result = DialogConstants.CANCEL;
			this.dispose();
		}
	}

	/**
	 * @return result DialogConstants.OK or DialogConstants.CANCEL
	 */
	public int getResult() {
		return result;
	}

	/**
	 * @return {@link PartsTable} of Dialog
	 */
	public PartsTable getTable() {
		return table;
	}

}

/**
 * Table of {@link RIADependenciesPartsDialog} for choosing types of parts for
 * dependencies
 * 
 * 
 * 
 */
class PartsTable extends JTable {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of {@link PartsTable}
	 * 
	 * @param dialog
	 *            {@link RIADependenciesPartsDialog}
	 * @param collaboration
	 *            source Collaboration from UWE Profile
	 */
	public PartsTable(RIADependenciesPartsDialog dialog, Collaboration collaboration) {
		// Create the JTable
		super(new PartsTableModel(collaboration));

		this.columnModel.getColumn(0).setCellRenderer(new PartRenderer());
		this.columnModel.getColumn(1).setCellRenderer(new TypeRenderer());
		ButtonColumn editorRenderer = new ButtonColumn(dialog, this);
		this.columnModel.getColumn(2).setCellRenderer(editorRenderer);
		this.columnModel.getColumn(2).setCellEditor(editorRenderer);

		// Set column widths
		this.getColumnModel().getColumn(0).setPreferredWidth(130);
		this.getColumnModel().getColumn(1).setPreferredWidth(130);
		this.getColumnModel().getColumn(2).setPreferredWidth(130);

		// Set row height
		this.setRowHeight(23);

		this.setPreferredScrollableViewportSize(this.getPreferredSize());
		this.setFillsViewportHeight(true);
		this.setIntercellSpacing(new Dimension(0, 5));
		this.getTableHeader().setReorderingAllowed(false);

	}
}

/**
 * TableModel with data of {@link PartsTable}
 * 
 * 
 * 
 */
class PartsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	// column names
	private String[] columnNames = { "Part", "Type", "" };

	// table data
	private Object[][] tableData;

	/**
	 * 
	 * @param collaboration
	 *            for access to parts of Collaboration
	 */
	public PartsTableModel(Collaboration collaboration) {
		super();
		getTableData(collaboration);
	}

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
		return tableData.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return tableData[row][col];

	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 2) {
			return true;
		}
		return false;
	}

	@Override
	public java.lang.Class<?> getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}

	@Override
	public void setValueAt(Object value, int row, int col) {

		tableData[row][col] = value;
		fireTableCellUpdated(row, col);

	}

	/**
	 * Creates tableData with parts of collaboration
	 * 
	 * @param collaboration
	 * @return tableData
	 */
	public Object[][] getTableData(Collaboration collaboration) {
		if (tableData == null) {
			Collection<Property> parts = collaboration.getAttribute();
			Object[] partsArray = parts.toArray();
			tableData = new Object[partsArray.length][3];
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = partsArray[i];
				tableData[i][2] = "Select type...";
			}
		}

		return tableData;
	}

}

/**
 * Renderer for first column of {@link PartsTable}. Shows name and icon of
 * parts.
 * 
 * 
 * 
 */
class PartRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		Property element = (Property) value;
		if (element == null) {
			label.setText("");
		} else {
			label.setText(element.getName());
			List<Stereotype> stereotypesOfElement = StereotypesHelper.getStereotypes(element.getType());
			if (stereotypesOfElement.size() >= 1) {
				Stereotype firstStereotype = StereotypesHelper.getFirstStereotypeWithIcon(stereotypesOfElement);
				ResizableIcon icon = StereotypesHelper.getIcon(firstStereotype);
				label.setIcon(icon);
			} else {
				label.setIcon(new ImageIcon(PluginManager.class.getResource("icons/ria/class.png")));
			}
		}
		return label;
	}

}

/**
 * Renderer for second column of {@link PartsTable}. Shows icon and name of
 * chosen type.
 * 
 * 
 * 
 */
class TypeRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		Class element = (Class) value;
		label.setText((element == null) ? "" : element.getName());
		if (element != null) {
			List<Stereotype> stereotypesOfElement = StereotypesHelper.getStereotypes(element);

			if (stereotypesOfElement.size() >= 1) {
				Stereotype firstStereotype = StereotypesHelper.getFirstStereotypeWithIcon(stereotypesOfElement);
				ResizableIcon icon = StereotypesHelper.getIcon(firstStereotype);
				label.setIcon(icon);
			} else {
				label.setIcon(new ImageIcon(PluginManager.class.getResource("icons/ria/class.png")));
			}
		} else {
			label.setIcon(null);
		}

		return label;
	}

}

/**
 * Renderer for third column of {@link PartsTable}. Shows button
 * "Select type...". Opens ElementSelectionDlg for choosing types of parts.
 * 
 * 
 * 
 */
class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton renderButton;
	private JButton editButton;
	private String text;
	private JTable table;
	private RIADependenciesPartsDialog dialog;

	/**
	 * Constructor of {@link ButtonColumn}
	 * 
	 * @param dialog
	 *            {@link RIADependenciesPartsDialog} as parent of
	 *            ElementSelectionDlg
	 * @param table
	 *            JTable for setting result of ElementSelectionDlg
	 */
	public ButtonColumn(RIADependenciesPartsDialog dialog, JTable table) {
		super();
		this.table = table;
		this.dialog = dialog;
		renderButton = new JButton();

		editButton = new JButton();
		editButton.setFocusPainted(false);
		editButton.addActionListener(this);

	}

	@Override
	public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (hasFocus) {
			renderButton.setForeground(tbl.getForeground());
			renderButton.setBackground(UIManager.getColor("Button.background"));
		} else if (isSelected) {
			renderButton.setForeground(tbl.getSelectionForeground());
			renderButton.setBackground(tbl.getSelectionBackground());
		} else {
			renderButton.setForeground(tbl.getForeground());
			renderButton.setBackground(UIManager.getColor("Button.background"));
		}

		renderButton.setText((value == null) ? "" : value.toString());
		return renderButton;
	}

	@Override
	public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
		text = (value == null) ? "" : value.toString();
		editButton.setText(text);
		return editButton;
	}

	@Override
	public Object getCellEditorValue() {
		return text;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		fireEditingStopped();
		ElementSelectionDlg esDialog = getElementSelectionDialog();
		esDialog.setVisible(true);
		Class selected = null;
		if (esDialog.getResult() == DialogConstants.OK) {
			selected = (Class) esDialog.getSelectedElement();
			table.setValueAt(selected, table.getSelectedRow(), 1);

		}

	}

	/**
	 * Creates {@link ElementSelectionDlg} for type selection
	 * 
	 * @return ElementSelectionDlg
	 */
	private ElementSelectionDlg getElementSelectionDialog() {
		LinkedList<java.lang.Class<? extends Object>> select = new LinkedList<java.lang.Class<? extends Object>>();
		select.add(Class.class);

		SelectElementTypes types = new SelectElementTypes(null, select, select, null);

		SelectElementInfo info = new SelectElementInfo(false, false);

		ElementSelectionDlg esDialog = ElementSelectionDlgFactory.create(dialog);
		ElementSelectionDlgFactory.initSingle(esDialog, types, info, null);

		return esDialog;
	}
}
