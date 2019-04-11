/**
 * 
 */
package magicUWE.actions.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import magicUWE.shared.MagicDrawElementOperations;
import magicUWE.shared.MessageWriter;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeWithKey;
import magicUWE.stereotypes.UWEStereotypeOfElWithSecondKey;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.AggregationKindEnum;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

/**
 * Draws a property of a new class into an existing and selected class
 * 
 * @author PST LMU
 */
public class DrawPropertyAction extends DrawShapeDiagramAction {
	private UWEStereotypeWithKey classStereotype;
	private Boolean showWarningAgain = true;

	/**
	 * DrawPropertyAction
	 * 
	 * @param stereotype
	 */
	public DrawPropertyAction(UWEStereotypeOfElWithSecondKey stereotype) {
		super(stereotype.getDisplayName(1), stereotype.getDisplayName(1), stereotype.getSecondKeyStroke());
		this.classStereotype = stereotype;
	}

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DrawPropertyAction.class);

	public static void setSupressStrctureOfClass(PresentationElement presentationElement, boolean val) {
		PropertyManager propertyManager = new PropertyManager();
		propertyManager.addProperty(new BooleanProperty(PropertyID.SUPPRESS_STRUCTURE, val));
		try {
			PresentationElementsManager.getInstance().setPresentationElementProperties(presentationElement,
					propertyManager);

		} catch (ReadOnlyElementException e) {
			logger.error("Can't modify the SUPPRESS_STRUCTURE property of the element!");
		}
	}

	@Override
	protected Element createElement() {
		SessionManager.getInstance().createSession("Try to create Class and Property and add it to a container");
		// project active diagram
		Project project = Application.getInstance().getProject();
		DiagramPresentationElement activeDiagram = project.getActiveDiagram();

		// inform the user about the wrong diagram type once
		UWEDiagramType dgType = UWEDiagramType.getDiagramType(activeDiagram.getDiagram());
		if (showWarningAgain != null && showWarningAgain == true
				&& (dgType == null || !dgType.mayContainStereotype(classStereotype))) {
			showWarningAgain =
					DrawElementAction.showWarningBecauseOfWrongDiagramTypeEverySecondTime(classStereotype, logger);
		}
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		// selected elements in diagram
		for (Object selected : activeDiagram.getContainer().getSelected()) {
			PresentationElement presentationElement = (PresentationElement) selected;
			Element element = presentationElement.getElement();

			Property property = null;

			if (element instanceof Class) {
				// presentationElement representing class
				Class containerClass = (Class) element;
				setSupressStrctureOfClass(presentationElement, false);
				property = createClassAndGetProperty(project, presentationElement);
				if (property != null) {
					// add property to class this is needed.. MagicDraw is
					// not well thought out at that point, because you can
					// paint "nothing" into another property than that one
					// (or class) you have previously selected (and linked
					// with the following line)
					property.setOwner(containerClass);
				}

			} else if (element instanceof Property) {
				Property containerProperty = (Property) element;
				property = createClassAndGetProperty(project, presentationElement);
				if (property != null) {
					property.setOwner(containerProperty.getType());
				}

			} else {
				break;
			}

			SessionManager.getInstance().closeSession();
			return property;
		}
		MessageWriter.showMessage(
				"Please retry and select the element (which will contain the future property) first!\n"
						+ "Cancel this operation!", logger);
		SessionManager.getInstance().closeSession();
		return null;
	}

	/**
	 * Wraps the first call of createElement(). Used to display the message
	 * maximally once per toolbar action (uses null value of showWarningAgain =>
	 * must be changed back to true)
	 */
	@Override
	protected PresentationElement createPresentationElement() {
		if (showWarningAgain == null) {
			showWarningAgain = true;
		}
		PresentationElement presElement = super.createPresentationElement();
		if (showWarningAgain) {
			showWarningAgain = null;
		}
		return presElement;
	}

	private Property createClassAndGetProperty(Project project, PresentationElement presentationElement) {
		String[] names = (new GUIForPropertyAndClassNames()).getPropertyAndClassname();
		if (names != null) {
			// create property
			Property property = project.getElementsFactory().createPropertyInstance();
			property.setName(names[0]);
			property.setAggregation(AggregationKindEnum.COMPOSITE);

			// Set Stereotype to Class and to Property
			// create class and set it as property type
			Class clazz = MagicDrawElementOperations.createClass(classStereotype.toString());
			clazz.setOwner(presentationElement.getElement().getOwner());
			clazz.setName(names[1]);
			property.setType(clazz);
			MagicDrawElementOperations.addStereotypeToElement(property, classStereotype.toString());
			return property;
		}
		return null;
	}
}

