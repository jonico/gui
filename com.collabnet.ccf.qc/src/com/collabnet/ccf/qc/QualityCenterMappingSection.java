package com.collabnet.ccf.qc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.qc.dialogs.RequirementTypeSelectionDialog;
import com.collabnet.ccf.qc.schemageneration.QCLayoutExtractor;

public class QualityCenterMappingSection extends MappingSection {
	private Combo domainCombo;
	private Text projectText;
	private Label requirementTypeLabel;
	private Text requirementTypeText;
	private Button requirementTypeBrowseButton;
	private Button defectsButton;
	private Button requirementsButton;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private static final String PREVIOUS_TYPE = "QualityCenterMappingSection.type";
	public static final String PREVIOUS_DOMAIN = "QualityCenterMappingSection.previousDomain.";
	public static final String PREVIOUS_DOMAIN_COUNT = "QualityCenterMappingSection.previousDomainCount";
	private static final int TYPE_DEFECTS = 0;
	private static final int TYPE_REQUIREMENTS = 1;
	
	public void updateSourceFields(SynchronizationStatus projectMapping) {
		projectMapping.setSourceRepositoryId(getRepositoryId());
		projectMapping.setSourceRepositoryKind("DEFECT");
	}
	
	public void updateTargetFields(SynchronizationStatus projectMapping) {
		projectMapping.setTargetRepositoryId(getRepositoryId());
		projectMapping.setTargetRepositoryKind("DEFECT");
	}
	
	private String getRepositoryId() {
		StringBuffer repositoryId = new StringBuffer(domainCombo.getText().trim() + "-" + projectText.getText().trim());
		if (requirementsButton.getSelection()) {
			repositoryId.append("-" + requirementTypeText.getText().trim());
		}
		return repositoryId.toString();
	}
	
