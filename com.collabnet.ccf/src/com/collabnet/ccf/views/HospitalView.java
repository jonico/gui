package com.collabnet.ccf.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.actions.ExaminePayloadAction;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.dialogs.HospitalFilterDialog;
import com.collabnet.ccf.model.Patient;
import com.collabnet.ccf.preferences.CcfPreferencePage;
import com.collabnet.ccf.preferences.HospitalPreferencePage;

public class HospitalView extends ViewPart {
	private TableViewer tableViewer;
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private CcfDataProvider dataProvider = new CcfDataProvider();
	private Patient[] patients;
	private boolean hospitalLoaded;
	
	private static HospitalView view;
	private static String contentDescription;
	private static Filter[]	filters;
	private static boolean filtering;
	private static boolean filtersActive = true;
	
	private static List<String> selectedColumns;
	private static List<String> allColumns;
	private static String[] columnHeaders = {
		"ID",
		"Timestamp",
		"Exception Class",
		"Exception Message",
		"Cause Exception Class",
		"Cause Exception Message",
		"Stack Trace",
		"Adaptor Name",
		"Originating Component",
		"Data Type",
		"Data",
		"Fixed",
		"Reprocessed",
		"Source System ID",
		"Source Repository ID",
		"Target System ID",
		"Target Repository ID",
		"Source System Kind",
		"Source Repository Kind",
		"Target System Kind",
		"Target Repository Kind",
		"Source Artifact ID",
		"Target Artifact ID",
		"Error Code",
		"Source Last Modified",
		"Target Last Modified",
		"Source Artifact Version",
		"Target Artifact Version",
		"Artifact Type",
		"Generic Artifact"
	};
	private static int[] columnWidths = {
		50,
		75,
		100,
		200,
		200,
		200,
		200,
		200,
		200,
		200,
		200,
		50,
		50,
		200,
		100,
		200,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		100,
		300
	};
	
	public static final String ID = "com.collabnet.ccf.views.HospitalView";
	
	public HospitalView() {
		super();
		view = this;
	}

	@Override
	public void createPartControl(Composite parent) {
		if (settings.getBoolean("hospitalFilters.set")) {
			filtersActive = settings.getBoolean("hospitalFilters.active");
			getPreviousFilters();
		}
		
		setSelectedColumns();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		tableViewer = createTable(parent);
		
		createMenus();
		createToolbar();
		
		getSite().setSelectionProvider(tableViewer);
		
		if (Activator.getDefault().getPreferenceStore().getBoolean(Activator.PREFERENCES_AUTOCONNECT)) {
			getPatients();
		} else {
			setContentDescription("Click Refresh to load Hospital");			
		}
	}

	@Override
	public void setFocus() {
	}
	
	public void dispose() {
		view = null;
		super.dispose();
	}
	
	public void refresh() {
		getPatients();
	}
	
	public boolean isHospitalLoaded() {
		return hospitalLoaded;
	}
	
	public static HospitalView getView() {
		return view;
	}
	
	public static void setFilters(Filter[] filters, boolean filtering) {
		HospitalView.filters = filters;
		HospitalView.filtering = filtering;
		if (filtering) contentDescription = "(Filters Active)";
		else contentDescription = "";
	}
	
	private TableViewer createTable(Composite parent) {
		Table table =	new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gridData);
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
        
