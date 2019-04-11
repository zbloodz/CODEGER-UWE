package magicUWE.actions.context.sessionTransmissionCheck;

import static java.awt.BorderLayout.CENTER;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * GUI for results
 * 
 * @author PST LMU
 */
public class ViolationsGUI extends JPanel implements ItemListener {

	private static final long serialVersionUID = -8241647190078178952L;

	final JTable table;
	List<RuleViolationTransmissionType> violations;

	private final TableMouseAdapter tableMouseAdapter;
	private final JCheckBox drawRectangles = new JCheckBox("Draw rectangles around elements of listed items", true);

	private final Project project;

	public ViolationsGUI(Project project, List<RuleViolationTransmissionType> violations) {
		this.violations = violations;
		this.project = project;

		// dialog settings
		this.setLayout(new BorderLayout());

		table = new JTable(getTableRows(), getTableColumns()) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// // Set color of severity
		// table.getColumnModel().getColumn(1).setCellRenderer(new
		// ColoredCellRender());

		DefaultListSelectionModel sm = new DefaultListSelectionModel();
		sm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSelectionModel(sm);

		table.setAutoCreateRowSorter(true);
		table.setColumnSelectionAllowed(false);
		tableMouseAdapter = new TableMouseAdapter(this);
		table.addMouseListener(tableMouseAdapter);
		table.getTableHeader().setReorderingAllowed(true);

		JScrollPane scrollPane = new JScrollPane(table);
		drawRectangles.addItemListener(this);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		panel.add(drawRectangles);

		this.add(panel, BorderLayout.NORTH);
		this.add(scrollPane, CENTER);

		this.setVisible(true);
	}

	/**
	 * Call this method if you have updated your data in
	 * RuleViolationTransmissionType!
	 */
	public void update() {
		updateTable();
		if (!drawRectangles.isSelected()) {
			RuleViolationTransmissionType.cleanViolations(project);
		}
		repaintDiagram();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == drawRectangles) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				RuleViolationTransmissionType.cleanViolations(project);
			} else if (e.getStateChange() == ItemEvent.SELECTED) {
				RuleViolationTransmissionType.setViolations(project, violations);
			}
			repaintDiagram();
		}
	}

	private Vector<Vector<Object>> getTableRows() {
		Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();
		if (violations != null) {
			for (int i = 0; i < violations.size(); i++) {
				RuleViolationTransmissionType v = violations.get(i);
				Vector<Object> row = new Vector<Object>(3);
				row.add(String.valueOf(i));
				Element el = v.violationElement;
				if (el != null) {
					row.add(el.getHumanName());
				} else {
					row.add("(empty)");
				}
				row.add(v.tagValue);
				row.add(v.parentValueWas);

				rowData.addElement(row);
			}
		}
		return rowData;
	}

	private Vector<String> getTableColumns() {
		Vector<String> columnNames = new Vector<String>(3);
		columnNames.add("ID");
		columnNames.add("Name");
		columnNames.add("Value of {transmissionType}");
		columnNames.add("Parent's value");
		return columnNames;
	}

	private void updateTable() {
		table.setModel(new DefaultTableModel(getTableRows(), getTableColumns()));
		// // Set color of severity
		// table.getColumnModel().getColumn(1).setCellRenderer(new
		// ColoredCellRender());
		// no color
	}

	private void repaintDiagram() {
		DiagramPresentationElement diag = project.getActiveDiagram();
		if (diag != null) {
			diag.getDiagramSurface().repaint();
		}
	}
}

class TableMouseAdapter extends MouseAdapter {

	private ViolationsGUI gui;

	public TableMouseAdapter(ViolationsGUI gui) {
		this.gui = gui;
	}

	/**
	 * Select element from list in Containment Tree
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && gui.violations != null) {
			ContainmentTree ct = Application.getInstance().getMainFrame().getBrowser().getContainmentTree();
			Element el = gui.violations.get(Integer.valueOf((String) gui.table.getModel().getValueAt(
					gui.table.convertRowIndexToModel(gui.table.getSelectedRow()), 0))).violationElement;
			if (el != null) {
				ct.openNode(el, true);
			}
		}
	}
}

// class ColoredCellRender extends DefaultTableCellRenderer {
// private static final long serialVersionUID = 4166228907849520264L;
//
// @Override
// public Component getTableCellRendererComponent(JTable t, Object value,
// boolean isSelected, boolean hasFocus,
// int row, int column) {
// JLabel label = (JLabel) super.getTableCellRendererComponent(t, value,
// isSelected, hasFocus, row, column);
// label.setForeground(((ConsistencyRulePriority) value).getColor());
// return label;
// }
// }