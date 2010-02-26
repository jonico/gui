package com.collabnet.ccf.pt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.pt.dialogs.ProjectTrackerSelectionDialog;

public class ProjectTrackerMappingSection extends MappingSection {
	private Text projectText;
	private Text issueTypeText;
	private Button projectBrowseButton;
	private Button artifactTypeBrowseButton;

	public void updateSourceFields(SynchronizationStatus projectMapping) {
		projectMapping.setSourceRepositoryId(getRepositoryId());
		projectMapping.setSourceRepositoryKind("TRACKER");
	}
	
	public void updateTargetFields(SynchronizationStatus projectMapping) {
		projectMapping.setTargetRepositoryId(getRepositoryId());
		projectMapping.setTargetRepositoryKind("TRACKER");
	}
	
	private String getRepositoryId() {
		return projectText.getText().trim() + ":" + issueTypeText.getText().trim();
	}

	public Composite getComposite(Composite parent, final Landscape landscape) {
		Group ptGroup = new Group(parent, SWT.NULL);
		GridLayout ptLayout = new GridLayout();
		ptLayout.numColumns = 3;
		ptGroup.setLayout(ptLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		ptGroup.setLayoutData(gd);	
		ptGroup.setText("Project Tracker:");			
		
		Label ptProjectLabel = new Label(ptGroup, SWT.NONE);
		ptProjectLabel.setText("Project:");			
		projectText = new Text(ptGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		projectText.setLayoutData(gd);
		
		projectBrowseButton = new Button(ptGroup, SWT.PUSH);
		projectBrowseButton.setText("Browse...");
		projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				ProjectTrackerSelectionDialog dialog = new ProjectTrackerSelectionDialog(Display.getDefault().getActiveShell(), landscape, ProjectTrackerSelectionDialog.BROWSER_TYPE_PROJECT, getSystemNumber());
				if (dialog.open() == ProjectTrackerSelectionDialog.OK) {
					projectText.setText(dialog.getProjectName());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});
		
		Label ptIssueTypeLabel = new Label(ptGroup, SWT.NONE);
		ptIssueTypeLabel.setText("Artifact type:");			
		issueTypeText = new Text(ptGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		issueTypeText.setLayoutData(gd);
		
		artifactTypeBrowseButton = new Button(ptGroup, SWT.PUSH);
		artifactTypeBrowseButton.setText("Browse...");
		artifactTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				ProjectTrackerSelectionDialog dialog = new ProjectTrackerSelectionDialog(Display.getDefault().getActiveShell(), landscape, ProjectTrackerSelectionDialog.BROWSER_TYPE_ARTIFACT_TYPE, getSystemNumber());
				if (dialog.open() == ProjectTrackerSelectionDialog.OK) {
					projectText.setText(dialog.getProjectName());
					issueTypeText.setText(dialog.getArtifactType());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};

		projectText.addModifyListener(modifyListener);
		issueTypeText.addModifyListener(modifyListener);
		return ptGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		projectText.setText(getProject(projectMapping, type));
		issueTypeText.setText(getIssueType(projectMapping, type));
	}

	public boolean isPageComplete() {
		if (projectText == null) {
			return false;
		}
		if (projectText.getText().trim().length() == 0 ||
			issueTypeText.getText().trim().length() == 0) {
			return false;
		}
		return true;
	}

	public boolean validate(Landscape landscape) {
		return true;
	}
	
	private String getProject(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf(":");
		if (index == -1) return "";
		else return repositoryId.substring(0, index);
	}
	
	private String getIssueType(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf(":");
		if (index == -1) return "";
		else return repositoryId.substring(index + 1);
	}

}
