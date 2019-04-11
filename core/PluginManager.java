package magicUWE.core;

import static magicUWE.core.PluginManagerActions.*;
import static magicUWE.settings.GlobalConstants.*;
import static magicUWE.shared.UWEDiagramType.*;

import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import magicUWE.configurators.ToolbarConfigurator;
import magicUWE.configurators.context.*;
import magicUWE.configurators.menubar.MagicDrawMenuConfigurator;
import magicUWE.configurators.menubar.MagicUWEMenuConfigurator;
import magicUWE.settings.PropertyLoader;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeStatesNav;
import magicUWE.stereotypes.tags.*;
import magicUWE.stereotypes.tags.requirements.*;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;

/**
 * 
 * Main MagicUWE class where this Plug-in is initialized. Creates all necessary
 * configurators and GUI actions for the plug-in. Put all menu configurators in
 * the init() method to use them in MagicDraw.
 * 
 * @author PST LMU
 * 
 */

public class PluginManager extends Plugin {

	private static final Logger logger = Logger.getLogger(PluginManager.class);

	/**
	 * Initializing the MagicUWE Plug-in, all GUI elements (configurators) and
	 * associated actions are created and initialized at this place.
	 */
	@Override
	public void init() {
		logger.debug("...loading MagicUWE...");
		// Simple way to check if Plug-in is going to be loaded
		// The following message-box is shown _behind_ the spash-screen on
		// linux, be careful!
		// MessageWriter.showMessage("Loading MagicUWE now!", logger);

		// Read Properties from properties-file
		PropertyLoader.initConstants();

		// initialize project listener
		ProjectListener projectListener = new ProjectListener();
		Application.getInstance().addProjectEventListener(projectListener);
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();

		// initialize several UWE add ons for MagicDraw
		initMenus(manager);
		initContainmentTree(manager);
		initToolbar(manager);
		initContextMenus(manager);
		
		// add listener for getting events about the opened project
		Application.getInstance().addProjectEventListener(new ProjectEventChangeListener());
		logger.debug("MagicUWE was loaded.");
	}

	/**
	 * initialize the UWE menus (no contex menus)
	 * 
	 * @param manager
	 */
	private void initMenus(ActionsConfiguratorsManager manager) {
		// adding MagicUWE-submenus
		manager.addMainMenuConfigurator(new MagicUWEMenuConfigurator(getNewDiagramsSubmenuActions("New UWE Diagram",
				KeyEvent.VK_N, false)));
		manager.addMainMenuConfigurator(new MagicUWEMenuConfigurator(getTransformatorSubmenuActions()));

		// add menu to create uwe models
		manager.addMainMenuConfigurator(new MagicUWEMenuConfigurator(getModelsAction()));

		// add RIA Patterns menu
		//manager.addMainMenuConfigurator(new MagicUWEMenuConfigurator(getRIAPatternsAction()));

		// add "about MagicUWE" menu
		//manager.addMainMenuConfigurator(new MagicUWEMenuConfigurator(getAboutUweAction()));

		// add menu to MagicDraw's Diagrams main menu - be careful, the other
		// elements only LIST the available diagrams!
		// manager.addMainMenuConfigurator(new
		// MagicDrawMenuConfigurator(getDiagramsSubmenuActions(),
		// ActionsID.DIAGRAMS));

		// add menu to MagicDraw's Help menu
		manager.addMainMenuConfigurator(new MagicDrawMenuConfigurator(getHelpMenuAction(), ActionsID.HELP));
	}

