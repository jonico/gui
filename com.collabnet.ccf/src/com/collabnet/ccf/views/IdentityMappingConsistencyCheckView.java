package com.collabnet.ccf.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.part.ViewPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IProjectMappingsChangeListener;
import com.collabnet.ccf.actions.ChangeSynchronizationStatusAction;
import com.collabnet.ccf.actions.IdentityMappingEditAction;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.IdentityMapping;
import com.collabnet.ccf.model.IdentityMappingConsistencyCheck;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class IdentityMappingConsistencyCheckView extends ViewPart implements IProjectMappingsChangeListener {
	private static Landscape landscape;
	
	private TreeViewer treeViewer;
	private CcfDataProvider dataProvider;
	
	private static IdentityMappingConsistencyCheckView view;
	public static final String ID = "com.collabnet.ccf.views.IdentityMappingConsistencyCheckView";
	
	public IdentityMappingConsistencyCheckView() {
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

		treeViewer.setLabelProvider(new ConsistencyLabelProvider());
		treeViewer.setContentProvider(new ConsistencyContentProvider());
		
		treeViewer.setUseHashlookup(true);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(this);
		treeViewer.setAutoExpandLevel(2);
		
		treeViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent se) {
				IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
				if (selection != null && selection.size() == 1) {
					ActionDelegate action = null;
					if (selection.getFirstElement() instanceof SynchronizationStatus) {
						action = new ChangeSynchronizationStatusAction();
					}
					else if (selection.getFirstElement() instanceof IdentityMapping) {
						action = new IdentityMappingEditAction();
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
		
		getSite().setSelectionProvider(treeViewer);
	}
	
	private void createMenus() {
		MenuManager menuMgr = new MenuManager("#ConsistencyCheckPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();		
		toolbarManager.add(new RefreshAction());
		toolbarManager.add(new Separator());
	}

	@Override
	public void setFocus() {

	}
	
	@Override
	public void dispose() {
		Activator.removeChangeListener(this);
		view = null;
		super.dispose();
	}
	
	public void refresh() {
		treeViewer.refresh();
		TreeItem[] items = treeViewer.getTree().getItems();
		for (TreeItem item : items) {
			treeViewer.setExpandedState(item.getData(), true);
		}
		treeViewer.expandAll();
	}

	public IdentityMappingConsistencyCheckView getView() {
		return view;
	}
	
	public static void setLandscape(Landscape landscape) {
		IdentityMappingConsistencyCheckView.landscape = landscape;
	}
	
	public CcfDataProvider getDataProvider() {
		if (dataProvider == null) dataProvider = new CcfDataProvider();
		return dataProvider;
	}
	
	public void refresh(Object object) {
		Object[] expandedElements = treeViewer.getExpandedElements();
		for (Object obj : expandedElements) {
			if (obj.equals(object)) {
				treeViewer.refresh(obj);
			}
		}
	}
	
	public void changed(ProjectMappings projectMappings) {
		refresh();
	}
	
	class ConsistencyLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			if (element instanceof SynchronizationStatus) {
				if (((SynchronizationStatus)element).isPaused())
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY_PAUSED);
				else
					return Activator.getImage(Activator.IMAGE_SYNC_STATUS_ENTRY);
			}
			else if (element instanceof Exception) return Activator.getImage(Activator.IMAGE_ERROR);
			else if (element instanceof IdentityMappingConsistencyCheck) {
				IdentityMappingConsistencyCheck consistencyCheck = (IdentityMappingConsistencyCheck)element;
				switch (consistencyCheck.getType()) {
				case IdentityMappingConsistencyCheck.MULTIPLE_SOURCE_TO_ONE_TARGET:
					return Activator.getImage(Activator.IMAGE_MULTIPLE_SOURCE);
				case IdentityMappingConsistencyCheck.MULTIPLE_TARGET_TO_ONE_SOURCE:
					return Activator.getImage(Activator.IMAGE_MULTIPLE_TARGET);
				case IdentityMappingConsistencyCheck.ONE_WAY:
					return Activator.getImage(Activator.IMAGE_ONE_WAY);
				default:
					return null;
				}
			}
			else if (element instanceof IdentityMapping) {
				return Activator.getImage(Activator.IMAGE_IDENTITY_MAPPING);
			}
			else return super.getImage(element);
		}
		
		public String getText(Object element) {
			if (element instanceof Exception) {
				if (((Exception)element).getMessage() == null) return super.getText(element);
				else return ((Exception)element).getMessage();
			}
			else if (element instanceof IdentityMapping) {
				IdentityMapping identityMapping = (IdentityMapping)element;
				return identityMapping.getEditableValue().toString();
			}
			else return super.getText(element);
		}
	}	
	
	class ConsistencyContentProvider implements ITreeContentProvider {
		private Object[] synchronizationStatuses;
		private Object[] inconsistentIdentityMappings;

		public Object getParent(Object element) {
			if (element instanceof SynchronizationStatus) {
				return this;
			}
			if (element instanceof IdentityMappingConsistencyCheck) {
				IdentityMappingConsistencyCheck consistencyCheck = (IdentityMappingConsistencyCheck)element;
				return consistencyCheck.getSynchronizationStatus();
			}
			return null;
		}
		
		public boolean hasChildren(Object element) {
			return !(element instanceof Exception) && !(element instanceof IdentityMapping);
		}
		
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IdentityMappingConsistencyCheckView && landscape != null) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						try {
							synchronizationStatuses = getDataProvider().getSynchronizationStatuses(landscape, null);
						} catch (Exception e) {
							synchronizationStatuses = new Object[1];
							synchronizationStatuses[0] = e;
							Activator.handleError(e);
						}
					}					
				});
				return synchronizationStatuses;
			}
			else if (parentElement instanceof SynchronizationStatus) {
				SynchronizationStatus synchronizationStatus = (SynchronizationStatus)parentElement;
				IdentityMappingConsistencyCheck multipleSourceCheck = new IdentityMappingConsistencyCheck(synchronizationStatus, IdentityMappingConsistencyCheck.MULTIPLE_SOURCE_TO_ONE_TARGET);
				IdentityMappingConsistencyCheck multipleTargetCheck = new IdentityMappingConsistencyCheck(synchronizationStatus, IdentityMappingConsistencyCheck.MULTIPLE_TARGET_TO_ONE_SOURCE);
				IdentityMappingConsistencyCheck oneWayCheck = new IdentityMappingConsistencyCheck(synchronizationStatus, IdentityMappingConsistencyCheck.ONE_WAY);
				IdentityMappingConsistencyCheck[] consistencyChecks = { multipleSourceCheck, multipleTargetCheck, oneWayCheck };
				return consistencyChecks;
			}
			else if (parentElement instanceof IdentityMappingConsistencyCheck) {
				final IdentityMappingConsistencyCheck consistencyCheck = (IdentityMappingConsistencyCheck)parentElement;
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						try {
							inconsistentIdentityMappings = getDataProvider().getIdentityMappingConsistencyCheckViolations(consistencyCheck.getLandscape(), consistencyCheck.getRepository(), consistencyCheck.getType());
						} catch (Exception e) {
							inconsistentIdentityMappings = new Object[1];
							inconsistentIdentityMappings[0] = e;
							Activator.handleError(e);
						}
					}					
				});		
				return inconsistentIdentityMappings;
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			treeViewer = (TreeViewer)viewer;
		}
	}
	
	class RefreshAction extends Action {
		public RefreshAction() {
			super();
			setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_REFRESH));
			setToolTipText("Refresh View");
		}
		public void run() {
			refresh();
		}
	}

}
