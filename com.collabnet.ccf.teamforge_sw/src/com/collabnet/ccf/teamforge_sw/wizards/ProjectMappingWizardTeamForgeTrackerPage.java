package com.collabnet.ccf.teamforge_sw.wizards;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.collabnet.ccf.Activator;
import com.collabnet.teamforge.api.tracker.TrackerRow;

public class ProjectMappingWizardTeamForgeTrackerPage extends WizardPage {
	private Map<String, TrackerRow[]> trackerMap = new HashMap<String, TrackerRow[]>();
	private TrackerRow[] trackers;
	private Table pbiTable;
	private TableViewer pbiViewer;
	private Table taskTable;
	private TableViewer taskViewer;
	private TrackerRow selectedPbiTracker;
	private TrackerRow selectedTaskTracker;
	private String projectId;
	
	private String[] columnHeaders = {"Tracker"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardTeamForgeTrackerPage() {
		super("trackerPage", "TeamForge Trackers", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group pbiGroup = new Group(outerContainer, SWT.NONE);
		pbiGroup.setLayout(new GridLayout());
		pbiGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		pbiGroup.setText("PBI Tracker:");
		
		pbiTable = createTable(pbiGroup);	
		pbiViewer = createViewer(pbiTable);
		
		Group taskGroup = new Group(outerContainer, SWT.NONE);
		taskGroup.setLayout(new GridLayout());
		taskGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		taskGroup.setText("Task Tracker:");
		
		taskTable = createTable(taskGroup);	
		taskViewer = createViewer(taskTable);
		
		ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setErrorMessage(null);
				IStructuredSelection pbiSelection = (IStructuredSelection)pbiViewer.getSelection();
				IStructuredSelection taskSelection = (IStructuredSelection)taskViewer.getSelection();
				selectedPbiTracker = (TrackerRow)pbiSelection.getFirstElement();
				selectedTaskTracker = (TrackerRow)taskSelection.getFirstElement();
				boolean pageComplete;
				if (pbiSelection.isEmpty() || taskSelection.isEmpty()) {
					pageComplete = false;
				} else {
					if (selectedPbiTracker.getId().equals(selectedTaskTracker.getId())) {
						pageComplete = false;
						setErrorMessage("PBI and Task trackers cannot be the same.");
					} else {
						pageComplete = true;
					}
				}
				setPageComplete(pageComplete);
			}		
		};
		
		pbiViewer.addSelectionChangedListener(selectionListener);
		taskViewer.addSelectionChangedListener(selectionListener);

		setMessage("Select the TeamForge trackers to be mapped.");

		setControl(outerContainer);
	}

	private Table createTable(Group pbiGroup) {
		Table table = new Table(pbiGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 200;
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

	private TrackerRow[] getTrackers(final String projectId) {
		trackers = trackerMap.get(projectId);
		if (trackers == null) {
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				public void run() {
					try {
						trackers = ((ProjectMappingWizard)getWizard()).getSoapClient().getAllTrackersOfProject(projectId);
					} catch (RemoteException e) {
						Activator.handleError(e);
						setErrorMessage(e.getMessage());
					}
				}
			});
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
