package magicUWE.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import magicUWE.settings.GlobalConstants;
import magicUWE.shared.MessageWriter;

import org.apache.log4j.Logger;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.modules.AutoLoadKind;
import com.nomagic.magicdraw.core.modules.ModuleDescriptor;
import com.nomagic.magicdraw.core.modules.MountTable;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.task.SimpleProgressStatus;

/**
 * Main plug-in listener class. The main goal is to manage the UWE Profile
 * module, in particular to load it if a project is opened in which the UWE
 * profile is missing.
 * 
 * 
 * @author PST LMU
 */
public class ProjectListener extends ProjectEventListenerAdapter {
	private static final Logger logger = Logger.getLogger(ProjectListener.class);
	private final static String PROFILES_DIR_MD = "profiles";

	/**
	 * Sets the default module file needed for all UWE stereotypes. First,
	 * search for the UWE-Profile in the directory of the actual project. If
	 * this file does not exist, the one in the profile directory of MagicDraw
	 * will be chosen.
	 * 
	 * @param project
	 *            actual project
	 * @param shouldTryToUseVersionInProjectDir
	 * @return Full path of the UWE Profile
	 */
	private File getModuleFile(Project project, boolean shouldTryToUseVersionInProjectDir) {
		String profileDirString = project.getDirectory();
		String profileName = GlobalConstants.getUWE_PROPERTY_PROFILE_NAME_IN_THE_PROJECT_DIR();
		if ((!shouldTryToUseVersionInProjectDir) || (profileDirString == null || profileDirString.trim().equals(""))) {
			shouldTryToUseVersionInProjectDir = false;
			profileDirString = PROFILES_DIR_MD;
			profileName = GlobalConstants.getUWE_PROPERTY_PROFILE_NAME();
		}

		File profileDir = new File(profileDirString);
		if (profileDir.isDirectory()) {
			File moduleFile = new File(profileDir.getAbsolutePath() + "/" + profileName);
			logger.debug("moduleFile: " + moduleFile.getAbsolutePath());
			if (moduleFile.isFile()) {
				return moduleFile;
			}
			if (shouldTryToUseVersionInProjectDir) {
				// if we only cannot find the profile within the directory of
				// our project, we will look in the MagicDraw/profiles directory
				return getModuleFile(project, false);
			}
		}
		MessageWriter.showError("Couldn't find UWE profile file: \"" + profileDir + "/" + profileName + "\"", logger);
		return null;
	}

	/**
	 * loads the UWE profile file from the profile directory
	 * 
	 * @return true if successful
	 */
	private boolean loadUweProfile() {
		// cannot be undone (maybe in future magicdraw-version)
		boolean done = false;
		Project project = Application.getInstance().getProjectsManager().getActiveProject();

		File moduleProfileFile = getModuleFile(project, true);

		if (moduleProfileFile != null && project != null) {
			ProjectDescriptor module = ProjectDescriptorsFactory.createProjectDescriptor(moduleProfileFile.toURI());

			MountTable mountTable = project.getMountTable();
			logger.debug("module: " + module + "mountTable: " + mountTable);
			synchronized (this) {
				try {
					ModuleDescriptor moduleDescriptor = mountTable.mountModule(module);
					moduleDescriptor.setEditable(false);
					moduleDescriptor.setLoadType(AutoLoadKind.ALWAYS_LOAD);
					mountTable.addModule(moduleDescriptor);
					mountTable.loadModule(moduleDescriptor, new SimpleProgressStatus());
					mountTable.importModule(moduleDescriptor);
					done = true;

				} catch (IOException e) {
					logger.error("could not load UWE-module");
					e.printStackTrace();
				}
			}
		}
		return done;
	}

	/**
	 * Checks if the UWE profile is loaded
	 * 
	 * @return true if the UWE Module is loaded into the active project.
	 */
	private boolean isUweLoaded() {
		Project project = Application.getInstance().getProjectsManager().getActiveProject();
		if (project != null) {
			Collection<ModuleDescriptor> modules = project.getMountTable().getModules();
			if (modules != null) {
				for (Iterator<ModuleDescriptor> it = modules.iterator(); it.hasNext();) {
					ModuleDescriptor moduleDescriptor = it.next();
					URI uri = moduleDescriptor.getURI();
					logger.debug("modules size: " + modules.size() + "URI: " + uri);
					// one of the two alternative profiles must be loaded
					if (GlobalConstants.getUWE_PROPERTY_PROFILE_NAME().equals(new File(uri).getName())
							|| GlobalConstants.getUWE_PROPERTY_PROFILE_NAME_IN_THE_PROJECT_DIR().equals(
									new File(uri).getName())) {
						return moduleDescriptor.isLoaded();
					}
				}
			}
		}
		return false;
	}

	/**
	 * executed when a project is opened.
	 */
	@Override
	public void projectOpened(Project project) {
		logger.debug("Project " + project.getHumanName() + " was opened.");

		if (!isUweLoaded()) {
			if (MessageWriter.showQuestion("The UWE Profile is not loaded.\n"
					+ "You can not work with the MagicUWE plugin until you load the UWE Profile.\n \n"
					+ "Do you want to load the profile now?", logger)) {
				if (!loadUweProfile()) {
					MessageWriter.showError("Failed to load UWE Profile.", logger);
				}
			}
		}
	}

	@Override
	public void projectClosed(Project proj) {
		// MessageWriter.log(">> Project was closed: " +
		// this.activeProject.getName(), logger);
	}

	@Override
	public void projectSaved(Project proj, boolean bol) {
		// do nothing
	}

	@Override
	public void projectActivated(Project proj) {
		// MessageWriter.log(">> Project was activated: " +
		// this.activeProject.getName(), logger);
		// isUweLoaded();
	}

	@Override
	public void projectDeActivated(Project proj) {
		// MessageWriter.log(">> Project was deactivated: " +
		// this.activeProject.getName(), logger);
	}

	@Override
	public void projectReplaced(Project proj1, Project proj2) {
		// MessageWriter.log(">> Project was replaced: " +
		// this.activeProject.getName(), logger);
	}
}
