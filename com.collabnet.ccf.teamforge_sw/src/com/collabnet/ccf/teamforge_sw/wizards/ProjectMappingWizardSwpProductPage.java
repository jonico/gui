package com.collabnet.ccf.teamforge_sw.wizards;

import java.util.Properties;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.danube.scrumworks.api.client.ScrumWorksEndpoint;
import com.danube.scrumworks.api.client.ScrumWorksEndpointBindingStub;
import com.danube.scrumworks.api.client.ScrumWorksServiceLocator;
import com.danube.scrumworks.api.client.types.ProductWSO;

public class ProjectMappingWizardSwpProductPage extends WizardPage {
	private ProductWSO[] products;
	private Table table;
	private TableViewer viewer;
	private ProductWSO selectedProduct;
	
	private String[] columnHeaders = {"Product"};
	private ColumnLayoutData columnLayouts[] = {
		new ColumnWeightData(450, 450, true)};

	public ProjectMappingWizardSwpProductPage() {
		super("productPage", "ScrumWorks Product", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		setMessage("Select the ScrumWorks product to be mapped.");
		products = getProducts();
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		if (products != null) {
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
		}

		setControl(outerContainer);
	}
	
	public ProductWSO getSelectedProduct() {
		return selectedProduct;
	}

	private ProductWSO[] getProducts() {
		if (products == null) {
			final Landscape landscape = ((ProjectMappingWizard)getWizard()).getProjectMappings().getLandscape();
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
				public void run() {
					Properties properties = null;
					if (landscape.getType1().equals("SW")) {
						properties = landscape.getProperties1();
					} else {
						properties = landscape.getProperties2();
					}
					
					String url = properties.get(Activator.PROPERTIES_SW_URL).toString();
					String user = properties.get(Activator.PROPERTIES_SW_USER).toString();
					String password = properties.get(Activator.PROPERTIES_SW_PASSWORD).toString();
					if (!url.endsWith("/")) {
						url = url + "/";
					}
					url = url + "scrumworks-api/scrumworks";
					ScrumWorksServiceLocator locator = new ScrumWorksServiceLocator();
					locator.setScrumWorksEndpointPortEndpointAddress(url);
					try {
						ScrumWorksEndpoint endpoint = locator.getScrumWorksEndpointPort();
						((ScrumWorksEndpointBindingStub) endpoint).setUsername(user);
						((ScrumWorksEndpointBindingStub) endpoint).setPassword(password);
						products = endpoint.getProducts();
					} catch (Exception e) {
						Activator.handleError(e);
						setErrorMessage(e.getMessage());
					}
				}			
			});	
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
			return products;
		}
	}	

}
