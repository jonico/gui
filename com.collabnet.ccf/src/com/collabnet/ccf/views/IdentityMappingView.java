package com.collabnet.ccf.views;

import java.sql.Timestamp;
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
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.actions.IdentityMappingAction;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.dialogs.HospitalFilterDialog;
import com.collabnet.ccf.dialogs.IdentityMappingFilterDialog;
import com.collabnet.ccf.model.IdentityMapping;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.preferences.CcfPreferencePage;
import com.collabnet.ccf.preferences.IdentityMappingPreferencePage;
import com.collabnet.ccf.views.HospitalView.HospitalSorter;

public class IdentityMappingView extends ViewPart {
	private Composite parentComposite;
	private TableViewer tableViewer;
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private CcfDataProvider dataProvider = new CcfDataProvider();
	private IdentityMapping[] identityMappings;
	private boolean identityMappingsLoaded;
	
	public static final String ID = "com.collabnet.ccf.views.IdentityMappingView";

	private static IdentityMappingView view;
	private static String contentDescription;
	private static Landscape landscape;
	private static Filter[][] filters;
	private static boolean filtering;
	private static boolean filtersActive = true;
	
	private static List<String> selectedColumns;
	private static List<String> allColumns;
	private static List<String> columnHeaderList;
	private static String[] columnHeaders = {
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
		"Source Last Modified",
		"Target Last Modified",
		"Source Artifact Version",
		"Target Artifact Version",
		"Artifact Type",
		"Child Source Artifact ID",
		"Child Source Repository ID",
		"Child Source Repository Kind",
		"Child Target Artifact ID",
		"Child Target Repository ID",
		"Child Target Repository Kind",		
		"Parent Source Artifact ID",
		"Parent Source Repository ID",
		"Parent Source Repository Kind",
		"Parent Target Artifact ID",
		"Parent Target Repository ID",
		"Parent Target Repository Kind",
	};
	static {
		columnHeaderList = new ArrayList<String>();
		for (int i = 0; i < columnHeaders.length; i++) {
			columnHeaderList.add(columnHeaders[i]);
		}
	}
	private static int[] columnWidths = {
		100,
		200,
		100,
		200,
		100,
		100,
		100,
		100,
		100,
		100,
		200,
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
		100,
		100
	};
	
	public IdentityMappingView() {
		super();
		view = this;
	}

	@Override
	public void createPartControl(Composite parent) {
		parentComposite = parent;
		
		if (settings.getBoolean(Filter.IDENTITY_MAPPING_FILTERS_SET)) {
			filtersActive = settings.getBoolean(Filter.IDENTITY_MAPPING_FILTERS_ACTIVE);
			getPreviousFilters();
		}
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		
		createControls();

		if (Activator.getDefault().getPreferenceStore().getBoolean(Activator.PREFERENCES_AUTOCONNECT)) {
			getIdentityMappings();
		} else {
			setContentDescription("Click Refresh to load identity mappings");			
		}
	}
	
