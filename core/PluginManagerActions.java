package magicUWE.core;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import magicUWE.actions.NewDiagramMenuAction;
import magicUWE.actions.TransformatorAction;
import magicUWE.actions.context.DiagramContextBooleanTagAction;
import magicUWE.actions.context.DiagramContextInheritUseCasePackageStereotypesAction;
import magicUWE.actions.context.DiagramContextPropertyCopyVsClassAction;
import magicUWE.actions.context.DiagramContextSetDefaultNavStateStereotype;
import magicUWE.actions.context.DiagramContextStringTagAction;
import magicUWE.actions.context.DiagramContextSubmachineStateCopyVsStateMachineAction;
import magicUWE.actions.context.NavDiagramContextAssocAction;
import magicUWE.actions.context.requirements.ContentModelInsertion;
import magicUWE.actions.context.requirements.ContentModelModification;
import magicUWE.actions.context.requirements.NavigationModelInsertion;
import magicUWE.actions.context.requirements.NavigationModelModification;
import magicUWE.actions.context.requirements.PresentationModelInsertion;
import magicUWE.actions.context.requirements.PresentationModelModification;
import magicUWE.actions.context.requirements.ProcessModelInsertion;
import magicUWE.actions.context.requirements.ProcessModelModification;
import magicUWE.actions.context.requirements.SetRequirementsElementTypeAction;
import magicUWE.actions.context.requirements.TransformationsListener;
import magicUWE.actions.context.sessionTransmissionCheck.CheckSessionTransmissionType;
import magicUWE.actions.menubar.AboutUweAction;
import magicUWE.actions.menubar.ModelAction;
import magicUWE.actions.menubar.RIAPatternsAction;
import magicUWE.actions.menubar.ShowHelpAction;
import magicUWE.actions.toolbar.DrawAssociationAction;
import magicUWE.actions.toolbar.DrawBasicRightsDependencyAction;
import magicUWE.actions.toolbar.DrawElementAction;
import magicUWE.actions.toolbar.DrawPropertyAction;
import magicUWE.actions.toolbar.DrawTransitionAction;
import magicUWE.riaPatterns.RIATagsHelper;
import magicUWE.settings.GlobalConstants;
import magicUWE.shared.UWEDiagramType;
import magicUWE.stereotypes.UWEStereotypeAssoc;
import magicUWE.stereotypes.UWEStereotypeBasicRightsDependencies;
import magicUWE.stereotypes.UWEStereotypeClassGeneral;
import magicUWE.stereotypes.UWEStereotypeClassNav;
import magicUWE.stereotypes.UWEStereotypeClassPres;
import magicUWE.stereotypes.UWEStereotypeOfElWithSecondKey;
import magicUWE.stereotypes.UWEStereotypeOfElement;
import magicUWE.stereotypes.UWEStereotypeProcessFlow;
import magicUWE.stereotypes.UWEStereotypeStatesNav;
import magicUWE.stereotypes.UWEStereotypeTransitions;
import magicUWE.stereotypes.UWEStereotypeWithKey;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsActions;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsPins;
import magicUWE.stereotypes.requirements.UWEStereotypeRequirementsUseCases;
import magicUWE.stereotypes.tags.NodeTag;
import magicUWE.transformation.TransformationType;

import org.apache.log4j.Logger;

import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DrawShapeDiagramAction;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;

/**
 * Methods called from {@link PluginManager#init()}. Basically the menu
 * configurators (they say how the menu is displayed) and the
 * menu-action-listener (they say what should happen when you click at a menu)
 * are linked together.
 * 
 * @author PST LMU
 */
abstract class PluginManagerActions {

	private static ActionsCategory newUWEDiagramMenu = null;
	private static final String iconsPath = "icons/";
	private static final String iconsRequirementsPath = "requirements/";
	private static final String iconSuffix = ".png";
	private static final String stereotypeIconPath = "stereotypes/";
	private static final String transformationIconPath = "transformations/";

	protected static final Logger logger = Logger.getLogger(PluginManagerActions.class);

	/**
	 * Creates a submenu with new-UWE-Diagram entries.
	 * 
	 * @param diagramsSubMenuName
	 *            Label of the menu
	 * @param keyEvent
	 *            for menu
	 * @param isBrowserContextMenu
	 *            true <=> construction for containment tree context menu
	 * 
	 * @return category
	 */
	static NMAction getNewDiagramsSubmenuActions(String diagramsSubMenuName, int keyEvent, boolean isBrowserContextMenu) {
		newUWEDiagramMenu = new ActionsCategory(diagramsSubMenuName, diagramsSubMenuName, keyEvent,
				ActionsGroups.PROJECT_OPENED_RELATED);
		setIcon(newUWEDiagramMenu, iconsPath + "MDUWEclassDiagram" + iconSuffix);

		// Submenus
		newUWEDiagramMenu.setNested(true);
		for (UWEDiagramType dgTyp : UWEDiagramType.values()) {
			NewDiagramMenuAction diagramAction = getNewDiagramAction("", dgTyp, isBrowserContextMenu);
			newUWEDiagramMenu.addAction(diagramAction);
		}
		return newUWEDiagramMenu;
	}

