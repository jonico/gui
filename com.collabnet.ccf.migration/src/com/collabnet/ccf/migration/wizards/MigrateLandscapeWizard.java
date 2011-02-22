package com.collabnet.ccf.migration.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.api.CcfMasterClient;
import com.collabnet.ccf.api.model.Participant;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.migration.webclient.TeamForgeClient;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.teamforge.api.pluggable.PluggableComponentDO;
import com.collabnet.teamforge.api.pluggable.PluggableComponentParameterDO;
import com.collabnet.teamforge.api.pluggable.PluggablePermissionDO;

public class MigrateLandscapeWizard extends Wizard {
	private Landscape landscape;
	private MigrateLandscapeWizardCcfMasterPage ccfMasterPage;
	private TeamForgeClient teamForgeClient;
	
	private Exception exception;

	public MigrateLandscapeWizard(Landscape landscape) {
		super();
		this.landscape = landscape;
		
		teamForgeClient = TeamForgeClient.getTeamForgeClient(landscape);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Migrate Landscape to CCF 2.x");
		ccfMasterPage = new MigrateLandscapeWizardCcfMasterPage();
		addPage(ccfMasterPage);
	}	

	@Override
	public boolean performFinish() {
		exception = null;
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				String taskName = "Migrating landscape";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, 6);	
				CcfMasterClient ccfMasterClient = getCcfMasterClient();
				try {					
					
					monitor.subTask("Checking for existing participants");
					String otherType;
					if (landscape.getType1().equals("TF")) {
						otherType = landscape.getType2();
					} else {
						otherType = landscape.getType1();
					}		
					Participant[] participants = ccfMasterClient.getParticipants();
					Participant teamForgeParticipant = null;
					Participant otherParticipant = null;
					for (Participant participant : participants) {
						if (participant.getSystemId().equals("TF")) {
							teamForgeParticipant = participant;
						}
						else if (participant.getSystemId().equals(otherType)) {
							otherParticipant = participant;
						}
					}
					monitor.worked(1);
					if (teamForgeParticipant == null) {
						monitor.subTask("Creating TeamForge participant");
						teamForgeParticipant = new Participant();
						teamForgeParticipant.setSystemId("TF");
						teamForgeParticipant.setDescription("TeamForge");
						teamForgeParticipant = ccfMasterClient.createParticipant(teamForgeParticipant);
					}
					monitor.worked(1);
					
					if (otherParticipant == null) {
						String otherDescription;
						if (otherType.equals("QC")) {
							otherDescription = "Quality Center";
						}
						else if (otherType.equals("PT")) {
							otherDescription = "Project Tracker";
						}
						else if (otherType.equals("SWP")) {
							otherDescription = "ScrumWorks Pro";
						}	
						else {
							otherDescription = "Unknown";
						}
						monitor.subTask("Creating " + otherDescription + " participant");
						otherParticipant = new Participant();
						otherParticipant.setSystemId(otherType);
						otherParticipant.setDescription(otherDescription);			
						otherParticipant = ccfMasterClient.createParticipant(otherParticipant);
					}
					monitor.worked(1);
					
					monitor.subTask("Creating integrated application");
					teamForgeClient.getConnection().login();
					
					String plugName = landscape.getDescription();
					String description = "19n.CCFMASTER";
					String baseUrl = ccfMasterPage.getCcfMasterUrl();
					String goUrl = ccfMasterPage.getCcfMasterUrl() + "gourl/%p/%o";
					String prefix = "ccf";
					String isScmRequired = "false";
					String requireProjPrefix = "false";
					String iconFileId = "";
					String endPoint = "http://localhost:8090/services/DummyService";
					PluggableComponentParameterDO[] paramDO = {};
					String adminUrl = "http://localhost/";
					PluggablePermissionDO defaultPermission = new PluggablePermissionDO();
					defaultPermission.setDapMappedTo("View");
					defaultPermission.setPermission("Default");
					PluggablePermissionDO hospitalPermission = new PluggablePermissionDO();
					hospitalPermission.setPermission("Hospital");
					PluggablePermissionDO identityMappingsPermission = new PluggablePermissionDO();
					identityMappingsPermission.setPermission("Identity Mappings");
					PluggablePermissionDO repositoryMappingsPermission = new PluggablePermissionDO();
					repositoryMappingsPermission.setPermission("Repository Mappings");
					PluggablePermissionDO resetSynchronizationStatusPermission = new PluggablePermissionDO();
					resetSynchronizationStatusPermission.setPermission("Reset Synchronization Status");
					PluggablePermissionDO pauseSynchronizationPermission = new PluggablePermissionDO();
					pauseSynchronizationPermission.setPermission("Pause Synchronization");
					PluggablePermissionDO mappingRulesPermission = new PluggablePermissionDO();
					mappingRulesPermission.setPermission("Mapping Rules");
					PluggablePermissionDO mappingRuleTemplatesPermission = new PluggablePermissionDO();
					mappingRuleTemplatesPermission.setPermission("Mapping Rule Templates");
					PluggablePermissionDO ccfCoreConfigurationPermission = new PluggablePermissionDO();
					ccfCoreConfigurationPermission.setPermission("CCF Core Configuration");
					PluggablePermissionDO[] permDO = {
							defaultPermission,
							hospitalPermission,
							identityMappingsPermission,
							repositoryMappingsPermission, 
							resetSynchronizationStatusPermission,
							pauseSynchronizationPermission,
							mappingRulesPermission,
							mappingRuleTemplatesPermission,
							ccfCoreConfigurationPermission
					};
					String pceInputType = "select";
					String pceResultFormat = "list";
					String pceDescription = "description";
					String pceTitle = "title";
									
					teamForgeClient.getConnection().getIntegratedAppClient(false).createIntegratedApplication(plugName, description, baseUrl, goUrl, prefix, isScmRequired, requireProjPrefix, iconFileId, endPoint, paramDO, adminUrl, permDO, pceInputType, pceResultFormat, pceDescription, pceTitle);					
					monitor.worked(1);
					
					PluggableComponentDO integratedApplication = teamForgeClient.getConnection().getIntegratedAppClient(false).getIntegratedApplicationByName(landscape.getDescription());
					monitor.worked(1);
					
					teamForgeClient.getConnection().getIntegratedAppClient(false).setPluggableAppMessageResource(integratedApplication.getId(), "en_US", "19n.CCFMASTER", "CCFMASTER");					
					monitor.worked(1);
					
				} catch (Exception e) {
					exception = e;
					return;					
				} finally {
					monitor.done();
				}
			}			
		};
		
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			ExceptionDetailsErrorDialog.openError(getShell(), "Migrate Landscape to CCF 2.x", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
			return false;
		}
		
		if (exception != null) {
			Activator.handleError(exception);
			ExceptionDetailsErrorDialog.openError(getShell(), "Migrate Landscape to CCF 2.x", exception.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, exception.getLocalizedMessage(), exception));
		}
		return exception == null;
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	public Landscape getLandscape() {
		return landscape;
	}

	public CcfMasterClient getCcfMasterClient() {
		return CcfMasterClient.getClient(ccfMasterPage.getCcfMasterUrl(), ccfMasterPage.getCcfMasterUser(), ccfMasterPage.getCcfMasterPassword());
	}
}