class GUIForPropertyAndClassNames {
	boolean exited = false;
	static JDialog dialog;

	protected String[] getPropertyAndClassname() {
		String[] names = { "newProperty", "newClass" };

		dialog = new JDialog();
		dialog.setModal(true);
		dialog.setTitle("Insert new Property of new Class");

		// The user closes the window
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				exited = true;
			}
		});
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		// Create buttons, labels and text fields
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIForPropertyAndClassNames.dialog.dispose();
			}
		});

		JLabel labelProperty = new JLabel("Name of Property:");
		JLabel labelClass = new JLabel("Name of new Class:");
		JLabel labelDoublePoint = new JLabel(":");

		JTextField textPropertyName = new JTextField(names[0]);
		JTextField textClassName = new JTextField(names[1]);

		// Create and set up the content pane.
		JComponent newContentPane = new JPanel(new GridBagLayout());

		GridBagConstraints cc1 = new GridBagConstraints();
		cc1.fill = GridBagConstraints.BOTH;
		cc1.anchor = GridBagConstraints.LINE_START;
		cc1.insets = new Insets(8, 2, 5, 0);
		cc1.gridx = 0;
		cc1.gridy = 0;
		cc1.gridwidth = 2;

		GridBagConstraints cc2 = new GridBagConstraints();
		cc2.fill = GridBagConstraints.BOTH;
		cc2.insets = new Insets(8, 2, 5, 0);
		cc2.gridx = 2;
		cc2.gridy = 0;

		newContentPane.add(labelProperty, cc1);
		newContentPane.add(labelClass, cc2);

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.anchor = GridBagConstraints.LINE_START;
		c1.insets = new Insets(0, 0, 5, 0);
		c1.weightx = 0.45;
		c1.weighty = 1;
		c1.gridx = 0;
		c1.gridy = 1;

		GridBagConstraints c2 = new GridBagConstraints();
		c2.anchor = GridBagConstraints.CENTER;
		c2.insets = new Insets(0, 0, 5, 0);
		c2.weightx = 0.1;
		c2.weighty = 1;
		c2.gridx = 1;
		c2.gridy = 1;

		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.BOTH;
		c3.insets = new Insets(0, 0, 5, 0);
		c3.weightx = 0.45;
		c3.weighty = 1;
		c3.anchor = GridBagConstraints.LINE_END;
		c3.gridx = 2;
		c3.gridy = 1;

		newContentPane.add(textPropertyName, c1);
		newContentPane.add(labelDoublePoint, c2);
		newContentPane.add(textClassName, c3);

		GridBagConstraints ccc1 = new GridBagConstraints();
		ccc1.fill = GridBagConstraints.BOTH;
		ccc1.insets = new Insets(2, 80, 5, 80);
		ccc1.weighty = 1;
		ccc1.gridx = 0;
		ccc1.gridy = 2;
		ccc1.gridwidth = 3;
		newContentPane.add(okButton, ccc1);

		dialog.getRootPane().setDefaultButton(okButton);
		dialog.setContentPane(newContentPane);

		// set the position of the dialog
		dialog.setBounds(0, 0, 300, 120);
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
			names[0] = textPropertyName.getText().trim();
			names[1] = textClassName.getText().trim();
			return names;
		}
		return null;
	}
}