	/**
	 * newDiagram Menu (suffix is " Diagram")
	 * 
	 * @param strPrefix
	 * @param dgTyp
	 * @param isBrowserContextMenu
	 *            true <=> construction for containment tree context menu
	 * @return newDiagram with type dgType
	 */
	static NewDiagramMenuAction getNewDiagramAction(String strPrefix, UWEDiagramType dgTyp, boolean isBrowserContextMenu) {
		NewDiagramMenuAction diagramAction = new NewDiagramMenuAction(strPrefix + dgTyp.toString() + " Diagram",
				ActionsGroups.PROJECT_OPENED_RELATED, isBrowserContextMenu);
		// use the right icon according to the diagram type
		if (dgTyp.umlDiagramType == DiagramTypeConstants.UML_ACTIVITY_DIAGRAM) {
			setIcon(diagramAction, iconsPath + "MDactivityDiagram" + iconSuffix);
		} else if (dgTyp.umlDiagramType == DiagramTypeConstants.UML_STATECHART_DIAGRAM) {
			setIcon(diagramAction, iconsPath + "MDstatechartDiagram" + iconSuffix);
		} else if (dgTyp.umlDiagramType == DiagramTypeConstants.UML_USECASE_DIAGRAM) {
			setIcon(diagramAction, iconsPath + "MDusecaseDiagram" + iconSuffix);
		} else {
			setIcon(diagramAction, iconsPath + "MDclassDiagram" + iconSuffix);
		}
		return diagramAction;
	}

	/**
	 * creates the UWE menu in MagicDraw's Help menu
	 * 
	 * @return category
	 */
	static NMAction getHelpMenuAction() {
		ActionsCategory category = new ActionsCategory("", "");
		ShowHelpAction help = new ShowHelpAction("Visit the MagicUWE website", KeyEvent.VK_U,
				ActionsGroups.APPLICATION_RELATED);
		setIcon(help, iconsPath + "UWEicon" + iconSuffix);
		category.addAction(help);
		return category;
	}

