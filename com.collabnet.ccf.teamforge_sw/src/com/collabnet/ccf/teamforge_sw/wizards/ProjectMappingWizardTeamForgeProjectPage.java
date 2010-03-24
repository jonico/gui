package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.collabnet.ccf.Activator;
import com.collabnet.teamforge.api.main.ProjectRow;

public class ProjectMappingWizardTeamForgeProjectPage extends WizardPage {
	private ProjectRow[] projects;
	private Table table;
	private TableViewer viewer;
	private ProjectRow selectedProject;
	private boolean projectsRetrieved;
	
	private String[] columnHeaders = {"Project"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardTeamForgeProjectPage() {
		super("projectPage", "TeamForge Project", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		setMessage("Select the TeamForge project to be mapped.");
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		table = new Table(outerContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 200;
		table.setLayoutData(gd);
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		viewer = new TableViewer(table);		
		viewer.setContentProvider(new ProjectsContentProvider());
		viewer.setLabelProvider(new ProjectsLabelProvider());
		for (int i = 0; i < columnHeaders.length; i++) {
			tableLayout.addColumnData(columnLayouts[i]);
			TableColumn tc = new TableColumn(table, SWT.NONE,i);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setText(columnHeaders[i]);
		}
		viewer.setInput(this);	
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection projectSelection = (IStructuredSelection)viewer.getSelection();
				setPageComplete(!projectSelection.isEmpty());
				selectedProject = (ProjectRow)projectSelection.getFirstElement();
			}		
		});	
		viewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				ProjectRow p1 = (ProjectRow)e1;
				ProjectRow p2 = (ProjectRow)e2;
				return p1.getTitle().compareTo(p2.getTitle());
			}			
		});

		setControl(outerContainer);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible && projects == null && !projectsRetrieved) {
			projectsRetrieved = true;
			getProjects();
			viewer.refresh();
		}
		super.setVisible(visible);
	}
	
	public ProjectRow getSelectedProject() {
		return selectedProject;
	}

	private ProjectRow[] getProjects() {
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				String taskName = "Retrieving TeamForge projects";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				monitor.subTask("");
				try {
					projects = ((ProjectMappingWizard)getWizard()).getSoapClient().getAllProjects();
				} catch (RemoteException e) {
					Activator.handleError(e);
					setErrorMessage(e.getMessage());
				}
				monitor.done();
			}
		};
		
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			setErrorMessage(e.getMessage());
		}

		return projects;
	}

	static class ProjectsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			ProjectRow project = (ProjectRow)element;
			switch (columnIndex) { 
				case 0: return project.getTitle();
			}
			return "";  //$NON-NLS-1$
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
	}	
	
	class ProjectsContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Object[] getElements(Object obj) {
			if (projects == null) {
				return new ProjectRow[0];
			}
			return projects;
		}
	}	

}