	public Composite getComposite(Composite parent, final Landscape landscape) {
		Group qcGroup = new Group(parent, SWT.NULL);
		GridLayout qcLayout = new GridLayout();
		qcLayout.numColumns = 3;
		qcGroup.setLayout(qcLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcGroup.setLayoutData(gd);	
		
		if (landscape.getType1().equals("QC") && landscape.getType2().equals("QC")) {
			if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
				String url;
				if (getSystemNumber() == 1) {
					url = landscape.getProperties1().getProperty(QualityCenterCcfParticipant.PROPERTIES_QC_URL);
				} else {
					url = landscape.getProperties2().getProperty(QualityCenterCcfParticipant.PROPERTIES_QC_URL);
				}
				qcGroup.setText("Quality Center (" + url + "):");
			} else {
				qcGroup.setText("Quality Center " + getSystemNumber());
			}
		} else {
			qcGroup.setText("Quality Center:");
		}		
		
		Label domainLabel = new Label(qcGroup, SWT.NONE);
		domainLabel.setText("Domain:");

		domainCombo = new Combo(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		domainCombo.setLayoutData(gd);	
		
		String[] previousDomains = getPreviousDomains();
		for (String domain : previousDomains) {
			domainCombo.add(domain);
		}
		if (previousDomains.length > 0) domainCombo.setText(previousDomains[0]);
		
		new Label(qcGroup, SWT.NONE);
		
		Label projectLabel = new Label(qcGroup, SWT.NONE);
		projectLabel.setText("Project:");
	
		projectText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		projectText.setLayoutData(gd);	
		
		new Label(qcGroup, SWT.NONE);
		
		defectsButton = new Button(qcGroup, SWT.RADIO);
		defectsButton.setText("Defects");
		gd = new GridData();
		gd.horizontalSpan = 3;
		defectsButton.setLayoutData(gd);
		requirementsButton = new Button(qcGroup, SWT.RADIO);
		requirementsButton.setText("Requirements");
		gd = new GridData();
		gd.horizontalSpan = 3;
		requirementsButton.setLayoutData(gd);
	
		int qcType;
		try {
			qcType = settings.getInt(PREVIOUS_TYPE);
		} catch (Exception e) { qcType = TYPE_DEFECTS; }
		switch (qcType) {
		case TYPE_REQUIREMENTS:
			requirementsButton.setSelection(true);
			break;
		default:
			defectsButton.setSelection(true);
			break;
		}		
	
		requirementTypeLabel = new Label(qcGroup, SWT.NONE);
		requirementTypeLabel.setText("Requirement type:");
		requirementTypeLabel.setVisible(requirementsButton.getSelection());
		requirementTypeText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		requirementTypeText.setLayoutData(gd);	
		requirementTypeText.setVisible(requirementsButton.getSelection());
		requirementTypeBrowseButton = new Button(qcGroup, SWT.PUSH);
		requirementTypeBrowseButton.setText("Browse...");
		requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsButton.getSelection());
		requirementTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (!validate(landscape)) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Select Requirement Type", "Invalid Quality Center Domain/Project entered.");
					return;
				}
				RequirementTypeSelectionDialog dialog = new RequirementTypeSelectionDialog(Display.getDefault().getActiveShell(), landscape, domainCombo.getText().trim(), projectText.getText().trim());
				if (dialog.open() == RequirementTypeSelectionDialog.OK) {
					requirementTypeText.setText(dialog.getType());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});
		
		if (landscape.getRole() == Landscape.ROLE_OPERATOR) {
			requirementTypeBrowseButton.setEnabled(false);
		}
	
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		projectText.addModifyListener(modifyListener);
		domainCombo.addModifyListener(modifyListener);
		requirementTypeText.addModifyListener(modifyListener);
		
		domainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		});
		
		SelectionListener typeListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (requirementsButton.getSelection()) {
					settings.put(PREVIOUS_TYPE, TYPE_REQUIREMENTS);
					requirementTypeText.setFocus();
				} else {
					settings.put(PREVIOUS_TYPE, TYPE_DEFECTS);
				}
				requirementTypeLabel.setVisible(requirementsButton.getSelection());
				requirementTypeText.setVisible(requirementsButton.getSelection());
				requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsButton.getSelection());
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		defectsButton.addSelectionListener(typeListener);
		requirementsButton.addSelectionListener(typeListener);		
		
		return qcGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		domainCombo.setText(getDomain(projectMapping, type));
		projectText.setText(getProject(projectMapping, type));
		String requirementType = getRequirementType(projectMapping, type);
		if (requirementType != null) {
			requirementTypeLabel.setVisible(true);
			requirementTypeText.setVisible(true);
			requirementTypeBrowseButton.setVisible(true);
			requirementTypeText.setText(requirementType);
			requirementsButton.setSelection(true);
			defectsButton.setSelection(false);
		} else {
			requirementTypeLabel.setVisible(false);
			requirementTypeText.setVisible(false);
			requirementTypeBrowseButton.setVisible(false);
			requirementsButton.setSelection(false);
			defectsButton.setSelection(true);
		}
	}

	public boolean isPageComplete() {
		if (domainCombo == null) {
			return false;
		}
		if (domainCombo.getText().trim().length() == 0 ||
			projectText.getText().trim().length() == 0) {
			return false;
		}
		if (requirementTypeText.isVisible() && requirementTypeText.getText().trim().length() == 0) {
			return false;
		}
		return true;
	}

	public boolean validate(Landscape landscape) {
		// Only validate on windows.
		if (landscape.getRole() == Landscape.ROLE_OPERATOR || !"win32".equals(SWT.getPlatform())) return true;
		QCLayoutExtractor qcLayoutExtractor = new QCLayoutExtractor();
		Properties properties = landscape.getProperties1();
		String url = properties.getProperty(Activator.PROPERTIES_QC_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_QC_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_QC_PASSWORD, "");
		qcLayoutExtractor.setServerUrl(url);
		qcLayoutExtractor.setUserName(user);
		qcLayoutExtractor.setPassword(password);
		
		boolean validDomainAndProject;
		try {
			qcLayoutExtractor.validateQCDomainAndProject(domainCombo.getText().trim(), projectText.getText().trim());
			validDomainAndProject = true;
		} catch (Exception e) {
			validDomainAndProject = false;
		}
		if (!validDomainAndProject) {
			if (!showValidationQuestionDialog("Invalid Quality Center Domain/Project entered.  Add project mapping anyway?")) {
				return false;
			}
		}
		return true;
	}
	
	private String[] getPreviousDomains() {
		List<String> domainList = new ArrayList<String>();
		int count = 0;
		try {
			count = settings.getInt(PREVIOUS_DOMAIN_COUNT);
		} catch (Exception e) {}
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				String domain = settings.get(PREVIOUS_DOMAIN + i);
				if (domain != null) domainList.add(domain);
			}
		}
		String[] domains = new String[domainList.size()];
		domainList.toArray(domains);
		return domains;
	}
	
	private String getDomain(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else return repositoryId.substring(0, index);
	}
	
	private String getProject(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else {
			String project = repositoryId = repositoryId.substring(index + 1);
			index = project.indexOf("-");
			if (index != -1) {
				project = project.substring(0, index);
			}
			return project;
		}
	}
	
	private String getRequirementType(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index != -1) {
			String project = repositoryId.substring(index + 1);
			index = project.indexOf("-");
			if (index != -1) {
				return project.substring(index + 1);
			}
		}
		return null;
	}	

}
