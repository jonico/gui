package com.collabnet.ccf.teamforge_sw.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.sw.ScrumWorksCcfParticipant;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;
import com.collabnet.teamforge.api.main.ProjectDO;
import com.collabnet.teamforge.api.main.ProjectMemberList;
import com.collabnet.teamforge.api.main.ProjectMemberRow;
import com.collabnet.teamforge.api.main.ProjectRow;
import com.collabnet.teamforge.api.main.UserDO;
import com.collabnet.teamforge.api.rbac.RbacClient;
import com.collabnet.teamforge.api.rbac.RoleDO;
import com.collabnet.teamforge.api.tracker.TrackerDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldValueDO;
import com.collabnet.teamforge.api.tracker.TrackerRow;
import com.danube.scrumworks.api.client.ScrumWorksEndpoint;
import com.danube.scrumworks.api.client.ScrumWorksEndpointBindingStub;
import com.danube.scrumworks.api.client.ScrumWorksServiceLocator;
import com.danube.scrumworks.api.client.types.ProductWSO;
import com.danube.scrumworks.api.client.types.ServerException;
import com.danube.scrumworks.api.client.types.SprintWSO;
import com.danube.scrumworks.api.client.types.ThemeWSO;
import com.danube.scrumworks.api.client.types.UserWSO;

public class ProjectMappingWizard extends Wizard {
	private Landscape landscape;
	private ProjectMappings projectMappings;
	
	private ProjectMappingWizardSwpProductPage productPage;
	private ProjectMappingWizardTeamForgeProjectPage projectPage;
	private ProjectMappingWizardTeamForgeTrackerPage trackerPage;
	private ProjectMappingWizardPreviewPage previewPage;
	
	private TFSoapClient soapClient;
	private List<SynchronizationStatus> existingMappings;
	private List<Exception> errors;
	private List<String> notCreated;
	private boolean userMappingErrors;
	
	private ScrumWorksEndpoint scrumWorksEndpoint;
	
	private final static String TRACKER_DESCRIPTION_PBIS = "SWP Product Backlog Items";
	private final static String TRACKER_DESCRIPTION_TASKS = "SWP Tasks";
	private final static String TRACKER_ICON_PBIS = "icon_41.png";
	private final static String TRACKER_ICON_TASKS = "icon_35.png";
	
	public final static String PRODUCT_DEVELOPER_ROLE_TITLE = "Product Developer";
	public final static String PRODUCT_DEVELOPER_ROLE_DESCRIPTION = "People who develop the software application, taking story input from the product managers, breaking them down into story tasks, estimating them, and implementing them when backlogged to a release or an iteration.";

