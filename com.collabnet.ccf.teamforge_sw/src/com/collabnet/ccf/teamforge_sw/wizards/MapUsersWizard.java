package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.teamforge.api.main.UserDO;
import com.collabnet.teamforge.api.rbac.RbacClient;
import com.collabnet.teamforge.api.rbac.RoleDO;
import com.collabnet.teamforge.api.rbac.RoleList;
import com.collabnet.teamforge.api.rbac.RoleRow;
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
					int totalWork = createUserList.size() + activateUserList.size() + (addProjectMemberList.size() * 2);
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
					List<String> newUsers = new ArrayList<String>();
					for (UserWSO swpUser : addProjectMemberList) {
						try {
							monitor.subTask("Adding " + swpUser.getUserName() + " to member list");
							getSoapClient().addProjectMember(wizardPage.getProjectId(), swpUser.getUserName());
							newUsers.add(swpUser.getUserName());
						} catch (Exception e) {
							Activator.handleError(e);
							errors = true;
						}
						monitor.worked(1);
					}
					if (newUsers != null && newUsers.size() > 0) {
						createRole(wizardPage.getProjectId(), newUsers, monitor);
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
	
	private void createRole(String projectId, List<String> users, IProgressMonitor monitor) throws RemoteException {
		String roleId = null;
		RoleList roleList = getSoapClient().getRoleList(projectId);
		RoleRow[] roleRows = roleList.getDataRows();
		for (RoleRow roleRow : roleRows) {
			if (roleRow.getTitle().equals(ProjectMappingWizard.PRODUCT_DEVELOPER_ROLE_TITLE)) {
				roleId = roleRow.getId();
				break;
			}
		}
		if (roleId == null) {
			RoleDO roleDO = getSoapClient().createRole(projectId, ProjectMappingWizard.PRODUCT_DEVELOPER_ROLE_TITLE, ProjectMappingWizard.PRODUCT_DEVELOPER_ROLE_DESCRIPTION);
			roleId = roleDO.getId();
			getSoapClient().addCluster(roleId, RbacClient.TRACKER_CREATE, "");
			getSoapClient().addCluster(roleId, RbacClient.TRACKER_EDIT, "");
			getSoapClient().addCluster(roleId, RbacClient.PAGE_VIEW, "");
			getSoapClient().addCluster(roleId, RbacClient.DOCMAN_CREATE, "");
			getSoapClient().addCluster(roleId, RbacClient.DOCMAN_EDIT, "");
			getSoapClient().addCluster(roleId, RbacClient.SCM_COMMIT, "");
			getSoapClient().addCluster(roleId, RbacClient.DISCUSSION_PARTICIPATE, "");
		}
		for (String username : users) {
			monitor.subTask("Setting permissions for " + username);
			getSoapClient().addUser(roleId, username);
			monitor.worked(1);
		}
	}

}
