package com.collabnet.ccf.migration.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.api.CcfMasterClient;
import com.collabnet.ccf.api.model.Direction;
import com.collabnet.ccf.api.model.Directions;
import com.collabnet.ccf.api.model.ExternalApp;
import com.collabnet.ccf.api.model.Participant;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.migration.MigrationResult;
import com.collabnet.ccf.migration.dialogs.MigrateLandscapeErrorDialog;
import com.collabnet.ccf.migration.dialogs.MigrateLandscapeResultsDialog;
import com.collabnet.ccf.migration.webclient.TeamForgeClient;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.teamforge.api.pluggable.PluggableComponentDO;
import com.collabnet.teamforge.api.pluggable.PluggableComponentParameterDO;
import com.collabnet.teamforge.api.pluggable.PluggablePermissionDO;
import com.collabnet.teamforge.api.tracker.TrackerDO;

public class MigrateLandscapeWizard extends Wizard {
	private Landscape landscape;
	private MigrateLandscapeWizardCcfMasterPage ccfMasterPage;
	private TeamForgeClient teamForgeClient;
	
	private List<MigrationResult> migrationResults;
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
		migrationResults = new ArrayList<MigrationResult>();
		exception = null;
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				String taskName = "Migrating landscape";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, 16);	
				CcfMasterClient ccfMasterClient = getCcfMasterClient();
				try {					
					
					monitor.subTask("Checking for existing CCF Master participants");
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
							migrationResults.add(new MigrationResult("TeamForge participant already exists in CCF Master."));
						}
						else if (participant.getSystemId().equals(otherType)) {
							otherParticipant = participant;
							migrationResults.add(new MigrationResult(getParticipantDescription(otherType) + " participant already exists in CCF Master."));
						}
					}
					monitor.worked(1);
					if (teamForgeParticipant == null) {
						monitor.subTask("Creating CCF Master TeamForge participant");
						teamForgeParticipant = new Participant();
						teamForgeParticipant.setSystemId("TF");
						teamForgeParticipant.setDescription("TeamForge");
						teamForgeParticipant = ccfMasterClient.createParticipant(teamForgeParticipant);
						migrationResults.add(new MigrationResult("TeamForge participant created in CCF Master."));
					}
					monitor.worked(1);
					
					if (otherParticipant == null) {
						String otherDescription = getParticipantDescription(otherType);			
						monitor.subTask("Creating CCF Master " + otherDescription + " participant");
						otherParticipant = new Participant();
						otherParticipant.setSystemId(otherType);
						otherParticipant.setDescription(otherDescription);			
						otherParticipant = ccfMasterClient.createParticipant(otherParticipant);
						migrationResults.add(new MigrationResult(getParticipantDescription(otherType) + " participant created in CCF Master."));
					}
					monitor.worked(1);
					
					teamForgeClient.getConnection().login();
					monitor.subTask("Checking for CCF Master integrated application");
					String plugId = teamForgeClient.getConnection().getIntegratedAppClient(false).getPlugIdByBaseUrl(ccfMasterPage.getCcfMasterUrl());
					if (plugId != null) {
						migrationResults.add(new MigrationResult("Integrated application " + plugId + " already exists."));
					}
					monitor.worked(1);
					
					boolean integratedApplicationCreated = false;
					if (plugId == null) {
						monitor.subTask("Creating CCF Master integrated application");				
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
						integratedApplicationCreated = true;
					}
					monitor.worked(1);
					
					if (plugId == null) {
						PluggableComponentDO integratedApplication = teamForgeClient.getConnection().getIntegratedAppClient(false).getIntegratedApplicationByName(landscape.getDescription());
						plugId = integratedApplication.getId();
					}
					monitor.worked(1);
					
					if (integratedApplicationCreated) {
						migrationResults.add(new MigrationResult("Integrated application " + plugId + " created."));
						teamForgeClient.getConnection().getIntegratedAppClient(false).setPluggableAppMessageResource(plugId, "en_US", "19n.CCFMASTER", "CCFMASTER");					
					}
					monitor.worked(1);
					
					monitor.subTask("Checking for existing CCF Master landscape");
					com.collabnet.ccf.api.model.Landscape ccfMasterLandscape = null;
					com.collabnet.ccf.api.model.Landscape[] landscapes = ccfMasterClient.getLandscapes();
					for (com.collabnet.ccf.api.model.Landscape landscape : landscapes) {
						if (landscape.getPlugId().equals(plugId) && landscape.getTeamForge().getId() == teamForgeParticipant.getId() && landscape.getParticipant().getId() == otherParticipant.getId()) {
							ccfMasterLandscape = landscape;
							migrationResults.add(new MigrationResult("Landscape " + ccfMasterLandscape.getDescription() + " already exists in CCF Master."));
							break;
						}
					}
					monitor.worked(1);
					
					boolean landscapeAlreadyExists = ccfMasterLandscape != null;
					
					if (ccfMasterLandscape == null) {
						monitor.subTask("Creating CCF Master landscape");
						ccfMasterLandscape = new com.collabnet.ccf.api.model.Landscape();
						ccfMasterLandscape.setDescription(landscape.getDescription());
						ccfMasterLandscape.setParticipant(otherParticipant);
						ccfMasterLandscape.setPlugId(plugId);
						ccfMasterLandscape.setTeamForge(teamForgeParticipant);
						ccfMasterLandscape = ccfMasterClient.createLandscape(ccfMasterLandscape);
						migrationResults.add(new MigrationResult("Landscape " + ccfMasterLandscape.getDescription() + " created in CCF Master."));
					}
					monitor.worked(1);
					
					Direction forward = null;
					Direction reverse = null;
					if (landscapeAlreadyExists) {
						monitor.subTask("Checking for existing CCF Master directions");
						Direction[] directions = ccfMasterClient.getDirections();
						for (Direction direction : directions) {
							if (direction.getLandscape().getId() == ccfMasterLandscape.getId()) {
								if (direction.getDirections().equals(Directions.FORWARD)) {
									forward = direction;
									migrationResults.add(new MigrationResult("Direction " + forward.getDescription() + " (FORWARD) already exists in CCF Master."));
								}
								else if (direction.getDirections().equals(Directions.REVERSE)) {
									reverse = direction;
									migrationResults.add(new MigrationResult("Direction " + reverse.getDescription() + " (REVERSE) already exists in CCF Master."));
								}
								if (forward != null && reverse != null) {
									break;
								}
							}
						}
					}
					monitor.worked(1);
					
					if (forward == null || reverse == null) {
						monitor.subTask("Creating CCF Master directions");
					}
					
					if (forward == null) {
						forward = new Direction();
						forward.setLandscape(ccfMasterLandscape);
						forward.setDirections(Directions.FORWARD);
						forward.setDescription(landscape.getType1() + landscape.getType2());
						forward = ccfMasterClient.createDirection(forward);
						migrationResults.add(new MigrationResult("Direction " + forward.getDescription() + " (FORWARD) created in CCF Master."));
					}
					monitor.worked(1);
					
					if (reverse == null) {
						reverse = new Direction();
						reverse.setLandscape(ccfMasterLandscape);
						reverse.setDirections(Directions.REVERSE);
						reverse.setDescription(landscape.getType2() + landscape.getType1());
						reverse = ccfMasterClient.createDirection(reverse);
						migrationResults.add(new MigrationResult("Direction " + reverse.getDescription() + " (REVERSE) created in CCF Master."));
					}
					monitor.worked(1);
					
					monitor.subTask("Retrieving CCF 1.x project mappings");
					CcfDataProvider ccfDataProvider = new CcfDataProvider();
					SynchronizationStatus[] projectMappings = ccfDataProvider.getSynchronizationStatuses(landscape, null);
					monitor.worked(1);
					
					monitor.subTask("Compiling TeamForge project list");
					List<String> projectIds = new ArrayList<String>();
					for (SynchronizationStatus projectMapping : projectMappings) {
						String repositoryId = null;
						if (projectMapping.getSourceSystemKind().startsWith("TF")) {
							repositoryId = projectMapping.getSourceRepositoryId();
						}
						else if (projectMapping.getTargetSystemKind().startsWith("TF")) {
							repositoryId = projectMapping.getTargetRepositoryId();
						}
						if (repositoryId != null) {
							String projectId = null;
							if (repositoryId.startsWith("proj")) {
								int index = repositoryId.indexOf("-");
								if (index == -1) {
									projectId = repositoryId;
								} else {
									projectId = repositoryId.substring(0, index);
								}
							}
							else if (repositoryId.startsWith("tracker")) {
								String trackerId = null;
								int index = repositoryId.indexOf("-");
								if (index == -1) {
									trackerId = repositoryId;
								} else {
									trackerId = repositoryId.substring(0, index);
								}
								if (trackerId != null) {
									TrackerDO tracker = teamForgeClient.getConnection().getTrackerClient().getTrackerData(trackerId);
									if (tracker != null) {
										projectId = tracker.getProjectId();
									}
								}
							}
							if (projectId != null && !projectIds.contains(projectId)) {
								projectIds.add(projectId);
							}
						}
					}
					monitor.worked(1);
					
					Map<String, String> linkMap = new HashMap<String, String>();
					
					if (projectIds.size() > 0) {
						monitor.subTask("Enabling integrated application for projects");
						for (String projectId : projectIds) {
							String linkId = null;
							try {
								linkId = teamForgeClient.getConnection().getIntegratedAppClient(false).getLinkPlugIdByPlugId(projectId, plugId);
								if (linkId != null) {
									migrationResults.add(new MigrationResult("Integrated application " + linkId + " already enabled for project " + projectId + "."));
								}
							} catch (Exception e) { 
								// Ignore.  Throws exception if not already enabled. 
							}
							if (linkId == null) {
								PluggableComponentParameterDO[] params = {};
								teamForgeClient.getConnection().getIntegratedAppClient(false).enablePluggableComponent(projectId, plugId, params, "ccf");							
								linkId = teamForgeClient.getConnection().getIntegratedAppClient(false).getLinkPlugIdByPlugId(projectId, plugId);
								linkMap.put(projectId, linkId);
								migrationResults.add(new MigrationResult("Integrated application " + linkId + " enabled for project " + projectId + "."));
							}
						}
					}
					monitor.worked(1);
					
					if (linkMap.size() > 0) {
						monitor.subTask("Creating CCF Master external applications");
						Set<String> projects = linkMap.keySet();
						for (String project : projects) {
							String linkId = linkMap.get(project);
							ExternalApp externalApp = new ExternalApp();
							externalApp.setProjectId(project);
							externalApp.setLinkId(linkId);
							externalApp.setLandscape(ccfMasterLandscape);
							externalApp = ccfMasterClient.createExternalApp(externalApp);
							migrationResults.add(new MigrationResult("External application " + externalApp.getLinkId() + " (" + project + ") created in CCF Master."));
						}
					}
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
			if (e.getMessage().contains("<html>")) {
				MigrateLandscapeErrorDialog dialog = new MigrateLandscapeErrorDialog(getShell(), e);
				dialog.open();				
			} else {
				ExceptionDetailsErrorDialog.openError(getShell(), "Migrate Landscape to CCF 2.x", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
			}
			migrationResults.add(new MigrationResult(e));
			return false;
		}
		
		if (exception != null) {
			Activator.handleError(exception);
			if (exception.getMessage().contains("<html>")) {
				MigrateLandscapeErrorDialog dialog = new MigrateLandscapeErrorDialog(getShell(), exception);
				dialog.open();					
			} else {
				ExceptionDetailsErrorDialog.openError(getShell(), "Migrate Landscape to CCF 2.x", exception.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, exception.getLocalizedMessage(), exception));
			}
			migrationResults.add(new MigrationResult(exception));
		}
		
		MigrationResult[] migrationResultArray = new MigrationResult[migrationResults.size()];
		migrationResults.toArray(migrationResultArray);
		MigrateLandscapeResultsDialog dialog = new MigrateLandscapeResultsDialog(getShell(), migrationResultArray);
		if (dialog.open() == MigrateLandscapeResultsDialog.CANCEL) {
			return false;
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

	private CcfMasterClient getCcfMasterClient() {
		return CcfMasterClient.getClient(ccfMasterPage.getCcfMasterUrl(), ccfMasterPage.getCcfMasterUser(), ccfMasterPage.getCcfMasterPassword());
	}
	
	private String getParticipantDescription(String type) {
		String description;
		if (type.equals("QC")) {
			description = "Quality Center";
		}
		else if (type.equals("PT")) {
			description = "Project Tracker";
		}
		else if (type.equals("SWP")) {
			description = "ScrumWorks Pro";
		}	
		else {
			description = "Unknown";
		}
		return description;
	}
}