	private void createControls() {
		setSelectedColumns();
		
		// If tableViewer has already been created, we are refreshing because
		// column preferences have been changed.
		boolean recreatingTable = tableViewer != null;
		if (recreatingTable) {
			Control[] controls = parentComposite.getChildren();
			for (int i = 0; i < controls.length; i++) {
				controls[i].dispose();
			}
		}
		
		tableViewer = createTable(parentComposite);
		
		createMenus(recreatingTable);
		if (!recreatingTable) createToolbar();
		
		getSite().setSelectionProvider(tableViewer);	
		
		if (recreatingTable && identityMappings != null) {
			tableViewer.setInput(identityMappings);
			parentComposite.layout();
			parentComposite.redraw();
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
		getIdentityMappings();
	}
	
	public void refreshTableLayout() {
		createControls();
	}
	
	public boolean isIdentityMappingsLoaded() {
		return identityMappingsLoaded;
	}
	
	public static IdentityMappingView getView() {
		return view;
	}
	
	public static void setFilters(Filter[][] filters, boolean filtering, String description) {
		IdentityMappingView.filters = filters;
		IdentityMappingView.filtering = filtering;
		if (filtering) {
			if (description == null) {
				contentDescription = "(Filters Active)";
			} else {
				contentDescription = description;
			}
		} else {
			contentDescription = "";
		}
	}
	
	public static void setLandscape(Landscape landscape) {
		IdentityMappingView.landscape = landscape;
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
		tableViewer.setLabelProvider(new IdentityMappingLabelProvider());
		
		int sortIndex = 0;
		boolean sortReversed = false;
		String sortColumn = settings.get("IdentityMappingView.sortColumn");
		if (sortColumn != null) {
			int index = columnHeaderList.indexOf(sortColumn);
			if (index != -1) {
				String columnName = allColumns.get(index);
				int nameIndex = selectedColumns.indexOf(columnName);
				if (nameIndex != -1) {
					sortIndex = nameIndex;
					sortReversed = settings.getBoolean("IdentityMappingView.sortReversed");					
				}
			}
		}
		
		IdentityMappingSorter sorter = new IdentityMappingSorter(sortIndex);
		sorter.setReversed(sortReversed);
		tableViewer.setSorter(sorter);
		if (sortReversed) table.setSortDirection(SWT.DOWN);
		else table.setSortDirection(SWT.UP);
		table.setSortColumn(table.getColumn(sortIndex));
		
		tableViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent evt) {
				// TODO: Edit option
			}			
		});
		
		Transfer[] dropTypes = { LocalSelectionTransfer.getTransfer() };
		tableViewer.addDropSupport(DND.DROP_COPY | DND.DROP_DEFAULT, dropTypes,
				new IdentityMappingDropAdapter(tableViewer));
		
		return tableViewer;
	}
	
	private void createColumns(Table table, TableLayout layout) {
		DisposeListener disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TableColumn col = (TableColumn)e.getSource();
				if (col.getWidth() > 0) settings.put("IdentityMappingView." + col.getText(), col.getWidth()); //$NON-NLS-1$
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
		
		if (columnName.equals("ID")) table.setSortColumn(col);
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
		IdentityMappingSorter oldSorter = (IdentityMappingSorter)tableViewer.getSorter();
		if (oldSorter != null && column == oldSorter.getColumnNumber()) {
			oldSorter.setReversed(!oldSorter.isReversed());
			if (oldSorter.isReversed()) tableViewer.getTable().setSortDirection(SWT.DOWN);
			else tableViewer.getTable().setSortDirection(SWT.UP);	
			tableViewer.refresh();
		} else {
			IdentityMappingSorter newSorter = new IdentityMappingSorter(column);
			tableViewer.setSorter(newSorter);
			tableViewer.getTable().setSortDirection(SWT.UP);
		}
		tableViewer.getTable().setSortColumn(tableViewer.getTable().getColumn(column));
		String columnName = tableViewer.getTable().getColumn(column).getText();
		HospitalSorter sorter = (HospitalSorter)tableViewer.getSorter();
		settings.put("IdentityMappingView.sortColumn", columnName);
		settings.put("IdentityMappingView.sortReversed", sorter.isReversed());
	}
	
	private void setColumnWidth(TableLayout layout,
			DisposeListener disposeListener, TableColumn col, int defaultWidth) {
		String columnWidth = settings.get("IdentityMappingView." + col.getText()); //$NON-NLS-1$
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
	
	private void createMenus(boolean recreatingTable) {
		MenuManager menuMgr = new MenuManager("#IdentityMappingViewPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IdentityMappingView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);		
		getSite().registerContextMenu(menuMgr, tableViewer);
		
		if (!recreatingTable) {
			IActionBars actionBars = getViewSite().getActionBars();
			IMenuManager actionBarsMenu = actionBars.getMenuManager();
			actionBarsMenu.add(new ArrangeColumnsAction());
		}
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));		
	}
	
	private void getIdentityMappings() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				if (contentDescription == null) setContentDescription("");
				else setContentDescription(contentDescription);
				try {
					if (filtering) identityMappings = dataProvider.getIdentityMappings(landscape, filters);
					else identityMappings = dataProvider.getIdentityMappings(landscape, (Filter[])null);
					identityMappingsLoaded = true;
				} catch (Exception e) {
					setContentDescription("Could not connect to database.  See error log.");
					identityMappings = new IdentityMapping[0];
					Activator.handleError("Could not connect to database", e);
				}
				tableViewer.setInput(identityMappings);
				tableViewer.refresh();
			}			
		});
	}
	
	private void getPreviousFilters() {
		List<Filter> filterList = new ArrayList<Filter>();
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, true);	
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, true);	
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_KIND, true);		
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_KIND, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_ARTIFACT_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_ID, true);
		updateFilterList(filterList, CcfDataProvider.IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_KIND, true);
		if (filterList.size() > 0) {
			
			Filter[] previousFilters = new Filter[filterList.size()];
			filterList.toArray(previousFilters);
			Filter[][] filterGroups = { previousFilters };

			filtering = filtersActive;
			setFilters(filterGroups, filtering, null);
		}
	}
	
	private void updateFilterList(List<Filter> filterList, String columnName, boolean stringValue) {
		String filterValue = settings.get(Filter.IDENTITY_MAPPING_FILTER_VALUE + columnName);
		if (filterValue != null && filterValue.length() > 0) {
			Filter filter = new Filter(columnName, filterValue, stringValue, settings.getInt(Filter.IDENTITY_MAPPING_FILTER_TYPE + columnName));
			filterList.add(filter);			
		}
	}
	
	public static void setSelectedColumns() {
		selectedColumns = new ArrayList<String>();
		String columns = Activator.getDefault().getPreferenceStore().getString(Activator.PREFERENCES_IDENTITY_MAPPING_COLUMNS);
		String[] columnArray = columns.split("\\,");
		for (int i = 0; i < columnArray.length; i++) {
			selectedColumns.add(columnArray[i]);
		}
		allColumns = new ArrayList<String>();
		String[] allColumnArray = CcfDataProvider.IDENTITY_MAPPING_COLUMNS.split("\\,");
		for (int i = 0; i < allColumnArray.length; i++) {
			allColumns.add(allColumnArray[i]);
		}
	}
	
	class IdentityMappingLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String columnName = selectedColumns.get(columnIndex);
			int index = allColumns.indexOf(columnName);			
			IdentityMapping identityMapping = (IdentityMapping)element;
			switch (index) {
			case 0:
				if (identityMapping.getSourceSystemId() == null) return "";
				else return identityMapping.getSourceSystemId();
			case 1:
				if (identityMapping.getSourceRepositoryId() == null) return "";
				else return identityMapping.getSourceRepositoryId();		
			case 2:
				if (identityMapping.getTargetSystemId() == null) return "";
				else return identityMapping.getTargetSystemId();
			case 3:
				if (identityMapping.getTargetRepositoryId() == null) return "";
				else return identityMapping.getTargetRepositoryKind();	
			case 4:
				if (identityMapping.getSourceSystemKind() == null) return "";
				else return identityMapping.getSourceSystemKind();
			case 5:
				if (identityMapping.getSourceRepositoryKind() == null) return "";
				else return identityMapping.getSourceRepositoryKind();	
			case 6:
				if (identityMapping.getTargetSystemKind() == null) return "";
				else return identityMapping.getTargetSystemKind();
			case 7:
				if (identityMapping.getTargetRepositoryKind() == null) return "";
				else return identityMapping.getTargetRepositoryKind();	
			case 8:
				if (identityMapping.getSourceArtifactId() == null) return "";
				else return identityMapping.getSourceArtifactId();	
			case 9:
				if (identityMapping.getTargetArtifactId() == null) return "";
				else return identityMapping.getTargetArtifactId();	
			case 10:
				if (identityMapping.getSourceLastModificationTime() == null) return "";
				else return identityMapping.getSourceLastModificationTime().toString();
			case 11:
				if (identityMapping.getTargetLastModificationTime() == null) return "";
				else return identityMapping.getTargetLastModificationTime().toString();
			case 12:
				if (identityMapping.getSourceArtifactVersion() == null) return "";
				else return identityMapping.getSourceArtifactVersion();
			case 13:
				if (identityMapping.getTargetArtifactVersion() == null) return "";
				else return identityMapping.getTargetArtifactVersion();	
			case 14:
				if (identityMapping.getArtifactType() == null) return "";
				else return identityMapping.getArtifactType();
			case 15:
				if (identityMapping.getChildSourceArtifactId() == null) return "";
				else return identityMapping.getChildSourceArtifactId();
			case 16:
				if (identityMapping.getChildSourceRepositoryId() == null) return "";
				else return identityMapping.getChildSourceRepositoryId();	
			case 17:
				if (identityMapping.getChildSourceRepositoryKind() == null) return "";
				else return identityMapping.getChildSourceRepositoryKind();	
			case 18:
				if (identityMapping.getChildTargetArtifactId() == null) return "";
				else return identityMapping.getChildTargetArtifactId();
			case 19:
				if (identityMapping.getChildTargetRepositoryId() == null) return "";
				else return identityMapping.getChildTargetRepositoryId();	
			case 20:
				if (identityMapping.getChildTargetRepositoryKind() == null) return "";
				else return identityMapping.getChildTargetRepositoryKind();	
			case 21:
				if (identityMapping.getParentSourceArtifactId() == null) return "";
				else return identityMapping.getParentSourceArtifactId();
			case 22:
				if (identityMapping.getParentSourceRepositoryId() == null) return "";
				else return identityMapping.getParentSourceRepositoryId();	
			case 23:
				if (identityMapping.getParentSourceRepositoryKind() == null) return "";
				else return identityMapping.getParentSourceRepositoryKind();	
			case 24:
				if (identityMapping.getParentTargetArtifactId() == null) return "";
				else return identityMapping.getParentTargetArtifactId();
			case 25:
				if (identityMapping.getParentTargetRepositoryId() == null) return "";
				else return identityMapping.getParentTargetRepositoryId();	
			case 26:
				if (identityMapping.getParentTargetRepositoryKind() == null) return "";
				else return identityMapping.getParentTargetRepositoryKind();										
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
	
	class IdentityMappingSorter extends ViewerSorter {
		private boolean reversed = false;
		private int columnNumber;
		private int index;
		
		public IdentityMappingSorter(int columnNumber) {
			this.columnNumber = columnNumber;
			String columnName = selectedColumns.get(columnNumber);
			index = allColumns.indexOf(columnName);			
		}
		
		public void setReversed(boolean reversed) {
			this.reversed = reversed;
		}
		
		public int getColumnNumber() {
			return columnNumber;
		}
		
		public boolean isReversed() {
			return reversed;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 == null || e2 == null) return super.compare(viewer, e1, e2);
			
			IdentityMapping m1 = (IdentityMapping)e1;
			IdentityMapping m2 = (IdentityMapping)e2;
			int result = 0;
			
			result = compareColumnValue(m1, m2);
			
			if (reversed)
				result = -result;
			return result;
		}
		
		private int compareColumnValue(IdentityMapping m1, IdentityMapping m2) {
			String value1 = null;
			String value2 = null;
			switch (index) {
			case 0:
				value1 = m1.getSourceSystemId();
				value2 = m2.getSourceSystemId();
				break;
			case 1:
				value1 = m1.getSourceRepositoryId();
				value2 = m2.getSourceRepositoryId();
				break;
			case 2:
				value1 = m1.getTargetSystemId();
				value2 = m2.getTargetSystemId();
				break;
			case 3:
				value1 = m1.getTargetRepositoryId();
				value2 = m2.getTargetRepositoryId();
				break;
			case 4:
				value1 = m1.getSourceSystemKind();
				value2 = m2.getSourceSystemKind();
				break;
			case 5:
				value1 = m1.getSourceRepositoryKind();
				value2 = m2.getSourceRepositoryKind();
				break;
			case 6:
				value1 = m1.getTargetSystemKind();
				value2 = m2.getTargetSystemKind();
				break;
			case 7:
				value1 = m1.getTargetRepositoryKind();
				value2 = m2.getTargetRepositoryKind();
				break;	
			case 8:
				value1 = m1.getSourceArtifactId();
				value2 = m2.getSourceArtifactId();
				break;
			case 9:
				value1 = m1.getTargetArtifactId();
				value2 = m2.getTargetArtifactId();
				break;
			case 10:
				Timestamp ts1 = m1.getSourceLastModificationTime();
				Timestamp ts2 = m2.getSourceLastModificationTime();
				if (ts1 == null && ts2 == null) return 0;
				else if (ts1 == null) return -1;
				else if (ts2 == null) return 1;
				else return ts1.compareTo(ts2);
			case 11:
				ts1 = m1.getTargetLastModificationTime();
				ts2 = m2.getTargetLastModificationTime();
				if (ts1 == null && ts2 == null) return 0;
				else if (ts1 == null) return -1;
				else if (ts2 == null) return 1;
				else return ts1.compareTo(ts2);
			case 12:
				value1 = m1.getSourceArtifactVersion();
				value2 = m2.getSourceArtifactVersion();
				break;
			case 13:
				value1 = m1.getTargetArtifactVersion();
				value2 = m2.getTargetArtifactVersion();
				break;
			case 14:
				value1 = m1.getArtifactType();
				value2 = m2.getArtifactType();
				break;
			case 15:
				value1 = m1.getChildSourceArtifactId();
				value2 = m2.getChildSourceArtifactId();
				break;
			case 16:
				value1 = m1.getChildSourceRepositoryId();
				value2 = m2.getChildSourceRepositoryId();
				break;	
			case 17:
				value1 = m1.getChildSourceRepositoryKind();
				value2 = m2.getChildSourceRepositoryKind();
				break;
			case 18:
				value1 = m1.getChildTargetArtifactId();
				value2 = m2.getChildTargetArtifactId();
				break;
			case 19:
				value1 = m1.getChildTargetRepositoryId();
				value2 = m2.getChildTargetRepositoryId();
				break;	
			case 20:
				value1 = m1.getChildTargetRepositoryKind();
				value2 = m2.getChildTargetRepositoryKind();
				break;	
			case 21:
				value1 = m1.getParentSourceArtifactId();
				value2 = m2.getParentSourceArtifactId();
				break;
			case 22:
				value1 = m1.getParentSourceRepositoryId();
				value2 = m2.getParentSourceRepositoryId();
				break;	
			case 23:
				value1 = m1.getParentSourceRepositoryKind();
				value2 = m2.getParentSourceRepositoryKind();
				break;
			case 24:
				value1 = m1.getParentTargetArtifactId();
				value2 = m2.getParentTargetArtifactId();
				break;
			case 25:
				value1 = m1.getParentTargetRepositoryId();
				value2 = m2.getParentTargetRepositoryId();
				break;	
			case 26:
				value1 = m1.getParentTargetRepositoryKind();
				value2 = m2.getParentTargetRepositoryKind();
				break;								
			default:
				break;
			}
			if (value1 == null) value1 = "";
			if (value2 == null) value2 = "";
			return value1.compareTo(value2);
		}
		
	}
	
	class RefreshAction extends Action {
		public RefreshAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_REFRESH));
			setToolTipText("Refresh View");
		}
		public void run() {
			getIdentityMappings();
		}
	}
	
	class FilterAction extends Action {
		public FilterAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_FILTERS));
			setToolTipText("Filters...");
		}
		public void run() {
			IdentityMappingFilterDialog dialog = new IdentityMappingFilterDialog(Display.getDefault().getActiveShell(), filters, filtersActive);
			if (dialog.open() == HospitalFilterDialog.OK) {				
				filtering = dialog.isFiltering();
				filtersActive = dialog.filtersActive();
				setFilters(dialog.getFilters(), filtering, null);
				getIdentityMappings();
			}
		}
	}
	
	class ArrangeColumnsAction extends Action {
		public ArrangeColumnsAction() {
			super();
			setText("Columns...");
		}
		public void run() {
			PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), IdentityMappingPreferencePage.ID, null, null);
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
	
	class IdentityMappingDropAdapter extends ViewerDropAdapter {
		
		protected IdentityMappingDropAdapter(Viewer viewer) {
			super(viewer);
			setFeedbackEnabled(true);
		}

		@Override
		public boolean performDrop(Object arg0) {
			return true;
		}

		@Override
		public boolean validateDrop(Object arg0, int arg1, TransferData arg2) {
			return true;
		}
		
		@Override
		public void drop(DropTargetEvent event) {
			if (event.data instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection)event.data;
				IdentityMappingAction action = new IdentityMappingAction();
				action.selectionChanged(null, selection);
				action.run(null);		
			}
		}
		
		public void dragEnter(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
		}
		
		public void dragOver(DropTargetEvent event) {
			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
		}
		
		public void dragOperationChanged(DropTargetEvent event) {
			if ((event.detail == DND.DROP_DEFAULT) || (event.operations & DND.DROP_COPY) != 0) {

				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
		
	}	

}
