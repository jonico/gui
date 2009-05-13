package com.collabnet.ccf.views;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
		
		IMenuManager barMenuManager = getViewSite().getActionBars().getMenuManager();
		NewLandscapeAction newLandscapeAction = new NewLandscapeAction("New CCF Landscape...");
		barMenuManager.add(newLandscapeAction);
//		barMenuManager.setRemoveAllWhenShown(true);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		MenuManager sub = new MenuManager("New", IWorkbenchActionConstants.GROUP_ADD); //$NON-NLS-1$
		NewLandscapeAction newLandscapeAction = new NewLandscapeAction("CCF Landscape");
		sub.add(newLandscapeAction);
		sub.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(sub);		
		
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		if (selection.size() == 1 && selection.getFirstElement() instanceof Landscape) {
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
				if (((SynchronizationStatus)element).isPaused())
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY_PAUSED);
				else
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY);
			}
			else return super.getImage(element);
		}
		
		public String getText(Object element) {
			if (element instanceof Landscape) return ((Landscape) element).getDescription();
			else return super.getText(element);
		}
	}
	
	class LandscapeContentProvider extends WorkbenchContentProvider {
		private SynchronizationStatus[] synchronizationStatuses;
		
		public Object getParent(Object element) {
			return null;
		}
		
		public boolean hasChildren(Object element) {
			if (element instanceof SynchronizationStatus || element instanceof Log) return false;
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
							synchronizationStatuses = new SynchronizationStatus[0];
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

}
