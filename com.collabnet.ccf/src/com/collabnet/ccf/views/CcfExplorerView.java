package com.collabnet.ccf.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.part.ViewPart;

import com.collabnet.ccf.actions.NewLandscapeAction;

public class CcfExplorerView extends ViewPart {
	private static CcfExplorerView view;
	private TreeViewer treeViewer;
	
	public static final String ID = "com.collabnet.ccf.views.CcfExplorerView";

	public CcfExplorerView() {
		super();
		view = this;
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
		
		createMenus();
		createToolbar();
		
		getSite().setSelectionProvider(treeViewer);
	}
	
	private void createMenus() {
		
	}
	
	private void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();		
		toolbarManager.add(new NewLandscapeAction());
	}

	@Override
	public void setFocus() {
	}
	
	public void refresh() {
		treeViewer.refresh();
	}
	
	public void dispose() {
		view = null;
		super.dispose();
	}
	
	public static CcfExplorerView getView() {
		return view;
	}
	
	class LandscapeLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}
		
		public String getText(Object element) {
			return super.getText(element);
		}
	}
	
	class LandscapeContentProvider extends WorkbenchContentProvider {
		public Object getParent(Object element) {
			return null;
		}
		
		public boolean hasChildren(Object element) {
			return false;
		}
		
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}
		
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}
	}

}
