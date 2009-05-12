package com.collabnet.ccf.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IProjectMappingsChangeListener;
import com.collabnet.ccf.actions.ChangeSynchronizationStatusAction;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.dialogs.NewProjectMappingDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.views.ActiveViewSelectionDragAdapter;
import com.collabnet.ccf.views.CcfExplorerView;

public class CcfProjectMappingsEditorPage extends CcfEditorPage implements IProjectMappingsChangeListener {
	private ScrolledForm form;
	private FormToolkit toolkit;
	
	private TableViewer tableViewer1;
	private TableViewer tableViewer2;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private CcfDataProvider dataProvider = new CcfDataProvider();
	private SynchronizationStatus[] direction1Mappings;
	private SynchronizationStatus[] direction2Mappings;

	public final static String DIRECTION1_SECTION_STATE = "CcfProjectMappingsEditorPage.direction1SectionExpanded";
	public final static String DIRECTION2_SECTION_STATE = "CcfProjectMappingsEditorPage.direction2SectionExpanded";

	public CcfProjectMappingsEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		Activator.addChangeListener(this);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
        toolkit = getEditor().getToolkit();
        TableWrapLayout formLayout = new TableWrapLayout();
        formLayout.numColumns = 2;
        form.getBody().setLayout(formLayout);
		createControls(form.getBody());
	}
	
	private void createControls(Composite composite) {
		Label headerImageLabel = new Label(composite, SWT.NONE);
		headerImageLabel.setImage(Activator.getImage(getLandscape()));
		headerImageLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		
		Label headerLabel = new Label(composite, SWT.NONE);
		headerLabel.setText("Project Mappings");
		headerLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		headerLabel.setFont(JFaceResources.getHeaderFont());
        TableWrapData td = new TableWrapData(TableWrapData.FILL);
        headerLabel.setLayoutData(td);
        
		Section direction1Section = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        direction1Section.setLayoutData(td);
 
        direction1Section.setText(Landscape.getTypeDescription(getLandscape().getType2()) + " => " + Landscape.getTypeDescription(getLandscape().getType1()));
        Composite direction1SectionClient = toolkit.createComposite(direction1Section); 
        GridLayout direction1Layout = new GridLayout();
        direction1Layout.numColumns = 1;
        direction1Layout.verticalSpacing = 10;
        direction1SectionClient.setLayout(direction1Layout);
        direction1Section.setClient(direction1SectionClient);
        direction1Section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(DIRECTION1_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(DIRECTION1_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        Table table1 = toolkit.createTable(direction1SectionClient, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		table1.setData("direction1");
        table1.setHeaderVisible(true);
		table1.setLinesVisible(true);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gridData.heightHint = 150;
		table1.setLayoutData(gridData);
		TableLayout layout1 = new TableLayout();
		table1.setLayout(layout1);
        
		tableViewer1 = new TableViewer(table1);
		createColumns(tableViewer1, table1, layout1);
		
		tableViewer1.setContentProvider(new ArrayContentProvider());
		tableViewer1.setLabelProvider(new ProjectMappingsLabelProvider());
		
		tableViewer1.addOpenListener(new IOpenListener() {
			public void open(OpenEvent oe) {
				IStructuredSelection selection = (IStructuredSelection)tableViewer1.getSelection();
				if (selection != null && selection.size() == 1) {
					ActionDelegate action = new ChangeSynchronizationStatusAction();
					action.selectionChanged(null, selection);
					action.run(null);						
				}
			}			
		});
		
		boolean sortReversed = false;
		int sortIndex = 0;
		try {
			sortIndex = settings.getInt("projectMappings.direction1.sortColumn");
			sortReversed = settings.getBoolean("projectMappings.direction1.sortReversed");					
		} catch (Exception e) {}
		
		ProjectMappingsSorter direction1Sorter = new ProjectMappingsSorter(sortIndex);
		direction1Sorter.setReversed(sortReversed);
		tableViewer1.setSorter(direction1Sorter);
		if (sortReversed) table1.setSortDirection(SWT.DOWN);
		else table1.setSortDirection(SWT.UP);
		table1.setSortColumn(table1.getColumn(sortIndex));
		
		Transfer[] dragTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };		
		tableViewer1.addDragSupport(DND.DROP_COPY | DND.DROP_DEFAULT, dragTypes, getDragSourceListener(tableViewer1));

		Section direction2Section = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        direction2Section.setLayoutData(td);
 
        direction2Section.setText(Landscape.getTypeDescription(getLandscape().getType1()) + " => " + Landscape.getTypeDescription(getLandscape().getType2()));
        Composite direction2SectionClient = toolkit.createComposite(direction2Section); 
        GridLayout direction2Layout = new GridLayout();
        direction2Layout.numColumns = 1;
        direction2Layout.verticalSpacing = 10;
        direction2SectionClient.setLayout(direction2Layout);
        direction2Section.setClient(direction2SectionClient);
        direction2Section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(DIRECTION2_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(DIRECTION2_SECTION_STATE, STATE_CONTRACTED);
            }
        }); 
        
        Table table2 = toolkit.createTable(direction2SectionClient, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		table2.setData("direction2");
        table2.setHeaderVisible(true);
		table2.setLinesVisible(true);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gridData.heightHint = 150;
		table2.setLayoutData(gridData);
		TableLayout layout2 = new TableLayout();
		table2.setLayout(layout2);
        
		tableViewer2 = new TableViewer(table2);
		createColumns(tableViewer2, table2, layout2);
		
		tableViewer2.setContentProvider(new ArrayContentProvider());
		tableViewer2.setLabelProvider(new ProjectMappingsLabelProvider());
		
		tableViewer2.addOpenListener(new IOpenListener() {
			public void open(OpenEvent oe) {
				IStructuredSelection selection = (IStructuredSelection)tableViewer2.getSelection();
				if (selection != null && selection.size() == 1) {
					ActionDelegate action = new ChangeSynchronizationStatusAction();
					action.selectionChanged(null, selection);
					action.run(null);						
				}
			}			
		});
		
		sortReversed = false;
		sortIndex = 0;
		try {
			sortIndex = settings.getInt("projectMappings.direction2.sortColumn");
			sortReversed = settings.getBoolean("projectMappings.direction2.sortReversed");					
		} catch (Exception e) {}
		
		ProjectMappingsSorter direction2Sorter = new ProjectMappingsSorter(sortIndex);
		direction2Sorter.setReversed(sortReversed);
		tableViewer2.setSorter(direction2Sorter);
		if (sortReversed) table2.setSortDirection(SWT.DOWN);
		else table2.setSortDirection(SWT.UP);
		table2.setSortColumn(table2.getColumn(sortIndex));

		tableViewer2.addDragSupport(DND.DROP_COPY | DND.DROP_DEFAULT, dragTypes, getDragSourceListener(tableViewer2));
        
        toolkit.paintBordersFor(direction1SectionClient);
        toolkit.paintBordersFor(direction2SectionClient);
        
        String expansionState = getDialogSettings().get(DIRECTION1_SECTION_STATE);
        direction1Section.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        expansionState = getDialogSettings().get(DIRECTION2_SECTION_STATE);
        direction2Section.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        getMappings();
		tableViewer1.setInput(direction1Mappings);
		tableViewer1.refresh();
		tableViewer2.setInput(direction2Mappings);
		tableViewer2.refresh();
		
		createMenus(tableViewer1);
		createMenus(tableViewer2);
		
		getSite().setSelectionProvider(tableViewer1);
		getSite().setSelectionProvider(tableViewer2);
	}

	private DragSourceListener getDragSourceListener(TableViewer tableViewer) {
		DragSourceListener dragSourceListener1 = new ActiveViewSelectionDragAdapter(tableViewer) {
			@Override
			protected boolean isDragable(ISelection selection) {
				if (selection == null || selection.isEmpty()) return false;
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				return structuredSelection.size() == 1;
			}			
		};
		return dragSourceListener1;
	}
	
	private void createMenus(final TableViewer tableViewer) {
		MenuManager menuMgr = new MenuManager("#ProjectMappingsPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager, tableViewer);
			}
		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableViewer);
	}
	
	private void fillContextMenu(IMenuManager manager, final TableViewer tableViewer) {	
		MenuManager sub = new MenuManager("New", IWorkbenchActionConstants.GROUP_ADD); //$NON-NLS-1$
		sub.add(new Action("Project Mapping") {	
			@Override
			public void run() {
				ProjectMappings projectMappings = new ProjectMappings(getLandscape());
				NewProjectMappingDialog dialog = new NewProjectMappingDialog(Display.getDefault().getActiveShell(), projectMappings);
				if (tableViewer == tableViewer1) dialog.setDirection(0);
				else dialog.setDirection(1);
				if (dialog.open() == NewProjectMappingDialog.OK) {
					refresh();
					if (CcfExplorerView.getView() != null) {
						CcfExplorerView.getView().refresh(projectMappings);
					}
				}
			}
		});
		sub.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(sub);		
	}
	
	private void refresh() {
		getMappings();
		tableViewer1.setInput(direction1Mappings);
		tableViewer1.refresh();
		tableViewer2.setInput(direction2Mappings);
		tableViewer2.refresh();
	}		
	
	@Override
	public void dispose() {
		Activator.removeChangeListener(this);
		super.dispose();
	}

	private void getMappings() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					SynchronizationStatus[] projectMappings = dataProvider.getSynchronizationStatuses(getLandscape(), null);
					List<SynchronizationStatus> direction1List = new ArrayList<SynchronizationStatus>();
					List<SynchronizationStatus> direction2List = new ArrayList<SynchronizationStatus>();
					for (SynchronizationStatus status : projectMappings) {
						if (status.getSourceSystemId().equals(getLandscape().getId2())) {
							direction1List.add(status);
						} else {
							direction2List.add(status);
						}
					}
					direction1Mappings = new SynchronizationStatus[direction1List.size()];
					direction1List.toArray(direction1Mappings);
					direction2Mappings = new SynchronizationStatus[direction2List.size()];
					direction2List.toArray(direction2Mappings);
				} catch (Exception e) {
					Activator.handleError(e);
				};
			}
		});
	}
	
	private void createColumns(TableViewer tableViewer, final Table table, TableLayout layout) {
		DisposeListener disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TableColumn col = (TableColumn)e.getSource();
				if (col.getWidth() > 0) settings.put("projectMappingTable." + table.getData() + "." + col.getText(), col.getWidth()); //$NON-NLS-1$
			}			
		};
		
		SelectionListener headerListener = getColumnListener(tableViewer);
		
		TableColumn col;

		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Source Repository ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Target Repository ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Conflict Resolution");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Artifact Modification Date");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Artifact Version");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
		
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText("Artifact ID");
		col.addSelectionListener(headerListener);
		setColumnWidth(table, layout, disposeListener, col, 200);
	}
	
	private void setColumnWidth(Table table, TableLayout layout,
			DisposeListener disposeListener, TableColumn col, int defaultWidth) {
		String columnWidth = settings.get("projectMappingTable." + table.getData() + "." + col.getText()); //$NON-NLS-1$
		if (columnWidth == null || columnWidth.equals("0")) layout.addColumnData(new ColumnWeightData(defaultWidth, defaultWidth, true)); //$NON-NLS-1$
		else layout.addColumnData(new ColumnPixelData(Integer.parseInt(columnWidth), true));
		col.addDisposeListener(disposeListener);
	}
	
	private SelectionListener getColumnListener(final TableViewer tableViewer) {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int column = tableViewer.getTable().indexOf((TableColumn) e.widget);
				setSortColumn(tableViewer, column);
			}
		};
	}
	
	public void setSortColumn(final TableViewer tableViewer, int column) {
		ProjectMappingsSorter oldSorter = (ProjectMappingsSorter)tableViewer.getSorter();
		if (oldSorter != null && column == oldSorter.getColumnNumber()) {
			oldSorter.setReversed(!oldSorter.isReversed());
			if (oldSorter.isReversed()) tableViewer.getTable().setSortDirection(SWT.DOWN);
			else tableViewer.getTable().setSortDirection(SWT.UP);	
			tableViewer.refresh();
		} else {
			ProjectMappingsSorter newSorter = new ProjectMappingsSorter(column);
			tableViewer.setSorter(newSorter);
			tableViewer.getTable().setSortDirection(SWT.UP);
		}
		tableViewer.getTable().setSortColumn(tableViewer.getTable().getColumn(column));
		ProjectMappingsSorter sorter = (ProjectMappingsSorter)tableViewer.getSorter();
		settings.put("projectMappings." + tableViewer.getTable().getData() + ".sortColumn", column);
		settings.put("projectMappings." + tableViewer.getTable().getData() + ".sortReversed", sorter.isReversed());
	}
	
	public void changed(ProjectMappings projectMappings) {
		if (projectMappings.getLandscape().equals(getLandscape())) {
			refresh();
		}
	}
	
	class ProjectMappingsLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				SynchronizationStatus status = (SynchronizationStatus)element;
				if (status.isPaused())
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY_PAUSED);
				else
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY);
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			SynchronizationStatus status = (SynchronizationStatus)element;
			String value = null;
			switch (columnIndex) {
			case 0:
				value = status.getSourceRepositoryId();
				break;
			case 1:
				value = status.getTargetRepositoryId();
				break;
			case 2:
				value = status.getConflictResolutionPriority();
				break;
			case 3:
				if (status.getSourceLastModificationTime() != null)
					value = status.getSourceLastModificationTime().toString();
				break;
			case 4:
				value = status.getSourceLastArtifactVersion();
				break;
			case 5:
				value = status.getSourceLastArtifactId();
				break;
			default:
				break;
			}
			if (value == null) value = "";
			return value;
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
		
	};
	
	class ProjectMappingsSorter extends ViewerSorter {
		private boolean reversed = false;
		private int columnNumber;
		
		private int[][] SORT_ORDERS_BY_COLUMN = {
				{ 0, 1, 2, 3, 4, 5 },
				{ 1, 0, 2, 3, 4, 5 },
				{ 2, 0, 1, 3, 4, 5 },
				{ 3, 0, 1, 2, 4, 5 },
				{ 4, 0, 1, 2, 3, 5 },
				{ 5, 0, 1, 2, 3, 4 }
			};
		
		public ProjectMappingsSorter(int columnNumber) {
			this.columnNumber = columnNumber;
		}
		
		public int compare(Viewer viewer, Object o1, Object o2) {
            SynchronizationStatus s1 = (SynchronizationStatus)o1;
            SynchronizationStatus s2 = (SynchronizationStatus)o2;
			int result = 0;
			if (s1 == null || s2 == null) {
				result = super.compare(viewer, o1, o2);
			} else {
				int[] columnSortOrder = SORT_ORDERS_BY_COLUMN[columnNumber];;
				for (int i = 0; i < columnSortOrder.length; ++i) {
					result = compareColumnValue(columnSortOrder[i], s1, s2);
					if (result != 0)
						break;
				}
			}
			if (reversed)
				result = -result;
			return result;
		}
		
		int compareColumnValue(int columnNumber, SynchronizationStatus s1, SynchronizationStatus s2) {
			String value1 = null;
			String value2 = null;
			switch (columnNumber) {
				case 0:
                    if (s1.getSourceRepositoryId() == null) value1 = ""; //$NON-NLS-1$
                    else value1 = s1.getSourceRepositoryId();
                    if (s2.getSourceRepositoryId() == null) value2 = ""; //$NON-NLS-1$
                    else value2 = s2.getSourceRepositoryId();
                    break;
				case 1:
                    if (s1.getTargetRepositoryId() == null) value1 = ""; //$NON-NLS-1$
                    else value1 = s1.getTargetRepositoryId();
                    if (s2.getTargetRepositoryId() == null) value2 = ""; //$NON-NLS-1$
                    else value2 = s2.getTargetRepositoryId();
                    break;
				case 2:
                    if (s1.getConflictResolutionPriority() == null) value1 = ""; //$NON-NLS-1$
                    else value1 = s1.getConflictResolutionPriority();
                    if (s2.getConflictResolutionPriority() == null) value2 = ""; //$NON-NLS-1$
                    else value2 = s2.getConflictResolutionPriority();
                    break;
				case 3: /* date */
					if (s1.getSourceLastModificationTime() == null || s2.getSourceLastModificationTime() == null) {
						if (s1.getSourceLastModificationTime() == null) value1 = "";
						else value1 = s1.getSourceLastModificationTime().toString();
						if (s2.getSourceLastModificationTime() == null) value2 = "";
						else value2 = s2.getSourceLastModificationTime().toString();
					}
					else return s1.getSourceLastModificationTime().compareTo(s2.getSourceLastModificationTime());
				case 4:
                    if (s1.getSourceLastArtifactVersion() == null) value1 = ""; //$NON-NLS-1$
                    else value1 = s1.getSourceLastArtifactVersion();
                    if (s2.getSourceLastArtifactVersion() == null) value2 = ""; //$NON-NLS-1$
                    else value2 = s2.getSourceLastArtifactVersion();
                    break;
				case 5:
                    if (s1.getSourceLastArtifactId() == null) value1 = ""; //$NON-NLS-1$
                    else value1 = s1.getSourceLastArtifactId();
                    if (s2.getSourceLastArtifactId() == null) value2 = ""; //$NON-NLS-1$
                    else value2 = s2.getSourceLastArtifactId();
                    break;        
				default:
					value1 = ""; //$NON-NLS-1$
					value2 = ""; //$NON-NLS-1$
			}
			return value1.compareTo(value2);
		}		
		
		public int getColumnNumber() {
			return columnNumber;
		}

		public boolean isReversed() {
			return reversed;
		}

		public void setReversed(boolean newReversed) {
			reversed = newReversed;
		}		
	}

}
