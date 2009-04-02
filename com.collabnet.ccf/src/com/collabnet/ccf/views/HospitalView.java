package com.collabnet.ccf.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.dialogs.HospitalFilterDialog;
import com.collabnet.ccf.model.Hospital;
import com.collabnet.ccf.preferences.CcfPreferencePage;

public class HospitalView extends ViewPart {
	private TableViewer tableViewer;
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private CcfDataProvider dataProvider = new CcfDataProvider();
	private Hospital[] hospitals;
	private boolean hospitalLoaded;
	
	private static HospitalView view;
	private static String contentDescription;
	private static Filter[]	filters;
	private static boolean filtering;
	private static boolean filtersActive = true;
	
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
			getHospitals();
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
		getHospitals();
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

		TableColumn col;
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 50);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Timestamp");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 75);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Exception Class");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Exception Message");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Cause Exception Class");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Cause Exception Message");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Stack Trace");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Adaptor Name");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Originating Component");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Data Type");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Data");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Fixed");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 50);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Reprocessed");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 50);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source System ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Repository ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target System ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Repository ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source System Kind");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Repository Kind");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target System Kind");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Repository Kind");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);	
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Artifact ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Artifact ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Error Code");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Last Modified");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Last Modified");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Artifact Version");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Artifact Version");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Artifact Type");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 100);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Generic Artifact");
		col.addSelectionListener(headerListener);
		setColumnWidth(layout, disposeListener, col, 300);
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
	
	private void getHospitals() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				if (contentDescription == null) setContentDescription("");
				else setContentDescription(contentDescription);
				try {
					if (filtering) hospitals = dataProvider.getHospitals(filters);
					else hospitals = dataProvider.getHospitals(null);
					hospitalLoaded = true;
				} catch (Exception e) {
					setContentDescription("Could not connect to database.  See error log.");
					hospitals = new Hospital[0];
					Activator.handleError("Could not connect to database", e);
				}
				tableViewer.setInput(hospitals);
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
	
	class HospitalLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				Hospital hospital = (Hospital)element;
				if (hospital.isFixed()) return Activator.getImage(Activator.IMAGE_HOSPITAL_ENTRY_FIXED);
				else return Activator.getImage(Activator.IMAGE_HOSPITAL_ENTRY);
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			Hospital hospital = (Hospital)element;
			switch (columnIndex) {
			case 0:
				if (hospital.getId() == 0) return "";
				else return Integer.toString(hospital.getId());
			case 1:
				if (hospital.getTimeStamp() == null) return "";
				else return hospital.getTimeStamp();		
			case 2:
				if (hospital.getExceptionClassName() == null) return "";
				else return hospital.getExceptionClassName();
			case 3:
				if (hospital.getExceptionMessage() == null) return "";
				else return hospital.getExceptionMessage();	
			case 4:
				if (hospital.getCauseExceptionClassName() == null) return "";
				else return hospital.getCauseExceptionClassName();
			case 5:
				if (hospital.getCauseExceptionMessage() == null) return "";
				else return hospital.getCauseExceptionMessage();
			case 6:
				if (hospital.getStackTrace() == null) return "";
				else return hospital.getStackTrace();	
			case 7:
				if (hospital.getAdaptorName() == null) return "";
				else return hospital.getAdaptorName();	
			case 8:
				if (hospital.getOriginatingComponent() == null) return "";
				else return hospital.getOriginatingComponent();	
			case 9:
				if (hospital.getDataType() == null) return "";
				else return hospital.getDataType();	
			case 10:
				if (hospital.getData() == null) return "";
				else return hospital.getData();	
			case 11:
				if (hospital.isFixed()) return "true";
				else return "";	
			case 12:
				if (hospital.isReprocessed()) return "true";
				else return "";	
			case 13:
				if (hospital.getSourceSystemId() == null) return "";
				else return hospital.getSourceSystemId();	
			case 14:
				if (hospital.getSourceRepositoryId() == null) return "";
				else return hospital.getSourceRepositoryId();
			case 15:
				if (hospital.getTargetSystemId() == null) return "";
				else return hospital.getTargetSystemId();	
			case 16:
				if (hospital.getTargetRepositoryId() == null) return "";
				else return hospital.getSourceRepositoryId();	
			case 17:
				if (hospital.getSourceSystemKind() == null) return "";
				else return hospital.getSourceSystemKind();
			case 18:
				if (hospital.getSourceRepositoryKind() == null) return "";
				else return hospital.getSourceRepositoryKind();	
			case 19:
				if (hospital.getTargetSystemKind() == null) return "";
				else return hospital.getTargetSystemKind();
			case 20:
				if (hospital.getTargetRepositoryKind() == null) return "";
				else return hospital.getTargetRepositoryKind();	
			case 21:
				if (hospital.getSourceArtifactId() == null) return "";
				else return hospital.getSourceArtifactId();	
			case 22:
				if (hospital.getTargetArtifactId() == null) return "";
				else return hospital.getTargetArtifactId();	
			case 23:
				if (hospital.getErrorCode() == null) return "";
				else return hospital.getErrorCode();
			case 24:
				if (hospital.getSourceLastModificationTime() == null) return "";
				else return hospital.getSourceLastModificationTime().toString();
			case 25:
				if (hospital.getTargetLastModificationTime() == null) return "";
				else return hospital.getTargetLastModificationTime().toString();
			case 26:
				if (hospital.getSourceArtifactVersion() == null) return "";
				else return hospital.getSourceArtifactVersion();
			case 27:
				if (hospital.getTargetArtifactVersion() == null) return "";
				else return hospital.getTargetArtifactVersion();	
			case 28:
				if (hospital.getArtifactType() == null) return "";
				else return hospital.getArtifactType();	
			case 29:
				if (hospital.getGenericArtifact() == null) return "";
				else return hospital.getGenericArtifact();					
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
			getHospitals();
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
				getHospitals();
			}
		}
	}
	
	class ArrangeColumnsAction extends Action {
		public ArrangeColumnsAction() {
			super();
			setText("Columns...");
		}
		public void run() {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Arrange Columns", "Not yet implemented.");
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
