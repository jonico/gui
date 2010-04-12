package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.teamforge.api.main.UserDO;
import com.danube.scrumworks.api.client.types.UserWSO;

public class MapUsersWizard extends AbstractMappingWizard {
	private MapUsersWizardPage wizardPage;
	private boolean errors;

	public MapUsersWizard(SynchronizationStatus projectMapping) {
		super(projectMapping);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Map Users");
		wizardPage = new MapUsersWizardPage();
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		final List<UserDO> activateUserList = wizardPage.getActivateUserList();
		final List<UserWSO> createUserList = wizardPage.getCreateUserList();
		final List<UserWSO> addProjectMemberList = wizardPage.getAddProjectMemberList();
		if (activateUserList.size() == 0 && createUserList.size() == 0 && addProjectMemberList.size() == 0) {
			return true;
		}
		errors = false;
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					int totalWork = createUserList.size() + activateUserList.size() + addProjectMemberList.size();
					String taskName = "Mapping ScrumWorks users to TeamForge";
					monitor.setTaskName(taskName);
					monitor.beginTask(taskName, totalWork);
					for (UserWSO swpUser : createUserList) {
						monitor.subTask("Creating " + swpUser.getUserName());
						String email = swpUser.getUserName() + "@default.com";
						String locale = "en";
						String timeZone = getScrumWorksEndpoint().getTimezone();
						String password = swpUser.getUserName() + "_defaultPassword";
						try {
							getSoapClient().createUser(swpUser.getUserName(), email, swpUser.getDisplayName(), locale, timeZone, false, false, password);
						} catch (Exception e) {
							Activator.handleError(e);
							errors = true;
						}
						monitor.worked(1);
					}
					for (UserDO userDO : activateUserList) {
						try {
							monitor.subTask("Activating " + userDO.getUsername());
							userDO.setStatus("Active");
							getSoapClient().setUserData(userDO);
						} catch (Exception e) {
							Activator.handleError(e);
							errors = true;
						}
						monitor.worked(1);
					}
					for (UserWSO swpUser : addProjectMemberList) {
						try {
							monitor.subTask("Adding " + swpUser.getUserName() + " to member list");
							getSoapClient().addProjectMember(wizardPage.getProjectId(), swpUser.getUserName());
						} catch (Exception e) {
							Activator.handleError(e);
							errors = true;
						}
						monitor.worked(1);
					}
				} catch (Exception e) {
					Activator.handleError(e);
					errors = true;
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			MessageDialog.openError(getShell(), "Map Users", e.getMessage());
			return false;
		}
		if (errors == true) {
			wizardPage.refresh(true);
			MessageDialog.openWarning(getShell(), "Map Users", "One or more error occurred mapping ScrumWorks users to TeamForge.  See error log for details.");
			return false;
		}
		return true;
	}

}