	public ProjectMappingWizard(Landscape landscape, ProjectMappings projectMappings) {
		super();
		this.landscape = landscape;
		this.projectMappings = projectMappings;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Map TeamForge and ScrumWorks Projects");
		productPage = new ProjectMappingWizardSwpProductPage();
		addPage(productPage);
		projectPage = new ProjectMappingWizardTeamForgeProjectPage();
		addPage(projectPage);
		trackerPage = new ProjectMappingWizardTeamForgeTrackerPage();
		addPage(trackerPage);
		previewPage = new ProjectMappingWizardPreviewPage();
		addPage(previewPage);
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public boolean performFinish() {		
		errors = new ArrayList<Exception>();
		userMappingErrors = false;
		notCreated = new ArrayList<String>();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				boolean newProject = false;
				
				// Create 8 mappings.
				int totalWork = 8;
				
				// Need to create project.
				if (getSelectedProject() == null) {
					totalWork++;
					newProject = true;
				}
				
				if (getSelectedPbiTracker() != null || getSelectedTaskTracker() != null) {
					// If we are using an existing tracker, we will first need to retrieve the
					// existing mappings to make sure we don't try to add one that already exists.
					totalWork++;
				}
				
				// Need to create PBIs tracker.
				if (getSelectedPbiTracker() == null) {
					totalWork++;
				}
				
				// Need to create Tasks tracker.
				if (getSelectedTaskTracker() == null) {
					totalWork++;
				}
				
				// Need to map users.
				if (productPage.isMapUsers()) {
					totalWork = totalWork + 3;
					if (!newProject) {
						totalWork++;
					}
				}
				
				CcfDataProvider dataProvider = new CcfDataProvider();
				String taskName = "Creating project mappings";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, totalWork);
				
				existingMappings = new ArrayList<SynchronizationStatus>();
				if (getSelectedPbiTracker() != null || getSelectedTaskTracker() != null) {
					monitor.subTask("");
					getExistingMappings(dataProvider);
					monitor.worked(1);
				}
				
				String projectId = null;
				String pbiTrackerId = null;
				String taskTrackerId = null;
				
				try {			
					if (getSelectedProject() == null) {
						monitor.subTask("Creating project " + projectPage.getNewProjectTitle());
						ProjectDO projectDO = getSoapClient().createProject(null, projectPage.getNewProjectTitle(), previewPage.getNewProjectDescription());
						projectId = projectDO.getId();
						monitor.worked(1);
					} else {
						projectId = getSelectedProject().getId();
					}
					
					if (getSelectedPbiTracker() == null) {
						monitor.subTask("Creating tracker " + trackerPage.getNewPbiTrackerTitle());
						TrackerDO trackerDO = getSoapClient().createTracker(projectId, null, trackerPage.getNewPbiTrackerTitle(), TRACKER_DESCRIPTION_PBIS, TRACKER_ICON_PBIS);
						pbiTrackerId = trackerDO.getId();
						TrackerFieldDO[] fields = getSoapClient().getFields(pbiTrackerId);
						for (TrackerFieldDO field : fields) {
							String fieldName = field.getName();
							if (fieldName.equals("group") ||
							    fieldName.equals("customer") ||
							    fieldName.equals("reportedInRelease") ||
							    fieldName.equals("resolvedInRelease") ||
							    fieldName.startsWith("estimated") ||
							    fieldName.startsWith("actual")) {
								field.setDisabled(true);
								getSoapClient().setField(pbiTrackerId, field);
							}
						}
						getSoapClient().addTextField(pbiTrackerId, "Benefit", 5, 1, false, false, false, null);
						getSoapClient().addTextField(pbiTrackerId, "Penalty", 5, 1, false, false, false, null);
						getSoapClient().addTextField(pbiTrackerId, "Backlog Effort", 5, 1, false, false, false, null);
						getSoapClient().addTextField(pbiTrackerId, "SWP-Key", 30, 1, false, false, false, null);
						getSoapClient().addTextField(pbiTrackerId, "Team", 30, 1, false, false, false, null);
						getSoapClient().addTextField(pbiTrackerId, "Sprint", 30, 1, false, false, false, null);
						getSoapClient().addDateField(pbiTrackerId, "Sprint Start", false, false, false);
						getSoapClient().addDateField(pbiTrackerId, "Sprint End", false, false, false);
						
						String[] themeValues = getThemeValues();
						getSoapClient().addMultiSelectField(pbiTrackerId, "Themes", 4, false, false, false, themeValues, null);
	
						for (TrackerFieldDO field : fields) {
							if (field.getName().equals("status")) {
								TrackerFieldValueDO[] oldValues = field.getFieldValues();
								TrackerFieldValueDO open = new TrackerFieldValueDO(getSoapClient().supports50());
								open.setIsDefault(true);
								open.setValue("Open");
								open.setValueClass("Open");
								open.setId(getFieldId("Open", oldValues));
								TrackerFieldValueDO done = new TrackerFieldValueDO(getSoapClient().supports50());
								done.setIsDefault(false);
								done.setValue("Done");
								done.setValueClass("Close");
								done.setId(getFieldId("Done", oldValues));
								TrackerFieldValueDO[] fieldValues = { open, done };
								field.setFieldValues(fieldValues);
								getSoapClient().setField(pbiTrackerId, field);
								break;
							}
						}
						
						monitor.worked(1);
					} else {
						pbiTrackerId = getSelectedPbiTracker().getId();
					}
					
					if (getSelectedTaskTracker() == null) {
						monitor.subTask("Creating tracker " + trackerPage.getNewTaskTrackerTitle());
						TrackerDO trackerDO = getSoapClient().createTracker(projectId, null, trackerPage.getNewTaskTrackerTitle(), TRACKER_DESCRIPTION_TASKS, TRACKER_ICON_TASKS);
						taskTrackerId = trackerDO.getId();
						TrackerFieldDO[] fields = getSoapClient().getFields(taskTrackerId);
						for (TrackerFieldDO field : fields) {
							String fieldName = field.getName();
							if (fieldName.equals("group") ||
							    fieldName.equals("customer") ||
							    fieldName.equals("reportedInRelease") ||
							    fieldName.equals("resolvedInRelease") ||
							    fieldName.startsWith("autosumming") ||
							    fieldName.startsWith("estimated") ||
							    fieldName.startsWith("actual")) {
								field.setDisabled(true);
								getSoapClient().setField(taskTrackerId, field);
							}
						}
						for (TrackerFieldDO field : fields) {
							if (field.getName().equals("status")) {
								TrackerFieldValueDO[] oldValues = field.getFieldValues();
								TrackerFieldValueDO notStarted = new TrackerFieldValueDO(getSoapClient().supports50());
								notStarted.setIsDefault(true);
								notStarted.setValue("Not Started");
								notStarted.setValueClass("Open");
								notStarted.setId(getFieldId("Not Started", oldValues));
								TrackerFieldValueDO impeded = new TrackerFieldValueDO(getSoapClient().supports50());
								impeded.setIsDefault(false);
								impeded.setValue("Impeded");
								impeded.setValueClass("Open");
								impeded.setId(getFieldId("Impeded", oldValues));
								TrackerFieldValueDO inProgress = new TrackerFieldValueDO(getSoapClient().supports50());
								inProgress.setIsDefault(false);
								inProgress.setValue("In Progress");
								inProgress.setValueClass("Open");
								inProgress.setId(getFieldId("In Progress", oldValues));
								TrackerFieldValueDO done = new TrackerFieldValueDO(getSoapClient().supports50());
								done.setIsDefault(false);
								done.setValue("Done");
								done.setValueClass("Close");
								done.setId(getFieldId("Done", oldValues));
								TrackerFieldValueDO[] fieldValues = { notStarted, impeded, inProgress, done };
								field.setFieldValues(fieldValues);
								getSoapClient().setField(taskTrackerId, field);
								break;
							}
						}
						
						monitor.worked(1);
					} else {
						taskTrackerId = getSelectedTaskTracker().getId();
					}
					if (productPage.isMapUsers()) {
						userMappingErrors = !mapUsers(getSelectedProduct(), projectId, newProject, monitor);
					}
				} catch (Exception e) {
					Activator.handleError(e);
					errors.add(e);
					monitor.done();
					return;
				}
				
				monitor.subTask(previewPage.getTrackerTaskMapping());
				SynchronizationStatus projectMapping = new SynchronizationStatus();
				projectMapping.setGroup(landscape.getGroup());
				projectMapping.setTargetRepositoryKind("TRACKER");
				if (landscape.getEncoding2() != null && landscape.getEncoding2().trim().length() > 0) {
					projectMapping.setTargetSystemEncoding(landscape.getEncoding2());
				}				
				if (landscape.getType1().equals("TF")) {
					projectMapping.setSourceSystemId(landscape.getId1());	
					projectMapping.setTargetSystemId(landscape.getId2());			
					projectMapping.setSourceSystemKind(landscape.getType1());			
					projectMapping.setTargetSystemKind(landscape.getType2());
					projectMapping.setSourceSystemTimezone(landscape.getTimezone1());
					projectMapping.setTargetSystemTimezone(landscape.getTimezone2());
				} else {
					projectMapping.setSourceSystemId(landscape.getId2());	
					projectMapping.setTargetSystemId(landscape.getId1());			
					projectMapping.setSourceSystemKind(landscape.getType2());			
					projectMapping.setTargetSystemKind(landscape.getType1());
					projectMapping.setSourceSystemTimezone(landscape.getTimezone2());
					projectMapping.setTargetSystemTimezone(landscape.getTimezone1());					
				}
				projectMapping.setSourceRepositoryId(taskTrackerId);
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-Task");
				projectMapping.setConflictResolutionPriority(previewPage.getTrackerTaskConflictResolutionPriority());
//				projectMapping.setSourceRepositoryKind("TRACKER");
				projectMapping.setSourceRepositoryKind("TemplateTasks.xsl");
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getTrackerPbiMapping());
				projectMapping.setSourceRepositoryId(pbiTrackerId);
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-PBI");
				projectMapping.setConflictResolutionPriority(previewPage.getTrackerPbiConflictResolutionPriority());
				projectMapping.setSourceRepositoryKind("TemplatePBIs.xsl");
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getPlanningFolderProductMapping());
				projectMapping.setSourceRepositoryId(projectId + "-planningFolders");
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-Product");
				projectMapping.setConflictResolutionPriority(previewPage.getPlanningFolderProductConflictResolutionPriority());
				projectMapping.setSourceRepositoryKind("TemplateProducts.xsl");
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getPlanningFolderProductReleaseMapping());
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-Release");
				projectMapping.setConflictResolutionPriority(previewPage.getPlanningFolderProductReleaseConflictResolutionPriority());
				projectMapping.setSourceRepositoryKind("TemplateReleases.xsl");
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getTaskTrackerMapping());
				if (landscape.getType1().equals(ScrumWorksCcfParticipant.TYPE)) {
					projectMapping.setSourceSystemId(landscape.getId1());	
					projectMapping.setTargetSystemId(landscape.getId2());			
					projectMapping.setSourceSystemKind(landscape.getType1());			
					projectMapping.setTargetSystemKind(landscape.getType2());
					projectMapping.setSourceSystemTimezone(landscape.getTimezone1());
					projectMapping.setTargetSystemTimezone(landscape.getTimezone2());
				} else {
					projectMapping.setSourceSystemId(landscape.getId2());	
					projectMapping.setTargetSystemId(landscape.getId1());			
					projectMapping.setSourceSystemKind(landscape.getType2());			
					projectMapping.setTargetSystemKind(landscape.getType1());
					projectMapping.setSourceSystemTimezone(landscape.getTimezone2());
					projectMapping.setTargetSystemTimezone(landscape.getTimezone1());					
				}
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-Task");
				projectMapping.setSourceRepositoryKind("TemplateTasks.xsl");
				projectMapping.setTargetRepositoryId(taskTrackerId);
				projectMapping.setConflictResolutionPriority(previewPage.getTaskTrackerConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getPbiTrackerMapping());
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-PBI");
				projectMapping.setSourceRepositoryKind("TemplatePBIs.xsl");
				projectMapping.setTargetRepositoryId(pbiTrackerId);
				projectMapping.setConflictResolutionPriority(previewPage.getPbiTrackerConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getProductPlanningFolderMapping());
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-Product");
				projectMapping.setSourceRepositoryKind("TemplateProducts.xsl");
				projectMapping.setTargetRepositoryId(projectId + "-planningFolders");
				projectMapping.setConflictResolutionPriority(previewPage.getProductPlanningFolderConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getProductReleasePlanningFolderMapping());
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-Release");
				projectMapping.setSourceRepositoryKind("TemplateReleases.xsl");
				projectMapping.setConflictResolutionPriority(previewPage.getProductReleasePlanningFolderConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			MessageDialog.openError(getShell(), "Create Project Mappings", e.getMessage());
			return false;
		}
		if (errors.size() > 0) {
			StringBuffer errorMessage = new StringBuffer();
			for (Exception error : errors) {
				if (errorMessage.length() > 0) {
					errorMessage.append("\n\n");
				}
				errorMessage.append(error.getMessage());
			}
			MessageDialog.openError(getShell(), "Create Project Mappings", errorMessage.toString());
			return false;
		}
		if (userMappingErrors) {
			MessageDialog.openError(getShell(), "Map Users", "Errors occurred mapping ScrumWorks users to TeamForge.  See error log for details.");
		}
		if (notCreated.size() > 0) {
			StringBuffer notCreatedMessage = new StringBuffer("The following mappings already existed and were not created:\n");
			for (String mapping : notCreated) {
				notCreatedMessage.append("\n" + mapping);
			}
			MessageDialog.openInformation(getShell(), "Create Project Mappings", notCreatedMessage.toString());
		}
		return true;
	}
	
	private String getFieldId(String value, TrackerFieldValueDO[] oldValues) {
		String id = null;
		for (TrackerFieldValueDO oldValue : oldValues) {
			if (oldValue.getValue().equals(value)) {
				id = oldValue.getId();
				break;
			}
		}
		return id;
	}
	
	public ProductWSO getSelectedProduct() {
		return productPage.getSelectedProduct();
	}

	public ProjectRow getSelectedProject() {
		return projectPage.getSelectedProject();
	}
	
	public TrackerRow getSelectedPbiTracker() {
		return trackerPage.getSelectedPbiTracker();
	}
	
	public TrackerRow getSelectedTaskTracker() {
		return trackerPage.getSelectedTaskTracker();
	}
	
	public ProjectMappings getProjectMappings() {
		return projectMappings;
	}
	
	public TFSoapClient getSoapClient() {
		if (soapClient == null) {
			Properties properties = null;
			if (landscape.getType1().equals("TF")) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}
			if (properties != null) {
				String serverUrl = properties.getProperty(Activator.PROPERTIES_SFEE_URL);
				String userId = properties.getProperty(Activator.PROPERTIES_SFEE_USER);
				String password = properties.getProperty(Activator.PROPERTIES_SFEE_PASSWORD);
				soapClient = TFSoapClient.getSoapClient(serverUrl, userId, password);
			}
		}
		return soapClient;
	}
	
	public ProductWSO[] getProducts() throws ServerException, RemoteException, ServiceException {
		return getScrumWorksEndpoint().getProducts();
	}
	
	private boolean mapUsers(ProductWSO product, String projectId, boolean newProject, IProgressMonitor monitor) {
		boolean errors = false;
		monitor.subTask("Getting product " + product.getName() + " users");
		List<String> productUserList = new ArrayList<String>();
		try {
			SprintWSO[] sprints = getScrumWorksEndpoint().getSprints(product);
			for (SprintWSO sprint : sprints) {
				String[] sprintUsers = getScrumWorksEndpoint().getUsersForSprint(sprint);
				if (sprintUsers != null) {
					for (String sprintUser : sprintUsers) {
						if (!productUserList.contains(sprintUser)) {
							productUserList.add(sprintUser);
						}
					}
				}
			}
		} catch (Exception e) {
			Activator.handleError(e);
			errors = true;
		}
		monitor.worked(1);
		if (errors) {
			monitor.worked(2);
		} else {			
			List<String> projectMemberList = new ArrayList<String>();
			if (!newProject) {
				monitor.subTask("Getting project members");	
				try {
					ProjectMemberList memberList = getSoapClient().getProjectMemberList(projectId);
					ProjectMemberRow[] memberRows = memberList.getDataRows();
					for (ProjectMemberRow memberRow : memberRows) {
						projectMemberList.add(memberRow.getUserName());
					}
				} catch (Exception e) {
					Activator.handleError(e);
					errors = true;
				}
				monitor.worked(1);
			}
			if (!errors) {
				monitor.subTask("Creating TeamForge users");
				try {
					List<String> newUsers = new ArrayList<String>();
					UserWSO[] swpUsers = getScrumWorksEndpoint().getUsers();
					for (UserWSO swpUser : swpUsers) {
						if (productUserList.contains(swpUser.getDisplayName())) {
							UserDO userDO = null;
							try {
								userDO = getSoapClient().getUserData(swpUser.getUserName());
							} catch (Exception e) {}
							if (userDO == null) {
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
							} else {
								// If user already exists but is not active (i.e., deleted status), activate user.
								if (!userDO.getStatus().equals("Active")) {
									userDO.setStatus("Active");
									try {
										getSoapClient().setUserData(userDO);
									} catch (Exception e) {
										Activator.handleError(e);
										errors = true;
									}
								}
							}
							if (!projectMemberList.contains(swpUser.getUserName())) {
								try {
									getSoapClient().addProjectMember(projectId, swpUser.getUserName());
									newUsers.add(swpUser.getUserName());
								} catch (Exception e) {
									Activator.handleError(e);
									errors = true;
								}
							}
						}
					}
					if (newUsers != null && newUsers.size() > 0) {
						createRole(projectId, newUsers);
					}
				} catch (Exception e) {
					Activator.handleError(e);
					errors = true;
				}
			}
			monitor.worked(1);
		}
		return !errors;
	}
	
	private void createRole(String projectId, List<String> users) throws RemoteException {
		RoleDO roleDO = getSoapClient().createRole(projectId, PRODUCT_DEVELOPER_ROLE_TITLE, PRODUCT_DEVELOPER_ROLE_DESCRIPTION);
		getSoapClient().addCluster(roleDO.getId(), RbacClient.TRACKER_CREATE, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.TRACKER_EDIT, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.PAGE_VIEW, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.DOCMAN_CREATE, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.DOCMAN_EDIT, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.SCM_COMMIT, "");
		getSoapClient().addCluster(roleDO.getId(), RbacClient.DISCUSSION_PARTICIPATE, "");
		for (String username : users) {
			getSoapClient().addUser(roleDO.getId(), username);
		}
	}
	
	private ThemeWSO[] getThemes(ProductWSO product) throws ServerException, RemoteException, ServiceException {
		return getScrumWorksEndpoint().getThemes(product);
	}
	
	private String[] getThemeValues() throws ServerException, RemoteException, ServiceException {
		List<String> themeList = new ArrayList<String>();
		ThemeWSO[] themes = getThemes(getSelectedProduct());
		if (themes != null) {
			for (ThemeWSO theme : themes) {
				themeList.add(theme.getName());
			}
		}
		String[] themeValues = new String[themeList.size()];
		themeList.toArray(themeValues);
		return themeValues;
	}
	
	private ScrumWorksEndpoint getScrumWorksEndpoint() throws ServiceException {
		if (scrumWorksEndpoint == null) {
			Properties properties = null;
			if (landscape.getType1().equals(ScrumWorksCcfParticipant.TYPE)) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}	
			String url = properties.get(Activator.PROPERTIES_SW_URL).toString();
			String user = properties.get(Activator.PROPERTIES_SW_USER).toString();
			String password = properties.get(Activator.PROPERTIES_SW_PASSWORD).toString();
			if (!url.endsWith("scrumworks-api/scrumworks")) {
				if (!url.endsWith("/")) {
					url = url + "/";
				}
				url = url + "scrumworks-api/scrumworks";
			}
			ScrumWorksServiceLocator locator = new ScrumWorksServiceLocator();
			locator.setScrumWorksEndpointPortEndpointAddress(url);
			scrumWorksEndpoint = locator.getScrumWorksEndpointPort();
			((ScrumWorksEndpointBindingStub) scrumWorksEndpoint).setUsername(user);
			((ScrumWorksEndpointBindingStub) scrumWorksEndpoint).setPassword(password);
		}
		return scrumWorksEndpoint;
	}

	private void createMapping(SynchronizationStatus status, CcfDataProvider dataProvider) {
		if (existingMappings.contains(status)) {
			notCreated.add(status.toString());
			return;
		}
		if (!status.getSourceSystemKind().endsWith("_paused")) {
			status.setSourceSystemKind(status.getSourceSystemKind() + "_paused");
		}
		try {
			dataProvider.addSynchronizationStatus(landscape, status);
		} catch (Exception e) {
			Activator.handleError(e);
			errors.add(e);
			return;
		}
		if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
			createFieldMappingFile(status);
		}
	}
	
	private void createFieldMappingFile(SynchronizationStatus status) {
//		if (status.getSourceRepositoryKind().startsWith("Template")) {
//			return;
//		}
		status.setLandscape(landscape);
		status.clearXslInfo();
		File xslFile = status.getXslFile();
		if (!xslFile.exists()) {
			try {
				xslFile.createNewFile();
				File sampleFile = status.getSampleXslFile();
				if (sampleFile != null && sampleFile.exists()) {
					CcfDataProvider.copyFile(sampleFile, xslFile);
				}
			} catch (IOException e) {
				Activator.handleError(e);
				errors.add(e);
			}
		}
	}
	
	private void getExistingMappings(CcfDataProvider dataProvider) {
		try {
			SynchronizationStatus[] existingMappingsArray = dataProvider.getSynchronizationStatuses(landscape, projectMappings);
			for (SynchronizationStatus mapping : existingMappingsArray) {
				existingMappings.add(mapping);
			}
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}

}
