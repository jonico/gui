package com.collabnet.ccf.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.schemageneration.PTClient;

public class ProjectTrackerSelectionDialog extends CcfDialog {
	private Landscape landscape;
	private int type;
	private TreeViewer treeViewer;
	
	private String title;
	
	private PTClient ptClient;
	private Project[] projects;
	private ArtifactType[] artifactTypes;
	private String projectName;
	private String artifactType;
	
	public static final int BROWSER_TYPE_PROJECT = 0;
	public static final int BROWSER_TYPE_ARTIFACT_TYPE = 1;
	
	private Button okButton;

	public ProjectTrackerSelectionDialog(Shell shell, Landscape landscape, int type) {
		super(shell, "ProjectTrackerSelectionDialog");
		this.landscape = landscape;
		this.type = type;
	}
	
	protected Control createDialogArea(Composite parent) {
		switch (type) {
		case BROWSER_TYPE_PROJECT:
			title = "Select Project";
			break;
		default:
			title = "Select Artifact Type";
			break;
		}
		
		getShell().setText(title);
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));	
		
		treeViewer = new TreeViewer(composite);
		treeViewer.setLabelProvider(new ProjectTrackerSelectionLabelProvider());
		treeViewer.setContentProvider(new ProjectTrackerSelectionContentProvider());
		treeViewer.setUseHashlookup(true);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
		layoutData.heightHint = 300;
		layoutData.widthHint = 300;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(this);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (okButton != null) {
					okButton.setEnabled(canFinish());
				}
			}		
		});
		
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                if (okButton.isEnabled()) okPressed();
            }
        });  
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		Object firstSelection = selection.getFirstElement();
		if (firstSelection instanceof Project) {
			projectName = ((Project)firstSelection).getName();
		}
		if (firstSelection instanceof ArtifactType) {
			projectName = ((ArtifactType)firstSelection).getProjectName();
			artifactType = ((ArtifactType)firstSelection).getName();
		}
		super.okPressed();
	}
	
	public String getProjectName() {
		return projectName;
	}

	public String getArtifactType() {
		return artifactType;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			okButton.setEnabled(false);
		}
        return button;
    }
	
	private boolean canFinish() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if (selection.isEmpty()) return false;
		Object firstSelection = selection.getFirstElement();
		if (type == BROWSER_TYPE_PROJECT) return (firstSelection instanceof Project);
		else return (!(firstSelection instanceof Project));
	}
	
	private Project[] getProjects() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				projects = null;
				try {
					List<String> projectList = getClient().getProjects(PTClient.PROJECT_TYPE_PT);
					projects = new Project[projectList.size()];
					int i = 0;
					for (String projectName : projectList) {
						projects[i++] = new Project(projectName, getProjectUrl(projectName));
					}
				} catch (Exception e) {
					MessageDialog.openError(getShell(), title, e.getMessage());
				}
			}
		});
		if (projects == null) return new Project[0];
		else return projects;
	}
	
	private ArtifactType[] getArtifactTypes(final Project project) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				artifactTypes = null;
				try {
					List<String> artifactTypeList = getClient().getArtifactTypes(project.getUrl());
					artifactTypes = new ArtifactType[artifactTypeList.size()];
					int i = 0;
					for (String artifactTypeName : artifactTypeList) {
						artifactTypes[i++] = new ArtifactType(artifactTypeName, project.getName());
					}
				} catch (Exception e) {
					MessageDialog.openError(getShell(), title, e.getMessage());
				}
			}
		});
		if (artifactTypes == null) return new ArtifactType[0];
		else return artifactTypes;		
	}
	
	private String getProjectUrl(String projectName) {
		String baseurl = landscape.getCeeProperties().getProperty(Activator.PROPERTIES_CEE_URL);
		int prefixIndex = baseurl.indexOf("//"); //$NON-NLS-1$
		int postfixIndex = baseurl.indexOf("."); //$NON-NLS-1$
		
		return baseurl.substring(0, prefixIndex+2) 
		 	+ projectName
		 	+ baseurl.substring(postfixIndex);		
	}
	
	private PTClient getClient() {
		if (ptClient == null) {
			String serverUrl = landscape.getCeeProperties().getProperty(Activator.PROPERTIES_CEE_URL);
			String userId = landscape.getCeeProperties().getProperty(Activator.PROPERTIES_CEE_USER);
			String password = landscape.getCeeProperties().getProperty(Activator.PROPERTIES_CEE_PASSWORD);
			ptClient = PTClient.getClient(serverUrl, userId, password);
		}
		return ptClient;
	}
	
	class ProjectTrackerSelectionLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return null;
		}
		public String getText(Object element) {
			if (element instanceof Project) {
				return ((Project)element).getName();
			}
			if (element instanceof ArtifactType) {
				return ((ArtifactType)element).getName();
			}
			return super.getText(element);
		}
	}
	
	class ProjectTrackerSelectionContentProvider extends WorkbenchContentProvider {
		public Object[] getChildren(Object element) {
			if (element instanceof ProjectTrackerSelectionDialog) {
				return getProjects();
			}
			if (element instanceof Project) {
				return getArtifactTypes((Project)element);
			}
			return new Object[0];
		}
		public Object[] getElements(Object element) {
			return getChildren(element);
		}
		public boolean hasChildren(Object element) {
			if (element instanceof Project) {
				return type == BROWSER_TYPE_ARTIFACT_TYPE;
			}
			return false;
		}
	}
	
	class Project {
		private String name;
		private String url;
		
		public Project(String name, String url) {
			this.name = name;
			this.url = url;
		}
		
		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}
		
	}
	
	class ArtifactType {
		private String name;
		private String projectName;
		
		public ArtifactType(String name, String projectName) {
			super();
			this.name = name;
			this.projectName = projectName;
		}

		public String getName() {
			return name;
		}

		public String getProjectName() {
			return projectName;
		}
		
	}

}
