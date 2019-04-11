package magicUWE.actions.toolbar;

import static javax.swing.SwingConstants.HORIZONTAL;

import static magicUWE.stereotypes.UWEStereotypeBasicRightsDependencies.*;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeBasicRightsDependencies;
import magicUWE.stereotypes.UWEStereotypeWithKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DrawPathDiagramAction;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * Create and draw an dependency into a diagram.
 * 
 * @author PST LMU
 */
public class DrawBasicRightsDependencyAction extends DrawPathDiagramAction {

	private static final Logger logger = Logger.getLogger(DrawBasicRightsDependencyAction.class);
	private static final long serialVersionUID = 1L;
	private Boolean showWarningAgain = true;
	private final UWEStereotypeWithKey ster;

	private boolean showGUInextTime = false;

	/**
	 * DrawDependencyAction
	 * 
	 * @param ster
	 *            can be null!
	 */
	public DrawBasicRightsDependencyAction(String name, KeyStroke keyStroke, UWEStereotypeWithKey ster) {
		super(name, name, keyStroke);
		this.ster = ster;
	}

	@Override
	protected Element createElement() {
		// inform the user about the wrong diagram type once
		UWEDiagramType dgType = UWEDiagramType.getDiagramType(Application.getInstance().getProject().getActiveDiagram()
				.getDiagram());
		if (showWarningAgain != null && showWarningAgain == true
				&& (dgType == null || dgType != UWEDiagramType.BASIC_RIGHTS)) {
			String message = "<html>This is the wrong UWE diagram type for a " + ster == null ? "Basic Rights" : ster
					.getDisplayName() + " dependency." + "<br/><br/>It is recommended to cancel this operation.</html>";
			logger.info(message);
			showWarningAgain = (new MessageWithDisplayAgainOption(message)).showAgain;
		}
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		logger.debug("prepare/create Dependency");
		Dependency dependency = Application.getInstance().getProject().getElementsFactory().createDependencyInstance();

		// add stereotypes
		if (ster != null) {
			MagicDrawElementOperations.addStereotypeToElement(dependency, ster.toString());
		} else {
			if (showGUInextTime) {
				// show GUI for selecting stereotypes
				for (UWEStereotypeBasicRightsDependencies s : (new DependencySelectorGUI()).getPropertyAndClassname()) {
					MagicDrawElementOperations.addStereotypeToElement(dependency, s.toString());
				}
			}
		}
		return dependency;
	}

	/**
	 * Wraps the first call of createElement(). Used to display the message
	 * maximally once per toolbar action (uses null value of showWarningAgain =>
	 * must be changed back to true)
	 */
	@Override
	protected PathElement createPathElement() {
		showGUInextTime = false;
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		PathElement pathElement = super.createPathElement();
		if (showWarningAgain) {
			showWarningAgain = null;
		}
		showGUInextTime = true;
		return pathElement;
	}
}

class DependencySelectorGUI implements ItemListener {
	boolean exited = false;
	static JDialog dialog;
	private List<JChkBoxWithDependency> chkBoxList = new LinkedList<JChkBoxWithDependency>();
	List<UWEStereotypeBasicRightsDependencies> selectedStereotypes = new LinkedList<UWEStereotypeBasicRightsDependencies>();

