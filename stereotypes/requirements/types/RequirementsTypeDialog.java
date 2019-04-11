/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.stereotypes.requirements.types;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import magicUWE.core.PluginManager;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Pin;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;

/**
 *
 * @author PST LMU
 */
public class RequirementsTypeDialog extends JDialog implements ActionListener 
{
	private static final long serialVersionUID = -8070269403549014625L;
	private static final String sOK="OK";
    private static final String sCANCEL="Cancel";
    public static final String sREMOVE="remove";
    private static final String sTITLE_DIALOG="Select Type of Requirements Element";
    private static final String sTITLE_OPTIONS="Available Types:";

    private static final String iconsPath = "icons/requirements/";
    private static final String iconSuffix = ".png";
    
    // Font definitions
    private static final String sFONT="SansSerif";
    private static final int iTEXT_BIG=16;
    private static final int iTEXT_NORMAL=12;

    private String sSelection;

    private JRadioButton radioTypes[];
    private JLabel labelTypes[];
    private JPanel panelTypes[];
    private ButtonGroup groupRadioTypes;

    /**
     * Constructor of RequirementsTypeDialog, opens the Dialog
     *
     */
    public RequirementsTypeDialog(Element element)
    {
        this.setModal(true);
        this.setTitle(sTITLE_DIALOG);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel radioPanel = new JPanel(new GridBagLayout());
        radioPanel.setAlignmentX(LEFT_ALIGNMENT);

	groupRadioTypes = new ButtonGroup();

        String sType=null;
        String sPath=null;
        String sValues[]=null;

        if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString())))
        {
            if (StereotypesHelper.getStereotypePropertyValue(element, UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), 
                    UWEDisplayActionType.sTagName).size()==1)
            {
                sType=((EnumerationLiteral)(StereotypesHelper.getStereotypePropertyFirst(element,
                        UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), UWEDisplayActionType.sTagName))).getName();
            }

            radioTypes = new JRadioButton [UWEDisplayActionType.values().length];
            labelTypes = new JLabel [UWEDisplayActionType.values().length];
            panelTypes = new JPanel [UWEDisplayActionType.values().length];

            sValues=new String[UWEDisplayActionType.values().length];
            for (int i=0;i<UWEDisplayActionType.values().length;i++)
            {
                sValues[i]=UWEDisplayActionType.values()[i].getName();
            }
            sPath=UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString()+"/";
        }
        else if ((element instanceof Action)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString())))
        {
            if (StereotypesHelper.getStereotypePropertyValue(element, UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), 
                    UWENavigationActionType.sTagName).size()==1)
            {
                sType=((EnumerationLiteral)(StereotypesHelper.getStereotypePropertyFirst(element,
                        UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), 
                        UWENavigationActionType.sTagName))).getName();
            }

            radioTypes = new JRadioButton [UWENavigationActionType.values().length];
            labelTypes = new JLabel [UWENavigationActionType.values().length];
            panelTypes = new JPanel [UWENavigationActionType.values().length];

            sValues=new String[UWENavigationActionType.values().length];
            for (int i=0;i<UWENavigationActionType.values().length;i++)
            {
                sValues[i]=UWENavigationActionType.values()[i].getName();
            }
            sPath=UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString()+"/";
        }
        else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString())))
        {
            if (StereotypesHelper.getStereotypePropertyValue(element, UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(), 
                    UWEDisplayPinType.sTagName).size()==1)
            {
                sType=((EnumerationLiteral)(StereotypesHelper.getStereotypePropertyFirst(element,
                        UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(), 
                        UWEDisplayPinType.sTagName))).getName();
            }

            radioTypes = new JRadioButton [UWEDisplayPinType.values().length];
            labelTypes = new JLabel [UWEDisplayPinType.values().length];
            panelTypes = new JPanel [UWEDisplayPinType.values().length];

            sValues=new String[UWEDisplayPinType.values().length];
            for (int i=0;i<UWEDisplayPinType.values().length;i++)
            {
                sValues[i]=UWEDisplayPinType.values()[i].getName();
            }
            sPath=UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString()+"/";
        }
        else if ((element instanceof Pin)&&(StereotypesHelper.hasStereotype(element, UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString())))
        {
            if (StereotypesHelper.getStereotypePropertyValue(element, UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(), 
                    UWEInteractionPinType.sTagName).size()==1)
            {
                sType=((EnumerationLiteral)(StereotypesHelper.getStereotypePropertyFirst(element,
                        UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(), UWEInteractionPinType.sTagName))).getName();
            }

            radioTypes = new JRadioButton [UWEInteractionPinType.values().length];
            labelTypes = new JLabel [UWEInteractionPinType.values().length];
            panelTypes = new JPanel [UWEInteractionPinType.values().length];

            sValues=new String[UWEInteractionPinType.values().length];
            for (int i=0;i<UWEInteractionPinType.values().length;i++)
            {
                sValues[i]=UWEInteractionPinType.values()[i].getName();
            }
            sPath=UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString()+"/";
        }

        for (int i=0;i<sValues.length;i++)
        {
            radioTypes[i] = new JRadioButton();
            radioTypes[i].setActionCommand(sValues[i]);

            labelTypes[i] = new JLabel(sValues[i], new ImageIcon(PluginManager.class.getResource(iconsPath+sPath+sValues[i]+iconSuffix)), SwingConstants.LEFT);
            labelTypes[i].setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));

            panelTypes[i] = new JPanel(new FlowLayout());
            panelTypes[i].add(radioTypes[i]);
            panelTypes[i].add(labelTypes[i]);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = i;
            c.anchor = GridBagConstraints.LINE_START;
            radioPanel.add(panelTypes[i], c);

            if ((sType!=null)&&(sValues[i].equals(sType)))
            {
                radioTypes[i].setSelected(true);
            }
            else
            {
                radioTypes[i].setSelected(false);
            }

            groupRadioTypes.add(radioTypes[i]);
        }

        if (sType==null)
        {
            radioTypes[0].setSelected(true);
        }
        else
        {
            JRadioButton radioDelete = new JRadioButton();
            radioDelete.setActionCommand(sREMOVE);
            radioDelete.setAlignmentX(LEFT_ALIGNMENT);
            radioDelete.setSelected(true);

            JPanel panelDelete = new JPanel(new FlowLayout());
            panelDelete.setAlignmentX(LEFT_ALIGNMENT);

            JLabel labelDelete = new JLabel(sREMOVE, new ImageIcon(PluginManager.class.getResource(iconsPath+sREMOVE+iconSuffix)), SwingConstants.LEFT);
            labelDelete.setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));

            panelDelete.add(radioDelete);
            panelDelete.add(labelDelete);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = radioTypes.length;
            c.anchor = GridBagConstraints.LINE_START;

            groupRadioTypes.add(radioDelete);
            radioPanel.add(panelDelete, c);
        }

        JLabel label = new JLabel(sTITLE_OPTIONS);
        label.setFont(new Font(sFONT, Font.PLAIN, iTEXT_BIG));
        label.setBorder(BorderFactory.createEmptyBorder(10, 7, 5, 0));
        this.getContentPane().add(label, BorderLayout.NORTH);
        this.getContentPane().add(radioPanel, BorderLayout.CENTER);
        this.getContentPane().add(new JLabel("  "), BorderLayout.WEST);
        this.getContentPane().add(new JLabel("  "), BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton buttonOK = new JButton(sOK);
        buttonOK.addActionListener(this);
        buttonOK.setActionCommand(sOK);
        this.getRootPane().setDefaultButton(buttonOK);
        buttonOK.setMnemonic(KeyEvent.VK_O);

        JButton buttonCancel = new JButton(sCANCEL);
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand(sCANCEL);
        buttonCancel.setMnemonic(KeyEvent.VK_C);

        buttonPanel.add(buttonOK);
        buttonPanel.add(buttonCancel);

        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        this.pack();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int) ((toolkit.getScreenSize().getWidth() - this.getWidth()) / 2);
        int y = (int) ((toolkit.getScreenSize().getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);

        this.setSize(this.getWidth()*2,this.getHeight());
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setVisible(true);
    }
    
    /** Is called when an event occured.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equals(sOK))
        {
            sSelection = groupRadioTypes.getSelection().getActionCommand();
            this.dispose();
        }
        else if (event.getActionCommand().equals(sCANCEL))
        {
            this.dispose();
        }
    }

    /** Returns the value of the selection
     * 
     * @return name of the selected type
     */
    public String getSelection()
    {
        return sSelection;
    }
}
