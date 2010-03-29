package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.collabnet.ccf.Activator;
import com.danube.scrumworks.api.client.types.ProductWSO;

public class ProjectMappingWizardSwpProductPage extends WizardPage {
	private ProductWSO[] products;
	private Table table;
	private TableViewer viewer;
	private ProductWSO selectedProduct;
	private boolean productsRetrieved;
	private Exception getProductsError;
	
	private String[] columnHeaders = {"Product"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardSwpProductPage() {
		super("productPage", "ScrumWorks Product", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		setMessage("Select the ScrumWorks product to be mapped.");
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		table = new Table(outerContainer, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 200;
		table.setLayoutData(gd);
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		viewer = new TableViewer(table);		
		viewer.setContentProvider(new ProductsContentProvider());
		viewer.setLabelProvider(new ProductsLabelProvider());
		for (int i = 0; i < columnHeaders.length; i++) {
			tableLayout.addColumnData(columnLayouts[i]);
			TableColumn tc = new TableColumn(table, SWT.NONE,i);
			tc.setResizable(columnLayouts[i].resizable);
			tc.setText(columnHeaders[i]);
		}
		viewer.setInput(this);	
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection productSelection = (IStructuredSelection)viewer.getSelection();
				setPageComplete(!productSelection.isEmpty());
				selectedProduct = (ProductWSO)productSelection.getFirstElement();
			}		
		});

		setControl(outerContainer);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && products == null && !productsRetrieved) {
			productsRetrieved = true;
			getProducts();
			viewer.refresh();
		}
	}
	
	public ProductWSO getSelectedProduct() {
		return selectedProduct;
	}

	private ProductWSO[] getProducts() {
		getProductsError = null;
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				String taskName = "Retrieving ScrumWorks products";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
				monitor.subTask("");
				try {
					products = ((ProjectMappingWizard)getWizard()).getProducts();
				} catch (Exception e) {
					Activator.handleError(e);
					getProductsError = e;
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
		
		if (getProductsError != null) {
			setErrorMessage("An unexpected error occurred while getting SWP products.  See error log for details.");
		}
		
		return products;
	}
	
	static class ProductsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			ProductWSO product = (ProductWSO)element;
			switch (columnIndex) { 
				case 0: return product.getName();
			}
			return "";  //$NON-NLS-1$
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
	}	
	
	class ProductsContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Object[] getElements(Object obj) {
			if (products == null) {
				return new ProductWSO[0];
			}
			return products;
		}
	}	

}
