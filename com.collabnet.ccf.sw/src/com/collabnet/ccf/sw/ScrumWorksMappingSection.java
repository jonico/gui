package com.collabnet.ccf.sw;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.sw.SWPMetaData.SWPType;
import com.collabnet.ccf.sw.dialogs.ScrumWorksSelectionDialog;
import com.danube.scrumworks.api.client.ScrumWorksEndpoint;
import com.danube.scrumworks.api.client.ScrumWorksEndpointBindingStub;
import com.danube.scrumworks.api.client.ScrumWorksServiceLocator;
import com.danube.scrumworks.api.client.types.ProductWSO;

public class ScrumWorksMappingSection extends MappingSection {
	private Text productText;
	private Combo typeCombo;
	
	private ProductWSO[] products;
	private Exception soapException;
	
	private IDialogSettings settings = com.collabnet.ccf.sw.Activator.getDefault().getDialogSettings();

	private static final String PREVIOUS_TYPE = "ScrumWorksMappingSection.type";
	
	public Composite getComposite(Composite parent, final Landscape landscape) {
		Group swGroup = new Group(parent, SWT.NULL);
		GridLayout swLayout = new GridLayout();
		swLayout.numColumns = 3;
		swGroup.setLayout(swLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		swGroup.setLayoutData(gd);	
		swGroup.setText("ScrumWorks:");
		
		Label productLabel = new Label(swGroup, SWT.NONE);
		productLabel.setText("Product:");
		
		productText = new Text(swGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		productText.setLayoutData(gd);
		
		Button productBrowseButton = new Button(swGroup, SWT.PUSH);
		productBrowseButton.setText("Browse...");
		productBrowseButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				selectProduct(landscape);
			}
		});
		
		Label typeLabel = new Label(swGroup, SWT.NONE);
		typeLabel.setText("Mapping type:");
		typeCombo = new Combo(swGroup, SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		typeCombo.setLayoutData(gd);
		typeCombo.add(SWPMetaData.PRODUCT.toString());
		typeCombo.add(SWPMetaData.TASK.toString());
		typeCombo.add(SWPMetaData.PBI.toString());	
		typeCombo.add(SWPMetaData.PRODUCT_RELEASE.toString());
		String previousType = null;
		try {
			previousType = settings.get(PREVIOUS_TYPE);
		} catch (Exception e) {}
		if (previousType == null) {
			previousType = SWPMetaData.TASK.toString();
		}
		typeCombo.setText(previousType);		
		typeCombo.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
					settings.put(PREVIOUS_TYPE, typeCombo.getText());
				}
			}
		});
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		productText.addModifyListener(modifyListener);
		
		return swGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		String product;
		SWPType swpType;
		if (type == IMappingSection.TYPE_SOURCE) {
			product = SWPMetaData.retrieveProductFromRepositoryId(projectMapping.getSourceRepositoryId());
			swpType = SWPMetaData.retrieveSWPTypeFromRepositoryId(projectMapping.getSourceRepositoryId());
		} else {
			product = SWPMetaData.retrieveProductFromRepositoryId(projectMapping.getTargetRepositoryId());
			swpType = SWPMetaData.retrieveSWPTypeFromRepositoryId(projectMapping.getTargetRepositoryId());
		}
		productText.setText(product);
		if (swpType == SWPType.PRODUCT_RELEASE) {
			typeCombo.setText(SWPMetaData.PRODUCT_RELEASE.toString());
		}
		else if (swpType == SWPType.PRODUCT) {
			typeCombo.setText(SWPMetaData.PRODUCT.toString());
		}
		else if (swpType == SWPType.PBI) {
			typeCombo.setText(SWPMetaData.PBI.toString());
		} else {
			typeCombo.setText(SWPMetaData.TASK.toString());
		}
	}

	public boolean isPageComplete() {
		if (productText == null || productText.getText().trim().length() == 0) {
			return false;
		}
		return true;
	}

	public void updateSourceFields(SynchronizationStatus projectMapping) {
		projectMapping.setSourceRepositoryId(getRepositoryId());
		if (projectMapping.getSourceRepositoryId().endsWith("ProductRelease")) {
			projectMapping.setSourceRepositoryKind("TemplateProductReleases.xsl");
		} else if (projectMapping.getSourceRepositoryId().endsWith("PBI")) {
			projectMapping.setSourceRepositoryKind("TemplatePBIs.xsl");
		} else if (projectMapping.getSourceRepositoryId().endsWith("Task")) {
			projectMapping.setSourceRepositoryKind("TemplateTasks.xsl");
		} else if (projectMapping.getSourceRepositoryId().endsWith("Product")) {
			projectMapping.setSourceRepositoryKind("TemplateProducts.xsl");
		} else {
			projectMapping.setSourceRepositoryKind("TRACKER");
		}
	}
	
	public void updateTargetFields(SynchronizationStatus projectMapping) {
		projectMapping.setTargetRepositoryId(getRepositoryId());
		projectMapping.setTargetRepositoryKind("TRACKER");
	}

	public boolean validate(Landscape landscape) {
		return true;
	}
	
	private String getRepositoryId() {
		return productText.getText().trim() + SWPMetaData.REPOSITORY_ID_SEPARATOR + typeCombo.getText();
	}
	
	private void selectProduct(Landscape landscape) {
		products = getProducts(landscape);
		if (products != null) {
			ScrumWorksSelectionDialog dialog = new ScrumWorksSelectionDialog(Display.getDefault().getActiveShell(), ScrumWorksSelectionDialog.BROWSER_TYPE_PRODUCT);
			dialog.setProducts(products);
			if (dialog.open() == ScrumWorksSelectionDialog.OK) {
				productText.setText(dialog.getSelection());
			}
		}
	}
	
	private ProductWSO[] getProducts(final Landscape landscape) {
		products = null;
		soapException = null;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				Properties properties = null;
				if (landscape.getType1().equals(ScrumWorksCcfParticipant.TYPE)) {
					properties = landscape.getProperties1();
				} else {
					properties = landscape.getProperties2();
				}
				
				String url = properties.get(Activator.PROPERTIES_SW_URL).toString();
				String user = properties.get(Activator.PROPERTIES_SW_USER).toString();
				String password = properties.get(Activator.PROPERTIES_SW_PASSWORD).toString();
				if (!url.endsWith("scrumworks-api/scrumworks")) {
					if (!url.endsWith("/")) {
						url = url + "/";
					}
					url = url + "scrumworks-api/scrumworks";
				}
				ScrumWorksServiceLocator locator = new ScrumWorksServiceLocator();
				locator.setScrumWorksEndpointPortEndpointAddress(url);
				try {
					ScrumWorksEndpoint endpoint = locator.getScrumWorksEndpointPort();
					((ScrumWorksEndpointBindingStub) endpoint).setUsername(user);
					((ScrumWorksEndpointBindingStub) endpoint).setPassword(password);
					products = endpoint.getProducts();
				} catch (Exception e) {
					soapException = e;
				}
			}			
		});	
		if (soapException != null) {
			ExceptionDetailsErrorDialog.openError(Display.getDefault().getActiveShell(), "Select Product", soapException.getMessage(), new Status(IStatus.ERROR, com.collabnet.ccf.sw.Activator.PLUGIN_ID, soapException.getLocalizedMessage(), soapException));
		}
		return products;
	}

}