	protected List<UWEStereotypeBasicRightsDependencies> getPropertyAndClassname() {
		dialog = new JDialog();
		dialog.setModal(true);
		dialog.setTitle("Add");

		// The user closes the window
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				exited = true;
			}
		});
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Create buttons, labels and text fields
		JButton okButton = new JButton("Add stereotypes");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DependencySelectorGUI.dialog.dispose();
			}
		});

		JPanel chkBoxContainer = new JPanel();
		chkBoxContainer.setLayout(new BoxLayout(chkBoxContainer, BoxLayout.PAGE_AXIS));
		for (UWEStereotypeBasicRightsDependencies s : UWEStereotypeBasicRightsDependencies.values()) {
			JChkBoxWithDependency chk = new JChkBoxWithDependency(s);
			chk.setSelected(false);
			chk.addItemListener(this);
			chkBoxList.add(chk);
			chkBoxContainer.add(chk);
			switch (s) {
			case EXECUTE_ALL:
				chkBoxContainer.add(new JSeparator(HORIZONTAL));
				break;
			case READ:
				chkBoxContainer.add(new JSeparator(HORIZONTAL));
				break;
			default:
				break;
			}
		}

		// Create and set up the content pane.
		JComponent newContentPane = new JPanel(new BorderLayout());

		dialog.getRootPane().setDefaultButton(okButton);
		dialog.setContentPane(newContentPane);

		newContentPane.add(chkBoxContainer, BorderLayout.CENTER);
		newContentPane.add(okButton, BorderLayout.SOUTH);

		// set the position of the dialog
		dialog.setBounds(0, 0, 150, 250);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) ((toolkit.getScreenSize().getWidth() - dialog.getWidth()) / 2);
		int y = (int) ((toolkit.getScreenSize().getHeight() - dialog.getHeight()) / 2);
		dialog.setLocation(x, y);

		// Display the window.
		dialog.setVisible(true);
		try {
			// need to wait a moment to show the dialog on the top!
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// wake up
		}
		dialog.toFront();

		// FUNCTIONALITY - get Names
		if (exited == false) {
			return selectedStereotypes;
		}
		return new LinkedList<UWEStereotypeBasicRightsDependencies>();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItemSelectable() instanceof JChkBoxWithDependency) {
			JChkBoxWithDependency source = ((JChkBoxWithDependency) e.getItemSelectable());
			if (e.getStateChange() == ItemEvent.SELECTED) {
				selectedStereotypes.add(source.dependencyType);

				// Actions for classes / attributes and methods are distinct
				switch (source.dependencyType) {
				case CREATE:
				case DELETE:
				case UPDATE_ALL:
				case READ_ALL:
				case EXECUTE_ALL:
					setStateOfChkBox(UPDATE, true, false, false);
					setStateOfChkBox(READ, true, false, false);
					setStateOfChkBox(EXECUTE, true, false, false);
					break;
				case UPDATE:
				case READ:
					setStateOfChkBox(CREATE, true, false, false);
					setStateOfChkBox(DELETE, true, false, false);
					setStateOfChkBox(READ_ALL, true, false, false);
					setStateOfChkBox(UPDATE_ALL, true, false, false);
					setStateOfChkBox(EXECUTE_ALL, true, false, false);
					setStateOfChkBox(EXECUTE, true, false, false);
					break;
				case EXECUTE:
					for (UWEStereotypeBasicRightsDependencies s : UWEStereotypeBasicRightsDependencies.values()) {
						if (s != EXECUTE)
							setStateOfChkBox(s, true, false, false);
					}
					break;
				default:
					break;
				}

				// update includes read
				switch (source.dependencyType) {
				case UPDATE:
					setStateOfChkBox(READ, true, false, true);
					break;
				case UPDATE_ALL:
					setStateOfChkBox(READ_ALL, true, false, true);
					break;
				default:
					break;
				}

			} else if (e.getStateChange() == ItemEvent.DESELECTED) {
				selectedStereotypes.remove(source.getText());
				// update includes read
				switch (source.dependencyType) {
				case UPDATE:
					setStateOfChkBox(READ, false, true, false);
					break;
				case UPDATE_ALL:
					setStateOfChkBox(READ_ALL, false, true, false);
					break;
				default:
					break;
				}

				// Actions for classes / attributes and methods are distinct
				if ((source.dependencyType == CREATE && !searchChkBoxByDependency(DELETE).isSelected()
						&& !searchChkBoxByDependency(READ_ALL).isSelected()
						&& !searchChkBoxByDependency(UPDATE_ALL).isSelected() && !searchChkBoxByDependency(EXECUTE_ALL)
						.isSelected())
						|| (source.dependencyType == DELETE && !searchChkBoxByDependency(CREATE).isSelected())
						&& !searchChkBoxByDependency(READ_ALL).isSelected()
						&& !searchChkBoxByDependency(UPDATE_ALL).isSelected()
						&& !searchChkBoxByDependency(EXECUTE_ALL).isSelected()
						|| (source.dependencyType == READ_ALL && !searchChkBoxByDependency(CREATE).isSelected())
						&& !searchChkBoxByDependency(DELETE).isSelected()
						&& !searchChkBoxByDependency(UPDATE_ALL).isSelected()
						&& !searchChkBoxByDependency(EXECUTE_ALL).isSelected()
						|| (source.dependencyType == UPDATE_ALL && !searchChkBoxByDependency(CREATE).isSelected())
						&& !searchChkBoxByDependency(DELETE).isSelected()
						&& !searchChkBoxByDependency(READ_ALL).isSelected()
						&& !searchChkBoxByDependency(EXECUTE_ALL).isSelected()
						|| (source.dependencyType == EXECUTE_ALL && !searchChkBoxByDependency(CREATE).isSelected())
						&& !searchChkBoxByDependency(DELETE).isSelected()
						&& !searchChkBoxByDependency(UPDATE_ALL).isSelected()
						&& !searchChkBoxByDependency(READ_ALL).isSelected()
						|| (source.dependencyType == UPDATE && !searchChkBoxByDependency(READ).isSelected() || source.dependencyType == READ
								&& !searchChkBoxByDependency(UPDATE).isSelected()) || source.dependencyType == EXECUTE) {
					for (JChkBoxWithDependency box : chkBoxList) {
						box.setEnabled(true);
					}
				}
			}
		}
	}

	private void setStateOfChkBox(UWEStereotypeBasicRightsDependencies brDependency, boolean deleteFromchkBoxList,
			boolean setEnabled, Boolean setSelected) {
		JChkBoxWithDependency chkBox = searchChkBoxByDependency(brDependency);
		if (setSelected != null) {
			chkBox.setSelected(setSelected);
		}
		chkBox.setEnabled(setEnabled);
		if (deleteFromchkBoxList) {
			selectedStereotypes.remove(brDependency);
		}
	}

	private JChkBoxWithDependency searchChkBoxByDependency(UWEStereotypeBasicRightsDependencies dependencyType) {
		for (int i = 0; i < chkBoxList.size(); i++) {
			if (chkBoxList.get(i).dependencyType == dependencyType) {
				return chkBoxList.get(i);
			}
		}
		return null;
	}

	class JChkBoxWithDependency extends JCheckBox {

		private static final long serialVersionUID = 1L;

		public final UWEStereotypeBasicRightsDependencies dependencyType;

		public JChkBoxWithDependency(UWEStereotypeBasicRightsDependencies dependencyType) {
			super(dependencyType.toString());
			this.dependencyType = dependencyType;
		}
	}
}