/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package magicUWE.actions.context.requirements;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import magicUWE.shared.UWEDiagramType;
import magicUWE.transformation.requirements.ElementCollector;
import magicUWE.transformation.requirements.ReqToPreTransformations;

import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
/**
 *
 * @author PST LMU
 */
public class PresentationClassInsertionDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -7439816091307652061L;
	// Text used in this dialog
    private static final String sOK="OK";
    private static final String sCANCEL="Cancel";
    private static final String sPACKAGES="Containing Package:";
    private static final String sCLASSES="Available Classnames:";
    private static final String sTITLE_DIALOG_GROUP="Presentation Group Generation";
    private static final String sTITLE_LABEL_GROUP="Presentation Group Generation";
    private static final String sTITLE_DIALOG_FORM="Input Form Generation";
    private static final String sTITLE_LABEL_FORM="Input Form Generation";
    private static final String sTITLE_OPTIONS="Options:";
    private static final String sCREATE_CLASS_ONLY="Create Class only";
    private static final String sCREATE_TOPLEVEL="Create Class with top level Presentation Element Children only";
    private static final String sCREATE_ALL="Create Class with all Presentation Element Children";
    
    // Font definitions
    private static final String sFONT="SansSerif";
    private static final int iTEXT_BIG=16;
    private static final int iTEXT_NORMAL=12;
    
    // Bounds
    private static final int iWIDTH=400;
    private static final int iHEIGHT=250;
    
    // Selection values
    public static final int iCREATE_CLASS_ONLY=0;
    public static final int iCREATE_TOPLEVEL=1;
    public static final int iCREATE_ALL=2;

    // Dialog elements
    private static JComboBox comboboxClasses;
    private static JComboBox comboboxPackages;
    private static JRadioButton radioClassOnly;
    private static JRadioButton radioToplevel;
    private static JRadioButton radioAll;
    private static ButtonGroup groupOptions;

    // Data
    private static String sName;
    private static NamedElement cPackage=null;
    private static ArrayList<NamedElement> acPackages;
    private static ArrayList<NamedElement> acCandidates;

    /**
     * Constructor of RequirementsTypeDialog, opens the Dialog
     *
     */
    public PresentationClassInsertionDialog(boolean bGroup)
    {
        cPackage=null;
        
        JLabel labelTitle = null;
        if (bGroup)
        {
            setTitle(sTITLE_DIALOG_GROUP);
            labelTitle = new JLabel(sTITLE_LABEL_GROUP);
            acCandidates=ReqToPreTransformations.getTopPresentationGroupCandidates(true);
        }
        else
        {
            setTitle(sTITLE_DIALOG_FORM);
            labelTitle = new JLabel(sTITLE_LABEL_FORM);
            acCandidates=ReqToPreTransformations.getTopInputFormCandidates(true);
        }

        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        labelTitle.setFont(new Font(sFONT, Font.PLAIN, iTEXT_BIG));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.2;
        constraints.weighty=1.2;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 5, 0, 5);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(labelTitle,constraints);

        acPackages=ElementCollector.getNamedElements(UWEDiagramType.PRESENTATION.modelStereotype, null, Model.class, false, true);
        ArrayList<NamedElement> acTempPackages=ElementCollector.getNamedElements(null, null, Package.class, true, acPackages, false);
        for (int i=0;i<acTempPackages.size();i++)
        {
            acPackages.add(acTempPackages.get(i));
        }

        if ((cPackage==null)&&(acPackages.size()>0))
        {
            cPackage=acPackages.get(0);
        }

        JLabel labelPackage = new JLabel(sPACKAGES);
        comboboxPackages = new JComboBox();

        for (int i=0;i<acPackages.size();i++)
        {
            comboboxPackages.addItem(acPackages.get(i).getName());

            if ((cPackage!=null)&&(cPackage.equals(acPackages.get(i))))
            {
                comboboxPackages.setSelectedIndex(i);
            }
        }

        ArrayList<NamedElement> acTemp=new ArrayList<NamedElement>();
        acTemp.add(cPackage);
        ArrayList<NamedElement> acClasses=ElementCollector.getNamedElements(null, null, Class.class, false, acTemp, true);

        JLabel labelClasses = new JLabel(sCLASSES);
        comboboxClasses = new JComboBox();

        ArrayList<String> asComboboxNames=new ArrayList<String>();
        for (int i=0;i<acCandidates.size();i++)
        {
            if ((ElementCollector.getNamedElementFromArrayList(acClasses,acCandidates.get(i).getName(),true,true)==ElementCollector.iNO_ELEMENT) &&
                (!asComboboxNames.contains(acCandidates.get(i).getName())))

            {
                asComboboxNames.add(acCandidates.get(i).getName());
                comboboxClasses.addItem(acCandidates.get(i).getName());
            }
        }

        comboboxPackages.setActionCommand(sPACKAGES);
        comboboxPackages.addActionListener(this);

        JPanel panelPackages = new JPanel(new GridBagLayout());
        labelPackage.setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.WEST;
        panelPackages.add(labelPackage, constraints);
        comboboxPackages.setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.EAST;
        panelPackages.add(comboboxPackages, constraints);

        JPanel panelClasses = new JPanel(new GridBagLayout());
        labelClasses.setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));
        constraints.gridwidth=GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.WEST;
        panelClasses.add(labelClasses, constraints);
        comboboxClasses.setFont(new Font(sFONT, Font.PLAIN, iTEXT_NORMAL));
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.EAST;
        panelClasses.add(comboboxClasses, constraints);


        JPanel panelOptions = new JPanel(new GridBagLayout());
        JLabel labelOptions = new JLabel(sTITLE_OPTIONS);
        constraints.anchor = GridBagConstraints.WEST;
        panelOptions.add(labelOptions, constraints);

        if (groupOptions==null)
        {
            groupOptions = new ButtonGroup();
        }

        if (radioClassOnly==null)
        {
            radioClassOnly = new JRadioButton();
            radioClassOnly.setActionCommand(sCREATE_CLASS_ONLY);
            radioClassOnly.setSelected(true);
            groupOptions.add(radioClassOnly);
        }

        JLabel labelClassOnly = new JLabel(sCREATE_CLASS_ONLY, SwingConstants.LEFT);

        JPanel panelClassOnly = new JPanel(new FlowLayout());
        panelClassOnly.add(radioClassOnly);
        panelClassOnly.add(labelClassOnly);

        if (radioToplevel==null)
        {
            radioToplevel = new JRadioButton();
            radioToplevel.setActionCommand(sCREATE_TOPLEVEL);
            radioToplevel.setSelected(true);
            groupOptions.add(radioToplevel);
        }

        JLabel labelToplevel = new JLabel(sCREATE_TOPLEVEL, SwingConstants.LEFT);

        JPanel panelToplevel = new JPanel(new FlowLayout());
        panelToplevel.add(radioToplevel);
        panelToplevel.add(labelToplevel);

        if (radioAll==null)
        {
            radioAll = new JRadioButton();
            radioAll.setActionCommand(sCREATE_ALL);
            radioAll.setSelected(true);
            groupOptions.add(radioAll);
        }

        JLabel labelAll = new JLabel(sCREATE_ALL, SwingConstants.LEFT);

        JPanel panelAll = new JPanel(new FlowLayout());
        panelAll.add(radioAll);
        panelAll.add(labelAll);

        panelOptions.add(panelClassOnly, constraints);
        panelOptions.add(panelToplevel, constraints);
        panelOptions.add(panelAll, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(panelPackages, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel labelEmpty = new JLabel(" ");
        getContentPane().add(labelEmpty, constraints);
        getContentPane().add(panelClasses, constraints);
        JLabel labelEmpty2 = new JLabel(" ");
        getContentPane().add(labelEmpty2, constraints);
        getContentPane().add(panelOptions, constraints);

        // Create buttons, labels and text fields
        JButton buttonOK = new JButton(sOK);
        buttonOK.setActionCommand(sOK);
        buttonOK.addActionListener(this);
        JButton buttonCancel = new JButton(sCANCEL);
        buttonCancel.setActionCommand(sCANCEL);
        buttonCancel.addActionListener(this);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(buttonOK);
        panelButtons.add(buttonCancel);
        getContentPane().add(panelButtons, constraints);

        getRootPane().setDefaultButton(buttonOK);

        // set the position of the dialog
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setBounds((int) ((toolkit.getScreenSize().getWidth() - getWidth()) / 2), 
                (int) ((toolkit.getScreenSize().getHeight() - getHeight()) / 2), iWIDTH, iHEIGHT);

        this.pack();

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setVisible(true);

    }

    /** Is called when an event occured. Computes new combobox values.
     * 
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equals(sOK))
        {
            sName=comboboxClasses.getSelectedItem().toString();
            cPackage=acPackages.get(comboboxPackages.getSelectedIndex());
            this.dispose();
        }
        else if (event.getActionCommand().equals(sCANCEL))
        {
            sName=null;
            this.dispose();
        }
        else if (event.getActionCommand().equals(sPACKAGES))
        {
            comboboxClasses.removeAllItems();

            ArrayList<NamedElement> acTemp=new ArrayList<NamedElement>();
            acTemp.add(acPackages.get(comboboxPackages.getSelectedIndex()));
            ArrayList<NamedElement> acClasses=ElementCollector.getNamedElements(null, null, Class.class, false, acTemp, true);


            ArrayList<String> asComboboxNames=new ArrayList<String>();
            for (int i=0;i<acCandidates.size();i++)
            {
                if ((ElementCollector.getNamedElementFromArrayList(acClasses,acCandidates.get(i).getName(),true,false)==ElementCollector.iNO_ELEMENT) &&
                    (!asComboboxNames.contains(acCandidates.get(i).getName())))

                {
                    asComboboxNames.add(acCandidates.get(i).getName());
                    comboboxClasses.addItem(acCandidates.get(i).getName());
                }
            }
        }
    }

    /** Returns the seletected class name
     * 
     * @return class name
     */
    public String getClassName()
    {
        return(sName);
    }

    /** Returns the selected package
     * 
     * @return package
     */
    public NamedElement getPackage()
    {
        return(cPackage);
    }
    
    /** Returns the value of the selection
     * 
     * @return integer constant matching the actual selection
     */
    public int getSettings()
    {
        if (radioToplevel.isSelected())
        {
            return(iCREATE_TOPLEVEL);
        }
        else if (radioAll.isSelected())
        {
            return(iCREATE_ALL);
        }
            
        return(iCREATE_CLASS_ONLY);
    }
}
