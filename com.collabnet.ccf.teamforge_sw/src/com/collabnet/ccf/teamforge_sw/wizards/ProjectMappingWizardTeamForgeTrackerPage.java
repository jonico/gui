package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.teamforge.api.tracker.TrackerRow;

public class ProjectMappingWizardTeamForgeTrackerPage extends WizardPage {
	private Map<String, TrackerRow[]> trackerMap = new HashMap<String, TrackerRow[]>();
	private TrackerRow[] trackers;
	private Button newPbiTrackerButton;
	private Text newPbiTrackerText;
	private Table pbiTable;
	private TableViewer pbiViewer;
	private Button newTaskTrackerButton;
	private Text newTaskTrackerText;
	private Table taskTable;
	private TableViewer taskViewer;
	private TrackerRow selectedPbiTracker;
	private TrackerRow selectedTaskTracker;
	private String projectId;
	private String newPbiTrackerTitle;
	private String newTaskTrackerTitle;
	private Exception getTrackersError;
	
	private String[] columnHeaders = {"Tracker"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardTeamForgeTrackerPage() {
		super("trackerPage", "TeamForge Trackers", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group pbiGroup = new Group(outerContainer, SWT.NONE);
		GridLayout pbiLayout = new GridLayout();
		pbiLayout.numColumns = 2;
		pbiGroup.setLayout(pbiLayout);
		pbiGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		pbiGroup.setText("PBI Tracker:");
		
		newPbiTrackerButton = new Button(pbiGroup, SWT.CHECK);
		newPbiTrackerButton.setText("Create new tracker:");
		newPbiTrackerButton.setSelection(true);
		newPbiTrackerText = new Text(pbiGroup, SWT.BORDER);
		newPbiTrackerText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		newPbiTrackerTitle = "PBIs";
		newPbiTrackerText.setText(newPbiTrackerTitle);
		
		pbiTable = createTable(pbiGroup);	
		pbiViewer = createViewer(pbiTable);
		
		Group taskGroup = new Group(outerContainer, SWT.NONE);
		GridLayout taskLayout = new GridLayout();
		taskLayout.numColumns = 2;
		taskGroup.setLayout(taskLayout);
		taskGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		taskGroup.setText("Task Tracker:");
		
		newTaskTrackerButton = new Button(taskGroup, SWT.CHECK);
		newTaskTrackerButton.setText("Create new tracker:");
		newTaskTrackerButton.setSelection(true);
		newTaskTrackerText = new Text(taskGroup, SWT.BORDER);
		newTaskTrackerText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		newTaskTrackerTitle = "Tasks";
		newTaskTrackerText.setText(newTaskTrackerTitle);
		
		taskTable = createTable(taskGroup);	
		taskViewer = createViewer(taskTable);
		
		SelectionListener selectionListener = new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent event) {
				if (event.getSource() == newPbiTrackerButton) {
					if (newPbiTrackerButton.getSelection()) {
						pbiViewer.getTable().deselectAll();
					}
				}
				if (event.getSource() == newTaskTrackerButton) {
					if (newTaskTrackerButton.getSelection()) {
						taskViewer.getTable().deselectAll();
					}
				}
				setPageComplete(canFinish());
			}
		};
		
		newPbiTrackerButton.addSelectionListener(selectionListener);
		newTaskTrackerButton.addSelectionListener(selectionListener);
		
		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSource() == pbiViewer) {
					IStructuredSelection pbiSelection = (IStructuredSelection)pbiViewer.getSelection();
					if (!pbiSelection.isEmpty()) {
						newPbiTrackerButton.setSelection(false);
					}
				}
				if (event.getSource() == taskViewer) {
					IStructuredSelection taskSelection = (IStructuredSelection)taskViewer.getSelection();
					if (!taskSelection.isEmpty()) {
						newTaskTrackerButton.setSelection(false);
					}
				}
				setPageComplete(canFinish());
			}		
		};
		
		pbiViewer.addSelectionChangedListener(selectionChangedListener);
		taskViewer.addSelectionChangedListener(selectionChangedListener);
		
		ModifyListener modifyListener = new ModifyListener() {	
			public void modifyText(ModifyEvent event) {
				newPbiTrackerTitle = newPbiTrackerText.getText().trim();
				newTaskTrackerTitle = newTaskTrackerText.getText().trim();
				setPageComplete(canFinish());
			}
		};
		
		newPbiTrackerText.addModifyListener(modifyListener);
		newTaskTrackerText.addModifyListener(modifyListener);

		setMessage("Select the TeamForge trackers to be mapped.");

		setControl(outerContainer);
	}
	
	private boolean canFinish() {
		setErrorMessage(null);
		IStructuredSelection pbiSelection = (IStructuredSelection)pbiViewer.getSelection();
		IStructuredSelection taskSelection = (IStructuredSelection)taskViewer.getSelection();
		selectedPbiTracker = (TrackerRow)pbiSelection.getFirstElement();
		selectedTaskTracker = (TrackerRow)taskSelection.getFirstElement();
		boolean pageComplete;
		if (!newPbiTrackerButton.getSelection() && pbiSelection.isEmpty()) {
			pageComplete = false;
		}
		else if (newPbiTrackerButton.getSelection() && newPbiTrackerText.getText().trim().length() == 0) {
			pageComplete = false;
		}
		else if (!newTaskTrackerButton.getSelection() && taskSelection.isEmpty()) {
			pageComplete = false;
		}
		else if (newTaskTrackerButton.getSelection() && newTaskTrackerText.getText().trim().length() == 0) {
			pageComplete = false;
		}
		else if (!newPbiTrackerButton.getSelection() && !newTaskTrackerButton.getSelection() && selectedPbiTracker.getId().equals(selectedTaskTracker.getId())) {
			pageComplete = false;
			setErrorMessage("PBI and Task trackers cannot be the same.");
		}
		else {
			pageComplete = true;
		}
		newPbiTrackerText.setEnabled(newPbiTrackerButton.getSelection());
		newTaskTrackerText.setEnabled(newTaskTrackerButton.getSelection());
		return pageComplete;
	}

	private Table createTable(Group pbiGroup) {
		Table table = new Table(pbiGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 200;
		gd.horizontalSpan = 2;
		table.setLayoutData(gd);
		TableLayout tableLayout = new TableLayout();
		for (int i = 0; i < columnHeaders.length; i++) {
			tableLayout.addColumnData(columnLayouts[i]);
			TableColumn tc = new TableColumn(table, SWT.NONE,i);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setText(columnHeaders[i]);
		}
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		return table;
	}
	
	private TableViewer createViewer(Table table) {
		TableViewer viewer = new TableViewer(table);		
		viewer.setContentProvider(new TrackersContentProvider());
		viewer.setLabelProvider(new TrackersLabelProvider());
		viewer.setInput(this);
		return viewer;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {		
			if (projectId == null || !projectId.equals(((ProjectMappingWizard)getWizard()).getSelectedProject().getId())) {
				projectId = ((ProjectMappingWizard)getWizard()).getSelectedProject().getId();
				trackers = getTrackers(((ProjectMappingWizard)getWizard()).getSelectedProject().getId());
				pbiViewer.refresh();
				taskViewer.refresh();
			}
		}
	}

	public TrackerRow getSelectedPbiTracker() {
		return selectedPbiTracker;
	}
	
	public TrackerRow getSelectedTaskTracker() {
		return selectedTaskTracker;
	}

	public String getNewPbiTrackerTitle() {
		return newPbiTrackerTitle;
	}

	public String getNewTaskTrackerTitle() {
		return newTaskTrackerTitle;
	}

	private TrackerRow[] getTrackers(final String projectId) {
		getTrackersError = null;
		trackers = trackerMap.get(projectId);
		if (trackers == null) {
			
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					String taskName = "Retrieving TeamForge trackers";
					monitor.setTaskName(taskName);
					monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
					monitor.subTask("");
					try {
						trackers = ((ProjectMappingWizard)getWizard()).getSoapClient().getAllTrackersOfProject(projectId);
						if (trackers != null) {
							trackerMap.put(projectId, trackers);
						}
					} catch (RemoteException e) {
						Activator.handleError(e);
						getTrackersError = e;
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
			if (getTrackersError != null) {
				setErrorMessage("An unexpected error occurred while getting TeamForge trackers.  See error log for details.");
			}

		}
		return trackers;
	}
	
	static class TrackersLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			TrackerRow tracker = (TrackerRow)element;
			switch (columnIndex) { 
				case 0: return tracker.getTitle();
			}
			return "";  //$NON-NLS-1$
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
	}	
	
	class TrackersContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Object[] getElements(Object obj) {
			if (trackers == null) {
				return new TrackerRow[0];
			}
			return trackers;
		}
	}	

}