		tableViewer = new TableViewer(table);
		createColumns(table, layout);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new HospitalLabelProvider());
		
		tableViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent evt) {
				ExaminePayloadAction action = new ExaminePayloadAction();
				action.selectionChanged(null, tableViewer.getSelection());
				action.run(null);
			}			
		});
		
		return tableViewer;
	}
	
	private void createColumns(Table table, TableLayout layout) {
		DisposeListener disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TableColumn col = (TableColumn)e.getSource();
				if (col.getWidth() > 0) settings.put("HospitalView." + col.getText(), col.getWidth()); //$NON-NLS-1$
			}			
		};
		
		SelectionListener headerListener = getColumnListener();

		Iterator<String> iter = selectedColumns.iterator();
		while (iter.hasNext()) {
			String columnName = iter.next();
			createColumn(columnName, table, headerListener, disposeListener, layout);
		}
	}
	
	private void createColumn(String columnName, Table table, SelectionListener headerListener, DisposeListener disposeListener, TableLayout layout) {
		int index = allColumns.indexOf(columnName);
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText(columnHeaders[index]);
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, columnWidths[index]);
	}

	private SelectionListener getColumnListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int column = tableViewer.getTable().indexOf((TableColumn) e.widget);
				setSortColumn(tableViewer, column);
			}
		};
	}
	
	private void setSortColumn(TableViewer tableViewer, int column) {
		
	}
	
	private void setColumnWidth(TableLayout layout,
			DisposeListener disposeListener, TableColumn col, int defaultWidth) {
		String columnWidth = settings.get("HospitalView." + col.getText()); //$NON-NLS-1$
		if (columnWidth == null || columnWidth.equals("0")) layout.addColumnData(new ColumnWeightData(defaultWidth, true)); //$NON-NLS-1$
		else layout.addColumnData(new ColumnPixelData(Integer.parseInt(columnWidth), true));
		col.addDisposeListener(disposeListener);
	}
	
	private void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(new RefreshAction());
		toolbarManager.add(new FilterAction());
		toolbarManager.add(new ConnectionAction());
	}
	
	private void createMenus() {
		MenuManager menuMgr = new MenuManager("#HospitalViewPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				HospitalView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);		
		getSite().registerContextMenu(menuMgr, tableViewer);
		
		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager actionBarsMenu = actionBars.getMenuManager();
		actionBarsMenu.add(new ArrangeColumnsAction());
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
	}
	
	private void getPatients() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				if (contentDescription == null) setContentDescription("");
				else setContentDescription(contentDescription);
				try {
					if (filtering) patients = dataProvider.getPatients(filters);
					else patients = dataProvider.getPatients(null);
					hospitalLoaded = true;
				} catch (Exception e) {
					setContentDescription("Could not connect to database.  See error log.");
					patients = new Patient[0];
					Activator.handleError("Could not connect to database", e);
				}
				tableViewer.setInput(patients);
				tableViewer.refresh();
			}			
		});
	}
	
	private void getPreviousFilters() {
		List<Filter> filterList = new ArrayList<Filter>();
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TIMESTAMP, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_EXCEPTION_CLASS_NAME, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_EXCEPTION_MESSAGE, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_CAUSE_EXCEPTION_CLASS_NAME, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_CAUSE_EXCEPTION_MESSAGE, true);		
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_STACK_TRACE, true);	
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_ADAPTOR_NAME, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_ORIGINATING_COMPONENT, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_DATA_TYPE, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_DATA, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_FIXED, false);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_REPROCESSED, false);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_ID, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_SYSTEM_ID, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, true);	
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_KIND, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_SYSTEM_KIND, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_ERROR_CODE, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_SOURCE_ARTIFACT_VERSION, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_TARGET_ARTIFACT_VERSION, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_ARTIFACT_TYPE, true);
		updateFilterList(filterList, CcfDataProvider.HOSPITAL_GENERIC_ARTIFACT, true);
		if (filterList.size() > 0) {
			filters = new Filter[filterList.size()];
			filterList.toArray(filters);
			filtering = filtersActive;
			setFilters(filters, filtering);
		}
	}
	
	private void updateFilterList(List<Filter> filterList, String columnName, boolean stringValue) {
		String filterValue = settings.get(Filter.HOSPITAL_FILTER_VALUE + columnName);
		if (filterValue != null && filterValue.length() > 0) {
			Filter filter = new Filter(columnName, filterValue, stringValue, settings.getInt(Filter.HOSPITAL_FILTER_TYPE + columnName));
			filterList.add(filter);			
		}
	}
	
	public static void setSelectedColumns() {
		selectedColumns = new ArrayList<String>();
		String columns = Activator.getDefault().getPreferenceStore().getString(Activator.PREFERENCES_HOSPITAL_COLUMNS);
		String[] columnArray = columns.split("\\,");
		for (int i = 0; i < columnArray.length; i++) {
			selectedColumns.add(columnArray[i]);
		}
		allColumns = new ArrayList<String>();
		String[] allColumnArray = CcfDataProvider.HOSPITAL_COLUMNS.split("\\,");
		for (int i = 0; i < allColumnArray.length; i++) {
			allColumns.add(allColumnArray[i]);
		}
	}
	
	class HospitalLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				Patient patient = (Patient)element;
				if (patient.isFixed()) return Activator.getImage(Activator.IMAGE_HOSPITAL_ENTRY_FIXED);
				else return Activator.getImage(Activator.IMAGE_HOSPITAL_ENTRY);
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String columnName = selectedColumns.get(columnIndex);
			int index = allColumns.indexOf(columnName);			
			Patient patient = (Patient)element;
			switch (index) {
			case 0:
				if (patient.getId() == 0) return "";
				else return Integer.toString(patient.getId());
			case 1:
				if (patient.getTimeStamp() == null) return "";
				else return patient.getTimeStamp();		
			case 2:
				if (patient.getExceptionClassName() == null) return "";
				else return patient.getExceptionClassName();
			case 3:
				if (patient.getExceptionMessage() == null) return "";
				else return patient.getExceptionMessage();	
			case 4:
				if (patient.getCauseExceptionClassName() == null) return "";
				else return patient.getCauseExceptionClassName();
			case 5:
				if (patient.getCauseExceptionMessage() == null) return "";
				else return patient.getCauseExceptionMessage();
			case 6:
				if (patient.getStackTrace() == null) return "";
				else return patient.getStackTrace();	
			case 7:
				if (patient.getAdaptorName() == null) return "";
				else return patient.getAdaptorName();	
			case 8:
				if (patient.getOriginatingComponent() == null) return "";
				else return patient.getOriginatingComponent();	
			case 9:
				if (patient.getDataType() == null) return "";
				else return patient.getDataType();	
			case 10:
				if (patient.getData() == null) return "";
				else return patient.getData();	
			case 11:
				if (patient.isFixed()) return "true";
				else return "";	
			case 12:
				if (patient.isReprocessed()) return "true";
				else return "";	
			case 13:
				if (patient.getSourceSystemId() == null) return "";
				else return patient.getSourceSystemId();	
			case 14:
				if (patient.getSourceRepositoryId() == null) return "";
				else return patient.getSourceRepositoryId();
			case 15:
				if (patient.getTargetSystemId() == null) return "";
				else return patient.getTargetSystemId();	
			case 16:
				if (patient.getTargetRepositoryId() == null) return "";
				else return patient.getSourceRepositoryId();	
			case 17:
				if (patient.getSourceSystemKind() == null) return "";
				else return patient.getSourceSystemKind();
			case 18:
				if (patient.getSourceRepositoryKind() == null) return "";
				else return patient.getSourceRepositoryKind();	
			case 19:
				if (patient.getTargetSystemKind() == null) return "";
				else return patient.getTargetSystemKind();
			case 20:
				if (patient.getTargetRepositoryKind() == null) return "";
				else return patient.getTargetRepositoryKind();	
			case 21:
				if (patient.getSourceArtifactId() == null) return "";
				else return patient.getSourceArtifactId();	
			case 22:
				if (patient.getTargetArtifactId() == null) return "";
				else return patient.getTargetArtifactId();	
			case 23:
				if (patient.getErrorCode() == null) return "";
				else return patient.getErrorCode();
			case 24:
				if (patient.getSourceLastModificationTime() == null) return "";
				else return patient.getSourceLastModificationTime().toString();
			case 25:
				if (patient.getTargetLastModificationTime() == null) return "";
				else return patient.getTargetLastModificationTime().toString();
			case 26:
				if (patient.getSourceArtifactVersion() == null) return "";
				else return patient.getSourceArtifactVersion();
			case 27:
				if (patient.getTargetArtifactVersion() == null) return "";
				else return patient.getTargetArtifactVersion();	
			case 28:
				if (patient.getArtifactType() == null) return "";
				else return patient.getArtifactType();	
			case 29:
				if (patient.getGenericArtifact() == null) return "";
				else return patient.getGenericArtifact();					
			default:
				break;
			}
			return "";
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String columnIndex) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {		
		}
		
	}
	
	class RefreshAction extends Action {
		public RefreshAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_REFRESH));
			setToolTipText("Refresh View");
		}
		public void run() {
			getPatients();
		}
	}
	
	class FilterAction extends Action {
		public FilterAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_FILTERS));
			setToolTipText("Filters...");
		}
		public void run() {
			HospitalFilterDialog dialog = new HospitalFilterDialog(Display.getDefault().getActiveShell(), filters, filtersActive);
			if (dialog.open() == HospitalFilterDialog.OK) {
				filtering = dialog.isFiltering();
				filtersActive = dialog.filtersActive();
				setFilters(dialog.getFilters(), filtering);
				getPatients();
			}
		}
	}
	
	class ArrangeColumnsAction extends Action {
		public ArrangeColumnsAction() {
			super();
			setText("Columns...");
		}
		public void run() {
			PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), HospitalPreferencePage.ID, null, null);
			if (pref != null) pref.open();		
		}
	}
	
	class ConnectionAction extends Action {
		public ConnectionAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_DATABASE_CONNECTION));
			setToolTipText("Database connection...");
		}
		public void run() {
			PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), CcfPreferencePage.ID, null, null);
			if (pref != null) pref.open();			
		}
	}		

}