	/**
	 * Gets all UWE transformations into one category. (submenus)
	 * 
	 * @return category
	 */
	static NMAction getTransformatorSubmenuActions() {
		final String TRANSFORMATION_SUB_MENU_NAME = "Transformations";
		ActionsCategory category = new ActionsCategory(TRANSFORMATION_SUB_MENU_NAME, TRANSFORMATION_SUB_MENU_NAME,
				KeyEvent.VK_T, ActionsGroups.DIAGRAM_OPENED_RELATED);
		category.setNested(true);
		setIcon(category, iconsPath + transformationIconPath + "Transformation" + iconSuffix);

//		category.addAction(getTransformationReq2Content(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationReq2Navigation(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationReq2Process(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationReq2Presentation(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//
//		category.addAction(getTransformationContent2Navigation(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationNavigation2Presentation(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationNavigation2ProcessStructure(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
//		category.addAction(getTransformationNavigation2ProcessFlows(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
		category.addAction(getTransformationContent2JavaCode(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
		category.addAction(getTransformationPresentation2JavaCode(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
		category.addAction(getTransformationProcess2JavaCode(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
		category.addAction(getTransformationNavigation2JavaCode(ActionsGroups.DIAGRAM_OPENED_RELATED, "", false));
		return category;
	}

	/**
	 * @return TransformatorAction for the Content2Navigation transformation
	 *         from the containment tree context menu
	 */
	static NMAction getTransformatorBrowserContextActionRequirements() {
		ActionsCategory category = new ActionsCategory("", "", null, ActionsGroups.PROJECT_OPENED_RELATED);
		category.addAction(getTransformationReq2Content(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ", true));
		category.addAction(getTransformationReq2Navigation(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ", true));
		category.addAction(getTransformationReq2Process(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ", true));
		category.addAction(getTransformationReq2Presentation(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ", true));
		return category;
	}

	/**
	 * @return TransformatorAction for the Content2Navigation transformation
	 *         from the containment tree context menu
	 */
	static NMAction getTransformatorBrowserContextActionContent2Navigation() {
		ActionsCategory category = new ActionsCategory("", "", null, ActionsGroups.PROJECT_OPENED_RELATED);
		category.addAction(getTransformationContent2Navigation(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ", true));
		return category;
	}

	/**
	 * @return TransformatorAction for the Navigation2Presentation
	 *         transformation from the containment tree context menu
	 */
	static NMAction getTransformatorBrowserContextActionNavigation2Presentation() {
		ActionsCategory category = new ActionsCategory("", "", null, ActionsGroups.PROJECT_OPENED_RELATED);
		category.addAction(getTransformationNavigation2Presentation(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ",
				true));
		return category;
	}

	/**
	 * @return TransformatorAction for the Navigation2Process_Structure
	 *         transformation from the containment tree context menu
	 */
	static NMAction getTransformatorBrowserContextActionNavigation2ProcessStructure() {
		ActionsCategory category = new ActionsCategory("", "", null, ActionsGroups.PROJECT_OPENED_RELATED);
		category.addAction(getTransformationNavigation2ProcessStructure(ActionsGroups.PROJECT_OPENED_RELATED,
				"MagicUWE: ", true));
		return category;
	}

	/**
	 * @return TransformatorAction for the Navigation2Process_Flow
	 *         transformation from the containment tree context menu
	 */
	static NMAction getTransformatorBrowserContextActionNavigation2ProcessFlows() {
		ActionsCategory category = new ActionsCategory("", "", null, ActionsGroups.PROJECT_OPENED_RELATED);
		category.addAction(getTransformationNavigation2ProcessFlows(ActionsGroups.PROJECT_OPENED_RELATED, "MagicUWE: ",
				true));
		return category;
	}

	/**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Content2Navigation transformation
	 */
	private static TransformatorAction getTransformationContent2Navigation(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "content to navigation" transformation
		TransformatorAction content2NavigationAction = new TransformatorAction(TransformationType.CONTENT_2_NAVIGATION,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(content2NavigationAction, iconsPath + transformationIconPath + "TransformationContentToNavigation"
				+ iconSuffix);
		return content2NavigationAction;
	}

	/**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Content2Navigation transformation
	 */
	private static TransformatorAction getTransformationReq2Content(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "content to navigation" transformation
		TransformatorAction toContentAction = new TransformatorAction(TransformationType.REQUIREMENTS_2_CONTENT,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(toContentAction, iconsPath + transformationIconPath + "TransformationReqToCon"
				+ iconSuffix);
		return toContentAction;
	}

        /**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Content2Navigation transformation
	 */
	private static TransformatorAction getTransformationReq2Navigation(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "content to navigation" transformation
		TransformatorAction toNavigationAction = new TransformatorAction(TransformationType.REQUIREMENTS_2_NAVIGATION,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(toNavigationAction, iconsPath + transformationIconPath + "TransformationReqToNav"
				+ iconSuffix);
		return toNavigationAction;
	}

        /**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Content2Navigation transformation
	 */
	private static TransformatorAction getTransformationReq2Process(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "content to navigation" transformation
		TransformatorAction toProcessAction = new TransformatorAction(TransformationType.REQUIREMENTS_2_PROCESS,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(toProcessAction, iconsPath + transformationIconPath + "TransformationReqToPro"
				+ iconSuffix);
		return toProcessAction;
	}

        /**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Content2Navigation transformation
	 */
	private static TransformatorAction getTransformationReq2Presentation(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "content to navigation" transformation
		TransformatorAction toProcessAction = new TransformatorAction(TransformationType.REQUIREMENTS_2_PRESENTATION,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(toProcessAction, iconsPath + transformationIconPath + "TransformationReqToPre"
				+ iconSuffix);
		return toProcessAction;
	}

	/**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Navigation2Presentation
	 *         transformation
	 */
	private static TransformatorAction getTransformationNavigation2Presentation(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "navigation to presentation" transformation
		TransformatorAction navigation2Presentation = new TransformatorAction(
				TransformationType.NAVIGATION_2_PRESENTATION, actionsGroups, prefix,
				openSelectedDiagramFromContainmentTree);
		setIcon(navigation2Presentation, iconsPath + transformationIconPath + "TransformationNavigationToPresentation"
				+ iconSuffix);
		return navigation2Presentation;
	}

	/**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Navigation2Process_Structure
	 *         transformation
	 */
	private static TransformatorAction getTransformationNavigation2ProcessStructure(String actionsGroups,
			String prefix, boolean openSelectedDiagramFromContainmentTree) {
		// add "navigation to process structure" transformation
		TransformatorAction navigation2Process = new TransformatorAction(
				TransformationType.NAVIGATION_2_PROCESS_STRUCTURE, actionsGroups, prefix,
				openSelectedDiagramFromContainmentTree);
		setIcon(navigation2Process, iconsPath + transformationIconPath + "TransformationNavigationToProcessStructure"
				+ iconSuffix);
		return navigation2Process;
	}

	/**
	 * @param actionsGroups
	 * @param prefix
	 * @param openSelectedDiagramFromContainmentTree
	 * @return TransformatorAction for the Navigation2Process_Flows
	 *         transformation
	 */
	private static TransformatorAction getTransformationNavigation2ProcessFlows(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree) {
		// add "navigation to process flows" transformation
		TransformatorAction navigation2Process = new TransformatorAction(TransformationType.NAVIGATION_2_PROCESS_FLOWS,
//		TransformatorAction navigation2Process = new TransformatorAction(TransformationType.CONTENT_2_PROCESS_FLOWS,
				actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(navigation2Process, iconsPath + transformationIconPath + "TransformationNavigationToProcessFlows"
				+ iconSuffix);
		return navigation2Process;
	}

	private static TransformatorAction getTransformationContent2JavaCode(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree){
		TransformatorAction content2JavaCode = new TransformatorAction(TransformationType.CONTENT_2_JAVACODE, actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(content2JavaCode, "");
		return content2JavaCode;
	}
	private static TransformatorAction getTransformationProcess2JavaCode(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree){
		TransformatorAction Pro2JavaCode = new TransformatorAction(TransformationType.PROCESS_2_JAVACODE, actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(Pro2JavaCode, "");
		return Pro2JavaCode;
	}
	private static TransformatorAction getTransformationNavigation2JavaCode(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree){
		TransformatorAction nav2JavaCode = new TransformatorAction(TransformationType.NAVIGATION_2_JAVACODE, actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(nav2JavaCode, "");
		return nav2JavaCode;
	}
	
	private static TransformatorAction getTransformationPresentation2JavaCode(String actionsGroups, String prefix,
			boolean openSelectedDiagramFromContainmentTree){
		TransformatorAction presentation2JavaCode = new TransformatorAction(TransformationType.PRESENTATION_2_JAVACODE, actionsGroups, prefix, openSelectedDiagramFromContainmentTree);
		setIcon(presentation2JavaCode, "");
		return presentation2JavaCode;
	}
	/**
	 * Returns the "create UWE default Models" sub menu.
	 * 
	 * @return NMAction
	 */
	static NMAction getModelsAction() {
		ActionsCategory category = new ActionsCategory("", "");
		ModelAction models = new ModelAction("Create Default Models", KeyEvent.VK_P,
				ActionsGroups.PROJECT_OPENED_RELATED);
		setIcon(models, iconsPath + "MDUWEmodels" + iconSuffix);
		category.addAction(models);
		return category;
	}

	/**
	 * Returns the "RIA Patterns" sub menu
	 * 
	 * @return NMAction
	 */
	static NMAction getRIAPatternsAction() {
		ActionsCategory category = new ActionsCategory("", "");
		RIAPatternsAction ria = new RIAPatternsAction("RIA Patterns Options", KeyEvent.VK_R, ActionsGroups.APPLICATION_RELATED);
		setIcon(ria, iconsPath + "UWERiaPatterns" + iconSuffix);
		category.addAction(ria);
		return category;
	}

	/**
	 * Returns the "about MagicUWE" sub menu.
	 * 
	 * @return category
	 */
	static NMAction getAboutUweAction() {
		ActionsCategory category = new ActionsCategory("", "");
		AboutUweAction about = new AboutUweAction("About MagicUWE", KeyEvent.VK_A, ActionsGroups.APPLICATION_RELATED);
		setIcon(about, iconsPath + "UWEicon" + iconSuffix);
		category.addAction(about);
		return category;
	}

	/**
	 * Retrieves all UWE navigation class actions based on the stereotypes in
	 * the UWEProfile.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarNavigationClassActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeClassNav.values()) {
			addDrawElementShapeDiagramAction(category, ster, "navigation");
		}
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE navigation association actions based on the stereotypes
	 * in the UWEProfile.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarNavigationAssociationActions() {
		ActionsCategory category = new ActionsCategory("", "");
		addDrawAssociationAction(category, UWEStereotypeAssoc.NAVIGATION_LINK, false);
		addDrawAssociationAction(category, UWEStereotypeAssoc.NAVIGATION_LINK_DIRECTED, true);
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE navigation states transitions actions based on the
	 * stereotypes in the UWEProfile.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarNavigationStatesTransitionActions() {
		ActionsCategory category = new ActionsCategory("", "");
		DrawTransitionAction assoc = new DrawTransitionAction(UWEStereotypeTransitions.SEARCH);
		setIcon(assoc, iconsPath + stereotypeIconPath + "navigation/" + UWEStereotypeTransitions.SEARCH + iconSuffix);
		category.addAction(assoc);

		addDrawTransitionAction(category, UWEStereotypeTransitions.INTEGRATED_MENU);
		addDrawTransitionAction(category, UWEStereotypeTransitions.FROM_COLLECTION);
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE presentation diagram elements needed for UWE diagram
	 * modeling.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarPresentationActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeClassPres ster : UWEStereotypeClassPres.values()) {
			if (ster.getShouldBeUsedAsClass()) {
				addDrawElementShapeDiagramAction(category, ster, "presentation");
			}
		}
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE presentation diagram Property elements needed for UWE
	 * diagram modeling.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarPresentationPropertyActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeClassPres ster : UWEStereotypeClassPres.values()) {
			if (ster.getShouldBeUsedAsProperty()) {
				addDrawPropertyShapeDiagramAction(category, ster, "presentation");
			}
		}
		return convertActionsToGenericCol(category.getActions());
	}
	
	/**
	 * Retrieves all UWE basic rights diagram Property elements needed for UWE
	 * diagram modeling.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarBasicRightsActions() {
		ActionsCategory category = new ActionsCategory("", "");
		DrawBasicRightsDependencyAction action = new DrawBasicRightsDependencyAction("select dependency", null, null);
		setIcon(action, iconsPath + "dependencies/dependencySelect" + iconSuffix);
		category.addAction(action);
		
		// MAYBE read keys from property files (Right now I'm in a hurry)
		
		for (UWEStereotypeBasicRightsDependencies ster : UWEStereotypeBasicRightsDependencies.values()) {
			action = new DrawBasicRightsDependencyAction(ster.toString(), null, ster);
			setIcon(action, iconsPath + "dependencies/" + ster.toString() + iconSuffix);
			category.addAction(action);
		}
		
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE process flow elements needed for UWE diagram modeling.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarProcessFlowActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeProcessFlow.values()) {
			addDrawElementShapeDiagramAction(category, ster, "process");
		}
		return convertActionsToGenericCol(category.getActions());
	}

        /**
	 * Retrieves all UWE requirement actions elements needed for UWE diagram modeling.
	 *
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarRequirementsActionsActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeRequirementsActions.values()) {
			addDrawElementShapeDiagramAction(category, ster, "requirements");
		}
		return convertActionsToGenericCol(category.getActions());
	}

         /**
	 * Retrieves all UWE requirements pins needed for UWE diagram modeling.
	 *
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarRequirementsPinsActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeRequirementsPins.values()) {
			addDrawElementShapeDiagramAction(category, ster, "requirements");
		}
		return convertActionsToGenericCol(category.getActions());
	}

         /**
	 * Retrieves all UWE requirements use cases needed for UWE diagram modeling.
	 *
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarRequirementsUseCasesActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeRequirementsUseCases.values()) {
			addDrawElementShapeDiagramAction(category, ster, "requirements");
		}
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE statechart elements needed for UWE diagram modeling.
	 * 
	 * @return actions
	 */
	static Collection<NMAction> getDiagramToolbarStateChartActions() {
		ActionsCategory category = new ActionsCategory("", "");
		for (UWEStereotypeOfElement ster : UWEStereotypeStatesNav.values()) {
			addDrawElementShapeDiagramAction(category, ster, "navigation");
		}
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * adds {@link DrawElementAction} to given category and loads icon
	 * 
	 * @param category
	 * @param ster
	 * @param iconFolder
	 */
	private static void addDrawElementShapeDiagramAction(ActionsCategory category, UWEStereotypeOfElement ster,
			String iconFolder) {
		DrawShapeDiagramAction dsda = new DrawElementAction(ster);
		setIcon(dsda, iconsPath + stereotypeIconPath + iconFolder + "/" + ster.toString() + iconSuffix);
		category.addAction(dsda);
	}

	/**
	 * adds {@link DrawPropertyAction} to given category and loads icon
	 * 
	 * @param category
	 * @param ster
	 * @param iconFolder
	 */
	private static void addDrawPropertyShapeDiagramAction(ActionsCategory category,
			UWEStereotypeOfElWithSecondKey ster, String iconFolder) {
		DrawShapeDiagramAction dsda = new DrawPropertyAction(ster);
		setIcon(dsda, iconsPath + stereotypeIconPath + iconFolder + "/" + ster.toString() + iconSuffix);
		category.addAction(dsda);
	}

	/**
	 * Retrieves all UWE process class diagram elements.
	 * 
	 * @return DiagramToolbarProcessActions
	 */
	static Collection<NMAction> getDiagramToolbarProcessClassActions() {
		ActionsCategory category = new ActionsCategory("", "");
		UWEStereotypeClassGeneral ster = UWEStereotypeClassGeneral.PROCESS_CLASS;
		DrawElementAction proc = new DrawElementAction(ster);
		setIcon(proc, iconsPath + stereotypeIconPath + "process/" + ster.toString() + iconSuffix);
		category.addAction(proc);
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * Retrieves all UWE process association diagram elements.
	 * 
	 * @return DiagramToolbarProcessActions
	 */
	static Collection<NMAction> getDiagramToolbarProcessAssociationActions() {
		ActionsCategory category = new ActionsCategory("", "");
		addDrawAssociationAction(category, UWEStereotypeAssoc.PROCESS_LINK, false);
		addDrawAssociationAction(category, UWEStereotypeAssoc.PROCESS_LINK_DIRECTED, true);
		return convertActionsToGenericCol(category.getActions());
	}

	/**
	 * adds new {@link DrawAssociationAction} to given category and loads icon
	 * 
	 * @param category
	 *            to which the new Action will be added
	 * @param ster
	 *            Stereotype
	 * @param isDirected
	 */
	private static void addDrawAssociationAction(ActionsCategory category, UWEStereotypeWithKey ster, boolean isDirected) {
		DrawAssociationAction assoc = new DrawAssociationAction(ster, isDirected);
		if (isDirected) {
			setIcon(assoc, iconsPath + "MDassociationDirected" + iconSuffix);
		} else {
			setIcon(assoc, iconsPath + "MDassociation" + iconSuffix);
		}
		category.addAction(assoc);
	}

	/**
	 * adds new {@link DrawTransitionAction} to given category and loads icon
	 * 
	 * @param category
	 * @param ster
	 */
	private static void addDrawTransitionAction(ActionsCategory category, UWEStereotypeWithKey ster) {
		DrawTransitionAction assoc = new DrawTransitionAction(ster);
		setIcon(assoc, iconsPath + "MDassociationDirected" + iconSuffix);
		category.addAction(assoc);
	}

	/**
	 * Creates a diagram's context main UWE menu
	 * @param dgContext
	 * @param icon
	 * @return actions
	 */
	static ActionsCategory getContextInsertMainMenuActions(String dgContext, String icon) {
		ActionsCategory category = new ActionsCategory(dgContext, dgContext);
		setIcon(category, iconsPath + icon + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}


        static ActionsCategory getContentDiagramContextTransformationActions() {
		ActionsCategory category =
				new ActionsCategory(GlobalConstants.UWE_CONTENT_DG_TRANSFORMATIONS,GlobalConstants.UWE_CONTENT_DG_TRANSFORMATIONS);
		setIcon(category, iconsPath + "UWEicon" + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}

        static ActionsCategory getUserModelDiagramContextTransformationActions() {
		ActionsCategory category =
				new ActionsCategory(GlobalConstants.UWE_USER_MODEL_DG_TRANSFORMATIONS,GlobalConstants.UWE_USER_MODEL_DG_TRANSFORMATIONS);
		setIcon(category, iconsPath + "UWEicon" + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}

        static ActionsCategory getContentModelInsertionActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		ContentModelInsertion insertClass =
                    new ContentModelInsertion(ContentModelInsertion.sACTION_TITLE,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertClass);

                Application.getInstance().addProjectEventListener(TransformationsListener.getListener());
                
		return category;
	}

        static ActionsCategory getContentModelModificationActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		ContentModelModification addAttributes =
                    new ContentModelModification(ContentModelModification.sACTION_ADD_ATTRIBUTES,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addAttributes);

		ContentModelModification addAssociations =
                    new ContentModelModification(ContentModelModification.sACTION_ADD_ASSOCIATIONS,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addAssociations);

		ContentModelModification addBoth =
                    new ContentModelModification(ContentModelModification.sACTION_ADD_ATTRIBUTES_AND_ASSOCIATIONS,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addBoth);

		return category;
	}


        static ActionsCategory getNavigationDiagramContextTransformationActions() {
		ActionsCategory category =
				new ActionsCategory(GlobalConstants.UWE_NAVIGATION_DG_TRANSFORMATIONS,GlobalConstants.UWE_NAVIGATION_DG_TRANSFORMATIONS);
		setIcon(category, iconsPath + "UWEicon" + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}

        static ActionsCategory getNavigationModelInsertionActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		NavigationModelInsertion insertNavigationClass =
                    new NavigationModelInsertion(NavigationModelInsertion.sACTION_NAVIGATION,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertNavigationClass);
		NavigationModelInsertion insertProcessClass =
                    new NavigationModelInsertion(NavigationModelInsertion.sACTION_PROCESS,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertProcessClass);

		return category;
	}

        static ActionsCategory getNavigationModelModificationActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		NavigationModelModification addLinksMenu =
                    new NavigationModelModification(NavigationModelModification.sACTION_ADD_LINKS_AND_MENU,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addLinksMenu);
                
		NavigationModelModification addLinks =
                    new NavigationModelModification(NavigationModelModification.sACTION_ADD_LINKS,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addLinks);

		return category;
	}

        static ActionsCategory getProcessDiagramContextTransformationActions() {
		ActionsCategory category =
				new ActionsCategory(GlobalConstants.UWE_PROCESS_DG_TRANSFORMATIONS,
                        GlobalConstants.UWE_PROCESS_DG_TRANSFORMATIONS);
		setIcon(category, iconsPath + "UWEicon" + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}

        static ActionsCategory getProcessModelInsertionActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		ProcessModelInsertion insertClass =
                    new ProcessModelInsertion(ProcessModelInsertion.sACTION_TITLE,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertClass);
                
		return category;
	}

        static ActionsCategory getProcessModelModificationActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		ProcessModelModification addWorkflow =
                    new ProcessModelModification(ProcessModelModification.sACTION_ADD_WORKFLOW,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addWorkflow);

		return category;
	}

        static ActionsCategory getPresentationDiagramContextTransformationActions() {
		ActionsCategory category =
				new ActionsCategory(GlobalConstants.UWE_PRESENTATION_DG_TRANSFORMATIONS,
                        GlobalConstants.UWE_PRESENTATION_DG_TRANSFORMATIONS);
		setIcon(category, iconsPath + "UWEicon" + iconSuffix);
		category.setNested(true); // Main navigation context menu
		return category;
	}

        static ActionsCategory getPresentationModelInsertionActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

		PresentationModelInsertion insertGroup =
                    new PresentationModelInsertion(PresentationModelInsertion.sACTION_GROUP,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertGroup);
                
		PresentationModelInsertion insertForm =
                    new PresentationModelInsertion(PresentationModelInsertion.sACTION_FORM,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(insertForm);
                
		return category;
	}

        static ActionsCategory getPresentationModelModificationActions()
        {
		ActionsCategory category = new ActionsCategory("", "");

                PresentationModelModification addAllProps =
                    new PresentationModelModification(PresentationModelModification.sACTION_ADD_PROPERTIES,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addAllProps);
                PresentationModelModification addToplevelProps =
                    new PresentationModelModification(PresentationModelModification.sACTION_ADD_PROPERTIES_TOPLEVEL,
                    null,ActionsGroups.DIAGRAM_EDIT_RELATED);
		category.addAction(addToplevelProps);

		return category;
	}

	/**
	 * @return "insert Menu" and "-Query" and "-Index" menu for the Navigation
	 *         diagram context menu
	 */
	static ActionsCategory getNavigationDiagramContextInsertActions() {
		// Submenus of UWE_NAVIGATION_DG_CONTEXT
		ActionsCategory category = new ActionsCategory("", "");
		final String insertFolder = "insert/";

		final String ins = "Insert ";
		// Insert query
		NavDiagramContextAssocAction insQuery = new NavDiagramContextAssocAction(ins
				+ UWEStereotypeClassNav.QUERY.toString(), null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED,
				UWEStereotypeClassNav.QUERY);
		setIcon(insQuery, iconsPath + insertFolder + "UWEinsertQuery" + iconSuffix);
		category.addAction(insQuery);

		// Insert index
		NavDiagramContextAssocAction insIndex = new NavDiagramContextAssocAction(ins
				+ UWEStereotypeClassNav.INDEX.toString(), null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED,
				UWEStereotypeClassNav.INDEX);
		setIcon(insIndex, iconsPath + insertFolder + "UWEinsertIndex" + iconSuffix);
		category.addAction(insIndex);

		// Insert guided tour
		NavDiagramContextAssocAction insGuidedTour = new NavDiagramContextAssocAction(ins
				+ UWEStereotypeClassNav.GUIDEDTOUR.toString(), null,
				ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED, UWEStereotypeClassNav.GUIDEDTOUR);
		setIcon(insGuidedTour, iconsPath + insertFolder + "UWEinsertGuidedTour" + iconSuffix);
		category.addAction(insGuidedTour);

		// Insert menu
		NavDiagramContextAssocAction insMenu = new NavDiagramContextAssocAction(ins
				+ UWEStereotypeClassNav.MENU.toString(), null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED,
				UWEStereotypeClassNav.MENU);
		setIcon(insMenu, iconsPath + insertFolder + "UWEinsertMenu" + iconSuffix);
		category.addAction(insMenu);
		return category;
	}

	/**
	 * @param nodeTags
	 * @param dgType
	 * @return ActionsCategory of DiagramContextClassActions for the diagram
	 *         context menu (Boolean Tags)
	 */
	static ActionsCategory getDiagramContextBooleanTagActions(NodeTag[] nodeTags, UWEDiagramType dgType) {
		// create a new category (for the Tags)
		ActionsCategory category = new ActionsCategory("", "");
		for (NodeTag tag : nodeTags) {
			if (tag.isBoolean()) {
				DiagramContextBooleanTagAction action = new DiagramContextBooleanTagAction(tag.toString(), null,
						ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED, dgType, tag);
				category.addAction(action);
				String iconName = RIATagsHelper.getIconName(tag.getRIAOption());
				if (!iconName.equals("no icon")) {
					setIcon(action, iconsPath + "ria/" + iconName + iconSuffix);
				}
				if (dgType.equals(UWEDiagramType.PRESENTATION)) {
					RIATagsHelper.riaTagActions.add(action);
				}
			}
		}
		return category;
	}

	/**
	 * @param nodeTags
	 * @param dgType
	 * @return ActionsCategory of DiagramContextClassActions for the diagram
	 *         context menu (String Tags)
	 */
	static ActionsCategory getDiagramContextStringTagActions(NodeTag[] nodeTags, UWEDiagramType dgType) {
		// create a new category (for the Tags)
		ActionsCategory category = new ActionsCategory("", "");
		for (NodeTag tag : nodeTags) {
			if (!tag.isBoolean()) {
				DiagramContextStringTagAction action = new DiagramContextStringTagAction(tag.toString(), null,
						ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED, dgType, tag);
				category.addAction(action);
				setIcon(action, iconsPath + "TaggedValueString" + iconSuffix);
			}
		}
		return category;
	}

	/**
	 * get "Set Type..." context menu
	 *
	 * @return return action category
	 */
	static ActionsCategory getSetRequirementsType() {
		ActionsCategory category = new ActionsCategory("", "");
		SetRequirementsElementTypeAction action = new SetRequirementsElementTypeAction(
				"Set Type", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + iconsRequirementsPath+ "setType" + iconSuffix);
		return category;
	}

	/**
	 * get "Copy UWE Tags..." context menu
	 * 
	 * @return return action category
	 */
	static ActionsCategory getCopyUWETags() {
		ActionsCategory category = new ActionsCategory("", "");
		DiagramContextPropertyCopyVsClassAction action = new DiagramContextPropertyCopyVsClassAction(
				"Copy UWE tags...", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + "CopyTaggedValues" + iconSuffix);
		return category;
	}

	/**
	 * get "Copy UWE stereotypes..." context menu
	 * 
	 * @return return action category
	 */
	static ActionsCategory getCopyUWESubstateMachineStereotypes() {
		ActionsCategory category = new ActionsCategory("", "");
		DiagramContextSubmachineStateCopyVsStateMachineAction action = new DiagramContextSubmachineStateCopyVsStateMachineAction(
				"Copy UWE stereotypes...", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + "CopyUWEStateMachineStereotypes" + iconSuffix);
		return category;
	}

	/**
	 * get "Copy UWE stereotypes..." context menu
	 * 
	 * @return return action category
	 */
	static ActionsCategory getCopyUWEuseCaseStereotypes() {
		ActionsCategory category = new ActionsCategory("", "");
		DiagramContextInheritUseCasePackageStereotypesAction action = new DiagramContextInheritUseCasePackageStereotypesAction(
				"Copy UWE stereotypes...", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + "CopyStereotypesUseCases" + iconSuffix);
		return category;
	}

	/**
	 * get "Set «navigationalNode» to unstereotyped substates..." context menu
	 * 
	 * @return return action category
	 */
	static ActionsCategory getSetNavigationalNodeToUnstereotypedSubstates() {
		ActionsCategory category = new ActionsCategory("", "");
		DiagramContextSetDefaultNavStateStereotype action = new DiagramContextSetDefaultNavStateStereotype(
				"Set «navigationalNode» to substates...", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + "SetDefaultNavStereotype" + iconSuffix);
		return category;
	}

	/**
	 * get "Check {transmissionType} on substates" context menu
	 * 
	 * @return return action category
	 */
	static ActionsCategory getCheckSessionNodeTransmissionTypeChange() {
		ActionsCategory category = new ActionsCategory("", "");
		CheckSessionTransmissionType action = new CheckSessionTransmissionType(
				"Check {transmissionType} on substates", null, ActionsGroups.PRESENTATION_ELEMENT_SELECTION_RELATED);
		category.addAction(action);
		setIcon(action, iconsPath + "CheckSessionTransmissionType" + iconSuffix);
		return category;
	}

	/**
	 * Provides save conversion from List to Collection<NMAction>
	 * 
	 * @param actions
	 *            List
	 * @return Collection<NMAction>
	 */
	@SuppressWarnings("rawtypes")
	private static Collection<NMAction> convertActionsToGenericCol(List actions) {
		Collection<NMAction> col = new LinkedList<NMAction>();
		for (Object action : actions) {
			try {
				col.add((NMAction) action);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return col;
	}

	/**
	 * Set (small) icon to action (e.g menu). Icon should be 16x16 pixel
	 * 
	 * @param action
	 * @param path
	 *            of the icon
	 */
	private static void setIcon(NMAction action, String path) {
		Icon icon = PluginManager.getIcon(path);
		if (icon != null) {
			action.setSmallIcon(icon); // e.g for menu bar
			action.setLargeIcon(icon); // e.g for tool bar
		}
	}
}
