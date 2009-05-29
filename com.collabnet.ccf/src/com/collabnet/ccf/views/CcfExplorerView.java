package com.collabnet.ccf.views;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.part.ViewPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.ILandscapeContributor;
import com.collabnet.ccf.IProjectMappingsChangeListener;
import com.collabnet.ccf.actions.ChangeSynchronizationStatusAction;
import com.collabnet.ccf.actions.EditLandscapeAction;
import com.collabnet.ccf.actions.EditLogAction;
import com.collabnet.ccf.actions.JmxBrowserAction;
import com.collabnet.ccf.actions.NewLandscapeAction;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.Log;
import com.collabnet.ccf.model.Logs;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class CcfExplorerView extends ViewPart implements IProjectMappingsChangeListener {
	private static CcfExplorerView view;
	private TreeViewer treeViewer;
	private CcfDataProvider dataProvider;
	private CcfComparator ccfComparator = new CcfComparator();
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	
	public static final String PROJECT_MAPPING_SORT_ORDER = "CcfExplorerView.projectMappingSort";
	public static final int SORT_BY_SOURCE_REPOSITORY = 0;
	public static final int SORT_BY_TARGET_REPOSITORY = 1;	
	public static final int SORT_BY_QC_REPOSITORY = 2;
	
	public static final String ID = "com.collabnet.ccf.views.CcfExplorerView";

	public CcfExplorerView() {
		super();
		view = this;
		Activator.addChangeListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		treeViewer = new TreeViewer(parent);

		treeViewer.setLabelProvider(new LandscapeLabelProvider());
		treeViewer.setContentProvider(new LandscapeContentProvider());
		
		int sortOrder = 0;
		try {
			sortOrder = settings.getInt(PROJECT_MAPPING_SORT_ORDER);
		} catch (Exception e) {}
		ccfComparator.setSortOrder(sortOrder);	
		treeViewer.setComparator(ccfComparator);
		
		treeViewer.setUseHashlookup(true);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(this);
		
		treeViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent se) {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if (selection != null && selection.size() == 1) {
					ActionDelegate action = null;
					if (selection.getFirstElement() instanceof Landscape) {
						action = new EditLandscapeAction();
					}
					else if (selection.getFirstElement() instanceof Log) {
						action = new EditLogAction();
					}
					else if (selection.getFirstElement() instanceof SynchronizationStatus) {
						action = new ChangeSynchronizationStatusAction();
					}
					else if (selection.getFirstElement() instanceof Exception) {
						Exception exception = (Exception)selection.getFirstElement();
						StringBuffer errorMessage = new StringBuffer("An unexpected error occurred.  Review error log for more details.");
						if (exception.getLocalizedMessage() != null) {
							errorMessage.append("\n\n" + exception.getLocalizedMessage());
						}
						if (exception.getCause() != null && exception.getCause().getLocalizedMessage() != null) {
							errorMessage.append("\n\nCause:\n\n" + exception.getCause().getLocalizedMessage());
						}
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Exception", errorMessage.toString());					
					}
					if (action != null) {
						action.selectionChanged(null, selection);
						action.run(null);						
					}
				}
			}			
		});
		
		createMenus();
		createToolbar();
		
		Transfer[] dragTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		
		DragSourceListener dragSourceListener = new ActiveViewSelectionDragAdapter(treeViewer) {
			@Override
			protected boolean isDragable(ISelection selection) {
				if (selection == null || selection.isEmpty()) return false;
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				if (structuredSelection.size() > 1) return false;
				Object selectedObject = structuredSelection.getFirstElement();
				return (selectedObject instanceof Landscape || selectedObject instanceof SynchronizationStatus);
			}			
		};
		treeViewer.addDragSupport(DND.DROP_COPY | DND.DROP_DEFAULT, dragTypes, dragSourceListener);
		
		getSite().setSelectionProvider(treeViewer);
	}
	
	private void createMenus() {
		MenuManager menuMgr = new MenuManager("#CcfExplorerPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
		
		final IMenuManager barMenuManager = getViewSite().getActionBars().getMenuManager();
		barMenuManager.setRemoveAllWhenShown(true);		
		barMenuManager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager menu) {
        		NewLandscapeAction newLandscapeAction = new NewLandscapeAction("New CCF Landscape...");
        		barMenuManager.add(newLandscapeAction);  
        		barMenuManager.add(new Separator());
        		MenuManager sortMenu = new MenuManager("Sort project mappings by");
        		SortAction bySourceAction = new SortAction("Source repository", SORT_BY_SOURCE_REPOSITORY);
        		SortAction byTargetAction = new SortAction("Target repository", SORT_BY_TARGET_REPOSITORY);
        		SortAction byQcAction = new SortAction("Quality Center repository", SORT_BY_QC_REPOSITORY);
        		sortMenu.add(bySourceAction);
        		sortMenu.add(byTargetAction);
        		sortMenu.add(byQcAction);
        		switch (ccfComparator.getSortOrder()) {
				case SORT_BY_SOURCE_REPOSITORY:
					bySourceAction.setChecked(true);
					break;
				case SORT_BY_TARGET_REPOSITORY:
					byTargetAction.setChecked(true);
					break;
				case SORT_BY_QC_REPOSITORY:
					byQcAction.setChecked(true);
					break;					
				default:
					bySourceAction.setChecked(true);
					break;
				}
        		barMenuManager.add(sortMenu);
            }
		});
   		NewLandscapeAction newLandscapeAction = new NewLandscapeAction("New CCF Landscape...");
		barMenuManager.add(newLandscapeAction);  	
	}
	
	private void fillContextMenu(IMenuManager manager) {
		MenuManager sub = new MenuManager("New", IWorkbenchActionConstants.GROUP_ADD); //$NON-NLS-1$
		NewLandscapeAction newLandscapeAction = new NewLandscapeAction("CCF Landscape");
		sub.add(newLandscapeAction);
		sub.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(sub);	
		
		manager.add(new Separator());
		
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if (selection.size() == 1 && selection.getFirstElement() instanceof Landscape) {
			
			MenuManager jmxMenu = new MenuManager("Open JMX console in browser", IWorkbenchActionConstants.GROUP_ADD); //$NON-NLS-1$
			JmxBrowserAction jmxToQcAction = new JmxBrowserAction((Landscape)selection.getFirstElement(), JmxBrowserAction.TO_QC);
			jmxMenu.add(jmxToQcAction);
			JmxBrowserAction jmxFromQcAction = new JmxBrowserAction((Landscape)selection.getFirstElement(), JmxBrowserAction.FROM_QC);
			jmxMenu.add(jmxFromQcAction);			
			manager.add(jmxMenu);
			
			ILandscapeContributor landscapeContributor = null;
			try {
				landscapeContributor = Activator.getLandscapeContributor((Landscape)selection.getFirstElement());
			} catch (Exception e) {
				Activator.handleError(e);
			}
			if (landscapeContributor != null) {
				Action[] editPropertiesActions = landscapeContributor.getEditPropertiesActions((Landscape)selection.getFirstElement());
				if (editPropertiesActions != null) {
					for (int i = 0; i < editPropertiesActions.length; i++) {
						manager.add(editPropertiesActions[i]);
					}
					manager.add(new Separator());
				}
			}
		}
	}
	
	private void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();		
		toolbarManager.add(new RefreshAction());
		toolbarManager.add(new Separator());
		toolbarManager.add(new NewLandscapeAction("New CCF Landscape..."));
	}

	@Override
	public void setFocus() {
	}
	
	public void refresh() {
		treeViewer.refresh();
	}
	
	public void refresh(Object object) {
		Object[] expandedElements = treeViewer.getExpandedElements();
		for (Object obj : expandedElements) {
			if (obj.equals(object)) {
				treeViewer.refresh(obj);
			}
		}
	}
	
	public void refreshProjectMappings() {
		Object[] expandedElements = treeViewer.getExpandedElements();
		for (Object obj : expandedElements) {
			if (obj instanceof ProjectMappings) {
				treeViewer.refresh(obj);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void refreshViewerNode() {
    	IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
        	refresh(iter.next());
        }
	}
	
	public void dispose() {
		Activator.removeChangeListener(this);
		view = null;
		super.dispose();
	}
	
	public CcfDataProvider getDataProvider() {
		if (dataProvider == null) dataProvider = new CcfDataProvider();
		return dataProvider;
	}
	
	public static CcfExplorerView getView() {
		return view;
	}
	
	class LandscapeLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			if (element instanceof Landscape) return Activator.getImage((Landscape)element);
			else if (element instanceof ProjectMappings) return Activator.getImage(Activator.IMAGE_PROJECT_MAPPINGS);
			else if (element instanceof Logs) return Activator.getImage(Activator.IMAGE_LOGS);
			else if (element instanceof Log) return Activator.getImage(Activator.IMAGE_LOG);
			else if (element instanceof SynchronizationStatus) {
				if (((SynchronizationStatus)element).getHospitalEntries() > 0)
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY_WITH_HOSPITAL_ENTRIES);
				else if (((SynchronizationStatus)element).isPaused())
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY_PAUSED);
				else
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY);
			}
			else if (element instanceof Exception) return Activator.getImage(Activator.IMAGE_ERROR);
			else return super.getImage(element);
		}
		
		public String getText(Object element) {
			if (element instanceof Landscape) return ((Landscape) element).getDescription();
			else if (element instanceof Exception) {
				if (((Exception)element).getMessage() == null) return super.getText(element);
				else return ((Exception)element).getMessage();
			}
			else return super.getText(element);
		}
	}
	
	class LandscapeContentProvider extends WorkbenchContentProvider {
		private Object[] synchronizationStatuses;
		
		public Object getParent(Object element) {
			return null;
		}
		
		public boolean hasChildren(Object element) {
			if (element instanceof SynchronizationStatus || element instanceof Log || element instanceof Exception) return false;
			return true;
		}
		
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof CcfExplorerView) {
				return Activator.getDefault().getLandscapes();
			}
			else if (parentElement instanceof Landscape) {
				ProjectMappings projectMappings = new ProjectMappings((Landscape)parentElement);
				Logs logs = new Logs((Landscape)parentElement);
				Object[] children = { projectMappings, logs };
				return children;
			}
			else if (parentElement instanceof ProjectMappings) {
				final ProjectMappings projectMappings = (ProjectMappings)parentElement;
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						try {
							synchronizationStatuses = getDataProvider().getSynchronizationStatuses(projectMappings.getLandscape(), projectMappings);
						} catch (Exception e) {
//							synchronizationStatuses = new SynchronizationStatus[0];
							synchronizationStatuses = new Object[1];
							synchronizationStatuses[0] = e;
							Activator.handleError(e);
						}
					}					
				});
				return synchronizationStatuses;
			}
			else if (parentElement instanceof Logs) {
				Logs logs = (Logs)parentElement;
				switch (logs.getType()) {
				case Logs.TYPE_FOLDER:
					Logs logs1 = new Logs(logs.getLandscape(), Logs.TYPE_1_2);
					Logs logs2 = new Logs(logs.getLandscape(), Logs.TYPE_2_1);
					Logs[] logsArray = { logs1, logs2 };
					return logsArray;
				default:
					return logs.getLandscape().getLogs(logs);
				}
			}
			return new Object[0];
		}
	}
	
	class RefreshAction extends Action {
		public RefreshAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_REFRESH));
			setToolTipText("Refresh View");
		}
		public void run() {
			treeViewer.refresh();
		}
	}

	public void changed(ProjectMappings projectMappings) {
		refresh(projectMappings);
	}
	
	class CcfComparator extends ViewerComparator {
		private int sortOrder = SORT_BY_SOURCE_REPOSITORY;

		public void setSortOrder(int sortOrder) {
			this.sortOrder = sortOrder;
		}
		
		public int getSortOrder() {
			return sortOrder;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof SynchronizationStatus && e2 instanceof SynchronizationStatus) {
				SynchronizationStatus s1 = (SynchronizationStatus)e1;
				SynchronizationStatus s2 = (SynchronizationStatus)e2;
				String cmp1;
				String cmp2;
				
				switch (sortOrder) {
				case SORT_BY_SOURCE_REPOSITORY:
					cmp1 = s1.toString();
					cmp2 = s2.toString();					
					break;
				case SORT_BY_TARGET_REPOSITORY:
					cmp1 = s1.getTargetRepositoryId() + s1.getSourceRepositoryId();
					cmp2 = s2.getTargetRepositoryId() + s2.getSourceRepositoryId();	
					break;
				case SORT_BY_QC_REPOSITORY:
					if (s1.getSourceSystemKind().startsWith("QC")) {
						cmp1 = s1.getSourceRepositoryId() + s1.getTargetRepositoryId();
					} else {
						cmp1 = s1.getTargetRepositoryId() + s1.getSourceRepositoryId();
					}
					if (s2.getSourceSystemKind().startsWith("QC")) {
						cmp2 = s2.getSourceRepositoryId() + s2.getTargetRepositoryId();
					} else {
						cmp2 = s2.getTargetRepositoryId() + s2.getSourceRepositoryId();
					}
					break;					
				default:
					cmp1 = s1.toString();
					cmp2 = s2.toString();		
					break;
				}
				return cmp1.compareTo(cmp2);				
			}
			return 0;
		}
	
	}
	
	class SortAction extends Action {
		private int order;

		public SortAction(String text, int order) {
			super(text, Action.AS_CHECK_BOX);
			this.order = order;
		}
		
		public void run() {
			settings.put(PROJECT_MAPPING_SORT_ORDER, order);
			ccfComparator.setSortOrder(order);
			refreshProjectMappings();
		}
		
	}

}