	/**
	 * initialize the UWE containment tree menus
	 * 
	 * @param manager
	 */
	private void initContainmentTree(ActionsConfiguratorsManager manager) {
		// add MagicUWE Diagrams to containment browser new diagram context menu
		// if wanted in submenu called "UWE"
		if (USE_NEW_SUBMENU) {
			manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(PluginManagerActions
					.getNewDiagramsSubmenuActions("UWE", KeyEvent.VK_U, true), ActionsID.NEW_DIAGRAM, null));
		} else {
			for (UWEDiagramType dgTyp : UWEDiagramType.values()) {
				manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(PluginManagerActions
						.getNewDiagramAction("UWE - ", dgTyp, true), ActionsID.NEW_DIAGRAM, null));
			}
		}

		// add transformation menus to BrowserContext (Containment Tree context
		// menu)
		// (they are only shown if the right diagram is selected)
		manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(PluginManagerActions
				.getTransformatorBrowserContextActionContent2Navigation(), null, CONTENT));
		manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(
				getTransformatorBrowserContextActionNavigation2Presentation(), null, NAVIGATION));
		manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(
				getTransformatorBrowserContextActionNavigation2ProcessStructure(), null, NAVIGATION));
		manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(
				getTransformatorBrowserContextActionNavigation2ProcessFlows(), null, NAVIGATION));
		manager.addContainmentBrowserContextConfigurator(new BrowserContextConfigurator(PluginManagerActions
				.getTransformatorBrowserContextActionRequirements(), null, USE_CASE));
	}

	/**
	 * initialize the UWE toolbar entries
	 * 
	 * @param manager
	 */
	private void initToolbar(ActionsConfiguratorsManager manager) {
		// add class diagram toolbar actions
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarProcessAssociationActions(), NAVIGATION.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarNavigationAssociationActions(), NAVIGATION.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarNavigationClassActions(), NAVIGATION.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarProcessClassActions(), PROCESS_STRUCTURE.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarPresentationPropertyActions(), PRESENTATION.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarPresentationActions(), PRESENTATION.toString()));
		
		// add basic rights toolbar actions
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarBasicRightsActions(), BASIC_RIGHTS.toString()));

		// add activity diagram toolbar actions
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_ACTIVITY_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarProcessFlowActions(), PROCESS_FLOW.toString()));

		// add statechart diagram toolbar actions
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_STATECHART_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarStateChartActions(), NAVIGATION_STATES.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_STATECHART_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarNavigationStatesTransitionActions(), NAVIGATION_STATES.toString()));

		// add requirements toolbar actions
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_ACTIVITY_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarRequirementsActionsActions(), USE_CASE_FLOW.toString()));
		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_ACTIVITY_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarRequirementsPinsActions(), USE_CASE_FLOW.toString()));

		manager.addDiagramToolbarConfigurator(DiagramTypeConstants.UML_USECASE_DIAGRAM, new ToolbarConfigurator(
				getDiagramToolbarRequirementsUseCasesActions(), USE_CASE.toString()));
		
	}

	/**
	 * initialize the UWE context menus
	 * 
	 * @param manager
	 */
	private void initContextMenus(ActionsConfiguratorsManager manager) {
		final String classDg = DiagramTypeConstants.UML_CLASS_DIAGRAM;
		final String activityDg = DiagramTypeConstants.UML_ACTIVITY_DIAGRAM;
		// add diagram context configurator for the NAVIGATION model
		// add main navigation context-menu
		manager.addDiagramContextConfigurator(
				classDg,
				new DiagramContextConfigurator(getContextInsertMainMenuActions(UWE_NAVIGATION_DG_CONTEXT,
						"UWENaviMenuIcon"), NAVIGATION, null));

		// add the insert submenus to associations
		manager.addDiagramContextConfigurator(classDg, new DiagramContextConfiguratorForAssociations(
				getNavigationDiagramContextInsertActions(), NAVIGATION, null, UWE_NAVIGATION_DG_CONTEXT));

		// add the navigation sub-context-menus
		addNodeTags(manager, NAVIGATION, UWETagNavigationNode.values(), UWETagNavigationNode.associatedStereotype,
				UWE_NAVIGATION_DG_CONTEXT, classDg);
		addNodeTags(manager, NAVIGATION, UWETagLink.values(), UWETagLink.associatedStereotype,
				UWE_NAVIGATION_DG_CONTEXT, classDg);
		addNodeTags(manager, NAVIGATION, UWETagExternalNode.values(), UWETagExternalNode.associatedStereotype,
				UWE_NAVIGATION_DG_CONTEXT, classDg);
		addNodeTags(manager, NAVIGATION, UWETagGuidedTour.values(), UWETagGuidedTour.associatedStereotype,
				UWE_NAVIGATION_DG_CONTEXT, classDg);
		addNodeTags(manager, NAVIGATION, UWETagQuery.values(), UWETagQuery.associatedStereotype,
				UWE_NAVIGATION_DG_CONTEXT, classDg);

		
		// add diagram context configurator for the NAVIGATION_STATES model
		// add main navigation context-menu
		final String stateChart = DiagramTypeConstants.UML_STATECHART_DIAGRAM;
		manager.addDiagramContextConfigurator(stateChart, new DiagramContextConfigurator(
				getContextInsertMainMenuActions(UWE_NAV_STATES_DG_CONTEXT, "UWENaviMenuIcon"), NAVIGATION_STATES, null));

		// add sub-menu for the state machine or reverse copy of sterotypes
		manager.addDiagramContextConfigurator(stateChart, new DiagramContextConfiguratorSubmenu(
				getCopyUWESubstateMachineStereotypes(), NAVIGATION_STATES, null, UWE_NAV_STATES_DG_CONTEXT, false,
				false, true));

		// add sub-menu to set default stereotype (navigationalNode) on all
		// states nested in a state typed by a kind of <<navigationalNode>>
		// stereotype
		manager.addDiagramContextConfigurator(stateChart, new DiagramContextConfiguratorSubmenu(
				getSetNavigationalNodeToUnstereotypedSubstates(), NAVIGATION_STATES, null, UWE_NAV_STATES_DG_CONTEXT,
				false, false, false));

		// add transissionType checker
		manager.addDiagramContextConfigurator(stateChart,
				new DiagramContextConfiguratorSubmenu(getCheckSessionNodeTransmissionTypeChange(), NAVIGATION_STATES,
						UWEStereotypeStatesNav.SESSION.toString(), UWE_NAV_STATES_DG_CONTEXT, false, false, false));

		// add the navigation sub-context-menus
		addNodeTags(manager, NAVIGATION_STATES, UWETagNavigationalNode.values(),
				UWETagNavigationalNode.associatedStereotype, UWE_NAV_STATES_DG_CONTEXT, stateChart);
		addNodeTags(manager, NAVIGATION_STATES, UWETagExternalLink.values(), UWETagExternalLink.associatedStereotype,
				UWE_NAV_STATES_DG_CONTEXT, stateChart);
		addNodeTags(manager, NAVIGATION_STATES, UWETagSearch.values(), UWETagSearch.associatedStereotype,
				UWE_NAV_STATES_DG_CONTEXT, stateChart);
		addNodeTags(manager, NAVIGATION_STATES, UWETagSession.values(), UWETagSession.associatedStereotype,
				UWE_NAV_STATES_DG_CONTEXT, stateChart);
		addNodeTags(manager, NAVIGATION_STATES, UWETagTarget.values(), UWETagTarget.associatedStereotype,
				UWE_NAV_STATES_DG_CONTEXT, stateChart);

		
		// add diagram context configurator for the PRESENTATION model
		manager.addDiagramContextConfigurator(
				classDg,
				new DiagramContextConfigurator(getContextInsertMainMenuActions(UWE_PRESENTATION_DG_CONTEXT,
						"UWEPresMenuIcon"), PRESENTATION, null));

		// copy tags
		manager.addDiagramContextConfigurator(classDg, new DiagramContextConfiguratorSubmenu(getCopyUWETags(),
				PRESENTATION, null, UWE_PRESENTATION_DG_CONTEXT, true, false, false));

		// add the presentation sub-context-menus
		addNodeTags(manager, PRESENTATION, UWETagUIElement.values(), UWETagUIElement.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagInteractiveElement.values(),
				UWETagInteractiveElement.associatedStereotype, UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagPresentationGroup.values(),
				UWETagPresentationGroup.associatedStereotype, UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagIteratedPresentationGroup.values(),
				UWETagIteratedPresentationGroup.associatedStereotype, UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagTextInput.values(), UWETagTextInput.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagInputElement.values(), UWETagInputElement.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagSelection.values(), UWETagSelection.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagValuedElement.values(), UWETagValuedElement.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagOutputElement.values(), UWETagOutputElement.associatedStereotype,
				UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagPresentationElement.values(),
				UWETagPresentationElement.associatedStereotype, UWE_PRESENTATION_DG_CONTEXT, classDg);
		addNodeTags(manager, PRESENTATION, UWETagPresentationPage.values(),
				UWETagPresentationPage.associatedStereotype, UWE_PRESENTATION_DG_CONTEXT, classDg);
		
                manager.addDiagramContextConfigurator(activityDg,
				new DiagramContextConfigurator(getContextInsertMainMenuActions(UWE_PROCESS_FLOW_DG_CONTEXT,
						"UWEPresMenuIcon"), PROCESS_FLOW, null));

		// add the presentation sub-context-menus
		addNodeTags(manager, PROCESS_FLOW, UWETagSystemAction.values(),
				UWETagSystemAction.associatedStereotype, UWE_PROCESS_FLOW_DG_CONTEXT, activityDg);
		addNodeTags(manager, PROCESS_FLOW, UWETagUserAction.values(),
				UWETagUserAction.associatedStereotype, UWE_PROCESS_FLOW_DG_CONTEXT, activityDg);
                
		// add diagram context configurator for the REQUIREMENTS model
		// (USE CASE diagrams)
		// add main navigation context-menu
		final String useCase = DiagramTypeConstants.UML_USECASE_DIAGRAM;
		manager.addDiagramContextConfigurator(
				useCase,
				new DiagramContextConfigurator(getContextInsertMainMenuActions(UWE_USECASE_DG_CONTEXT,
						"UWEUseCaseMenuIcon"), USE_CASE, null));
                
		addNodeTags(manager, USE_CASE, UWETagWebUseCase.values(), UWETagWebUseCase.associatedStereotype,
				UWE_USECASE_DG_CONTEXT, useCase,false,false,false,true,true,false,false);

		// add sub-menu for the inheritance of sterotypes
		manager.addDiagramContextConfigurator(useCase, new DiagramContextConfiguratorSubmenu(
				getCopyUWEuseCaseStereotypes(), USE_CASE, null, UWE_USECASE_DG_CONTEXT, false, false, false,
                        true, false, false, false));


                manager.addDiagramContextConfigurator(activityDg,
				new DiagramContextConfigurator(getContextInsertMainMenuActions(UWE_USECASEFLOW_DG_CONTEXT,
						"UWEUseCaseMenuIcon"), USE_CASE_FLOW, null));

                manager.addDiagramContextConfigurator(activityDg, new DiagramContextConfiguratorSubmenu(getSetRequirementsType(),
				USE_CASE_FLOW, UWEStereotypeRequirementsActions.DISPLAY_ACTION.toString(), UWE_USECASEFLOW_DG_CONTEXT, 
                        false, false, false, false, false, false, true));
                manager.addDiagramContextConfigurator(activityDg, new DiagramContextConfiguratorSubmenu(getSetRequirementsType(),
				USE_CASE_FLOW, UWEStereotypeRequirementsActions.NAVIGATION_ACTION.toString(), UWE_USECASEFLOW_DG_CONTEXT, 
                        false, false, false, false, false, false, true));
                manager.addDiagramContextConfigurator(activityDg, new DiagramContextConfiguratorSubmenu(getSetRequirementsType(),
				USE_CASE_FLOW, UWEStereotypeRequirementsPins.DISPLAY_PIN_INPUT.toString(), UWE_USECASEFLOW_DG_CONTEXT, 
                        false, false, false, false, false, true, false));
                manager.addDiagramContextConfigurator(activityDg, new DiagramContextConfiguratorSubmenu(getSetRequirementsType(),
				USE_CASE_FLOW, UWEStereotypeRequirementsPins.INTERACTION_PIN_INPUT.toString(), UWE_USECASEFLOW_DG_CONTEXT, 
                        false, false, false, false, false, true, false));
		addNodeTags(manager, USE_CASE_FLOW, UWETagSystemAction.values(),
				UWETagSystemAction.associatedStereotype, UWE_USECASEFLOW_DG_CONTEXT, activityDg);
		addNodeTags(manager, USE_CASE_FLOW, UWETagUserAction.values(),
				UWETagUserAction.associatedStereotype, UWE_USECASEFLOW_DG_CONTEXT, activityDg);

		// add the presentation sub-context-menus
		addNodeTags(manager, USE_CASE_FLOW, UWETagDisplayAction.values(), UWETagDisplayAction.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,false,true);
		addNodeTags(manager, USE_CASE_FLOW, UWETagNavigationAction.values(), UWETagNavigationAction.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,false,true);
		addNodeTags(manager, USE_CASE_FLOW, UWETagRequirementsAction.values(), UWETagRequirementsAction.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,false,true);
		addNodeTags(manager, USE_CASE_FLOW, UWETagDisplayPin.values(), UWETagDisplayPin.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,true,false);
		addNodeTags(manager, USE_CASE_FLOW, UWETagInteractionPin.values(), UWETagInteractionPin.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,true,false);
		addNodeTags(manager, USE_CASE_FLOW, UWETagPresentationPin.values(), UWETagPresentationPin.associatedStereotype,
				UWE_USECASEFLOW_DG_CONTEXT, activityDg,false,false,false,false,false,true,false);
                


		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextConfiguratorSelect(
				getContentDiagramContextTransformationActions(), CONTENT, null,false,true,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getContentModelInsertionActions(),
                                CONTENT, UWE_CONTENT_DG_TRANSFORMATIONS,null,false,false,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getContentModelModificationActions(),
                                CONTENT, UWE_CONTENT_DG_TRANSFORMATIONS,null,false,true,false));
                
                manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextConfiguratorSelect(
				getUserModelDiagramContextTransformationActions(), USER_MODEL, null,false,true,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getContentModelInsertionActions(),
                                USER_MODEL, UWE_USER_MODEL_DG_TRANSFORMATIONS,null,false,false,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getContentModelModificationActions(),
                                USER_MODEL, UWE_USER_MODEL_DG_TRANSFORMATIONS,null,false,true,false));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextConfiguratorSelect(
				getNavigationDiagramContextTransformationActions(), NAVIGATION, null,false,true,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getNavigationModelInsertionActions(),
                                NAVIGATION, UWE_NAVIGATION_DG_TRANSFORMATIONS,null,false,false,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getNavigationModelModificationActions(),
                                NAVIGATION, UWE_NAVIGATION_DG_TRANSFORMATIONS,UWEStereotypeClassNav.NAVIGATION_CLASS.toString(),false,true,false));
		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getNavigationModelModificationActions(),
                                NAVIGATION, UWE_NAVIGATION_DG_TRANSFORMATIONS,UWEStereotypeClassGeneral.PROCESS_CLASS.toString(),false,true,false));
                
                
		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextConfiguratorSelect(
				getProcessDiagramContextTransformationActions(), PROCESS_STRUCTURE, null,false,true,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getProcessModelInsertionActions(),
                                PROCESS_STRUCTURE, UWE_PROCESS_DG_TRANSFORMATIONS,null,false,false,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getProcessModelModificationActions(),
                                PROCESS_STRUCTURE, UWE_PROCESS_DG_TRANSFORMATIONS,null,false,true,false));
                
                
		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM, new DiagramContextConfiguratorSelect(
				getPresentationDiagramContextTransformationActions(), PRESENTATION, null,true,true,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getPresentationModelInsertionActions(),
                                PRESENTATION, UWE_PRESENTATION_DG_TRANSFORMATIONS,null,false,false,true));

		manager.addDiagramContextConfigurator(DiagramTypeConstants.UML_CLASS_DIAGRAM,
				new DiagramContextConfiguratorSelectSub(getPresentationModelModificationActions(),
                                PRESENTATION, UWE_PRESENTATION_DG_TRANSFORMATIONS,null,true,true,false));
	}

	/**
	 * Add Boolean and String entries of NodeTags to the context menu of a
	 * diagram
	 * 
	 * @param manager
	 * @param dgType
	 * @param nodeTags
	 * @param onStereotyp
	 * @param addToThisMenu
	 * @param kindOfMDdiagram
	 */
	private static void addNodeTags(ActionsConfiguratorsManager manager, UWEDiagramType dgType, NodeTag[] nodeTags,
			String onStereotyp, String addToThisMenu, String kindOfMDdiagram) {
		// add tagged values of a boolean type
		manager.addDiagramContextConfigurator(kindOfMDdiagram, new DiagramContextConfiguratorWithMenuStatesForTags(
				getDiagramContextBooleanTagActions(nodeTags, dgType), dgType, onStereotyp, addToThisMenu));

		// add tagged values of a String type
		manager.addDiagramContextConfigurator(kindOfMDdiagram, new DiagramContextConfiguratorSubmenu(
				getDiagramContextStringTagActions(nodeTags, dgType), dgType, onStereotyp, addToThisMenu, true, true, false));
	}
        
        private static void addNodeTags(ActionsConfiguratorsManager manager, UWEDiagramType dgType, NodeTag[] nodeTags,
			String onStereotyp, String addToThisMenu, String kindOfMDdiagram,boolean displayOnProperty,
			boolean displayOnClass, boolean displayOnSubMachineStateNotState,
                        boolean displayOnPackage, boolean displayOnUseCase, boolean displayOnPin, boolean displayOnAction) {
		// add tagged values of a boolean type
		manager.addDiagramContextConfigurator(kindOfMDdiagram, new DiagramContextConfiguratorWithMenuStatesForTags(
				getDiagramContextBooleanTagActions(nodeTags, dgType), dgType, onStereotyp, addToThisMenu));

		// add tagged values of a String type
		manager.addDiagramContextConfigurator(kindOfMDdiagram, new DiagramContextConfiguratorSubmenu(
				getDiagramContextStringTagActions(nodeTags, dgType), dgType, onStereotyp, addToThisMenu, 
                        displayOnProperty, displayOnClass, displayOnSubMachineStateNotState,displayOnPackage,displayOnUseCase,displayOnPin,displayOnAction));
	}


	/**
	 * Return always true, because this Plug-in does not have any close specific
	 * actions.
	 */
	@Override
	public boolean close() {
		return true;
	}

	/**
	 * @see Plugin#isSupported()
	 */
	@Override
	public boolean isSupported() {
		return true;
	}
	
	/**
	 * get Icon
	 * @param path
	 * @return icon
	 */
	public static Icon getIcon(String path) {
		URL url = PluginManagerActions.class.getResource(path);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}
}
